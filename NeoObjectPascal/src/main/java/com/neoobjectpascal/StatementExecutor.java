package com.neoobjectpascal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class StatementExecutor {

    private final InterpreterState state;
    private final ExpressionEvaluator expr;

    StatementExecutor(InterpreterState state, ExpressionEvaluator expr) {
        this.state = state;
        this.expr = expr;
    }

    // ---- Block ----

    Object executeBlock(NeoObjectPascalParser.BlockContext ctx, Interpreter interp) {
        for (NeoObjectPascalParser.StatementContext stmt : ctx.statement()) {
            if (state.debugger.isEnabled()) {
                state.debugger.setSymbolTable(state.symbolTable);
                if (!state.debugger.shouldPause(stmt)) return null;
            }
            interp.visit(stmt);
        }
        return null;
    }

    // ---- Variable declaration ----

    Object declareVariable(NeoObjectPascalParser.VariableDeclarationContext ctx) {
        String varName = ctx.identifier().getText();
        Type type = InterpreterState.resolveType(ctx.type());
        state.symbolTable.put(varName, new Symbol(varName, type));
        return null;
    }

    // ---- Assignment ----

    Object executeAssignment(NeoObjectPascalParser.AssignmentContext ctx, Interpreter interp) {
        Object value = interp.visit(ctx.expression());

        if (ctx.memberAccess() != null) {
            Object result = interp.visit(ctx.memberAccess());
            if (result instanceof MemberAccessResult) {
                MemberAccessResult mar = (MemberAccessResult) result;
                if (mar.object.hasField(mar.memberName)) {
                    mar.object.setFieldValue(mar.memberName, value);
                } else {
                    throw new RuntimeException("Cannot assign to method '" + mar.memberName + "'");
                }
            } else {
                throw new RuntimeException("Cannot assign to non-field member");
            }
            return null;
        }

        String varName = ctx.identifier().getText();
        Symbol symbol = state.symbolTable.get(varName);
        if (symbol != null) {
            symbol.setValue(coerce(symbol.getType(), value));
        }
        return null;
    }

    Object executeArrayElementAssignment(NeoObjectPascalParser.ArrayElementAssignmentContext ctx,
                                         Interpreter interp) {
        String varName = ctx.identifier().getText();
        Symbol symbol = state.symbolTable.get(varName);
        if (symbol == null) throw new NeoException("Undefined variable '" + varName + "'");

        Object arrObj = symbol.getValue();
        if (!(arrObj instanceof List)) throw new NeoException("Variable '" + varName + "' is not an array");

        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) arrObj;
        int idx = expr.toInt(interp.visit(ctx.expression(0)));
        Object value = interp.visit(ctx.expression(1));

        if (idx < 0 || idx >= list.size()) {
            throw new NeoException("Array index out of bounds: " + idx + " (size: " + list.size() + ")");
        }
        list.set(idx, value);
        return null;
    }

    // ---- Function call ----

    Object executeCall(NeoObjectPascalParser.CallContext ctx, Interpreter interp) {
        String functionName = ctx.identifier().getText();

        if (state.mockManager.isFunctionMocked(functionName)) {
            List<Object> args = collectArgs(ctx.expressionList(), interp);
            return state.mockManager.getMockedFunctionReturn(functionName, args);
        }

        Function function = state.functions.get(functionName);
        if (function != null) {
            List<Object> args = collectArgs(ctx.expressionList(), interp);
            SymbolTable newScope = new SymbolTable(state.symbolTable);
            for (int i = 0; i < function.getParameters().size() && i < args.size(); i++) {
                String param = function.getParameters().get(i);
                newScope.put(param, new Symbol(param, null, args.get(i)));
            }
            SymbolTable oldScope = state.symbolTable;
            state.symbolTable = newScope;
            boolean framed = interp.enterDebugFrame(functionName, newScope, null);
            try {
                interp.visit(function.getBody());
                return null;
            } catch (ReturnValue rv) {
                return rv.value;
            } finally {
                state.symbolTable = oldScope;
                if (framed) state.debugger.exitFrame();
            }
        }

        // Native (Java-backed) built-in, e.g. TerminalInk widgets registered via `uses terminalink`.
        NativeFunction nativeFn = state.nativeFunctions.get(functionName);
        if (nativeFn != null) {
            List<Object> args = collectArgs(ctx.expressionList(), interp);
            return nativeFn.call(args, interp);
        }
        return null;
    }

    // ---- Control flow ----

    Object executeIf(NeoObjectPascalParser.IfStatementContext ctx, Interpreter interp) {
        if (expr.isTrue(interp.visit(ctx.expression()))) {
            interp.execStatement(ctx.statement(0));
        } else if (ctx.statement().size() > 1) {
            interp.execStatement(ctx.statement(1));
        }
        return null;
    }

    Object executeWhile(NeoObjectPascalParser.WhileStatementContext ctx, Interpreter interp) {
        while (expr.isTrue(interp.visit(ctx.expression()))) {
            interp.execStatement(ctx.statement());
        }
        return null;
    }

    Object executeFor(NeoObjectPascalParser.ForStatementContext ctx, Interpreter interp) {
        String varName = ctx.identifier().getText();
        int start = expr.toInt(interp.visit(ctx.expression(0)));
        int end   = expr.toInt(interp.visit(ctx.expression(1)));

        Symbol symbol = state.symbolTable.get(varName);
        if (symbol == null) {
            symbol = new Symbol(varName, Type.INTEGER);
            state.symbolTable.put(varName, symbol);
        }

        for (int i = start; i <= end; i++) {
            symbol.setValue(i);
            interp.execStatement(ctx.statement());
        }
        return null;
    }

    Object executeForIn(NeoObjectPascalParser.ForInStatementContext ctx, Interpreter interp) {
        String varName = ctx.identifier().getText();
        Object iterableObj = interp.visit(ctx.expression());

        if (!(iterableObj instanceof List)) {
            throw new NeoException("for..in requires an array value");
        }

        @SuppressWarnings("unchecked")
        List<Object> iterable = (List<Object>) iterableObj;

        Symbol symbol = state.symbolTable.get(varName);
        if (symbol == null) {
            symbol = new Symbol(varName, Type.OBJECT);
            state.symbolTable.put(varName, symbol);
        }

        for (Object item : iterable) {
            symbol.setValue(item);
            interp.execStatement(ctx.statement());
        }
        return null;
    }

    // ---- Error handling ----

    Object executeTry(NeoObjectPascalParser.TryStatementContext ctx, Interpreter interp) {
        String errorVar = ctx.identifier().getText();
        try {
            interp.visit(ctx.block(0));
        } catch (ReturnValue rv) {
            throw rv; // propagate return values out of try blocks
        } catch (NeoException e) {
            bindErrorVar(errorVar, e.getValue());
            interp.visit(ctx.block(1));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            bindErrorVar(errorVar, msg);
            interp.visit(ctx.block(1));
        } finally {
            if (ctx.block().size() > 2) {
                interp.visit(ctx.block(2));
            }
        }
        return null;
    }

    Object executeRaise(NeoObjectPascalParser.RaiseStatementContext ctx, Interpreter interp) {
        Object value = interp.visit(ctx.expression());
        throw new NeoException(value);
    }

    private void bindErrorVar(String varName, Object value) {
        Symbol existing = state.symbolTable.get(varName);
        if (existing != null) {
            existing.setValue(value);
        } else {
            state.symbolTable.put(varName, new Symbol(varName, Type.STRING, value));
        }
    }

    // ---- I/O ----

    Object executeWriteLn(NeoObjectPascalParser.WriteLnStatementContext ctx, Interpreter interp) {
        // No debugger hook here: the surrounding block (or execStatement for control-flow bodies)
        // already pauses on this statement. Hooking again caused a double-stop on the same line.
        if (ctx.expressionList() != null) {
            for (NeoObjectPascalParser.ExpressionContext e : ctx.expressionList().expression()) {
                System.out.print(interp.visit(e));
            }
        }
        System.out.println();
        return null;
    }

    Object executeReadLn(NeoObjectPascalParser.ReadLnStatementContext ctx) {
        if (state.debugger.isEnabled()) {
            state.debugger.setSymbolTable(state.symbolTable);
        }
        String varName = ctx.identifier().getText();
        Symbol symbol = state.symbolTable.get(varName);
        if (symbol == null) return null;

        // While debugging, stdin is the DAP protocol channel — don't consume it. Use a default value
        // and tell the user to use Run for interactive input.
        if (state.debugger != null && state.debugger.isDapMode()) {
            System.out.println("[debug] ReadLn skipped — interactive input isn't available while debugging (use Run).");
            symbol.setValue(defaultValue(symbol.getType()));
            return null;
        }

        try {
            System.out.flush();
            String input = state.inputScanner.nextLine();
            symbol.setValue(coerce(symbol.getType(), input));
        } catch (Exception e) {
            symbol.setValue(defaultValue(symbol.getType()));
        }
        return null;
    }

    Object executeShowMenu(NeoObjectPascalParser.ShowMenuStatementContext ctx, Interpreter interp) {
        System.out.println("--- MENU ---");
        for (int i = 0; i < ctx.expressionList().expression().size(); i++) {
            System.out.println((i + 1) + ". " + interp.visit(ctx.expressionList().expression(i)));
        }
        System.out.println("----------");
        return null;
    }

    // ---- Data parsing ----

    Object executeJsonParse(NeoObjectPascalParser.JsonParseStatementContext ctx, Interpreter interp) {
        String jsonString = (String) interp.visit(ctx.expression());
        String varName = ctx.identifier().getText();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object value = mapper.readValue(jsonString, Object.class);
            Symbol symbol = state.symbolTable.get(varName);
            if (symbol != null) symbol.setValue(value);
        } catch (Exception e) {
            System.err.println("Erro ao processar JSON: " + e.getMessage());
        }
        return null;
    }

    Object executeCsvParse(NeoObjectPascalParser.CsvParseStatementContext ctx, Interpreter interp) {
        String csvString = (String) interp.visit(ctx.expression());
        String varName = ctx.identifier().getText();
        try {
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> allRows = reader.readAll();
            Symbol symbol = state.symbolTable.get(varName);
            if (symbol != null) symbol.setValue(allRows);
        } catch (Exception e) {
            System.err.println("Erro ao processar CSV: " + e.getMessage());
        }
        return null;
    }

    // ---- Helpers ----

    private List<Object> collectArgs(NeoObjectPascalParser.ExpressionListContext listCtx, Interpreter interp) {
        List<Object> args = new ArrayList<>();
        if (listCtx != null) {
            for (NeoObjectPascalParser.ExpressionContext e : listCtx.expression()) {
                args.add(interp.visit(e));
            }
        }
        return args;
    }

    private Object coerce(Type type, Object value) {
        if (type == null) return value;
        switch (type) {
            case INTEGER:
                if (value instanceof String) {
                    try { return Integer.parseInt((String) value); } catch (NumberFormatException e) { return 0; }
                }
                if (value instanceof Double) return ((Double) value).intValue();
                return value;
            case REAL:
                if (value instanceof Integer) return ((Integer) value).doubleValue();
                if (value instanceof String) {
                    try { return Double.parseDouble((String) value); } catch (NumberFormatException e) { return 0.0; }
                }
                return value;
            case BOOLEAN:
                if (value instanceof String) return Boolean.parseBoolean((String) value);
                return value;
            case CURRENCY:
                if (value instanceof java.math.BigDecimal) return value;
                if (value instanceof Number) return new java.math.BigDecimal(value.toString());
                if (value instanceof String) {
                    try { return new java.math.BigDecimal(((String) value).trim()); }
                    catch (NumberFormatException e) { throw new NeoException("Cannot convert '" + value + "' to Currency"); }
                }
                return value;
            case DATE:
                if (value instanceof java.time.LocalDate) return value;
                if (value instanceof String) {
                    try { return java.time.LocalDate.parse(((String) value).trim()); }
                    catch (Exception e) { throw new NeoException("Cannot parse '" + value + "' as Date (expected yyyy-MM-dd)"); }
                }
                return value;
            case TIME:
                if (value instanceof java.time.LocalTime) return value;
                if (value instanceof String) {
                    try { return java.time.LocalTime.parse(((String) value).trim()); }
                    catch (Exception e) { throw new NeoException("Cannot parse '" + value + "' as Time (expected HH:mm[:ss])"); }
                }
                return value;
            case DATETIME:
                if (value instanceof java.time.LocalDateTime) return value;
                if (value instanceof String) {
                    try { return java.time.LocalDateTime.parse(((String) value).trim().replace(' ', 'T')); }
                    catch (Exception e) { throw new NeoException("Cannot parse '" + value + "' as DateTime (expected yyyy-MM-ddTHH:mm[:ss])"); }
                }
                return value;
            default:
                return value;
        }
    }

    private Object defaultValue(Type type) {
        if (type == null) return null;
        switch (type) {
            case INTEGER: return 0;
            case REAL: return 0.0;
            case BOOLEAN: return false;
            default: return "";
        }
    }
}
