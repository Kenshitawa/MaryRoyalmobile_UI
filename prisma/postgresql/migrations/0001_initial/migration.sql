-- CreateSchema
CREATE SCHEMA IF NOT EXISTS "public";

-- CreateEnum
CREATE TYPE "Role" AS ENUM ('DIRECTOR', 'HEAD_TEACHER', 'DEPUTY', 'SECRETARY', 'ACCOUNTANT', 'TEACHER', 'WELFARE_CHAIR', 'NON_TEACHING_STAFF', 'PARENT', 'LEARNER');

-- CreateTable
CREATE TABLE "User" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "avatarUrl" TEXT,
    "staffNumber" TEXT,
    "tscNumber" TEXT,
    "qualifications" TEXT,
    "employmentTitle" TEXT,
    "email" TEXT NOT NULL,
    "phone" TEXT,
    "governmentId" TEXT,
    "passwordHash" TEXT NOT NULL,
    "role" "Role" NOT NULL,
    "forcePasswordChange" BOOLEAN NOT NULL DEFAULT true,
    "suspended" BOOLEAN NOT NULL DEFAULT false,
    "suspensionReason" TEXT,
    "failedLogins" INTEGER NOT NULL DEFAULT 0,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "uiTheme" TEXT DEFAULT 'system',
    "isWelfareChair" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Learner" (
    "id" TEXT NOT NULL,
    "admissionNo" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "gender" TEXT NOT NULL,
    "archivedAt" TIMESTAMP(3),
    "admissionStatus" TEXT NOT NULL DEFAULT 'APPROVED',
    "admissionApprovedById" TEXT,
    "admissionApprovedAt" TIMESTAMP(3),

    CONSTRAINT "Learner_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearnerProfile" (
    "id" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "dateOfBirth" TIMESTAMP(3),
    "nationality" TEXT NOT NULL DEFAULT 'Kenyan',
    "upiNumber" TEXT,
    "stream" TEXT,
    "admissionDate" TIMESTAMP(3),
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
    "distanceToSchoolKm" DOUBLE PRECISION,
    "transportMeans" TEXT,
    "latitude" DOUBLE PRECISION,
    "longitude" DOUBLE PRECISION,
    "knownConditions" TEXT,
    "allergies" TEXT,
    "currentMedication" TEXT,
    "specialNeeds" TEXT,
    "bloodGroup" TEXT,
    "doctorOrHospital" TEXT,
    "immunizationStatus" TEXT,
    "healthInsurance" TEXT,
    "feePayer" TEXT,
    "consentAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "LearnerProfile_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearnerGuardian" (
    "id" TEXT NOT NULL,
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
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "LearnerGuardian_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearnerEmergencyContact" (
    "id" TEXT NOT NULL,
    "profileId" TEXT NOT NULL,
    "fullName" TEXT NOT NULL,
    "relationship" TEXT NOT NULL,
    "phone" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "LearnerEmergencyContact_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearnerPickupPerson" (
    "id" TEXT NOT NULL,
    "profileId" TEXT NOT NULL,
    "fullName" TEXT NOT NULL,
    "relationship" TEXT NOT NULL,
    "phone" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "LearnerPickupPerson_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "AdmissionDraft" (
    "id" TEXT NOT NULL,
    "admissionNo" TEXT NOT NULL,
    "createdById" TEXT NOT NULL,
    "learnerId" TEXT,
    "payload" TEXT NOT NULL DEFAULT '{}',
    "currentStep" INTEGER NOT NULL DEFAULT 1,
    "status" TEXT NOT NULL DEFAULT 'DRAFT',
    "submittedAt" TIMESTAMP(3),
    "enrolledAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "AdmissionDraft_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "AdmissionDocument" (
    "id" TEXT NOT NULL,
    "draftId" TEXT NOT NULL,
    "category" TEXT NOT NULL,
    "originalName" TEXT NOT NULL,
    "storageKey" TEXT NOT NULL,
    "contentType" TEXT NOT NULL,
    "sizeBytes" INTEGER NOT NULL,
    "uploadedById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "AdmissionDocument_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "AdmissionCounter" (
    "id" TEXT NOT NULL,
    "lastNumber" INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT "AdmissionCounter_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "SchoolSetting" (
    "key" TEXT NOT NULL,
    "value" TEXT NOT NULL,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "SchoolSetting_pkey" PRIMARY KEY ("key")
);

-- CreateTable
CREATE TABLE "CbcLevel" (
    "code" TEXT NOT NULL,
    "label" TEXT NOT NULL,
    "minScore" DOUBLE PRECISION NOT NULL,
    "maxScore" DOUBLE PRECISION NOT NULL,
    "descriptor" TEXT NOT NULL,
    "sortOrder" INTEGER NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT "CbcLevel_pkey" PRIMARY KEY ("code")
);

-- CreateTable
CREATE TABLE "GuardianLink" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "relation" TEXT NOT NULL DEFAULT 'Guardian',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "GuardianLink_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Attendance" (
    "id" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "date" TIMESTAMP(3) NOT NULL,
    "status" TEXT NOT NULL,

    CONSTRAINT "Attendance_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "TeacherAssignment" (
    "id" TEXT NOT NULL,
    "teacherId" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "subject" TEXT NOT NULL,

    CONSTRAINT "TeacherAssignment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ClassTeacherAssignment" (
    "id" TEXT NOT NULL,
    "teacherId" TEXT NOT NULL,
    "grade" TEXT NOT NULL,

    CONSTRAINT "ClassTeacherAssignment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "TeachingLog" (
    "id" TEXT NOT NULL,
    "teacherId" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "topics" TEXT NOT NULL,
    "reflection" TEXT,
    "status" TEXT NOT NULL DEFAULT 'SUBMITTED',
    "confirmedById" TEXT,
    "confirmedAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "TeachingLog_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Assignment" (
    "id" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "instructions" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "dueAt" TIMESTAMP(3),
    "maxScore" INTEGER NOT NULL DEFAULT 100,
    "answerKey" TEXT,
    "autoMark" BOOLEAN NOT NULL DEFAULT false,
    "attachmentUrl" TEXT,
    "attachmentType" TEXT,
    "teacherId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Assignment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "AssignmentSubmission" (
    "id" TEXT NOT NULL,
    "assignmentId" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "response" TEXT NOT NULL,
    "attachmentUrl" TEXT,
    "attachmentType" TEXT,
    "score" INTEGER,
    "feedback" TEXT,
    "status" TEXT NOT NULL DEFAULT 'SUBMITTED',
    "submittedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "AssignmentSubmission_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearningResource" (
    "id" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "kind" TEXT NOT NULL,
    "url" TEXT NOT NULL,
    "teacherId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "LearningResource_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearningGameAttempt" (
    "id" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "gameKey" TEXT NOT NULL,
    "score" INTEGER NOT NULL,
    "maxScore" INTEGER NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "LearningGameAttempt_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Complaint" (
    "id" TEXT NOT NULL,
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
    "attachmentKey" TEXT,
    "attachmentName" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Complaint_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ComplaintReply" (
    "id" TEXT NOT NULL,
    "complaintId" TEXT NOT NULL,
    "authorId" TEXT NOT NULL,
    "message" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "ComplaintReply_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Assessment" (
    "id" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "enteredById" TEXT,
    "subject" TEXT NOT NULL,
    "score" DOUBLE PRECISION NOT NULL,
    "term" TEXT NOT NULL,
    "strand" TEXT,
    "competency" TEXT,
    "comment" TEXT,
    "reviewComment" TEXT,
    "status" TEXT NOT NULL DEFAULT 'DRAFT',
    "version" INTEGER NOT NULL DEFAULT 1,
    "examSessionId" TEXT,
    "rubricCode" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Assessment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "AssessmentHistory" (
    "id" TEXT NOT NULL,
    "assessmentId" TEXT NOT NULL,
    "actorId" TEXT NOT NULL,
    "version" INTEGER NOT NULL,
    "oldScore" DOUBLE PRECISION,
    "newScore" DOUBLE PRECISION NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "AssessmentHistory_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeeInvoice" (
    "id" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "term" TEXT NOT NULL,
    "dueAt" TIMESTAMP(3),
    "status" TEXT NOT NULL DEFAULT 'OPEN',
    "creditApplied" INTEGER NOT NULL DEFAULT 0,
    "createdById" TEXT NOT NULL,
    "structureId" TEXT,
    "structureVersion" INTEGER,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FeeInvoice_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "InvoiceLine" (
    "id" TEXT NOT NULL,
    "invoiceId" TEXT NOT NULL,
    "category" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "amount" INTEGER NOT NULL,

    CONSTRAINT "InvoiceLine_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeeStructure" (
    "id" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "term" TEXT NOT NULL,
    "version" INTEGER NOT NULL,
    "effectiveFrom" TIMESTAMP(3) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "createdById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FeeStructure_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeeStructureLine" (
    "id" TEXT NOT NULL,
    "structureId" TEXT NOT NULL,
    "category" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "amount" INTEGER NOT NULL,
    "optional" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "FeeStructureLine_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ReceiptCounter" (
    "id" TEXT NOT NULL,
    "lastNumber" INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT "ReceiptCounter_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeePayment" (
    "id" TEXT NOT NULL,
    "invoiceId" TEXT NOT NULL,
    "receiptNo" TEXT NOT NULL,
    "amount" INTEGER NOT NULL,
    "method" TEXT NOT NULL,
    "mpesaCode" TEXT,
    "reference" TEXT,
    "status" TEXT NOT NULL DEFAULT 'RECORDED',
    "confirmedById" TEXT,
    "confirmedAt" TIMESTAMP(3),
    "voidReason" TEXT,
    "version" INTEGER NOT NULL DEFAULT 1,
    "recordedById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "FeePayment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeeCredit" (
    "id" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "sourcePaymentId" TEXT NOT NULL,
    "amount" INTEGER NOT NULL,
    "remaining" INTEGER NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'AVAILABLE',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FeeCredit_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeePaymentHistory" (
    "id" TEXT NOT NULL,
    "paymentId" TEXT NOT NULL,
    "actorId" TEXT NOT NULL,
    "version" INTEGER NOT NULL,
    "amount" INTEGER NOT NULL,
    "method" TEXT NOT NULL,
    "status" TEXT NOT NULL,
    "reason" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FeePaymentHistory_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeeExpense" (
    "id" TEXT NOT NULL,
    "category" TEXT NOT NULL,
    "supplier" TEXT,
    "amount" INTEGER NOT NULL,
    "description" TEXT NOT NULL,
    "paidAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "enteredById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "FeeExpense_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "PayrollEntry" (
    "id" TEXT NOT NULL,
    "staffId" TEXT NOT NULL,
    "period" TEXT NOT NULL,
    "grossAmount" INTEGER NOT NULL,
    "deductions" INTEGER NOT NULL DEFAULT 0,
    "netAmount" INTEGER NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'PENDING',
    "version" INTEGER NOT NULL DEFAULT 1,
    "reference" TEXT,
    "paidAt" TIMESTAMP(3),
    "enteredById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "PayrollEntry_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "PayrollHistory" (
    "id" TEXT NOT NULL,
    "payrollId" TEXT NOT NULL,
    "actorId" TEXT NOT NULL,
    "version" INTEGER NOT NULL,
    "grossAmount" INTEGER NOT NULL,
    "deductions" INTEGER NOT NULL,
    "netAmount" INTEGER NOT NULL,
    "status" TEXT NOT NULL,
    "reason" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "PayrollHistory_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "AuditEvent" (
    "id" TEXT NOT NULL,
    "actorId" TEXT,
    "action" TEXT NOT NULL,
    "entity" TEXT NOT NULL,
    "entityId" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "AuditEvent_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearningVideo" (
    "id" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "videoId" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "strand" TEXT NOT NULL,
    "subStrand" TEXT,
    "source" TEXT NOT NULL DEFAULT 'KICD_EDUTV',
    "status" TEXT NOT NULL DEFAULT 'VERIFIED',
    "addedById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "LearningVideo_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "LearningVideoWatch" (
    "id" TEXT NOT NULL,
    "videoId" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "watchedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "LearningVideoWatch_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "StaffAttendance" (
    "id" TEXT NOT NULL,
    "staffId" TEXT NOT NULL,
    "date" TIMESTAMP(3) NOT NULL,
    "status" TEXT NOT NULL,
    "checkInAt" TIMESTAMP(3),
    "note" TEXT,
    "recordedById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "StaffAttendance_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "DisciplineIncident" (
    "id" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "incidentAt" TIMESTAMP(3) NOT NULL,
    "category" TEXT NOT NULL,
    "severity" TEXT NOT NULL,
    "summary" TEXT NOT NULL,
    "actionTaken" TEXT NOT NULL,
    "parentNotifiedAt" TIMESTAMP(3),
    "followUpAt" TIMESTAMP(3),
    "status" TEXT NOT NULL DEFAULT 'OPEN',
    "recordedById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "DisciplineIncident_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ExamSession" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "term" TEXT NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'DRAFT',
    "startDate" TIMESTAMP(3),
    "endDate" TIMESTAMP(3),
    "marksOpenAt" TIMESTAMP(3),
    "marksCloseAt" TIMESTAMP(3),
    "classesJson" TEXT NOT NULL DEFAULT '[]',
    "subjectsJson" TEXT NOT NULL DEFAULT '[]',
    "timetableJson" TEXT NOT NULL DEFAULT '[]',
    "reopenReason" TEXT,
    "reopenApprovedById" TEXT,
    "createdById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "ExamSession_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "CbcRubric" (
    "id" TEXT NOT NULL,
    "version" INTEGER NOT NULL DEFAULT 1,
    "code" TEXT NOT NULL,
    "label" TEXT NOT NULL,
    "minScore" DOUBLE PRECISION NOT NULL,
    "maxScore" DOUBLE PRECISION NOT NULL,
    "descriptor" TEXT NOT NULL,
    "color" TEXT NOT NULL,
    "learningArea" TEXT,
    "gradeBand" TEXT,
    "effectiveDate" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "createdById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "CbcRubric_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "FeeWaiver" (
    "id" TEXT NOT NULL,
    "invoiceId" TEXT NOT NULL,
    "amount" INTEGER NOT NULL,
    "reason" TEXT NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'PENDING',
    "requestedById" TEXT NOT NULL,
    "approvedById" TEXT,
    "approvedAt" TIMESTAMP(3),
    "decisionNote" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "FeeWaiver_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Textbook" (
    "id" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "publisher" TEXT,
    "curriculumCode" TEXT,
    "totalCopies" INTEGER NOT NULL DEFAULT 1,
    "availableCopies" INTEGER NOT NULL DEFAULT 1,
    "createdById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Textbook_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "TextbookIssue" (
    "id" TEXT NOT NULL,
    "textbookId" TEXT NOT NULL,
    "teacherId" TEXT NOT NULL,
    "copyNumber" TEXT,
    "issuedById" TEXT NOT NULL,
    "issuedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "dueDate" TIMESTAMP(3),
    "status" TEXT NOT NULL DEFAULT 'ISSUED_PENDING_CONFIRMATION',
    "confirmedAt" TIMESTAMP(3),
    "returnedAt" TIMESTAMP(3),
    "remarks" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "TextbookIssue_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "SchemeOfWork" (
    "id" TEXT NOT NULL,
    "teacherId" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "term" TEXT NOT NULL,
    "year" INTEGER NOT NULL DEFAULT 2026,
    "week" INTEGER NOT NULL DEFAULT 1,
    "lessonNo" INTEGER NOT NULL DEFAULT 1,
    "strand" TEXT NOT NULL,
    "subStrand" TEXT NOT NULL,
    "learningOutcomes" TEXT NOT NULL,
    "keyInquiryQuestions" TEXT,
    "learningExperiences" TEXT NOT NULL,
    "learningResources" TEXT NOT NULL,
    "assessmentMethods" TEXT NOT NULL,
    "reflection" TEXT,
    "status" TEXT NOT NULL DEFAULT 'SUBMITTED',
    "deputyFeedback" TEXT,
    "deputyConfirmedAt" TIMESTAMP(3),
    "headteacherFeedback" TEXT,
    "headteacherVerifiedAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "SchemeOfWork_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "SchoolTimetable" (
    "id" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "term" TEXT NOT NULL,
    "year" INTEGER NOT NULL DEFAULT 2026,
    "status" TEXT NOT NULL DEFAULT 'DRAFT',
    "scheduleJson" TEXT NOT NULL,
    "createdById" TEXT NOT NULL,
    "verifiedById" TEXT,
    "verifiedAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "SchoolTimetable_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "WelfareMember" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "totalSavings" INTEGER NOT NULL DEFAULT 0,
    "joinedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "active" BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT "WelfareMember_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "WelfareContribution" (
    "id" TEXT NOT NULL,
    "memberId" TEXT NOT NULL,
    "month" INTEGER NOT NULL,
    "year" INTEGER NOT NULL,
    "amount" INTEGER NOT NULL,
    "category" TEXT NOT NULL DEFAULT 'MONTHLY_SAVINGS',
    "productTitle" TEXT,
    "receiptRef" TEXT,
    "notes" TEXT,
    "recordedById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "WelfareContribution_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "WelfareProduct" (
    "id" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "targetAmount" INTEGER NOT NULL DEFAULT 0,
    "monthlyAmount" INTEGER,
    "status" TEXT NOT NULL DEFAULT 'ACTIVE',
    "createdById" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "WelfareProduct_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "WelfareLoan" (
    "id" TEXT NOT NULL,
    "memberId" TEXT NOT NULL,
    "type" TEXT NOT NULL,
    "principalAmount" INTEGER NOT NULL,
    "repaymentTermMonths" INTEGER NOT NULL DEFAULT 1,
    "monthlyDeduction" INTEGER NOT NULL DEFAULT 0,
    "status" TEXT NOT NULL DEFAULT 'PENDING_HEADTEACHER',
    "repaidAmount" INTEGER NOT NULL DEFAULT 0,
    "purpose" TEXT,
    "headteacherNotes" TEXT,
    "headteacherApprovedAt" TIMESTAMP(3),
    "directorNotes" TEXT,
    "disbursedAt" TIMESTAMP(3),
    "paidAt" TIMESTAMP(3),
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "WelfareLoan_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Notification" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "message" TEXT NOT NULL,
    "type" TEXT NOT NULL DEFAULT 'INFO',
    "link" TEXT,
    "read" BOOLEAN NOT NULL DEFAULT false,
    "actionRequired" BOOLEAN NOT NULL DEFAULT false,
    "actionDone" BOOLEAN NOT NULL DEFAULT false,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "Notification_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "User_email_key" ON "User"("email");

-- CreateIndex
CREATE UNIQUE INDEX "User_phone_key" ON "User"("phone");

-- CreateIndex
CREATE UNIQUE INDEX "User_governmentId_key" ON "User"("governmentId");

-- CreateIndex
CREATE UNIQUE INDEX "Learner_admissionNo_key" ON "Learner"("admissionNo");

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

-- CreateIndex
CREATE UNIQUE INDEX "CbcLevel_sortOrder_key" ON "CbcLevel"("sortOrder");

-- CreateIndex
CREATE INDEX "GuardianLink_learnerId_idx" ON "GuardianLink"("learnerId");

-- CreateIndex
CREATE UNIQUE INDEX "GuardianLink_userId_learnerId_key" ON "GuardianLink"("userId", "learnerId");

-- CreateIndex
CREATE INDEX "Attendance_date_idx" ON "Attendance"("date");

-- CreateIndex
CREATE UNIQUE INDEX "Attendance_learnerId_date_key" ON "Attendance"("learnerId", "date");

-- CreateIndex
CREATE INDEX "TeacherAssignment_teacherId_grade_idx" ON "TeacherAssignment"("teacherId", "grade");

-- CreateIndex
CREATE UNIQUE INDEX "TeacherAssignment_teacherId_grade_subject_key" ON "TeacherAssignment"("teacherId", "grade", "subject");

-- CreateIndex
CREATE UNIQUE INDEX "ClassTeacherAssignment_grade_key" ON "ClassTeacherAssignment"("grade");

-- CreateIndex
CREATE INDEX "ClassTeacherAssignment_teacherId_idx" ON "ClassTeacherAssignment"("teacherId");

-- CreateIndex
CREATE INDEX "TeachingLog_teacherId_createdAt_idx" ON "TeachingLog"("teacherId", "createdAt");

-- CreateIndex
CREATE INDEX "TeachingLog_status_createdAt_idx" ON "TeachingLog"("status", "createdAt");

-- CreateIndex
CREATE INDEX "Assignment_grade_subject_dueAt_idx" ON "Assignment"("grade", "subject", "dueAt");

-- CreateIndex
CREATE INDEX "Assignment_teacherId_createdAt_idx" ON "Assignment"("teacherId", "createdAt");

-- CreateIndex
CREATE INDEX "AssignmentSubmission_learnerId_submittedAt_idx" ON "AssignmentSubmission"("learnerId", "submittedAt");

-- CreateIndex
CREATE INDEX "AssignmentSubmission_assignmentId_status_idx" ON "AssignmentSubmission"("assignmentId", "status");

-- CreateIndex
CREATE UNIQUE INDEX "AssignmentSubmission_assignmentId_learnerId_key" ON "AssignmentSubmission"("assignmentId", "learnerId");

-- CreateIndex
CREATE INDEX "LearningResource_grade_subject_createdAt_idx" ON "LearningResource"("grade", "subject", "createdAt");

-- CreateIndex
CREATE INDEX "LearningResource_teacherId_createdAt_idx" ON "LearningResource"("teacherId", "createdAt");

-- CreateIndex
CREATE INDEX "LearningGameAttempt_learnerId_createdAt_idx" ON "LearningGameAttempt"("learnerId", "createdAt");

-- CreateIndex
CREATE INDEX "LearningGameAttempt_learnerId_gameKey_idx" ON "LearningGameAttempt"("learnerId", "gameKey");

-- CreateIndex
CREATE INDEX "Complaint_openedById_createdAt_idx" ON "Complaint"("openedById", "createdAt");

-- CreateIndex
CREATE INDEX "Complaint_assignedToId_status_idx" ON "Complaint"("assignedToId", "status");

-- CreateIndex
CREATE INDEX "ComplaintReply_complaintId_createdAt_idx" ON "ComplaintReply"("complaintId", "createdAt");

-- CreateIndex
CREATE INDEX "Assessment_learnerId_term_idx" ON "Assessment"("learnerId", "term");

-- CreateIndex
CREATE INDEX "Assessment_enteredById_term_idx" ON "Assessment"("enteredById", "term");

-- CreateIndex
CREATE INDEX "Assessment_status_term_idx" ON "Assessment"("status", "term");

-- CreateIndex
CREATE UNIQUE INDEX "Assessment_learnerId_subject_term_key" ON "Assessment"("learnerId", "subject", "term");

-- CreateIndex
CREATE INDEX "AssessmentHistory_assessmentId_version_idx" ON "AssessmentHistory"("assessmentId", "version");

-- CreateIndex
CREATE UNIQUE INDEX "AssessmentHistory_assessmentId_version_key" ON "AssessmentHistory"("assessmentId", "version");

-- CreateIndex
CREATE INDEX "FeeInvoice_term_status_idx" ON "FeeInvoice"("term", "status");

-- CreateIndex
CREATE UNIQUE INDEX "FeeInvoice_learnerId_term_key" ON "FeeInvoice"("learnerId", "term");

-- CreateIndex
CREATE INDEX "InvoiceLine_invoiceId_idx" ON "InvoiceLine"("invoiceId");

-- CreateIndex
CREATE INDEX "FeeStructure_grade_term_active_idx" ON "FeeStructure"("grade", "term", "active");

-- CreateIndex
CREATE UNIQUE INDEX "FeeStructure_grade_term_version_key" ON "FeeStructure"("grade", "term", "version");

-- CreateIndex
CREATE INDEX "FeeStructureLine_structureId_idx" ON "FeeStructureLine"("structureId");

-- CreateIndex
CREATE UNIQUE INDEX "FeePayment_receiptNo_key" ON "FeePayment"("receiptNo");

-- CreateIndex
CREATE UNIQUE INDEX "FeePayment_mpesaCode_key" ON "FeePayment"("mpesaCode");

-- CreateIndex
CREATE INDEX "FeePayment_invoiceId_status_idx" ON "FeePayment"("invoiceId", "status");

-- CreateIndex
CREATE INDEX "FeePayment_createdAt_idx" ON "FeePayment"("createdAt");

-- CreateIndex
CREATE UNIQUE INDEX "FeeCredit_sourcePaymentId_key" ON "FeeCredit"("sourcePaymentId");

-- CreateIndex
CREATE INDEX "FeeCredit_learnerId_status_idx" ON "FeeCredit"("learnerId", "status");

-- CreateIndex
CREATE INDEX "FeePaymentHistory_paymentId_createdAt_idx" ON "FeePaymentHistory"("paymentId", "createdAt");

-- CreateIndex
CREATE UNIQUE INDEX "FeePaymentHistory_paymentId_version_key" ON "FeePaymentHistory"("paymentId", "version");

-- CreateIndex
CREATE INDEX "FeeExpense_paidAt_category_idx" ON "FeeExpense"("paidAt", "category");

-- CreateIndex
CREATE INDEX "PayrollEntry_period_status_idx" ON "PayrollEntry"("period", "status");

-- CreateIndex
CREATE UNIQUE INDEX "PayrollEntry_staffId_period_key" ON "PayrollEntry"("staffId", "period");

-- CreateIndex
CREATE INDEX "PayrollHistory_payrollId_createdAt_idx" ON "PayrollHistory"("payrollId", "createdAt");

-- CreateIndex
CREATE UNIQUE INDEX "PayrollHistory_payrollId_version_key" ON "PayrollHistory"("payrollId", "version");

-- CreateIndex
CREATE INDEX "AuditEvent_createdAt_idx" ON "AuditEvent"("createdAt");

-- CreateIndex
CREATE UNIQUE INDEX "LearningVideo_videoId_key" ON "LearningVideo"("videoId");

-- CreateIndex
CREATE INDEX "LearningVideo_grade_subject_strand_status_idx" ON "LearningVideo"("grade", "subject", "strand", "status");

-- CreateIndex
CREATE INDEX "LearningVideoWatch_learnerId_watchedAt_idx" ON "LearningVideoWatch"("learnerId", "watchedAt");

-- CreateIndex
CREATE INDEX "LearningVideoWatch_userId_watchedAt_idx" ON "LearningVideoWatch"("userId", "watchedAt");

-- CreateIndex
CREATE UNIQUE INDEX "LearningVideoWatch_videoId_learnerId_userId_key" ON "LearningVideoWatch"("videoId", "learnerId", "userId");

-- CreateIndex
CREATE INDEX "StaffAttendance_date_status_idx" ON "StaffAttendance"("date", "status");

-- CreateIndex
CREATE INDEX "StaffAttendance_recordedById_date_idx" ON "StaffAttendance"("recordedById", "date");

-- CreateIndex
CREATE UNIQUE INDEX "StaffAttendance_staffId_date_key" ON "StaffAttendance"("staffId", "date");

-- CreateIndex
CREATE INDEX "DisciplineIncident_learnerId_incidentAt_idx" ON "DisciplineIncident"("learnerId", "incidentAt");

-- CreateIndex
CREATE INDEX "DisciplineIncident_status_followUpAt_idx" ON "DisciplineIncident"("status", "followUpAt");

-- CreateIndex
CREATE INDEX "ExamSession_term_status_idx" ON "ExamSession"("term", "status");

-- CreateIndex
CREATE INDEX "ExamSession_createdById_idx" ON "ExamSession"("createdById");

-- CreateIndex
CREATE INDEX "CbcRubric_code_version_idx" ON "CbcRubric"("code", "version");

-- CreateIndex
CREATE INDEX "CbcRubric_gradeBand_learningArea_idx" ON "CbcRubric"("gradeBand", "learningArea");

-- CreateIndex
CREATE INDEX "FeeWaiver_invoiceId_status_idx" ON "FeeWaiver"("invoiceId", "status");

-- CreateIndex
CREATE INDEX "FeeWaiver_requestedById_idx" ON "FeeWaiver"("requestedById");

-- CreateIndex
CREATE INDEX "Textbook_grade_subject_idx" ON "Textbook"("grade", "subject");

-- CreateIndex
CREATE INDEX "TextbookIssue_teacherId_status_idx" ON "TextbookIssue"("teacherId", "status");

-- CreateIndex
CREATE INDEX "TextbookIssue_textbookId_status_idx" ON "TextbookIssue"("textbookId", "status");

-- CreateIndex
CREATE INDEX "SchemeOfWork_teacherId_term_idx" ON "SchemeOfWork"("teacherId", "term");

-- CreateIndex
CREATE INDEX "SchemeOfWork_grade_subject_term_idx" ON "SchemeOfWork"("grade", "subject", "term");

-- CreateIndex
CREATE INDEX "SchemeOfWork_status_idx" ON "SchemeOfWork"("status");

-- CreateIndex
CREATE INDEX "SchoolTimetable_term_status_idx" ON "SchoolTimetable"("term", "status");

-- CreateIndex
CREATE UNIQUE INDEX "WelfareMember_userId_key" ON "WelfareMember"("userId");

-- CreateIndex
CREATE INDEX "WelfareMember_active_idx" ON "WelfareMember"("active");

-- CreateIndex
CREATE INDEX "WelfareContribution_memberId_year_month_idx" ON "WelfareContribution"("memberId", "year", "month");

-- CreateIndex
CREATE INDEX "WelfareContribution_category_idx" ON "WelfareContribution"("category");

-- CreateIndex
CREATE INDEX "WelfareLoan_memberId_status_idx" ON "WelfareLoan"("memberId", "status");

-- CreateIndex
CREATE INDEX "WelfareLoan_status_idx" ON "WelfareLoan"("status");

-- CreateIndex
CREATE INDEX "Notification_userId_read_idx" ON "Notification"("userId", "read");

-- CreateIndex
CREATE INDEX "Notification_userId_actionRequired_idx" ON "Notification"("userId", "actionRequired");

-- AddForeignKey
ALTER TABLE "LearnerProfile" ADD CONSTRAINT "LearnerProfile_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearnerGuardian" ADD CONSTRAINT "LearnerGuardian_profileId_fkey" FOREIGN KEY ("profileId") REFERENCES "LearnerProfile"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearnerEmergencyContact" ADD CONSTRAINT "LearnerEmergencyContact_profileId_fkey" FOREIGN KEY ("profileId") REFERENCES "LearnerProfile"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearnerPickupPerson" ADD CONSTRAINT "LearnerPickupPerson_profileId_fkey" FOREIGN KEY ("profileId") REFERENCES "LearnerProfile"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AdmissionDraft" ADD CONSTRAINT "AdmissionDraft_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AdmissionDraft" ADD CONSTRAINT "AdmissionDraft_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AdmissionDocument" ADD CONSTRAINT "AdmissionDocument_draftId_fkey" FOREIGN KEY ("draftId") REFERENCES "AdmissionDraft"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AdmissionDocument" ADD CONSTRAINT "AdmissionDocument_uploadedById_fkey" FOREIGN KEY ("uploadedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "GuardianLink" ADD CONSTRAINT "GuardianLink_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "GuardianLink" ADD CONSTRAINT "GuardianLink_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Attendance" ADD CONSTRAINT "Attendance_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TeacherAssignment" ADD CONSTRAINT "TeacherAssignment_teacherId_fkey" FOREIGN KEY ("teacherId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ClassTeacherAssignment" ADD CONSTRAINT "ClassTeacherAssignment_teacherId_fkey" FOREIGN KEY ("teacherId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TeachingLog" ADD CONSTRAINT "TeachingLog_teacherId_fkey" FOREIGN KEY ("teacherId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Assignment" ADD CONSTRAINT "Assignment_teacherId_fkey" FOREIGN KEY ("teacherId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AssignmentSubmission" ADD CONSTRAINT "AssignmentSubmission_assignmentId_fkey" FOREIGN KEY ("assignmentId") REFERENCES "Assignment"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AssignmentSubmission" ADD CONSTRAINT "AssignmentSubmission_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearningResource" ADD CONSTRAINT "LearningResource_teacherId_fkey" FOREIGN KEY ("teacherId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearningGameAttempt" ADD CONSTRAINT "LearningGameAttempt_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Complaint" ADD CONSTRAINT "Complaint_openedById_fkey" FOREIGN KEY ("openedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Complaint" ADD CONSTRAINT "Complaint_assignedToId_fkey" FOREIGN KEY ("assignedToId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ComplaintReply" ADD CONSTRAINT "ComplaintReply_complaintId_fkey" FOREIGN KEY ("complaintId") REFERENCES "Complaint"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ComplaintReply" ADD CONSTRAINT "ComplaintReply_authorId_fkey" FOREIGN KEY ("authorId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Assessment" ADD CONSTRAINT "Assessment_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Assessment" ADD CONSTRAINT "Assessment_enteredById_fkey" FOREIGN KEY ("enteredById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Assessment" ADD CONSTRAINT "Assessment_examSessionId_fkey" FOREIGN KEY ("examSessionId") REFERENCES "ExamSession"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AssessmentHistory" ADD CONSTRAINT "AssessmentHistory_assessmentId_fkey" FOREIGN KEY ("assessmentId") REFERENCES "Assessment"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AssessmentHistory" ADD CONSTRAINT "AssessmentHistory_actorId_fkey" FOREIGN KEY ("actorId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeInvoice" ADD CONSTRAINT "FeeInvoice_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeInvoice" ADD CONSTRAINT "FeeInvoice_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeInvoice" ADD CONSTRAINT "FeeInvoice_structureId_fkey" FOREIGN KEY ("structureId") REFERENCES "FeeStructure"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "InvoiceLine" ADD CONSTRAINT "InvoiceLine_invoiceId_fkey" FOREIGN KEY ("invoiceId") REFERENCES "FeeInvoice"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeStructure" ADD CONSTRAINT "FeeStructure_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeStructureLine" ADD CONSTRAINT "FeeStructureLine_structureId_fkey" FOREIGN KEY ("structureId") REFERENCES "FeeStructure"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeePayment" ADD CONSTRAINT "FeePayment_invoiceId_fkey" FOREIGN KEY ("invoiceId") REFERENCES "FeeInvoice"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeePayment" ADD CONSTRAINT "FeePayment_recordedById_fkey" FOREIGN KEY ("recordedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeCredit" ADD CONSTRAINT "FeeCredit_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeCredit" ADD CONSTRAINT "FeeCredit_sourcePaymentId_fkey" FOREIGN KEY ("sourcePaymentId") REFERENCES "FeePayment"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeePaymentHistory" ADD CONSTRAINT "FeePaymentHistory_paymentId_fkey" FOREIGN KEY ("paymentId") REFERENCES "FeePayment"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeePaymentHistory" ADD CONSTRAINT "FeePaymentHistory_actorId_fkey" FOREIGN KEY ("actorId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeExpense" ADD CONSTRAINT "FeeExpense_enteredById_fkey" FOREIGN KEY ("enteredById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PayrollEntry" ADD CONSTRAINT "PayrollEntry_staffId_fkey" FOREIGN KEY ("staffId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PayrollEntry" ADD CONSTRAINT "PayrollEntry_enteredById_fkey" FOREIGN KEY ("enteredById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PayrollHistory" ADD CONSTRAINT "PayrollHistory_payrollId_fkey" FOREIGN KEY ("payrollId") REFERENCES "PayrollEntry"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PayrollHistory" ADD CONSTRAINT "PayrollHistory_actorId_fkey" FOREIGN KEY ("actorId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "AuditEvent" ADD CONSTRAINT "AuditEvent_actorId_fkey" FOREIGN KEY ("actorId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearningVideo" ADD CONSTRAINT "LearningVideo_addedById_fkey" FOREIGN KEY ("addedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearningVideoWatch" ADD CONSTRAINT "LearningVideoWatch_videoId_fkey" FOREIGN KEY ("videoId") REFERENCES "LearningVideo"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearningVideoWatch" ADD CONSTRAINT "LearningVideoWatch_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LearningVideoWatch" ADD CONSTRAINT "LearningVideoWatch_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "StaffAttendance" ADD CONSTRAINT "StaffAttendance_staffId_fkey" FOREIGN KEY ("staffId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "StaffAttendance" ADD CONSTRAINT "StaffAttendance_recordedById_fkey" FOREIGN KEY ("recordedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DisciplineIncident" ADD CONSTRAINT "DisciplineIncident_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DisciplineIncident" ADD CONSTRAINT "DisciplineIncident_recordedById_fkey" FOREIGN KEY ("recordedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ExamSession" ADD CONSTRAINT "ExamSession_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "CbcRubric" ADD CONSTRAINT "CbcRubric_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeWaiver" ADD CONSTRAINT "FeeWaiver_invoiceId_fkey" FOREIGN KEY ("invoiceId") REFERENCES "FeeInvoice"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeWaiver" ADD CONSTRAINT "FeeWaiver_requestedById_fkey" FOREIGN KEY ("requestedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "FeeWaiver" ADD CONSTRAINT "FeeWaiver_approvedById_fkey" FOREIGN KEY ("approvedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Textbook" ADD CONSTRAINT "Textbook_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TextbookIssue" ADD CONSTRAINT "TextbookIssue_textbookId_fkey" FOREIGN KEY ("textbookId") REFERENCES "Textbook"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TextbookIssue" ADD CONSTRAINT "TextbookIssue_teacherId_fkey" FOREIGN KEY ("teacherId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TextbookIssue" ADD CONSTRAINT "TextbookIssue_issuedById_fkey" FOREIGN KEY ("issuedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "SchemeOfWork" ADD CONSTRAINT "SchemeOfWork_teacherId_fkey" FOREIGN KEY ("teacherId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "SchoolTimetable" ADD CONSTRAINT "SchoolTimetable_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "SchoolTimetable" ADD CONSTRAINT "SchoolTimetable_verifiedById_fkey" FOREIGN KEY ("verifiedById") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WelfareMember" ADD CONSTRAINT "WelfareMember_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WelfareContribution" ADD CONSTRAINT "WelfareContribution_memberId_fkey" FOREIGN KEY ("memberId") REFERENCES "WelfareMember"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WelfareContribution" ADD CONSTRAINT "WelfareContribution_recordedById_fkey" FOREIGN KEY ("recordedById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WelfareProduct" ADD CONSTRAINT "WelfareProduct_createdById_fkey" FOREIGN KEY ("createdById") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WelfareLoan" ADD CONSTRAINT "WelfareLoan_memberId_fkey" FOREIGN KEY ("memberId") REFERENCES "WelfareMember"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

