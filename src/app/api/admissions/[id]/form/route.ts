import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { NextResponse } from "next/server";
import { PDFDocument, StandardFonts, rgb, type PDFFont, type PDFPage } from "pdf-lib";
import { cookies } from "next/headers";
import { db } from "@/lib/db";
import { assertCanInScope } from "@/lib/permissions";
import { readSession } from "@/lib/session";
import { admissionDocumentCategoryLabels, admissionDraftDataSchema } from "@/lib/admissions";
import { readStoredFile } from "@/lib/file-storage";

const blue = rgb(0.114, 0.306, 0.847);
const navy = rgb(0.055, 0.114, 0.224);
const muted = rgb(0.31, 0.37, 0.45);
function wrap(value: string, font: PDFFont, size: number, width: number) {
  const lines: string[] = [];
  for (const paragraph of value.split(/\r?\n/)) {
    let line = "";
    for (const word of paragraph.split(/\s+/).filter(Boolean)) {
      const candidate = line ? `${line} ${word}` : word;
      if (line && font.widthOfTextAtSize(candidate, size) > width) { lines.push(line); line = word; } else line = candidate;
    }
    lines.push(line);
  }
  return lines.length ? lines : [""];
}

export async function GET(request: Request, { params }: { params: Promise<{ id: string }> }) {
  const token = (await cookies()).get("mary_session")?.value;
  const session = token ? await readSession(token) : null;
  if (!session) return NextResponse.json({ error: "Unauthenticated" }, { status: 401 });
  const user = await db.user.findUnique({ where: { id: session.id }, select: { id: true, role: true, suspended: true, forcePasswordChange: true } });
  if (!user || user.suspended || user.forcePasswordChange) return NextResponse.json({ error: "Unauthenticated" }, { status: 401 });
  try { assertCanInScope(user.role, "admissions", "view", "school"); } catch { await db.auditEvent.create({ data: { actorId: user.id, action: "ACCESS_DENIED", entity: "AdmissionDraft" } }); return NextResponse.json({ error: "Forbidden" }, { status: 403 }); }
  const { id } = await params;
  const draft = await db.admissionDraft.findUnique({ where: { id }, include: { documents: { select: { category: true, storageKey: true, contentType: true } }, learner: { select: { id: true, admissionApprovedAt: true } } } });
  if (!draft) return NextResponse.json({ error: "Admission form not found." }, { status: 404 });
  let raw: unknown;
  try { raw = JSON.parse(draft.payload) as unknown; } catch { raw = {}; }
  const parsed = admissionDraftDataSchema.safeParse(raw);
  const data = parsed.success ? parsed.data : {};
  const pdf = await PDFDocument.create();
  pdf.setTitle(`Admission form ${draft.admissionNo} · Mary Royal School`);
  pdf.setAuthor("Mary Royal School · Obunga, Kisumu");
  const font = await pdf.embedFont(StandardFonts.Helvetica);
  const bold = await pdf.embedFont(StandardFonts.HelveticaBold);
  const pageSize: [number, number] = [595.28, 841.89];
  let page!: PDFPage;
  let y = 0;
  const newPage = () => { page = pdf.addPage(pageSize); y = 792; };
  const ensure = (height: number) => { if (y - height < 62) newPage(); };
  const line = (text: string, size = 9, color = navy, strong = false) => {
    const selected = strong ? bold : font;
    const lines = wrap(text || "Not provided", selected, size, 500);
    ensure(lines.length * (size + 4) + 5);
    for (const item of lines) { page.drawText(item, { x: 48, y, size, font: selected, color }); y -= size + 4; }
  };
  const section = (title: string) => {
    ensure(34); page.drawRectangle({ x: 42, y: y - 6, width: 511, height: 22, color: rgb(0.91, 0.95, 1) });
    page.drawText(title.toUpperCase(), { x: 50, y: y, size: 9, font: bold, color: blue }); y -= 29;
  };
  newPage();
  try {
    const logo = await readFile(join(process.cwd(), "public", "mary-royal-logo.png"));
    const image = await pdf.embedPng(logo);
    page.drawImage(image, { x: 46, y: 744, width: 58, height: 58 });
  } catch { /* The text masthead remains printable if the optional logo file is unavailable. */ }
  page.drawText("MARY ROYAL SCHOOL", { x: 115, y: 777, size: 18, font: bold, color: navy });
  page.drawText("Obunga, Kisumu · Hard work pays", { x: 115, y: 758, size: 10, font, color: muted });
  page.drawText("LEARNER ADMISSION FORM", { x: 115, y: 739, size: 10, font: bold, color: blue });
  page.drawText(`Admission No. ${draft.admissionNo}`, { x: 390, y: 777, size: 10, font: bold, color: navy });
  page.drawText(`Status: ${draft.status}`, { x: 390, y: 760, size: 9, font, color: muted });
  page.drawLine({ start: { x: 42, y: 730 }, end: { x: 553, y: 730 }, thickness: 1.2, color: blue }); y = 710;
  const learner = data.learner;
  section("Learner details");
  for (const [key, value] of Object.entries({ "Full name": learner?.fullName, "Date of birth": learner?.dateOfBirth, Gender: learner?.gender, Nationality: learner?.nationality, "UPI / NEMIS number": learner?.upiNumber, "Class and stream": [learner?.grade, learner?.stream].filter(Boolean).join(" · "), "Admission date": learner?.admissionDate, "Previous school / class": [learner?.previousSchool, learner?.previousClass].filter(Boolean).join(" · "), "Reason for leaving": learner?.reasonForLeaving, Religion: learner?.religion, "Languages spoken": learner?.languagesSpoken?.join(", ") })) line(`${key}: ${value || "Not provided"}`);
  section("Residence and transport");
  const residence = data.residence;
  for (const [key, value] of Object.entries({ County: residence?.county, "Sub-county": residence?.subCounty, Ward: residence?.ward, Village: residence?.village, "Physical address": residence?.physicalAddress, Landmark: residence?.landmark, "Distance to school": residence?.distanceToSchoolKm == null ? "" : `${residence.distanceToSchoolKm} km`, Transport: residence?.transportMeans, "Map coordinates": residence?.latitude == null || residence.longitude == null ? "" : `${residence.latitude}, ${residence.longitude}` })) line(`${key}: ${value || "Not provided"}`);
  section("Parents and guardians");
  for (const [index, guardian] of (data.guardians ?? []).entries()) {
    line(`${index + 1}. ${guardian.fullName || "Guardian"} · ${guardian.relationship || "Relationship not provided"}${guardian.primaryContact ? " · Primary contact" : ""}${guardian.paysFees ? " · Fee payer" : ""}`, 9, navy, true);
    line(`Phone: ${guardian.phone || "Not provided"} · WhatsApp: ${guardian.whatsapp ? "Yes" : "No"} · Email: ${guardian.email || "Not provided"}`);
    line(`ID/passport: ${guardian.idNumber || "Not provided"} · Occupation: ${guardian.occupation || "Not provided"} · Employer: ${guardian.employer || "Not provided"}`);
    if (guardian.homeAddress) line(`Home address: ${guardian.homeAddress}`);
    if (guardian.workAddress) line(`Work address: ${guardian.workAddress}`);
  }
  section("Emergency contacts and authorized collection");
  for (const [index, contact] of (data.emergencyContacts ?? []).entries()) line(`Emergency ${index + 1}: ${contact.fullName || "Not provided"} · ${contact.relationship || ""} · ${contact.phone || ""}`);
  for (const [index, person] of (data.pickupPersons ?? []).entries()) line(`Pick-up ${index + 1}: ${person.fullName || "Not provided"} · ${person.relationship || ""} · ${person.phone || ""}`);
  section("Health and wellbeing · restricted school record");
  const health = data.health;
  for (const [key, value] of Object.entries({ "Known conditions": health?.knownConditions, Allergies: health?.allergies, Medication: health?.currentMedication, "Special needs": health?.specialNeeds, "Blood group": health?.bloodGroup, "Doctor / hospital": health?.doctorOrHospital, Immunization: health?.immunizationStatus, Insurance: health?.healthInsurance })) line(`${key}: ${value || "Not provided"}`);
  section("Fee hand-off and consent");
  line(`Term: ${data.fee?.term || "Not provided"}`);
  line(`Person responsible for fees: ${data.fee?.feePayerGuardian || "Not provided"}`);
  line(`Optional items requested: ${(data.fee?.requestedOptionalItems ?? []).join(", ") || "None"}`);
  line(`Handoff note: ${data.fee?.handoffNote || "Accountant to confirm charges from the approved school fee structure."}`);
  line(`Data processing consent recorded: ${data.consent ? "Yes" : "No"} · Kenya Data Protection Act 2019`, 9, navy, true);
  section("Document checklist");
  for (const [category, label] of Object.entries(admissionDocumentCategoryLabels)) {
    const uploaded = draft.documents.some(document => document.category === category);
    line(`${uploaded ? "[X]" : "[ ]"} ${label}${category === "PASSPORT_PHOTO" || category === "BIRTH_CERTIFICATE" ? " · Required" : ""}`, 8, uploaded ? rgb(0.08, 0.42, 0.24) : muted);
  }
  const photo = draft.documents.find(document => document.category === "PASSPORT_PHOTO");
  if (photo && photo.contentType !== "application/pdf" && /^[a-f0-9-]{36}\.(jpg|png)$/.test(photo.storageKey)) {
    try {
      const bytes = await readStoredFile("private", `admissions/${id}/${photo.storageKey}`);
      const image = photo.contentType === "image/png" ? await pdf.embedPng(bytes) : await pdf.embedJpg(bytes);
      ensure(118);
      const photoY = y - 102;
      page.drawImage(image, { x: 445, y: photoY, width: 78, height: 92, opacity: 0.98 });
      page.drawRectangle({ x: 445, y: photoY, width: 78, height: 92, borderColor: rgb(0.75, 0.81, 0.9), borderWidth: 1 });
      y -= 112;
    } catch { /* Keep the checklist and form usable if an image is corrupt. */ }
  }
  ensure(70);
  const signatureY = Math.max(45, y - 42);
  for (const [x, label] of [[48, "Parent / guardian signature"], [235, "Secretary"], [420, "Head Teacher"]] as const) {
    page.drawLine({ start: { x, y: signatureY }, end: { x: x + 130, y: signatureY }, thickness: 0.7, color: muted });
    page.drawText(label, { x, y: signatureY - 13, size: 7, font, color: muted });
  }
  page.drawText(`Date: ${new Intl.DateTimeFormat("en-KE", { dateStyle: "medium", timeZone: "Africa/Nairobi" }).format(new Date())}`, { x: 48, y: signatureY - 31, size: 8, font, color: muted });
  for (const item of pdf.getPages()) item.drawText("Mary Royal School · Confidential learner information · Obunga, Kisumu", { x: 42, y: 24, size: 7, font, color: muted });
  await db.auditEvent.create({ data: { actorId: user.id, action: "ADMISSION_FORM_EXPORTED", entity: "AdmissionDraft", entityId: id } });
  const bytes = await pdf.save();
  const body = new ArrayBuffer(bytes.byteLength);
  new Uint8Array(body).set(bytes);
  const inline = new URL(request.url).searchParams.get("download") === "0";
  return new NextResponse(body, { headers: { "content-type": "application/pdf", "content-disposition": `${inline ? "inline" : "attachment"}; filename="Mary-Royal-Admission-${draft.admissionNo}.pdf"`, "cache-control": "private, no-store", "x-content-type-options": "nosniff" } });
}
