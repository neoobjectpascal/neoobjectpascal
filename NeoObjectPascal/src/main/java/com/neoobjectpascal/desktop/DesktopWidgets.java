package com.neoobjectpascal.desktop;

import com.neoobjectpascal.Interpreter;
import com.neoobjectpascal.NativeFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registers tolerant node builders for the DesktopInk component vocabulary. */
final class DesktopWidgets {
    private static final String[] TYPES = { "Window", "Container", "Section", "Grid", "Row", "Col",
            "Card", "Divider", "Spacer", "Heading", "Text", "Badge", "StatCard", "Alert", "Button",
            "TextInput", "PasswordInput", "TextArea", "Select", "Checkbox", "Form", "Table", "List",
            "ProgressBar", "Spinner", "Modal" };

    private DesktopWidgets() {}

    static void register(Interpreter interpreter) {
        for (String type : TYPES) {
            final String nodeType = type;
            interpreter.registerNative(nodeType, (NativeFunction) (args, ignored) -> build(nodeType, args));
        }
    }

    private static DesktopNode build(String type, List<Object> args) {
        Map<String, Object> props = new LinkedHashMap<>(props(args));
        String text = text(args);
        if (!text.isEmpty() && !props.containsKey(DesktopNode.TEXT_PROP)) props.put(DesktopNode.TEXT_PROP, text);
        return DesktopNode.of(type, props, children(args));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> props(List<Object> args) {
        for (Object arg : args) if (arg instanceof Map) return (Map<String, Object>) arg;
        return new LinkedHashMap<>();
    }

    private static List<DesktopNode> children(List<Object> args) {
        List<DesktopNode> children = new ArrayList<>();
        boolean consumedProps = false;
        for (Object arg : args) {
            if (!consumedProps && arg instanceof Map) { consumedProps = true; continue; }
            append(arg, children);
        }
        return children;
    }

    private static void append(Object value, List<DesktopNode> children) {
        if (value instanceof DesktopNode) children.add((DesktopNode) value);
        else if (value instanceof List) for (Object item : (List<?>) value) append(item, children);
        else if (value != null && !(value instanceof Map)) children.add(DesktopNode.text(String.valueOf(value)));
    }

    private static String text(List<Object> args) {
        StringBuilder text = new StringBuilder();
        for (Object arg : args) if (arg != null && !(arg instanceof Map) && !(arg instanceof DesktopNode)
                && !(arg instanceof List)) text.append(arg);
        return text.toString();
    }
}
