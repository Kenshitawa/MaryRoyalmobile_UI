package com.example.maryroyal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.maryroyal.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Learner::class,
        AttendanceRecord::class,
        StaffAttendanceRecord::class,
        AssessmentRecord::class,
        FeeInvoice::class,
        FeePayment::class,
        FeeStructure::class,
        TimetableEntry::class,
        Textbook::class,
        WelfareContribution::class,
        WelfareLoan::class,
        AuditEvent::class,
        ComplaintMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MaryRoyalDatabase : RoomDatabase() {
    abstract fun dao(): MaryRoyalDao

    companion object {
        @Volatile
        private var INSTANCE: MaryRoyalDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MaryRoyalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MaryRoyalDatabase::class.java,
                    "mary_royal_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.dao())
                    }
                }
            }

            suspend fun populateInitialData(dao: MaryRoyalDao) {
                // 1. Initial Users
                val users = listOf(
                    User(
                        id = "user_dir",
                        name = "Dr. Mary Otieno",
                        email = "director@maryroyal.sc.ke",
                        role = Role.DIRECTOR,
                        phone = "+254 712 345 678",
                        staffNumber = "MRS-001",
                        employmentTitle = "School Director & Proprietor",
                        qualifications = "PhD Educational Leadership, M.Ed, B.Ed"
                    ),
                    User(
                        id = "user_head",
                        name = "Ezekiel Onyango",
                        email = "headteacher@maryroyal.sc.ke",
                        role = Role.HEAD_TEACHER,
                        phone = "+254 723 456 789",
                        staffNumber = "MRS-002",
                        tscNumber = "TSC/398210",
                        employmentTitle = "Head Teacher",
                        qualifications = "B.Ed Primary Education (Moi Univ)"
                    ),
                    User(
                        id = "user_deputy",
                        name = "Grace Akinyi",
                        email = "deputy@maryroyal.sc.ke",
                        role = Role.DEPUTY,
                        phone = "+254 734 567 890",
                        staffNumber = "MRS-003",
                        tscNumber = "TSC/412098",
                        employmentTitle = "Deputy Head Teacher / Academics",
                        qualifications = "B.Ed Arts (Maseno Univ)"
                    ),
                    User(
                        id = "user_sec",
                        name = "Beatrice Auma",
                        email = "secretary@maryroyal.sc.ke",
                        role = Role.SECRETARY,
                        phone = "+254 745 678 901",
                        staffNumber = "MRS-004",
                        employmentTitle = "School Secretary & Admissions Officer",
                        qualifications = "Diploma in Business Administration"
                    ),
                    User(
                        id = "user_acc",
                        name = "Patrick Omondi",
                        email = "accountant@maryroyal.sc.ke",
                        role = Role.ACCOUNTANT,
                        phone = "+254 756 789 012",
                        staffNumber = "MRS-005",
                        employmentTitle = "Bursar & Finance Officer",
                        qualifications = "CPA-K, B.Com Finance"
                    ),
                    User(
                        id = "user_tr1",
                        name = "Mercy Chebet",
                        email = "m.chebet@maryroyal.sc.ke",
                        role = Role.TEACHER,
                        phone = "+254 767 890 123",
                        staffNumber = "MRS-006",
                        tscNumber = "TSC/521340",
                        employmentTitle = "Class Teacher (Grade 3)",
                        qualifications = "Diploma in Teacher Education (CBE)"
                    ),
                    User(
                        id = "user_tr2",
                        name = "David Wanyama",
                        email = "d.wanyama@maryroyal.sc.ke",
                        role = Role.TEACHER,
                        phone = "+254 778 901 234",
                        staffNumber = "MRS-007",
                        tscNumber = "TSC/498712",
                        employmentTitle = "Senior Teacher (Grade 7 JSS)",
                        qualifications = "B.Ed Science (Mathematics & Integrated Science)"
                    ),
                    User(
                        id = "user_parent",
                        name = "Millicent Adhiambo",
                        email = "millicent.adhiambo@gmail.com",
                        role = Role.PARENT,
                        phone = "+254 711 223 344",
                        employmentTitle = "Guardian of Brian & Michelle",
                        qualifications = "Parent Representative"
                    ),
                    User(
                        id = "user_learner",
                        name = "Brian Ochieng",
                        email = "brian.ochieng@student.maryroyal.sc.ke",
                        role = Role.LEARNER,
                        phone = "+254 700 111 222",
                        employmentTitle = "Learner - Grade 4",
                        qualifications = "Class Monitor"
                    )
                )
                dao.insertUsers(users)

                // 2. Initial Learners (Obunga / Kisumu context)
                val learners = listOf(
                    Learner(
                        id = "lrn_1",
                        admissionNo = "MRS-2024-001",
                        name = "Brian Ochieng",
                        grade = "Grade 4",
                        stream = "Royal",
                        gender = "Male",
                        upiNumber = "KICD-882910-BO",
                        dateOfBirth = "2016-04-14",
                        admissionStatus = "APPROVED",
                        village = "Obunga Central",
                        physicalAddress = "Near Obunga Baptist, Kisumu",
                        knownConditions = "Mild Asthmatic",
                        allergies = "Dust",
                        bloodGroup = "O+",
                        guardianName = "Millicent Adhiambo",
                        guardianPhone = "+254 711 223 344",
                        guardianRelation = "Mother"
                    ),
                    Learner(
                        id = "lrn_2",
                        admissionNo = "MRS-2024-002",
                        name = "Michelle Anyango",
                        grade = "Grade 1",
                        stream = "Royal",
                        gender = "Female",
                        upiNumber = "KICD-910243-MA",
                        dateOfBirth = "2019-08-20",
                        admissionStatus = "APPROVED",
                        village = "Obunga West",
                        physicalAddress = "Off Guba Road, Kisumu",
                        knownConditions = "None",
                        allergies = "Peanuts",
                        bloodGroup = "A+",
                        guardianName = "Millicent Adhiambo",
                        guardianPhone = "+254 711 223 344",
                        guardianRelation = "Mother"
                    ),
                    Learner(
                        id = "lrn_3",
                        admissionNo = "MRS-2025-014",
                        name = "Kevin Kiprono",
                        grade = "Grade 7",
                        stream = "Crown",
                        gender = "Male",
                        upiNumber = "KICD-773412-KK",
                        dateOfBirth = "2013-11-05",
                        admissionStatus = "APPROVED",
                        village = "Kanyakwar",
                        physicalAddress = "Near Kisumu Girls bypass",
                        knownConditions = "None",
                        allergies = "None",
                        bloodGroup = "B+",
                        guardianName = "Paul Kiprono",
                        guardianPhone = "+254 722 998 877",
                        guardianRelation = "Father"
                    ),
                    Learner(
                        id = "lrn_4",
                        admissionNo = "MRS-2025-021",
                        name = "Faith Zawadi",
                        grade = "PP2",
                        stream = "Gold",
                        gender = "Female",
                        upiNumber = "KICD-665544-FZ",
                        dateOfBirth = "2021-02-18",
                        admissionStatus = "APPROVED",
                        village = "Obunga East",
                        physicalAddress = "Sega Lane, Obunga",
                        knownConditions = "None",
                        allergies = "None",
                        bloodGroup = "O+",
                        guardianName = "Christine Zawadi",
                        guardianPhone = "+254 733 445 566",
                        guardianRelation = "Mother"
                    ),
                    Learner(
                        id = "lrn_5",
                        admissionNo = "MRS-2026-030",
                        name = "Trevor Otieno",
                        grade = "Grade 3",
                        stream = "Royal",
                        gender = "Male",
                        upiNumber = "KICD-554433-TO",
                        dateOfBirth = "2017-09-10",
                        admissionStatus = "APPROVED",
                        village = "Obunga Central",
                        physicalAddress = "Opposite Community Hall",
                        knownConditions = "None",
                        allergies = "Penicillin",
                        bloodGroup = "AB+",
                        guardianName = "George Otieno",
                        guardianPhone = "+254 714 887 766",
                        guardianRelation = "Father"
                    )
                )
                dao.insertLearners(learners)

                // 3. Fee Structures
                val feeStructures = listOf(
                    FeeStructure(
                        id = "fs_pp",
                        grade = "PP1 & PP2 (Early Years)",
                        term = "Term 1 2026",
                        tuitionFee = 12000.0,
                        mealFee = 4500.0,
                        activityFee = 1500.0,
                        cbcMaterialsFee = 2000.0,
                        transportFee = 3500.0
                    ),
                    FeeStructure(
                        id = "fs_lower",
                        grade = "Grade 1 - 3 (Lower Primary)",
                        term = "Term 1 2026",
                        tuitionFee = 14000.0,
                        mealFee = 5000.0,
                        activityFee = 2000.0,
                        cbcMaterialsFee = 2500.0,
                        transportFee = 4000.0
                    ),
                    FeeStructure(
                        id = "fs_upper",
                        grade = "Grade 4 - 6 (Upper Primary)",
                        term = "Term 1 2026",
                        tuitionFee = 16000.0,
                        mealFee = 5500.0,
                        activityFee = 2500.0,
                        cbcMaterialsFee = 3000.0,
                        transportFee = 4000.0
                    ),
                    FeeStructure(
                        id = "fs_jss",
                        grade = "Grade 7 - 9 (Junior Secondary)",
                        term = "Term 1 2026",
                        tuitionFee = 20000.0,
                        mealFee = 6000.0,
                        activityFee = 3000.0,
                        cbcMaterialsFee = 4000.0,
                        transportFee = 4500.0
                    )
                )
                dao.insertFeeStructures(feeStructures)

                // 4. Invoices & Payments
                val invoices = listOf(
                    FeeInvoice(
                        id = "inv_1",
                        learnerId = "lrn_1",
                        learnerName = "Brian Ochieng",
                        grade = "Grade 4",
                        term = "Term 1 2026",
                        totalAmount = 25500.0,
                        creditApplied = 0.0,
                        createdAt = "2026-01-08"
                    ),
                    FeeInvoice(
                        id = "inv_2",
                        learnerId = "lrn_2",
                        learnerName = "Michelle Anyango",
                        grade = "Grade 1",
                        term = "Term 1 2026",
                        totalAmount = 23500.0,
                        creditApplied = 0.0,
                        createdAt = "2026-01-08"
                    ),
                    FeeInvoice(
                        id = "inv_3",
                        learnerId = "lrn_3",
                        learnerName = "Kevin Kiprono",
                        grade = "Grade 7",
                        term = "Term 1 2026",
                        totalAmount = 33000.0,
                        creditApplied = 1500.0,
                        createdAt = "2026-01-08"
                    )
                )
                dao.insertInvoices(invoices)

                val payments = listOf(
                    FeePayment(
                        id = "pay_1",
                        invoiceId = "inv_1",
                        learnerId = "lrn_1",
                        learnerName = "Brian Ochieng",
                        receiptNo = "REC-2026-0042",
                        amount = 18000.0,
                        method = PaymentMethod.MPESA,
                        mpesaCode = "TBH829AKX1",
                        reference = "Mrs. Otieno School Fee",
                        status = "RECORDED",
                        date = "2026-01-12"
                    ),
                    FeePayment(
                        id = "pay_2",
                        invoiceId = "inv_2",
                        learnerId = "lrn_2",
                        learnerName = "Michelle Anyango",
                        receiptNo = "REC-2026-0055",
                        amount = 23500.0,
                        method = PaymentMethod.BANK,
                        mpesaCode = "",
                        reference = "KCB Bank Deposit Slip #8912",
                        status = "RECORDED",
                        date = "2026-01-15"
                    ),
                    FeePayment(
                        id = "pay_3",
                        invoiceId = "inv_3",
                        learnerId = "lrn_3",
                        learnerName = "Kevin Kiprono",
                        receiptNo = "REC-2026-0061",
                        amount = 20000.0,
                        method = PaymentMethod.MPESA,
                        mpesaCode = "TCK9910BZZ",
                        reference = "JSS Term 1 Installment",
                        status = "RECORDED",
                        date = "2026-01-18"
                    )
                )
                dao.insertPayments(payments)

                // 5. Assessments (CBE / CBC Grading)
                val assessments = listOf(
                    AssessmentRecord(
                        learnerId = "lrn_1",
                        learnerName = "Brian Ochieng",
                        grade = "Grade 4",
                        subject = "Mathematics",
                        term = "Term 1 2026",
                        strand = "Numbers & Operations",
                        competency = "Multiplication of two-digit numbers",
                        score = 88,
                        descriptor = CbcDescriptor.EE,
                        teacherComment = "Outstanding grasp of long multiplication and mental math.",
                        status = "RELEASED"
                    ),
                    AssessmentRecord(
                        learnerId = "lrn_1",
                        learnerName = "Brian Ochieng",
                        grade = "Grade 4",
                        subject = "Science & Technology",
                        term = "Term 1 2026",
                        strand = "Living Things",
                        competency = "Classification of plants and local flora in Kisumu",
                        score = 76,
                        descriptor = CbcDescriptor.ME,
                        teacherComment = "Consistently accurate observations during nature walk.",
                        status = "RELEASED"
                    ),
                    AssessmentRecord(
                        learnerId = "lrn_1",
                        learnerName = "Brian Ochieng",
                        grade = "Grade 4",
                        subject = "English Language",
                        term = "Term 1 2026",
                        strand = "Reading Comprehension",
                        competency = "Inferring meaning from short story texts",
                        score = 82,
                        descriptor = CbcDescriptor.EE,
                        teacherComment = "Vibrant vocabulary and fluent expressive reading.",
                        status = "RELEASED"
                    ),
                    AssessmentRecord(
                        learnerId = "lrn_2",
                        learnerName = "Michelle Anyango",
                        grade = "Grade 1",
                        subject = "Language Activities",
                        term = "Term 1 2026",
                        strand = "Phonics & Word Recognition",
                        competency = "Letter sounds blending",
                        score = 92,
                        descriptor = CbcDescriptor.EE,
                        teacherComment = "Excellent phonics mastery and word association.",
                        status = "RELEASED"
                    ),
                    AssessmentRecord(
                        learnerId = "lrn_3",
                        learnerName = "Kevin Kiprono",
                        grade = "Grade 7",
                        subject = "Integrated Science",
                        term = "Term 1 2026",
                        strand = "Mixtures & Separation Techniques",
                        competency = "Laboratory filtration experiment",
                        score = 72,
                        descriptor = CbcDescriptor.ME,
                        teacherComment = "Handled apparatus with diligence and recorded measurements neatly.",
                        status = "RELEASED"
                    )
                )
                dao.insertAssessments(assessments)

                // 6. Today's Attendance
                val today = "2026-02-02"
                val attendanceList = listOf(
                    AttendanceRecord(learnerId = "lrn_1", learnerName = "Brian Ochieng", grade = "Grade 4", date = today, status = AttendanceStatus.PRESENT),
                    AttendanceRecord(learnerId = "lrn_2", learnerName = "Michelle Anyango", grade = "Grade 1", date = today, status = AttendanceStatus.PRESENT),
                    AttendanceRecord(learnerId = "lrn_3", learnerName = "Kevin Kiprono", grade = "Grade 7", date = today, status = AttendanceStatus.LATE, note = "Rain delay"),
                    AttendanceRecord(learnerId = "lrn_4", learnerName = "Faith Zawadi", grade = "PP2", date = today, status = AttendanceStatus.PRESENT),
                    AttendanceRecord(learnerId = "lrn_5", learnerName = "Trevor Otieno", grade = "Grade 3", date = today, status = AttendanceStatus.PRESENT)
                )
                dao.insertAttendance(attendanceList)

                // 7. Timetable
                val timetable = listOf(
                    TimetableEntry(grade = "Grade 4", dayOfWeek = "Monday", period = 1, timeRange = "08:00 - 08:45", subject = "Mathematics", teacherName = "Mercy Chebet"),
                    TimetableEntry(grade = "Grade 4", dayOfWeek = "Monday", period = 2, timeRange = "08:45 - 09:30", subject = "English", teacherName = "Mercy Chebet"),
                    TimetableEntry(grade = "Grade 4", dayOfWeek = "Monday", period = 3, timeRange = "09:45 - 10:30", subject = "Kiswahili", teacherName = "Grace Akinyi"),
                    TimetableEntry(grade = "Grade 4", dayOfWeek = "Monday", period = 4, timeRange = "10:30 - 11:15", subject = "Science & Tech", teacherName = "David Wanyama"),
                    TimetableEntry(grade = "Grade 7", dayOfWeek = "Monday", period = 1, timeRange = "08:00 - 08:45", subject = "Integrated Science", teacherName = "David Wanyama"),
                    TimetableEntry(grade = "Grade 7", dayOfWeek = "Monday", period = 2, timeRange = "08:45 - 09:30", subject = "Pre-Technical Studies", teacherName = "David Wanyama")
                )
                dao.insertTimetableEntries(timetable)

                // 8. Textbooks
                val textbooks = listOf(
                    Textbook(title = "Primary Mathematics Grade 4", subject = "Mathematics", grade = "Grade 4", totalCopies = 45, issuedCopies = 38),
                    Textbook(title = "Spotlight Science & Tech Grade 4", subject = "Science", grade = "Grade 4", totalCopies = 40, issuedCopies = 35),
                    Textbook(title = "Tuzo la Kiswahili Gredi 4", subject = "Kiswahili", grade = "Grade 4", totalCopies = 45, issuedCopies = 40),
                    Textbook(title = "JSS Integrated Science Learner's Book 7", subject = "Science", grade = "Grade 7", totalCopies = 35, issuedCopies = 28)
                )
                dao.insertTextbooks(textbooks)

                // 9. Welfare
                val welfareContribs = listOf(
                    WelfareContribution(staffId = "user_tr1", staffName = "Mercy Chebet", amount = 1500.0, month = "January", year = 2026),
                    WelfareContribution(staffId = "user_tr2", staffName = "David Wanyama", amount = 1500.0, month = "January", year = 2026),
                    WelfareContribution(staffId = "user_sec", staffName = "Beatrice Auma", amount = 1500.0, month = "January", year = 2026),
                    WelfareContribution(staffId = "user_acc", staffName = "Patrick Omondi", amount = 1500.0, month = "January", year = 2026)
                )
                dao.insertWelfareContributions(welfareContribs)

                // 10. Audit Log
                val auditEvents = listOf(
                    AuditEvent(
                        actorName = "Dr. Mary Otieno",
                        actorRole = "DIRECTOR",
                        action = "LOGIN_SUCCEEDED",
                        entity = "UserSession",
                        entityId = "user_dir",
                        timestamp = "2026-02-02 07:45:12 EAT"
                    ),
                    AuditEvent(
                        actorName = "Patrick Omondi",
                        actorRole = "ACCOUNTANT",
                        action = "PAYMENT_RECORDED",
                        entity = "FeePayment",
                        entityId = "pay_1",
                        timestamp = "2026-01-12 11:20:44 EAT"
                    ),
                    AuditEvent(
                        actorName = "Ezekiel Onyango",
                        actorRole = "HEAD_TEACHER",
                        action = "ADMISSION_APPROVED",
                        entity = "Learner",
                        entityId = "lrn_5",
                        timestamp = "2026-01-20 14:15:30 EAT"
                    )
                )
                for (event in auditEvents) {
                    dao.insertAuditEvent(event)
                }
            }
        }
    }
}
