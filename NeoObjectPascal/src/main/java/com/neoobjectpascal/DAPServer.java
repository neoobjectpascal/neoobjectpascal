package com.neoobjectpascal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Debug Adapter Protocol (DAP) Server with full debugging support
 * Integrates with Debugger.java for breakpoints, stepping, and variable inspection
 */
public class DAPServer {
    private final BufferedReader input;
    private final PrintWriter output;
    private final ObjectMapper mapper;
    private final String filePath;
    private int sequenceNumber = 1;
    
    private Interpreter interpreter;
    private Debugger debugger;
    private Thread executionThread;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private CountDownLatch pauseLatch;
    private String lastCommand = null;
    private BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
    
    private Map<String, List<Integer>> breakpoints = new HashMap<>();
    private int currentThreadId = 1;
    private int currentFrameId = 1;
    
    public DAPServer(InputStream in, OutputStream out, String filePath) {
        this.input = new BufferedReader(new InputStreamReader(in));
        this.output = new PrintWriter(new OutputStreamWriter(out), true);
        this.mapper = new ObjectMapper();
        this.filePath = filePath;
    }
    
    public void start() {
        if (VERBOSE) System.err.println("[DAP] Starting DAP server...");
        try {
            while (true) {
                String message = readMessage();
                if (message == null) {
                    if (VERBOSE) System.err.println("[DAP] No more messages, exiting...");
                    break;
                }
                
                if (VERBOSE) System.err.println("[DAP] Received message: " + message.substring(0, Math.min(100, message.length())));
                JsonNode request = mapper.readTree(message);
                handleRequest(request);
            }
        } catch (Exception e) {
            if (VERBOSE) System.err.println("[DAP] Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Wait for execution thread to finish
        if (executionThread != null && executionThread.isAlive()) {
            try {
                if (VERBOSE) System.err.println("[DAP] Waiting for execution thread...");
                executionThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        if (VERBOSE) System.err.println("[DAP] DAP server stopped");
    }
    
    private String readMessage() throws IOException {
        // Read Content-Length header
        Map<String, String> headers = new HashMap<>();
        String line;
        
        while ((line = input.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }
        
        if (!headers.containsKey("Content-Length")) {
            return null;
        }
        
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        int read = input.read(buffer, 0, contentLength);
        
        if (read != contentLength) {
            return null;
        }
        
        return new String(buffer);
    }
    
    // Internal DAP protocol tracing — off unless NEOPASCAL_DAP_VERBOSE=true (keeps the Debug Console clean).
    private static final boolean VERBOSE = "true".equals(System.getenv("NEOPASCAL_DAP_VERBOSE"));

    private synchronized void sendMessage(ObjectNode message) {
        try {
            String json = mapper.writeValueAsString(message);
            // Content-Length must be the BYTE length of the UTF-8 body, not the char count.
            byte[] bytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            output.print("Content-Length: " + bytes.length + "\r\n\r\n" + json);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void handleRequest(JsonNode request) {
        String command = request.get("command").asText();
        int seq = request.get("seq").asInt();
        
        switch (command) {
            case "initialize":
                handleInitialize(seq);
                break;
            case "launch":
                handleLaunch(seq, request);
                break;
            case "setBreakpoints":
                handleSetBreakpoints(seq, request);
                break;
            case "configurationDone":
                handleConfigurationDone(seq);
                break;
            case "threads":
                handleThreads(seq);
                break;
            case "stackTrace":
                handleStackTrace(seq);
                break;
            case "scopes":
                handleScopes(seq, request);
                break;
            case "variables":
                handleVariables(seq, request);
                break;
            case "continue":
                handleContinue(seq);
                break;
            case "next":
                handleNext(seq);
                break;
            case "stepIn":
                handleStepIn(seq);
                break;
            case "stepOut":
                handleStepOut(seq);
                break;
            case "pause":
                handlePause(seq);
                break;
            case "evaluate":
                handleEvaluate(seq, request);
                break;
            case "setVariable":
                handleSetVariable(seq, request);
                break;
            case "disconnect":
                handleDisconnect(seq);
                break;
            default:
                sendErrorResponse(seq, "Unknown command: " + command);
        }
    }
    
    private void handleInitialize(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "initialize");
        
        ObjectNode body = mapper.createObjectNode();
        body.put("supportsConfigurationDoneRequest", true);
        body.put("supportsEvaluateForHovers", true);
        body.put("supportsSetVariable", true);
        body.put("supportsStepBack", false);
        body.put("supportsStepInTargetsRequest", false);
        
        response.set("body", body);
        sendMessage(response);
        
        // Send initialized event
        ObjectNode event = mapper.createObjectNode();
        event.put("seq", sequenceNumber++);
        event.put("type", "event");
        event.put("event", "initialized");
        sendMessage(event);
    }
    
    private void handleLaunch(int seq, JsonNode request) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "launch");
        sendMessage(response);
    }
    
    private void handleSetBreakpoints(int seq, JsonNode request) {
        JsonNode args = request.get("arguments");
        String sourcePath = args.get("source").get("path").asText();
        ArrayNode bps = (ArrayNode) args.get("breakpoints");
        
        List<Integer> lines = new ArrayList<>();
        if (bps != null) {
            for (JsonNode bp : bps) {
                lines.add(bp.get("line").asInt());
            }
        }
        
        breakpoints.put(sourcePath, lines);

        // Apply to the live debugger too, so adding/removing a breakpoint while the program is
        // still running (e.g. a long-lived WebInk server) takes effect immediately — not only at
        // launch. (Before execution starts, `debugger` is null and executeProgram loads them.)
        if (debugger != null) {
            debugger.setFileBreakpoints(sourcePath, lines);
        }

        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "setBreakpoints");
        
        ObjectNode body = mapper.createObjectNode();
        ArrayNode breakpointsArray = mapper.createArrayNode();
        
        for (int line : lines) {
            ObjectNode verified = mapper.createObjectNode();
            verified.put("verified", true);
            verified.put("line", line);
            breakpointsArray.add(verified);
        }
        
        body.set("breakpoints", breakpointsArray);
        response.set("body", body);
        sendMessage(response);
    }
    
    private void handleConfigurationDone(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "configurationDone");
        sendMessage(response);
        
        // Start execution in a separate thread
        executionThread = new Thread(() -> executeProgram());
        executionThread.start();
    }
    
    private void executeProgram() {
        try {
            isRunning = true;
            
            // Parse
            CharStream inputStream = CharStreams.fromFileName(filePath);
            NeoObjectPascalLexer lexer = new NeoObjectPascalLexer(inputStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            NeoObjectPascalParser parser = new NeoObjectPascalParser(tokens);
            
            // Suppress errors
            parser.removeErrorListeners();
            lexer.removeErrorListeners();
            
            ParseTree tree = parser.program();
            
            // Create interpreter rooted at the program's directory so `uses folder.module`
            // resolves relative to the file (not the launcher's working directory).
            interpreter = new Interpreter(getBaseDirectory(filePath), true);
            debugger = new Debugger();
            debugger.setSymbolTable(interpreter.getSymbolTable());
            
            // Set the debugger in the interpreter (important!)
            interpreter.setDebugger(debugger);
            
            // Set breakpoints in debugger, keyed by their source file so a line number in one file
            // doesn't falsely trigger in another file that happens to share it.
            if (VERBOSE) System.err.println("[DAP] Setting breakpoints...");
            for (Map.Entry<String, List<Integer>> entry : breakpoints.entrySet()) {
                String file = entry.getKey();
                for (int line : entry.getValue()) {
                    debugger.addBreakpoint(file, line);
                    if (VERBOSE) System.err.println("[DAP] Breakpoint added at " + file + ":" + line);
                }
            }
            if (VERBOSE) System.err.println("[DAP] Total breakpoints: " + debugger.getBreakpoints().size());
            
            // Enable debugger with DAP mode and callback
            if (VERBOSE) System.err.println("[DAP] Enabling debugger in DAP mode...");
            debugger.setDapMode(true);
            debugger.setCallback(new Debugger.DebuggerCallback() {
                @Override
                public void onPause(int line, SymbolTable symbolTable) {
                    handleDebuggerPause(line, symbolTable);
                }
                
                @Override
                public String waitForCommand() {
                    try {
                        return commandQueue.take(); // Block until command arrives
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return "continue";
                    }
                }
            });
            debugger.enable();
            if (VERBOSE) System.err.println("[DAP] Debugger enabled: " + debugger.isEnabled());
            interpreter.enableDebugger();
            if (VERBOSE) System.err.println("[DAP] Interpreter debugger enabled");
            if (VERBOSE) System.err.println("[DAP] Starting program execution...");

            // Capture the program's stdout (WriteLn) and forward it as DAP 'output' events,
            // so it shows in the Debug Console instead of colliding with the DAP protocol on stdout.
            PrintStream realOut = System.out;
            // autoFlush=false so output is grouped per line (flushed on '\n'), not per print() call.
            PrintStream captured = new PrintStream(new ProgramOutputStream(), false);
            System.setOut(captured);
            try {
                interpreter.visit(tree);
            } finally {
                captured.flush();
                System.setOut(realOut);
            }

            if (VERBOSE) System.err.println("[DAP] Program execution completed");

            // Send terminated event
            isRunning = false;
            sendTerminatedEvent();

        } catch (Exception e) {
            System.setOut(realOutSafe());
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            sendOutputEvent("stderr", "Execution error: " + msg + "\n");
            isRunning = false;
            sendTerminatedEvent();
        }
    }

    /** DAP directory of the program file, used as the interpreter's module base. */
    private static String getBaseDirectory(String path) {
        int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return lastSlash >= 0 ? path.substring(0, lastSlash + 1) : "./";
    }

    /** Send program output to the Debug Console as a DAP 'output' event. */
    private void sendOutputEvent(String category, String text) {
        ObjectNode event = mapper.createObjectNode();
        event.put("seq", sequenceNumber++);
        event.put("type", "event");
        event.put("event", "output");
        ObjectNode body = event.putObject("body");
        body.put("category", category); // "stdout" | "stderr"
        body.put("output", text);
        sendMessage(event);
    }

    private PrintStream realOutSafe() {
        // FileDescriptor.out is the real stdout even after System.setOut was redirected.
        return new PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out), true);
    }

    /** OutputStream that buffers a line of program output (UTF-8) and forwards it as a DAP event. */
    private final class ProgramOutputStream extends java.io.OutputStream {
        private final java.io.ByteArrayOutputStream line = new java.io.ByteArrayOutputStream();

        @Override
        public synchronized void write(int b) {
            line.write(b);
            if (b == '\n') flushLine();
        }

        @Override
        public synchronized void flush() {
            if (line.size() > 0) flushLine();
        }

        private void flushLine() {
            try {
                sendOutputEvent("stdout", new String(line.toByteArray(), java.nio.charset.StandardCharsets.UTF_8));
            } finally {
                line.reset();
            }
        }
    }
    
    private void handleDebuggerPause(int line, SymbolTable symbolTable) {
        isPaused = true;
        currentLine = line;
        sendStoppedEvent("breakpoint", line);
        
        // The debugger will block in waitForCommand() until we put a command in the queue
        // Commands are added by handleContinue(), handleNext(), handleStepIn()
    }
    
    private int currentLine = 1;
    
    private void handleThreads(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "threads");
        
        ObjectNode body = mapper.createObjectNode();
        ArrayNode threads = mapper.createArrayNode();
        
        ObjectNode thread = mapper.createObjectNode();
        thread.put("id", currentThreadId);
        thread.put("name", "main");
        threads.add(thread);
        
        body.set("threads", threads);
        response.set("body", body);
        sendMessage(response);
    }
    
