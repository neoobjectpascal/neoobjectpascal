// uiBuilder.js — CustomTextEditorProvider for .xnpas (the visual UI Builder).
// The .xnpas file is a JSON document; the webview edits it via WorkspaceEdit
// (so undo/redo/dirty/save are native). The sibling .npas is kept in step by
// sync.js: whichever of the two files is newer updates the other.
'use strict';
const vscode = require('vscode');
const { arbitrate, writeNpas, resolveLang, syncMode, t } = require('./sync');

const VIEW_TYPE = 'neoobjectpascal.uiBuilder';

// Generate and write the sibling <name>.npas next to <name>.xnpas.
const writeSibling = (document) => writeNpas(document);

class UiBuilderProvider {
  constructor(context) { this.context = context; }

  static register(context) {
    const provider = new UiBuilderProvider(context);
    return vscode.window.registerCustomEditorProvider(VIEW_TYPE, provider, {
      webviewOptions: { retainContextWhenHidden: true },
      supportsMultipleEditorsPerDocument: false,
    });
  }

  async resolveCustomTextEditor(document, panel, _token) {
    const webview = panel.webview;
    webview.options = {
      enableScripts: true,
      localResourceRoots: [vscode.Uri.joinPath(this.context.extensionUri, 'media')],
    };
    webview.html = this.getHtml(webview);

    const post = () => webview.postMessage({ type: 'init', text: document.getText() });
    const postLang = () => webview.postMessage({ type: 'lang', lang: resolveLang(), sync: syncMode() });

    const changeSub = vscode.workspace.onDidChangeTextDocument((e) => {
      if (e.document.uri.toString() === document.uri.toString()) post();
    });
    // Keep the editor in sync when the language setting changes elsewhere.
    const cfgSub = vscode.workspace.onDidChangeConfiguration((e) => {
      if (e.affectsConfiguration('neoobjectpascal.uiBuilder.language')
        || e.affectsConfiguration('neoobjectpascal.uiBuilder.sync')) postLang();
    });
    panel.onDidDispose(() => { changeSub.dispose(); cfgSub.dispose(); });

    webview.onDidReceiveMessage(async (msg) => {
      switch (msg && msg.type) {
        case 'ready':
          postLang();
          // A .npas edited since the last save wins: adopt it before the first
          // paint, so the builder never opens on a stale model.
          try { await arbitrate(document.uri); } catch (e) { /* keep the model as-is */ }
          post();
          break;
        case 'update': await this.applyEdit(document, msg.text); break;
        case 'undo': await vscode.commands.executeCommand('undo'); break;
        case 'redo': await vscode.commands.executeCommand('redo'); break;
        case 'run': await this.runLive(document); break;
        case 'setLang':
          await vscode.workspace.getConfiguration('neoobjectpascal')
            .update('uiBuilder.language', NpI18n.normalize(msg.lang), vscode.ConfigurationTarget.Global);
          break;
      }
    });
  }

  applyEdit(document, newText) {
    if (document.getText() === newText) return Promise.resolve(true);
    const edit = new vscode.WorkspaceEdit();
    const full = new vscode.Range(0, 0, document.lineCount, 0);
    edit.replace(document.uri, full, newText);
    return vscode.workspace.applyEdit(edit);
  }

  async runLive(document) {
    if (document.isDirty) await document.save();        // triggers the save hook → writes .npas
    const npasUri = await writeSibling(document);        // ensure it's fresh regardless
    if (npasUri) {
      try { await vscode.commands.executeCommand('neoobjectpascal.run', npasUri); }
      catch (e) { vscode.window.showErrorMessage(t('Não foi possível rodar: ') + e.message); }
    }
  }

  getHtml(webview) {
    const nonce = String(Math.random()).slice(2) + String(Date.now());
    const uri = (f) => webview.asWebviewUri(vscode.Uri.joinPath(this.context.extensionUri, 'media', f));
    const csp = [
      "default-src 'none'",
      `img-src ${webview.cspSource} data:`,
      `style-src ${webview.cspSource} 'unsafe-inline'`,
      `script-src 'nonce-${nonce}'`,
      `font-src ${webview.cspSource}`,
    ].join('; ');
    return `<!doctype html><html lang="pt-BR"><head><meta charset="utf-8">
<meta http-equiv="Content-Security-Policy" content="${csp}">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="${uri('builder.css')}">
<link rel="stylesheet" href="${uri('preview.css')}">
<link rel="stylesheet" href="${uri('preview-terminal.css')}">
<title>NeoObjectPascal UI Builder</title></head>
<body><div class="app">
  <div class="toolbar" id="toolbar"></div>
  <div class="main">
    <div class="palette" id="palette"></div>
    <div class="canvas">
      <div class="canvas-bar">Canvas — pré-visualização real · arraste da paleta para inserir · clique para selecionar</div>
      <div class="stage" id="stage"></div>
    </div>
    <div class="inspector" id="inspector">
      <div class="itabs" id="itabs">
        <button data-tab="props" class="on">Propriedades</button>
        <button data-tab="state">Estado</button>
        <button data-tab="events">Eventos</button>
      </div>
      <div class="ibody" id="ibody"></div>
    </div>
  </div>
</div>
<script nonce="${nonce}" src="${uri('i18n.js')}"></script>
<script nonce="${nonce}" src="${uri('icons.js')}"></script>
<script nonce="${nonce}" src="${uri('widgets-webink.js')}"></script>
<script nonce="${nonce}" src="${uri('widgets-terminalink.js')}"></script>
<script nonce="${nonce}" src="${uri('widgets-desktopink.js')}"></script>
<script nonce="${nonce}" src="${uri('builder.js')}"></script>
</body></html>`;
  }
}

module.exports = { UiBuilderProvider, writeSibling, VIEW_TYPE, resolveLang, t, arbitrate };
