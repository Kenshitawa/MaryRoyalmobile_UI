"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { AlertTriangle, RotateCcw } from "lucide-react";

export default function Error({ error, reset }: { error: Error & { digest?: string }; reset: () => void }) {
  const router = useRouter();
  useEffect(() => { if (error.digest) console.error("Page rendering failed", { digest: error.digest }); }, [error]);
  return <main role="alert" className="grid min-h-[70vh] place-items-center bg-slate-50 p-5 dark:bg-slate-950"><section className="w-full max-w-lg rounded-3xl border border-slate-200 bg-white p-7 text-center shadow-lg dark:border-slate-800 dark:bg-slate-900"><span className="mx-auto grid h-12 w-12 place-items-center rounded-2xl bg-amber-50 text-amber-800 dark:bg-amber-950 dark:text-amber-300"><AlertTriangle size={22}/></span><h1 className="mt-4 text-2xl font-extrabold text-slate-950 dark:text-white">This page could not load</h1><p className="mt-2 text-sm text-slate-600 dark:text-slate-300">Your information is safe. Try loading this page again, or return to your dashboard.</p><div className="mt-6 flex flex-wrap justify-center gap-3"><button type="button" onClick={() => reset()} className="inline-flex min-h-11 items-center gap-2 rounded-xl bg-blue-700 px-5 font-semibold text-white hover:bg-blue-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"><RotateCcw size={16}/>Try again</button><button type="button" onClick={() => router.push("/dashboard")} className="min-h-11 rounded-xl border border-slate-200 px-5 font-semibold text-slate-700 hover:bg-slate-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 dark:border-slate-700 dark:text-slate-200 dark:hover:bg-slate-800">Go to dashboard</button></div></section></main>;
}
