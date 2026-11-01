ALTER TABLE "FeeInvoice" ADD COLUMN "structureId" TEXT;
ALTER TABLE "FeeInvoice" ADD COLUMN "structureVersion" INTEGER;

CREATE TABLE "FeeStructure" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "grade" TEXT NOT NULL,
  "term" TEXT NOT NULL,
  "version" INTEGER NOT NULL,
  "effectiveFrom" DATETIME NOT NULL,
  "active" BOOLEAN NOT NULL DEFAULT true,
  "createdById" TEXT NOT NULL,
  "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "FeeStructure_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE "FeeStructureLine" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "structureId" TEXT NOT NULL,
  "category" TEXT NOT NULL,
  "description" TEXT NOT NULL,
  "amount" INTEGER NOT NULL,
  "optional" BOOLEAN NOT NULL DEFAULT false,
  CONSTRAINT "FeeStructureLine_structureId_fkey" FOREIGN KEY ("structureId") REFERENCES "FeeStructure" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE UNIQUE INDEX "FeeStructure_grade_term_version_key" ON "FeeStructure"("grade", "term", "version");
CREATE INDEX "FeeStructure_grade_term_active_idx" ON "FeeStructure"("grade", "term", "active");
CREATE INDEX "FeeStructureLine_structureId_idx" ON "FeeStructureLine"("structureId");
CREATE INDEX "FeeInvoice_structureId_idx" ON "FeeInvoice"("structureId");
