# MARY ROYAL SCHOOL MANAGEMENT SYSTEM: UPGRADE ROUND 2

The system already exists in this repo. Read `AGENTS.md`, `docs/PROGRESS.md` and `docs/DECISIONS.md` first, then apply every change below. Keep all existing strict role rules (Section 3 of the original brief): permissions in `lib/permissions.ts`, server-side checks on every action, audit logging, row-level scoping. Do NOT weaken security to add features.

## 0. OPERATING RULES (fully autonomous)
- Complete ALL upgrade phases (U1 to U9) in ONE continuous run. Do not stop, pause or ask questions between phases. Log ambiguous choices in `docs/DECISIONS.md` and continue.
- After EVERY phase: run the app locally, fix all errors, run lint, type-check, unit and e2e tests, update the permission test suite for any new role or page, append to `docs/PROGRESS.md`, and `git commit`.
- No placeholders, fake buttons or "coming soon". Everything must work with real data.
- Keep running locally on localhost. No deployment work.
- Write migrations for every schema change. Do not break existing data structures unnecessarily.

## U1. NEW ROLE: SECRETARY, plus role adjustments
Add role SECRETARY (admissions and records desk).
- Secretary can: run the full admission process, create learner accounts and parent/guardian accounts as part of admission (see U4), edit learner records, upload documents, print/download the admission form, view the fee structure (read-only), view a learner's fee paid and balance (read-only), view class lists, search learners.
- Secretary cannot: record or void payments, see marks/discipline/medical notes beyond what admission needs, create staff accounts, change roles, or edit the fee structure.
- Director still has full control over all accounts and can edit or suspend any account the secretary created.
- Learner accounts are NO LONGER created automatically. A learner gets a login only when the secretary (or Director) explicitly creates it from the learner's profile or at the end of admission via a "Create learner login" option. Same for parent accounts: existing parent is linked rather than duplicated (match by phone/ID number).
- Add Secretary to the permission matrix, the audit log, the seed data (demo user), the navigation, the dashboard, and the permission test suite.

## U2. RESET: START FRESH
- Remove ALL demo and seed data (learners, parents, teachers, classes, marks, attendance, invoices, payments, announcements, discipline, messages).
- Keep ONLY: the Director account (forced password change preserved), the empty school settings structure, the grading/CBC level defaults, and system configuration.
- Add `npm run reset:fresh` (wipes everything except the Director and defaults, with a confirmation prompt) and `npm run seed:demo` (optional demo data, off by default).
- `npm run setup` must now produce a clean, empty system that I can walk through the full real process: Director sets up school (year, terms, classes, subjects, rubrics, fee structure) then creates staff, then Secretary admits learners, then Deputy sets exams, then teachers take attendance and enter marks, then Accountant collects fees.
- Make sure the empty states guide the user through that exact order, e.g. a "Getting started" checklist on the Director dashboard showing completed and pending setup steps.

## U3. NAVIGATION AND BACK BUTTON FIX (global)
Problem: users get stuck or lost when going back from dashboards and classroom/detail pages. Fix it system-wide:
- Add a consistent breadcrumb trail on every page below a dashboard (e.g. Dashboard > Classes > Grade 4 East > Learner).
- Add a smart Back button in the page header: goes to the previous in-app page if one exists, otherwise to a sensible parent route (never leaves the app, never loops, never lands on a login page).
- On mobile, show a top app bar with a back arrow on detail pages and keep the bottom navigation visible on top-level pages.
- The sidebar highlights the current section; clicking the logo or "Home" always returns to the user's role dashboard.
- Preserve list state when going back (filters, search, pagination, scroll position).
- Warn before leaving a form with unsaved changes.
- Handle browser back/forward correctly and never break after login/logout redirects.
- Test this across every role and every nested page.

## U4. ADMISSION: FULL UPGRADE (Secretary workflow)
Redesign admission as a polished multi-step wizard with progress bar, autosave of drafts, validation per step, and a final review screen.

