// widgets-webink.js — WebInk widget registry for the visual builder.
// Each entry: group, label, container, defaultProps, fields (inspector),
// preview(node, innerHtml) -> HTML using the semantic classes in preview.css.
(function (global) {
  const esc = (s) =>
    String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  const p = (node, k, d) => (node.props && node.props[k] !== undefined ? node.props[k] : d);
  // A prop value that references a global var (=cliques) previews as the var name.
  const showVal = (v) => (typeof v === 'string' && v.startsWith('=') ? v.slice(1) : esc(v));
  // A prop bound to a variable/expression (fx): the value is a "=..." string.
  const isFx = (v) => typeof v === 'string' && v.startsWith('=');
  const asArr = (v) => (Array.isArray(v) ? v : []);
  // Chip shown in the preview where fixed data would be, when the prop is fx-bound.
  const fxChip = (v) => `<span class="fx-chip" title="ligado a variável/expressão">fx ${esc(String(v).slice(1)) || '…'}</span>`;

  function tablePreview(n) {
    if (isFx(p(n, 'rows'))) return `<div class="wk-tablewrap">${fxChip(p(n, 'rows'))}</div>`;
    const cols = asArr(p(n, 'columns', []));
    const rows = asArr(p(n, 'rows', []));
    const head = cols.map((c) => `<th>${esc(c)}</th>`).join('');
    const bodyR = rows.map((r) => '<tr>' + (Array.isArray(r) ? r : []).map((c) => `<td>${esc(c)}</td>`).join('') + '</tr>').join('');
    return `<div class="wk-tablewrap"><table class="wk-table"><thead><tr>${head}</tr></thead><tbody>${bodyR}</tbody></table></div>`;
  }
  function chartPreview(n) {
    if (isFx(p(n, 'data'))) return `<div class="wk-chart">${fxChip(p(n, 'data'))}</div>`;
    const data = p(n, 'data', {}) || {};
    const ds = (data.datasets && data.datasets[0]) || {};
    const vals = (ds.data || []).map(Number);
    const labels = data.labels || [];
    const max = Math.max(1, ...vals.filter((x) => !isNaN(x)));
    const bars = vals.map((v, i) =>
      `<div class="cbar" style="height:${Math.round(((v || 0) / max) * 100)}%"><span>${esc(labels[i] != null ? labels[i] : '')}</span></div>`).join('');
    return `<div class="wk-chart"><div class="cbars">${bars || '<span class="ph">gráfico</span>'}</div></div>`;
  }

  const COLORS = ['slate', 'red', 'green', 'blue', 'amber', 'indigo', 'emerald', 'rose'];
  const F = {
    className: { key: 'className', label: 'className (Tailwind)', kind: 'text', ph: 'ex.: mt-2 w-full' },
    text: { key: 'text', label: 'Texto', kind: 'text' },
  };

  const W = {
    // ── Layout ──────────────────────────────────────────────────────────────
    Page: { group: 'Layout', label: 'Page', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<div class="wk-page">${i}</div>` },
    Container: { group: 'Layout', label: 'Container', container: true, defaultProps: { className: 'py-8 space-y-6' },
      fields: [F.className], preview: (n, i) => `<div class="wk-container">${i}</div>` },
    Section: { group: 'Layout', label: 'Section', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<section class="wk-section">${i}</section>` },
    Grid: { group: 'Layout', label: 'Grid', container: true, defaultProps: { cols: 3 },
      fields: [{ key: 'cols', label: 'Colunas', kind: 'number', min: 1, max: 12 }, F.className],
      preview: (n, i) => `<div class="wk-grid" style="grid-template-columns:repeat(${p(n, 'cols', 3)},minmax(0,1fr))">${i}</div>` },
    Row: { group: 'Layout', label: 'Row', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<div class="wk-row">${i}</div>` },
    Col: { group: 'Layout', label: 'Col', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<div class="wk-col">${i}</div>` },
    Card: { group: 'Layout', label: 'Card', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<div class="wk-card">${i}</div>` },
    Divider: { group: 'Layout', label: 'Divider', container: false, defaultProps: {},
      fields: [F.className], preview: () => `<hr class="wk-divider"/>` },
    Spacer: { group: 'Layout', label: 'Spacer', container: false, defaultProps: {},
      fields: [F.className], preview: () => `<div class="wk-spacer"></div>` },

    // ── Tipografia ──────────────────────────────────────────────────────────
    Heading: { group: 'Tipografia', label: 'Heading', container: false, defaultProps: { level: 2, text: 'Título' },
      fields: [{ key: 'level', label: 'Nível', kind: 'enum', options: [1, 2, 3, 4, 5, 6] }, F.text, F.className],
      preview: (n) => `<div class="wk-h wk-h${p(n, 'level', 2)}">${showVal(p(n, 'text', ''))}</div>` },
    Text: { group: 'Tipografia', label: 'Text', container: false, defaultProps: { text: 'Texto' },
      fields: [F.text, F.className], preview: (n) => `<p class="wk-text">${showVal(p(n, 'text', ''))}</p>` },
    Badge: { group: 'Tipografia', label: 'Badge', container: false, defaultProps: { text: 'novo', color: 'slate' },
      fields: [F.text, { key: 'color', label: 'Cor', kind: 'enum', options: COLORS }, F.className],
      preview: (n) => `<span class="wk-badge wk-badge-${p(n, 'color', 'slate')}">${showVal(p(n, 'text', ''))}</span>` },
    StatCard: { group: 'Tipografia', label: 'StatCard', container: false, defaultProps: { label: 'Métrica', value: '0' },
      fields: [{ key: 'label', label: 'Rótulo', kind: 'text' }, { key: 'value', label: 'Valor', kind: 'text', bindable: true }, { key: 'delta', label: 'Delta (opcional)', kind: 'text', bindable: true }, F.className],
      preview: (n) => `<div class="wk-stat"><div class="lbl">${showVal(p(n, 'label', ''))}</div>` +
        `<div class="val">${showVal(p(n, 'value', ''))}</div>` +
        (p(n, 'delta', '') ? `<div class="delta">${showVal(p(n, 'delta', ''))}</div>` : '') + `</div>` },

    // ── Navegação ───────────────────────────────────────────────────────────
    Navbar: { group: 'Navegação', label: 'Navbar', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<nav class="wk-nav">${i}</nav>` },
    Link: { group: 'Navegação', label: 'Link', container: false, defaultProps: { text: 'Link', href: '/' },
      fields: [F.text, { key: 'href', label: 'Rota (href)', kind: 'text' }, F.className],
      preview: (n) => `<a class="wk-link">${showVal(p(n, 'text', ''))}</a>` },
    Sidebar: { group: 'Navegação', label: 'Sidebar', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<aside class="wk-sidebar">${i}</aside>` },
    Tabs: { group: 'Navegação', label: 'Tabs', container: true, defaultProps: {},
      fields: [F.className], preview: (n, i) => `<div class="wk-tabs">${i}</div>` },

    // ── Formulários ─────────────────────────────────────────────────────────
    Button: { group: 'Formulários', label: 'Button', container: false, defaultProps: { text: 'Botão', variant: 'primary' },
      fields: [F.text, { key: 'variant', label: 'Variante', kind: 'enum', options: ['primary', 'secondary', 'danger'] }, { key: 'onClick', label: 'onClick', kind: 'event' }, F.className],
      preview: (n) => `<button class="wk-btn wk-btn-${p(n, 'variant', 'primary')}">${showVal(p(n, 'text', ''))}</button>` },
    TextInput: { group: 'Formulários', label: 'TextInput', container: false, defaultProps: { placeholder: 'Digite…' },
      fields: [{ key: 'placeholder', label: 'Placeholder', kind: 'text' }, { key: 'value', label: 'Valor', kind: 'text', bindable: true },
        { key: 'onChange', label: 'onChange', kind: 'event' }, { key: 'onSubmit', label: 'onSubmit', kind: 'event' }, F.className],
      preview: (n) => `<div class="wk-input">${p(n, 'value', '') ? showVal(p(n, 'value', '')) : `<span class="ph">${showVal(p(n, 'placeholder', '') || '')}</span>`}</div>` },
    TextArea: { group: 'Formulários', label: 'TextArea', container: false, defaultProps: { placeholder: 'Digite…', rows: 3 },
      fields: [{ key: 'placeholder', label: 'Placeholder', kind: 'text' }, { key: 'rows', label: 'Linhas', kind: 'number', min: 1, max: 20 },
        { key: 'value', label: 'Valor', kind: 'text', bindable: true }, { key: 'onChange', label: 'onChange', kind: 'event' }, F.className],
      preview: (n) => `<div class="wk-input wk-textarea" style="min-height:${(p(n, 'rows', 3)) * 17 + 12}px">${p(n, 'value', '') ? showVal(p(n, 'value', '')) : `<span class="ph">${showVal(p(n, 'placeholder', '') || '')}</span>`}</div>` },
    Select: { group: 'Formulários', label: 'Select', container: false, defaultProps: { options: ['Opção 1', 'Opção 2'] },
      fields: [{ key: 'options', label: 'Opções (separadas por vírgula)', kind: 'csv', bindable: true }, { key: 'onChange', label: 'onChange', kind: 'event' }, F.className],
      preview: (n) => `<div class="wk-select">${isFx(p(n, 'options')) ? fxChip(p(n, 'options')) : esc((asArr(p(n, 'options', []))[0]) || 'Selecione…')}<span class="chev">▾</span></div>` },
    Checkbox: { group: 'Formulários', label: 'Checkbox', container: false, defaultProps: { label: 'Aceito os termos', checked: false },
      fields: [{ key: 'label', label: 'Rótulo', kind: 'text' }, { key: 'checked', label: 'Marcado', kind: 'bool' }, { key: 'onChange', label: 'onChange', kind: 'event' }, F.className],
      preview: (n) => `<label class="wk-checkbox"><span class="cbox ${p(n, 'checked', false) ? 'on' : ''}"></span>${showVal(p(n, 'label', ''))}</label>` },
    Form: { group: 'Formulários', label: 'Form', container: true, defaultProps: {},
      fields: [{ key: 'onSubmit', label: 'onSubmit', kind: 'event' }, F.className],
      preview: (n, i) => `<form class="wk-form">${i}</form>` },

    // ── Dados ────────────────────────────────────────────────────────────────
    Table: { group: 'Dados', label: 'Table', container: false,
      defaultProps: { columns: ['Coluna A', 'Coluna B'], rows: [['a1', 'b1'], ['a2', 'b2']] },
      fields: [{ key: 'columns', label: 'Colunas (vírgula)', kind: 'csv', bindable: true },
        { key: 'rows', label: 'Linhas (uma por linha; células por vírgula)', kind: 'rows', bindable: true }, F.className],
      preview: (n) => tablePreview(n) },
    List: { group: 'Dados', label: 'List', container: false, defaultProps: { items: ['Item 1', 'Item 2', 'Item 3'] },
      fields: [{ key: 'items', label: 'Itens (vírgula)', kind: 'csv', bindable: true }, { key: 'ordered', label: 'Numerada', kind: 'bool' }, F.className],
      preview: (n) => { if (isFx(p(n, 'items'))) return `<div class="wk-list">${fxChip(p(n, 'items'))}</div>`; const t = p(n, 'ordered', false) ? 'ol' : 'ul'; return `<${t} class="wk-list">` + asArr(p(n, 'items', [])).map((it) => `<li>${esc(it)}</li>`).join('') + `</${t}>`; } },
    Chart: { group: 'Dados', label: 'Chart', container: false,
      defaultProps: { type: 'bar', data: { labels: ['Jan', 'Fev', 'Mar'], datasets: [{ label: 'Série', data: [10, 20, 15] }] } },
      fields: [{ key: 'chart', label: '', kind: 'chart', bindable: true }],
      preview: (n) => chartPreview(n) },
    Alert: { group: 'Dados', label: 'Alert', container: false, defaultProps: { variant: 'info', text: 'Mensagem informativa.' },
      fields: [F.text, { key: 'variant', label: 'Variante', kind: 'enum', options: ['info', 'success', 'warning', 'error'] }, F.className],
      preview: (n) => `<div class="wk-alert wk-alert-${p(n, 'variant', 'info')}">${showVal(p(n, 'text', ''))}</div>` },
    ProgressBar: { group: 'Dados', label: 'ProgressBar', container: false, defaultProps: { value: 60 },
      fields: [{ key: 'value', label: 'Valor (0–100)', kind: 'number', min: 0, max: 100, bindable: true }, F.className],
      preview: (n) => { const rv = p(n, 'value', 0); if (isFx(rv)) return `<div class="wk-progress fx">${fxChip(rv)}</div>`; return `<div class="wk-progress"><div class="fill" style="width:${Math.max(0, Math.min(100, Number(rv) || 0))}%"></div></div>`; } },
    Spinner: { group: 'Dados', label: 'Spinner', container: false, defaultProps: {},
      fields: [F.className], preview: () => `<div class="wk-spinner"></div>` },
  };

  const GROUPS = ['Layout', 'Tipografia', 'Navegação', 'Formulários', 'Dados'];

  global.WebInkWidgets = {
    defs: W,
    groups: GROUPS,
    isContainer: (t) => !!(W[t] && W[t].container),
    def: (t) => W[t],
    esc,
    scaffoldRoot: () => ({ type: 'Page', props: {}, children: [] }),
  };
})(typeof window !== 'undefined' ? window : globalThis);
