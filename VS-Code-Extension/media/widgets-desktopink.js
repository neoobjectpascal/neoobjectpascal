// widgets-desktopink.js — DesktopInk widget registry for the visual builder.
// The preview mirrors DesktopInk's dark Swing controls while generated nodes map
// directly to the DesktopInk widget vocabulary.
(function (global) {
  const esc = (s) => String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  const p = (n, k, d) => (n.props && n.props[k] !== undefined ? n.props[k] : d);
  const show = (v) => typeof v === 'string' && v.startsWith('=') ? esc(v.slice(1)) : esc(v);
  const tablePreview = (n) => {
    const columns = p(n, 'columns', []) || [];
    const rows = p(n, 'rows', []);
    const body = typeof rows === 'string' && rows.startsWith('=')
      ? `<tr><td colspan="${Math.max(1, columns.length)}" class="dk-fx">fx ${show(rows)}</td></tr>`
      : (Array.isArray(rows) ? rows : []).map((row, index) =>
        `<tr class="${index === 0 && p(n, 'selectedRow', null) !== undefined ? 'selected' : ''}">` +
        `${(Array.isArray(row) ? row : []).map((cell) => `<td>${show(cell)}</td>`).join('')}</tr>`).join('');
    return `<div class="dk-tablewrap"><table class="dk-table"><thead><tr>` +
      `${columns.map((column) => `<th>${show(column)}</th>`).join('')}</tr></thead><tbody>${body}</tbody></table></div>`;
  };
  const F = { text: { key: 'text', label: 'Texto', kind: 'text' } };
  const event = { key: 'onClick', label: 'onClick', kind: 'event' };
  const W = {
    Window: { group: 'Layout', label: 'Window', container: true, defaultProps: {}, fields: [], preview: (n, i) => `<div class="dk-window">${i}</div>` },
    Container: { group: 'Layout', label: 'Container', container: true, defaultProps: {}, fields: [], preview: (n, i) => `<div class="dk-container">${i}</div>` },
    Sidebar: { group: 'Layout', label: 'Sidebar', container: true, defaultProps: { width: 220 }, fields: [{ key: 'width', label: 'Largura', kind: 'number', min: 100, max: 400 }], preview: (n, i) => `<aside class="dk-sidebar" style="width:${p(n, 'width', 220)}px">${i}</aside>` },
    Section: { group: 'Layout', label: 'Section', container: true, defaultProps: {}, fields: [], preview: (n, i) => `<section class="dk-section">${i}</section>` },
    Grid: { group: 'Layout', label: 'Grid', container: true, defaultProps: { columns: 2, gap: 12 }, fields: [{ key: 'columns', label: 'Colunas', kind: 'number', min: 1, max: 12 }, { key: 'gap', label: 'Espaço entre filhos (gap)', kind: 'number', min: 0, max: 40 }], preview: (n, i) => `<div class="dk-grid" style="grid-template-columns:repeat(${p(n, 'columns', p(n, 'cols', 2))},minmax(0,1fr));gap:${p(n, 'gap', 12)}px">${i}</div>` },
    Row: { group: 'Layout', label: 'Row', container: true, defaultProps: { gap: 8 }, fields: [{ key: 'gap', label: 'Espaço entre filhos (gap)', kind: 'number', min: 0, max: 40 }], preview: (n, i) => `<div class="dk-row" style="gap:${p(n, 'gap', 16)}px">${i}</div>` },
    Col: { group: 'Layout', label: 'Col', container: true, defaultProps: {}, fields: [], preview: (n, i) => `<div class="dk-col">${i}</div>` },
    Card: { group: 'Layout', label: 'Card', container: true, defaultProps: {}, fields: [], preview: (n, i) => `<div class="dk-card">${i}</div>` },
    Modal: { group: 'Layout', label: 'Modal', container: true, defaultProps: { title: 'Modal' }, fields: [{ key: 'title', label: 'Título', kind: 'text' }], preview: (n, i) => `<div class="dk-modal"><b>${show(p(n, 'title', 'Modal'))}</b>${i}</div>` },
    Divider: { group: 'Layout', label: 'Divider', container: false, defaultProps: {}, fields: [], preview: () => '<hr class="dk-divider">' },
    Spacer: { group: 'Layout', label: 'Spacer', container: false, defaultProps: {}, fields: [], preview: () => '<div class="dk-spacer"></div>' },
    Heading: { group: 'Tipografia', label: 'Heading', container: false, defaultProps: { level: 1, text: 'Título' }, fields: [{ key: 'level', label: 'Nível', kind: 'enum', options: [1, 2, 3] }, F.text], preview: (n) => `<div class="dk-h dk-h${p(n, 'level', 1)}">${show(p(n, 'text', ''))}</div>` },
    Text: { group: 'Tipografia', label: 'Text', container: false, defaultProps: { text: 'Texto' }, fields: [F.text], preview: (n) => `<p class="dk-text">${show(p(n, 'text', ''))}</p>` },
    Badge: { group: 'Tipografia', label: 'Badge', container: false, defaultProps: { text: 'novo' }, fields: [F.text], preview: (n) => `<span class="dk-badge">${show(p(n, 'text', ''))}</span>` },
    StatCard: { group: 'Dados', label: 'StatCard', container: false, defaultProps: { label: 'Métrica', value: '0' }, fields: [{ key: 'label', label: 'Rótulo', kind: 'text' }, { key: 'value', label: 'Valor', kind: 'text', bindable: true }], preview: (n) => `<div class="wk-stat"><div class="lbl">${show(p(n, 'label', ''))}</div><div class="val">${show(p(n, 'value', ''))}</div></div>` },
    Alert: { group: 'Dados', label: 'Alert', container: false, defaultProps: { text: 'Mensagem', variant: 'info' }, fields: [F.text, { key: 'variant', label: 'Variante', kind: 'enum', options: ['info', 'error'] }], preview: (n) => `<div class="wk-alert">${show(p(n, 'text', ''))}</div>` },
    Button: { group: 'Formulários', label: 'Button', container: false, defaultProps: { text: 'Botão' }, fields: [F.text, event], preview: (n) => `<button class="dk-btn ${p(n, 'variant', 'primary') === 'secondary' ? 'dk-btn-secondary' : ''}">${show(p(n, 'text', ''))}</button>` },
    TextInput: { group: 'Formulários', label: 'TextInput', container: false, defaultProps: { placeholder: 'Digite...' }, fields: [{ key: 'placeholder', label: 'Placeholder', kind: 'text' }, { key: 'value', label: 'Valor', kind: 'text', bindable: true }], preview: (n) => `<div class="wk-input"><span class="ph">${show(p(n, 'placeholder', ''))}</span></div>` },
    PasswordInput: { group: 'Formulários', label: 'PasswordInput', container: false, defaultProps: { placeholder: 'Senha...' }, fields: [{ key: 'placeholder', label: 'Placeholder', kind: 'text' }], preview: (n) => `<div class="wk-input"><span class="ph">${show(p(n, 'placeholder', ''))}</span></div>` },
    TextArea: { group: 'Formulários', label: 'TextArea', container: false, defaultProps: { placeholder: 'Digite...' }, fields: [{ key: 'placeholder', label: 'Placeholder', kind: 'text' }], preview: (n) => `<div class="wk-input wk-textarea"><span class="ph">${show(p(n, 'placeholder', ''))}</span></div>` },
    Select: { group: 'Formulários', label: 'Select', container: false, defaultProps: { options: ['Opção 1'] }, fields: [{ key: 'options', label: 'Opções (separadas por vírgula)', kind: 'csv', bindable: true }], preview: (n) => `<div class="wk-select">${show((p(n, 'options', [])[0]) || 'Selecione...')}</div>` },
    Checkbox: { group: 'Formulários', label: 'Checkbox', container: false, defaultProps: { label: 'Aceito', checked: false }, fields: [{ key: 'label', label: 'Rótulo', kind: 'text' }, { key: 'checked', label: 'Marcado', kind: 'bool' }, { key: 'onChange', label: 'onChange', kind: 'event' }], preview: (n) => `<label class="wk-checkbox"><span class="cbox"></span>${show(p(n, 'label', ''))}</label>` },
    Form: { group: 'Formulários', label: 'Form', container: true, defaultProps: {}, fields: [], preview: (n, i) => `<form class="wk-form">${i}</form>` },
    Table: { group: 'Dados', label: 'Table', container: false, defaultProps: { columns: ['Coluna'], rows: [['valor']] }, fields: [{ key: 'columns', label: 'Colunas (vírgula)', kind: 'csv' }, { key: 'rows', label: 'Linhas (uma por linha; células por vírgula)', kind: 'rows' }], preview: tablePreview },
    List: { group: 'Dados', label: 'List', container: false, defaultProps: { items: ['Item'] }, fields: [{ key: 'items', label: 'Itens (vírgula)', kind: 'csv' }], preview: (n) => `<ul class="wk-list">${(p(n, 'items', []) || []).map((x) => `<li>${show(x)}</li>`).join('')}</ul>` },
    ProgressBar: { group: 'Dados', label: 'ProgressBar', container: false, defaultProps: { value: 60 }, fields: [{ key: 'value', label: 'Valor (0–100)', kind: 'number', min: 0, max: 100, bindable: true }], preview: () => '<div class="wk-progress"><div class="fill" style="width:60%"></div></div>' },
    Spinner: { group: 'Dados', label: 'Spinner', container: false, defaultProps: { label: 'Carregando...' }, fields: [{ key: 'label', label: 'Rótulo', kind: 'text' }], preview: (n) => `<div class="wk-spinner">${show(p(n, 'label', ''))}</div>` },
  };
  global.DesktopInkWidgets = { defs: W, groups: ['Layout', 'Tipografia', 'Formulários', 'Dados'], isContainer: (t) => !!(W[t] && W[t].container), def: (t) => W[t], esc, scaffoldRoot: () => ({ type: 'Window', props: {}, children: [] }) };
})(typeof window !== 'undefined' ? window : globalThis);
