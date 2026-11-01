# MARY ROYAL SCHOOL MANAGEMENT SYSTEM: TAKEOVER, COMPLETION AND REDESIGN

Project: a complete, secure, modern school management system for Mary Royal School (Obunga, Kisumu, Kenya), a CBC (Competency Based Curriculum) school. Slogan: "Nurturing Excellence Through CBC". Stack already in the repo (Next.js App Router, TypeScript, Tailwind, shadcn/ui, Prisma with SQLite locally and PostgreSQL-compatible, Auth.js, Zod, Vitest, Playwright). It runs locally on localhost first. Deployment is out of scope.

The previous AI agent (Codex) stopped when its quota expired, possibly mid-task, after finishing PART of the work below. You are taking over. Do NOT rebuild what already works.

======================================================================
## 0. TAKEOVER (do this first, then keep going)
======================================================================
1. Inspect the repo: `git status`, `git log --oneline -20`, read `AGENTS.md`, `docs/PROGRESS.md`, `docs/DECISIONS.md`, `docs/ROADMAP.md`, and the code structure. Look for uncommitted or half-finished changes and decide per file whether to finish or revert.
2. Get it healthy before adding anything: install dependencies, run migrations, run build, lint, type-check and all tests. Fix every error, broken migration and failing test. Commit as "Takeover: stabilize".
3. Audit every requirement in Section 4 (items R1 to R10). Do not trust progress notes alone: verify each by reading the code AND running the feature in the app as the relevant role. Mark each DONE (works end to end and has tests), PARTIAL, or MISSING. Write evidence (file paths, what you tested) to `docs/TAKEOVER_AUDIT.md`.
4. Finish every PARTIAL and MISSING item. Leave DONE items alone except restyling them in the new design language (Section 3).
5. Keep all strict role-based permissions, audit logging and tests working. Never weaken security to add a feature.

======================================================================
## 1. OPERATING RULES (fully autonomous)
======================================================================
- Work through everything in ONE continuous run. Do not stop, pause or ask me questions between phases. Never end with "let me know if you'd like me to continue". Log ambiguous decisions in `docs/DECISIONS.md` and keep going.
- After EVERY phase: run the app locally, fix all errors, run lint, type-check, unit, e2e and the role permission suite, append a summary to `docs/PROGRESS.md`, and `git commit` with a clear message.
- Debug and fix problems yourself. Only skip something if truly impossible, then record it in `docs/ROADMAP.md` and continue.
- No placeholders, fake buttons, "coming soon" pages or lorem ipsum. Everything must work with real data.
- Keep running locally. No deployment, no domains, no paid services.
- Provide `npm run setup` (install, migrate, create clean state) and `npm run dev`. Keep the README accurate for Windows, Mac and Linux.
- Update `AGENTS.md` so it holds these rules, the permission rules and the design rules.

