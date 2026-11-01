ALTER TABLE "User" ADD COLUMN "staffNumber" TEXT;
ALTER TABLE "User" ADD COLUMN "tscNumber" TEXT;
ALTER TABLE "User" ADD COLUMN "qualifications" TEXT;
ALTER TABLE "User" ADD COLUMN "employmentTitle" TEXT;

CREATE TABLE "StaffAttendance" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "staffId" TEXT NOT NULL,
    "date" DATETIME NOT NULL,
    "status" TEXT NOT NULL,
    "checkInAt" DATETIME,
    "note" TEXT,
    "recordedById" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "StaffAttendance_staffId_fkey" FOREIGN KEY ("staffId") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "StaffAttendance_recordedById_fkey" FOREIGN KEY ("recordedById") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE UNIQUE INDEX "StaffAttendance_staffId_date_key" ON "StaffAttendance"("staffId", "date");
CREATE INDEX "StaffAttendance_date_status_idx" ON "StaffAttendance"("date", "status");
CREATE INDEX "StaffAttendance_recordedById_date_idx" ON "StaffAttendance"("recordedById", "date");

CREATE TABLE "DisciplineIncident" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "learnerId" TEXT NOT NULL,
    "incidentAt" DATETIME NOT NULL,
    "category" TEXT NOT NULL,
    "severity" TEXT NOT NULL,
    "summary" TEXT NOT NULL,
    "actionTaken" TEXT NOT NULL,
    "parentNotifiedAt" DATETIME,
    "followUpAt" DATETIME,
    "status" TEXT NOT NULL DEFAULT 'OPEN',
    "recordedById" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "DisciplineIncident_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner" ("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "DisciplineIncident_recordedById_fkey" FOREIGN KEY ("recordedById") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE INDEX "DisciplineIncident_learnerId_incidentAt_idx" ON "DisciplineIncident"("learnerId", "incidentAt");
CREATE INDEX "DisciplineIncident_status_followUpAt_idx" ON "DisciplineIncident"("status", "followUpAt");
