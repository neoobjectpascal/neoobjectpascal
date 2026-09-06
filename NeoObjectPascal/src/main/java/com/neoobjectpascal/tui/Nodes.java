package com.neoobjectpascal.tui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Small builders for the primitive {@code Text}/{@code Box} nodes that phase-2 components
 * expand into. Centralising them keeps the widget factories terse and consistent (DRY).
 */
final class Nodes {

    private Nodes() {}

    /** Build a props map from alternating key/value pairs. */
    static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    /** A {@code Text} leaf carrying {@code content} plus any style key/value pairs. */
    static TuiNode text(String content, Object... styleKv) {
        Map<String, Object> p = map(styleKv);
        p.put(TuiNode.TEXT_PROP, content != null ? content : "");
        return new TuiNode("Text", p, new ArrayList<>());
    }

    /** A row {@code Box} (flexDirection=row) with the given gap. */
    static TuiNode hbox(int gap, List<TuiNode> kids) {
        return new TuiNode("Box", map("flexDirection", "row", "gap", gap), kids);
    }

    /** A column {@code Box} (flexDirection=column) with the given gap. */
    static TuiNode vbox(int gap, List<TuiNode> kids) {
        return new TuiNode("Box", map("flexDirection", "column", "gap", gap), kids);
    }

    /** A {@code Box} with explicit props and children. */
    static TuiNode box(Map<String, Object> props, List<TuiNode> kids) {
        return new TuiNode("Box", props, kids);
    }
}
