// builder.js — NeoObjectPascal UI Builder webview app (vanilla JS).
// Model = the .xnpas JSON. Every change is serialized and posted to the
// extension, which applies a WorkspaceEdit (so undo/redo/save work natively).
/* global acquireVsCodeApi, NpIcons, NpI18n, WebInkWidgets */
(function () {
  'use strict';
  const vscode = acquireVsCodeApi();
  const TARGETS = { webink: WebInkWidgets, terminalink: (typeof TerminalInkWidgets !== 'undefined' ? TerminalInkWidgets : WebInkWidgets) };
  let W = WebInkWidgets;   // active widget registry (switches with model.target)
  const icon = (n, o) => NpIcons.get(n, o);

  // ── i18n: the editor UI language (set by the extension host; user-selectable) ──
  let LANG = 'pt';
  const t = (s) => NpI18n.tr(s, LANG);
  const LANG_NAMES = { pt: 'Português', en: 'English', de: 'Deutsch', fr: 'Français', it: 'Italiano' };

  let model = scaffold();
  let sel = null;        // selected node id
  let tab = 'props';     // props | state | events
  let screenIdx = 0;
  let applyingRemote = false;
  // Texts we posted to the extension. The extension echoes each change back as
  // an 'init'; we must ignore our own echoes, otherwise a full re-render on every
  // keystroke destroys the focused input. Only genuinely external changes (undo/
  // redo, edits from elsewhere) should trigger a re-render.
  const pending = new Set();
  // Per node+event: user forced "Código" mode (free NeoObjectPascal). Without this
  // the mode was inferred from the handler body, so a body matching a no-code
  // pattern could never be switched to the code editor.
  const forceCode = {};
  // Per node+prop: user toggled fx (bind to a variable/expression) on/off. Undefined
  // → infer from the value (a "=expr" string is bound). Same idea for the `visible`
  // condition control (key = node.id + '/visible').
  const fxMode = {};
  // A prop value bound to a variable/expression is stored as a "=..." string.
  const isBound = (v) => typeof v === 'string' && v.startsWith('=');
  function fxState(node, pk) {
    const fkey = node.id + '/' + pk;
    return fxMode[fkey] === true || (isBound(node.props[pk]) && fxMode[fkey] !== false);
  }

  function scaffold() {
    return {
      xnpas: 1, target: 'webink', state: [], handlers: [],
      screens: [{ route: '/', name: 'home', root: { type: 'Page', props: {}, children: [] } }],
    };
  }

  // ── model helpers ─────────────────────────────────────────────────────────
  let idSeq = 1;
  function ensureIds(node) {
    if (!node.id) node.id = 'n' + (idSeq++);
    else { const m = /^n(\d+)$/.exec(node.id); if (m) idSeq = Math.max(idSeq, +m[1] + 1); }
    (node.children || []).forEach(ensureIds);
  }
  function curScreen() { return model.screens[screenIdx] || model.screens[0]; }
  function find(node, id, parent) {
    if (!node) return null;
    if (node.id === id) return { node, parent };
    for (const c of (node.children || [])) { const r = find(c, id, node); if (r) return r; }
    return null;
  }
  function selected() { const s = curScreen(); return sel ? find(s.root, sel) : null; }

  function push() {
    if (applyingRemote) return;
    pruneHandlers();
    const text = JSON.stringify(model, null, 2);
    pending.add(text);
    vscode.postMessage({ type: 'update', text });
  }
  const EVENT_PROPS = ['onClick', 'onChange', 'onSubmit', 'onConfirm', 'onCancel'];
  function pruneHandlers() {
    const used = new Set();
    const walk = (n) => {
      for (const k of EVENT_PROPS) {
        const v = n.props && n.props[k];
        if (typeof v === 'string' && v.startsWith('@')) used.add(v.slice(1));
      }
      (n.children || []).forEach(walk);
    };
    model.screens.forEach((s) => walk(s.root));
    model.handlers = (model.handlers || []).filter((h) => used.has(h.name));
  }

  // ── render everything ───────────────────────────────────────────────────
  function render() { applyShellI18n(); renderToolbar(); renderPalette(); renderCanvas(); renderInspector(); }

  // Translate the static shell chrome (rendered once in the HTML, not by a render fn).
  function applyShellI18n() {
    const bar = el('.canvas-bar');
    if (bar) bar.textContent = t('Canvas — pré-visualização real · arraste da paleta para inserir · clique para selecionar');
    const tabs = { props: 'Propriedades', state: 'Estado', events: 'Eventos' };
    document.querySelectorAll('#itabs button').forEach((b) => {
      const k = tabs[b.dataset.tab]; if (k) b.textContent = t(k);
    });
  }

  function renderToolbar() {
    const tb = el('#toolbar');
    const screens = model.screens.map((s, i) =>
      `<option value="${i}" ${i === screenIdx ? 'selected' : ''}>${s.route ? esc(s.route) + ' · ' : ''}${esc(s.name)}</option>`).join('');
    const term = model.target === 'terminalink';
    const langOpts = NpI18n.langs.map((l) => `<option value="${l}" ${l === LANG ? 'selected' : ''}>${esc(LANG_NAMES[l] || l)}</option>`).join('');
    tb.innerHTML =
      `<div class="seg">
         <button id="tgWeb" class="${term ? '' : 'active'}">WebInk</button>
         <button id="tgTerm" class="${term ? 'active' : ''}">TerminalInk</button>
       </div>
       <div class="route">${icon('route')}<select id="routeSel">${screens}</select></div>
       <button class="tbtn icon" id="addScreen" title="${esc(term ? t('Nova tela') : t('Nova rota/tela'))}">${icon('plus')}</button>
       <div class="spring"></div>
       <div class="lang" title="${esc(t('Idioma'))}">${icon('globe', { s: 14 })}<select id="langSel">${langOpts}</select></div>
       <div class="sync">${icon('check', { s: 14 })} ${esc(t('sincroniza com o .npas ao salvar'))}</div>
       <button class="tbtn icon" id="undo" title="${esc(t('Desfazer'))}">${icon('undo')}</button>
       <button class="tbtn icon" id="redo" title="${esc(t('Refazer'))}">${icon('redo')}</button>
       <button class="tbtn run" id="run">${icon('play', { s: 14 })} ${esc(t('Rodar ao vivo'))}</button>`;
    el('#tgWeb').onclick = () => setTarget('webink');
    el('#tgTerm').onclick = () => setTarget('terminalink');
    el('#routeSel').onchange = (e) => { screenIdx = +e.target.value; sel = null; render(); };
    el('#addScreen').onclick = addScreen;
    el('#langSel').onchange = (e) => setLang(e.target.value);
    el('#undo').onclick = () => vscode.postMessage({ type: 'undo' });
    el('#redo').onclick = () => vscode.postMessage({ type: 'redo' });
    el('#run').onclick = () => vscode.postMessage({ type: 'run' });
  }

  function renderPalette() {
    // Render the search box ONCE; filtering only re-renders the chip list below
    // it, so the search input keeps focus while typing.
    const p = el('#palette');
    p.innerHTML = `<div class="search">${icon('gear', { s: 13 })}<input id="psearch" placeholder="${esc(t('Buscar componente…'))}"></div><div id="pgroups"></div>`;
    const s = el('#psearch');
    s.value = window.__psearch || '';
    s.oninput = (e) => { window.__psearch = e.target.value; renderPaletteGroups(); };
    renderPaletteGroups();
  }
  function renderPaletteGroups() {
    const q = (window.__psearch || '').toLowerCase();
    const groupIcon = { Layout: 'box', 'Tipografia': 'type', 'Navegação': 'nav', 'Formulários': 'form', 'Dados': 'data' };
    let html = '';
    for (const g of W.groups) {
      const items = Object.entries(W.defs).filter(([, d]) => d.group === g)
        .filter(([t]) => !q || t.toLowerCase().includes(q));
      if (!items.length) continue;
      html += `<div class="panel-h">${esc(t(g))}</div><div class="chips">`;
      for (const [t] of items) {
        html += `<div class="chip" draggable="true" data-widget="${t}"><span class="i">${icon(groupIcon[g] || 'box', { s: 13 })}</span>${t}</div>`;
      }
      html += `</div>`;
    }
    const c = el('#pgroups');
    c.innerHTML = html;
    c.querySelectorAll('.chip').forEach((chip) => {
      chip.ondragstart = (e) => e.dataTransfer.setData('text/widget', chip.dataset.widget);
    });
  }

  // ── canvas / preview (DOM-based: preserves each widget's CSS classes) ──────
  function buildNode(node, isRoot) {
    const def = W.def(node.type);
    const tmp = document.createElement('div');
    tmp.innerHTML = def ? def.preview(node, '') : `<div>${esc(node.type)}</div>`;
    const elm = tmp.firstElementChild || document.createElement('div');
    elm.setAttribute('data-id', node.id);
    if (node.id === sel) elm.classList.add('sel');
    if (!isRoot) {   // existing nodes are draggable → reorder / move between containers
      elm.draggable = true;
      elm.addEventListener('dragstart', (e) => {
        e.stopPropagation();
        e.dataTransfer.setData('text/move', node.id);
        e.dataTransfer.effectAllowed = 'move';
      });
    }
    if (def && def.container) (node.children || []).forEach((c) => elm.appendChild(buildNode(c, false)));
    return elm;
  }
  // where a drop lands among a container's children (by vertical midpoint)
  function dropIndex(containerEl, y) {
    const kids = containerEl.querySelectorAll(':scope > [data-id]');
    for (let i = 0; i < kids.length; i++) {
      const r = kids[i].getBoundingClientRect();
      if (y < r.top + r.height / 2) return i;
    }
    return kids.length;
  }

  // The selection tag (type label + delete) is a canvas-level overlay so it is
  // NEVER clipped by a widget's own overflow (ProgressBar, Table, etc.).
  function positionTag() {
    const stage = el('#stage');
    let tag = stage.querySelector('.node-tag');
    const selEl = sel ? stage.querySelector('[data-id="' + cssEsc(sel) + '"]') : null;
    if (!selEl) { if (tag) tag.remove(); return; }
    if (!tag) { tag = document.createElement('div'); tag.className = 'node-tag'; stage.appendChild(tag); }
    tag.innerHTML = `${esc(nodeType(sel))}<button title="${esc(t('Remover'))}">${icon('trash', { s: 12 })}</button>`;
    tag.querySelector('button').onclick = (ev) => { ev.stopPropagation(); removeNode(sel); };
    const sr = stage.getBoundingClientRect(), er = selEl.getBoundingClientRect();
    tag.style.left = (er.left - sr.left + stage.scrollLeft) + 'px';
    tag.style.top = Math.max(0, er.top - sr.top + stage.scrollTop - 21) + 'px';
  }
  function cssEsc(s) { return String(s).replace(/"/g, '\\"'); }

  function renderCanvas() {
    const stage = el('#stage');
    const s = curScreen();
    const term = model.target === 'terminalink';
    const empty = !(s.root.children && s.root.children.length);
    const device = document.createElement('div'); device.className = 'device' + (term ? ' device-term' : '');
    const root = document.createElement('div'); root.className = term ? 'tk-root' : 'wk-root';
    root.appendChild(buildNode(s.root, true));
    device.appendChild(root);
    if (empty) {
      const e = document.createElement('div'); e.className = 'empty';
      e.innerHTML = t('Arraste um componente da paleta para dentro do {name} para começar.')
        .replace('{name}', '<b>' + esc(s.root.type) + '</b>');
      device.appendChild(e);
    }
    stage.innerHTML = ''; stage.appendChild(device);

    stage.querySelectorAll('[data-id]').forEach((elm) => {
      const id = elm.getAttribute('data-id');
      elm.onclick = (ev) => { ev.stopPropagation(); sel = id; if (tab === 'state' || tab === 'events') tab = 'props'; render(); };
      if (W.isContainer(nodeType(id))) {
        elm.ondragover = (ev) => { ev.preventDefault(); ev.stopPropagation(); elm.classList.add('drop-ok'); };
        elm.ondragleave = () => elm.classList.remove('drop-ok');
        elm.ondrop = (ev) => {
          ev.preventDefault(); ev.stopPropagation(); elm.classList.remove('drop-ok');
          const moveId = ev.dataTransfer.getData('text/move');
          const wt = ev.dataTransfer.getData('text/widget');
          const index = dropIndex(elm, ev.clientY);
          if (moveId) moveNode(moveId, id, index);
          else if (wt) addChild(id, wt, index);
        };
      }
    });
    positionTag();
    stage.onscroll = positionTag;
    stage.onclick = () => { if (sel) { sel = null; render(); } };
  }
  function nodeType(id) { const r = find(curScreen().root, id); return r ? r.node.type : null; }

  function addChild(containerId, widgetType, index) {
    const def = W.def(widgetType); if (!def) return;
    const r = find(curScreen().root, containerId); if (!r) return;
    const n = { id: 'n' + (idSeq++), type: widgetType, props: Object.assign({}, def.defaultProps) };
    if (def.container) n.children = [];
    r.node.children = r.node.children || [];
    const i = (index == null) ? r.node.children.length : Math.max(0, Math.min(index, r.node.children.length));
    r.node.children.splice(i, 0, n);
    sel = n.id; render(); push();
  }
  function moveNode(moveId, targetId, index) {
    if (moveId === targetId) return;
    const src = find(curScreen().root, moveId);
    if (!src || !src.parent) return;             // never move a screen root
    if (find(src.node, targetId)) return;        // never move into own subtree
    const target = find(curScreen().root, targetId);
    if (!target || !W.isContainer(target.node.type)) return;
    const sameParent = src.parent === target.node;
    const oldIndex = src.parent.children.indexOf(src.node);
    src.parent.children = src.parent.children.filter((c) => c.id !== moveId);
    target.node.children = target.node.children || [];
    let i = (index == null) ? target.node.children.length : index;
    if (sameParent && oldIndex < i) i--;         // account for the removal shifting indices
    i = Math.max(0, Math.min(i, target.node.children.length));
    target.node.children.splice(i, 0, src.node);
    sel = moveId; render(); push();
  }
  function removeNode(id) {
    const r = find(curScreen().root, id);
    if (!r || !r.parent) return; // never remove the Page root
    r.parent.children = r.parent.children.filter((c) => c.id !== id);
    if (sel === id) sel = null;
    render(); push();
  }

  // ── inspector ─────────────────────────────────────────────────────────────
  function renderInspector() {
    const ins = el('#inspector');
    ins.querySelector('#itabs').querySelectorAll('button').forEach((b) => {
      b.classList.toggle('on', b.dataset.tab === tab);
      b.onclick = () => { tab = b.dataset.tab; renderInspector(); };
    });
    const body = ins.querySelector('#ibody');
    if (tab === 'state') return renderStateTab(body);
    if (tab === 'events') return renderEventsTab(body);
    renderPropsTab(body);
  }

  function renderPropsTab(body) {
    const r = selected();
    if (!r) { body.innerHTML = `<div class="hint">${esc(t('Selecione um componente no canvas para editar suas propriedades.'))}</div>`; return; }
    const node = r.node, def = W.def(node.type);
    let html = `<div class="icomp">${esc(t('Componente'))} <span class="tt">${node.type}</span><span class="id">#${node.id}</span></div>`;
    for (const f of (def.fields || [])) html += fieldHtml(node, f);
    // `visible` applies to every component except the screen root (which always renders).
    if (r.parent) html += visibleFieldHtml(node);
    body.innerHTML = html;
    wireFields(body, node);
  }

  // fx toggle button (bind a prop to a variable/expression).
  function fxToggle(pk, on) {
    return `<button type="button" class="fxbtn ${on ? 'on' : ''}" data-fx-toggle="${escAttr(pk)}" ` +
      `title="${esc(on ? t('Voltar a valor fixo') : t('Vincular a uma variável/expressão'))}">fx</button>`;
  }
  function fxInputHtml(pk, raw) {
    const expr = isBound(raw) ? String(raw).slice(1) : '';
    return `<input type="text" class="fx-input" data-fx="${escAttr(pk)}" value="${escAttr(expr)}" ` +
      `placeholder="${esc(t('variável ou expressão (ex.: itens, dados)'))}">`;
  }

  // The universal `visible` control: "Sempre" vs "Condição (fx)". Absent prop = sempre.
  function visibleFieldHtml(node) {
    const raw = node.props.visible;
    const cond = fxState(node, 'visible') || raw !== undefined;
    const expr = isBound(raw) ? String(raw).slice(1) : (raw === false ? 'false' : (typeof raw === 'string' ? raw : ''));
    let html = `<div class="fld fld-vis"><label>${icon('bolt', { s: 12 })} ${esc(t('Visível'))}</label>`;
    html += `<div class="segf"><button data-vis="always" class="${cond ? '' : 'on'}">${esc(t('Sempre'))}</button>` +
      `<button data-vis="cond" class="${cond ? 'on' : ''}">${esc(t('Condição (fx)'))}</button></div>`;
    if (cond) html += `<input type="text" class="fx-input" data-vis-expr="1" value="${escAttr(expr)}" ` +
      `placeholder='ex.: nome &lt;&gt; "" ou mostrarConfirma' style="margin-top:6px">`;
    return html + `</div>`;
  }

  function fieldHtml(node, f) {
    if (f.kind === 'visible') return visibleFieldHtml(node);
    // Chart is a compound editor; fx binds its whole `data` prop.
    if (f.kind === 'chart') {
      const on = fxState(node, 'data');
      const head = `<div class="fld fxhead"><label>${esc(t('Gráfico'))} ${f.bindable ? fxToggle('data', on) : ''}</label></div>`;
      if (f.bindable && on) return head + `<div class="fld">${fxInputHtml('data', node.props.data)}</div>`;
      return head + chartFieldHtml(node);
    }
    // Bindable fields get an fx toggle; when bound, the editor becomes an expression input.
    if (f.bindable) {
      const pk = f.key;
      const on = fxState(node, pk);
      if (on) return `<div class="fld"><label>${esc(t(f.label || pk))} ${fxToggle(pk, true)}</label>${fxInputHtml(pk, node.props[pk])}</div>`;
      const base = baseFieldHtml(node, f);
      return base.replace('</label>', ' ' + fxToggle(pk, false) + '</label>');
    }
    return baseFieldHtml(node, f);
  }

  function baseFieldHtml(node, f) {
    const v = node.props[f.key];
    if (f.kind === 'event') return `<div class="fld"><label>${f.key} — ${esc(t('evento'))}</label>${eventEditorHtml(node, f.key)}</div>`;
    if (f.kind === 'bool')
      return `<div class="fld"><label><input type="checkbox" data-k="${f.key}" ${v ? 'checked' : ''}> ${esc(t(f.label))}</label></div>`;
    if (f.kind === 'number')
      return `<div class="fld"><label>${esc(t(f.label))}</label><input type="number" data-k="${f.key}" value="${v == null ? '' : esc(v)}" min="${f.min ?? ''}" max="${f.max ?? ''}"></div>`;
    if (f.kind === 'enum') {
      const opts = f.options.map((o) => `<option ${String(v) === String(o) ? 'selected' : ''}>${esc(o)}</option>`).join('');
      // small enums render as a segmented control
      if (f.options.length <= 3 && f.options.every((o) => typeof o === 'string'))
        return `<div class="fld"><label>${esc(t(f.label))}</label><div class="segf">` +
          f.options.map((o) => `<button data-seg="${f.key}" data-val="${esc(o)}" class="${String(v) === String(o) ? 'on' : ''}">${esc(o)}</button>`).join('') + `</div></div>`;
      return `<div class="fld"><label>${esc(t(f.label))}</label><select data-k="${f.key}">${opts}</select></div>`;
    }
    if (f.kind === 'csv') {
      const cur = Array.isArray(v) ? v.join(', ') : '';
      return `<div class="fld"><label>${esc(t(f.label))}</label><input type="text" data-csv="${f.key}" value="${escAttr(cur)}" placeholder="${esc(f.ph || '')}"></div>`;
    }
    if (f.kind === 'rows') {
      const cur = Array.isArray(v) ? v.map((r) => (Array.isArray(r) ? r.join(', ') : r)).join('\n') : '';
      return `<div class="fld"><label>${esc(t(f.label))}</label><textarea data-rows="${f.key}" rows="4" style="font-family:var(--vscode-editor-font-family,monospace)">${esc(cur)}</textarea></div>`;
    }
    if (f.kind === 'chart') return chartFieldHtml(node);
    if (f.kind === 'options') {
      const cur = Array.isArray(v) ? v.map((o) => (o && o.value != null && o.value !== o.label) ? `${o.label} = ${o.value}` : (o && o.label != null ? o.label : o)).join('\n') : '';
      return `<div class="fld"><label>${esc(t(f.label))}</label><textarea data-options="${f.key}" rows="4" style="font-family:var(--vscode-editor-font-family,monospace)">${esc(cur)}</textarea></div>`;
    }
    if (f.kind === 'border') {
      const cur = v || 'none';
      const opts = [['none', t('sem borda')], ['round', 'round'], ['single', 'single']];
      return `<div class="fld"><label>${esc(t(f.label))}</label><select data-border="${f.key}">${opts.map(([o, lbl]) => `<option value="${o}" ${cur === o ? 'selected' : ''}>${esc(lbl)}</option>`).join('')}</select></div>`;
    }
    return `<div class="fld"><label>${esc(t(f.label))}</label><input type="text" data-k="${f.key}" value="${v == null ? '' : escAttr(v)}" placeholder="${esc(f.ph || '')}"></div>`;
  }

  function chartFieldHtml(node) {
    const type = node.props.type || 'bar';
    const data = node.props.data || {};
    const ds = (data.datasets && data.datasets[0]) || {};
    const labels = (data.labels || []).join(', ');
    const series = ds.label || '';
    const values = (ds.data || []).join(', ');
    const types = ['bar', 'line', 'pie', 'doughnut', 'radar'];
    return `<div class="fld"><label>${esc(t('Tipo de gráfico'))}</label><select data-chart="type">${types.map((ty) => `<option ${ty === type ? 'selected' : ''}>${ty}</option>`).join('')}</select></div>` +
      `<div class="fld"><label>${esc(t('Rótulos (vírgula)'))}</label><input type="text" data-chart="labels" value="${escAttr(labels)}" placeholder="Jan, Fev, Mar"></div>` +
      `<div class="fld"><label>${esc(t('Nome da série'))}</label><input type="text" data-chart="series" value="${escAttr(series)}" placeholder="Vendas"></div>` +
      `<div class="fld"><label>${esc(t('Valores (vírgula)'))}</label><input type="text" data-chart="values" value="${escAttr(values)}" placeholder="10, 20, 15"></div>`;
  }

  function wireFields(body, node) {
    body.querySelectorAll('[data-k]').forEach((inp) => {
      const k = inp.dataset.k;
      const handler = () => {
        let val = inp.type === 'checkbox' ? inp.checked : inp.value;
        if (inp.type === 'number') val = val === '' ? undefined : Number(val);
        if (val === '' || val === undefined) delete node.props[k]; else node.props[k] = val;
        renderCanvas(); push();
      };
      inp.oninput = handler; inp.onchange = handler;
    });
    body.querySelectorAll('[data-seg]').forEach((b) => {
      b.onclick = () => { node.props[b.dataset.seg] = b.dataset.val; renderPropsTab(body); renderCanvas(); push(); };
    });
    const csvArr = (s) => (s || '').split(',').map((x) => x.trim()).filter((x) => x !== '');
    body.querySelectorAll('[data-csv]').forEach((inp) => {
      inp.oninput = () => {
        const arr = csvArr(inp.value);
        if (arr.length) node.props[inp.dataset.csv] = arr; else delete node.props[inp.dataset.csv];
        renderCanvas(); push();
      };
    });
    body.querySelectorAll('[data-rows]').forEach((inp) => {
      inp.oninput = () => {
        const rows = inp.value.split('\n').map((line) => csvArr(line)).filter((r) => r.length);
        if (rows.length) node.props[inp.dataset.rows] = rows; else delete node.props[inp.dataset.rows];
        renderCanvas(); push();
      };
    });
    body.querySelectorAll('[data-options]').forEach((inp) => {
      inp.oninput = () => {
        const arr = inp.value.split('\n').map((l) => l.trim()).filter(Boolean).map((line) => {
          const i = line.indexOf('=');
          if (i >= 0) return { label: line.slice(0, i).trim(), value: line.slice(i + 1).trim() };
          return { label: line, value: line };
        });
        if (arr.length) node.props[inp.dataset.options] = arr; else delete node.props[inp.dataset.options];
        renderCanvas(); push();
      };
    });
    body.querySelectorAll('[data-border]').forEach((sb) => {
      sb.onchange = () => {
        if (sb.value === 'none') delete node.props[sb.dataset.border]; else node.props[sb.dataset.border] = sb.value;
        renderCanvas(); push();
      };
    });
    const chartCtrls = body.querySelectorAll('[data-chart]');
    if (chartCtrls.length) {
      const applyChart = () => {
        const get = (r) => { const e = body.querySelector('[data-chart="' + r + '"]'); return e ? e.value : ''; };
        const values = csvArr(get('values')).map((x) => { const nx = Number(x); return isNaN(nx) ? x : nx; });
        node.props.type = get('type') || 'bar';
        node.props.data = { labels: csvArr(get('labels')), datasets: [{ label: get('series'), data: values }] };
        renderCanvas(); push();
      };
      chartCtrls.forEach((c) => { c.oninput = applyChart; c.onchange = applyChart; });
    }
    // fx: toggle a bindable prop between fixed value and variable/expression.
    body.querySelectorAll('[data-fx-toggle]').forEach((b) => {
      b.onclick = (ev) => {
        ev.preventDefault();
        const pk = b.dataset.fxToggle;
        const fkey = node.id + '/' + pk;
        if (fxState(node, pk)) {                 // turning fx OFF → drop any binding
          fxMode[fkey] = false;
          if (isBound(node.props[pk])) { delete node.props[pk]; renderCanvas(); push(); }
        } else {
          fxMode[fkey] = true;                   // turning fx ON (value typed later)
        }
        renderPropsTab(el('#ibody')); wireFields(el('#ibody'), node);
      };
    });
    body.querySelectorAll('[data-fx]').forEach((inp) => {
      inp.oninput = () => {
        const pk = inp.dataset.fx;
        const expr = inp.value.trim();
        if (expr) node.props[pk] = '=' + expr; else delete node.props[pk];
        renderCanvas(); push();
      };
    });
    // visible: Sempre (prop absent) vs Condição (=expr).
    body.querySelectorAll('[data-vis]').forEach((b) => {
      b.onclick = () => {
        const fkey = node.id + '/visible';
        if (b.dataset.vis === 'cond') { fxMode[fkey] = true; }
        else { fxMode[fkey] = false; if (node.props.visible !== undefined) { delete node.props.visible; renderCanvas(); push(); } }
        renderPropsTab(el('#ibody')); wireFields(el('#ibody'), node);
      };
    });
    const visExpr = body.querySelector('[data-vis-expr]');
    if (visExpr) visExpr.oninput = () => {
      const e = visExpr.value.trim();
      if (e) node.props.visible = '=' + e; else delete node.props.visible;
      renderCanvas(); push();
    };
    wireEventEditors(body, node);
  }

  // ── hybrid event editor ────────────────────────────────────────────────────
  const ACTIONS = {
    none: { label: '(nenhuma ação)' },
    inc: { label: 'Incrementar variável', needsVar: true, body: (v) => `${v} := ${v} + 1;\nreturn true;` },
    set: { label: 'Definir variável', needsVar: true, needsValue: true, body: (v, val) => `${v} := ${val};\nreturn true;` },
    fromInput: { label: 'Usar valor do input', needsVar: true, param: 'v', body: (v) => `${v} := v;\nreturn true;` },
    nav: { label: 'Ir para rota', needsRoute: true, body: (v, val, route) => `navigate("${route}");\nreturn true;` },
    focus: { label: 'Focar componente', needsKey: true, term: true, body: (v, val, route, key) => `focus("${key}");\nreturn true;` },
  };
  // Keys defined on components of the current screen (targets for the focus action).
  function focusKeys() {
    const keys = [];
    const walk = (n) => { const k = n.props && n.props.key; if (k && !isBound(k)) keys.push(k); (n.children || []).forEach(walk); };
    walk(curScreen().root);
    return keys;
  }
  function handlerOf(node, ev) {
    const ref = node.props[ev];
    if (typeof ref === 'string' && ref.startsWith('@')) return (model.handlers || []).find((h) => h.name === ref.slice(1)) || null;
    return null;
  }
  function detectAction(h) {
    if (!h) return { action: 'none' };
    const b = (h.body || '').replace(/\s+/g, ' ').trim();
    if (b === '' || b === 'return true;' || b === 'return true') return { action: 'none' };
    let m;
    if ((m = /^(\w+) := \1 \+ 1;/.exec(b))) return { action: 'inc', varName: m[1] };
    if ((m = /^navigate\("([^"]*)"\);/.exec(b))) return { action: 'nav', route: m[1] };
    if ((m = /^focus\("([^"]*)"\);/.exec(b))) return { action: 'focus', key: m[1] };
    if ((m = /^(\w+) := v;/.exec(b))) return { action: 'fromInput', varName: m[1] };
    if ((m = /^(\w+) := (.+);/.exec(b))) return { action: 'set', varName: m[1], value: m[2] };
    return { action: 'code' };
  }
  function eventEditorHtml(node, ev) {
    const h = handlerOf(node, ev);
    const st = detectAction(h);
    const fkey = node.id + '/' + ev;
    const fc = forceCode[fkey];
    // fc===true → user forced Code; fc===false → user forced No-code (always show
    // the action dropdown); undefined → infer from the handler body.
    const mode = fc === true ? 'code' : (fc === false ? 'nocode' : (st.action === 'code' ? 'code' : 'nocode'));
    const ref = h ? `<span class="badge">@${esc(h.name)}</span>` : '';
    let html = `<div class="evt" data-ev="${ev}"><div class="evt-h">${icon('bolt', { s: 13 })} ${esc(t('ação'))} ${ev} ${ref}</div>`;
    html += `<div class="evt-modes">
      <button data-mode="nocode" class="${mode === 'nocode' ? 'on' : ''}">${icon('gear', { s: 12 })} ${esc(t('Sem código'))}</button>
      <button data-mode="code" class="${mode === 'code' ? 'on' : ''}">${icon('code', { s: 12 })} ${esc(t('Código'))}</button></div>`;
    if (mode === 'nocode') {
      const varOpts = (model.state || []).map((s) => `<option ${s.name === st.varName ? 'selected' : ''}>${esc(s.name)}</option>`).join('');
      const routeOf = (s) => (model.target === 'terminalink' ? s.name : (s.route || '/'));
      const routeOpts = model.screens.map((s) => `<option ${routeOf(s) === st.route ? 'selected' : ''}>${esc(routeOf(s))}</option>`).join('');
      html += `<div class="evt-row"><select data-role="action">` +
        Object.entries(ACTIONS).filter(([, a]) => !a.term || model.target === 'terminalink')
          .map(([k, a]) => `<option value="${k}" ${st.action === k ? 'selected' : ''}>${esc(t(a.label))}</option>`).join('') +
        `</select>`;
      const a = ACTIONS[st.action] || ACTIONS.none;
      if (a.needsVar) html += `<select data-role="var" style="max-width:120px">${varOpts || '<option>' + esc(t('(sem variáveis)')) + '</option>'}</select>`;
      if (a.needsRoute) html += `<select data-role="route" style="max-width:120px">${routeOpts}</select>`;
      if (a.needsKey) {
        const keys = focusKeys();
        const keyOpts = keys.length ? keys.map((k) => `<option ${k === st.key ? 'selected' : ''}>${esc(k)}</option>`).join('')
          : '<option value="">' + esc(t('(defina "Chave" nos componentes)')) + '</option>';
        html += `<select data-role="key" style="max-width:140px">${keyOpts}</select>`;
      }
      html += `</div>`;
      if (a.needsValue) html += `<div class="evt-row" style="margin-top:6px"><input type="text" data-role="value" placeholder="${escAttr(t('valor (ex.: 10 ou "texto")'))}" value="${st.value ? escAttr(st.value) : ''}"></div>`;
      if (h) html += `<div class="code">${esc(previewHandler(h))}</div>`;
    } else {
      const b = h ? h.body : 'return true;';
      html += `<div class="code"><textarea data-role="code">${esc(b)}</textarea></div>`;
    }
    html += `</div>`;
    return html;
  }
  function previewHandler(h) {
    const params = (h.params || []).join(', ');
    return `function ${h.name}(${params}): ${h.returns || 'Boolean'}\n  ${(h.body || '').split('\n').join('\n  ')}`;
  }
  function ensureHandler(node, ev) {
    let h = handlerOf(node, ev);
    if (h) return h;
    const base = ev === 'onClick' ? 'aoClicar' : ev === 'onSubmit' ? 'aoEnviar' : 'aoMudar';
    let name = base, i = 2;
    const taken = new Set((model.handlers || []).map((x) => x.name));
    while (taken.has(name)) name = base + (i++);
    h = { name, returns: 'Boolean', params: [], body: 'return true;' };
    model.handlers = model.handlers || [];
    model.handlers.push(h);
    node.props[ev] = '@' + name;
    return h;
  }
  function wireEventEditors(body, node) {
    body.querySelectorAll('.evt').forEach((box) => {
      const ev = box.dataset.ev;
      box.querySelectorAll('[data-mode]').forEach((b) => {
        b.onclick = () => {
          const fkey = node.id + '/' + ev;
          if (b.dataset.mode === 'code') { forceCode[fkey] = true; ensureHandler(node, ev); }
          else { forceCode[fkey] = false; }
          renderPropsTab(el('#ibody')); wireFields(el('#ibody'), node);
        };
      });
      const applyNocode = () => {
        const action = box.querySelector('[data-role=action]').value;
        if (action === 'none') { delete node.props[ev]; renderPropsTab(el('#ibody')); wireFields(el('#ibody'), node); renderCanvas(); push(); return; }
        const a = ACTIONS[action];
        const h = ensureHandler(node, ev);
        const vn = box.querySelector('[data-role=var]') ? box.querySelector('[data-role=var]').value : '';
        const route = box.querySelector('[data-role=route]') ? box.querySelector('[data-role=route]').value : '/';
        const value = box.querySelector('[data-role=value]') ? box.querySelector('[data-role=value]').value : '';
        const key = box.querySelector('[data-role=key]') ? box.querySelector('[data-role=key]').value : '';
        h.params = a.param ? [a.param] : [];
        h.body = a.body ? a.body(vn, value, route, key) : 'return true;';
        renderPropsTab(el('#ibody')); wireFields(el('#ibody'), node); renderCanvas(); push();
      };
      box.querySelectorAll('[data-role]').forEach((c) => { c.onchange = applyNocode; });
      const code = box.querySelector('[data-role=code]');
      if (code) code.oninput = () => { const h = ensureHandler(node, ev); h.body = code.value; push(); };
    });
  }

  // ── state tab ─────────────────────────────────────────────────────────────
  const TYPES = ['Integer', 'Real', 'String', 'Boolean', 'Object'];
  function renderStateTab(body) {
    let html = `<div class="icomp">${esc(t('Estado'))} <span class="tt">${esc(t('variáveis globais'))}</span></div>`;
    html += `<div class="hint" style="margin-bottom:10px">${esc(t('Compartilhadas entre telas e eventos. Os eventos leem e alteram estas variáveis.'))}</div>`;
    (model.state || []).forEach((s, i) => {
      html += `<div class="state-row">
        <input type="text" data-si="${i}" data-f="name" value="${escAttr(s.name)}" style="width:88px">
        <select data-si="${i}" data-f="type">${TYPES.map((ty) => `<option ${ty === s.type ? 'selected' : ''}>${ty}</option>`).join('')}</select>
        <span class="ty">=</span>
        <input type="text" data-si="${i}" data-f="initial" value="${escAttr(s.initial || '')}" style="flex:1" placeholder="${esc(t('inicial'))}">
        <button class="iconbtn" data-del-state="${i}">${icon('trash', { s: 13 })}</button></div>`;
    });
    html += `<button class="linkbtn" id="addVar">${icon('plus', { s: 13 })} ${esc(t('Nova variável'))}</button>`;
    body.innerHTML = html;
    body.querySelectorAll('[data-si]').forEach((inp) => {
      inp.onchange = inp.oninput = () => {
        const s = model.state[+inp.dataset.si]; if (!s) return;
        s[inp.dataset.f] = inp.value; push();
        if (inp.dataset.f === 'name') renderCanvas();
      };
    });
    body.querySelectorAll('[data-del-state]').forEach((b) => {
      b.onclick = () => { model.state.splice(+b.dataset.delState, 1); renderStateTab(body); push(); };
    });
    el('#addVar').onclick = () => {
      let n = 'contador', i = 2; const taken = new Set(model.state.map((s) => s.name));
      while (taken.has(n)) n = 'contador' + (i++);
      model.state.push({ name: n, type: 'Integer', initial: '0' }); renderStateTab(body); push();
    };
  }

  function renderEventsTab(body) {
    let html = `<div class="icomp">${esc(t('Eventos'))} <span class="tt">${esc(t('handlers'))}</span></div>`;
    if (!(model.handlers || []).length) html += `<div class="hint">${t('Nenhum evento ainda. Selecione um componente (ex.: Button) e configure o <b>onClick</b> na aba Propriedades.')}</div>`;
    (model.handlers || []).forEach((h) => {
      html += `<div class="fld"><label>function ${esc(h.name)}(${(h.params || []).join(', ')}): ${h.returns || 'Boolean'}</label>` +
        `<div class="code">${esc(h.body || '')}</div></div>`;
    });
    body.innerHTML = html;
  }

  // ── target + screens ───────────────────────────────────────────────────────
  // Change the editor UI language: re-render everything and persist the choice.
  function setLang(lang) {
    LANG = NpI18n.normalize(lang);
    render();
    vscode.postMessage({ type: 'setLang', lang: LANG });
  }

  function setTarget(target) {
    if (model.target === target || !TARGETS[target]) return;
    model.target = target;
    W = TARGETS[target];
    // Widget sets differ between targets, so reset each screen to a fresh root.
    model.screens.forEach((s) => { s.root = W.scaffoldRoot(); ensureIds(s.root); });
    sel = null; render(); push();
  }
  function addScreen() {
    const term = model.target === 'terminalink';
    let name = term ? 'tela' : 'tela', i = 2; const taken = new Set(model.screens.map((s) => s.name));
    while (taken.has(name)) name = 'tela' + (i++);
    const route = term ? '' : '/' + name;
    const sc = { route, name, root: W.scaffoldRoot() };
    ensureIds(sc.root);
    model.screens.push(sc);
    screenIdx = model.screens.length - 1; sel = null; render(); push();
  }

  // ── custom themed dropdown ──────────────────────────────────────────────────
  // Native <select> popups can't be styled (the OS draws them), so we hide the
  // native control and drive a themed popup from it — dispatching real change
  // events so every existing wiring keeps working untouched.
  function enhanceSelect(sel) {
    if (sel.__np) return;
    sel.__np = true;
    sel.tabIndex = -1;
    sel.classList.add('np-native');
    const wrap = document.createElement('span');
    wrap.className = 'np-sel';
    wrap.style.cssText = sel.style.cssText;   // carry inline width/max-width
    sel.style.cssText = '';
    sel.parentNode.insertBefore(wrap, sel);
    wrap.appendChild(sel);
    const face = document.createElement('button');
    face.type = 'button'; face.className = 'np-face';
    wrap.appendChild(face);
    let pop = null;

    const syncFace = () => {
      const o = sel.options[sel.selectedIndex];
      face.innerHTML = `<span class="np-lbl">${esc(o ? o.text : '')}</span>${icon('chevron', { s: 12 })}`;
    };
    const close = () => {
      if (pop) { pop.remove(); pop = null; }
      wrap.classList.remove('open');
      document.removeEventListener('mousedown', onDoc, true);
      document.removeEventListener('keydown', onKey, true);
      window.removeEventListener('scroll', close, true);
      window.removeEventListener('resize', close, true);
    };
    const onDoc = (ev) => { if (pop && !pop.contains(ev.target) && !wrap.contains(ev.target)) close(); };
    const onKey = (ev) => { if (ev.key === 'Escape') close(); };
    const place = () => {
      const r = face.getBoundingClientRect();
      pop.style.width = Math.max(r.width, 130) + 'px';
      pop.style.left = Math.min(r.left, window.innerWidth - pop.offsetWidth - 8) + 'px';
      const h = Math.min(pop.scrollHeight, 260);
      const below = r.bottom + 4;
      pop.style.top = (below + h > window.innerHeight && r.top - 4 - h > 0 ? r.top - 4 - h : below) + 'px';
    };
    const open = () => {
      pop = document.createElement('div');
      pop.className = 'np-pop';
      Array.from(sel.options).forEach((o, i) => {
        const it = document.createElement('div');
        it.className = 'np-opt' + (i === sel.selectedIndex ? ' on' : '');
        it.innerHTML = `<span class="np-check">${icon('check', { s: 12 })}</span><span>${esc(o.text)}</span>`;
        it.onmousedown = (ev) => {
          ev.preventDefault();
          if (i !== sel.selectedIndex) {
            sel.selectedIndex = i;
            sel.dispatchEvent(new Event('input', { bubbles: true }));
            sel.dispatchEvent(new Event('change', { bubbles: true }));
          }
          close(); syncFace();
        };
        pop.appendChild(it);
      });
      document.body.appendChild(pop);
      wrap.classList.add('open');
      place();
      document.addEventListener('mousedown', onDoc, true);
      document.addEventListener('keydown', onKey, true);
      window.addEventListener('scroll', close, true);
      window.addEventListener('resize', close, true);
    };
    face.onclick = (ev) => { ev.preventDefault(); ev.stopPropagation(); pop ? close() : open(); };
    sel.addEventListener('change', syncFace);
    syncFace();
  }
  function enhanceSelectsIn(node) {
    if (node.nodeType !== 1) return;
    if (node.tagName === 'SELECT') enhanceSelect(node);
    else if (node.querySelectorAll) node.querySelectorAll('select').forEach(enhanceSelect);
  }
  // Any <select> added by any (re)render is themed automatically.
  const selObserver = new MutationObserver((muts) => {
    for (const m of muts) for (const n of m.addedNodes) enhanceSelectsIn(n);
  });

  // ── utils ──────────────────────────────────────────────────────────────────
  function el(s) { return document.querySelector(s); }
  function esc(s) { return String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;'); }
  function escAttr(s) { return esc(s).replace(/"/g, '&quot;'); }

  // ── messaging ──────────────────────────────────────────────────────────────
  window.addEventListener('message', (e) => {
    const msg = e.data;
    if (msg.type === 'lang') {                 // language set by the extension host
      const L = NpI18n.normalize(msg.lang);
      if (L !== LANG) { LANG = L; render(); }
      return;
    }
    if (msg.type === 'init') {
      if (pending.has(msg.text)) { pending.delete(msg.text); return; } // ignore our own echo
      applyingRemote = true;
      try {
        const parsed = msg.text && msg.text.trim() ? JSON.parse(msg.text) : scaffold();
        model = normalize(parsed);
      } catch (err) { model = scaffold(); }
      W = TARGETS[model.target] || TARGETS.webink;
      model.screens.forEach((s) => ensureIds(s.root));
      if (screenIdx >= model.screens.length) screenIdx = 0;
      applyingRemote = false;
      render();
    }
  });
  function normalize(m) {
    if (!m || typeof m !== 'object') return scaffold();
    m.target = m.target || 'webink';
    m.state = m.state || []; m.handlers = m.handlers || [];
    if (!Array.isArray(m.screens) || !m.screens.length) m.screens = scaffold().screens;
    m.screens.forEach((s) => { s.root = s.root || { type: 'Page', props: {}, children: [] }; });
    return m;
  }

  selObserver.observe(document.body, { childList: true, subtree: true });
  enhanceSelectsIn(document.body);   // theme the shell's tabs/any present selects
  vscode.postMessage({ type: 'ready' });
})();
