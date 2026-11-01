'use strict';

const header = document.querySelector('[data-header]');
const backTop = document.querySelector('[data-back-top]');
const nav = document.querySelector('[data-nav]');
const menuToggle = document.querySelector('[data-menu-toggle]');
const overlay = document.querySelector('[data-nav-overlay]');
const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

if (header && backTop) {
  const updateScrollState = () => {
    const y = window.scrollY;
    header.classList.toggle('scrolled', y > 40);
    backTop.classList.toggle('show', y > 600);
  };
  window.addEventListener('scroll', updateScrollState, { passive: true });
  updateScrollState();
  backTop.addEventListener('click', () => window.scrollTo({ top: 0, behavior: reducedMotion ? 'auto' : 'smooth' }));
}

/* The mobile menu and its backdrop share one stacking level below the header. */
if (header && nav && menuToggle && overlay) {
  const navLinksInPanel = [...nav.querySelectorAll('a')];
  const setNavOpen = (open, restoreFocus = false) => {
    const collapsed = window.innerWidth <= 900 && !open;
    nav.classList.toggle('open', open);
    menuToggle.classList.toggle('open', open);
    overlay.classList.toggle('show', open);
    overlay.setAttribute('aria-hidden', String(!open));
    nav.setAttribute('aria-hidden', String(collapsed));
    nav.inert = collapsed;
    menuToggle.setAttribute('aria-expanded', String(open));
    menuToggle.setAttribute('aria-label', open ? 'Close main menu' : 'Open main menu');
    document.body.style.overflow = open ? 'hidden' : '';
    if (open) navLinksInPanel[0]?.focus();
    else if (restoreFocus) menuToggle.focus();
  };

  document.documentElement.classList.add('js-enabled');
  setNavOpen(false);
  menuToggle.addEventListener('click', () => setNavOpen(menuToggle.getAttribute('aria-expanded') !== 'true'));
  overlay.addEventListener('click', () => setNavOpen(false, true));
  navLinksInPanel.forEach(link => link.addEventListener('click', () => setNavOpen(false)));
  document.addEventListener('keydown', event => {
    if (menuToggle.getAttribute('aria-expanded') !== 'true') return;
    if (event.key === 'Escape') {
      event.preventDefault();
      setNavOpen(false, true);
      return;
    }
    if (event.key !== 'Tab') return;
    const focusable = [menuToggle, ...navLinksInPanel];
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  });
  window.addEventListener('resize', () => {
    if (window.innerWidth > 900 && menuToggle.getAttribute('aria-expanded') === 'true') setNavOpen(false);
  }, { passive: true });
}

/* Gentle reveal; content remains visible when reduced motion is requested. */
if (!reducedMotion && 'IntersectionObserver' in window) {
  const revealObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        revealObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });
  document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el));

  const animateCount = el => {
    const target = Number.parseFloat(el.dataset.count);
    if (!Number.isFinite(target)) return;
    const suffix = el.dataset.suffix || '';
    const duration = 1600;
    const start = performance.now();
    const tick = now => {
      const p = Math.min((now - start) / duration, 1);
      const eased = 1 - Math.pow(1 - p, 3);
      el.textContent = Math.round(target * eased).toLocaleString() + suffix;
      if (p < 1) requestAnimationFrame(tick);
    };
    requestAnimationFrame(tick);
  };
  const countObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        animateCount(entry.target);
        countObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.5 });
  document.querySelectorAll('[data-count]').forEach(el => countObserver.observe(el));
} else {
  document.querySelectorAll('.reveal').forEach(el => el.classList.add('visible'));
}

/* Keep the active section reflected in the in-page navigation. */
if ('IntersectionObserver' in window) {
  const navLinks = document.querySelectorAll('.nav a[href^="#"]');
  const sections = document.querySelectorAll('main section[id]');
  const sectionObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        navLinks.forEach(link => link.classList.toggle('active', link.getAttribute('href') === `#${entry.target.id}`));
      }
    });
  }, { rootMargin: '-40% 0px -55% 0px' });
  sections.forEach(section => sectionObserver.observe(section));
}

/* Accessible photo lightbox for the school-life gallery. */
const lightbox = document.querySelector('.gallery-lightbox');
if (lightbox && typeof lightbox.showModal === 'function') {
  const lightboxImage = lightbox.querySelector('img');
  const caption = lightbox.querySelector('figcaption');
  const closeButton = lightbox.querySelector('.gallery-lightbox-close');
  document.querySelectorAll('.g-item').forEach(item => {
    const open = () => {
      const source = item.querySelector('img');
      if (!source || !lightboxImage || !caption) return;
      lightboxImage.src = source.currentSrc || source.src;
      lightboxImage.alt = source.alt;
      caption.textContent = item.querySelector('figcaption')?.textContent || source.alt;
      lightbox.showModal();
      closeButton?.focus();
    };
    item.addEventListener('click', open);
    item.addEventListener('keydown', event => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        open();
      }
    });
  });
  closeButton?.addEventListener('click', () => lightbox.close());
  lightbox.addEventListener('click', event => {
    if (event.target === lightbox) lightbox.close();
  });
}
