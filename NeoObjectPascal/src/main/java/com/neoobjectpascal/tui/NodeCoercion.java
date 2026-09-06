package com.neoobjectpascal.tui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts loosely-typed values coming from NeoObjectPascal into {@link TuiNode}s.
 *
 * <p>Widget natives receive a raw argument list; this class centralises the tolerant
 * parsing rules so every widget behaves consistently:
 * <ul>
 *   <li>The first {@code Map} argument is the props record.</li>
 *   <li>Children come from the first {@code List} argument (each element coerced), or a
 *       {@code String}/{@code TuiNode} argument after the props.</li>
 *   <li>A raw child coerces as: {@link TuiNode} -> itself; String -> Text node;
 *       List -> flattened; null -> skipped.</li>
 * </ul>
 */
public final class NodeCoercion {

    private NodeCoercion() {}

    /** The first {@code Map} argument, treated as the props record; empty map if none. */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> extractProps(List<Object> args) {
        for (Object a : args) {
            if (a instanceof Map) {
                return (Map<String, Object>) a;
            }
        }
        return new java.util.LinkedHashMap<>();
    }

    /**
     * Children for a container: coerces every argument that is not the props map. A
     * {@code List} is flattened; a {@code String} becomes a Text node; a {@code TuiNode}
     * passes through. The props {@code Map} (first Map seen) is skipped.
     */
    public static List<TuiNode> extractChildren(List<Object> args) {
        List<TuiNode> out = new ArrayList<>();
        boolean propsConsumed = false;
        for (Object a : args) {
            if (!propsConsumed && a instanceof Map) {
                propsConsumed = true; // first Map is props, not a child
                continue;
            }
            appendCoerced(a, out);
        }
        return out;
    }

    /**
     * Concatenated text for a {@code Text} widget: joins every non-Map argument that is a
     * String (Text takes text, not child nodes). Numbers/booleans are stringified too.
     */
    public static String extractText(List<Object> args) {
        StringBuilder sb = new StringBuilder();
        boolean propsConsumed = false;
        for (Object a : args) {
            if (!propsConsumed && a instanceof Map) {
                propsConsumed = true;
                continue;
            }
            if (a == null || a instanceof Map || a instanceof TuiNode || a instanceof List) {
                continue;
            }
            sb.append(String.valueOf(a));
        }
        return sb.toString();
    }

    /** Coerce a single raw value to a node, or null if it should be skipped. */
    public static TuiNode coerceChild(Object raw) {
        if (raw == null) return null;
        if (raw instanceof TuiNode) return (TuiNode) raw;
        if (raw instanceof String) return TuiNode.text((String) raw);
        if (raw instanceof List) {
            // A bare list as a single child -> wrap in an anonymous row Box.
            List<TuiNode> kids = new ArrayList<>();
            appendCoerced(raw, kids);
            return TuiNode.of("Box", new java.util.LinkedHashMap<>(), kids);
        }
        // Numbers/booleans etc. -> render as text.
        return TuiNode.text(String.valueOf(raw));
    }

    /** Append the coercion of {@code raw} into {@code out}, flattening nested lists. */
    private static void appendCoerced(Object raw, List<TuiNode> out) {
        if (raw == null) return;
        if (raw instanceof TuiNode) {
            TuiNode tn = (TuiNode) raw;
            if (!Props.getBool(tn.props, "visible", true)) return;   // visible:false: drop this child
            out.add(tn);
        } else if (raw instanceof String) {
            out.add(TuiNode.text((String) raw));
        } else if (raw instanceof List) {
            for (Object e : (List<?>) raw) {
                appendCoerced(e, out);
            }
        } else if (!(raw instanceof Map)) {
            out.add(TuiNode.text(String.valueOf(raw)));
        }
    }
}
