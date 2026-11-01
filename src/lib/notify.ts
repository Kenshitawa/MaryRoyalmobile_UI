import { db } from "@/lib/db";

export async function createNotification({
  userId,
  title,
  message,
  type = "INFO",
  link,
  actionRequired = false,
}: {
  userId: string;
  title: string;
  message: string;
  type?: "INFO" | "SUCCESS" | "WARNING" | "ACTION";
  link?: string;
  actionRequired?: boolean;
}) {
  await db.notification.create({
    data: { userId, title, message, type, link: link ?? null, actionRequired },
  });
}

export async function notifyMany(userIds: string[], payload: Omit<Parameters<typeof createNotification>[0], "userId">) {
  await db.notification.createMany({
    data: userIds.map((userId) => ({ userId, ...payload, link: payload.link ?? null })),
  });
}
