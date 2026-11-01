-- CreateTable
CREATE TABLE "SchoolSetting" (
    "key" TEXT NOT NULL PRIMARY KEY,
    "value" TEXT NOT NULL,
    "updatedAt" DATETIME NOT NULL
);

-- CreateTable
CREATE TABLE "CbcLevel" (
    "code" TEXT NOT NULL PRIMARY KEY,
    "label" TEXT NOT NULL,
    "minScore" REAL NOT NULL,
    "maxScore" REAL NOT NULL,
    "descriptor" TEXT NOT NULL,
    "sortOrder" INTEGER NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true
);

-- RedefineTables
PRAGMA defer_foreign_keys=ON;
PRAGMA foreign_keys=OFF;
CREATE TABLE "new_Complaint" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "subject" TEXT NOT NULL,
    "category" TEXT NOT NULL DEFAULT 'OTHER',
    "message" TEXT NOT NULL,
    "audience" TEXT NOT NULL,
    "reporterName" TEXT,
    "reporterRole" TEXT,
    "status" TEXT NOT NULL DEFAULT 'OPEN',
    "openedById" TEXT NOT NULL,
    "assignedToId" TEXT,
    "learnerId" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "Complaint_openedById_fkey" FOREIGN KEY ("openedById") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "Complaint_assignedToId_fkey" FOREIGN KEY ("assignedToId") REFERENCES "User" ("id") ON DELETE SET NULL ON UPDATE CASCADE
);
INSERT INTO "new_Complaint" ("assignedToId", "audience", "createdAt", "id", "learnerId", "message", "openedById", "reporterName", "reporterRole", "status", "subject", "updatedAt") SELECT "assignedToId", "audience", "createdAt", "id", "learnerId", "message", "openedById", "reporterName", "reporterRole", "status", "subject", "updatedAt" FROM "Complaint";
DROP TABLE "Complaint";
ALTER TABLE "new_Complaint" RENAME TO "Complaint";
CREATE INDEX "Complaint_openedById_createdAt_idx" ON "Complaint"("openedById", "createdAt");
CREATE INDEX "Complaint_assignedToId_status_idx" ON "Complaint"("assignedToId", "status");
PRAGMA foreign_keys=ON;
PRAGMA defer_foreign_keys=OFF;

-- CreateIndex
CREATE UNIQUE INDEX "CbcLevel_sortOrder_key" ON "CbcLevel"("sortOrder");

