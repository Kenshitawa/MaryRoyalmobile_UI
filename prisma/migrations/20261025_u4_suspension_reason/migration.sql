-- Distinguish admission-pending access suspension from a Director account suspension.
ALTER TABLE "User" ADD COLUMN "suspensionReason" TEXT;
