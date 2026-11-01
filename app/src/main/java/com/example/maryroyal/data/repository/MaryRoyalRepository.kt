package com.example.maryroyal.data.repository

import com.example.maryroyal.data.local.MaryRoyalDao
import com.example.maryroyal.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MaryRoyalRepository(private val dao: MaryRoyalDao) {

    // Users & Session
    fun getAllUsers(): Flow<List<User>> = dao.getAllUsers()
    suspend fun getUserById(id: String): User? = dao.getUserById(id)

    // Learners
    fun getAllLearners(): Flow<List<Learner>> = dao.getAllLearners()
    fun getLearnersByGrade(grade: String): Flow<List<Learner>> = dao.getLearnersByGrade(grade)
    suspend fun getLearnerById(id: String): Learner? = dao.getLearnerById(id)
    suspend fun insertLearner(learner: Learner) = dao.insertLearner(learner)
    suspend fun updateLearner(learner: Learner) = dao.updateLearner(learner)

    // Attendance
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> = dao.getAttendanceByDate(date)
    fun getAttendanceForLearner(learnerId: String): Flow<List<AttendanceRecord>> = dao.getAttendanceForLearner(learnerId)
    suspend fun saveAttendance(records: List<AttendanceRecord>) = dao.insertAttendance(records)
    suspend fun updateAttendanceRecord(record: AttendanceRecord) = dao.insertAttendanceRecord(record)

    // Staff Attendance
    fun getStaffAttendanceByDate(date: String): Flow<List<StaffAttendanceRecord>> = dao.getStaffAttendanceByDate(date)
    suspend fun saveStaffAttendance(records: List<StaffAttendanceRecord>) = dao.insertStaffAttendance(records)

    // Assessments
    fun getAllAssessments(): Flow<List<AssessmentRecord>> = dao.getAllAssessments()
    fun getAssessmentsForLearner(learnerId: String): Flow<List<AssessmentRecord>> = dao.getAssessmentsForLearner(learnerId)
    fun getAssessmentsByClass(grade: String, subject: String): Flow<List<AssessmentRecord>> = dao.getAssessmentsByClass(grade, subject)
    suspend fun recordAssessment(assessment: AssessmentRecord) = dao.insertAssessment(assessment)
    suspend fun updateAssessment(assessment: AssessmentRecord) = dao.updateAssessment(assessment)

    // Finance
    fun getAllInvoices(): Flow<List<FeeInvoice>> = dao.getAllInvoices()
    fun getInvoicesForLearner(learnerId: String): Flow<List<FeeInvoice>> = dao.getInvoicesForLearner(learnerId)
    fun getAllPayments(): Flow<List<FeePayment>> = dao.getAllPayments()
    fun getPaymentsForLearner(learnerId: String): Flow<List<FeePayment>> = dao.getPaymentsForLearner(learnerId)
    fun getFeeStructures(): Flow<List<FeeStructure>> = dao.getFeeStructures()

    suspend fun recordPayment(
        invoiceId: String,
        learnerId: String,
        learnerName: String,
        amount: Double,
        method: PaymentMethod,
        mpesaCode: String,
        reference: String,
        actorName: String
    ): FeePayment {
        val count = System.currentTimeMillis() % 10000
        val receiptNo = "REC-2026-${String.format(Locale.US, "%04d", count)}"
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val payment = FeePayment(
            invoiceId = invoiceId,
            learnerId = learnerId,
            learnerName = learnerName,
            receiptNo = receiptNo,
            amount = amount,
            method = method,
            mpesaCode = mpesaCode,
            reference = reference,
            status = "RECORDED",
            date = today
        )
        dao.insertPayment(payment)
        logAudit(actorName, "ACCOUNTANT", "PAYMENT_RECORDED", "FeePayment", payment.id)
        return payment
    }

    suspend fun voidPayment(payment: FeePayment, reason: String, actorName: String) {
        val updated = payment.copy(status = "VOID", voidReason = reason)
        dao.updatePayment(updated)
        logAudit(actorName, "ACCOUNTANT", "PAYMENT_VOIDED", "FeePayment", payment.id)
    }

    // Timetable & Library & Welfare
    fun getTimetableForGrade(grade: String): Flow<List<TimetableEntry>> = dao.getTimetableForGrade(grade)
    fun getAllTextbooks(): Flow<List<Textbook>> = dao.getAllTextbooks()
    fun getAllWelfareContributions(): Flow<List<WelfareContribution>> = dao.getAllWelfareContributions()
    fun getAllWelfareLoans(): Flow<List<WelfareLoan>> = dao.getAllWelfareLoans()
    suspend fun applyWelfareLoan(loan: WelfareLoan) = dao.insertWelfareLoan(loan)

    // Audit Log
    fun getAuditEvents(): Flow<List<AuditEvent>> = dao.getAuditEvents()
    suspend fun logAudit(actorName: String, role: String, action: String, entity: String, entityId: String) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'EAT'", Locale.US).format(Date())
        dao.insertAuditEvent(
            AuditEvent(
                actorName = actorName,
                actorRole = role,
                action = action,
                entity = entity,
                entityId = entityId,
                timestamp = time
            )
        )
    }

    // Complaints
    fun getAllComplaints(): Flow<List<ComplaintMessage>> = dao.getAllComplaints()
    fun getComplaintsForParent(parentId: String): Flow<List<ComplaintMessage>> = dao.getComplaintsForParent(parentId)
    suspend fun sendComplaint(complaint: ComplaintMessage) = dao.insertComplaint(complaint)
    suspend fun respondToComplaint(complaint: ComplaintMessage, responseText: String) {
        val updated = complaint.copy(response = responseText, status = "RESOLVED")
        dao.updateComplaint(updated)
    }
}
