-- CreateTable
CREATE TABLE "LearnerProfile" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "learnerId" TEXT NOT NULL,
    "dateOfBirth" DATETIME,
    "nationality" TEXT NOT NULL DEFAULT 'Kenyan',
    "upiNumber" TEXT,
    "stream" TEXT,
    "admissionDate" DATETIME,
    "previousSchool" TEXT,
    "previousClass" TEXT,
    "reasonForLeaving" TEXT,
    "religion" TEXT,
    "languagesSpoken" TEXT NOT NULL DEFAULT '',
    "county" TEXT NOT NULL DEFAULT '',
    "subCounty" TEXT NOT NULL DEFAULT '',
    "ward" TEXT NOT NULL DEFAULT '',
    "village" TEXT NOT NULL DEFAULT '',
    "physicalAddress" TEXT NOT NULL DEFAULT '',
    "landmark" TEXT,
    "distanceToSchoolKm" REAL,
    "transportMeans" TEXT,
    "latitude" REAL,
    "longitude" REAL,
    "knownConditions" TEXT,
    "allergies" TEXT,
    "currentMedication" TEXT,
    "specialNeeds" TEXT,
    "bloodGroup" TEXT,
    "doctorOrHospital" TEXT,
    "immunizationStatus" TEXT,
    "healthInsurance" TEXT,
    "feePayer" TEXT,
    "consentAt" DATETIME,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "LearnerProfile_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "LearnerGuardian" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "profileId" TEXT NOT NULL,
    "fullName" TEXT NOT NULL,
    "relationship" TEXT NOT NULL,
    "idNumber" TEXT,
    "phone" TEXT NOT NULL,
    "alternatePhone" TEXT,
    "whatsapp" BOOLEAN NOT NULL DEFAULT false,
    "email" TEXT,
    "occupation" TEXT,
    "employer" TEXT,
    "homeAddress" TEXT,
    "workAddress" TEXT,
    "paysFees" BOOLEAN NOT NULL DEFAULT false,
    "primaryContact" BOOLEAN NOT NULL DEFAULT false,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "LearnerGuardian_profileId_fkey" FOREIGN KEY ("profileId") REFERENCES "LearnerProfile" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "LearnerEmergencyContact" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "profileId" TEXT NOT NULL,
    "fullName" TEXT NOT NULL,
    "relationship" TEXT NOT NULL,
    "phone" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "LearnerEmergencyContact_profileId_fkey" FOREIGN KEY ("profileId") REFERENCES "LearnerProfile" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "LearnerPickupPerson" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "profileId" TEXT NOT NULL,
    "fullName" TEXT NOT NULL,
    "relationship" TEXT NOT NULL,
    "phone" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "LearnerPickupPerson_profileId_fkey" FOREIGN KEY ("profileId") REFERENCES "LearnerProfile" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "AdmissionDraft" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "admissionNo" TEXT NOT NULL,
    "createdById" TEXT NOT NULL,
    "learnerId" TEXT,
    "payload" TEXT NOT NULL DEFAULT '{}',
    "currentStep" INTEGER NOT NULL DEFAULT 1,
    "status" TEXT NOT NULL DEFAULT 'DRAFT',
    "submittedAt" DATETIME,
    "enrolledAt" DATETIME,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "AdmissionDraft_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "AdmissionDraft_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner" ("id") ON DELETE SET NULL ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "AdmissionDocument" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "draftId" TEXT NOT NULL,
    "category" TEXT NOT NULL,
    "originalName" TEXT NOT NULL,
    "storageKey" TEXT NOT NULL,
    "contentType" TEXT NOT NULL,
    "sizeBytes" INTEGER NOT NULL,
    "uploadedById" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "AdmissionDocument_draftId_fkey" FOREIGN KEY ("draftId") REFERENCES "AdmissionDraft" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "AdmissionDocument_uploadedById_fkey" FOREIGN KEY ("uploadedById") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- CreateIndex
CREATE UNIQUE INDEX "LearnerProfile_learnerId_key" ON "LearnerProfile"("learnerId");

-- CreateIndex
CREATE INDEX "LearnerProfile_county_subCounty_idx" ON "LearnerProfile"("county", "subCounty");

-- CreateIndex
CREATE INDEX "LearnerGuardian_profileId_primaryContact_idx" ON "LearnerGuardian"("profileId", "primaryContact");

-- CreateIndex
CREATE INDEX "LearnerGuardian_phone_idx" ON "LearnerGuardian"("phone");

-- CreateIndex
CREATE INDEX "LearnerEmergencyContact_profileId_idx" ON "LearnerEmergencyContact"("profileId");

-- CreateIndex
CREATE INDEX "LearnerPickupPerson_profileId_idx" ON "LearnerPickupPerson"("profileId");

-- CreateIndex
CREATE UNIQUE INDEX "AdmissionDraft_admissionNo_key" ON "AdmissionDraft"("admissionNo");

-- CreateIndex
CREATE UNIQUE INDEX "AdmissionDraft_learnerId_key" ON "AdmissionDraft"("learnerId");

-- CreateIndex
CREATE INDEX "AdmissionDraft_createdById_status_updatedAt_idx" ON "AdmissionDraft"("createdById", "status", "updatedAt");

-- CreateIndex
CREATE INDEX "AdmissionDraft_status_updatedAt_idx" ON "AdmissionDraft"("status", "updatedAt");

-- CreateIndex
CREATE UNIQUE INDEX "AdmissionDocument_storageKey_key" ON "AdmissionDocument"("storageKey");

-- CreateIndex
CREATE INDEX "AdmissionDocument_draftId_category_idx" ON "AdmissionDocument"("draftId", "category");
