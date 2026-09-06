package com.neoobjectpascal;

import java.util.List;

/**
 * A function implemented in Java but callable from NeoObjectPascal code by name
 * (like a built-in). Registered in {@link InterpreterState#nativeFunctions}, e.g. by
 * the TerminalInk runtime when a program does {@code uses terminalink;}.
 */
@FunctionalInterface
public interface NativeFunction {
    Object call(List<Object> args, Interpreter interp);
}
