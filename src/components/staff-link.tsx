import React from "react";
import Link from "next/link";

interface StaffLinkProps {
  id: string;
  name: string;
  role?: string;
  className?: string;
  disabled?: boolean;
}

export function StaffLink({
  id,
  name,
  role,
  className = "",
  disabled = false
}: StaffLinkProps) {
  if (disabled) {
    return (
      <span className={`inline-flex flex-col text-slate-700 dark:text-slate-300 ${className}`}>
        <span className="font-semibold">{name}</span>
        {role && <span className="text-[11px] text-slate-400">{role}</span>}
      </span>
    );
  }

  return (
    <div className={`inline-flex flex-col ${className}`}>
      <Link
        href={`/staff/${id}`}
        className="group inline-flex items-center gap-1 font-bold text-blue-700 transition hover:text-blue-900 hover:underline focus-visible:rounded focus-visible:outline focus-visible:outline-2 focus-visible:outline-blue-600 dark:text-blue-400 dark:hover:text-blue-300"
      >
        <span>{name}</span>
      </Link>
      {role && <span className="text-[11px] text-slate-400">{role}</span>}
    </div>
  );
}