    private void handleStackTrace(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "stackTrace");
        
        ObjectNode body = mapper.createObjectNode();
        ArrayNode stackFrames = mapper.createArrayNode();

        if (isPaused && debugger != null) {
            // Report the real call stack, top frame first. Each frame carries its own source file,
            // so stepping into a method defined in another module shows the correct file/line.
            List<Debugger.StackFrame> frames = debugger.getCallStack();
            for (int i = frames.size() - 1; i >= 0; i--) {
                Debugger.StackFrame f = frames.get(i);
                ObjectNode frame = mapper.createObjectNode();
                // Frame id == stack index, so scopes/variables can resolve the right frame while paused.
                frame.put("id", i);
                frame.put("name", f.getName());

                String path = (f.getFile() != null && !f.getFile().isEmpty()) ? f.getFile() : filePath;
                ObjectNode source = mapper.createObjectNode();
                source.put("path", path);
                frame.set("source", source);

                frame.put("line", f.getLine());
                frame.put("column", 1);

                stackFrames.add(frame);
            }
        }

        body.set("stackFrames", stackFrames);
        body.put("totalFrames", stackFrames.size());
        
        response.set("body", body);
        sendMessage(response);
    }
    
    private void handleScopes(int seq, JsonNode request) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "scopes");
        
        ObjectNode body = mapper.createObjectNode();
        ArrayNode scopes = mapper.createArrayNode();

        if (isPaused) {
            // Map the requested frame to a distinct variablesReference (frameId + 1, so it stays > 0)
            // so "Locals" shows that frame's own scope — the method's params/self while stepping in.
            int frameId = 0;
            JsonNode args = request.get("arguments");
            if (args != null && args.has("frameId")) frameId = args.get("frameId").asInt();

            ObjectNode scope = mapper.createObjectNode();
            scope.put("name", "Locals");
            scope.put("variablesReference", frameId + 1);
            scope.put("expensive", false);
            scopes.add(scope);
        }

        body.set("scopes", scopes);
        response.set("body", body);
        sendMessage(response);
    }
    
    private void handleVariables(int seq, JsonNode request) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "variables");
        
        ObjectNode body = mapper.createObjectNode();
        ArrayNode variables = mapper.createArrayNode();

        if (isPaused && debugger != null) {
            // Resolve which frame's scope to show from the variablesReference (frameId + 1).
            JsonNode args = request.get("arguments");
            int reference = (args != null && args.has("variablesReference"))
                    ? args.get("variablesReference").asInt() : 1;
            int frameIndex = reference - 1;

            List<Debugger.StackFrame> frames = debugger.getCallStack();
            SymbolTable scope = null;
            Debugger.StackFrame frame = null;
            if (frameIndex >= 0 && frameIndex < frames.size()) {
                frame = frames.get(frameIndex);
                scope = frame.getScope();
            }
            if (scope == null && interpreter != null) {
                scope = interpreter.getSymbolTable();
            }

            // 'self' first for method frames, so instance state is visible while stepping through a method.
            if (frame != null && frame.getInstance() != null) {
                ObjectNode selfVar = mapper.createObjectNode();
                selfVar.put("name", "self");
                selfVar.put("value", String.valueOf(frame.getInstance()));
                selfVar.put("type", frame.getInstance().getClassDefinition().getName());
                selfVar.put("variablesReference", 0);
                variables.add(selfVar);
            }

            if (scope != null) {
                Map<String, Symbol> symbols = scope.getAllSymbols();
                if (symbols != null) {
                    for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
                        if (entry.getValue() != null) {
                            ObjectNode var = mapper.createObjectNode();
                            var.put("name", entry.getKey());

                            Object value = entry.getValue().getValue();
                            var.put("value", value != null ? String.valueOf(value) : "null");

                            Type type = entry.getValue().getType();
                            var.put("type", type != null ? type.toString() : "unknown");

                            var.put("variablesReference", 0);
                            variables.add(var);
                        }
                    }
                }
            }
        }

        body.set("variables", variables);
        response.set("body", body);
        sendMessage(response);
    }
    
    private void handleContinue(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "continue");
        
        ObjectNode body = mapper.createObjectNode();
        body.put("allThreadsContinued", true);
        response.set("body", body);
        
        sendMessage(response);
        
        // Resume execution by sending command to queue
        isPaused = false;
        commandQueue.offer("continue");
    }
    
    private void handleNext(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "next");
        sendMessage(response);
        
        // Step over by sending command to queue
        isPaused = false;
        commandQueue.offer("step");
    }
    
    private void handleStepIn(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "stepIn");
        sendMessage(response);
        
        // Step into by sending command to queue
        isPaused = false;
        commandQueue.offer("stepIn");
    }

    private void handleStepOut(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "stepOut");
        sendMessage(response);

        // Step out by sending command to queue
        isPaused = false;
        commandQueue.offer("stepOut");
    }

    private void handlePause(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "pause");
        sendMessage(response);
        
        // TODO: Implement pause
    }
    
    private void handleEvaluate(int seq, JsonNode request) {
        JsonNode args = request.get("arguments");
        String expression = args.get("expression").asText();
        
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "evaluate");
        
        ObjectNode body = mapper.createObjectNode();
        
        if (isPaused && interpreter != null) {
            Symbol symbol = interpreter.getSymbolTable().get(expression);
            if (symbol != null) {
                body.put("result", String.valueOf(symbol.getValue()));
                body.put("type", symbol.getType().toString());
                body.put("variablesReference", 0);
            } else {
                body.put("result", "undefined");
                body.put("variablesReference", 0);
            }
        } else {
            body.put("result", "not available");
            body.put("variablesReference", 0);
        }
        
        response.set("body", body);
        sendMessage(response);
    }
    
    private void handleSetVariable(int seq, JsonNode request) {
        JsonNode args = request.get("arguments");
        String name = args.get("name").asText();
        String value = args.get("value").asText();
        
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "setVariable");
        
        ObjectNode body = mapper.createObjectNode();
        
        if (isPaused && interpreter != null && debugger != null) {
            debugger.setVariable(name, value);
            body.put("value", value);
        }
        
        response.set("body", body);
        sendMessage(response);
    }
    
    private void handleDisconnect(int seq) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", true);
        response.put("command", "disconnect");
        sendMessage(response);
        
        if (executionThread != null) {
            executionThread.interrupt();
        }
        
        System.exit(0);
    }
    
    private void sendStoppedEvent(String reason, int line) {
        ObjectNode event = mapper.createObjectNode();
        event.put("seq", sequenceNumber++);
        event.put("type", "event");
        event.put("event", "stopped");
        
        ObjectNode body = mapper.createObjectNode();
        body.put("reason", reason);
        body.put("threadId", currentThreadId);
        body.put("allThreadsStopped", true);
        
        event.set("body", body);
        sendMessage(event);
    }
    
    private void sendTerminatedEvent() {
        ObjectNode event = mapper.createObjectNode();
        event.put("seq", sequenceNumber++);
        event.put("type", "event");
        event.put("event", "terminated");
        sendMessage(event);
    }
    
    private void sendErrorResponse(int seq, String message) {
        ObjectNode response = mapper.createObjectNode();
        response.put("seq", sequenceNumber++);
        response.put("type", "response");
        response.put("request_seq", seq);
        response.put("success", false);
        response.put("message", message);
        sendMessage(response);
    }
    
    interface DebuggerCallback {
        void onPause(int line, Map<String, Object> variables);
    }
}
