const { spawn } = require('child_process');
const net = require('net');
const path = require('path');
const fs = require('fs');

/**
 * Debug Adapter that communicates with NeoObjectPascal via DAP protocol
 */
class NeoObjectPascalDebugAdapter {
    constructor() {
        this.debugProcess = null;
        this.debuggerSocket = null;
        this.dapSocketServer = null;
        this.pendingDebuggerMessages = [];
        this.pendingClientRequests = new Map();
        this.didTerminate = false;
        this.sequence = 1;
        
        // Separate buffers for VS Code and Debugger
        this.vscodeBuffer = Buffer.alloc(0);
        this.vscodeContentLength = 0;
        
        this.debuggerBuffer = Buffer.alloc(0);
        this.debuggerContentLength = 0;
    }

    start(inputStream, outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        // Read messages from VS Code
        inputStream.on('data', (data) => {
            this.handleVSCodeData(data);
        });

        inputStream.on('close', () => {
            this.shutdown();
        });

        inputStream.on('error', (error) => {
            console.error('Input stream error:', error);
        });
    }

    handleVSCodeData(data) {
        this.vscodeBuffer = Buffer.concat([this.vscodeBuffer, data]);

        while (true) {
            if (this.vscodeContentLength === 0) {
                const idx = this.vscodeBuffer.indexOf('\r\n\r\n');
                if (idx === -1) break;

                const header = this.vscodeBuffer.toString('utf8', 0, idx);
                const match = header.match(/Content-Length: (\d+)/);
                if (match) {
                    this.vscodeContentLength = parseInt(match[1]);
                }

                this.vscodeBuffer = this.vscodeBuffer.slice(idx + 4);
            }

            if (this.vscodeContentLength > 0) {
                if (this.vscodeBuffer.length >= this.vscodeContentLength) {
                    const messageBuffer = this.vscodeBuffer.slice(0, this.vscodeContentLength);
                    this.vscodeBuffer = this.vscodeBuffer.slice(this.vscodeContentLength);
                    this.vscodeContentLength = 0;

                    try {
                        const message = JSON.parse(messageBuffer.toString('utf8'));
                        this.handleVSCodeMessage(message);
                    } catch (error) {
                        console.error('Failed to parse VS Code message:', error);
                    }
                } else {
                    break;
                }
            }
        }
    }

    handleVSCodeMessage(message) {
        const { type, command } = message;

        if (type === 'response') {
            const pending = this.pendingClientRequests.get(message.request_seq);
            if (!pending) return;
            this.pendingClientRequests.delete(message.request_seq);
            if (message.success === false) pending.reject(new Error(message.message || 'VS Code rejected the request'));
            else pending.resolve(message.body || {});
            return;
        }

        if (type === 'request') {
            switch (command) {
                case 'initialize':
                    this.handleInitialize(message);
                    break;
                case 'launch':
                    this.handleLaunch(message);
                    break;
                case 'setBreakpoints':
                case 'configurationDone':
                case 'threads':
                case 'stackTrace':
                case 'scopes':
                case 'variables':
                case 'continue':
                case 'next':
                case 'stepIn':
                case 'stepOut':
                case 'pause':
                case 'evaluate':
                case 'setVariable':
                case 'disconnect':
                    // Forward to debugger
                    this.sendToDebugger(message);
                    break;
                default:
                    this.sendErrorResponse(message, `Unknown command: ${command}`);
            }
        }
    }

    handleInitialize(message) {
        // Send initialize response
        this.sendResponse(message, {
            supportsConfigurationDoneRequest: true,
            supportsEvaluateForHovers: false,
            supportsSetVariable: false
        });

        // Send initialized event
        this.sendEvent('initialized', {});
    }

    handleLaunch(message) {
        const { program, jarPath } = message.arguments;

        // Find JAR file: use the provided jarPath if valid, else any neoobjectpascal*.jar in bundled bin.
        let fullJarPath = (jarPath && fs.existsSync(jarPath)) ? jarPath : null;
        if (!fullJarPath) {
            const binDir = path.join(__dirname, 'bin');
            const stable = path.join(binDir, 'neoobjectpascal.jar');
            if (fs.existsSync(stable)) {
                fullJarPath = stable;
            } else {
                try {
                    const jars = fs.readdirSync(binDir)
                        .filter(f => /^neoobjectpascal.*\.jar$/i.test(f)).sort();
                    if (jars.length) fullJarPath = path.join(binDir, jars[jars.length - 1]);
                } catch (e) { /* ignore */ }
            }
        }

        if (!fs.existsSync(fullJarPath)) {
            this.sendErrorResponse(message, `JAR file not found: ${fullJarPath}`);
            return;
        }

        if (!fs.existsSync(program)) {
            this.sendErrorResponse(message, `Program file not found: ${program}`);
            return;
        }

        this.didTerminate = false;
        this.sendResponse(message, {});

        if (this.usesTerminalInk(program)) {
            this.startTerminalDebug(fullJarPath, program);
            this.sendToDebugger(message);
            return;
        }

        // Start NeoObjectPascal in DAP mode.
        this.debugProcess = spawn('java', [
            '-jar', fullJarPath,
            '--dap',
            program
        ]);

        // Forward stdout from debugger (DAP messages)
        this.debugProcess.stdout.on('data', (data) => {
            this.handleDebuggerOutput(data);
        });

        // Forward stderr from debugger (program output)
        this.debugProcess.stderr.on('data', (data) => {
            this.sendEvent('output', {
                category: 'stdout',
                output: data.toString()
            });
        });

        this.debugProcess.on('close', (code) => {
            this.sendTerminatedOnce();
        });

        this.debugProcess.on('error', (error) => {
            this.sendEvent('output', {
                category: 'stderr',
                output: `Error starting debugger: ${error.message}\n`
            });
        });

        // Forward launch request to debugger
        this.sendToDebugger(message);
    }

