package com.neoobjectpascal.tui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A single node in the TerminalInk virtual UI tree.
 *
 * <p>The tree is a plain, serialisable data structure produced by the widget builder
 * natives ({@code Text}, {@code Box}, {@code VBox}, {@code HBox}, {@code Spacer}) and
 * consumed by {@link LayoutEngine} and {@link Renderer}. It intentionally carries no
 * behaviour so later phases can add interactive widgets without changing this shape.
 *
 * <p>Conventions (stable — phase 2 extends, does not break):
 * <ul>
 *   <li>{@link #type} — widget kind: {@code "Text"}, {@code "Box"}, {@code "Spacer"}.
 *       (VBox/HBox are {@code "Box"} with {@code flexDirection} preset.)</li>
 *   <li>{@link #props} — never null; empty map when no props. Read via {@link Props}.</li>
 *   <li>{@link #children} — never null; empty list for leaves.</li>
 *   <li>{@link #key} — optional identity hint for future reconciliation; may be null.</li>
 *   <li>Text content lives in {@code props["text"]} (a String).</li>
 * </ul>
 */
public class TuiNode {

    /** Prop key under which {@code Text} nodes store their string content. */
    public static final String TEXT_PROP = "text";

    public String type;
    public Map<String, Object> props;
    public List<TuiNode> children;
    public String key;

    public TuiNode(String type, Map<String, Object> props, List<TuiNode> children) {
        this.type = type;
        this.props = props != null ? props : new LinkedHashMap<>();
        this.children = children != null ? children : new ArrayList<>();
        this.key = Props.getString(this.props, "key", null);
    }

    /** Factory for a container/leaf node with explicit props and children. */
    public static TuiNode of(String type, Map<String, Object> props, List<TuiNode> children) {
        return new TuiNode(type, props, children);
    }

    /** Factory for a {@code Text} leaf carrying the given content and no styling. */
    public static TuiNode text(String content) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put(TEXT_PROP, content != null ? content : "");
        return new TuiNode("Text", p, new ArrayList<>());
    }

    /** True for the {@code Text} leaf widget. */
    public boolean isText() {
        return "Text".equals(type);
    }

    /** The text content of a {@code Text} node (empty string if absent). */
    public String textContent() {
        return Props.getString(props, TEXT_PROP, "");
    }

    @Override
    public String toString() {
        return "TuiNode{" + type + ", props=" + props + ", children=" + children.size() + "}";
    }
}
