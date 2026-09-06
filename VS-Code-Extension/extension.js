const vscode = require('vscode');
const path = require('path');
const fs = require('fs');
const { spawn } = require('child_process');
const { UiBuilderProvider, writeSibling, t: uiT } = require('./uiBuilder');

/**
 * @param {vscode.ExtensionContext} context
 */
function activate(context) {
    console.log('NeoObjectPascal extension v2.1 is now active');

    // Register debug configuration provider
    context.subscriptions.push(
        vscode.debug.registerDebugConfigurationProvider('neoobjectpascal', {
            resolveDebugConfiguration(folder, config, token) {
                // If no configuration is provided, create a default one
                if (!config.type && !config.request && !config.name) {
                    const editor = vscode.window.activeTextEditor;
                    if (editor && editor.document.languageId === 'neoobjectpascal') {
                        config.type = 'neoobjectpascal';
                        config.name = 'Debug NeoObjectPascal File';
                        config.request = 'launch';
                        config.program = editor.document.uri.fsPath;
                        
                        // Get JAR path
                        const vsconfig = vscode.workspace.getConfiguration('neoobjectpascal');
                        let jarPath = vsconfig.get('jarPath');
                        if (!jarPath || jarPath.trim() === '') {
                            jarPath = path.join(context.extensionPath, 'bin');
                        }
                        config.jarPath = findJar(jarPath) || path.join(jarPath, 'neoobjectpascal.jar');
                    }
                }

                // If program is not specified, use active editor
                if (!config.program) {
                    const editor = vscode.window.activeTextEditor;
                    if (editor && editor.document.languageId === 'neoobjectpascal') {
                        config.program = editor.document.uri.fsPath;
                    } else {
                        return vscode.window.showErrorMessage('No NeoObjectPascal file is open').then(_ => {
                            return undefined; // abort launch
                        });
                    }
                }

                // Ensure jarPath is set
                if (!config.jarPath) {
                    const vsconfig = vscode.workspace.getConfiguration('neoobjectpascal');
                    let jarPath = vsconfig.get('jarPath');
                    if (!jarPath || jarPath.trim() === '') {
                        jarPath = path.join(context.extensionPath, 'bin');
                    }
                    config.jarPath = findJar(jarPath) || path.join(jarPath, 'neoobjectpascal.jar');
                }

                return config;
            }
        })
    );

    // Resolve a NeoObjectPascal JAR from a target that may be empty, a .jar file, or a directory.
    // Version-agnostic: prefers the stable "neoobjectpascal.jar", then any "neoobjectpascal*.jar".
    function findJar(target) {
        if (!target) return null;
        try {
            if (target.endsWith('.jar') && fs.existsSync(target)) return target;
            if (fs.existsSync(target) && fs.statSync(target).isDirectory()) {
                const stable = path.join(target, 'neoobjectpascal.jar');
                if (fs.existsSync(stable)) return stable;
                const jars = fs.readdirSync(target)
                    .filter(f => /^neoobjectpascal.*\.jar$/i.test(f)).sort();
                if (jars.length) return path.join(target, jars[jars.length - 1]);
            }
        } catch (e) { /* ignore and fall through */ }
        return null;
    }

    // Helper function to get JAR path (configured jarPath first, then the bundled bin folder)
    function getJarPath() {
        const configured = (vscode.workspace.getConfiguration('neoobjectpascal').get('jarPath') || '').trim();
        const bundledDir = path.join(context.extensionPath, 'bin');
        const jar = findJar(configured) || findJar(bundledDir);
        if (!jar) {
            throw new Error(
                `NeoObjectPascal JAR not found (bundled folder: ${bundledDir}` +
                (configured ? `, jarPath: ${configured}` : '') +
                `). Reinstall the extension, or set 'neoobjectpascal.jarPath' to a folder or a .jar file.`);
        }
        return jar;
    }

    // Helper function to get file path
    function getFilePath(uri) {
        if (uri && uri.fsPath) {
            return uri.fsPath;
        } else if (vscode.window.activeTextEditor) {
            return vscode.window.activeTextEditor.document.uri.fsPath;
        }
        return null;
    }

    // Directories skipped while searching for .npas files.
    const IGNORE_GLOB = '**/{node_modules,target,dist,build,out,.git,.vscode}/**';

    // A NeoObjectPascal program (a "main") terminates with `end.` (a dot). Units, classes,
    // records, functions and helpers end with `end;` — so `end.` at the end of the file is the
    // structural signal that this file is runnable. Case-insensitive (Object Pascal keywords are).
    function isMainProgram(content) {
        return /\bend\s*\.\s*$/i.test(content);
    }

    // Resolve the folder to operate on from a right-click uri (or the active editor):
    // a clicked folder → that folder; a clicked file → its workspace folder (else its directory);
    // nothing clicked → the first workspace folder.
    function resolveRoot(uri) {
        const clicked = uri && uri.fsPath
            ? uri.fsPath
            : (vscode.window.activeTextEditor ? vscode.window.activeTextEditor.document.uri.fsPath : null);
        if (clicked) {
            try {
                if (fs.existsSync(clicked) && fs.statSync(clicked).isDirectory()) return clicked;
            } catch (e) { /* ignore */ }
            const wf = vscode.workspace.getWorkspaceFolder(vscode.Uri.file(clicked));
            return wf ? wf.uri.fsPath : path.dirname(clicked);
        }
        const folders = vscode.workspace.workspaceFolders;
        return folders && folders.length ? folders[0].uri.fsPath : null;
    }

    // Find every runnable program (non-test .npas ending with `end.`) under a root directory.
    async function findMainFiles(rootDir) {
        const pattern = new vscode.RelativePattern(rootDir, '**/*.npas');
        const uris = await vscode.workspace.findFiles(pattern, IGNORE_GLOB);
        const mains = [];
        for (const u of uris) {
            const f = u.fsPath;
            if (f.endsWith('.test.npas')) continue;
            try {
                if (isMainProgram(fs.readFileSync(f, 'utf8'))) mains.push(f);
            } catch (e) { /* unreadable — skip */ }
        }
        mains.sort();
        return mains;
    }

    // Resolve WHICH main file to run/debug: auto-detect the `end.` program(s) under the root.
    // 0 found → error (with a sensible fallback to the clicked file). 1 → use it. >1 → ask the user.
    // Returns null when there is nothing to run or the user cancels the picker.
    async function resolveMainFile(uri) {
        const root = resolveRoot(uri);
        if (!root) {
            vscode.window.showErrorMessage('No NeoObjectPascal folder or workspace is open.');
            return null;
        }
        const mains = await findMainFiles(root);

        if (mains.length === 0) {
            const clicked = uri && uri.fsPath ? uri.fsPath : getFilePath(null);
            if (clicked && clicked.endsWith('.npas') && !clicked.endsWith('.test.npas')) {
                return clicked; // fallback: run the clicked file even if `end.` wasn't detected
            }
            vscode.window.showErrorMessage('No main program found — no .npas file ending with "end." under: ' + root);
            return null;
        }
        if (mains.length === 1) {
            return mains[0];
        }
        // More than one program: let the user choose which one to run/debug.
        const items = mains.map(f => ({
            label: '$(file-code) ' + path.basename(f),
            description: path.relative(root, f),
            file: f
        }));
        const pick = await vscode.window.showQuickPick(items, {
            placeHolder: 'Multiple programs found — select which one to run (file ending with "end.")',
            matchOnDescription: true
        });
        return pick ? pick.file : null; // null → user cancelled, do nothing
    }

    // Set the menu context keys (project has any .npas / has any *.test.npas) so the
    // right-click menu can show "Run All Unit Tests" only when tests exist, and the folder
    // Run/Debug entries only inside a NeoObjectPascal project.
    async function updateContextKeys() {
        let hasNpas = false;
        let hasTests = false;
        try {
            const uris = await vscode.workspace.findFiles('**/*.npas', IGNORE_GLOB);
            hasNpas = uris.length > 0;
            hasTests = uris.some(u => u.fsPath.endsWith('.test.npas'));
        } catch (e) { /* leave both false */ }
        vscode.commands.executeCommand('setContext', 'neoobjectpascal:hasNpas', hasNpas);
        vscode.commands.executeCommand('setContext', 'neoobjectpascal:hasTests', hasTests);
    }

    // Run the JAR inside a pseudoterminal we fully control: a clean framed output with NO shell and
    // NO echoed command line — identical on Windows, Linux and macOS (VS Code renders the ANSI itself).
    // stdin is piped so interactive ReadLn works.
    function runInPty(jarPath, args, targetPath, terminalName = 'NeoObjectPascal') {
        const name = path.basename(targetPath);
        const writeEmitter = new vscode.EventEmitter();
        const nl = '\r\n';
        const reset = '\x1b[0m', dim = '\x1b[2m', amber = '\x1b[1;33m', green = '\x1b[1;32m', red = '\x1b[1;31m';
        const rule = dim + '─'.repeat(46) + reset;
        const write = (s) => writeEmitter.fire(String(s).replace(/\r?\n/g, nl));
        let child = null;
        let lineBuf = '';

        const pty = {
            onDidWrite: writeEmitter.event,
            open: () => {
                write(amber + '▶ NeoObjectPascal' + reset + ' · ' + name + nl);
                write(rule + nl);
                try {
                    child = spawn('java', ['-jar', jarPath, ...args, targetPath]);
                } catch (e) {
                    write(red + 'Failed to start java: ' + e.message + reset + nl);
                    return;
                }
                child.stdout.on('data', (d) => write(d.toString('utf8')));
                child.stderr.on('data', (d) => write(d.toString('utf8')));
                child.on('error', (e) => write(red + 'Failed to start java (is Java 11+ on your PATH?): ' + e.message + reset + nl));
                child.on('close', (code) => {
                    write(rule + nl);
                    write((code === 0 ? green + '✓ Concluído' : red + '✗ Erro (código ' + code + ')') + reset + nl);
                    child = null;
                });
            },
            close: () => { if (child) { try { child.kill(); } catch (e) { /* ignore */ } child = null; } },
            handleInput: (data) => {
                if (!child || !child.stdin || !child.stdin.writable) return;
                for (const ch of data) {
                    if (ch === '\r') { write(nl); child.stdin.write(lineBuf + '\n'); lineBuf = ''; }
                    else if (ch === '\x7f' || ch === '\b') { if (lineBuf.length) { lineBuf = lineBuf.slice(0, -1); write('\b \b'); } }
                    else if (ch === '\x03') { child.kill(); }            // Ctrl+C
                    else if (ch >= ' ') { lineBuf += ch; write(ch); }    // printable
                }
            }
        };

        const existing = vscode.window.terminals.find((t) => t.name === terminalName);
        if (existing) existing.dispose();
        const terminal = vscode.window.createTerminal({ name: terminalName, pty });
        terminal.show();
    }

    // Helper function to execute command in terminal
    function executeInTerminal(command, terminalName = 'NeoObjectPascal') {
        let terminal = vscode.window.terminals.find(t => t.name === terminalName);
        if (!terminal) {
            terminal = vscode.window.createTerminal(terminalName);
        }
        terminal.show();
        terminal.sendText(command);
        return terminal;
    }

    // Command: Run NeoObjectPascal File (auto-detects the main; prompts if there are several)
    let runCommand = vscode.commands.registerCommand('neoobjectpascal.run', async (uri) => {
        try {
            const filePath = await resolveMainFile(uri);
            if (!filePath) {
                return; // no main found / user cancelled — resolveMainFile already reported it
            }

            const jarPath = getJarPath();
            runInPty(jarPath, [], filePath, 'NeoObjectPascal');
            vscode.window.showInformationMessage('Running: ' + path.basename(filePath));

        } catch (error) {
            vscode.window.showErrorMessage('Error: ' + error.message);
        }
    });

    // Command: Debug NeoObjectPascal File (auto-detects the main; prompts if there are several)
    let debugCommand = vscode.commands.registerCommand('neoobjectpascal.debug', async (uri) => {
        try {
            const filePath = await resolveMainFile(uri);
            if (!filePath) {
                return; // no main found / user cancelled
            }

            const jarPath = getJarPath();

            // Start debug session
            vscode.debug.startDebugging(undefined, {
                type: 'neoobjectpascal',
                name: 'Debug ' + path.basename(filePath),
                request: 'launch',
                program: filePath,
                jarPath: jarPath,
                stopOnEntry: true
            });

        } catch (error) {
            vscode.window.showErrorMessage('Error starting debugger: ' + error.message);
        }
    });

    // Command: Execute Unit Test
    let runTestCommand = vscode.commands.registerCommand('neoobjectpascal.runTest', async (uri) => {
        try {
            const filePath = getFilePath(uri);
            if (!filePath) {
                vscode.window.showErrorMessage('No test file selected');
                return;
            }

            if (!filePath.endsWith('.test.npas')) {
                vscode.window.showErrorMessage('Selected file is not a .test.npas file');
                return;
            }

            const jarPath = getJarPath();
            runInPty(jarPath, ['-t'], filePath, 'NeoObjectPascal Tests');
            vscode.window.showInformationMessage('Running test: ' + path.basename(filePath));

        } catch (error) {
            vscode.window.showErrorMessage('Error running test: ' + error.message);
        }
    });

    // Command: Execute All Unit Tests (Recursive with Coverage)
    let runAllTestsCommand = vscode.commands.registerCommand('neoobjectpascal.runAllTests', async (uri) => {
        try {
            // Works from any .npas file OR a folder — resolve the directory to scan recursively.
            const root = resolveRoot(uri);
            if (!root) {
                vscode.window.showErrorMessage('No NeoObjectPascal folder or workspace is open.');
                return;
            }

            const jarPath = getJarPath();

            // Use --test-all mode for recursive test execution with coverage
            runInPty(jarPath, ['--test-all'], root, 'NeoObjectPascal Tests');

            vscode.window.showInformationMessage('Running all tests recursively with coverage report...');

        } catch (error) {
            vscode.window.showErrorMessage('Error running tests: ' + error.message);
        }
    });

    // Command: Execute on Cloud
    let executeOnCloudCommand = vscode.commands.registerCommand('neoobjectpascal.executeOnCloud', async (uri) => {
        // ── Feature temporarily disabled — Coming Soon ──────────────────────────────
        // Cloud execution is not available yet. Inform the user and stop here.
        // The original implementation is preserved below and will be re-enabled when
        // the feature ships (remove this notice + return to restore it).
        vscode.window.showInformationMessage(
            'Execute Project On Cloud is coming soon — this feature is currently disabled.'
        );
        return;

        try {
            const filePath = getFilePath(uri);
            if (!filePath) {
                vscode.window.showErrorMessage('No NeoObjectPascal file selected');
                return;
            }

            if (!filePath.endsWith('.npas')) {
                vscode.window.showErrorMessage('Selected file is not a .npas file');
                return;
            }

            const config = vscode.workspace.getConfiguration('neoobjectpascal');
            const cloudUrl = config.get('cloudUrl') || 'http://localhost:8000';
            
            // Get project name (use workspace folder name or ask user)
            const workspaceFolder = vscode.workspace.getWorkspaceFolder(vscode.Uri.file(filePath));
            const defaultProjectName = workspaceFolder ? path.basename(workspaceFolder.uri.fsPath) : 'my_project';
            
            const projectName = await vscode.window.showInputBox({
                prompt: 'Enter project name',
                value: defaultProjectName,
                placeHolder: 'my_project'
            });

            if (!projectName) {
                return; // User cancelled
            }

            // Get username
            let username = config.get('cloudUsername');
            if (!username || username.trim() === '') {
                username = await vscode.window.showInputBox({
                    prompt: 'Enter NeoObjectPascal Cloud username',
                    placeHolder: 'user@example.com'
                });
            }

            if (!username) {
                return; // User cancelled
            }

            // Get password
            let password = config.get('cloudPassword');
            if (!password || password.trim() === '') {
                password = await vscode.window.showInputBox({
                    prompt: 'Enter NeoObjectPascal Cloud password',
                    password: true,
                    placeHolder: 'password'
                });
            }

            if (!password) {
                return; // User cancelled
            }

            const jarPath = getJarPath();
            const command = `java -jar "${jarPath}" --execute-on-cloud "${cloudUrl}" "${projectName}" "${username}" "${password}" "${filePath}"`;
            
            executeInTerminal(command, 'NeoObjectPascal Cloud');
            vscode.window.showInformationMessage('Executing on cloud: ' + path.basename(filePath));

        } catch (error) {
            vscode.window.showErrorMessage('Error executing on cloud: ' + error.message);
        }
    });

    // Command: Build Native Executable (packages the whole project into an app-image via jpackage:
    // .app on macOS, .exe folder on Windows, bin/ on Linux). Icon from a PNG.
    let buildCommand = vscode.commands.registerCommand('neoobjectpascal.build', async (uri) => {
        try {
            const filePath = await resolveMainFile(uri);
            if (!filePath) {
                return; // no main found / user cancelled — already reported
            }

            // App/executable name (default: capitalized program basename).
            const base = path.basename(filePath).replace(/\.npas$/i, '');
            const defaultName = base ? base.charAt(0).toUpperCase() + base.slice(1) : 'NeoApp';
            const appName = await vscode.window.showInputBox({
                prompt: 'Name of the native app / executable',
                value: defaultName,
                validateInput: (v) => (v && v.trim() ? null : 'Please enter a name')
            });
            if (!appName) {
                return; // cancelled
            }

            // Optional icon (PNG → converted per platform by the interpreter).
            let iconPath = null;
            const iconChoice = await vscode.window.showQuickPick(
                [
                    { label: '$(file-media) Choose icon (PNG)…', pick: 'choose' },
                    { label: '$(circle-slash) No icon', pick: 'none' }
                ],
                { placeHolder: 'App icon' }
            );
            if (!iconChoice) {
                return; // cancelled
            }
            if (iconChoice.pick === 'choose') {
                const picked = await vscode.window.showOpenDialog({
                    canSelectMany: false,
                    openLabel: 'Use as icon',
                    filters: { 'PNG image': ['png'] }
                });
                if (!picked || !picked.length) {
                    return; // cancelled
                }
                iconPath = picked[0].fsPath;
            }

            // Output directory: <workspace folder or program dir>/dist.
            const wf = vscode.workspace.getWorkspaceFolder(vscode.Uri.file(filePath));
            const outputDir = path.join(wf ? wf.uri.fsPath : path.dirname(filePath), 'dist');

            const jarPath = getJarPath();
            const args = ['--build', '--name', appName, '--output', outputDir];
            if (iconPath) {
                args.push('--icon', iconPath);
            }
            // runInPty appends the program path as the last argument (java -jar ... --build ... <program>).
            runInPty(jarPath, args, filePath, 'NeoObjectPascal Build');
            vscode.window.showInformationMessage('Building native app: ' + appName + ' → ' + outputDir);

        } catch (error) {
            vscode.window.showErrorMessage('Error: ' + error.message);
        }
    });

    // Register all commands
    context.subscriptions.push(runCommand);
    context.subscriptions.push(debugCommand);
    context.subscriptions.push(runTestCommand);
    context.subscriptions.push(runAllTestsCommand);
    context.subscriptions.push(buildCommand);
    context.subscriptions.push(executeOnCloudCommand);

    // Menu context keys: keep them in sync with the workspace so "Run All Unit Tests" (and the
    // folder Run/Debug entries) appear only when the project actually has .npas / *.test.npas files.
    updateContextKeys();
    const npasWatcher = vscode.workspace.createFileSystemWatcher('**/*.npas');
    npasWatcher.onDidCreate(updateContextKeys);
    npasWatcher.onDidDelete(updateContextKeys);
    context.subscriptions.push(npasWatcher);
    context.subscriptions.push(
        vscode.workspace.onDidChangeWorkspaceFolders(updateContextKeys)
    );

    // Register debug configuration provider
    context.subscriptions.push(
        vscode.debug.registerDebugConfigurationProvider('neoobjectpascal', new NeoObjectPascalDebugConfigurationProvider())
    );

    // ── Visual UI Builder (.xnpas) ─────────────────────────────────────────────
    // Custom editor for .xnpas + one-way sync: saving the .xnpas regenerates the
    // sibling .npas. A command scaffolds new .xnpas files.
    context.subscriptions.push(UiBuilderProvider.register(context));
    context.subscriptions.push(
        vscode.workspace.onDidSaveTextDocument(async (doc) => {
            if (doc.uri.fsPath.toLowerCase().endsWith('.xnpas')) {
                await writeSibling(doc);
            }
        })
    );
    context.subscriptions.push(
        vscode.commands.registerCommand('neoobjectpascal.newUi', () => createNewUiFile())
    );

    // Guard: warn when a generated .npas is focused — it is overwritten on .xnpas save.
    const warnedGenerated = new Set();
    context.subscriptions.push(
        vscode.window.onDidChangeActiveTextEditor(async (editor) => {
            if (!editor) return;
            const doc = editor.document;
            const p = doc.uri.fsPath;
            if (!p.toLowerCase().endsWith('.npas') || warnedGenerated.has(p)) return;
            const head = doc.getText(new vscode.Range(0, 0, 6, 0));
            if (!/NeoObjectPascal UI Builder/.test(head) || !/GERADO por/.test(head)) return;
            const base = path.basename(p).replace(/\.npas$/i, '');
            const xnpasUri = vscode.Uri.file(path.join(path.dirname(p), base + '.xnpas'));
            try { await vscode.workspace.fs.stat(xnpasUri); } catch (e) { return; }
            warnedGenerated.add(p);
            const openLabel = uiT('Abrir editor visual');
            const choice = await vscode.window.showWarningMessage(
                uiT('{base}.npas é gerado pelo editor visual ({base}.xnpas) e será sobrescrito ao salvar o .xnpas. Edite a interface pelo editor visual.').replace(/\{base\}/g, base),
                openLabel
            );
            if (choice === openLabel) {
                vscode.commands.executeCommand('vscode.openWith', xnpasUri, 'neoobjectpascal.uiBuilder');
            }
        })
    );
}

