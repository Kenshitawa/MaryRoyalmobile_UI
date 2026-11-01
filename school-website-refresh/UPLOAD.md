# Mary Royal School website publishing package

The public site is `https://maryroyalschool.co.ke/`. Its portal Login links to `https://portal.maryroyalschool.co.ke/`.

## Upload to cPanel

1. Back up the existing `/public_html` files before replacing the homepage.
2. Upload `mary-royal-website-upload.zip` into `/public_html` and extract it there. The archive contains only the static public site files and assets.
3. Allow replacement of `index.html`, `404.html`, `privacy.html`, `robots.txt`, `sitemap.xml`, and the referenced files under `assets/`.
4. Preserve `.htaccess`, `.well-known/`, hosting verification files, email/DNS files, and `cgi-bin`.
5. Remove the uploaded ZIP from the public web root after extraction.

## Verify

- Check the home page at 360, 390, 768, 900, 1024 and 1280 px. The menu stays mobile through 1023 px; the desktop navigation begins at 1024 px.
- Test the menu open/close and anchors, dark mode persistence, admission form link, email/telephone/WhatsApp links, image gallery and Privacy Notice.
- Check `/privacy.html`, `/robots.txt`, `/sitemap.xml`, `/404.html`, and the hero photo asset.

The admission enquiry is the school's existing Google Form. The contact form creates an email draft addressed to `maryroyalschool81@gmail.com`; it does not claim to send the message automatically.