======================================================================
## 2. ROLES AND STRICT ACCESS (must stay enforced)
======================================================================
Roles: DIRECTOR, HEAD_TEACHER, DEPUTY, SECRETARY, ACCOUNTANT, TEACHER, PARENT, LEARNER.
- No public registration. The Director manages all staff accounts. The Secretary creates learner and parent accounts through admission (see R4); the Director can edit or suspend any account.
- Enforce permissions in three layers: middleware route guards, server-side checks in every action/API route via the central matrix in `lib/permissions.ts`, and UI hiding. Row-level scoping: parent sees only linked children, learner only self, teacher only assigned classes and subjects, accountant finance data plus learner name/class/admission number only.
- Append-only audit log of logins, account/role changes, password resets, grade edits, attendance edits, payment create/edit/void, approvals, admissions, exports and denied access attempts.
- Rate-limited login, lockout after repeated failures, idle timeout, forced password change on first login, CSRF protection, secure headers, upload validation, Kenya Data Protection Act awareness (minimal data, access log for children's data, export/anonymize on Director request, privacy notice page).
- The automated permission test suite (every role against every other role's routes, actions and data, including IDOR attempts) must include the Secretary and every new page, and must pass.

======================================================================
## 3. DESIGN LANGUAGE: TWO REFERENCE DESIGNS, BOTH IN BLUE AND WHITE
======================================================================
Study the attached images and files in `docs/design-reference/` (reference 1: "Dashboard V15" soft bento UI; reference 2: "Glassy Dashboard V2" frosted-glass UI). Implement BOTH as two selectable themes in one design system. Keep the school's palette: royal blue primary (about #1D4ED8 with a full tonal scale), white, light blue-gray (#F8FAFC), dark navy text. Use red, amber and green only for status. Replace the original orange, lavender and brown tones with blue-based equivalents.

### Theme A: "Bento" (reference 1). Default for staff: Director, Head Teacher, Deputy, Secretary, Accountant, Teacher.
- Very light airy gradient background (soft blue to white with a faint glow).
- Floating pill-shaped top navigation with section tabs (active tab filled navy or royal blue), logo mark left, search, notification bell and avatar right.
- Slim floating icon rail on the left with tooltips.
- Large friendly page titles and a big "Search something..." header with live results (learners, staff, invoices, pages).
- Bento grid of rounded-2xl white cards with soft shadows: KPI cards with large numerals and small green/red trend chips, gauge/donut cards, smooth area/line charts, progress-bar lists, avatar leaderboard lists, schedule/calendar strip cards, and ONE bold royal-blue accent card per dashboard for the primary action (e.g. "Admit a learner", "Record payment", "Take attendance").
- Pastel tinted cards (soft blue, sky, mint, lavender) for classes and task lists.
- Hover lift, count-up numbers, chart draw-in animation, floating pill toggles for switching views.

### Theme B: "Glass" (reference 2). Default for Parent, Learner and the login page.
- Full-screen soft blurred blue-tinted background (replaceable image at `/public/backgrounds/school-blur.jpg`, with a CSS blue gradient fallback).
- Translucent frosted-glass cards (backdrop blur, thin white border, soft inner light) with guaranteed text contrast.
- Floating top bar with greeting ("Good morning, <name>"), small activity ticker and quick actions.
- Floating bottom dock navigation with an active pill (main navigation on mobile; bottom-center on desktop) plus a slim left icon rail on desktop.
- Cards: fee balance card with action buttons, stat tiles with trend chips, bar chart card, recent activity table with status pills, compact children/class cards.
- Fallback to solid soft-blue cards automatically when backdrop-filter is unsupported or `prefers-reduced-transparency` / `prefers-reduced-motion` is set, or on low-power devices.

### Theme switching and implementation
- Each user can switch Bento/Glass from the profile menu and a quick top-bar toggle; save the choice per user in the database. Keep light and dark mode, both blue-based. The Director sets the default theme per role in Settings.
- Write every component once using design tokens (CSS variables / Tailwind tokens for surface, border, blur, shadow, radius) so both themes work on every page. No duplicated pages.
- Use `framer-motion` (hover lift about -5px, fade/slide-in, layout transitions, count-ups) and `lucide-react`.
- Build reusable components: GlassCard, BentoCard, StatCard (title, value, change, icon, tone), AccentCard, FloatingPillNav, IconRail, BottomDock, PageHeader (breadcrumb plus smart back button), SearchHeader, ChartCard, ProgressList, AvatarList, ScheduleStrip, StatusPill, EmptyState, Skeleton variants.
- Charts: recharts with rounded bars, soft gradients, minimal gridlines.
- Mobile first: the Bento top pill nav collapses to a bottom dock on phones; single-column stacking; touch targets 44px or more; light payloads for slow connections; PWA stays working.
- Accessibility: WCAG AA contrast in both themes, focus rings, keyboard navigation, reduced-motion support.

### Apply to EVERY page
Create `docs/UI_UPGRADE_CHECKLIST.md` listing every page for every role (current problems, planned improvements, status). Redesign each page one by one in both themes: dashboards, tables, forms, wizards, profile pages, finance, attendance, exams and rubrics, complaints, learner videos, settings, login. No page is skipped; the checklist must end with zero unchecked pages. Role dashboards: Director (enrollment, attendance, fee collection, performance, getting-started checklist); Head Teacher (academics, approvals, complaints); Deputy (today's attendance by class, exam progress, discipline); Secretary (admissions pipeline, missing documents, new learners); Accountant (collections, arrears, recent payments); Teacher (my classes, timetable, pending marks); Parent and Learner (Glass: fee balance, attendance, latest results, homework, announcements, "My Learning" videos).

======================================================================
## 4. REQUIREMENTS TO VERIFY AND FINISH (R1 to R10)
======================================================================

### R1. SECRETARY ROLE
- Can: run the full admission process, create learner and parent accounts as part of admission, edit learner records, upload documents, print/download the admission form, view the fee structure (read-only), view a learner's fees paid and balance (read-only), view class lists, search learners.
- Cannot: record/void payments, see marks, discipline or medical notes beyond what admission needs, create staff accounts, change roles, or edit the fee structure.
- Learner accounts are NOT created automatically. A learner gets a login only when the Secretary (or Director) explicitly creates it from the learner profile or at the end of admission. Existing parents are linked (match by phone/ID number) rather than duplicated.
- Add to the permission matrix, navigation, dashboard, audit log and tests.

### R2. FRESH START
- Remove ALL demo and seed data (learners, parents, teachers, classes, marks, attendance, invoices, payments, announcements, discipline, messages).
- Keep ONLY the Director account (forced password change), empty school settings structure, CBC grading defaults and system configuration.
- Provide `npm run reset:fresh` (confirmation prompt) and an optional `npm run seed:demo` (off by default).
- Empty states guide the exact order: Director sets up school (year, terms, classes, subjects, rubrics, fee structure), creates staff, Secretary admits learners, Deputy sets exams, teachers take attendance and enter marks, Accountant collects fees. Add a "Getting started" checklist on the Director dashboard.

### R3. NAVIGATION AND BACK BUTTON
- Breadcrumbs on every page below a dashboard.
- Smart Back button in the page header: previous in-app page if one exists, otherwise a sensible parent route; never leaves the app, never loops, never lands on login.
- Mobile top app bar with a back arrow on detail pages; bottom nav visible on top-level pages.
- Sidebar/rail highlights the current section; logo/Home always returns to the role dashboard.
- Preserve list state (filters, search, pagination, scroll) when going back; warn before leaving forms with unsaved changes; handle browser back/forward and login/logout redirects. Test across every role and nested page.

### R4. ADMISSION (Secretary workflow)
Multi-step wizard with progress bar, draft autosave, per-step validation, final review:
1. Learner: full names, date of birth, gender, nationality, UPI/NEMIS number, admission number (auto, unique, sequential), class and stream applied for, admission date, previous school and last class, reason for leaving, religion (optional), languages.
2. Residence: county, sub-county, ward/estate, village/area, physical address and landmarks, distance and means of transport, optional map pin or coordinates.
3. Parent/guardian: father, mother and/or guardian (at least one): names, relationship, ID/passport number, phones (+254, WhatsApp flag), email, occupation, employer, addresses, who pays fees, primary contact flag. At least two emergency contacts. Authorized pick-up persons.
4. Health: illnesses/conditions, allergies, medication, disabilities/special needs, blood group, preferred hospital, immunization status, insurance (e.g. SHA). Restricted visibility (Director, Head Teacher, Deputy, Secretary, and the learner's class teacher) with an access log.
5. Documents (upload with preview, type/size validation, secure storage, replace/remove): learner passport photo (with crop tool), birth certificate, previous school report or leaving certificate, immunization card, parent ID copy, optional extras. Show a checklist of missing required documents.
6. Fees: show the fee structure for the chosen class, optional items (transport, lunch, boarding), first invoice preview. The Secretary does not record payments; hand off to the Accountant.
7. Review and submit: summary with edit links, Data Protection Act consent checkbox.
After submission: success screen with admission number and actions (Download admission form PDF, Print, Create learner login, Create/link parent login, Start another admission, Open learner profile). The PDF is professionally designed (logo, branding, all learner, residence, parent, health and fee information, passport photo, document checklist, signature lines for parent/guardian, secretary and head teacher, date) so it can be printed and filed physically, and can be re-downloaded from the learner profile any time. Admissions list shows Draft/Submitted/Enrolled with search and filters. Audit-log every admission action.

### R5. LEARNER AND STAFF PROFILES
Learner profile (opens when an authorized user clicks any learner anywhere): hero header with photo, name, admission number, class/stream, status, age and quick actions (Download admission form, Message parent, Edit, Create login). Tabs: Overview/Biography, Parents and guardians, Residence, Health (restricted), Documents, Attendance, Academics (results by term, CBC levels, trends), Fees (invoiced, paid, balance, history, receipts, status badge), Discipline, Activity timeline.
Visibility (server-enforced, Director-configurable): Director, Head Teacher, Deputy, Secretary see the full profile. Accountant sees biography basics, parent contacts and Fees only. Teacher sees only assigned learners: biography, parent contacts, attendance, academics for their subjects, discipline summary, and NO fee amounts by default (Director can enable "Teachers see fee status", showing only paid/not paid). Parent and Learner see only their own/their child's limited view.
Staff profile (when Head Teacher or Director taps a staff account): photo, contact, role(s), staff/TSC number, qualifications, subjects and classes, timetable, attendance/punctuality, lesson plans submitted, marks submission status, account status and last login; actions (reset password, suspend, edit) for the Director only. Teachers see only their own profile.

### R6. ATTENDANCE DASHBOARD, EXAMS, RUBRICS (Deputy; Head Teacher view/approve)
- Attendance dashboard page for Deputy and Head Teacher: school-wide rate, present/absent/late counts, by class and stream (heat map/table), weekly and termly trends, gender split; every class's attendance in one place with drill-down to class then learner; which classes have NOT taken attendance today with the responsible teacher and a reminder button; absentee follow-up list with repeat absentees, follow-up notes and parent notification; teacher attendance overview; filters by date, class, stream, term; export PDF/Excel.
- Exam management: Deputy creates exam sessions per term (Opening, Mid-Term, End-Term, etc.) with classes, subjects, dates, marks-entry open/close dates and status (Draft, Active, Closed, Published). Teachers enter marks ONLY for sessions that are Active for their assigned classes and subjects, so when End-Term is active only End-Term appears. Deputy sees marks-entry progress per class and subject with reminders, sets exam timetable, invigilators and rooms. Locking, reopening (reason plus approval) and audit logging stay.
- Rubrics: a central page where the Deputy defines the CBC performance scale for the whole school (EE, ME, AE, BE and sub-levels, score ranges, descriptors, colors; per learning area or grade band if needed). Teachers cannot define their own; levels compute automatically from scores. Changing a rubric after marks exist needs a confirmation showing the impact and is audit-logged; published report cards keep the rubric version they were issued with.

### R7. COMPLAINTS
Complaints exist ONLY as a one-way channel from Parent to Head Teacher and Director. Remove complaint pages, menus, forms, API routes and permissions for every other role. Parent submits (category, description, optional attachment, directed to Head Teacher, Director or both) and tracks status (Received, In review, Resolved) with replies. Head Teacher and Director get an inbox with filters, status changes, replies and resolution; replies notify the parent. Tests prove no other role can reach complaint endpoints.

### R8. LEARNER VIDEOS (KICD-based, tailored to weak topics)
- Learner dashboard "My Learning": identify weak learning areas, strands and sub-strands automatically from assessment results (lowest CBC levels and score trends) and recommend revision videos for those topics.
- Source: Kenya Institute of Curriculum Development (KICD) digital content published on its YouTube channel. Show only real, verified videos. NEVER invent video IDs or links.
- `LearningVideo` table: title, YouTube video ID, grade level, learning area, strand/sub-strand, source, added by, status.
- Primary method: optional YouTube Data API integration (key in an environment variable, off by default) that searches only the official KICD channel by grade, learning area and strand, validates results and saves them. Always-works fallback: a curation screen where Head Teacher, Deputy or teachers paste a YouTube link, tag grade, learning area and strand, and it is validated and added; include a "Suggested search on KICD channel" helper link.
- Learner sees only library videos matched to their grade and weak strands; if none exist show "Your teacher hasn't added videos for this topic yet".
- Embed with youtube-nocookie, no autoplay, limited related-video distraction, in a clean player card; track watched status and learner progress; teachers can see who watched what. Learners cannot browse arbitrary YouTube.

### R9. FEE STRUCTURE AND FINANCE
- Head Teacher and Director create and manage the fee structure per class and term: line items (tuition, transport, lunch, boarding, uniform, activity, exam, admission, etc.), compulsory vs. optional, amounts, effective dates. Changes are versioned and audit-logged; issued invoices keep original amounts. The structure appears read-only for the Secretary and Accountant and drives admission and invoice generation. Page features: clean table/cards by class and term, duplicate to next term, totals per class, parent billing preview.
- Accountant manages invoices, payments and discount requests; waiver/discount approval stays with the Director.
- Finance UI redesign in both themes with a clear status color language: Paid (green), Partially paid (amber), Unpaid (blue/neutral), Overdue (red), Pending approval (purple/amber), Voided (gray); pill badges, collection progress bars, consistent icons.
- Finance dashboard: collected today/this term, outstanding, collection rate vs. target, collections by method and by class, 12-week trend, top arrears, recent payments feed.
- Create-invoice flow as a stepper: search learner (name/admission number with photo), choose term, auto-load fee items with optional toggles, apply approved discounts, live totals with balance carry-forward, preview, confirm.
- Record-payment drawer: learner search, outstanding balance, amount, method (Cash, M-Pesa with transaction code and duplicate-code check, Bank, Cheque), reference, date, auto-allocation, preview, confirm.
- Confirmation and approval flows: a confirmation dialog before, and an animated success state after, recording payments and approving waivers/discounts/voids (check animation, receipt number, buttons: Download receipt, Print, Record another, View learner). Approvals show who, when and why. Notify the parent in-app on payment confirmation.
- Receipts and invoices as designed PDFs (logo, sequential numbers, balance, QR code for verification). Statements and arrears pages with colored aging buckets (0-30, 31-60, 61-90, 90+), bulk reminders, export. Receipts are never deleted, only voided with a reason.

### R10. WHOLE-SYSTEM QUALITY
- Upgrade tables (sticky header, sorting, filters, search, pagination, bulk actions, row hover actions, card layout on mobile), forms (grouped sections, inline validation, sensible defaults, autosave where relevant, large touch targets), and flows (clear primary action per page, drawers for quick actions, command palette Ctrl/Cmd+K global search). Print styles for lists, forms, receipts and reports. Dark mode on every page.
- Integrity: transactions, unique constraints, versioned history for marks and payments (never silently overwrite), sequential unique admission and receipt numbers, released report cards change only through an approved re-open.

======================================================================
## 5. TESTING AND FINISH
======================================================================
- Unit tests: permissions, grading and rubric mapping, fee calculations, balance carry-forward, exam activation filtering.
- End-to-end tests: Secretary admission (admit, upload documents, download form, create learner login, link parent); Deputy activates End-Term and the teacher sees only that exam; rubric applied to marks; Head Teacher creates fee structure and Secretary/Accountant see it; Accountant records an M-Pesa payment and issues a receipt; Parent complaint reaches only Head Teacher/Director; learner sees only library videos for weak topics; back-button navigation across roles; fresh-start reset leaves only the Director; negative cross-role access tests.
- Visual smoke tests loading every page for every role in both themes with no console errors.
- Update README and `docs/guides/` (a guide per role, including Secretary) with screenshots of both themes.

START NOW. Do not stop until the takeover audit items are all finished, every page is redesigned in both themes, all tests pass and everything is committed. Then send ONE final message with: (1) exact commands to run locally, (2) how to log in as the Director and the order to follow from setup, to admission, to first class, (3) the takeover audit summary: what Codex had already done, what you finished, what you redesigned, (4) test results, (5) anything left in `docs/ROADMAP.md`.