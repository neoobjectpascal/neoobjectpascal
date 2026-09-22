// npasParser.js — the inverse of codegen.js: turns a generated .npas back into
// the .xnpas design model (JSON), so the UI Builder can adopt edits made in the
// NeoObjectPascal source. It only understands the shape codegen.js emits; a file
// that deviates is rejected (the caller must then keep the .xnpas untouched).
'use strict';

// Props whose value is a handler reference (`@name`) rather than an expression.
// Mirrors the event list used by the builder webview.
const EVENT_PROPS = new Set(['onClick', 'onChange', 'onSubmit', 'onConfirm', 'onCancel', 'onClose']);

// TerminalInk widgets that codegen emits with a positional argument.
const TK_TEXT = new Set(['Text', 'Badge', 'StatusMessage', 'Alert']);
const TK_LISTS = new Set(['UnorderedList', 'OrderedList']);

const IDENT = /^[A-Za-z_][A-Za-z0-9_]*$/;

class ParseError extends Error {}

function fail(msg, line) {
  throw new ParseError(line == null ? msg : msg + ' (linha ' + (line + 1) + ')');
}

// Does this source carry the UI Builder banner? Only generated files may be
// imported back — a hand-written .npas must never overwrite a design model.
function isGenerated(src) {
  return /^\s*\/\/\s+GERADO por .+\.xnpas/m.test(String(src).split('\n').slice(0, 12).join('\n'));
}

// Drop a trailing `// comment`, ignoring `//` inside string literals.
function stripComment(line) {
  let inStr = false;
  for (let i = 0; i < line.length; i++) {
    const c = line[i];
    if (c === '\\' && inStr) { i++; continue; }
    if (c === '"') { inStr = !inStr; continue; }
    if (!inStr && c === '/' && line[i + 1] === '/') return line.slice(0, i);
  }
  return line;
}

// begin/end keywords on a line, ignoring strings and comments — used to find
// where a function body ends without parsing the whole language.
function blockDelta(line) {
  const code = stripComment(line).replace(/"(\\.|[^"\\])*"/g, '""');
  let delta = 0;
  const words = code.match(/[A-Za-z_][A-Za-z0-9_]*/g) || [];
  for (const w of words) {
    if (w === 'begin') delta++;
    else if (w === 'end') delta--;
  }
  return delta;
}

// ── expression reader ───────────────────────────────────────────────────────
// A cursor over the source of one expression. Reads exactly the subset that
// codegen.js can emit: literals, records, arrays and widget calls. Anything else
// is captured verbatim as `raw` (a NeoObjectPascal expression).
class Reader {
  constructor(src) { this.s = src; this.i = 0; }

  ws() {
    while (this.i < this.s.length) {
      const c = this.s[this.i];
      if (c === ' ' || c === '\t' || c === '\n' || c === '\r') { this.i++; continue; }
      if (c === '/' && this.s[this.i + 1] === '/') {
        while (this.i < this.s.length && this.s[this.i] !== '\n') this.i++;
        continue;
      }
      break;
    }
  }
  eof() { this.ws(); return this.i >= this.s.length; }
  peek() { this.ws(); return this.s[this.i]; }
  eat(ch) {
    this.ws();
    if (this.s[this.i] !== ch) fail('esperava ' + JSON.stringify(ch) + ' e encontrou ' + JSON.stringify(this.s.slice(this.i, this.i + 12)));
    this.i++;
  }
  tryEat(ch) { this.ws(); if (this.s[this.i] === ch) { this.i++; return true; } return false; }

  ident() {
    this.ws();
    const m = /^[A-Za-z_][A-Za-z0-9_]*/.exec(this.s.slice(this.i));
    if (!m) fail('esperava um identificador em ' + JSON.stringify(this.s.slice(this.i, this.i + 12)));
    this.i += m[0].length;
    return m[0];
  }

  string() {
    this.eat('"');
    let out = '';
    while (this.i < this.s.length) {
      const c = this.s[this.i++];
      if (c === '\\') { out += this.s[this.i++]; continue; }
      if (c === '"') return out;
      out += c;
    }
    return fail('string sem fechamento');
  }