    usesTerminalInk(program) {
        try {
            return /\buses\s+terminalink\s*;/i.test(fs.readFileSync(program, 'utf8'));
        } catch (error) {
            return false;
        }
    }

    startTerminalDebug(jarPath, program) {
        this.dapSocketServer = net.createServer((socket) => {
            if (this.debuggerSocket) {
                socket.destroy();
                return;
            }
            this.debuggerSocket = socket;
            socket.on('data', (data) => this.handleDebuggerOutput(data));
            socket.on('error', (error) => this.sendEvent('output', { category: 'stderr', output: `DAP socket error: ${error.message}\n` }));
            socket.on('close', () => this.sendTerminatedOnce());
            this.dapSocketServer.close();
            this.dapSocketServer = null;
            this.flushDebuggerMessages();
        });
        this.dapSocketServer.listen(0, '127.0.0.1', () => {
            const port = this.dapSocketServer.address().port;
            const args = ['-jar', jarPath, '--dap', '--dap-connect', String(port), '--dap-terminal', program];
            this.requestRunInTerminal({
                kind: 'integrated',
                title: 'NeoObjectPascal TerminalInk Debug',
                cwd: path.dirname(program),
                args: ['java', ...args]
            }).catch((error) => {
                this.sendEvent('output', { category: 'stderr', output: `Could not start TerminalInk debug terminal: ${error.message}\n` });
                this.sendTerminatedOnce();
            });
        });
        this.dapSocketServer.on('error', (error) => {
            this.sendEvent('output', { category: 'stderr', output: `Could not create DAP socket: ${error.message}\n` });
            this.sendTerminatedOnce();
        });
    }

    requestRunInTerminal(arguments_) {
        return new Promise((resolve, reject) => {
            const seq = this.sequence++;
            this.pendingClientRequests.set(seq, { resolve, reject });
            this.sendMessage({ seq, type: 'request', command: 'runInTerminal', arguments: arguments_ });
        });
    }

    handleDebuggerOutput(data) {
        this.debuggerBuffer = Buffer.concat([this.debuggerBuffer, data]);

        while (true) {
            if (this.debuggerContentLength === 0) {
                const idx = this.debuggerBuffer.indexOf('\r\n\r\n');
                if (idx === -1) break;

                const header = this.debuggerBuffer.toString('utf8', 0, idx);
                const match = header.match(/Content-Length: (\d+)/);
                if (match) {
                    this.debuggerContentLength = parseInt(match[1]);
                }

                this.debuggerBuffer = this.debuggerBuffer.slice(idx + 4);
            }

            if (this.debuggerContentLength > 0) {
                if (this.debuggerBuffer.length >= this.debuggerContentLength) {
                    const messageBuffer = this.debuggerBuffer.slice(0, this.debuggerContentLength);
                    this.debuggerBuffer = this.debuggerBuffer.slice(this.debuggerContentLength);
                    this.debuggerContentLength = 0;

                    try {
                        const message = JSON.parse(messageBuffer.toString('utf8'));
                        // Forward to VS Code (but skip if we already sent it)
                        if (message.command !== 'launch' && message.command !== 'initialize') {
                            this.sendMessage(message);
                        }
                        if (message.event === 'terminated') {
                            this.didTerminate = true;
                            if (this.debuggerSocket) this.debuggerSocket.end();
                        }
                    } catch (error) {
                        console.error('Failed to parse debugger message:', error);
                    }
                } else {
                    break;
                }
            }
        }
    }

    sendToDebugger(message) {
        if (!this.writeToDebugger(message)) this.pendingDebuggerMessages.push(message);
    }

    flushDebuggerMessages() {
        const queued = this.pendingDebuggerMessages;
        this.pendingDebuggerMessages = [];
        queued.forEach((message) => this.sendToDebugger(message));
    }

    writeToDebugger(message) {
        const stream = this.debuggerSocket || (this.debugProcess && this.debugProcess.stdin);
        if (!stream || stream.destroyed || !stream.writable) return false;
        const json = JSON.stringify(message);
        const header = `Content-Length: ${Buffer.byteLength(json, 'utf8')}\r\n\r\n`;
        stream.write(header + json);
        return true;
    }

    sendMessage(message) {
        const json = JSON.stringify(message);
        const header = `Content-Length: ${Buffer.byteLength(json, 'utf8')}\r\n\r\n`;
        this.outputStream.write(header + json);
    }

    sendResponse(request, body) {
        const response = {
            seq: this.sequence++,
            type: 'response',
            request_seq: request.seq,
            success: true,
            command: request.command,
            body: body
        };
        this.sendMessage(response);
    }

    sendErrorResponse(request, message) {
        const response = {
            seq: this.sequence++,
            type: 'response',
            request_seq: request.seq,
            success: false,
            command: request.command,
            message: message
        };
        this.sendMessage(response);
    }

    sendEvent(event, body) {
        const message = {
            seq: this.sequence++,
            type: 'event',
            event: event,
            body: body
        };
        this.sendMessage(message);
    }

    sendTerminatedOnce() {
        if (this.didTerminate) return;
        this.didTerminate = true;
        this.sendEvent('terminated', {});
    }

    shutdown() {
        if (this.debugProcess) {
            this.debugProcess.kill();
            this.debugProcess = null;
        }
        if (this.debuggerSocket) this.debuggerSocket.destroy();
        if (this.dapSocketServer) this.dapSocketServer.close();
    }
}

// Start the debug adapter
const adapter = new NeoObjectPascalDebugAdapter();
adapter.start(process.stdin, process.stdout);
