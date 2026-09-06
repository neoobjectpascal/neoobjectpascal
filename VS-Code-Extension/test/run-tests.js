#!/usr/bin/env node
// Automated tests for the UI Builder codegen (.xnpas -> .npas).
// Run:  node test/run-tests.js        (codegen golden tests)
//       RUN_JAR=1 node test/run-tests.js   (also smoke-run each on the bundled JAR)
'use strict';
const path = require('path');
const fs = require('fs');
const os = require('os');
const { spawn } = require('child_process');
const { generate } = require('../codegen');

let pass = 0, fail = 0;
const gen = (m) => generate(m, 'teste');
function t(name, fn) {
  try { fn(); pass++; console.log('  ✓ ' + name); }
  catch (e) { fail++; console.log('  ✗ ' + name + '\n      ' + e.message); }
}
function has(src, needle) {
  if (src.indexOf(needle) < 0) throw new Error('esperava conter: ' + JSON.stringify(needle));
}
function hasnt(src, needle) {
  if (src.indexOf(needle) >= 0) throw new Error('não deveria conter: ' + JSON.stringify(needle));
}

// ── fixtures ──────────────────────────────────────────────────────────────────
const webink = {
  xnpas: 1, target: 'webink',
  state: [{ name: 'cliques', type: 'Integer', initial: '0' }],
  handlers: [{ name: 'registrar', returns: 'Boolean', params: [], body: 'cliques := cliques + 1;\nreturn true;' }],
  screens: [
    { route: '/', name: 'home', root: { type: 'Page', props: {}, children: [
      { type: 'Navbar', props: {}, children: [{ type: 'Heading', props: { level: 3, text: 'Acme' } }] },
      { type: 'Grid', props: { cols: 3 }, children: [
        { type: 'StatCard', props: { label: 'Cliques', value: '=cliques' } },
      ] },
      { type: 'Table', props: { columns: ['A', 'B'], rows: [['1', '2']] } },
      { type: 'Select', props: { options: ['x', 'y'] } },
      { type: 'Chart', props: { type: 'bar', data: { labels: ['Jan'], datasets: [{ label: 'V', data: [10] }] } } },
      { type: 'Button', props: { text: 'Ok "x"', onClick: '@registrar' } },
    ] } },
    { route: '/sobre', name: 'sobre', root: { type: 'Page', props: {}, children: [] } },
  ],
  title: 'Acme',
};
const terminalink = {
  xnpas: 1, target: 'terminalink',
  state: [{ name: 'nome', type: 'String', initial: '' }],
  handlers: [
    { name: 'onNome', returns: 'Boolean', params: ['v'], body: 'nome := v;\nreturn true;' },
    { name: 'irSobre', returns: 'Boolean', params: [], body: 'navigate("sobre");\nreturn true;' },
  ],
  screens: [
    { name: 'ui', route: '', root: { type: 'VBox', props: { padding: 1, border: 'round', borderColor: 'cyan' }, children: [
      { type: 'Text', props: { text: 'Cadastro', color: 'cyan', bold: true } },
      { type: 'TextInput', props: { placeholder: 'nome', onChange: '@onNome' } },
      { type: 'Select', props: { options: [{ label: 'A', value: '1' }] } },
      { type: 'UnorderedList', props: { items: ['x', 'y'], marker: '-' } },
      { type: 'VBox', props: {}, children: [] },
      { type: 'ConfirmInput', props: { onConfirm: '@irSobre' } },
    ] } },
    { name: 'sobre', route: '', root: { type: 'VBox', props: {}, children: [{ type: 'Text', props: { text: 'Sobre' } }] } },
  ],
};

// ── codegen golden tests ──────────────────────────────────────────────────────
console.log('Codegen — WebInk');
const w = gen(webink);
t('uses webink + header', () => { has(w, 'uses webink;'); has(w, 'GERADO por teste.xnpas'); });
t('rotas em render', () => has(w, 'render(#{ "/": home, "/sobre": sobre }, #{ title: "Acme" });'));
t('state var + init', () => { has(w, 'var cliques: Integer;'); has(w, 'cliques := 0;'); });
t('handler function', () => has(w, 'function registrar(): Boolean'));
t('evento = referência de função (sem aspas)', () => { has(w, 'onClick: registrar'); hasnt(w, 'onClick: "registrar"'); });
t('=expr vira expressão bare', () => has(w, 'value: cliques'));
t('Table colunas/linhas', () => has(w, 'columns: ["A", "B"], rows: [["1", "2"]]'));
t('Select options array', () => has(w, 'options: ["x", "y"]'));
t('Chart data aninhado', () => has(w, 'data: #{ labels: ["Jan"], datasets: [#{ label: "V", data: [10] }] }'));
t('escape de aspas no texto', () => has(w, 'text: "Ok \\"x\\""'));
t('container vazio (WebInk sem filhos)', () => has(w, 'return Page(#{});'));