// Starter templates for new .xnpas files.
function buildTemplate(kind) {
    const T = {
        'webink-blank': {
            xnpas: 1, target: 'webink', state: [], handlers: [],
            screens: [{ route: '/', name: 'home', root: { type: 'Page', props: {}, children: [] } }],
        },
        'webink-dashboard': {
            xnpas: 1, target: 'webink',
            state: [{ name: 'cliques', type: 'Integer', initial: '0' }],
            handlers: [{ name: 'registrar', returns: 'Boolean', params: [], body: 'cliques := cliques + 1;\nreturn true;' }],
            screens: [{ route: '/', name: 'home', root: { type: 'Page', props: {}, children: [
                { type: 'Navbar', props: {}, children: [{ type: 'Heading', props: { level: 3, text: 'Acme Inc.' } }] },
                { type: 'Container', props: { className: 'py-8 space-y-6' }, children: [
                    { type: 'Heading', props: { level: 1, text: 'Meu Painel' } },
                    { type: 'Grid', props: { cols: 3 }, children: [
                        { type: 'StatCard', props: { label: 'Receita', value: 'R$ 128k', delta: '+12%' } },
                        { type: 'StatCard', props: { label: 'Usuários', value: '3.420', delta: '+5%' } },
                        { type: 'StatCard', props: { label: 'Pedidos', value: '512' } },
                    ] },
                    { type: 'Card', props: {}, children: [
                        { type: 'Heading', props: { level: 4, text: 'Vendas por mês' } },
                        { type: 'Chart', props: { type: 'bar', data: { labels: ['Jan', 'Fev', 'Mar', 'Abr'], datasets: [{ label: 'Vendas', data: [10, 20, 15, 25] }] } } },
                    ] },
                    { type: 'Card', props: {}, children: [
                        { type: 'StatCard', props: { label: 'Cliques', value: '=cliques' } },
                        { type: 'Button', props: { text: 'Registrar clique', variant: 'primary', onClick: '@registrar' } },
                    ] },
                ] },
            ] } }],
        },
        'terminalink-blank': {
            xnpas: 1, target: 'terminalink', state: [], handlers: [],
            screens: [{ name: 'ui', route: '', root: { type: 'VBox', props: { padding: 1, gap: 1 }, children: [] } }],
        },
        'terminalink-form': {
            xnpas: 1, target: 'terminalink',
            state: [{ name: 'nome', type: 'String', initial: '' }],
            handlers: [{ name: 'onNome', returns: 'Boolean', params: ['v'], body: 'nome := v;\nreturn true;' }],
            screens: [{ name: 'ui', route: '', root: { type: 'VBox', props: { padding: 1, gap: 1, border: 'round', borderColor: 'cyan' }, children: [
                { type: 'Text', props: { text: 'Cadastro', color: 'cyan', bold: true } },
                { type: 'HBox', props: { gap: 2 }, children: [
                    { type: 'Text', props: { text: 'Nome:', dim: true } },
                    { type: 'TextInput', props: { placeholder: 'seu nome…', onChange: '@onNome' } },
                ] },
                { type: 'Text', props: { text: '="Olá, " + nome' } },
                { type: 'HBox', props: { gap: 2 }, children: [
                    { type: 'Badge', props: { color: 'green', text: 'OK' } },
                    { type: 'Text', props: { text: 'Enter confirma · q sai', dim: true } },
                ] },
            ] } }],
        },
    };
    return T[kind] || T['webink-blank'];
}

