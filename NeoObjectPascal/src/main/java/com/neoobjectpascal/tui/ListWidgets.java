package com.neoobjectpascal.tui;

import com.neoobjectpascal.NativeFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Display-only list widgets ({@code Item}, {@code UnorderedList}, {@code OrderedList}).
 *
 * <p>{@code Item} produces a {@code "ListItem"} node that merely carries its content
 * children (no marker). The list widgets consume their {@code ListItem} children and prefix
 * each with a marker: a theme glyph for {@code UnorderedList}, an incrementing "{@code n.}"
 * for {@code OrderedList}. Non-{@code ListItem} children render as-is. Markers dim.
 *
 * <p>Nesting: a nested list inside an {@code Item} is an already-expanded VBox, so it
 * renders indented after the marker naturally. Numbering is single-level per list; deep
 * hierarchical numbering ("1.1.") like @inkjs/ui is a deliberate simplification.
 */
public final class ListWidgets {

    /** Node type produced by {@code Item}: carries content children, no marker. */
    static final String LIST_ITEM = "ListItem";

    private ListWidgets() {}

    /** {@code Item(props?, children...)} — a list item carrying its content. */
    public static NativeFunction item() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            List<TuiNode> children = NodeCoercion.extractChildren(args);
            return new TuiNode(LIST_ITEM, props, children);
        };
    }

    /** {@code UnorderedList(props?, children...)} — bullet list of {@code Item}s. */
    public static NativeFunction unorderedList() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            List<TuiNode> children = NodeCoercion.extractChildren(args);
            String marker = Props.getString(props, "marker",
                    Theme.getString("UnorderedList", "marker", "─"));

            List<TuiNode> rows = new ArrayList<>();
            for (TuiNode child : children) {
                if (isListItem(child)) {
                    rows.add(markerRow(marker, child));
                } else {
                    rows.add(child);
                }
            }
            return Nodes.vbox(0, rows);
        };
    }

    /** {@code OrderedList(props?, children...)} — numbered list of {@code Item}s. */
    public static NativeFunction orderedList() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            List<TuiNode> children = NodeCoercion.extractChildren(args);
            String suffix = Props.getString(props, "marker",
                    Theme.getString("OrderedList", "marker", "."));

            int itemCount = 0;
            for (TuiNode c : children) {
                if (isListItem(c)) itemCount++;
            }
            int pad = String.valueOf(Math.max(1, itemCount)).length();

            List<TuiNode> rows = new ArrayList<>();
            int n = 1;
            for (TuiNode child : children) {
                if (isListItem(child)) {
                    String number = String.format("%" + pad + "d", n) + suffix;
                    rows.add(markerRow(number, child));
                    n++;
                } else {
                    rows.add(child);
                }
            }
            return Nodes.vbox(0, rows);
        };
    }

    // ---- helpers ----

    private static boolean isListItem(TuiNode n) {
        return n != null && LIST_ITEM.equals(n.type);
    }

    /** A row = HBox([ dim marker, item content ]); content is a single node or a column. */
    private static TuiNode markerRow(String marker, TuiNode listItem) {
        TuiNode content;
        List<TuiNode> kids = listItem.children;
        if (kids.isEmpty()) {
            content = Nodes.text("");
        } else if (kids.size() == 1) {
            content = kids.get(0);
        } else {
            content = Nodes.vbox(0, new ArrayList<>(kids));
        }
        List<TuiNode> cells = new ArrayList<>();
        cells.add(Nodes.text(marker, "dim", true));
        cells.add(content);
        return Nodes.hbox(1, cells);
    }
}
