# Mary Royal public website refresh

## Inspection and root causes

The public website is one static HTML page (`school-website-refresh/index.html`) served from cPanel; it has one shared stylesheet and one browser script. The authenticated Next.js school portal is a separate application on `portal.maryroyalschool.co.ke`.

The public site and the authenticated Next.js portal are separate deployments. In the static page, the mobile menu used a translucent, blurred header treatment that reduced text clarity, and the page's school-highlights markup had an extra closing wrapper and an orphan label. The hero image also needed a predictable aspect ratio at narrow widths. The earlier carousel was already removed to avoid loading several large images before users needed them.

## Work completed

- Rebuilt the mobile menu as a keyboard-accessible full-width dropdown. Corrected stacking, control IDs and ARIA state, added Escape/backdrop/link closing, focus handling, reduced-motion support, and a no-JavaScript link fallback.
- Reworked visual tokens to a consistent royal blue, navy and white palette; added sticky-header contrast, responsive layout, focus visibility, and mobile gallery layout.
- Removed the autoplay hero carousel and used one optimized graduation image as the hero and social preview.
- Added a responsive gallery and keyboard-operable lightbox. Existing source images were preserved.
- Added descriptive alternate text, image dimensions, lazy loading below the fold, a 48px favicon, Apple touch icon, preload for the hero, canonical metadata, social cards, school JSON-LD, robots rules, sitemap, branded 404, and crawler noindex for the authenticated portal.
- Repaired the school-highlights markup, reduced mobile typography, made the mobile navigation panel fully opaque and sharp, improved its spacing and touch targets, and made the hero photo scale with a fixed responsive aspect ratio. Renamed the phone action to “Call the school” so its result is clear.

## Supplied photo placement

All 11 supplied photos are used as optimized WebP assets. The graduation group photo is the homepage hero. The other ten photos appear in the School Life gallery; the teacher-supporting-learner photo is also used in the About inset. New files are `school-graduation-hero.webp`, `graduation-celebration.webp`, `cultural-dance-performance.webp`, `early-years-school-event.webp`, `learner-speaking-at-assembly.webp`, `learners-in-school-parade.webp`, `learners-at-school-event.webp`, `junior-learners-performing.webp`, `teacher-supporting-learner.webp`, `school-celebration-stage.webp`, and `graduation-gifts-display.webp`. None of the supplied photos were skipped.

## Verification

- Responsive browser smoke check at 360, 390, 768, 1024 and 1280 pixels: no horizontal overflow, one H1, mobile Login visible when menu opens, Escape closes the menu.
- Gallery lightbox opens and closes with keyboard input; no browser page errors.
- Static validation: all 25 image elements include alt text and explicit dimensions; referenced image files exist. One 50-character title and one 155-character description; CSS parses and JavaScript passes `node --check`.
- Portal `npm run lint`: 0 errors, 39 existing warnings in unrelated files. `npm run typecheck` and `npm run build:production` passed.
- Latest static browser run at 360, 390, 768, 1024 and 1280 pixels: no horizontal overflow, no page errors, the mobile menu opens with a solid navy background and no backdrop blur, tapping a section link navigates and closes the panel, the hero image resizes to the available width, and all 24 referenced images load.

## Publishing follow-up

The new mobile fixes are local until copied to cPanel `/public_html`. A fresh upload archive is available at the repository root as `mary-royal-website-upload.zip`. The live homepage still serves the previous stylesheet. cPanel publishing could not be completed because the browser-control safety layer could not verify the active Chrome URL; it stopped further computer interaction. Follow `school-website-refresh/UPLOAD.md` to upload and extract the archive outside the public web root, then verify the live homepage, Login, sitemap, robots, 404 response and social-preview image. After publishing, add the canonical domain to Google Search Console, submit `/sitemap.xml`, request indexing and claim or verify the Google Business Profile.