// Create a new .xnpas UI Builder file (from a template) and open it in the visual editor.
async function createNewUiFile() {
    const pick = await vscode.window.showQuickPick([
        { label: uiT('WebInk — em branco'), description: uiT('Página web vazia'), kind: 'webink-blank' },
        { label: 'WebInk — Dashboard', description: uiT('Navbar, StatCards, gráfico e contador'), kind: 'webink-dashboard' },
        { label: uiT('TerminalInk — em branco'), description: uiT('Tela de terminal vazia'), kind: 'terminalink-blank' },
        { label: uiT('TerminalInk — Formulário'), description: uiT('Título, campo e badge'), kind: 'terminalink-form' },
    ], { placeHolder: uiT('Escolha um modelo para o novo arquivo .xnpas') });
    if (!pick) return;

    const name = await vscode.window.showInputBox({
        prompt: uiT('Nome do arquivo (.xnpas)'),
        value: 'tela',
        validateInput: (v) => (/^[A-Za-z_][\w-]*$/.test(v || '') ? null : uiT('Use letras, números, _ ou - (começando por letra).')),
    });
    if (!name) return;

    let dir;
    const active = vscode.window.activeTextEditor;
    const folders = vscode.workspace.workspaceFolders;
    if (active && active.document.uri.scheme === 'file') dir = path.dirname(active.document.uri.fsPath);
    else if (folders && folders.length) dir = folders[0].uri.fsPath;
    else { vscode.window.showErrorMessage(uiT('Abra uma pasta no VS Code para criar o arquivo.')); return; }

    const uri = vscode.Uri.file(path.join(dir, name + '.xnpas'));
    const scaffold = JSON.stringify(buildTemplate(pick.kind), null, 2);
    try {
        await vscode.workspace.fs.writeFile(uri, Buffer.from(scaffold, 'utf8'));
    } catch (e) {
        vscode.window.showErrorMessage(uiT('Erro ao criar .xnpas: ') + e.message);
        return;
    }
    await vscode.commands.executeCommand('vscode.openWith', uri, 'neoobjectpascal.uiBuilder');
}

// Debug Configuration Provider
class NeoObjectPascalDebugConfigurationProvider {
    resolveDebugConfiguration(folder, config, token) {
        // If launch.json is missing or empty
        if (!config.type && !config.request && !config.name) {
            const editor = vscode.window.activeTextEditor;
            if (editor && editor.document.languageId === 'neoobjectpascal') {
                config.type = 'neoobjectpascal';
                config.name = 'Debug';
                config.request = 'launch';
                config.program = '${file}';
                config.stopOnEntry = true;
            }
        }

        if (!config.program) {
            return vscode.window.showInformationMessage("Cannot find a program to debug").then(_ => {
                return undefined; // abort launch
            });
        }

        return config;
    }
}

function deactivate() {
    console.log('NeoObjectPascal extension is now deactivated');
}

module.exports = {
    activate,
    deactivate
};
