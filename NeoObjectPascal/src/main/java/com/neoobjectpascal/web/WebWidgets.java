package com.neoobjectpascal.web;

import com.neoobjectpascal.Interpreter;
import com.neoobjectpascal.NativeFunction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers the WebInk widget natives. Each widget is a thin builder that returns a {@link WebNode}
 * of a given {@code type}; all layout/styling lives in {@link HtmlRenderer}. Every widget takes an
 * optional props record ({@code #{}}) first, then children (nodes) and/or scalar text.
 */
final class WebWidgets {

    private WebWidgets() {}

    /** The ~25 v1 widgets (see the design spec §5). */
    private static final String[] TYPES = {
        // layout
        "Page", "Section", "Container", "Grid", "Row", "Col", "Card", "Divider", "Spacer",
        // typography
        "Heading", "Text", "Badge", "Stat", "StatCard",
        // navigation
        "Navbar", "Sidebar", "Link", "Tabs",
        // forms
        "Button", "TextInput", "Select", "Checkbox", "TextArea", "Form",
        // data / feedback
        "Table", "List", "Chart", "Alert", "ProgressBar", "Spinner"
    };

    static void register(Interpreter interp) {
        for (String type : TYPES) {
            final String t = type;
            interp.registerNative(t, (NativeFunction) (args, i) -> build(t, args));
        }
    }

    /** Build a node: copy the props record, fold scalar args into a `text` prop, collect node children. */
    private static WebNode build(String type, List<Object> args) {
        Map<String, Object> props = new LinkedHashMap<>(WebNodes.props(args));
        String text = WebNodes.text(args);
        if (!text.isEmpty() && !props.containsKey("text")) props.put("text", text);
        return WebNode.of(type, props, WebNodes.children(args));
    }
}
