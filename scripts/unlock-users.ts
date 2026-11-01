import { PrismaClient } from "@prisma/client";

const db = new PrismaClient();

async function resetLockouts() {
  const result = await db.user.updateMany({
    data: {
      failedLogins: 0,
      suspended: false
    }
  });
  console.log(`Unlocked ${result.count} users.`);
}

resetLockouts().finally(() => db.$disconnect());
