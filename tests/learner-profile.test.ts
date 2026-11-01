import { describe, expect, it } from "vitest";
import { Role } from "@prisma/client";
import { can, canInScope } from "../src/lib/permissions";

describe("R5 Learner Profile Edit Permissions", () => {
  it("grants learner editing strictly to Secretary and Head Teacher", () => {
    expect(can(Role.SECRETARY, "learners", "update")).toBe(true);
    expect(can(Role.HEAD_TEACHER, "learners", "update")).toBe(true);
  });

  it("denies learner editing to all other roles including Director", () => {
    const nonEditingRoles = [
      Role.DIRECTOR,
      Role.DEPUTY,
      Role.ACCOUNTANT,
      Role.TEACHER,
      Role.PARENT,
      Role.LEARNER
    ];

    for (const role of nonEditingRoles) {
      expect(
        can(role, "learners", "update"),
        `Role ${role} must NOT have learner edit permission`
      ).toBe(false);
    }
  });

  it("enforces role-scoped viewing for learner profiles", () => {
    // School-wide view
    expect(canInScope(Role.DIRECTOR, "learners", "view", "school")).toBe(true);
    expect(canInScope(Role.HEAD_TEACHER, "learners", "view", "school")).toBe(true);
    expect(canInScope(Role.DEPUTY, "learners", "view", "school")).toBe(true);
    expect(canInScope(Role.SECRETARY, "learners", "view", "school")).toBe(true);

    // Scoped views
    expect(canInScope(Role.TEACHER, "learners", "view", "assigned")).toBe(true);
    expect(canInScope(Role.PARENT, "learners", "view", "linked")).toBe(true);
    expect(canInScope(Role.LEARNER, "learners", "view", "self")).toBe(true);
    expect(canInScope(Role.ACCOUNTANT, "learners", "view", "learner-identity")).toBe(true);

    // Accountant cannot view full school learner records
    expect(canInScope(Role.ACCOUNTANT, "learners", "view", "school")).toBe(false);
  });
});
