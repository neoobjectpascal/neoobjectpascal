package com.neoobjectpascal.tui;

import com.neoobjectpascal.NativeFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Stateless phase-2 feedback / display widgets. Each expands to a primitive
 * {@code Text}/{@code Box} tree (or, for {@code ProgressBar}, a self-drawing leaf node) so
 * the phase-1 layout/render pipeline handles the drawing.
 * <ul>
 *   <li>{@code Spinner(props?)} — time-based animated glyph + optional label.</li>
 *   <li>{@code ProgressBar(props?)} — a {@code "ProgressBar"} leaf filled by the renderer.</li>
 *   <li>{@code Badge(props?, text...)} — inverse-padded uppercase label.</li>
 *   <li>{@code StatusMessage(props?, text...)} — variant icon + message.</li>
 *   <li>{@code Alert(props?, text...)} — bordered variant panel with optional title.</li>
 * </ul>
 */
public final class FeedbackWidgets {

    /** Spinner animation cadence in milliseconds. */
    private static final long SPINNER_INTERVAL_MS = 80L;

    private FeedbackWidgets() {}

    // ===================================================================== Spinner

    /** {@code Spinner(props?)} — props: type("dots"), label. */
    public static NativeFunction spinner() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            String frame = spinnerFrame(System.currentTimeMillis());
            String label = Props.getString(props, "label", "");
            String color = Props.getString(props, "color", Theme.getString("Spinner", "color", "blue"));

            List<TuiNode> cells = new ArrayList<>();
            cells.add(Nodes.text(frame, "color", color));
            if (!label.isEmpty()) {
                cells.add(Nodes.text(label));
            }
            return Nodes.hbox(1, cells);
        };
    }

    /** Current spinner frame for the given wall-clock time (type "dots"). */
    static String spinnerFrame(long millis) {
        String[] frames = Glyphs.SPINNER_DOTS;
        int idx = (int) ((millis / SPINNER_INTERVAL_MS) % frames.length);
        return frames[idx];
    }

    // ================================================================= ProgressBar

    /** {@code ProgressBar(props?)} — props: value(0-100). A self-drawing leaf node. */
    public static NativeFunction progressBar() {
        return (args, interp) -> {
            Map<String, Object> props = new java.util.LinkedHashMap<>(NodeCoercion.extractProps(args));
            return new TuiNode("ProgressBar", props, new ArrayList<>());
        };
    }

    // ======================================================================= Badge

    /** {@code Badge(props?, text...)} — props: color(default "magenta"). */
    public static NativeFunction badge() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            String color = Props.getString(props, "color", Theme.getString("Badge", "color", "magenta"));
            String text = NodeCoercion.extractText(args).toUpperCase();
            String content = " " + text + " ";
            return Nodes.text(content, "backgroundColor", color, "color", "black");
        };
    }

    // =============================================================== StatusMessage

    /** {@code StatusMessage(props?, text...)} — props: variant(info/success/error/warning). */
    public static NativeFunction statusMessage() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            String[] iconColor = variantIconColor("StatusMessage", Props.getString(props, "variant", "info"));
            String message = NodeCoercion.extractText(args);
            List<TuiNode> cells = Arrays.asList(
                    Nodes.text(iconColor[0], "color", iconColor[1]),
                    Nodes.text(message));
            return Nodes.hbox(1, cells);
        };
    }

    // ======================================================================= Alert

    /** {@code Alert(props?, text...)} — props: variant, title(optional). */
    public static NativeFunction alert() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            String variant = Props.getString(props, "variant", "info");
            String[] iconColor = variantIconColor("Alert", variant);
            String title = Props.getString(props, "title", null);
            String message = NodeCoercion.extractText(args);

            List<TuiNode> bodyLines = new ArrayList<>();
            if (title != null && !title.isEmpty()) {
                bodyLines.add(Nodes.text(title, "bold", true));
            }
            bodyLines.add(Nodes.text(message));

            List<TuiNode> row = Arrays.asList(
                    Nodes.text(iconColor[0], "color", iconColor[1]),
                    Nodes.vbox(0, bodyLines));

            Map<String, Object> boxProps = Nodes.map(
                    "border", "round",
                    "borderColor", iconColor[1],
                    "paddingX", 1,
                    "gap", 1,
                    "flexDirection", "row");
            return Nodes.box(boxProps, row);
        };
    }

    // ====================================================================== helpers

    /**
     * Map a variant name to its {icon, colorName} for {@code component}. Icons are fixed
     * glyphs; colors resolve through the active theme ({@code <component>.<variant>Color})
     * with the hardcoded default as fallback. Unknown variant -> info.
     */
    static String[] variantIconColor(String component, String variant) {
        String v = variant == null ? "info" : variant.trim().toLowerCase();
        switch (v) {
            case "success": return new String[]{Glyphs.TICK, Theme.getString(component, "successColor", "green")};
            case "error":   return new String[]{Glyphs.CROSS, Theme.getString(component, "errorColor", "red")};
            case "warning": return new String[]{Glyphs.WARNING, Theme.getString(component, "warningColor", "yellow")};
            case "info":
            default:        return new String[]{Glyphs.INFO, Theme.getString(component, "infoColor", "blue")};
        }
    }
}
