CREATE TABLE "LearningVideo" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "videoId" TEXT NOT NULL,
    "grade" TEXT NOT NULL,
    "subject" TEXT NOT NULL,
    "strand" TEXT NOT NULL,
    "subStrand" TEXT,
    "source" TEXT NOT NULL DEFAULT 'KICD_EDUTV',
    "status" TEXT NOT NULL DEFAULT 'VERIFIED',
    "addedById" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "LearningVideo_addedById_fkey" FOREIGN KEY ("addedById") REFERENCES "User" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE UNIQUE INDEX "LearningVideo_videoId_key" ON "LearningVideo"("videoId");
CREATE INDEX "LearningVideo_grade_subject_strand_status_idx" ON "LearningVideo"("grade", "subject", "strand", "status");
CREATE INDEX "LearningVideo_addedById_createdAt_idx" ON "LearningVideo"("addedById", "createdAt");

CREATE TABLE "LearningVideoWatch" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "videoId" TEXT NOT NULL,
    "learnerId" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "watchedAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "LearningVideoWatch_videoId_fkey" FOREIGN KEY ("videoId") REFERENCES "LearningVideo" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "LearningVideoWatch_learnerId_fkey" FOREIGN KEY ("learnerId") REFERENCES "Learner" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "LearningVideoWatch_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE UNIQUE INDEX "LearningVideoWatch_videoId_learnerId_key" ON "LearningVideoWatch"("videoId", "learnerId");
CREATE INDEX "LearningVideoWatch_learnerId_watchedAt_idx" ON "LearningVideoWatch"("learnerId", "watchedAt");
