-- Persist normalized parent identity details for matching family accounts during admission.
ALTER TABLE "User" ADD COLUMN "phone" TEXT;
ALTER TABLE "User" ADD COLUMN "governmentId" TEXT;
CREATE UNIQUE INDEX "User_phone_key" ON "User"("phone");
CREATE UNIQUE INDEX "User_governmentId_key" ON "User"("governmentId");
