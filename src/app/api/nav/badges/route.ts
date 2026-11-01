import { NextResponse } from "next/server";
import { cookies } from "next/headers";
import { db } from "@/lib/db";
import { readSession } from "@/lib/session";

async function actor() {
  const token = (await cookies()).get("mary_session")?.value;
  const session = token ? await readSession(token) : null;
  if (!session) return null;
  const user = await db.user.findUnique({
    where: { id: session.id },
    select: { id: true, role: true, suspended: true, forcePasswordChange: true },
  });
  return user && !user.suspended && !user.forcePasswordChange ? user : null;
}

export async function GET() {
  const user = await actor();
  if (!user) return NextResponse.json({ error: "Unauthenticated" }, { status: 401 });

  let unreadNotifications = 0;
  let pendingComplaints = 0;
  let pendingApprovals = 0;

  try {
    const unread = await db.notification.count({
      where: { userId: user.id, read: false },
    });
    unreadNotifications = unread;

    if (user.role === "DIRECTOR" || user.role === "HEAD_TEACHER") {
      const [complaintsCount, waiversCount, timetablesCount] = await Promise.all([
        db.complaint.count({ where: { status: "OPEN" } }),
        db.feeWaiver.count({ where: { status: "PENDING" } }),
        db.schoolTimetable.count({ where: { status: "PENDING_APPROVAL" } }),
      ]);
      pendingComplaints = complaintsCount;
      pendingApprovals = waiversCount + timetablesCount;
    } else if (user.role === "PARENT") {
      const parentComplaints = await db.complaint.count({
        where: {
          openedById: user.id,
          replies: {
            some: {
              authorId: { not: user.id },
            },
          },
        },
      });
      pendingComplaints = parentComplaints;
    }
  } catch (err) {
    console.error("Failed to load nav badges:", err);
  }

  return NextResponse.json({
    unreadNotifications,
    pendingComplaints,
    pendingApprovals,
    messages: pendingComplaints + (user.role === "PARENT" ? unreadNotifications : 0),
  });
}
