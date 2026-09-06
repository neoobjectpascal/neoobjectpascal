package com.neoobjectpascal;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Interactive debugger for NeoObjectPascal.
 * Supports file-aware breakpoints, a real call stack, step over/into/out execution,
 * and variable inspection/modification.
 */
public class Debugger {

    /** Stepping modes. Depth-aware so step-over skips callee bodies and step-out returns to the caller. */
    private static final int STEP_NONE = 0, STEP_OVER = 1, STEP_INTO = 2, STEP_OUT = 3;

    /** A single call-stack frame: where execution currently is inside one function/method/program. */
    public static final class StackFrame {
        private final String name;
        private String file;
        private int line;
        private SymbolTable scope;
        private final ObjectInstance instance; // 'self' for method frames, null otherwise

        StackFrame(String name, String file, int line, SymbolTable scope, ObjectInstance instance) {
            this.name = name;
            this.file = file;
            this.line = line;
            this.scope = scope;
            this.instance = instance;
        }

        public String getName() { return name; }
        public String getFile() { return file; }
        public int getLine() { return line; }
        public SymbolTable getScope() { return scope; }
        public ObjectInstance getInstance() { return instance; }
    }

    // Breakpoints: file-aware (DAP) and line-only (interactive, matches any file).
    private final Map<String, Set<Integer>> fileBreakpoints;
    private final Set<Integer> anyFileBreakpoints;

    private final Map<String, Object> watchedVariables;
    private boolean enabled;
    private int stepKind;
    private int stepBaseDepth;
    private int currentLine;
    private String currentFile;
    private BufferedReader reader;
    private SymbolTable symbolTable;
    private final List<StackFrame> callStack;
    private DebuggerCallback callback;
    private boolean dapMode = false;

    public interface DebuggerCallback {
        void onPause(int line, SymbolTable symbolTable);
        String waitForCommand();
    }

    public Debugger() {
        // Concurrent: setBreakpoints (DAP thread) can update these while shouldPause (exec/server
        // thread) reads them, e.g. toggling a breakpoint on a running WebInk server.
        this.fileBreakpoints = new java.util.concurrent.ConcurrentHashMap<>();
        this.anyFileBreakpoints = new HashSet<>();
        this.watchedVariables = new HashMap<>();
        this.enabled = false;
        this.stepKind = STEP_NONE;
        this.stepBaseDepth = 0;
        this.currentLine = -1;
        this.currentFile = "";
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.callStack = new ArrayList<>();
    }

