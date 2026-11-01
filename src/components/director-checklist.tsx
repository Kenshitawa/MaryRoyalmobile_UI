"use client";

import React, { useState } from "react";
import Link from "next/link";
import { CheckCircle2, Circle, ArrowRight, Sparkles, ChevronDown, ChevronUp } from "lucide-react";
import { ThemedCard } from "@/components/design-system/card";

interface ChecklistItem {
  id: string;
  step: number;
  title: string;
  description: string;
  href: string;
  actionText: string;
  completed: boolean;
  role: string;
}

export function DirectorGettingStartedChecklist({
  stats
}: {
  stats: {
    staffCount: number;
    learnerCount: number;
    feeStructuresCount: number;
    examSessionsCount: number;
    attendanceCount: number;
    marksCount: number;
    paymentsCount: number;
  };
}) {
  const [collapsed, setCollapsed] = useState(false);

  const steps: ChecklistItem[] = [
    {
      id: "s1",
      step: 1,
      title: "Set Up Fee Structures & CBC Rubrics",
      description: "Define class term fees, compulsory and optional items, and CBC proficiency standards.",
      href: "/finance/structures",
      actionText: "Configure fee structures",
      completed: stats.feeStructuresCount > 0,
      role: "Director / Head Teacher"
    },
    {
      id: "s2",
      step: 2,
      title: "Create Staff Accounts",
      description: "Provision accounts for Head Teacher, Deputy, Secretary, and Teachers.",
      href: "/accounts",
      actionText: "Manage staff accounts",
      completed: stats.staffCount > 1,
      role: "Director"
    },
    {
      id: "s3",
      step: 3,
      title: "Admit First Learners",
      description: "Secretary completes the 7-step admission wizard, uploads documents, and generates admission numbers.",
      href: "/admissions",
      actionText: "Open admissions pipeline",
      completed: stats.learnerCount > 0,
      role: "Secretary"
    },
    {
      id: "s4",
      step: 4,
      title: "Schedule Term Exam Sessions",
      description: "Deputy sets active exam sessions (Opening, Mid-Term, End-Term) so teachers can enter marks.",
      href: "/assessments",
      actionText: "Manage exam sessions",
      completed: stats.examSessionsCount > 0,
      role: "Deputy Head Teacher"
    },
    {
      id: "s5",
      step: 5,
      title: "Take Daily Class Attendance",
      description: "Class teachers mark morning attendance registers with present, absent, and late tallies.",
      href: "/attendance",
      actionText: "View attendance overview",
      completed: stats.attendanceCount > 0,
      role: "Teachers"
    },
    {
      id: "s6",
      step: 6,
      title: "Enter CBC Assessment Marks",
      description: "Subject teachers record competency scores and descriptors for active exam sessions.",
      href: "/assessments",
      actionText: "Review assessments",
      completed: stats.marksCount > 0,
      role: "Teachers"
    },
    {
      id: "s7",
      step: 7,
      title: "Record Fee Collections",
      description: "The finance team issues term invoices and records M-Pesa, Cash, or Bank receipts.",
      href: "/finance",
      actionText: "Open finance desk",
      completed: stats.paymentsCount > 0,
      role: "Finance team"
    }
  ];

  const completedCount = steps.filter(s => s.completed).length;
  const progressPercent = Math.round((completedCount / steps.length) * 100);
  if (progressPercent >= 100) return null;

  return (
    <ThemedCard className="mb-6 overflow-hidden border-blue-200/80 p-0 shadow-md">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-3 bg-gradient-to-r from-blue-900 via-blue-800 to-blue-700 px-6 py-4 text-white">
        <div className="flex items-center gap-3">
          <div className="grid h-9 w-9 place-items-center rounded-xl bg-white/15 text-blue-200">
            <Sparkles size={18} />
          </div>
          <div>
            <h2 className="text-base font-extrabold tracking-tight">
              School Setup Guide &amp; Launch Order
            </h2>
            <p className="text-xs text-blue-200">
              {completedCount} of {steps.length} steps complete ({progressPercent}%)
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="hidden h-2 w-36 overflow-hidden rounded-full bg-white/20 sm:block">
            <div
              className="h-full rounded-full bg-emerald-400 transition-all duration-500"
              style={{ width: `${progressPercent}%` }}
            />
          </div>
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="flex items-center gap-1 rounded-lg bg-white/10 px-2.5 py-1 text-xs font-semibold text-white hover:bg-white/20"
          >
            {collapsed ? "Show steps" : "Hide"}
            {collapsed ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
          </button>
        </div>
      </div>

      {/* Steps body */}
      {!collapsed && (
        <div className="divide-y divide-slate-100 bg-white p-2 dark:divide-slate-800 dark:bg-slate-900">
          {steps.map(step => (
            <div
              key={step.id}
              className="flex flex-wrap items-center justify-between gap-3 p-3.5 transition hover:bg-slate-50/70 dark:hover:bg-slate-800/40 rounded-xl"
            >
              <div className="flex items-start gap-3 min-w-0 flex-1">
                <div className="mt-0.5 shrink-0">
                  {step.completed ? (
                    <CheckCircle2 size={18} className="text-emerald-600 dark:text-emerald-400" />
                  ) : (
                    <Circle size={18} className="text-slate-300 dark:text-slate-600" />
                  )}
                </div>
                <div className="min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-slate-800 dark:text-slate-200">
                      Step {step.step}: {step.title}
                    </span>
                    <span className="rounded bg-slate-100 px-1.5 py-0.5 text-[10px] font-semibold text-slate-500 dark:bg-slate-800">
                      {step.role}
                    </span>
                  </div>
                  <p className="mt-0.5 text-xs text-slate-500 dark:text-slate-400">
                    {step.description}
                  </p>
                </div>
              </div>

              <div>
                <Link
                  href={step.href}
                  className={`inline-flex items-center gap-1 rounded-lg px-3 py-1.5 text-xs font-bold transition ${
                    step.completed
                      ? "bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300"
                      : "bg-blue-600 text-white hover:bg-blue-700 shadow-sm"
                  }`}
                >
                  <span>{step.actionText}</span>
                  <ArrowRight size={13} />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </ThemedCard>
  );
}