  // Everything up to the next top-level `,` `;` `}` `]` or `)` — a raw expression.
  raw() {
    const start = this.i;
    let depth = 0, inStr = false;
    while (this.i < this.s.length) {
      const c = this.s[this.i];
      if (inStr) {
        if (c === '\\') this.i++;
        else if (c === '"') inStr = false;
        this.i++;
        continue;
      }
      if (c === '"') { inStr = true; this.i++; continue; }
      if (c === '(' || c === '[' || c === '{') { depth++; this.i++; continue; }
      if (c === ')' || c === ']' || c === '}') {
        if (depth === 0) break;
        depth--; this.i++; continue;
      }
      if ((c === ',' || c === ';') && depth === 0) break;
      this.i++;
    }
    const text = this.s.slice(start, this.i).trim();
    if (!text) fail('valor vazio');
    return text;
  }

  // A value ends where a literal can legally stop. Anything else after a literal
  // (an operator, a second term) means the whole thing is an expression.
  atValueEnd() {
    const c = this.peek();
    return c === undefined || c === ',' || c === ';' || c === '}' || c === ']' || c === ')';
  }

  value() {
    this.ws();
    const save = this.i;
    const v = this.primary();
    if (v.kind !== 'raw' && !this.atValueEnd()) {
      this.i = save;
      return { kind: 'raw', value: this.raw() };
    }
    return v;
  }

