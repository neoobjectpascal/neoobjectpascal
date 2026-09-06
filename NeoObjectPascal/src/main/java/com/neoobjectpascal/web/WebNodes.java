package com.neoobjectpascal.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tolerant conversion of loosely-typed NeoObjectPascal arguments into {@link WebNode}s,
 * mirroring TerminalInk's {@code NodeCoercion}. Widget natives receive a raw argument list:
 * the first {@code Map} is the props record ({@code #{}}); everything else is a child
 * (a {@code WebNode} passes through, a {@code List} is flattened, any other value becomes text).
 */
final class WebNodes {

    private WebNodes() {}

    /** First {@code Map} argument = props record; empty map if none. */
    @SuppressWarnings("unchecked")
    static Map<String, Object> props(List<Object> args) {
        for (Object a : args) {
            if (a instanceof Map) return (Map<String, Object>) a;
        }
        return new LinkedHashMap<>();
    }

    /** Children = every argument that is not the (first) props map, coerced to nodes. */
    static List<WebNode> children(List<Object> args) {
        List<WebNode> out = new ArrayList<>();
        boolean propsConsumed = false;
        for (Object a : args) {
            if (!propsConsumed && a instanceof Map) { propsConsumed = true; continue; }
            append(a, out);
        }
        return out;
    }

    /** Concatenated text for text widgets: every non-Map, non-node scalar argument, stringified. */
    static String text(List<Object> args) {
        StringBuilder sb = new StringBuilder();
        boolean propsConsumed = false;
        for (Object a : args) {
            if (!propsConsumed && a instanceof Map) { propsConsumed = true; continue; }
            if (a == null || a instanceof Map || a instanceof WebNode || a instanceof List) continue;
            sb.append(fmt(a));
        }
        return sb.toString();
    }

    private static void append(Object a, List<WebNode> out) {
        if (a == null) return;
        if (a instanceof WebNode) { out.add((WebNode) a); return; }
        if (a instanceof Map) return; // extra maps are not children
        if (a instanceof List) {
            for (Object e : (List<?>) a) append(e, out);
            return;
        }
        out.add(WebNode.text(fmt(a)));
    }

    /** Format a scalar for display (trim trailing .0 on whole Doubles, like the interpreter output). */
    static String fmt(Object v) {
        if (v == null) return "";
        if (v instanceof Double) {
            double d = (Double) v;
            if (d == Math.rint(d) && !Double.isInfinite(d)) return String.valueOf((long) d);
        }
        return String.valueOf(v);
    }
}
