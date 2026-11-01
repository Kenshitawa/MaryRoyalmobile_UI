package com.example.maryroyal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class Role(val title: String) {
    DIRECTOR("Director"),
    HEAD_TEACHER("Head Teacher"),
    DEPUTY("Deputy Head Teacher"),
    SECRETARY("Secretary"),
    ACCOUNTANT("Accountant"),
    TEACHER("Teacher"),
    PARENT("Parent / Guardian"),
    LEARNER("Learner"),
    WELFARE_CHAIR("Welfare Chair")
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val role: Role,
    val phone: String = "",
    val staffNumber: String = "",
    val tscNumber: String = "",
    val employmentTitle: String = "",
    val qualifications: String = "",
    val isSuspended: Boolean = false
)

@Entity(tableName = "learners")
data class Learner(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val admissionNo: String,
    val name: String,
    val grade: String, // e.g. "PP1", "PP2", "Grade 1" ... "Grade 9"
    val stream: String = "A",
    val gender: String = "Male",
    val upiNumber: String = "",
    val dateOfBirth: String = "2015-05-12",
    val admissionStatus: String = "APPROVED", // DRAFT, SUBMITTED, APPROVED
    val county: String = "Kisumu",
    val subCounty: String = "Kisumu Central",
    val ward: String = "Railways",
    val village: String = "Obunga",
    val physicalAddress: String = "Obunga, Kisumu",
    val knownConditions: String = "None",
    val allergies: String = "None",
    val bloodGroup: String = "O+",
    val specialNeeds: String = "None",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val guardianName: String = "",
    val guardianPhone: String = "",
    val guardianRelation: String = "Mother",
    val isArchived: Boolean = false
)

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE
}

@Entity(tableName = "attendance")
data class AttendanceRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val learnerId: String,
    val learnerName: String,
    val grade: String,
    val date: String, // YYYY-MM-DD
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val note: String = ""
)

@Entity(tableName = "staff_attendance")
data class StaffAttendanceRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val staffName: String,
    val role: Role,
    val date: String,
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val note: String = ""
)

enum class CbcDescriptor(val code: String, val label: String, val minScore: Int) {
    EE("EE", "Exceeding Expectations", 80),
    ME("ME", "Meeting Expectations", 60),
    AE("AE", "Approaching Expectations", 40),
    BE("BE", "Below Expectations", 0)
}

@Entity(tableName = "assessments")
data class AssessmentRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val learnerId: String,
    val learnerName: String,
    val grade: String,
    val subject: String,
    val term: String = "Term 1 2026",
    val strand: String,
    val competency: String,
    val score: Int, // 0 - 100
    val descriptor: CbcDescriptor = when {
        score >= 80 -> CbcDescriptor.EE
        score >= 60 -> CbcDescriptor.ME
        score >= 40 -> CbcDescriptor.AE
        else -> CbcDescriptor.BE
    },
    val teacherComment: String = "",
    val status: String = "RELEASED" // DRAFT, SUBMITTED, APPROVED, RELEASED
)

enum class PaymentMethod(val label: String) {
    CASH("Cash"),
    MPESA("M-PESA"),
    BANK("Bank Deposit")
}

@Entity(tableName = "invoices")
data class FeeInvoice(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val learnerId: String,
    val learnerName: String,
    val grade: String,
    val term: String = "Term 1 2026",
    val totalAmount: Double,
    val creditApplied: Double = 0.0,
    val createdAt: String = "2026-01-10"
)

@Entity(tableName = "payments")
data class FeePayment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val learnerId: String,
    val learnerName: String,
    val receiptNo: String,
    val amount: Double,
    val method: PaymentMethod = PaymentMethod.MPESA,
    val mpesaCode: String = "",
    val reference: String = "",
    val status: String = "RECORDED", // RECORDED, VOID
    val voidReason: String = "",
    val date: String = "2026-01-15"
)

@Entity(tableName = "fee_structures")
data class FeeStructure(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val grade: String,
    val term: String,
    val tuitionFee: Double,
    val mealFee: Double,
    val activityFee: Double,
    val cbcMaterialsFee: Double,
    val transportFee: Double = 0.0
) {
    val total: Double get() = tuitionFee + mealFee + activityFee + cbcMaterialsFee + transportFee
}

@Entity(tableName = "timetable")
data class TimetableEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val grade: String,
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday
    val period: Int, // 1 to 8
    val timeRange: String, // "08:00 - 08:45"
    val subject: String,
    val teacherName: String,
    val room: String = "Room 3"
)

@Entity(tableName = "textbooks")
data class Textbook(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val grade: String,
    val totalCopies: Int,
    val issuedCopies: Int,
    val publisher: String = "KICD / Moran",
    val condition: String = "Good"
) {
    val availableCopies: Int get() = totalCopies - issuedCopies
}

@Entity(tableName = "welfare_contributions")
data class WelfareContribution(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val staffName: String,
    val amount: Double,
    val month: String, // "January"
    val year: Int = 2026,
    val date: String = "2026-01-28"
)

@Entity(tableName = "welfare_loans")
data class WelfareLoan(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val staffId: String,
    val staffName: String,
    val amount: Double,
    val reason: String,
    val status: String = "APPROVED", // PENDING, APPROVED, REJECTED, REPAID
    val date: String = "2026-01-15"
)

@Entity(tableName = "audit_events")
data class AuditEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val actorName: String,
    val actorRole: String,
    val action: String,
    val entity: String,
    val entityId: String,
    val timestamp: String
)

@Entity(tableName = "complaints")
data class ComplaintMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val parentId: String,
    val parentName: String,
    val learnerName: String,
    val subject: String,
    val message: String,
    val response: String = "",
    val status: String = "OPEN", // OPEN, RESOLVED
    val createdAt: String = "2026-02-01"
)
