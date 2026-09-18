#!/usr/bin/env node
// Tests for sync.js — the .xnpas ⇄ .npas arbitration — against real files on
// disk, with the `vscode` module stubbed (the host API is not available here).
// Run:  node test/sync-tests.js
'use strict';
const Module = require('module');
const fs = require('fs');
const os = require('os');
const path = require('path');

let pass = 0, fail = 0;
function t(name, fn) {
  try { fn(); pass++; console.log('  ✓ ' + name); }
  catch (e) { fail++; console.log('  ✗ ' + name + '\n      ' + e.message); }
}
function eq(actual, expected, what) {
  if (actual !== expected) throw new Error(what + ': esperava ' + JSON.stringify(expected) + ' e veio ' + JSON.stringify(actual));
}

// ── stub of the VS Code host API ─────────────────────────────────────────────
const config = { 'uiBuilder.sync': 'bidirectional', 'uiBuilder.language': 'pt' };
const warnings = [];
const vscode = {
  env: { language: 'pt' },
  Uri: { file: (p) => ({ fsPath: p, toString: () => 'file://' + p }) },
  Range: function () {},
  WorkspaceEdit: function () {
    this.replace = (uri, range, text) => { this._uri = uri; this._text = text; };
  },
  window: { showWarningMessage: (m) => { warnings.push(m); return Promise.resolve(undefined); } },
  workspace: {
    textDocuments: [],
    getConfiguration: () => ({ get: (k, d) => (k in config ? config[k] : d) }),
    fs: {
      stat: async (u) => ({ mtime: fs.statSync(u.fsPath).mtimeMs }),
      readFile: async (u) => fs.readFileSync(u.fsPath),
      writeFile: async (u, buf) => fs.writeFileSync(u.fsPath, buf),
    },
    openTextDocument: async (u) => ({
      getText: () => fs.readFileSync(u.fsPath, 'utf8'),
      lineCount: fs.readFileSync(u.fsPath, 'utf8').split('\n').length,
      save: async () => true,
    }),
    applyEdit: async (e) => { fs.writeFileSync(e._uri.fsPath, e._text); return true; },
  },
};
const load = Module._load;
Module._load = function (request) {
  return request === 'vscode' ? vscode : load.apply(this, arguments);
};

const sync = require('../sync');
const { generate } = require('../codegen');

// ── fixture ──────────────────────────────────────────────────────────────────
const dir = fs.mkdtempSync(path.join(os.tmpdir(), 'npas-sync-'));
const xnpas = vscode.Uri.file(path.join(dir, 'teste.xnpas'));
const npas = vscode.Uri.file(path.join(dir, 'teste.npas'));
const model = {
  xnpas: 1, target: 'webink', state: [], handlers: [],
  screens: [{ route: '/', name: 'home', root: { id: 'n1', type: 'Page', props: {}, children: [
    { id: 'n2', type: 'Heading', props: { level: 1, text: 'Painel' } },
  ] } }],
};
const read = (u) => fs.readFileSync(u.fsPath, 'utf8');
const write = (u, s) => fs.writeFileSync(u.fsPath, s);
// Age a file forward so the arbitration has an unambiguous winner.
const touch = (u, msAhead) => {
  const when = (Date.now() + msAhead) / 1000;
  fs.utimesSync(u.fsPath, when, when);
};
const textOf = (m) => m.screens[0].root.children[0].props.text;

(async () => {
  console.log('Sync — arbitragem .xnpas ⇄ .npas');
  write(xnpas, JSON.stringify(model, null, 2));

  const r1 = await sync.arbitrate(xnpas);
  t('gera o .npas quando só existe o .xnpas', () => {
    eq(r1, 'toNpas', 'direção');
    if (!fs.existsSync(npas.fsPath)) throw new Error('.npas não foi criado');
  });
  const gerado = read(npas);

  const mtimeBefore = fs.statSync(npas.fsPath).mtimeMs;
  await new Promise((r) => setTimeout(r, 30));
  await sync.arbitrate(xnpas);
  t('não reescreve quando o conteúdo seria igual', () => {
    eq(fs.statSync(npas.fsPath).mtimeMs, mtimeBefore, 'mtime do .npas');
  });

  write(npas, gerado.replace('"Painel"', '"Painel Novo"'));
  touch(npas, 5000);
  const r3 = await sync.arbitrate(xnpas);
  t('o .npas mais recente atualiza o .xnpas', () => {
    eq(r3, 'toXnpas', 'direção');
    eq(textOf(JSON.parse(read(xnpas))), 'Painel Novo', 'texto importado');
  });

  const x4 = read(xnpas), n4 = read(npas);
  await sync.arbitrate(xnpas);
  await sync.arbitrate(xnpas);
  t('o sincronismo estabiliza (sem pingue-pongue)', () => {
    eq(read(xnpas), x4, '.xnpas');
    eq(read(npas), n4, '.npas');
  });

  const m5 = JSON.parse(x4);
  m5.screens[0].root.children[0].props.text = 'Do editor visual';
  write(xnpas, JSON.stringify(m5, null, 2));
  touch(xnpas, 9000);
  await sync.arbitrate(xnpas);
  t('o .xnpas mais recente atualiza o .npas', () => {
    if (read(npas).indexOf('"Do editor visual"') < 0) throw new Error('.npas não foi regerado');
  });

  // Desenho com alterações não salvas no editor visual: o .npas não entra.
  const naoSalvo = read(xnpas);
  write(npas, read(npas).replace('"Do editor visual"', '"Do codigo"'));
  touch(npas, 12000);
  vscode.workspace.textDocuments = [{ uri: xnpas, isDirty: true }];
  const avisosAntes = warnings.length;
  const rDirty = await sync.arbitrate(xnpas);
  vscode.workspace.textDocuments = [];
  t('.xnpas com alterações não salvas bloqueia a importação', () => {
    eq(rDirty, 'none', 'direção');
    eq(read(xnpas), naoSalvo, '.xnpas');
    eq(warnings.length, avisosAntes + 1, 'avisos mostrados');
  });

  const preservado = read(xnpas);
  write(npas, 'uses webink;\n\nbegin\n    WriteLn("oi");\nend.\n');
  touch(npas, 20000);
  const avisosAntesMao = warnings.length;
  const rMao = await sync.arbitrate(xnpas);
  t('.npas escrito à mão não sobrescreve o .xnpas', () => {
    eq(rMao, 'none', 'direção');
    eq(read(xnpas), preservado, '.xnpas');
    eq(warnings.length, avisosAntesMao + 1, 'avisos mostrados');
  });

  config['uiBuilder.sync'] = 'xnpasFirst';
  const r7 = await sync.arbitrate(xnpas);
  t('o modo xnpasFirst mantém o sentido único', () => {
    eq(r7, 'toNpas', 'direção');
    eq(read(npas), generate(m5, 'teste'), '.npas regerado');
  });
  config['uiBuilder.sync'] = 'bidirectional';

  fs.rmSync(dir, { recursive: true, force: true });
  console.log('\n' + (fail === 0 ? '✔ TODOS OS TESTES PASSARAM' : '✗ FALHAS') + `  (${pass} ok, ${fail} falhas)`);
  process.exit(fail === 0 ? 0 : 1);
})();
