// codegen.js — turns an .xnpas design model (JSON) into runnable NeoObjectPascal
// source. Phase 1 targets WebInk; TerminalInk lands in Phase 3. One-way only:
// the .xnpas is the source of truth and this regenerates the sibling .npas.
'use strict';

const EVENT_PROPS = new Set(['onClick', 'onChange', 'onSubmit']);
// Keys that are reserved words in the grammar → must be emitted as string keys.
const RESERVED_KEYS = new Set(['class', 'to', 'type', 'end', 'begin', 'var', 'function', 'do', 'in', 'if']);

function q(s) {
  return '"' + String(s).replace(/\\/g, '\\\\').replace(/"/g, '\\"') + '"';
}
function isIdent(k) { return /^[A-Za-z_][A-Za-z0-9_]*$/.test(k); }
function keyName(k) { return (isIdent(k) && !RESERVED_KEYS.has(k)) ? k : q(k); }

// Serialize a JSON value to a NeoObjectPascal expression.
function val(v) {
  if (v === null || v === undefined) return 'null';
  if (typeof v === 'number') return String(v);
  if (typeof v === 'boolean') return v ? 'true' : 'false';
  if (typeof v === 'string') {
    if (v.startsWith('@')) return v.slice(1);           // @handler → function reference
    if (v.startsWith('=')) return v.slice(1);           // =expr → raw expression (e.g. a global var)
    return q(v);
  }
  if (Array.isArray(v)) return '[' + v.map(val).join(', ') + ']';
  if (typeof v === 'object') return record(v);
  return q(String(v));
}

function record(obj) {
  const entries = Object.entries(obj || {})
    .filter(([, x]) => x !== undefined && x !== null && x !== '');
  if (!entries.length) return '#{}';
  return '#{ ' + entries.map(([k, x]) => keyName(k) + ': ' + val(x)).join(', ') + ' }';
}

// A widget node → `Widget(#{props}, [children])`. `indent` = 4-space units of the node.
function nodeSrc(node, indent) {
  if (!node || !node.type) return 'Text(#{})';
  const pad = '    '.repeat(indent);
  const props = record(node.props);
  const kids = Array.isArray(node.children) ? node.children : [];
  if (!kids.length) return node.type + '(' + props + ')';
  const inner = kids
    .map((c) => '    '.repeat(indent + 1) + nodeSrc(c, indent + 1))
    .join(',\n');
  return node.type + '(' + props + ', [\n' + inner + '\n' + pad + '])';
}

function defaultInit(t) {
  if (t === 'String') return '""';
  if (t === 'Boolean') return 'false';
  if (t === 'Real') return '0.0';
  return '0';
}
function initValue(s) {
  const v = s.initial;
  if (v === undefined || v === null || v === '') return defaultInit(s.type);
  if (s.type === 'String') return q(v);
  if (s.type === 'Boolean') return (v === true || v === 'true') ? 'true' : 'false';
  return String(v); // Integer / Real — numeric literal as authored
}

function header(name) {
  return [
    '// ============================================================================',
    '//  GERADO por ' + name + '.xnpas — NeoObjectPascal UI Builder',
    '//  Edite a interface pelo editor visual (.xnpas). Alterações feitas à mão',
    '//  neste arquivo serão sobrescritas ao salvar o .xnpas.',
    '// ============================================================================',
  ];
}

function generateWeb(model, fileBase) {
  const L = header(fileBase);
  L.push('');
  L.push('uses webink;');
  L.push('');

  const state = model.state || [];
  for (const s of state) L.push('var ' + s.name + ': ' + s.type + ';   // estado compartilhado');
  if (state.length) L.push('');

  for (const h of (model.handlers || [])) {
    const params = (h.params || []).join(', ');
    L.push('function ' + h.name + '(' + params + '): ' + (h.returns || 'Boolean'));
    L.push('begin');
    const body = (h.body && h.body.trim()) ? h.body : 'return true;';
    for (const line of body.split('\n')) L.push(line ? '    ' + line : '');
    L.push('end;');
    L.push('');
  }

  const screens = model.screens || [];
  for (const sc of screens) {
    L.push('function ' + sc.name + '(): Object');
    L.push('begin');
    L.push('    return ' + nodeSrc(sc.root, 1) + ';');
    L.push('end;');
    L.push('');
  }

  L.push('begin');
  for (const s of state) L.push('    ' + s.name + ' := ' + initValue(s) + ';');
  const routes = screens.map((sc) => q(sc.route || '/') + ': ' + sc.name).join(', ');
  const opts = model.title ? ', #{ title: ' + q(model.title) + ' }' : '';
  L.push('    render(#{ ' + routes + ' }' + opts + ');');
  L.push('end.');
  return L.join('\n') + '\n';
}

// ── TerminalInk ────────────────────────────────────────────────────────────
// Widgets that take their text as a positional string arg after the props record.
const TK_TEXT = new Set(['Text', 'Badge', 'StatusMessage', 'Alert']);
const TK_CONTAINERS = new Set(['Box', 'VBox', 'HBox']);
const TK_LISTS = new Set(['UnorderedList', 'OrderedList']);

function nodeSrcT(node, indent) {
  if (!node || !node.type) return 'Text(#{})';
  const pad = '    '.repeat(indent);
  const type = node.type;
  if (TK_LISTS.has(type)) {
    const props = Object.assign({}, node.props);
    const items = props.items || [];
    delete props.items;
    if (!items.length) return type + '(' + record(props) + ', [])';
    const kids = items.map((it) => '    '.repeat(indent + 1) + 'Item(#{}, ' + q(String(it)) + ')').join(',\n');
    return type + '(' + record(props) + ', [\n' + kids + '\n' + pad + '])';
  }
  if (TK_TEXT.has(type)) {
    const props = Object.assign({}, node.props);
    const text = props.text;
    delete props.text;
    const rec = record(props);
    if (text === undefined || text === null || text === '') return type + '(' + rec + ')';
    return type + '(' + rec + ', ' + val(text) + ')';
  }
  if (TK_CONTAINERS.has(type)) {
    const kids = Array.isArray(node.children) ? node.children : [];
    if (!kids.length) return type + '(' + record(node.props) + ', [])';
    const inner = kids.map((c) => '    '.repeat(indent + 1) + nodeSrcT(c, indent + 1)).join(',\n');
    return type + '(' + record(node.props) + ', [\n' + inner + '\n' + pad + '])';
  }
  return type + '(' + record(node.props) + ')';
}

function generateTerminal(model, fileBase) {
  const L = header(fileBase);
  L.push('');
  L.push('uses terminalink;');
  L.push('');
  const state = model.state || [];
  for (const s of state) L.push('var ' + s.name + ': ' + s.type + ';   // estado compartilhado');
  if (state.length) L.push('');
  for (const h of (model.handlers || [])) {
    const params = (h.params || []).join(', ');
    L.push('function ' + h.name + '(' + params + '): ' + (h.returns || 'Boolean'));
    L.push('begin');
    const body = (h.body && h.body.trim()) ? h.body : 'return true;';
    for (const line of body.split('\n')) L.push(line ? '    ' + line : '');
    L.push('end;');
    L.push('');
  }
  const screens = model.screens || [];
  for (const sc of screens) {
    L.push('function ' + sc.name + '(): Object');
    L.push('begin');
    L.push('    return ' + nodeSrcT(sc.root, 1) + ';');
    L.push('end;');
    L.push('');
  }
  L.push('begin');
  for (const s of state) L.push('    ' + s.name + ' := ' + initValue(s) + ';');
  if (screens.length <= 1) {
    L.push('    render(' + ((screens[0] && screens[0].name) || 'ui') + ');');
  } else {
    // Named screens (identifiers), first is the default. navigate("name") switches.
    L.push('    render(#{ ' + screens.map((s) => s.name + ': ' + s.name).join(', ') + ' });');
  }
  L.push('end.');
  return L.join('\n') + '\n';
}

function generate(model, fileBase) {
  fileBase = fileBase || 'arquivo';
  if (!model || typeof model !== 'object') throw new Error('modelo .xnpas inválido');
  if (model.target === 'terminalink') return generateTerminal(model, fileBase);
  return generateWeb(model, fileBase);
}

module.exports = { generate, nodeSrc, record, val };
