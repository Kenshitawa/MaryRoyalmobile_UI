"use client";

import { Printer, Download } from "lucide-react";

export function PrintButton({ receiptId }: { receiptId?: string }) {
  return (
    <div className="flex flex-wrap items-center gap-3 print:hidden">
      <button
        type="button"
        onClick={() => window.print()}
        className="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-xs font-bold text-slate-700 shadow-sm transition hover:border-blue-400 hover:text-blue-700 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:border-blue-500"
      >
        <Printer size={15} />
        Print Receipt
      </button>

      {receiptId && (
        <a
          href={`/api/finance/payments/${receiptId}/pdf`}
          download={`receipt-${receiptId}.pdf`}
          className="inline-flex items-center gap-2 rounded-xl bg-blue-700 px-4 py-2.5 text-xs font-bold text-white shadow-md shadow-blue-700/20 transition hover:bg-blue-800"
        >
          <Download size={15} />
          Download Official PDF
        </a>
      )}
    </div>
  );
}
