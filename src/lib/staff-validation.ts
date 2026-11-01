import { z } from "zod";
import { Role } from "@prisma/client";

export const kenyanPhoneRegex = /^(?:\+254|0)[17]\d{8}$/;

export const staffPersonalSchema = z.object({
  name: z.string().trim().min(2, "Name must be at least 2 characters").max(120),
  gender: z.enum(["Male", "Female", "Other"]).optional(),
  dateOfBirth: z.string().optional(),
  nationalId: z.string().trim().max(30).optional(),
  nationality: z.string().trim().default("Kenyan"),
  maritalStatus: z.enum(["Single", "Married", "Divorced", "Widowed", "Other"]).optional(),
  phone: z.string().trim().regex(kenyanPhoneRegex, "Enter a valid Kenyan phone number (+254 or 07/01...)").optional().or(z.literal("")),
  alternatePhone: z.string().trim().optional(),
  email: z.string().trim().email("Enter a valid email address").max(254).transform((v) => v.toLowerCase()),
  residenceAddress: z.string().trim().max(250).optional(),
  county: z.string().trim().max(60).optional(),
  avatarUrl: z.string().optional(),
});

export const staffEmploymentSchema = z.object({
  role: z.nativeEnum(Role),
  staffNumber: z.string().trim().max(40).optional(),
  tscNumber: z.string().trim().max(40).optional(),
  dateJoined: z.string().optional(),
  employmentType: z.enum(["Permanent", "Contract", "BOM", "Part-Time", "Probation"]).optional(),
  employmentTitle: z.string().trim().max(100).optional(),
  department: z.string().trim().max(100).optional(),
  lineManager: z.string().trim().max(100).optional(),
  probationEndDate: z.string().optional(),
  assignedSubjects: z.array(z.object({ grade: z.string(), subject: z.string() })).optional(),
  classTeacherGrade: z.string().optional(),
});

export const staffQualificationsSchema = z.object({
  educationHistory: z.string().trim().max(2000).optional(),
  professionalQualifications: z.string().trim().max(1000).optional(),
  certificates: z.string().trim().max(1000).optional(),
  specializations: z.string().trim().max(500).optional(),
  yearsOfExperience: z.coerce.number().min(0).max(60).optional(),
  previousEmployers: z.string().trim().max(1000).optional(),
});

export const staffEmergencyContactSchema = z.object({
  fullName: z.string().trim().min(2, "Name required").max(100),
  relationship: z.string().trim().min(2, "Relationship required").max(60),
  phone: z.string().trim().regex(kenyanPhoneRegex, "Valid Kenyan phone number required"),
  isNextOfKin: z.boolean().default(false),
});

export const staffRefereeSchema = z.object({
  fullName: z.string().trim().min(2, "Name required").max(100),
  organization: z.string().trim().min(2, "Organization required").max(100),
  title: z.string().trim().max(100).optional(),
  phone: z.string().trim().regex(kenyanPhoneRegex, "Valid Kenyan phone number required"),
  email: z.string().trim().email("Valid email required").optional().or(z.literal("")),
});

export const staffStatutorySchema = z.object({
  kraPin: z.string().trim().max(20).optional().or(z.literal("")),
  nssfNumber: z.string().trim().max(30).optional().or(z.literal("")),
  shaNumber: z.string().trim().max(30).optional().or(z.literal("")),
  bankName: z.string().trim().max(100).optional().or(z.literal("")),
  bankAccountName: z.string().trim().max(120).optional().or(z.literal("")),
  bankAccountNumber: z.string().trim().max(50).optional().or(z.literal("")),
  bankBranch: z.string().trim().max(100).optional().or(z.literal("")),
});

export const staffCreateWizardSchema = z.object({
  personal: staffPersonalSchema,
  employment: staffEmploymentSchema,
  qualifications: staffQualificationsSchema.optional(),
  emergencyContacts: z.array(staffEmergencyContactSchema).min(1, "At least one emergency contact is required"),
  referees: z.array(staffRefereeSchema).max(5).optional(),
  statutory: staffStatutorySchema.optional(),
  documentKeys: z.array(z.object({
    category: z.enum(["ID_COPY", "TSC_CARD", "ACADEMIC_CERTIFICATE", "CV", "CONTRACT", "PASSPORT_PHOTO"]),
    storageKey: z.string(),
    originalName: z.string(),
    contentType: z.string(),
    sizeBytes: z.number(),
  })).optional(),
});
