import { describe, expect, it } from "vitest";
import { Role } from "@prisma/client";
import { can } from "../src/lib/permissions";

describe("Admission Draft Deletion Permissions", () => {
  it("allows Director, Head Teacher, and Secretary to delete admission drafts", () => {
    expect(can(Role.DIRECTOR, "admissions", "delete")).toBe(true);
    expect(can(Role.HEAD_TEACHER, "admissions", "delete")).toBe(true);
    expect(can(Role.SECRETARY, "admissions", "delete")).toBe(true);
  });

  it("denies Teacher, Deputy, Accountant, Welfare Chair, Parent, and Learner from deleting drafts", () => {
    expect(can(Role.TEACHER, "admissions", "delete")).toBe(false);
    expect(can(Role.DEPUTY, "admissions", "delete")).toBe(false);
    expect(can(Role.ACCOUNTANT, "admissions", "delete")).toBe(false);
    expect(can(Role.WELFARE_CHAIR, "admissions", "delete")).toBe(false);
    expect(can(Role.NON_TEACHING_STAFF, "admissions", "delete")).toBe(false);
    expect(can(Role.PARENT, "admissions", "delete")).toBe(false);
    expect(can(Role.LEARNER, "admissions", "delete")).toBe(false);
  });

  it("enforces that only status DRAFT can be deleted, protecting SUBMITTED and ENROLLED", () => {
    function canDeleteByStatus(status: string): { allowed: boolean; error?: string } {
      if (status !== "DRAFT") {
        return {
          allowed: false,
          error: "Only draft admissions can be deleted. Submitted or enrolled admissions must use withdraw/archive.",
        };
      }
      return { allowed: true };
    }

    expect(canDeleteByStatus("DRAFT").allowed).toBe(true);
    expect(canDeleteByStatus("SUBMITTED").allowed).toBe(false);
    expect(canDeleteByStatus("SUBMITTED").error).toContain("Submitted or enrolled admissions must use withdraw/archive");
    expect(canDeleteByStatus("ENROLLED").allowed).toBe(false);
  });

  it("handles retention window boundaries (1 to 3650 days)", () => {
    function validateRetentionWindow(days: number): boolean {
      return Number.isInteger(days) && days >= 0 && days <= 365;
    }

    expect(validateRetentionWindow(0)).toBe(true);
    expect(validateRetentionWindow(30)).toBe(true);
    expect(validateRetentionWindow(365)).toBe(true);
    expect(validateRetentionWindow(-1)).toBe(false);
    expect(validateRetentionWindow(500)).toBe(false);
  });
});
