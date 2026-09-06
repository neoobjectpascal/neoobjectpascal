package com.neoobjectpascal;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Interpreter extends NeoObjectPascalParserBaseVisitor<Object> {

    private final InterpreterState state;
    private final ExpressionEvaluator expressionEvaluator;
    private final StatementExecutor statementExecutor;
    private final OOHandler ooHandler;
    private final TestHandler testHandler;

    public Interpreter() {
        this("./", false);
    }

    public Interpreter(String baseDirectory) {
        this(baseDirectory, false);
    }

    public Interpreter(String baseDirectory, boolean suppressWarnings) {
        state = new InterpreterState();
        state.baseDirectory = baseDirectory;
        state.suppressWarnings = suppressWarnings;
        expressionEvaluator = new ExpressionEvaluator(state);
        statementExecutor = new StatementExecutor(state, expressionEvaluator);
        ooHandler = new OOHandler(state);
        testHandler = new TestHandler(state, expressionEvaluator);
        // Always-available built-ins for Date/Time/DateTime and Currency (no `uses` needed).
        BuiltinFunctions.register(this);
    }

    // ---- Public API (unchanged for callers) ----

    public void setTestMode(boolean testMode) { state.isTestMode = testMode; }
    public SymbolTable getSymbolTable() { return state.symbolTable; }
    public Debugger getDebugger() { return state.debugger; }
    public void setDebugger(Debugger debugger) { state.debugger = debugger; }
    public List<TestResult> getTestResults() { return state.testResults; }
    public void enableDebugger() { state.debugger.enable(); }

    // Package-private: sub-handlers may need the state
    InterpreterState getState() { return state; }

    /** Register a Java-backed built-in callable from NeoObjectPascal by {@code name}. */
    public void registerNative(String name, NativeFunction fn) {
        state.nativeFunctions.put(name, fn);
    }

    /**
     * Invoke a value that came from NeoObjectPascal as a callback (a function reference).
     * Returns null if the value is not callable. Public so subpackages (tui) can call back
     * without referencing the package-private Function type.
     */
    public Object callCallback(Object fnValue, List<Object> args) {
        if (fnValue instanceof Function) {
            return callFunction((Function) fnValue, args);
        }
        return null;
    }

    /** True if the value is a NeoObjectPascal function reference (usable as a callback). */
    public boolean isCallable(Object value) {
        return value instanceof Function;
    }

    /**
     * Invoke a NeoObjectPascal function value with the given arguments and return its result.
     * Used as a bridge so native code (e.g. the TerminalInk event loop) can call back into
     * user-defined callbacks passed as function references.
     */
    public Object callFunction(Function function, List<Object> args) {
        SymbolTable newScope = new SymbolTable(state.symbolTable);
        for (int i = 0; i < function.getParameters().size() && i < args.size(); i++) {
            String param = function.getParameters().get(i);
            newScope.put(param, new Symbol(param, null, args.get(i)));
        }
        SymbolTable oldScope = state.symbolTable;
        state.symbolTable = newScope;
        boolean framed = enterDebugFrame(function.getName(), newScope, null);
        try {
            visit(function.getBody());
            return null;
        } catch (ReturnValue rv) {
            return rv.value;
        } finally {
            state.symbolTable = oldScope;
            if (framed) state.debugger.exitFrame();
        }
    }

    // ---- Debugger integration ----

    /**
     * Execute one statement, giving the debugger a single chance to pause before it runs.
     * Used for control-flow bodies (if/while/for) whose single statement bypasses {@code executeBlock}.
     * A compound {@code begin..end} body is left to {@code executeBlock}, which hooks its inner statements.
     */
    public Object execStatement(NeoObjectPascalParser.StatementContext stmt) {
        if (stmt.block() == null && state.debugger.isEnabled()) {
            state.debugger.setSymbolTable(state.symbolTable);
            if (!state.debugger.shouldPause(stmt)) return null;
        }
        return visit(stmt);
    }

    /**
     * Push a call-stack frame for the debugger (no-op when debugging is off).
     * Returns whether a frame was pushed, so the caller can balance {@code exitFrame()}.
     */
    boolean enterDebugFrame(String name, SymbolTable scope, ObjectInstance instance) {
        if (!state.debugger.isEnabled()) return false;
        state.debugger.enterFrame(name, scope, instance);
        return true;
    }

    // ---- Program structure ----

    @Override
    public Object visitProgram(NeoObjectPascalParser.ProgramContext ctx) {
        if (ctx.usesClause() != null) visit(ctx.usesClause());
        for (NeoObjectPascalParser.DeclarationContext decl : ctx.declaration()) {
            visit(decl);
        }
        if (ctx.block() != null) visit(ctx.block());
        return null;
    }

    @Override
    public Object visitBlock(NeoObjectPascalParser.BlockContext ctx) {
        return statementExecutor.executeBlock(ctx, this);
    }

    @Override
    public Object visitUsesClause(NeoObjectPascalParser.UsesClauseContext ctx) {
        for (NeoObjectPascalParser.ModulePathContext modulePath : ctx.modulePath()) {
            String path = buildModulePath(modulePath);
            if (path.equals("terminalink") || path.equals("internal.terminalink")) {
                registerNativeModule("terminalink");
            } else if (path.equals("webink") || path.equals("internal.webink")) {
                registerNativeModule("webink");
            } else if (path.equals("http") || path.equals("internal.http")) {
                registerNativeModule("http");
            } else if (path.startsWith("internal.")) {
                loadInternalLibrary(path);
            } else {
                String fileName = modulePathToFilePath(path) + ".npas";
                try {
                    CharStream input = CharStreams.fromFileName(fileName);
                    NeoObjectPascalLexer lexer = new NeoObjectPascalLexer(input);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    NeoObjectPascalParser parser = new NeoObjectPascalParser(tokens);
                    visit(parser.program());
                } catch (IOException e) {
                    System.err.println("Erro ao carregar módulo " + fileName + ": " + e.getMessage());
                }
            }
        }
        return null;
    }

    // ---- Expressions ----

    @Override
    public Object visitExpression(NeoObjectPascalParser.ExpressionContext ctx) {
        return expressionEvaluator.evaluate(ctx, this);
    }

    @Override
    public Object visitPrimary(NeoObjectPascalParser.PrimaryContext ctx) {
        return expressionEvaluator.evaluatePrimary(ctx, this);
    }

    @Override
    public Object visitArrayLiteral(NeoObjectPascalParser.ArrayLiteralContext ctx) {
        return expressionEvaluator.evaluateArrayLiteral(ctx, this);
    }

    @Override
    public Object visitJavaBlock(NeoObjectPascalParser.JavaBlockContext ctx) {
        try {
            String javaCode = ctx.JAVA_CODE().getText();
            List<Object> parameters = new ArrayList<>();
            List<String> paramNames = new ArrayList<>();
            if (ctx.expressionList() != null) {
                for (NeoObjectPascalParser.ExpressionContext e : ctx.expressionList().expression()) {
                    parameters.add(visit(e));
                    paramNames.add(bareIdentifierName(e));
                }
            }
            return JavaExecutor.executeJavaCode(javaCode, parameters, paramNames);
        } catch (Exception e) {
            System.err.println("Erro ao executar bloco Java: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Object visitNewExpression(NeoObjectPascalParser.NewExpressionContext ctx) {
        return ooHandler.createInstance(ctx, this);
    }

    @Override
    public Object visitMemberAccess(NeoObjectPascalParser.MemberAccessContext ctx) {
        return ooHandler.resolveMemberAccess(ctx, this);
    }

    @Override
    public Object visitMethodCall(NeoObjectPascalParser.MethodCallContext ctx) {
        return ooHandler.executeMethodCall(ctx, this);
    }

    // ---- Statements ----

    @Override
    public Object visitVariableDeclaration(NeoObjectPascalParser.VariableDeclarationContext ctx) {
        return statementExecutor.declareVariable(ctx);
    }

    @Override
    public Object visitAssignment(NeoObjectPascalParser.AssignmentContext ctx) {
        return statementExecutor.executeAssignment(ctx, this);
    }

    @Override
    public Object visitArrayElementAssignment(NeoObjectPascalParser.ArrayElementAssignmentContext ctx) {
        return statementExecutor.executeArrayElementAssignment(ctx, this);
    }

    @Override
    public Object visitCall(NeoObjectPascalParser.CallContext ctx) {
        return statementExecutor.executeCall(ctx, this);
    }

    @Override
    public Object visitIfStatement(NeoObjectPascalParser.IfStatementContext ctx) {
        return statementExecutor.executeIf(ctx, this);
    }

    @Override
    public Object visitWhileStatement(NeoObjectPascalParser.WhileStatementContext ctx) {
        return statementExecutor.executeWhile(ctx, this);
    }

    @Override
    public Object visitForStatement(NeoObjectPascalParser.ForStatementContext ctx) {
        return statementExecutor.executeFor(ctx, this);
    }

    @Override
    public Object visitForInStatement(NeoObjectPascalParser.ForInStatementContext ctx) {
        return statementExecutor.executeForIn(ctx, this);
    }

    @Override
    public Object visitTryStatement(NeoObjectPascalParser.TryStatementContext ctx) {
        return statementExecutor.executeTry(ctx, this);
    }

    @Override
    public Object visitRaiseStatement(NeoObjectPascalParser.RaiseStatementContext ctx) {
        return statementExecutor.executeRaise(ctx, this);
    }

    @Override
    public Object visitWriteLnStatement(NeoObjectPascalParser.WriteLnStatementContext ctx) {
        return statementExecutor.executeWriteLn(ctx, this);
    }

    @Override
    public Object visitReadLnStatement(NeoObjectPascalParser.ReadLnStatementContext ctx) {
        return statementExecutor.executeReadLn(ctx);
    }

    @Override
    public Object visitShowMenuStatement(NeoObjectPascalParser.ShowMenuStatementContext ctx) {
        return statementExecutor.executeShowMenu(ctx, this);
    }

    @Override
    public Object visitJsonParseStatement(NeoObjectPascalParser.JsonParseStatementContext ctx) {
        return statementExecutor.executeJsonParse(ctx, this);
    }

    @Override
    public Object visitCsvParseStatement(NeoObjectPascalParser.CsvParseStatementContext ctx) {
        return statementExecutor.executeCsvParse(ctx, this);
    }

    @Override
    public Object visitReturnStatement(NeoObjectPascalParser.ReturnStatementContext ctx) {
        throw new ReturnValue(visit(ctx.expression()));
    }

    // ---- Declarations ----

    @Override
    public Object visitFunctionDeclaration(NeoObjectPascalParser.FunctionDeclarationContext ctx) {
        return ooHandler.registerFunction(ctx);
    }

    @Override
    public Object visitClassDeclaration(NeoObjectPascalParser.ClassDeclarationContext ctx) {
        return ooHandler.registerClass(ctx, this);
    }

    @Override
    public Object visitInterfaceDeclaration(NeoObjectPascalParser.InterfaceDeclarationContext ctx) {
        return ooHandler.registerInterface(ctx);
    }

    // ---- Tests ----

    @Override
    public Object visitTestDeclaration(NeoObjectPascalParser.TestDeclarationContext ctx) {
        return testHandler.runTest(ctx, this);
    }

    @Override
    public Object visitExpectStatement(NeoObjectPascalParser.ExpectStatementContext ctx) {
        return testHandler.executeExpect(ctx, this);
    }

    @Override
    public Object visitMockStatement(NeoObjectPascalParser.MockStatementContext ctx) {
        return testHandler.executeMock(ctx, this);
    }

    @Override
    public Object visitVerifyStatement(NeoObjectPascalParser.VerifyStatementContext ctx) {
        return testHandler.executeVerify(ctx);
    }

    // ---- Module loading ----

    /**
     * Returns the identifier text when a java-block argument is a bare identifier
     * (e.g. {@code java:(nome)}), so it can be exposed inside the block by name in addition to
     * paramN. Returns null for any other expression (literals, calls, {@code a + b}, self, ...).
     */
    private String bareIdentifierName(NeoObjectPascalParser.ExpressionContext e) {
        if (e.primary() != null && e.primary().identifier() != null) {
            return e.primary().identifier().getText();
        }
        return null;
    }

    private void registerNativeModule(String name) {
        // A program is entirely TerminalInk or entirely WebInk — the two UI frameworks never mix
        // (they both define `render`/`navigate` and target different surfaces).
        if (name.equals("terminalink") && state.loadedNativeModules.contains("webink")
                || name.equals("webink") && state.loadedNativeModules.contains("terminalink")) {
            throw new NeoException("Um programa é totalmente TerminalInk ou totalmente WebInk — "
                    + "não use 'uses terminalink' e 'uses webink' no mesmo programa.");
        }
        if (!state.loadedNativeModules.add(name)) return; // register once
        if (name.equals("terminalink")) {
            com.neoobjectpascal.tui.TerminalInk.register(this);
        } else if (name.equals("webink")) {
            com.neoobjectpascal.web.WebInk.register(this);
        } else if (name.equals("http")) {
            com.neoobjectpascal.http.HttpModule.register(this);
        }
    }

    private String buildModulePath(NeoObjectPascalParser.ModulePathContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.IDENTIFIER().size(); i++) {
            if (i > 0) sb.append('.');
            sb.append(ctx.IDENTIFIER(i).getText());
        }
        return sb.toString();
    }

    private String modulePathToFilePath(String modulePath) {
        return state.baseDirectory + modulePath.replace('.', '/');
    }

    private void loadInternalLibrary(String modulePath) {
        String libraryName = modulePath.substring("internal.".length());
        String resourcePath = "/internal/" + libraryName + ".npas";
        try {
            InputStream stream = getClass().getResourceAsStream(resourcePath);
            if (stream == null) {
                if (!state.suppressWarnings) {
                    System.err.println("Biblioteca interna não encontrada: " + modulePath);
                }
                return;
            }
            CharStream input = CharStreams.fromStream(stream);
            NeoObjectPascalLexer lexer = new NeoObjectPascalLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            NeoObjectPascalParser parser = new NeoObjectPascalParser(tokens);

            if (state.suppressWarnings) {
                lexer.removeErrorListeners();
                parser.removeErrorListeners();
                lexer.addErrorListener(new org.antlr.v4.runtime.BaseErrorListener());
                parser.addErrorListener(new org.antlr.v4.runtime.BaseErrorListener());
            }

            visit(parser.program());
            stream.close();
        } catch (IOException e) {
            if (!state.suppressWarnings) {
                System.err.println("Erro ao carregar biblioteca interna " + modulePath + ": " + e.getMessage());
            }
        }
    }
}