console.log('Codegen — TerminalInk');
const tk = gen(terminalink);
t('uses terminalink', () => has(tk, 'uses terminalink;'));
t('telas nomeadas em render', () => has(tk, 'render(#{ ui: ui, sobre: sobre });'));
t('texto posicional', () => has(tk, 'Text(#{ color: "cyan", bold: true }, "Cadastro")'));
t('TextInput com onChange bare', () => has(tk, 'onChange: onNome'));
t('Select options como records', () => has(tk, 'Select(#{ options: [#{ label: "A", value: "1" }] })'));
t('lista expandida em Item', () => { has(tk, 'UnorderedList(#{ marker: "-" }, ['); has(tk, 'Item(#{}, "x")'); });
t('VBox vazio', () => has(tk, 'VBox(#{}, [])'));
t('ConfirmInput onConfirm', () => has(tk, 'ConfirmInput(#{ onConfirm: irSobre })'));
t('navigate no handler', () => has(tk, 'navigate("sobre");'));

console.log('Codegen — visible / fx-binding / foco');
// visible como condição (fx) e como false fixo.
const visModel = {
  xnpas: 1, target: 'webink',
  state: [{ name: 'mostrar', type: 'Boolean', initial: 'true' }],
  screens: [{ route: '/', name: 'home', root: { type: 'Page', props: {}, children: [
    { type: 'Text', props: { text: 'condicional', visible: '=mostrar' } },
    { type: 'Text', props: { text: 'oculto', visible: '=false' } },
    { type: 'Text', props: { text: 'sempre' } },
  ] } }],
};
const vis = gen(visModel);
t('visible expressão vira bare', () => has(vis, 'visible: mostrar'));
t('visible false fixo', () => has(vis, 'visible: false'));
t('sem visible quando ausente', () => { has(vis, 'Text(#{ text: "sempre" })'); hasnt(vis, 'visible: sempre'); });
// Dados vindos de variável (fx): Table.rows, Select.options, Chart.data, ProgressBar.value.
const fxModel = {
  xnpas: 1, target: 'webink',
  state: [{ name: 'linhas', type: 'Object', initial: '' }, { name: 'pct', type: 'Integer', initial: '0' }],
  screens: [{ route: '/', name: 'home', root: { type: 'Page', props: {}, children: [
    { type: 'Table', props: { columns: ['A'], rows: '=linhas' } },
    { type: 'Select', props: { options: '=linhas' } },
    { type: 'Chart', props: { type: 'bar', data: '=linhas' } },
    { type: 'ProgressBar', props: { value: '=pct' } },
  ] } }],
};
const fx = gen(fxModel);
t('Table rows fx bare', () => { has(fx, 'rows: linhas'); hasnt(fx, 'rows: "=linhas"'); });
t('Select options fx bare', () => has(fx, 'options: linhas'));
t('Chart data fx bare', () => has(fx, 'data: linhas'));
t('ProgressBar value fx bare', () => has(fx, 'value: pct'));
// TerminalInk: key + autoFocus como props e focus() no handler.
const focusModel = {
  xnpas: 1, target: 'terminalink',
  handlers: [{ name: 'aoConfirmar', returns: 'Boolean', params: [], body: 'focus("email");\nreturn true;' }],
  screens: [{ name: 'ui', route: '', root: { type: 'VBox', props: {}, children: [
    { type: 'TextInput', props: { placeholder: 'nome', key: 'nome', autoFocus: true, onSubmit: '@aoConfirmar' } },
    { type: 'TextInput', props: { placeholder: 'email', key: 'email' } },
  ] } }],
};
const fk = gen(focusModel);
t('key + autoFocus como props', () => has(fk, 'key: "nome", autoFocus: true'));
t('focus() no handler', () => has(fk, 'focus("email");'));

console.log('Codegen — casos de borda');
t('modelo inválido lança', () => {
  let threw = false; try { generate(null, 'x'); } catch (e) { threw = true; }
  if (!threw) throw new Error('deveria lançar para modelo inválido');
});
t('sem handlers/estado ainda gera', () => {
  const s = gen({ xnpas: 1, target: 'webink', screens: [{ route: '/', name: 'home', root: { type: 'Page', props: {}, children: [] } }] });
  has(s, 'render(#{ "/": home }');
});

