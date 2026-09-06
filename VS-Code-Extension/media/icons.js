// icons.js — professional inline SVG icon set for the UI Builder (no emoji).
// Stroke-based, 24x24 viewBox, uses currentColor so themes/CSS drive the color.
(function (global) {
  const S = (p, o) =>
    `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="${(o && o.w) || 1.7}" ` +
    `stroke-linecap="round" stroke-linejoin="round" width="${(o && o.s) || 16}" height="${(o && o.s) || 16}">${p}</svg>`;

  const ICONS = {
    // brand / editor
    layout: (o) => S('<rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18M9 21V9"/>', o),
    npas: (o) => S('<path d="M7 8l-4 4 4 4M13 8l4 4-4 4"/>', o),
    // toolbar
    play: (o) => S('<path d="M6 4l14 8-14 8V4z"/>', o),
    undo: (o) => S('<path d="M9 14L4 9l5-5"/><path d="M4 9h11a5 5 0 0 1 0 10h-1"/>', o),
    redo: (o) => S('<path d="M15 14l5-5-5-5"/><path d="M20 9H9a5 5 0 0 0 0 10h1"/>', o),
    sync: (o) => S('<path d="M21 12a9 9 0 0 1-9 9 9 9 0 0 1-6.7-3M3 12a9 9 0 0 1 9-9 9 9 0 0 1 6.7 3"/><path d="M21 3v5h-5M3 21v-5h5"/>', o),
    check: (o) => S('<path d="M20 6L9 17l-5-5"/>', o),
    route: (o) => S('<circle cx="6" cy="6" r="2.5"/><circle cx="18" cy="18" r="2.5"/><path d="M8.5 6H15a3 3 0 0 1 3 3v6"/>', o),
    // palette groups
    box: (o) => S('<rect x="3" y="3" width="18" height="18" rx="2"/>', o),
    grid: (o) => S('<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>', o),
    type: (o) => S('<path d="M4 7V5h16v2M9 19h6M12 5v14"/>', o),
    nav: (o) => S('<rect x="3" y="4" width="18" height="4" rx="1"/><path d="M3 12h10M3 17h7"/>', o),
    form: (o) => S('<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 9h10M7 13h6"/>', o),
    data: (o) => S('<ellipse cx="12" cy="6" rx="8" ry="3"/><path d="M4 6v6c0 1.7 3.6 3 8 3s8-1.3 8-3V6M4 12v6c0 1.7 3.6 3 8 3s8-1.3 8-3v-6"/>', o),
    chart: (o) => S('<path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/>', o),
    // actions
    plus: (o) => S('<path d="M12 5v14M5 12h14"/>', o),
    trash: (o) => S('<path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6M10 11v6M14 11v6"/>', o),
    code: (o) => S('<path d="M8 6l-6 6 6 6M16 6l6 6-6 6"/>', o),
    bolt: (o) => S('<path d="M13 2L4 14h7l-1 8 9-12h-7l1-8z"/>', o),
    chevron: (o) => S('<path d="M6 9l6 6 6-6"/>', o),
    copy: (o) => S('<rect x="9" y="9" width="12" height="12" rx="2"/><path d="M5 15V5a2 2 0 0 1 2-2h10"/>', o),
    move: (o) => S('<path d="M5 9l-3 3 3 3M9 5l3-3 3 3M15 19l-3 3-3-3M19 9l3 3-3 3M2 12h20M12 2v20"/>', o),
    gear: (o) => S('<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.6 1.6 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.6 1.6 0 0 0-2.7 1.1V21a2 2 0 0 1-4 0v-.1A1.6 1.6 0 0 0 7 19.4a1.6 1.6 0 0 0-1.8.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.6 1.6 0 0 0-1.1-2.7H1a2 2 0 0 1 0-4h.1A1.6 1.6 0 0 0 2.6 7a1.6 1.6 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1A1.6 1.6 0 0 0 7 2.6h.1A1.6 1.6 0 0 0 9 1.1V1a2 2 0 0 1 4 0v.1A1.6 1.6 0 0 0 15 2.6a1.6 1.6 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.6 1.6 0 0 0-.3 1.8V7a1.6 1.6 0 0 0 1.5 1H23a2 2 0 0 1 0 4h-.1a1.6 1.6 0 0 0-1.5 1z"/>', o),
    target: (o) => S('<circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5"/><circle cx="12" cy="12" r="1.5"/>', o),
    globe: (o) => S('<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c2.5 2.6 2.5 15.4 0 18M12 3c-2.5 2.6-2.5 15.4 0 18"/>', o),
  };

  global.NpIcons = {
    get(name, opts) { return (ICONS[name] || ICONS.box)(opts || {}); },
  };
})(typeof window !== 'undefined' ? window : globalThis);
