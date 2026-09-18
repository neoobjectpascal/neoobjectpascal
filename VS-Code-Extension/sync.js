// sync.js — keeps a .xnpas design model and its sibling .npas source in step.
//
// The pair syncs by modification time: whichever file is newer updates the other.
// Two guards keep it from looping. First, nothing is written when the content
// would not change (and codegen/npasParser round-trip byte-for-byte, so a sync
// settles after one pass). Second, a .npas that was not produced by the UI
// Builder — or that the parser cannot read — never overwrites the .xnpas.
'use strict';
const vscode = require('vscode');
const path = require('path');
const { generate } = require('./codegen');
const { parse, isGenerated } = require('./npasParser');
const NpI18n = require('./media/i18n.js');

// Clocks and editors disagree by a few milliseconds; below this, call it a tie
// (and a tie goes to the .xnpas, the canonical source).
const MTIME_TOLERANCE_MS = 1000;

// The editor UI language: the setting `neoobjectpascal.uiBuilder.language`, where
// 'auto' (default) follows the VS Code display language, falling back to Portuguese.
function resolveLang() {
  const cfg = vscode.workspace.getConfiguration('neoobjectpascal').get('uiBuilder.language', 'auto');
  return NpI18n.normalize(!cfg || cfg === 'auto' ? (vscode.env.language || 'pt') : cfg);
}
const t = (s) => NpI18n.tr(s, resolveLang());

function syncMode() {
  return vscode.workspace.getConfiguration('neoobjectpascal').get('uiBuilder.sync', 'bidirectional');
}

function baseName(uri, ext) {
  return path.basename(uri.fsPath).replace(new RegExp('\\.' + ext + '$', 'i'), '');
}
function sibling(uri, fromExt, toExt) {
  const base = baseName(uri, fromExt);
  return vscode.Uri.file(path.join(path.dirname(uri.fsPath), base + '.' + toExt));
}
const npasOf = (xnpasUri) => sibling(xnpasUri, 'xnpas', 'npas');
const xnpasOf = (npasUri) => sibling(npasUri, 'npas', 'xnpas');

async function statOf(uri) {
  try { return await vscode.workspace.fs.stat(uri); } catch (e) { return null; }
}
async function readText(uri) {
  try { return Buffer.from(await vscode.workspace.fs.readFile(uri)).toString('utf8'); }
  catch (e) { return null; }
}

// Write only when the bytes would actually change — this is what stops the two
// files from waking each other up forever.
async function writeIfChanged(uri, text) {
  const current = await readText(uri);
  if (current === text) return false;
  await vscode.workspace.fs.writeFile(uri, Buffer.from(text, 'utf8'));
  return true;
}

function parseModel(text) {
  const trimmed = String(text || '').trim();
  return trimmed ? JSON.parse(trimmed) : { target: 'webink', screens: [] };
}

// ── .xnpas → .npas ──────────────────────────────────────────────────────────
// Generate the sibling .npas from a design model. `source` is a TextDocument or
// a Uri. Returns the .npas Uri, or null when the model could not be used.
async function writeNpas(source, opts) {
  const quiet = !!(opts && opts.quiet);
  const uri = source.uri || source;
  const text = source.getText ? source.getText() : await readText(uri);
  let model;
  try {
    model = parseModel(text);
  } catch (e) {
    if (!quiet) vscode.window.showWarningMessage(t('NeoObjectPascal UI: .xnpas inválido — .npas não foi gerado.'));
    return null;
  }
  let src;
  try {
    src = generate(model, baseName(uri, 'xnpas'));
  } catch (e) {
    if (!quiet) vscode.window.showWarningMessage(t('NeoObjectPascal UI: erro ao gerar .npas — ') + e.message);
    return null;
  }
  const target = npasOf(uri);
  await writeIfChanged(target, src);
  return target;
}

