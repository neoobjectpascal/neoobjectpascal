package com.neoobjectpascal.desktop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Plain, serialisable node used by the DesktopInk widget builders and Swing renderer. */
public final class DesktopNode {
    public static final String TEXT_PROP = "text";

    public final String type;
    public final Map<String, Object> props;
    public final List<DesktopNode> children;
    public final String key;

    public DesktopNode(String type, Map<String, Object> props, List<DesktopNode> children) {
        this.type = type;
        this.props = props == null ? new LinkedHashMap<>() : props;
        this.children = children == null ? new ArrayList<>() : children;
        this.key = Props.getString(this.props, "key", null);
    }

    public static DesktopNode of(String type, Map<String, Object> props, List<DesktopNode> children) {
        return new DesktopNode(type, props, children);
    }

    public static DesktopNode text(String text) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put(TEXT_PROP, text == null ? "" : text);
        return new DesktopNode("Text", props, new ArrayList<>());
    }

    public String textContent() {
        return Props.getString(props, TEXT_PROP, "");
    }
}
