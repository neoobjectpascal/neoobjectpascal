package com.neoobjectpascal.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A single node in the WebInk virtual UI tree — the web analog of {@code TuiNode}.
 *
 * <p>The tree is a plain, serialisable data structure produced by the widget builder natives
 * ({@code Page}, {@code Card}, {@code Button}, {@code Chart}, ...) and consumed by
 * {@link HtmlRenderer}. It carries no behaviour: event props (onClick/onChange/onSubmit) hold
 * NeoObjectPascal function references that {@link WebRuntime} wires to server-side handler ids.
 */
public class WebNode {

    /** Prop key under which text-bearing nodes store their string content. */
    public static final String TEXT_PROP = "text";

    public String type;
    public Map<String, Object> props;
    public List<WebNode> children;
    public String key;

    public WebNode(String type, Map<String, Object> props, List<WebNode> children) {
        this.type = type;
        this.props = props != null ? props : new LinkedHashMap<>();
        this.children = children != null ? children : new ArrayList<>();
        Object k = this.props.get("key");
        this.key = k != null ? String.valueOf(k) : null;
    }

    public static WebNode of(String type, Map<String, Object> props, List<WebNode> children) {
        return new WebNode(type, props, children);
    }

    /** A plain text leaf carrying the given content. */
    public static WebNode text(String content) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put(TEXT_PROP, content != null ? content : "");
        return new WebNode("Text", p, new ArrayList<>());
    }

    public String textContent() {
        Object v = props.get(TEXT_PROP);
        return v != null ? String.valueOf(v) : "";
    }

    /** True for the plain {@code Text} leaf (used when a widget accepts a text child). */
    public boolean isTextLike() {
        return "Text".equals(type);
    }

    @Override
    public String toString() {
        return "WebNode{" + type + ", props=" + props + ", children=" + children.size() + "}";
    }
}
