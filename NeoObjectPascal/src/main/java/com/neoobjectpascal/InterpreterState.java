package com.neoobjectpascal;

import java.util.*;

class InterpreterState {

    SymbolTable symbolTable = new SymbolTable();
    final Map<String, Function> functions = new HashMap<>();
    final Map<String, ClassDefinition> classes = new HashMap<>();
    final Map<String, InterfaceDefinition> interfaces = new HashMap<>();
    // Java-backed built-in functions callable by name (e.g. TerminalInk widgets).
    final Map<String, NativeFunction> nativeFunctions = new HashMap<>();
    // Names of native modules already registered via `uses` (avoid double registration).
    final Set<String> loadedNativeModules = new HashSet<>();
    ObjectInstance currentInstance = null;
    final List<TestResult> testResults = new ArrayList<>();
    final MockManager mockManager = new MockManager();
    Debugger debugger = new Debugger();
    boolean isTestMode = false;
    boolean suppressWarnings = false;
    String baseDirectory = "./";
    Scanner inputScanner = new Scanner(System.in);

    static Type resolveType(NeoObjectPascalParser.TypeContext ctx) {
        if (ctx.TYPE_INTEGER() != null) return Type.INTEGER;
        if (ctx.TYPE_STRING() != null) return Type.STRING;
        if (ctx.TYPE_BOOLEAN() != null) return Type.BOOLEAN;
        if (ctx.TYPE_REAL() != null) return Type.REAL;
        if (ctx.TYPE_DOUBLE() != null) return Type.REAL;   // Double is an alias of Real
        if (ctx.TYPE_FLOAT() != null) return Type.REAL;    // Float is an alias of Real
        if (ctx.TYPE_DATE() != null) return Type.DATE;
        if (ctx.TYPE_TIME() != null) return Type.TIME;
        if (ctx.TYPE_DATETIME() != null) return Type.DATETIME;
        if (ctx.TYPE_CURRENCY() != null) return Type.CURRENCY;
        if (ctx.TYPE_OBJECT() != null) return Type.OBJECT;
        if (ctx.ARRAY() != null) return Type.ARRAY;
        return Type.OBJECT;
    }
}
