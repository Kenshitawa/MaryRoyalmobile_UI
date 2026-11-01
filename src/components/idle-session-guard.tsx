"use client";
import { useEffect } from "react";
import { usePathname } from "next/navigation";

export default function IdleSessionGuard() {
  const pathname = usePathname();
  useEffect(() => {
    if (pathname === "/" || pathname.startsWith("/privacy")) return;
    let timeout: ReturnType<typeof setTimeout>;
    let ended = false;
    let lastRefresh = Date.now();
    const expire = () => {
      if (ended) return;
      ended = true;
      void fetch("/api/logout", { method: "POST", credentials: "same-origin" }).finally(() => location.assign("/?reason=idle"));
    };
    const reset = () => {
      clearTimeout(timeout);
      timeout = setTimeout(expire, 10 * 60 * 1000);
      if (Date.now() - lastRefresh >= 60_000) {
        lastRefresh = Date.now();
        void fetch("/api/session/refresh", { method: "POST", credentials: "same-origin" }).then(response => { if (response.status === 401) expire(); }).catch(() => undefined);
      }
    };
    const events: Array<keyof WindowEventMap> = ["pointerdown", "keydown", "touchstart", "scroll"];
    events.forEach(event => window.addEventListener(event, reset, { passive: true }));
    reset();
    return () => { clearTimeout(timeout); events.forEach(event => window.removeEventListener(event, reset)); };
  }, [pathname]);
  return null;
}
