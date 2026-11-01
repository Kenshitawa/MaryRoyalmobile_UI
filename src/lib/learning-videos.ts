export function youtubeVideoId(value: string): string | null {
  try {
    const url = new URL(value);
    const host = url.hostname.toLowerCase().replace(/^www\./, "");
    const id = host === "youtu.be" ? url.pathname.split("/").filter(Boolean)[0] : host === "youtube.com" || host === "m.youtube.com" ? url.searchParams.get("v") ?? url.pathname.split("/").filter(Boolean).at(-1) : null;
    return id && /^[A-Za-z0-9_-]{11}$/.test(id) ? id : null;
  } catch { return null; }
}
