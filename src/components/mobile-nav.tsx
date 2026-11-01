"use client";

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import Link from "next/link";
import Image from "next/image";
import { usePathname, useRouter } from "next/navigation";
import {
  ArrowLeft,
  Bell,
  LogOut,
  MoreHorizontal,
  Search,
  X,
  ChevronRight,
  ShieldCheck,
  User,
} from "lucide-react";
import { getMobileNavigation, MobileNavItem, RoleMobileNav } from "@/lib/navigation";
import { ThemeStyleControls } from "@/components/design-system/theme-provider";

type MobileNavProps = {
  role: string;
  active: string;
  user: {
    name: string;
    role: string;
    avatarUrl?: string | null;
  };
};

export default function MobileNav({ role, active, user }: MobileNavProps) {
  const pathname = usePathname() ?? "";
  const router = useRouter();
  const [sheetOpen, setSheetOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [isKeyboardOpen, setIsKeyboardOpen] = useState(false);
  const [badges, setBadges] = useState<{
    unreadNotifications: number;
    pendingComplaints: number;
    pendingApprovals: number;
    messages: number;
  }>({
    unreadNotifications: 0,
    pendingComplaints: 0,
    pendingApprovals: 0,
    messages: 0,
  });

  const touchStartY = useRef<number | null>(null);
  const touchCurrentY = useRef<number | null>(null);
  const sheetContentRef = useRef<HTMLDivElement>(null);

  const navConfig: RoleMobileNav = useMemo(() => getMobileNavigation(role), [role]);

  // Check if current route is a full-screen wizard
  const isWizard = pathname.startsWith("/admissions/new") || pathname.startsWith("/staff/new");
  const wizardBackHref = pathname.startsWith("/admissions/new")
    ? "/admissions"
    : pathname.startsWith("/staff/new")
    ? "/staff"
    : "/dashboard";
  const wizardTitle = pathname.startsWith("/admissions/new")
    ? "Exit Admission Form"
    : "Exit Staff Onboarding";

  // Fetch real-time numeric badges
  const loadBadges = useCallback(async () => {
    try {
      const res = await fetch("/api/nav/badges");
      if (!res.ok) return;
      const data = await res.json();
      setBadges({
        unreadNotifications: data.unreadNotifications ?? 0,
        pendingComplaints: data.pendingComplaints ?? 0,
        pendingApprovals: data.pendingApprovals ?? 0,
        messages: data.messages ?? 0,
      });
    } catch {
      // ignore
    }
  }, []);

  useEffect(() => {
    void loadBadges();
    const interval = setInterval(loadBadges, 30000);
    return () => clearInterval(interval);
  }, [loadBadges]);

  // Keyboard awareness via visualViewport
  useEffect(() => {
    if (typeof window === "undefined" || !window.visualViewport) return;
    const viewport = window.visualViewport;

    const handleResize = () => {
      // If viewport height is noticeably smaller than screen height, keyboard is open
      const keyboardShowing = viewport.height < window.innerHeight * 0.78;
      setIsKeyboardOpen(keyboardShowing);
    };

    viewport.addEventListener("resize", handleResize);
    viewport.addEventListener("scroll", handleResize);
    return () => {
      viewport.removeEventListener("resize", handleResize);
      viewport.removeEventListener("scroll", handleResize);
    };
  }, []);

  // Close sheet on route change
  useEffect(() => {
    setSheetOpen(false);
    setSearchQuery("");
  }, [pathname]);

  // Handle ESC key to close sheet
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape" && sheetOpen) {
        setSheetOpen(false);
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [sheetOpen]);

  // Touch gestures for swipe-down to close sheet
  const handleTouchStart = (e: React.TouchEvent) => {
    touchStartY.current = e.touches[0].clientY;
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    touchCurrentY.current = e.touches[0].clientY;
  };

  const handleTouchEnd = () => {
    if (touchStartY.current !== null && touchCurrentY.current !== null) {
      const deltaY = touchCurrentY.current - touchStartY.current;
      if (deltaY > 75) {
        setSheetOpen(false);
      }
    }
    touchStartY.current = null;
    touchCurrentY.current = null;
  };

  // Helper to determine badge value for a slot
  const getBadgeValue = (badgeKey?: string) => {
    if (!badgeKey) return 0;
    if (badgeKey === "complaints") return badges.pendingComplaints;
    if (badgeKey === "approvals") return badges.pendingApprovals;
    if (badgeKey === "messages") return badges.messages;
    if (badgeKey === "notifications") return badges.unreadNotifications;
    return 0;
  };

  const moreBadgeCount =
    (badges.unreadNotifications > 0 ? 1 : 0) +
    (badges.pendingApprovals > 0 ? badges.pendingApprovals : 0);

  // Filtered categories for More bottom sheet search
  const filteredCategories = useMemo(() => {
    if (!searchQuery.trim()) return navConfig.moreCategories;
    const q = searchQuery.toLowerCase().trim();

    return navConfig.moreCategories
      .map((cat) => ({
        ...cat,
        items: cat.items.filter(
          (item) =>
            item.label.toLowerCase().includes(q) ||
            item.href.toLowerCase().includes(q)
        ),
      }))
      .filter((cat) => cat.items.length > 0);
  }, [navConfig.moreCategories, searchQuery]);

  // If keyboard is open, do not show the mobile bottom bar to prevent overlaying inputs
  if (isKeyboardOpen) {
    return null;
  }

  // Inside full-screen wizards, render an ergonomic docked back bar instead of 5 tabs
  if (isWizard) {
    return (
      <div className="fixed inset-x-0 bottom-0 z-40 border-t border-slate-200/80 bg-white/95 px-4 pb-[max(0.75rem,env(safe-area-inset-bottom))] pt-2 shadow-[0_-8px_24px_rgba(15,35,70,0.08)] backdrop-blur lg:hidden dark:border-slate-800 dark:bg-slate-950/95">
        <div className="flex items-center justify-between">
          <Link
            href={wizardBackHref}
            className="inline-flex min-h-[44px] items-center gap-2 rounded-xl bg-slate-100 px-4 py-2 text-xs font-bold text-slate-700 transition hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"
          >
            <ArrowLeft size={16} />
            <span>{wizardTitle}</span>
          </Link>
          <span className="text-[11px] font-semibold text-slate-400">Step In Progress</span>
        </div>
      </div>
    );
  }

  return (
    <>
      {/* 5-Slot Bottom Bar */}
      <nav
        aria-label="Mobile bottom navigation"
        className="mobile-nav fixed inset-x-0 bottom-0 z-30 flex items-center justify-around border-t border-slate-200/80 bg-white/95 px-1 pb-[max(0.6rem,env(safe-area-inset-bottom))] pt-1.5 shadow-[0_-8px_24px_rgba(15,35,70,0.07)] backdrop-blur lg:hidden dark:border-slate-800 dark:bg-slate-950/95"
      >
        {/* 4 Primary Tabs */}
        {navConfig.primary.map(({ label, href, icon: Icon, badgeKey }) => {
          const isCurrent =
            active === label ||
            (href === "/dashboard" && pathname === "/dashboard") ||
            (href !== "/dashboard" && pathname.startsWith(href.split("?")[0]));
          const badgeCount = getBadgeValue(badgeKey);

          return (
            <Link
              key={label}
              href={href}
              aria-current={isCurrent ? "page" : undefined}
              className={`relative flex min-h-[48px] min-w-[48px] flex-1 flex-col items-center justify-center gap-1 rounded-xl px-1 py-1 text-[10.5px] font-bold transition active:scale-95 ${
                isCurrent
                  ? "text-blue-700 dark:text-blue-400"
                  : "text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200"
              }`}
            >
              <div className="relative">
                <Icon size={20} className={isCurrent ? "stroke-[2.5]" : "stroke-[1.75]"} />
                {badgeCount > 0 && (
                  <span className="absolute -right-2 -top-1.5 flex h-4 min-w-[16px] items-center justify-center rounded-full bg-rose-600 px-1 text-[9px] font-black text-white shadow-sm ring-1 ring-white dark:ring-slate-900">
                    {badgeCount > 99 ? "99+" : badgeCount}
                  </span>
                )}
              </div>
              <span className="truncate max-w-[64px] text-center">{label}</span>
              {isCurrent && (
                <span className="absolute bottom-0.5 h-1 w-6 rounded-full bg-blue-600 dark:bg-blue-400" />
              )}
            </Link>
          );
        })}

        {/* 5th Slot: More Tab */}
        <button
          type="button"
          onClick={() => setSheetOpen(true)}
          aria-label="Open more pages and actions menu"
          aria-expanded={sheetOpen}
          className={`relative flex min-h-[48px] min-w-[48px] flex-1 flex-col items-center justify-center gap-1 rounded-xl px-1 py-1 text-[10.5px] font-bold transition active:scale-95 ${
            sheetOpen
              ? "text-blue-700 dark:text-blue-400"
              : "text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200"
          }`}
        >
          <div className="relative">
            <MoreHorizontal size={20} className={sheetOpen ? "stroke-[2.5]" : "stroke-[1.75]"} />
            {moreBadgeCount > 0 && (
              <span className="absolute -right-2 -top-1.5 flex h-4 min-w-[16px] items-center justify-center rounded-full bg-blue-600 px-1 text-[9px] font-black text-white shadow-sm ring-1 ring-white dark:ring-slate-900">
                {moreBadgeCount}
              </span>
            )}
          </div>
          <span>More</span>
        </button>
      </nav>

      {/* "More" Bottom Sheet & Backdrop */}
      {sheetOpen && (
        <div
          role="dialog"
          aria-modal="true"
          aria-label="All pages and settings"
          className="fixed inset-0 z-50 flex flex-col justify-end bg-slate-900/60 backdrop-blur-sm transition-opacity lg:hidden"
          onClick={(e) => {
            if (e.target === e.currentTarget) setSheetOpen(false);
          }}
        >
          <div
            ref={sheetContentRef}
            onTouchStart={handleTouchStart}
            onTouchMove={handleTouchMove}
            onTouchEnd={handleTouchEnd}
            className="flex max-h-[85vh] flex-col rounded-t-[28px] border-t border-slate-200 bg-white pb-[max(1rem,env(safe-area-inset-bottom))] shadow-2xl transition-transform dark:border-slate-800 dark:bg-slate-950"
          >
            {/* Drag Handle */}
            <div className="flex flex-col items-center pt-3 pb-2 cursor-grab">
              <div className="h-1.5 w-12 rounded-full bg-slate-300 dark:bg-slate-700" />
            </div>

            {/* Header & Search */}
            <div className="flex items-center justify-between border-b border-slate-100 px-5 pb-3 pt-1 dark:border-slate-800">
              <div>
                <h2 className="text-base font-black text-slate-900 dark:text-white">
                  Mary Royal Workspace
                </h2>
                <p className="text-[11px] text-slate-500">{user.name} · {role}</p>
              </div>
              <button
                type="button"
                onClick={() => setSheetOpen(false)}
                aria-label="Close menu"
                className="grid h-8 w-8 place-items-center rounded-full bg-slate-100 text-slate-600 transition hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300"
              >
                <X size={16} />
              </button>
            </div>

            {/* Quick Search Box */}
            <div className="px-5 pt-3">
              <div className="relative">
                <Search
                  size={15}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400"
                />
                <input
                  type="search"
                  placeholder="Search pages, tools and services..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full rounded-2xl border border-slate-200 bg-slate-50/80 py-2.5 pl-10 pr-4 text-xs font-medium text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-600 focus:bg-white dark:border-slate-800 dark:bg-slate-900 dark:text-white dark:focus:border-blue-400"
                />
                {searchQuery && (
                  <button
                    type="button"
                    onClick={() => setSearchQuery("")}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-slate-400 hover:text-slate-600"
                  >
                    Clear
                  </button>
                )}
              </div>
            </div>

            {/* Scrollable Categories & Icon Grid */}
            <div className="flex-1 overflow-y-auto px-5 py-4 space-y-5">
              {filteredCategories.length === 0 ? (
                <div className="py-8 text-center text-xs text-slate-500">
                  No pages match &ldquo;{searchQuery}&rdquo;.
                </div>
              ) : (
                filteredCategories.map((group) => (
                  <div key={group.category} className="space-y-2">
                    <p className="text-[10px] font-extrabold uppercase tracking-wider text-slate-400">
                      {group.category}
                    </p>
                    <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
                      {group.items.map((item) => {
                        const Icon = item.icon;
                        const isCurrent =
                          active === item.label ||
                          (pathname.startsWith(item.href.split("?")[0]) && item.href !== "/");
                        const badgeCount = getBadgeValue(item.badgeKey);

                        return (
                          <Link
                            key={item.label}
                            href={item.href}
                            onClick={() => setSheetOpen(false)}
                            className={`flex min-h-[50px] items-center gap-2.5 rounded-xl border p-2.5 text-xs font-semibold transition active:scale-98 ${
                              isCurrent
                                ? "border-blue-200 bg-blue-50 text-blue-900 dark:border-blue-800 dark:bg-blue-950/40 dark:text-blue-300"
                                : "border-slate-200/80 bg-white text-slate-700 hover:border-blue-100 hover:bg-slate-50 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-300 dark:hover:bg-slate-850"
                            }`}
                          >
                            <div className="relative grid h-8 w-8 shrink-0 place-items-center rounded-lg bg-blue-50 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300">
                              <Icon size={16} />
                              {badgeCount > 0 && (
                                <span className="absolute -right-1 -top-1 flex h-3.5 min-w-[14px] items-center justify-center rounded-full bg-rose-600 px-0.5 text-[8.5px] font-black text-white">
                                  {badgeCount}
                                </span>
                              )}
                            </div>
                            <span className="truncate leading-tight">{item.label}</span>
                          </Link>
                        );
                      })}
                    </div>
                  </div>
                ))
              )}
            </div>

            {/* Bottom Actions: Theme, Notifications, Profile, Sign Out */}
            <div className="border-t border-slate-100 bg-slate-50/70 px-5 pt-3 dark:border-slate-800 dark:bg-slate-900/50">
              <div className="flex flex-wrap items-center justify-between gap-3 pb-2">
                <div className="flex items-center gap-2">
                  <ThemeStyleControls compact />
                  <Link
                    href="/profile"
                    onClick={() => setSheetOpen(false)}
                    className="inline-flex min-h-[36px] items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-xs font-bold text-slate-700 shadow-sm transition hover:bg-slate-50 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-200"
                  >
                    <User size={14} />
                    <span>Profile</span>
                  </Link>
                </div>

                <form action="/api/logout" method="post">
                  <button
                    type="submit"
                    className="inline-flex min-h-[36px] items-center gap-1.5 rounded-xl border border-rose-200 bg-rose-50 px-3 py-1.5 text-xs font-bold text-rose-700 shadow-sm transition hover:bg-rose-100 dark:border-rose-900/50 dark:bg-rose-950/40 dark:text-rose-300"
                  >
                    <LogOut size={14} />
                    <span>Sign Out</span>
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
