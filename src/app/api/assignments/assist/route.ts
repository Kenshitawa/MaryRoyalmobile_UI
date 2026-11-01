import { NextResponse } from "next/server";
import { cookies } from "next/headers";
import { z } from "zod";
import { db } from "@/lib/db";
import { readSession } from "@/lib/session";
import { canInScope } from "@/lib/permissions";

const inputSchema = z.object({ grade: z.string().min(2).max(20), subject: z.string().min(2).max(60), topic: z.string().trim().min(3).max(200) });
const outputSchema = z.object({ title: z.string().min(3).max(120), instructions: z.string().min(5).max(2000), answerKey: z.string().max(500).optional() });
export async function POST(request: Request) {
  const token = (await cookies()).get("mary_session")?.value;
  const session = token ? await readSession(token) : null;
  if (!session) return NextResponse.json({ error: "Unauthenticated" }, { status: 401 });
  const actor = await db.user.findUnique({ where: { id: session.id }, select: { id: true, role: true, suspended: true, forcePasswordChange: true } });
  if (!actor || actor.suspended || actor.forcePasswordChange) return NextResponse.json({ error: "Unauthenticated" }, { status: 401 });
  if (!canInScope(actor.role, "assignments", "create", "assigned")) return NextResponse.json({ error: "Forbidden" }, { status: 403 });
  let body: unknown;
  try { body = await request.json(); } catch { return NextResponse.json({ error: "Invalid request." }, { status: 400 }); }
  const parsed = inputSchema.safeParse(body);
  if (!parsed.success) return NextResponse.json({ error: "Choose a class, subject and learning topic." }, { status: 400 });
  const assigned = await db.teacherAssignment.findUnique({ where: { teacherId_grade_subject: { teacherId: actor.id, grade: parsed.data.grade, subject: parsed.data.subject } } });
  if (!assigned) return NextResponse.json({ error: "This class and subject are outside your teaching assignment." }, { status: 403 });
  try {
    const response = await fetch("http://127.0.0.1:11434/api/generate", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ model: process.env.OLLAMA_MODEL || "llama3.2", stream: false, format: "json", prompt: `Create one age-appropriate Kenyan CBC classroom assignment for ${parsed.data.grade} ${parsed.data.subject}, topic: ${parsed.data.topic}. Return only JSON with title, clear learner-facing instructions, and a concise answerKey that can be checked case-insensitively. JSON shape: {"title":"...","instructions":"...","answerKey":"..."}. Do not include personal data.` }), signal: AbortSignal.timeout(15000) });
    if (!response.ok) throw new Error("Local model unavailable");
    const payload = await response.json() as { response?: string };
    const draft = outputSchema.safeParse(JSON.parse(payload.response ?? ""));
    if (!draft.success) throw new Error("Local model response was invalid");
    return NextResponse.json({ draft: draft.data, provider: "local" });
  } catch {
    return NextResponse.json({ error: "Local AI is not available. Install Ollama and download a model, then try again. No assignment was changed." }, { status: 503 });
  }
}