// ── .npas → .xnpas ──────────────────────────────────────────────────────────
const warned = new Set();   // one warning per file per session

async function warnNotImportable(npasUri, reason) {
  const key = npasUri.fsPath;
  if (warned.has(key)) return;
  warned.add(key);
  const base = baseName(npasUri, 'npas');
  const regen = t('Regerar .npas a partir do .xnpas');
  const keep = t('Manter como está');
  const choice = await vscode.window.showWarningMessage(
    t('NeoObjectPascal UI: {base}.npas não pôde ser lido de volta para o editor visual ({reason}). O {base}.xnpas foi preservado.')
      .replace(/\{base\}/g, base).replace('{reason}', reason),
    regen, keep);
  if (choice === regen) {
    warned.delete(key);
    await writeNpas(xnpasOf(npasUri));
  }
}

// Import a generated .npas back into its .xnpas. Returns true when the design
// model actually changed.
async function writeXnpas(npasUri) {
  const xnpasUri = xnpasOf(npasUri);
  if (!(await statOf(xnpasUri))) return false;

  // Unsaved work in the visual editor is newer than anything on disk, so the
  // import would silently throw it away. Stop and let the user decide.
  const open = (vscode.workspace.textDocuments || []).find((d) => d.uri.fsPath === xnpasUri.fsPath);
  if (open && open.isDirty) {
    const base = baseName(xnpasUri, 'xnpas');
    vscode.window.showWarningMessage(
      t('NeoObjectPascal UI: {base}.xnpas tem alterações não salvas, então o {base}.npas não foi importado. Salve o editor visual (ou desfaça) e salve o .npas de novo.')
        .replace(/\{base\}/g, base));
    return false;
  }

  const src = await readText(npasUri);
  if (src === null) return false;
  if (!isGenerated(src)) {
    await warnNotImportable(npasUri, t('não foi gerado pelo editor visual'));
    return false;
  }

  // Nothing to import when the .npas still matches what the current model emits
  // — this keeps node ids (and the .xnpas mtime) stable on a no-op save.
  const currentText = await readText(xnpasUri);
  try {
    if (generate(parseModel(currentText), baseName(xnpasUri, 'xnpas')) === src) return false;
  } catch (e) { /* unreadable .xnpas: the .npas wins below */ }

  let model;
  try {
    model = parse(src);
  } catch (e) {
    await warnNotImportable(npasUri, e.message);
    return false;
  }

  const json = JSON.stringify(model, null, 2) + '\n';
  // Edit through the TextDocument so the open custom editor refreshes and the
  // change lands in the native undo stack.
  try {
    const doc = await vscode.workspace.openTextDocument(xnpasUri);
    if (doc.getText() === json) return false;
    const edit = new vscode.WorkspaceEdit();
    edit.replace(xnpasUri, new vscode.Range(0, 0, doc.lineCount, 0), json);
    await vscode.workspace.applyEdit(edit);
    await doc.save();
  } catch (e) {
    await writeIfChanged(xnpasUri, json);
  }
  return true;
}

// ── arbitration ─────────────────────────────────────────────────────────────
// Decide which side of the pair is newer and update the other one. Returns
// 'toXnpas', 'toNpas' or 'none'.
async function arbitrate(xnpasUri) {
  const npasUri = npasOf(xnpasUri);
  const [sx, sn] = await Promise.all([statOf(xnpasUri), statOf(npasUri)]);
  if (!sx) return 'none';
  if (sn && syncMode() === 'bidirectional' && sn.mtime > sx.mtime + MTIME_TOLERANCE_MS) {
    if (await writeXnpas(npasUri)) return 'toXnpas';
    return 'none';
  }
  await writeNpas(xnpasUri, { quiet: true });
  return 'toNpas';
}

module.exports = {
  arbitrate, writeNpas, writeXnpas, npasOf, xnpasOf, syncMode,
  resolveLang, t, MTIME_TOLERANCE_MS,
};
