package com.neoobjectpascal.desktop;

import com.neoobjectpascal.Interpreter;
import com.neoobjectpascal.NativeFunction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Public DesktopInk entry point for hosts that register native UI modules explicitly. */
public final class DesktopInk {
    private DesktopInk() {}

    public static void register(Interpreter interpreter) {
        DesktopWidgets.register(interpreter);
        interpreter.registerNative("render", (NativeFunction) (args, current) -> {
            Object source = screenSource(args);
            if (source == null) {
                System.err.println("[DesktopInk] render(...) requires a component tree or screen map");
                return null;
            }
            DesktopRuntime.render(source, optionsMap(args), current);
            return null;
        });
    }

    private static Object screenSource(List<Object> args) {
        for (Object arg : args) if (arg instanceof DesktopNode || arg instanceof Map) return arg;
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> optionsMap(List<Object> args) {
        int maps = 0;
        for (Object arg : args) if (arg instanceof Map) {
            if (maps++ == 1) return new LinkedHashMap<>((Map<String, Object>) arg);
        }
        return new LinkedHashMap<>();
    }
}
