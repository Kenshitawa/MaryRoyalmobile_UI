import Link from "next/link";
import { cookies } from "next/headers";
import { notFound, redirect } from "next/navigation";
import PortalFrame from "@/components/portal-frame";
import { db } from "@/lib/db";
import { readSession } from "@/lib/session";
import { canInScope, canManageStaffTarget, canViewStaffStatutory, canEditStaffStatutory } from "@/lib/permissions";
import { ArrowLeft } from "lucide-react";
import { StaffProfileClient, type StaffProfileClientData } from "./staff-profile-client";

export default async function StaffProfilePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const token = (await cookies()).get("mary_session")?.value;
  const session = token ? await readSession(token) : null;
  if (!session) redirect("/");

  const actor = await db.user.findUnique({
    where: { id: session.id },
    select: { id: true, role: true, name: true, suspended: true, forcePasswordChange: true },
  });
  if (!actor || actor.suspended) redirect("/");
  if (actor.forcePasswordChange) redirect("/change-password");

  // Teachers and other non-leadership staff see only their own profile
  if (actor.id !== id && !canInScope(actor.role, "staff", "view", "school")) {
    await db.auditEvent.create({
      data: { actorId: actor.id, action: "ACCESS_DENIED", entity: "StaffProfile", entityId: id },
    });
    redirect("/dashboard");
  }

  const staff = await db.user.findUnique({
    where: { id },
      select: {
        id: true,
        name: true,
        email: true,
        phone: true,
        avatarUrl: true,
        role: true,
        suspended: true,
        staffNumber: true,
        tscNumber: true,
        employmentTitle: true,
        qualifications: true,
      staffProfile: {
        include: {
          emergencyContacts: true,
          referees: true,
          documents: {
            select: {
              id: true,
              category: true,
              originalName: true,
              contentType: true,
              sizeBytes: true,
              createdAt: true,
            },
          },
          editHistory: {
            include: {
              actor: {
                select: { name: true, role: true },
              },
            },
            orderBy: { createdAt: "desc" },
            take: 30,
          },
        },
      },
      assignments: {
        select: { grade: true, subject: true },
        orderBy: [{ grade: "asc" }, { subject: "asc" }],
      },
      classTeacherAssignments: {
        select: { grade: true },
      },
    },
  });

  if (!staff || ["PARENT", "LEARNER"].includes(staff.role)) notFound();

  const [payroll, teachingLogs, attendance] = await Promise.all([
    actor.role === "DIRECTOR"
      ? db.payrollEntry.findMany({
          where: { staffId: id },
          orderBy: { period: "desc" },
          take: 12,
          select: {
            period: true,
            grossAmount: true,
            deductions: true,
            netAmount: true,
            status: true,
            paidAt: true,
          },
        })
      : [],
    db.teachingLog.findMany({
      where: { teacherId: id },
      orderBy: { createdAt: "desc" },
      take: 10,
      select: {
        topics: true,
        reflection: true,
        grade: true,
        subject: true,
        status: true,
        createdAt: true,
      },
    }),
    db.staffAttendance.findMany({
      where: { staffId: id },
      orderBy: { date: "desc" },
      take: 60,
      select: { date: true, status: true, checkInAt: true, note: true },
    }),
  ]);

  const canSeeStatutory = canViewStaffStatutory(actor.role);
  if (canSeeStatutory) {
    await db.auditEvent.create({
      data: {
        actorId: actor.id,
        action: "STAFF_STATUTORY_VIEWED",
        entity: "StaffProfile",
        entityId: id,
      },
    });
  }

  // Strip restricted fields for unauthorized viewers
  let sanitizedStaff: StaffProfileClientData = { ...staff, staffProfile: staff.staffProfile };
  if (!canSeeStatutory && staff.staffProfile) {
    const {
      kraPin,
      nssfNumber,
      shaNumber,
      bankName,
      bankAccountName,
      bankAccountNumber,
      bankBranch,
      ...safeProfile
    } = staff.staffProfile;
    sanitizedStaff = {
      ...staff,
      staffProfile: safeProfile,
    };
  }

  const currentUser = actor.id === id;
  const canEdit = canManageStaffTarget(actor.role, staff.role);
  const canEditStat = canEditStaffStatutory(actor.role, staff.role);

  return (
    <PortalFrame active={currentUser ? "Profile" : "Staff"}>
      <main className="mx-auto max-w-5xl space-y-6 p-4 pb-28 sm:p-6 md:p-8">
        <Link
          href={currentUser ? "/profile" : "/staff"}
          className="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3.5 py-2 text-xs font-bold text-slate-700 shadow-sm transition hover:border-blue-300 hover:text-blue-700 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-200 dark:hover:border-blue-500"
        >
          <ArrowLeft size={14} />
          {currentUser ? "Back to Profile" : "Back to Staff Directory"}
        </Link>

        <StaffProfileClient
          staff={sanitizedStaff}
          actor={actor}
          canEdit={canEdit}
          canViewStatutory={canSeeStatutory}
          canEditStatutory={canEditStat}
          payroll={payroll.map((entry) => ({ ...entry, netAmount: Number(entry.netAmount) }))}
          teachingLogs={teachingLogs}
          attendance={attendance}
        />
      </main>
    </PortalFrame>
  );
}