    public void enable() {
        this.enabled = true;

        // Only show banner in interactive mode
        if (!dapMode) {
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║     NeoObjectPascal Debugger - Modo Interativo          ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Comandos disponíveis:");
            System.out.println("  b <linha>          - Adicionar breakpoint");
            System.out.println("  d <linha>          - Remover breakpoint");
            System.out.println("  list               - Listar breakpoints");
            System.out.println("  c, continue        - Continuar execução");
            System.out.println("  s, step            - Executar próxima linha (step over)");
            System.out.println("  i, into            - Entrar em função (step into)");
            System.out.println("  o, out             - Sair da função (step out)");
            System.out.println("  w <var>            - Watch variável");
            System.out.println("  p <var>            - Imprimir valor de variável");
            System.out.println("  set <var> <valor>  - Modificar valor de variável");
            System.out.println("  vars               - Listar todas as variáveis");
            System.out.println("  stack              - Mostrar call stack");
            System.out.println("  q, quit            - Sair do debugger");
            System.out.println();
        }
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // ---- Breakpoints ----

    /** File-aware breakpoint (DAP): only fires when execution is in {@code file} at {@code line}. */
    public void addBreakpoint(String file, int line) {
        fileBreakpoints.computeIfAbsent(canon(file), k -> new HashSet<>()).add(line);
    }

    /**
     * Replace all breakpoints for a file. DAP {@code setBreakpoints} sends the full line list for a
     * source on every change, so this keeps the live debugger in sync when breakpoints are added or
     * removed while the program is still running (e.g. a long-lived WebInk server).
     */
    public void setFileBreakpoints(String file, java.util.Collection<Integer> lines) {
        String key = canon(file);
        if (lines == null || lines.isEmpty()) {
            fileBreakpoints.remove(key);
        } else {
            fileBreakpoints.put(key, new HashSet<>(lines));
        }
    }

    /** Line-only breakpoint (interactive): fires on {@code line} in any file. */
    public void addBreakpoint(int line) {
        anyFileBreakpoints.add(line);
        if (!dapMode) {
            System.out.println("✓ Breakpoint adicionado na linha " + line);
        }
    }

    public void removeBreakpoint(int line) {
        if (anyFileBreakpoints.remove(line)) {
            System.out.println("✓ Breakpoint removido da linha " + line);
        } else {
            System.out.println("✗ Nenhum breakpoint na linha " + line);
        }
    }

    public void listBreakpoints() {
        List<Integer> all = new ArrayList<>(getBreakpoints());
        if (all.isEmpty()) {
            System.out.println("Nenhum breakpoint definido.");
        } else {
            System.out.println("Breakpoints:");
            Collections.sort(all);
            for (int line : all) {
                System.out.println("  Linha " + line);
            }
        }
    }

    private boolean isBreakpoint(String file, int line) {
        if (anyFileBreakpoints.contains(line)) return true;
        Set<Integer> lines = fileBreakpoints.get(canon(file));
        return lines != null && lines.contains(line);
    }

    /** Canonical absolute path, so breakpoint paths and token source names match across symlinks. */
    private static String canon(String path) {
        if (path == null) return "";
        try {
            return new java.io.File(path).getCanonicalPath();
        } catch (Exception e) {
            return new java.io.File(path).getAbsolutePath();
        }
    }

    // ---- Watches ----

    public void watchVariable(String varName) {
        watchedVariables.put(varName, null);
        System.out.println("✓ Watching variável: " + varName);
    }

    public void unwatchVariable(String varName) {
        if (watchedVariables.remove(varName) != null) {
            System.out.println("✓ Removido watch da variável: " + varName);
        } else {
            System.out.println("✗ Variável não está sendo watched: " + varName);
        }
    }

    public void updateWatchedVariables(SymbolTable symbolTable) {
        for (String varName : watchedVariables.keySet()) {
            Symbol symbol = symbolTable.get(varName);
            if (symbol != null) {
                Object oldValue = watchedVariables.get(varName);
                Object newValue = symbol.getValue();

                if (!Objects.equals(oldValue, newValue)) {
                    System.out.println("⚠ WATCH: " + varName + " = " + newValue +
                                     " (anterior: " + oldValue + ")");
                    watchedVariables.put(varName, newValue);
                }
            }
        }
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void printVariable(String varName) {
        if (symbolTable == null) {
            System.out.println("✗ Symbol table não disponível");
            return;
        }

        Symbol symbol = symbolTable.get(varName);
        if (symbol != null) {
            System.out.println(varName + " = " + formatValue(symbol.getValue()) +
                             " (tipo: " + symbol.getType() + ")");
        } else {
            System.out.println("✗ Variável não encontrada: " + varName);
        }
    }

    public void setVariable(String varName, String valueStr) {
        if (symbolTable == null) {
            System.out.println("✗ Symbol table não disponível");
            return;
        }

        Symbol symbol = symbolTable.get(varName);
        if (symbol != null) {
            Object oldValue = symbol.getValue();
            Object newValue = parseValue(valueStr, symbol.getType());

            symbol.setValue(newValue);
            System.out.println("✓ " + varName + " = " + newValue + " (anterior: " + oldValue + ")");
        } else {
            System.out.println("✗ Variável não encontrada: " + varName);
        }
    }

    public void listVariables() {
        if (symbolTable == null) {
            System.out.println("✗ Symbol table não disponível");
            return;
        }

        System.out.println("Variáveis locais:");
        Map<String, Symbol> vars = symbolTable.getAllSymbols();
        if (vars.isEmpty()) {
            System.out.println("  (nenhuma variável)");
        } else {
            for (Map.Entry<String, Symbol> entry : vars.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " +
                                 formatValue(entry.getValue().getValue()) +
                                 " (tipo: " + entry.getValue().getType() + ")");
            }
        }
    }

    // ---- Call stack ----

    /**
     * Push a frame when entering a function/method/constructor body. Balanced by {@link #exitFrame()}.
     * The initial (file,line) is the call site; it is overwritten as statements execute in the callee.
     */
    public void enterFrame(String name, SymbolTable scope, ObjectInstance instance) {
        ensureBaseFrame();
        callStack.add(new StackFrame(name, currentFile, currentLine, scope, instance));
    }

    /** Pop the top frame on return. Never pops the base (program) frame. */
    public void exitFrame() {
        if (callStack.size() > 1) {
            callStack.remove(callStack.size() - 1);
        }
    }

    /** Snapshot of the call stack, bottom (program) first. Valid while paused. */
    public List<StackFrame> getCallStack() {
        return new ArrayList<>(callStack);
    }

    private void ensureBaseFrame() {
        if (callStack.isEmpty()) {
            callStack.add(new StackFrame("main", currentFile, currentLine, symbolTable, null));
        }
    }

    public void printCallStack() {
        if (callStack.isEmpty()) {
            System.out.println("Call stack vazio");
        } else {
            System.out.println("Call Stack:");
            for (int i = callStack.size() - 1; i >= 0; i--) {
                StackFrame frame = callStack.get(i);
                System.out.println("  #" + i + " " + frame.getName() + " (linha " + frame.getLine() + ")");
            }
        }
    }

    /**
     * Check if execution should pause at this statement.
     * Returns true to continue execution, false to quit.
     */
    public boolean shouldPause(ParserRuleContext ctx) {
        if (!enabled) return true; // Continue if debugger not enabled

        Token token = ctx.getStart();
        currentLine = token.getLine();
        currentFile = sourceNameOf(token);

        // Keep the top frame's position in sync with the statement about to run.
        ensureBaseFrame();
        StackFrame top = callStack.get(callStack.size() - 1);
        top.file = currentFile;
        top.line = currentLine;
        top.scope = symbolTable;

        // Update watched variables
        if (symbolTable != null) {
            updateWatchedVariables(symbolTable);
        }

        int depth = callStack.size();
        boolean stepHit = stepKind != STEP_NONE && (
                stepKind == STEP_INTO
                || (stepKind == STEP_OVER && depth <= stepBaseDepth)
                || (stepKind == STEP_OUT && depth < stepBaseDepth));
        boolean shouldPause = stepHit || isBreakpoint(currentFile, currentLine);

        if (shouldPause) {
            stepKind = STEP_NONE; // Reset step mode after pausing

            // In DAP mode, don't show console output
            if (!dapMode) {
                showCurrentLocation(ctx);
            }

            return handleDebuggerPrompt();
        }

        return true; // Continue execution
    }

    /** Source file a token came from ({@code CharStreams.fromFileName} sets this), or "" if unknown. */
    private static String sourceNameOf(Token token) {
        try {
            if (token.getInputStream() != null) {
                String name = token.getInputStream().getSourceName();
                if (name != null && !name.equals(org.antlr.v4.runtime.IntStream.UNKNOWN_SOURCE_NAME)) {
                    return name;
                }
            }
        } catch (Exception ignored) {
            // fall through
        }
        return "";
    }

    private void showCurrentLocation(ParserRuleContext ctx) {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("⏸ PAUSADO na linha " + currentLine);
        System.out.println("───────────────────────────────────────────────────────────");

        // Show context (simplified - in production would show actual source lines)
        String text = ctx.getText();
        if (text.length() > 80) {
            text = text.substring(0, 77) + "...";
        }
        System.out.println("  " + text);
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    /**
     * Handle debugger prompt and commands
     * Returns true if execution should continue, false if should quit
     */
    private boolean handleDebuggerPrompt() {
        // DAP mode: use callback to wait for command
        if (dapMode && callback != null) {
            callback.onPause(currentLine, symbolTable);
            String command = callback.waitForCommand();

            if (command == null || command.equals("continue")) {
                return true;
            } else if (command.equals("step")) {
                beginStep(STEP_OVER);
                return true;
            } else if (command.equals("stepIn")) {
                beginStep(STEP_INTO);
                return true;
            } else if (command.equals("stepOut")) {
                beginStep(STEP_OUT);
                return true;
            } else if (command.equals("quit")) {
                return false;
            }
            return true;
        }

        // Interactive mode: read from stdin
        while (true) {
            try {
                System.out.print("debug> ");
                String input = reader.readLine();

                if (input == null || input.trim().isEmpty()) {
                    continue;
                }

                String[] parts = input.trim().split("\\s+");
                String command = parts[0].toLowerCase();

                switch (command) {
                    case "c":
                    case "continue":
                        return true;

                    case "s":
                    case "step":
                        beginStep(STEP_OVER);
                        return true;

                    case "i":
                    case "into":
                        beginStep(STEP_INTO);
                        return true;

                    case "o":
                    case "out":
                        beginStep(STEP_OUT);
                        return true;

                    case "b":
                        if (parts.length < 2) {
                            System.out.println("Uso: b <linha>");
                        } else {
                            try {
                                int line = Integer.parseInt(parts[1]);
                                addBreakpoint(line);
                            } catch (NumberFormatException e) {
                                System.out.println("Número de linha inválido");
                            }
                        }
                        break;

                    case "d":
                        if (parts.length < 2) {
                            System.out.println("Uso: d <linha>");
                        } else {
                            try {
                                int line = Integer.parseInt(parts[1]);
                                removeBreakpoint(line);
                            } catch (NumberFormatException e) {
                                System.out.println("Número de linha inválido");
                            }
                        }
                        break;

                    case "list":
                        listBreakpoints();
                        break;

                    case "w":
                    case "watch":
                        if (parts.length < 2) {
                            System.out.println("Uso: w <variável>");
                        } else {
                            watchVariable(parts[1]);
                        }
                        break;

                    case "p":
                    case "print":
                        if (parts.length < 2) {
                            System.out.println("Uso: p <variável>");
                        } else {
                            printVariable(parts[1]);
                        }
                        break;

                    case "set":
                        if (parts.length < 3) {
                            System.out.println("Uso: set <variável> <valor>");
                        } else {
                            setVariable(parts[1], parts[2]);
                        }
                        break;

                    case "vars":
                        listVariables();
                        break;

                    case "stack":
                        printCallStack();
                        break;

                    case "q":
                    case "quit":
                        System.out.println("Encerrando debugger...");
                        return false;

                    case "help":
                    case "?":
                        printHelp();
                        break;

                    default:
                        System.out.println("Comando desconhecido: " + command);
                        System.out.println("Digite 'help' para ver os comandos disponíveis");
                }

            } catch (IOException e) {
                System.err.println("Erro ao ler comando: " + e.getMessage());
                return false;
            }
        }
    }

    /** Arm a step, remembering the current call depth so over/out can tell where the caller is. */
    private void beginStep(int kind) {
        stepKind = kind;
        stepBaseDepth = Math.max(1, callStack.size());
    }

    private void printHelp() {
        System.out.println("Comandos do Debugger:");
        System.out.println("  b <linha>          - Adicionar breakpoint");
        System.out.println("  d <linha>          - Remover breakpoint");
        System.out.println("  list               - Listar breakpoints");
        System.out.println("  c, continue        - Continuar execução");
        System.out.println("  s, step            - Executar próxima linha (step over)");
        System.out.println("  i, into            - Entrar em função (step into)");
        System.out.println("  o, out             - Sair da função (step out)");
        System.out.println("  w <var>            - Watch variável");
        System.out.println("  p <var>            - Imprimir variável");
        System.out.println("  set <var> <valor>  - Modificar variável");
        System.out.println("  vars               - Listar variáveis");
        System.out.println("  stack              - Mostrar call stack");
        System.out.println("  q, quit            - Sair");
        System.out.println("  help, ?            - Mostrar esta ajuda");
    }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + value + "\"";
        return value.toString();
    }

    private Object parseValue(String valueStr, Type type) {
        try {
            switch (type) {
                case INTEGER:
                    return Integer.parseInt(valueStr);
                case REAL:
                    return Double.parseDouble(valueStr);
                case BOOLEAN:
                    return Boolean.parseBoolean(valueStr);
                case STRING:
                    // Remove quotes if present
                    if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
                        return valueStr.substring(1, valueStr.length() - 1);
                    }
                    return valueStr;
                default:
                    return valueStr;
            }
        } catch (Exception e) {
            System.out.println("⚠ Aviso: Não foi possível converter valor, usando como string");
            return valueStr;
        }
    }

    // Public methods for DAP integration
    public int getCurrentLine() {
        return currentLine;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    /** All breakpoint lines (file-aware + line-only), flattened. Used for diagnostics. */
    public Set<Integer> getBreakpoints() {
        Set<Integer> all = new HashSet<>(anyFileBreakpoints);
        for (Set<Integer> lines : fileBreakpoints.values()) {
            all.addAll(lines);
        }
        return all;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setCallback(DebuggerCallback callback) {
        this.callback = callback;
    }

    public void setDapMode(boolean dapMode) {
        this.dapMode = dapMode;
    }

    public boolean isDapMode() {
        return dapMode;
    }
}
