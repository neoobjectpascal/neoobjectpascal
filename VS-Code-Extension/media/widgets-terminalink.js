// widgets-terminalink.js — TerminalInk widget registry for the visual builder.
// Same shape as the WebInk registry; previews use the .tk-* classes in
// preview-terminal.css (a monospace terminal approximation).
(function (global) {
  const esc = (s) => String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  const p = (n, k, d) => (n.props && n.props[k] !== undefined ? n.props[k] : d);
  const showVal = (v) => (typeof v === 'string' && v.startsWith('=') ? v.slice(1) : esc(v));
  const isFx = (v) => typeof v === 'string' && v.startsWith('=');
  const asArr = (v) => (Array.isArray(v) ? v : []);
  const fxChip = (v) => `<span class="fx-chip" title="ligado a variável/expressão">fx ${esc(String(v).slice(1)) || '…'}</span>`;

  const PAL = { red: '#f14c4c', green: '#23d18b', yellow: '#e5c07b', blue: '#5b9bd5', magenta: '#c586c0',
    cyan: '#4ec9d4', white: '#d4d4d4', black: '#1a1a1a', gray: '#7d8b99', grey: '#7d8b99', 'default': '#c8ccd4' };
  const col = (name) => PAL[name] || PAL['default'];
  const CLR = ['default', 'red', 'green', 'yellow', 'blue', 'magenta', 'cyan', 'white', 'gray'];

  function tkBox(n, inner, dir) {
    const gap = p(n, 'gap', 0), pad = p(n, 'padding', 0);
    const border = p(n, 'border', null), bc = col(p(n, 'borderColor', 'default'));
    const aMap = { start: 'flex-start', center: 'center', end: 'flex-end' };
    const jMap = { start: 'flex-start', center: 'center', end: 'flex-end', 'space-between': 'space-between' };
    let s = `display:flex;flex-direction:${dir};gap:${gap * 8}px;padding:${pad * 8}px;` +
      `align-items:${aMap[p(n, 'alignItems', 'start')] || 'flex-start'};` +
      `justify-content:${jMap[p(n, 'justifyContent', 'start')] || 'flex-start'};`;
    if (border) s += `border:1px solid ${bc};border-radius:${border === 'round' ? '8px' : '2px'};`;
    // Note: inner is '' here — buildNode appends real children as DOM nodes after.
    // The empty-state hint is handled by `.tk-box:empty` in CSS (never duplicates).
    return `<div class="tk-box" style="${s}">${inner}</div>`;
  }
  function textStyle(n) {
    let s = `color:${col(p(n, 'color', 'default'))};`;
    if (p(n, 'bold', false)) s += 'font-weight:700;';
    if (p(n, 'dim', false)) s += 'opacity:.55;';
    const bg = p(n, 'backgroundColor', p(n, 'bg', null));
    if (bg) s += `background:${col(bg)};padding:0 4px;`;
    if (p(n, 'inverse', false)) s += `background:${col(p(n, 'color', 'default'))};color:#0d1220;padding:0 4px;`;
    return s;
  }
  const ST = { info: ['ℹ', 'blue'], success: ['✔', 'green'], error: ['✖', 'red'], warning: ['⚠', 'yellow'] };

  const boxFields = [
    { key: 'border', label: 'Borda', kind: 'border' },
    { key: 'borderColor', label: 'Cor da borda', kind: 'enum', options: CLR },
    { key: 'padding', label: 'Padding', kind: 'number', min: 0, max: 10 },
    { key: 'gap', label: 'Espaço entre filhos (gap)', kind: 'number', min: 0, max: 10 },
    { key: 'alignItems', label: 'Alinhar (eixo cruzado)', kind: 'enum', options: ['start', 'center', 'end'] },
    { key: 'justifyContent', label: 'Distribuir (eixo principal)', kind: 'enum', options: ['start', 'center', 'end', 'space-between'] },
  ];
  const evChange = { key: 'onChange', label: 'onChange', kind: 'event' };
  const evSubmit = { key: 'onSubmit', label: 'onSubmit', kind: 'event' };
  const phField = { key: 'placeholder', label: 'Placeholder', kind: 'text' };
  const valField = { key: 'value', label: 'Valor inicial', kind: 'text', bindable: true };
  // Foco: `key` dá um identificador estável ao componente (usado por focus("chave")
  // e como chave de estado); `autoFocus` põe o foco nele já no primeiro frame.
  const keyField = { key: 'key', label: 'Chave (para foco / estado)', kind: 'text', ph: 'ex.: nome' };
  const autoFocusField = { key: 'autoFocus', label: 'Foco inicial (autoFocus)', kind: 'bool' };
  const focusFields = [keyField, autoFocusField];

  const W = {
    // ── Layout ────────────────────────────────────────────────────────────────
    VBox: { group: 'Layout', label: 'VBox', container: true, defaultProps: { padding: 1, gap: 1 },
      fields: boxFields, preview: (n, i) => tkBox(n, i, 'column') },
    HBox: { group: 'Layout', label: 'HBox', container: true, defaultProps: { gap: 2 },
      fields: boxFields, preview: (n, i) => tkBox(n, i, 'row') },
    Box: { group: 'Layout', label: 'Box', container: true, defaultProps: {},
      fields: [{ key: 'flexDirection', label: 'Direção', kind: 'enum', options: ['row', 'column'] }].concat(boxFields),
      preview: (n, i) => tkBox(n, i, p(n, 'flexDirection', 'row')) },
    Spacer: { group: 'Layout', label: 'Spacer', container: false, defaultProps: {},
      fields: [], preview: () => `<div class="tk-spacer"></div>` },

    // ── Texto ─────────────────────────────────────────────────────────────────
    Text: { group: 'Texto', label: 'Text', container: false, defaultProps: { text: 'Texto' },
      fields: [{ key: 'text', label: 'Texto', kind: 'text' }, { key: 'color', label: 'Cor', kind: 'enum', options: CLR },
        { key: 'bold', label: 'Negrito', kind: 'bool' }, { key: 'dim', label: 'Esmaecido (dim)', kind: 'bool' }, { key: 'inverse', label: 'Invertido', kind: 'bool' }],
      preview: (n) => `<span class="tk-text" style="${textStyle(n)}">${showVal(p(n, 'text', '')) || '&nbsp;'}</span>` },
    Badge: { group: 'Texto', label: 'Badge', container: false, defaultProps: { text: 'ok', color: 'magenta' },
      fields: [{ key: 'text', label: 'Texto', kind: 'text' }, { key: 'color', label: 'Cor', kind: 'enum', options: CLR }],
      preview: (n) => `<span class="tk-badge" style="background:${col(p(n, 'color', 'magenta'))}">${esc(String(showVal(p(n, 'text', ''))).toUpperCase())}</span>` },

    // ── Entrada ───────────────────────────────────────────────────────────────
    TextInput: { group: 'Entrada', label: 'TextInput', container: false, defaultProps: { placeholder: 'Digite…' },
      fields: [phField, valField].concat(focusFields, [evChange, evSubmit]),
      preview: (n) => tkInput(p(n, 'value', p(n, 'defaultValue', '')), p(n, 'placeholder', '')) },
    PasswordInput: { group: 'Entrada', label: 'PasswordInput', container: false, defaultProps: { placeholder: 'Senha…' },
      fields: [phField, valField].concat(focusFields, [evChange, evSubmit]),
      preview: (n) => { const v = String(showVal(p(n, 'value', p(n, 'defaultValue', '')))); return tkInput(v ? '*'.repeat(v.length) : '', p(n, 'placeholder', '')); } },
    EmailInput: { group: 'Entrada', label: 'EmailInput', container: false, defaultProps: { placeholder: 'email…' },
      fields: [phField, valField, { key: 'domains', label: 'Domínios (vírgula)', kind: 'csv' }].concat(focusFields, [evChange, evSubmit]),
      preview: (n) => tkInput(p(n, 'value', p(n, 'defaultValue', '')), p(n, 'placeholder', '')) },
    ConfirmInput: { group: 'Entrada', label: 'ConfirmInput', container: false, defaultProps: { defaultChoice: 'confirm', submitOnEnter: true },
      fields: [{ key: 'defaultChoice', label: 'Padrão', kind: 'enum', options: ['confirm', 'cancel'] }, { key: 'submitOnEnter', label: 'Enter confirma', kind: 'bool' }]
        .concat(focusFields, [{ key: 'onConfirm', label: 'onConfirm', kind: 'event' }, { key: 'onCancel', label: 'onCancel', kind: 'event' }]),
      preview: (n) => `<span class="tk-text">Confirmar? <span class="dim">(${p(n, 'defaultChoice', 'confirm') === 'cancel' ? 'y/N' : 'Y/n'})</span></span>` },
    Select: { group: 'Entrada', label: 'Select', container: false, defaultProps: { options: [{ label: 'Opção A', value: 'a' }, { label: 'Opção B', value: 'b' }] },
      fields: [{ key: 'options', label: 'Opções (uma por linha; "rótulo = valor")', kind: 'options', bindable: true }, { key: 'visibleCount', label: 'Linhas visíveis', kind: 'number', min: 1, max: 12 }].concat(focusFields, [evChange]),
      preview: (n) => tkSelect(n, false) },
    MultiSelect: { group: 'Entrada', label: 'MultiSelect', container: false, defaultProps: { options: [{ label: 'Java', value: 'java' }, { label: 'Pascal', value: 'pascal' }] },
      fields: [{ key: 'options', label: 'Opções (uma por linha; "rótulo = valor")', kind: 'options', bindable: true }, { key: 'visibleCount', label: 'Linhas visíveis', kind: 'number', min: 1, max: 12 }].concat(focusFields, [evChange, evSubmit]),
      preview: (n) => tkSelect(n, true) },

    // ── Feedback ──────────────────────────────────────────────────────────────
    Spinner: { group: 'Feedback', label: 'Spinner', container: false, defaultProps: { label: 'Carregando…', color: 'blue' },
      fields: [{ key: 'label', label: 'Rótulo', kind: 'text' }, { key: 'color', label: 'Cor', kind: 'enum', options: CLR }],
      preview: (n) => `<span class="tk-text" style="color:${col(p(n, 'color', 'blue'))}"><span class="tk-spin">⠋</span> ${showVal(p(n, 'label', ''))}</span>` },
    ProgressBar: { group: 'Feedback', label: 'ProgressBar', container: false, defaultProps: { value: 60 },
      fields: [{ key: 'value', label: 'Valor (0–100)', kind: 'number', min: 0, max: 100, bindable: true }],
      preview: (n) => { const rv = p(n, 'value', 0); if (isFx(rv)) return `<span class="tk-progress">${fxChip(rv)}</span>`; const v = Math.max(0, Math.min(100, Number(rv) || 0)); const t = 22, f = Math.round(v / 100 * t); return `<span class="tk-progress"><span class="fill">${'◼'.repeat(f)}</span><span class="empty">${'░'.repeat(t - f)}</span></span>`; } },
    StatusMessage: { group: 'Feedback', label: 'StatusMessage', container: false, defaultProps: { variant: 'info', text: 'Mensagem' },
      fields: [{ key: 'text', label: 'Texto', kind: 'text' }, { key: 'variant', label: 'Variante', kind: 'enum', options: ['info', 'success', 'error', 'warning'] }],
      preview: (n) => { const s = ST[p(n, 'variant', 'info')] || ST.info; return `<span class="tk-text" style="color:${col(s[1])}">${s[0]} ${showVal(p(n, 'text', ''))}</span>`; } },
    Alert: { group: 'Feedback', label: 'Alert', container: false, defaultProps: { variant: 'info', title: '', text: 'Detalhes do alerta.' },
      fields: [{ key: 'title', label: 'Título', kind: 'text' }, { key: 'text', label: 'Mensagem', kind: 'text' }, { key: 'variant', label: 'Variante', kind: 'enum', options: ['info', 'success', 'error', 'warning'] }],
      preview: (n) => { const s = ST[p(n, 'variant', 'info')] || ST.info; const c = col(s[1]); const title = showVal(p(n, 'title', '')); return `<div class="tk-alert" style="border-color:${c}"><span style="color:${c}">${s[0]}</span> ${title ? `<b style="color:${c}">${title}</b> ` : ''}${showVal(p(n, 'text', ''))}</div>`; } },

    // ── Listas ────────────────────────────────────────────────────────────────
    UnorderedList: { group: 'Listas', label: 'UnorderedList', container: false, defaultProps: { items: ['Primeiro', 'Segundo'], marker: '─' },
      fields: [{ key: 'items', label: 'Itens (vírgula)', kind: 'csv', bindable: true }, { key: 'marker', label: 'Marcador', kind: 'text' }],
      preview: (n) => { if (isFx(p(n, 'items'))) return `<div class="tk-list">${fxChip(p(n, 'items'))}</div>`; return `<div class="tk-list">` + asArr(p(n, 'items', [])).map((it) => `<div><span class="dim">${esc(p(n, 'marker', '─'))}</span> ${esc(it)}</div>`).join('') + `</div>`; } },
    OrderedList: { group: 'Listas', label: 'OrderedList', container: false, defaultProps: { items: ['Primeiro', 'Segundo'], marker: '.' },
      fields: [{ key: 'items', label: 'Itens (vírgula)', kind: 'csv', bindable: true }, { key: 'marker', label: 'Sufixo do número', kind: 'text' }],
      preview: (n) => { if (isFx(p(n, 'items'))) return `<div class="tk-list">${fxChip(p(n, 'items'))}</div>`; return `<div class="tk-list">` + asArr(p(n, 'items', [])).map((it, i) => `<div><span class="dim">${i + 1}${esc(p(n, 'marker', '.'))}</span> ${esc(it)}</div>`).join('') + `</div>`; } },
  };

  function tkInput(value, ph) {
    const v = showVal(value);
    return `<span class="tk-input">${v ? v : `<span class="dim">${esc(ph || '')}</span>`}<span class="tk-cur"></span></span>`;
  }
  function tkSelect(n, multi) {
    if (isFx(p(n, 'options'))) return `<div class="tk-select">${fxChip(p(n, 'options'))}</div>`;
    const opts = asArr(p(n, 'options', []));
    const vis = p(n, 'visibleCount', 5);
    const rows = opts.slice(0, vis).map((o, i) => {
      const label = (o && o.label != null) ? o.label : (o && o.value != null ? o.value : o);
      const ptr = i === 0 ? '<span class="ptr">❯</span>' : '<span class="ptr"> </span>';
      const box = multi ? `<span class="chk">${i === 0 ? '◉' : '○'}</span> ` : '';
      return `<div class="tk-opt ${i === 0 ? 'on' : ''}">${ptr} ${box}${esc(label)}</div>`;
    }).join('');
    return `<div class="tk-select">${rows || '<div class="dim">sem opções</div>'}</div>`;
  }

  const GROUPS = ['Layout', 'Texto', 'Entrada', 'Feedback', 'Listas'];

  global.TerminalInkWidgets = {
    defs: W,
    groups: GROUPS,
    isContainer: (t) => !!(W[t] && W[t].container),
    def: (t) => W[t],
    esc,
    scaffoldRoot: () => ({ type: 'VBox', props: { padding: 1, gap: 1 }, children: [] }),
  };
})(typeof window !== 'undefined' ? window : globalThis);