  primary() {
    this.ws();
    const c = this.s[this.i];
    if (c === '"') return { kind: 'string', value: this.string() };
    if (c === '[') return this.array();
    if (c === '#' && this.s[this.i + 1] === '{') return this.record();
    const rest = this.s.slice(this.i);
    let m = /^-?\d+(\.\d+)?(?![\w.])/.exec(rest);
    if (m) { this.i += m[0].length; return { kind: 'number', value: Number(m[0]) }; }
    m = /^(true|false|null)(?![\w])/.exec(rest);
    if (m) {
      this.i += m[0].length;
      return m[0] === 'null' ? { kind: 'null', value: null } : { kind: 'bool', value: m[0] === 'true' };
    }
    // A widget call: Identifier( ... )
    m = /^([A-Za-z_][A-Za-z0-9_]*)[ \t\n\r]*\(/.exec(rest);
    if (m) {
      const name = this.ident();
      this.eat('(');
      const args = [];
      if (!this.tryEat(')')) {
        do { args.push(this.value()); } while (this.tryEat(','));
        this.eat(')');
      }
      return { kind: 'call', name: name, args: args };
    }
    return { kind: 'raw', value: this.raw() };
  }

  array() {
    this.eat('[');
    const items = [];
    if (this.tryEat(']')) return { kind: 'array', items: items };
    do { items.push(this.value()); } while (this.tryEat(','));
    this.eat(']');
    return { kind: 'array', items: items };
  }

  record() {
    this.eat('#'); this.eat('{');
    const entries = [];
    if (this.tryEat('}')) return { kind: 'record', entries: entries };
    do {
      this.ws();
      const key = this.s[this.i] === '"' ? this.string() : this.ident();
      this.eat(':');
      entries.push([key, this.value()]);
    } while (this.tryEat(','));
    this.eat('}');
    return { kind: 'record', entries: entries };
  }
}

// ── value → model prop ──────────────────────────────────────────────────────
function propValue(v, key) {
  switch (v.kind) {
    case 'string': return v.value;
    case 'number': return v.value;
    case 'bool': return v.value;
    case 'null': return null;
    case 'array': return v.items.map((x) => propValue(x, key));
    case 'record': {
      const o = {};
      for (const [k, x] of v.entries) o[k] = propValue(x, k);
      return o;
    }
    case 'call': fail('componente inesperado dentro de uma propriedade: ' + v.name); break;
    default:
      // A bare identifier under an event prop is a handler reference; anything
      // else is an expression binding (the `=expr` form the builder uses).
      return (EVENT_PROPS.has(key) && IDENT.test(v.value)) ? '@' + v.value : '=' + v.value;
  }
}

function recordToProps(v) {
  const props = {};
  if (!v || v.kind !== 'record') return props;
  for (const [k, x] of v.entries) props[k] = propValue(x, k);
  return props;
}

// ── call → widget node ──────────────────────────────────────────────────────
function toNode(call, target, nextId) {
  if (!call || call.kind !== 'call') fail('esperava um componente e encontrou outra expressão');
  const type = call.name;
  const node = { id: nextId(), type: type, props: recordToProps(call.args[0]) };
  const arg = call.args[1];

  if (target === 'terminalink' && TK_LISTS.has(type)) {
    const items = (arg && arg.kind === 'array' ? arg.items : [])
      .map((it) => (it.kind === 'call' && it.args[1] ? propValue(it.args[1], 'text') : ''));
    if (items.length) node.props.items = items;
    node.children = [];
    return node;
  }
  if (target === 'terminalink' && TK_TEXT.has(type)) {
    if (arg) node.props.text = propValue(arg, 'text');
    return node;
  }
  if (arg && arg.kind === 'array') {
    node.children = arg.items.map((c) => toNode(c, target, nextId));
  }
  return node;
}

// ── source → model ──────────────────────────────────────────────────────────
function parse(src) {
  if (typeof src !== 'string' || !src.trim()) fail('arquivo .npas vazio');
  if (!isGenerated(src)) fail('o .npas não foi gerado pelo editor visual');

  const lines = src.split('\n');
  const state = [];
  const fns = [];                 // { name, params, returns, body }
  let target = null;
  let main = null;                // the top-level begin…end. block

  let i = 0;
  while (i < lines.length) {
    const raw = lines[i];
    const line = stripComment(raw).trim();
    if (!line) { i++; continue; }

    let m = /^uses\s+([A-Za-z_][A-Za-z0-9_.]*)\s*;$/.exec(line);
    if (m) {
      target = m[1] === 'terminalink' ? 'terminalink' : m[1] === 'desktopink' ? 'desktopink' : 'webink';
      i++; continue;
    }

    m = /^var\s+([A-Za-z_][A-Za-z0-9_]*)\s*:\s*([A-Za-z_][A-Za-z0-9_]*)\s*;$/.exec(line);
    if (m) { state.push({ name: m[1], type: m[2] }); i++; continue; }

    m = /^function\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(([^)]*)\)\s*:\s*([A-Za-z_][A-Za-z0-9_]*)\s*$/.exec(line);
    if (m) {
      const head = { name: m[1], params: m[2].split(',').map((p) => p.trim()).filter(Boolean), returns: m[3] };
      const body = [];
      i++;
      if (stripComment(lines[i] || '').trim() !== 'begin') fail('esperava "begin" após function ' + head.name, i);
      i++;
      let depth = 1;
      while (i < lines.length) {
        const cur = lines[i];
        depth += blockDelta(cur);
        if (depth === 0) { i++; break; }
        body.push(cur);
        i++;
      }
      if (depth !== 0) fail('function ' + head.name + ' sem "end;"');
      head.body = body;
      fns.push(head);
      continue;
    }

    if (line === 'begin') {
      const body = [];
      i++;
      let depth = 1;
      while (i < lines.length) {
        const cur = lines[i];
        depth += blockDelta(cur);
        if (depth === 0) { i++; break; }
        body.push(cur);
        i++;
      }
      if (depth !== 0) fail('bloco principal sem "end."');
      main = body;
      continue;
    }

    fail('linha fora do formato gerado: ' + JSON.stringify(line.slice(0, 60)), i);
  }

  if (!target) fail('faltou a cláusula "uses"');
  if (!main) fail('faltou o bloco principal "begin … end."');