console.log('i18n — dicionário e cobertura');
global.window = global;
const NpI18n = require('../media/i18n');
require('../media/i18n'); // also attaches global.NpI18n
require('../media/widgets-webink');
require('../media/widgets-terminalink');
t('5 idiomas (pt/en/de/fr/it)', () => {
  if (NpI18n.langs.join(',') !== 'pt,en,de,fr,it') throw new Error('idiomas: ' + NpI18n.langs);
});
t('normalize aceita locale composto e cai em pt', () => {
  if (NpI18n.normalize('pt-BR') !== 'pt') throw new Error('pt-BR');
  if (NpI18n.normalize('de') !== 'de') throw new Error('de');
  if (NpI18n.normalize('xx') !== 'pt') throw new Error('fallback');
});
t('tr traduz e cai de volta no PT', () => {
  if (NpI18n.tr('Propriedades', 'en') !== 'Properties') throw new Error('en');
  if (NpI18n.tr('Propriedades', 'de') !== 'Eigenschaften') throw new Error('de');
  if (NpI18n.tr('Propriedades', 'pt') !== 'Propriedades') throw new Error('pt identity');
  if (NpI18n.tr('__inexistente__', 'fr') !== '__inexistente__') throw new Error('fallback');
});
t('placeholders {name}/{base} preservados na tradução', () => {
  const s = NpI18n.tr('Arraste um componente da paleta para dentro do {name} para começar.', 'it');
  if (s.indexOf('{name}') < 0) throw new Error('perdeu {name}');
});
t('todos os rótulos de campo e grupos têm tradução (de)', () => {
  const KEEP = new Set(['className (Tailwind)', 'Padding', 'Placeholder', 'Layout', 'Feedback',
    'Delta (opcional)', 'Rota (href)', 'Variante']);
  const EVENTS = new Set(['onClick', 'onChange', 'onSubmit', 'onConfirm', 'onCancel']);
  const miss = [];
  for (const R of [global.WebInkWidgets, global.TerminalInkWidgets]) {
    for (const g of R.groups) if (NpI18n.tr(g, 'de') === g && !KEEP.has(g)) miss.push('grupo ' + g);
    for (const def of Object.values(R.defs))
      for (const f of (def.fields || []))
        if (f.label && f.kind !== 'event' && !EVENTS.has(f.label) && !KEEP.has(f.label) && NpI18n.tr(f.label, 'de') === f.label)
          miss.push(f.label);
  }
  if (miss.length) throw new Error('sem tradução: ' + miss.join(', '));
});

// ── optional: smoke-run on the bundled JAR ────────────────────────────────────
async function jarSmoke() {
  const jar = path.join(__dirname, '..', 'bin', 'neoobjectpascal.jar');
  if (!fs.existsSync(jar)) { console.log('JAR não encontrado — pulando smoke test.'); return; }
  console.log('Smoke test no interpretador (JAR)');
  for (const [label, model, env] of [
    ['WebInk', webink, { WEBINK_NO_BROWSER: '1' }],
    ['TerminalInk', terminalink, {}],
  ]) {
    const file = path.join(os.tmpdir(), 'xnpas-smoke-' + label + '.npas');
    fs.writeFileSync(file, gen(model), 'utf8');
    const err = await new Promise((resolve) => {
      const p = spawn('java', ['-Djava.awt.headless=true', '-jar', jar, file], { env: Object.assign({}, process.env, env) });
      let out = '';
      p.stdout.on('data', (d) => { out += d; });
      p.stderr.on('data', (d) => { out += d; });
      const timer = setTimeout(() => p.kill('SIGKILL'), 3500);
      p.on('close', () => { clearTimeout(timer); resolve(out); });
      p.on('error', () => { clearTimeout(timer); resolve('SPAWN_ERROR'); });
    });
    const parseErr = /mismatched input|missing |extraneous input|no viable alternative|Cannot access|Exception in thread/.test(err);
    t(label + ' parseia/executa no JAR', () => { if (parseErr) throw new Error('erro de parse/execução:\n' + err.slice(0, 400)); });
  }
}

(async () => {
  if (process.env.RUN_JAR === '1') await jarSmoke();
  console.log('\n' + (fail === 0 ? '✔ TODOS OS TESTES PASSARAM' : '✗ FALHAS') + `  (${pass} ok, ${fail} falhas)`);
  process.exit(fail === 0 ? 0 : 1);
})();
