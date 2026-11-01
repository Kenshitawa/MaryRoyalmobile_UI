# Mary Royal School Management System (Android)

An Android application built with **Kotlin** and **Jetpack Compose** for Mary Royal School, Obunga, Kisumu, Kenya. The system provides local-first school administration with Role-Based Access Control (RBAC) supporting CBE (Competency-Based Education) workflows and KES/EAT regional context.

## Application Overview

- **Platform:** Android 8.0+ (API Level 26–35)
- **Architecture:** Clean Architecture + MVVM + Room Database + Flow + Coroutines
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Namespace:** `com.example`
- **Application ID:** `com.aistudio.maryroyalsms.kypmvt`
- **Launcher Label:** Mary Royal

## Key Features & Workflows

1. **Role-Based Workflows:**
   - **Director (`DIRECTOR`):** Executive overview of learning, admissions, staff welfare, fee collection, and tamper-proof audit trails.
   - **Head Teacher (`HEAD_TEACHER`):** Academic leadership, admission verification, and CBC assessment approvals.
   - **Deputy Head Teacher (`DEPUTY`):** Schoolwide daily attendance, 8-period timetable grid, and textbook library management.
   - **Secretary (`SECRETARY`):** 7-step guided admission wizard (Learner identity, academic history, residence in Obunga/Kisumu, medical safeguarding, guardian contacts, emergency contacts, review & enroll).
   - **Accountant (`ACCOUNTANT`):** Fee invoices, payment recording (Cash, M-PESA, Bank Deposit), instant receipt generation with QR verification codes, fee balance calculations. Strict privacy: grades and health records are hidden.
   - **Teacher (`TEACHER`):** Competency-based assessments with CBC descriptors (`EE` Exceeding Expectations, `ME` Meeting Expectations, `AE` Approaching Expectations, `BE` Below Expectations), comments, and daily class roll-call.
   - **Parent (`PARENT`):** Family portal with linked learners, released CBE evaluation reports, daily attendance presence %, fee balances, and direct messaging to leadership.
   - **Learner (`LEARNER`):** Student portal with personal records, released competencies, and class timetable.
   - **Welfare Chair (`WELFARE_CHAIR`):** Staff welfare contributions pool, savings balances, and emergency loan application reviews.

2. **CBE (Competency-Based Education) Grading:**
   - Aligned with KICD CBC guidelines:
     - `EE` (Exceeding Expectations, 80–100%)
     - `ME` (Meeting Expectations, 60–79%)
     - `AE` (Approaching Expectations, 40–59%)
     - `BE` (Below Expectations, 0–39%)
   - Tracks learning areas, strands, sub-strands, specific competencies, and teacher observation remarks.

3. **Local-First Data Persistence (Room):**
   - SQLite Room database (`MaryRoyalDatabase`) pre-seeded with realistic school data for Mary Royal School (Obunga, Kisumu).
   - Reactive updates powered by Kotlin `Flow`.
   - Immutable audit trail recording sensitive events (`LOGIN_SUCCEEDED`, `HEALTH_VIEW`, `PROFILE_VIEW`, `PAYMENT_RECORDED`).

## Project Structure

```
├── app/
│   ├── build.gradle.kts                     # App module build configuration
│   └── src/main/
│       ├── AndroidManifest.xml              # Manifest declaring MainActivity, icons, theme
│       ├── java/com/example/maryroyal/
│       │   ├── MainActivity.kt              # App shell, Scaffold, navigation, role switcher
│       │   ├── data/
│       │   │   ├── model/Models.kt          # Room entities & enums
│       │   │   ├── local/
│       │   │   │   ├── MaryRoyalDao.kt      # Room DAO with reactive Flows
│       │   │   │   └── MaryRoyalDatabase.kt # Pre-seeded SQLite database
│       │   │   └── repository/
│       │   │       └── MaryRoyalRepository.kt
│       │   └── ui/
│       │       ├── theme/                   # Royal Blue, Navy & Gold M3 palette
│       │       ├── components/              # BentoCard, StatCard, StatusBadge, HeroBanner
│       │       └── screens/
│       │           ├── DashboardScreen.kt   # Bento metric cards & role switcher
│       │           ├── AdmissionsScreen.kt  # 7-step guided admission wizard
│       │           ├── LearnersScreen.kt    # Learner directory & profile tabs
│       │           ├── AttendanceScreen.kt  # Daily register marking
│       │           ├── AssessmentsScreen.kt # CBE / CBC rubrics & scoring
│       │           ├── FinanceScreen.kt     # Invoices, M-PESA payments & receipts
│       │           ├── TimetableScreen.kt   # 8-period timetable & textbooks
│       │           ├── ReportsAndAuditScreen.kt # Analytics & security audit trail
│       │           └── WelfareAndMessagingScreen.kt # Staff welfare & parent dialog
│       └── res/
│           ├── drawable/                    # Vectors & launcher foreground
│           ├── mipmap-*/                    # Multi-density adaptive & round launcher icons
│           └── values/                      # strings.xml, colors.xml, themes.xml
├── gradle/
│   └── libs.versions.toml                   # Gradle Version Catalog
├── build.gradle.kts                         # Root Gradle build script
├── settings.gradle.kts                      # Root Gradle settings (Project: Mary Royal)
└── metadata.json                            # AI Studio platform identification
```

## Building & Running

In Android Studio:
1. Open the project directory.
2. Allow Gradle to sync dependencies from `libs.versions.toml`.
3. Select an Android device or emulator (API Level 26+) and run the `app` configuration (`Shift + F10`).