  // ── main block: state initializers + render(...) ──
  const inits = {};
  let renderArgs = null;
  const mainSrc = main.map(stripComment).join('\n');
  const rd = new Reader(mainSrc);
  while (!rd.eof()) {
    rd.ws();
    const rest = rd.s.slice(rd.i);
    let m = /^([A-Za-z_][A-Za-z0-9_]*)\s*:=/.exec(rest);
    if (m) {
      rd.i += m[0].length;
      const v = rd.value();
      inits[m[1]] = v;
      rd.tryEat(';');
      continue;
    }
    m = /^render\s*\(/.exec(rest);
    if (m) {
      rd.i += m[0].length;
      renderArgs = [];
      if (!rd.tryEat(')')) {
        do { renderArgs.push(rd.value()); } while (rd.tryEat(','));
        rd.eat(')');
      }
      rd.tryEat(';');
      continue;
    }
    fail('instrução não suportada no bloco principal: ' + JSON.stringify(rest.slice(0, 40)));
  }
  if (!renderArgs || !renderArgs.length) fail('faltou a chamada render(...)');

  // Screen order/route comes from the render map; everything else is a handler.
  const screenOrder = [];      // { name, route }
  const first = renderArgs[0];
  if (first.kind === 'record') {
    for (const [key, v] of first.entries) {
      if (v.kind !== 'raw' || !IDENT.test(v.value)) fail('rota "' + key + '" não aponta para uma tela');
      screenOrder.push({ name: v.value, route: target === 'webink' ? key : '' });
    }
  } else if (first.kind === 'raw' && IDENT.test(first.value)) {
    screenOrder.push({ name: first.value, route: '' });
  } else {
    fail('render(...) em formato não suportado');
  }

  const model = { xnpas: 1, target: target, state: [], handlers: [], screens: [] };

  const titleArg = renderArgs[1];
  if (titleArg && titleArg.kind === 'record') {
    const props = recordToProps(titleArg);
    if (props.title !== undefined) model.title = props.title;
    if (target === 'desktopink') {
      model.desktop = {};
      for (const key of ['centered', 'maximized', 'width', 'height', 'theme']) {
        if (props[key] !== undefined) model.desktop[key] = props[key];
      }
    }
  }

  for (const s of state) {
    const v = inits[s.name];
    const entry = { name: s.name, type: s.type };
    if (v !== undefined) {
      entry.initial = (v.kind === 'string') ? v.value
        : (v.kind === 'raw') ? v.value : String(v.value);
    }
    model.state.push(entry);
  }

  let seq = 0;
  const nextId = () => 'n' + (++seq);
  const screenNames = new Set(screenOrder.map((s) => s.name));

  for (const fn of fns) {
    if (screenNames.has(fn.name)) continue;
    const body = dedent(fn.body).join('\n').replace(/\s+$/, '');
    model.handlers.push({ name: fn.name, returns: fn.returns, params: fn.params, body: body });
  }

  for (const s of screenOrder) {
    const fn = fns.find((f) => f.name === s.name);
    if (!fn) fail('tela "' + s.name + '" citada em render(...) não tem function correspondente');
    const body = fn.body.map(stripComment).join('\n').trim();
    const m = /^return\s+([\s\S]+?);?\s*$/.exec(body);
    if (!m) fail('a tela "' + s.name + '" não é um "return <componente>;"');
    const reader = new Reader(m[1]);
    const root = toNode(reader.value(), target, nextId);
    // codegen places modal declarations at the screen root. Restore the
    // builder model boundary while preserving each modal's editable root node.
    const modals = (root.children || []).filter((n) => n.type === 'Modal')
      .map((modal, index) => {
        const name = modal.props.name || 'modal' + (index + 1);
        delete modal.props.name;
        return { name: name, root: modal };
      });
    if (modals.length) root.children = root.children.filter((n) => n.type !== 'Modal');
    const screen = { route: s.route, name: target === 'desktopink' ? 'screen' : s.name, root: root };
    if (modals.length) screen.modals = modals;
    model.screens.push(screen);
  }
  if (!model.screens.length) fail('nenhuma tela encontrada');

  return model;
}

// Remove the 4-space indent codegen adds to handler bodies.
function dedent(lines) {
  return lines.map((l) => (l.startsWith('    ') ? l.slice(4) : l));
}

module.exports = { parse, isGenerated, ParseError };
