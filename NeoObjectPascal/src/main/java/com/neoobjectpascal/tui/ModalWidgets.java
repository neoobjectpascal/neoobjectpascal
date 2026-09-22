package com.neoobjectpascal.tui;

import com.neoobjectpascal.NativeFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Native modal builder and its visual expansion. */
public final class ModalWidgets {

    private ModalWidgets() {}

    /** {@code Modal(props?, children?)}. Modals are rendered by {@link TerminalRuntime}. */
    public static NativeFunction modal() {
        return (args, interp) -> {
            Map<String, Object> props = new LinkedHashMap<>(NodeCoercion.extractProps(args));
            TuiContext context = TuiContext.currentOrTransient();
            props.put("_modalKey", context.keyFor("Modal", props));
            return new TuiNode("Modal", props, NodeCoercion.extractChildren(args));
        };
    }

    /** Build a full logical-screen overlay containing a centered modal panel. */
    static TuiNode overlay(TuiNode modal, int width, int height) {
        Map<String, Object> panelProps = new LinkedHashMap<>();
        copyLayoutProp(modal.props, panelProps, "width");
        copyLayoutProp(modal.props, panelProps, "height");
        copyLayoutProp(modal.props, panelProps, "minWidth");
        copyLayoutProp(modal.props, panelProps, "minHeight");
        copyLayoutProp(modal.props, panelProps, "maxWidth");
        copyLayoutProp(modal.props, panelProps, "maxHeight");
        copyLayoutProp(modal.props, panelProps, "padding");
        copyLayoutProp(modal.props, panelProps, "paddingX");
        copyLayoutProp(modal.props, panelProps, "paddingY");
        copyLayoutProp(modal.props, panelProps, "border");
        copyLayoutProp(modal.props, panelProps, "borderColor");
        panelProps.putIfAbsent("border", "round");
        panelProps.put("flexDirection", "column");

        List<TuiNode> panelChildren = new ArrayList<>();
        String title = Props.getString(modal.props, "title", "");
        if (!title.isEmpty()) panelChildren.add(Nodes.text(title, "bold", true));
        panelChildren.addAll(modal.children);
        TuiNode panel = new TuiNode("Box", panelProps, panelChildren);

        Map<String, Object> overlayProps = Nodes.map("width", width, "height", height,
                "flexDirection", "column", "justifyContent", "center", "alignItems", "center");
        return new TuiNode("Box", overlayProps, new ArrayList<>(java.util.Collections.singletonList(panel)));
    }

    private static void copyLayoutProp(Map<String, Object> source, Map<String, Object> target, String name) {
        if (Props.has(source, name)) target.put(name, Props.get(source, name));
    }
}
