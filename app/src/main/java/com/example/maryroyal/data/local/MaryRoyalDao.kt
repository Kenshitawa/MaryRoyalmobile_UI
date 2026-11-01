package com.example.maryroyal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.maryroyal.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MaryRoyalDao {
    // Users
    @Query("SELECT * FROM users WHERE isSuspended = 0 ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Learners
    @Query("SELECT * FROM learners WHERE isArchived = 0 ORDER BY grade ASC, name ASC")
    fun getAllLearners(): Flow<List<Learner>>

    @Query("SELECT * FROM learners WHERE id = :id LIMIT 1")
    suspend fun getLearnerById(id: String): Learner?

    @Query("SELECT * FROM learners WHERE grade = :grade AND isArchived = 0 ORDER BY name ASC")
    fun getLearnersByGrade(grade: String): Flow<List<Learner>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearners(learners: List<Learner>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearner(learner: Learner)

    @Update
    suspend fun updateLearner(learner: Learner)

    // Attendance
    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY grade ASC, learnerName ASC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE learnerId = :learnerId ORDER BY date DESC")
    fun getAttendanceForLearner(learnerId: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(records: List<AttendanceRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecord)

    // Staff Attendance
    @Query("SELECT * FROM staff_attendance WHERE date = :date ORDER BY staffName ASC")
    fun getStaffAttendanceByDate(date: String): Flow<List<StaffAttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffAttendance(records: List<StaffAttendanceRecord>)

    // Assessments
    @Query("SELECT * FROM assessments ORDER BY term DESC, subject ASC, score DESC")
    fun getAllAssessments(): Flow<List<AssessmentRecord>>

    @Query("SELECT * FROM assessments WHERE learnerId = :learnerId ORDER BY term DESC, subject ASC")
    fun getAssessmentsForLearner(learnerId: String): Flow<List<AssessmentRecord>>

    @Query("SELECT * FROM assessments WHERE grade = :grade AND subject = :subject ORDER BY learnerName ASC")
    fun getAssessmentsByClass(grade: String, subject: String): Flow<List<AssessmentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessments(assessments: List<AssessmentRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentRecord)

    @Update
    suspend fun updateAssessment(assessment: AssessmentRecord)

    // Finance - Invoices
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): Flow<List<FeeInvoice>>

    @Query("SELECT * FROM invoices WHERE learnerId = :learnerId ORDER BY createdAt DESC")
    fun getInvoicesForLearner(learnerId: String): Flow<List<FeeInvoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<FeeInvoice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: FeeInvoice)

    // Finance - Payments
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAllPayments(): Flow<List<FeePayment>>

    @Query("SELECT * FROM payments WHERE learnerId = :learnerId ORDER BY date DESC")
    fun getPaymentsForLearner(learnerId: String): Flow<List<FeePayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<FeePayment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: FeePayment)

    @Update
    suspend fun updatePayment(payment: FeePayment)

    // Fee Structures
    @Query("SELECT * FROM fee_structures ORDER BY grade ASC")
    fun getFeeStructures(): Flow<List<FeeStructure>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeStructures(structures: List<FeeStructure>)

    // Timetable
    @Query("SELECT * FROM timetable WHERE grade = :grade ORDER BY dayOfWeek ASC, period ASC")
    fun getTimetableForGrade(grade: String): Flow<List<TimetableEntry>>

    @Query("SELECT * FROM timetable ORDER BY dayOfWeek ASC, period ASC")
    fun getAllTimetableEntries(): Flow<List<TimetableEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableEntries(entries: List<TimetableEntry>)

    // Textbooks
    @Query("SELECT * FROM textbooks ORDER BY grade ASC, title ASC")
    fun getAllTextbooks(): Flow<List<Textbook>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextbooks(textbooks: List<Textbook>)

    @Update
    suspend fun updateTextbook(textbook: Textbook)

    // Welfare
    @Query("SELECT * FROM welfare_contributions ORDER BY date DESC")
    fun getAllWelfareContributions(): Flow<List<WelfareContribution>>

    @Query("SELECT * FROM welfare_loans ORDER BY date DESC")
    fun getAllWelfareLoans(): Flow<List<WelfareLoan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWelfareContributions(contributions: List<WelfareContribution>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWelfareLoan(loan: WelfareLoan)

    // Audit
    @Query("SELECT * FROM audit_events ORDER BY timestamp DESC LIMIT 200")
    fun getAuditEvents(): Flow<List<AuditEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditEvent(event: AuditEvent)

    // Complaints
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintMessage>>

    @Query("SELECT * FROM complaints WHERE parentId = :parentId ORDER BY createdAt DESC")
    fun getComplaintsForParent(parentId: String): Flow<List<ComplaintMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintMessage)

    @Update
    suspend fun updateComplaint(complaint: ComplaintMessage)
}
