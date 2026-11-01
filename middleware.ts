import { NextResponse, type NextRequest } from "next/server";
import { makeActivitySession, readActivitySession, readSession } from "@/lib/session";
export async function middleware(request: NextRequest) {
 const path = request.nextUrl.pathname;
 if (path.startsWith("/api/login") || path.startsWith("/api/logout") || path === "/" || path.startsWith("/privacy") || path.startsWith("/_next") || path === "/favicon.ico") return NextResponse.next();
 const token = request.cookies.get("mary_session")?.value; const session = token ? await readSession(token) : null;
 if (!session) return path.startsWith("/api/") ? NextResponse.json({ error: "Unauthenticated" }, { status: 401 }) : NextResponse.redirect(new URL("/", request.url));
 const activity = request.cookies.get("mary_activity")?.value;
 if (!activity || !(await readActivitySession(activity, session.id))) {
  const response = path.startsWith("/api/") ? NextResponse.json({ error: "Session expired after 10 minutes of inactivity." }, { status: 401 }) : NextResponse.redirect(new URL("/?reason=idle", request.url));
  response.cookies.set("mary_session", "", { httpOnly: true, path: "/", maxAge: 0, sameSite: "lax" });
  response.cookies.set("mary_activity", "", { httpOnly: true, path: "/", maxAge: 0, sameSite: "lax" });
  return response;
 }
 if (session.forcePasswordChange && path !== "/change-password" && !path.startsWith("/api/change-password")) return path.startsWith("/api/") ? NextResponse.json({ error: "Password change required" }, { status: 403 }) : NextResponse.redirect(new URL("/change-password", request.url));
 const response = NextResponse.next();
 response.cookies.set("mary_activity", await makeActivitySession(session.id), { httpOnly: true, secure: process.env.NODE_ENV === "production", sameSite: "lax", path: "/", maxAge: 600 });
 return response;
}
export const config = { matcher: ["/((?!_next/static|_next/image|favicon.ico|.*\\..*).*)"] };
