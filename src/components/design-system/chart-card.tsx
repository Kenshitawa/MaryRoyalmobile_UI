"use client";

import React from "react";
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid
} from "recharts";
import { ThemedCard } from "./card";

interface ChartCardProps {
  title: string;
  subtitle?: string;
  data: Array<Record<string, unknown>>;
  type?: "area" | "bar";
  dataKey: string;
  categoryKey: string;
  secondaryKey?: string;
  valueFormatter?: (value: number) => string;
  height?: number;
  className?: string;
}

export function ChartCard({
  title,
  subtitle,
  data,
  type = "area",
  dataKey,
  categoryKey,
  secondaryKey,
  valueFormatter = (v) => String(v),
  height = 220,
  className = ""
}: ChartCardProps) {
  return (
    <ThemedCard className={`p-5 sm:p-6 ${className}`}>
      <div className="mb-4 flex items-start justify-between gap-3">
        <div>
          <h3 className="text-base font-extrabold text-slate-900 dark:text-white">
            {title}
          </h3>
          {subtitle && (
            <p className="mt-0.5 text-xs text-slate-500 dark:text-slate-400">
              {subtitle}
            </p>
          )}
        </div>
      </div>

      <div style={{ width: "100%", height }}>
        {data.length === 0 ? (
          <div className="flex h-full items-center justify-center text-xs text-slate-400">
            No chart data available yet
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            {type === "area" ? (
              <AreaChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#1d4ed8" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#1d4ed8" stopOpacity={0.0} />
                  </linearGradient>
                  {secondaryKey && (
                    <linearGradient id="secondaryGradient" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#0ea5e9" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="#0ea5e9" stopOpacity={0.0} />
                    </linearGradient>
                  )}
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(148, 163, 184, 0.2)" />
                <XAxis
                  dataKey={categoryKey}
                  tickLine={false}
                  axisLine={false}
                  tick={{ fontSize: 11, fill: "#64748b" }}
                />
                <YAxis
                  tickLine={false}
                  axisLine={false}
                  tick={{ fontSize: 11, fill: "#64748b" }}
                  tickFormatter={valueFormatter}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#10233f",
                    borderRadius: "10px",
                    border: "none",
                    color: "#ffffff",
                    fontSize: "12px",
                    boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.3)"
                  }}
                  formatter={(value: unknown) => [valueFormatter(Number(value)), title]}
                />
                <Area
                  type="monotone"
                  dataKey={dataKey}
                  stroke="#1d4ed8"
                  strokeWidth={2.5}
                  fillOpacity={1}
                  fill="url(#areaGradient)"
                />
                {secondaryKey && (
                  <Area
                    type="monotone"
                    dataKey={secondaryKey}
                    stroke="#0ea5e9"
                    strokeWidth={2}
                    fillOpacity={1}
                    fill="url(#secondaryGradient)"
                  />
                )}
              </AreaChart>
            ) : (
              <BarChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(148, 163, 184, 0.2)" />
                <XAxis
                  dataKey={categoryKey}
                  tickLine={false}
                  axisLine={false}
                  tick={{ fontSize: 11, fill: "#64748b" }}
                />
                <YAxis
                  tickLine={false}
                  axisLine={false}
                  tick={{ fontSize: 11, fill: "#64748b" }}
                  tickFormatter={valueFormatter}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#10233f",
                    borderRadius: "10px",
                    border: "none",
                    color: "#ffffff",
                    fontSize: "12px",
                    boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.3)"
                  }}
                  formatter={(value: unknown) => [valueFormatter(Number(value)), "Amount"]}
                />
                <Bar
                  dataKey={dataKey}
                  fill="#1d4ed8"
                  radius={[6, 6, 0, 0]}
                  maxBarSize={45}
                />
                {secondaryKey && (
                  <Bar
                    dataKey={secondaryKey}
                    fill="#38bdf8"
                    radius={[6, 6, 0, 0]}
                    maxBarSize={45}
                  />
                )}
              </BarChart>
            )}
          </ResponsiveContainer>
        )}
      </div>
    </ThemedCard>
  );
}
