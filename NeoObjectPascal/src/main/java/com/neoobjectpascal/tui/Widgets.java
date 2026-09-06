package com.neoobjectpascal.tui;

import com.neoobjectpascal.NativeFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory natives for the phase-1 static layout widgets. Each returns a {@link TuiNode}
 * from a tolerant argument list (see {@link NodeCoercion} for the parsing rules):
 * <ul>
 *   <li>{@code Text(props?, text...)} — leaf; string args concatenate.</li>
 *   <li>{@code Box(props?, children?)} — flex container (default row).</li>
 *   <li>{@code VBox(props?, children?)} — Box with flexDirection = column.</li>
 *   <li>{@code HBox(props?, children?)} — Box with flexDirection = row.</li>
 *   <li>{@code Spacer()} — zero-size filler with flexGrow = 1.</li>
 * </ul>
 */
public final class Widgets {

    private Widgets() {}

    /** {@code Text(props?, text...)} */
    public static NativeFunction text() {
        return (args, interp) -> {
            Map<String, Object> props = new LinkedHashMap<>(NodeCoercion.extractProps(args));
            props.put(TuiNode.TEXT_PROP, NodeCoercion.extractText(args));
            return new TuiNode("Text", props, new ArrayList<>());
        };
    }

    /** {@code Box(props?, children?)} — direction defaults to row (like Ink). */
    public static NativeFunction box() {
        return (args, interp) -> makeBox(args, null);
    }

    /** {@code VBox(props?, children?)} — column direction. */
    public static NativeFunction vbox() {
        return (args, interp) -> makeBox(args, "column");
    }

    /** {@code HBox(props?, children?)} — row direction. */
    public static NativeFunction hbox() {
        return (args, interp) -> makeBox(args, "row");
    }

    /** {@code Spacer()} — flexible filler. */
    public static NativeFunction spacer() {
        return (args, interp) -> {
            Map<String, Object> props = new LinkedHashMap<>(NodeCoercion.extractProps(args));
            props.putIfAbsent("flexGrow", 1);
            return new TuiNode("Spacer", props, new ArrayList<>());
        };
    }

    private static TuiNode makeBox(List<Object> args, String forcedDirection) {
        Map<String, Object> props = new LinkedHashMap<>(NodeCoercion.extractProps(args));
        if (forcedDirection != null) {
            props.put("flexDirection", forcedDirection);
        }
        List<TuiNode> children = NodeCoercion.extractChildren(args);
        return new TuiNode("Box", props, children);
    }
}
