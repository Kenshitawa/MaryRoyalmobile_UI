"use client";

import React from "react";

export type StatusType =
  | "PAID"
  | "PARTIAL"
  | "UNPAID"
  | "OVERDUE"
  | "PENDING"
  | "VOID"
  | "RECORDED"
  | "DRAFT"
  | "ACTIVE"
  | "SUBMITTED"
  | "APPROVED"
  | "RELEASED"
  | "RESOLVED"
  | "IN_REVIEW"
  | "PRESENT"
  | "ABSENT"
  | "LATE";

interface StatusPillProps {
  status: string;
  size?: "sm" | "md";
  className?: string;
}

const statusConfig: Record<string, { label: string; bg: string; text: string; dot: string }> = {
  PAID: { label: "Paid", bg: "bg-emerald-50 dark:bg-emerald-950/40", text: "text-emerald-700 dark:text-emerald-300", dot: "bg-emerald-500" },
  PARTIAL: { label: "Partially Paid", bg: "bg-amber-50 dark:bg-amber-950/40", text: "text-amber-700 dark:text-amber-300", dot: "bg-amber-500" },
  UNPAID: { label: "Unpaid", bg: "bg-blue-50 dark:bg-blue-950/40", text: "text-blue-700 dark:text-blue-300", dot: "bg-blue-500" },
  OVERDUE: { label: "Overdue", bg: "bg-rose-50 dark:bg-rose-950/40", text: "text-rose-700 dark:text-rose-300", dot: "bg-rose-500" },
  PENDING: { label: "Pending Approval", bg: "bg-purple-50 dark:bg-purple-950/40", text: "text-purple-700 dark:text-purple-300", dot: "bg-purple-500" },
  VOID: { label: "Voided", bg: "bg-slate-100 dark:bg-slate-800", text: "text-slate-600 dark:text-slate-400", dot: "bg-slate-400" },
  RECORDED: { label: "Recorded", bg: "bg-emerald-50 dark:bg-emerald-950/40", text: "text-emerald-700 dark:text-emerald-300", dot: "bg-emerald-500" },
  DRAFT: { label: "Draft", bg: "bg-slate-100 dark:bg-slate-800", text: "text-slate-600 dark:text-slate-400", dot: "bg-slate-400" },
  ACTIVE: { label: "Active", bg: "bg-blue-50 dark:bg-blue-950/40", text: "text-blue-700 dark:text-blue-300", dot: "bg-blue-500" },
  CLOSED: { label: "Closed", bg: "bg-slate-100 dark:bg-slate-800", text: "text-slate-600 dark:text-slate-400", dot: "bg-slate-400" },
  SUBMITTED: { label: "Submitted", bg: "bg-amber-50 dark:bg-amber-950/40", text: "text-amber-700 dark:text-amber-300", dot: "bg-amber-500" },
  APPROVED: { label: "Approved", bg: "bg-emerald-50 dark:bg-emerald-950/40", text: "text-emerald-700 dark:text-emerald-300", dot: "bg-emerald-500" },
  RELEASED: { label: "Released", bg: "bg-indigo-50 dark:bg-indigo-950/40", text: "text-indigo-700 dark:text-indigo-300", dot: "bg-indigo-500" },
  RESOLVED: { label: "Resolved", bg: "bg-emerald-50 dark:bg-emerald-950/40", text: "text-emerald-700 dark:text-emerald-300", dot: "bg-emerald-500" },
  IN_REVIEW: { label: "In Review", bg: "bg-amber-50 dark:bg-amber-950/40", text: "text-amber-700 dark:text-amber-300", dot: "bg-amber-500" },
  RECEIVED: { label: "Received", bg: "bg-blue-50 dark:bg-blue-950/40", text: "text-blue-700 dark:text-blue-300", dot: "bg-blue-500" },
  PRESENT: { label: "Present", bg: "bg-emerald-50 dark:bg-emerald-950/40", text: "text-emerald-700 dark:text-emerald-300", dot: "bg-emerald-500" },
  ABSENT: { label: "Absent", bg: "bg-rose-50 dark:bg-rose-950/40", text: "text-rose-700 dark:text-rose-300", dot: "bg-rose-500" },
  LATE: { label: "Late", bg: "bg-amber-50 dark:bg-amber-950/40", text: "text-amber-700 dark:text-amber-300", dot: "bg-amber-500" },
};

export function StatusPill({ status, size = "sm", className = "" }: StatusPillProps) {
  const norm = status.toUpperCase();
  const conf = statusConfig[norm] ?? {
    label: status.replaceAll("_", " "),
    bg: "bg-slate-100 dark:bg-slate-800",
    text: "text-slate-700 dark:text-slate-300",
    dot: "bg-slate-400",
  };

  const sizeClasses = size === "sm" ? "px-2.5 py-0.5 text-[11px]" : "px-3 py-1 text-xs";

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full font-bold uppercase tracking-wider ${conf.bg} ${conf.text} ${sizeClasses} ${className}`}
    >
      <span className={`h-1.5 w-1.5 rounded-full ${conf.dot}`} aria-hidden="true" />
      {conf.label}
    </span>
  );
}