Steps and fields (all validated):
1. Learner details: full names, date of birth, gender, nationality, UPI/NEMIS number, admission number (auto, unique, sequential), class and stream applied for, admission date, previous school and last class, reason for leaving, religion (optional), languages spoken.
2. Residence: county, sub-county, ward/estate, village/area, physical address and landmarks, distance and means of transport to school, optional map pin or coordinates.
3. Parent/guardian information: father, mother and/or guardian (at least one required): full names, relationship, ID/passport number, phone numbers (+254 format, with WhatsApp flag), email, occupation, employer, home and work address, who pays fees, and a primary contact flag. Emergency contacts (at least two) with relationship and phone. Authorized pick-up persons.
4. Health: known illnesses and conditions, allergies, current medication, disabilities or special needs, blood group, doctor/hospital preference, immunization status, health insurance (e.g. SHA). Medical data is restricted: visible only to Director, Head Teacher, Deputy, Secretary and the learner's class teacher, with an access log.
5. Documents (upload with preview, type and size validation, secure storage, replace/remove): passport photo of learner (with crop tool), birth certificate (image/PDF), previous school report/leaving certificate, immunization card, parent ID copy, and optional extra documents. Show a checklist of which required documents are still missing.
6. Fees at admission: show the fee structure for the chosen class (read from the structure the Head Teacher/Director created), allow selecting optional items (transport, lunch, boarding), generate the first invoice preview, and record any admission payment hand-off to the Accountant (secretary does NOT record payments).
7. Review and submit: summary of everything with edit links per step, a declaration/consent checkbox (Kenya Data Protection Act consent for processing the child's data), and submit.

After successful submission:
- Show a success screen with the admission number and options: Download admission form (PDF), Print, Create learner login, Create/link parent login, Start another admission, Open learner profile.
- The downloadable PDF admission form must be professionally designed with school logo and branding, all learner, residence, parent, health and fee information, the learner's passport photo embedded, document checklist, signature lines (parent/guardian, secretary, head teacher) and date, so it can be printed and filed physically. Also allow re-downloading it any time from the learner profile.
- Save a draft if the secretary leaves midway; list drafts on the admissions page. Admissions list shows status (Draft, Submitted, Enrolled) with search and filters.
- Audit-log every admission action.

## U5. LEARNER PROFILE (biography) AND STAFF PROFILE
Learner profile page (opens when an authorized user clicks any learner anywhere in the system):
- Hero header with passport photo, name, admission number, class/stream, status, age, quick actions (Download admission form, Message parent, Edit, Create login).
- Tabs: Overview/Biography, Parents and guardians, Residence, Health (restricted), Documents (view/download), Attendance (summary and calendar), Academics (results by term, CBC levels, trend charts), Fees (invoiced, paid, balance, payment history, receipts, status badge), Discipline, Timeline/activity history.
- Visibility by role (enforced server-side, configurable by Director in settings):
  - Director, Head Teacher, Deputy, Secretary: full profile.
  - Accountant: biography basics, parents' contacts, and Fees tab only.
  - Teacher: only learners in their assigned classes; biography, parent contacts, attendance, academics for their subjects, discipline summary; NO fee amounts by default (Director can enable a setting "Teachers see fee status", showing only paid/not paid when enabled).
  - Parent and Learner: only their own/their child's view (limited tabs, no health notes unless the Director enables it).
- Fee balance and paid amounts shown to roles allowed above only.

Staff profile page: when the Head Teacher or Director taps a teacher (or any staff account) show: photo, contact, role(s), TSC/staff number, qualifications, subjects and classes assigned, timetable, attendance/punctuality record, lesson plans submitted, marks submission status, messages count, account status and last login, plus actions (reset password, suspend, edit) for the Director only. Teachers can see only their own profile.

## U6. DEPUTY AND HEAD TEACHER: ATTENDANCE DASHBOARD, EXAMS, RUBRICS
Attendance dashboard (Deputy and Head Teacher, own page "Attendance"):
- School-wide overview for today and any date range: overall attendance rate, present/absent/late counts, by class and stream (heat map or table), trend charts by week/term, gender split.
- See every class's attendance in one place with drill-down to a class and then to a learner; show which classes have NOT yet had attendance taken today, with the teacher responsible and a reminder button.
- Absentee follow-up list with repeat absentees highlighted, ability to log a follow-up call/note and notify the parent.
- Teacher attendance overview too (staff present/absent/late).
- Filter by date, class, stream, term; export PDF/Excel.

Exams management (Deputy; Head Teacher can view and approve):
- Deputy creates exam sessions per term (e.g. Opening Exam, Mid-Term, End-Term) with name, term, year, classes, subjects, start/end dates, marks entry open/close dates, and status (Draft, Active, Closed, Published).
- Teachers see and can enter marks ONLY for exam sessions currently Active for their assigned classes and subjects. When the Deputy sets End-Term as active, only End-Term appears in the teachers' marks entry. Closed or inactive exams are read-only or hidden.
- Deputy can set exam timetable, invigilators and rooms, and see marks entry progress per class and subject (which teachers have completed, which are pending) with reminders.
- Locking, reopening (with reason and approval) and audit logging stay in place.

Rubrics (Deputy sets, global for all classes and teachers):
- A central Rubrics page where the Deputy defines the CBC performance level scale used across the school: levels (EE, ME, AE, BE and sub-levels), score ranges, descriptors, and colors; and per learning area/subject or grade band if needed.
- Teachers do not define their own rubrics; they use the Deputy's rubric when entering marks, and levels are computed automatically from scores.
- Changing a rubric after marks exist requires a confirmation showing the impact and is audit-logged; published report cards keep the rubric version they were issued with (versioned rubrics).

## U7. COMPLAINTS, REMOVE FROM MOST ROLES
- Complaints exist ONLY as a one-way channel from Parent to Head Teacher and Director.
- Remove complaint pages, menus, forms, API routes and permissions for all other roles (Teacher, Learner, Accountant, Deputy, Secretary).
- Parent can submit a complaint (category, description, optional attachment) directed to Head Teacher, Director or both, and track its status (Received, In review, Resolved) and replies.
- Head Teacher and Director see an inbox with filters, assign status, reply, and resolve. Replies notify the parent.
- Update the permission matrix, navigation and test suite to prove no other role can see or call complaint endpoints.

## U8. LEARNER VIDEOS TAILORED TO WEAK TOPICS (KICD-based)
- On the Learner dashboard, add a "My Learning" section that identifies each learner's weak areas automatically from their assessment results (lowest-performing learning areas, strands and sub-strands, using CBC levels BE/AE and score trends), and shows recommended revision videos for those topics.
- Content source: Kenya Institute of Curriculum Development (KICD) digital content, which is published on YouTube by KICD. Build it so a video is only ever shown if it is a real, verified video. Do NOT invent video IDs or links.
- Implementation:
  - A `LearningVideo` table: title, YouTube video ID, grade level, learning area, strand/sub-strand, source (KICD channel), added by, status.
  - Primary method: optional YouTube Data API integration (key stored in environment variable, off by default) that searches the official KICD channel by grade, learning area and strand, with results restricted to the KICD channel, and the result saved to the table after validation.
  - Always-works fallback: a curation screen where the Head Teacher/Deputy/Teachers paste a YouTube link, tag it to grade, learning area and strand, and it is validated and added to the library. Include a "Suggested search on KICD channel" link to help teachers find videos manually.
  - The learner sees videos matched to their grade and weak strands. If no videos exist for a topic, show a clear message ("Your teacher hasn't added videos for this topic yet") rather than irrelevant content.
- Playback: embed with youtube-nocookie, no autoplay, no related-video distractions where possible, inside a clean modern player card; track "watched" status and show learner progress; optional short self-check question set per video as a stretch goal.
- Safety: the learner cannot browse arbitrary YouTube; only library videos appear. Teachers can see which learners watched what.

## U9. FINANCE: FEE STRUCTURE AND FULL UI UPGRADE
Fee structure ownership change:
- Head Teacher and Director create and manage the fee structure: per class and term, line items (tuition, transport, lunch, boarding, uniform, activity, exam, admission, etc.), optional vs. compulsory, amounts, and effective dates. Changes are versioned and audit-logged; invoices already issued keep their original amounts.
- The structure automatically appears (read-only) for the Secretary and Accountant, and is used at admission and invoice generation. The Accountant manages invoices, payments and discounts requests; waiver/discount approval stays with the Director.
- Fee structure page: clean table/cards by class and term, duplicate structure to next term, total per class, and a preview of what a parent will be billed.

Finance UI upgrade (Accountant, Director, Head Teacher read views, Secretary read-only):
- Redesign the whole finance module in the blue and white system with a clear status color language: Paid (green), Partially paid (amber), Unpaid (blue/neutral), Overdue (red), Pending approval (purple/amber), Voided (gray). Use soft badge pills, progress bars for fee collection, and consistent icons.
- Finance dashboard: collected today/this term, outstanding, collection rate vs. target, collections by payment method, by class, 12-week trend, top arrears, recent payments feed.
- Invoices: modern list with filters, status badges, totals bar; a beautiful create-invoice flow (stepper): choose learner (search by name/admission number with photo), choose term, auto-load fee structure items with optional items toggles, apply approved discounts, see live totals and balance carry-forward, preview the invoice, confirm.
- Payments: clean record-payment drawer/modal: learner search, outstanding balance display, amount, method (Cash, M-Pesa with transaction code and duplicate-code check, Bank, Cheque), reference, date, auto-allocation to invoices, preview, and confirm.
- Confirmation and approval flows: after recording a payment or approving a waiver/discount/void, show a clear confirmation dialog beforehand and an animated success state afterwards (check animation, receipt number, buttons: Download receipt, Print, Record another, View learner). Approval actions show who approved, when, and why. Send in-app notification to the parent on payment confirmation.
- Receipts and invoices as designed PDFs (logo, colors, sequential numbers, balance, QR code with receipt number for verification).
- Statements and arrears pages with colored aging buckets (0-30, 31-60, 61-90, 90+), bulk reminder action, export.

## U10. WHOLE-SYSTEM UI MODERNIZATION (page by page)
Go through EVERY page in the app, for EVERY role, one by one, and upgrade the interface, information and flow. Do not skip any page.
1. First generate `docs/UI_UPGRADE_CHECKLIST.md` listing every page and component, grouped by role, with columns: current problems, planned improvements, status.
2. Upgrade each page in turn and tick it off. Final output must have zero unchecked pages.

Standards for every page:
- Consistent modern blue and white design system: refined spacing, typography scale, color tokens, elevated cards, glass/soft-shadow headers, rounded corners, subtle micro-animations and transitions, skeleton loaders, polished empty states with illustrations/icons and a clear next action.
- Rich dashboards for every role with relevant KPIs, charts, quick actions, shortcuts, and "needs attention" panels.
- Better information: add useful context on every page (totals, status badges, last updated, who did what), avatars/photos for people, meaningful icons, and helpful tooltips.
- Tables: sticky header, column sorting, filters, search, pagination, bulk actions, row hover actions, responsive card layout on mobile.
- Forms: grouped sections, inline validation, helpful placeholders, sensible defaults, autosave where relevant, keyboard-friendly, large touch targets on mobile.
- Flow: clear primary action per page, fewer clicks, consistent patterns (drawers for quick actions, full pages for complex ones), command palette (Ctrl/Cmd+K) global search for learners, staff, invoices and pages.
- Responsive and fast: excellent on phones, tablets and desktops; optimize images; lazy load heavy components.
- Accessibility: AA contrast, focus rings, keyboard navigation, aria labels, reduced-motion support.
- Dark mode supported on every page.
- Print styles for lists, forms, receipts and reports.

## 11. TESTING AND FINISH
- Update unit and e2e tests: Secretary admission flow end to end (admit, upload documents, download form, create learner login, parent link), deputy sets End-Term exam and teacher sees only that exam, rubric applied to marks, fee structure created by Head Teacher visible to Secretary and Accountant, complaint flow Parent to Head Teacher/Director only, learner sees videos for weak topics only from the library, back-button navigation across roles, fresh-start reset leaves only the Director.
- Extend the permission test suite: every role against every route/action including the new Secretary role, learner profile visibility rules, finance pages, complaints, rubrics and exams. All must pass.
- Update README, `docs/guides/` (add a Secretary guide and update every role guide), and `AGENTS.md`.

START NOW. Do not stop until U1 to U10 are complete, tested and committed. Then reply with ONE final message containing: (1) how to run the fresh system locally, (2) how to log in as the Director and the order to follow for the full walkthrough from setup to admission to first class, (3) a list of everything changed per role, (4) test results, (5) anything left in `docs/ROADMAP.md`.