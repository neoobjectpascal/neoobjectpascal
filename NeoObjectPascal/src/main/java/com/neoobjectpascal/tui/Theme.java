package com.neoobjectpascal.tui;

import com.neoobjectpascal.NativeFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immediate-mode theming for TerminalInk.
 *
 * <p>A theme is a plain record (nested {@code Map}) whose top-level keys are component
 * names and whose leaf values are style tokens (color names / marker glyphs). There is a
 * single {@link #active} theme held statically — there is no React-style scoping; whatever
 * theme is active when a component expands wins. {@code setTheme}/{@code ThemeProvider}
 * mutate the global active theme as a side effect and therefore take effect from the frame
 * they run in (a stable theme is invisible across frames).
 *
 * <p>Resolution order for any token is: the component's own prop (checked by the widget) →
 * the active theme ({@link #get}) → the hardcoded fallback passed by the widget.
 */
public final class Theme {

    private static Map<String, Object> active = defaultTheme();

    private Theme() {}

    // ---- Default theme (the hardcoded defaults, surfaced as data) ----

    /** A fresh copy of the default theme record. */
    public static Map<String, Object> defaultTheme() {
        Map<String, Object> t = new LinkedHashMap<>();
        t.put("Spinner", Nodes.map("color", "blue"));
        t.put("Select", Nodes.map("pointerColor", "cyan", "selectedColor", "green"));
        t.put("MultiSelect", Nodes.map("pointerColor", "cyan", "selectedColor", "green"));
        t.put("StatusMessage", Nodes.map(
                "infoColor", "blue", "successColor", "green",
                "errorColor", "red", "warningColor", "yellow"));
        t.put("Alert", Nodes.map(
                "infoColor", "blue", "successColor", "green",
                "errorColor", "red", "warningColor", "yellow"));
        t.put("Badge", Nodes.map("color", "magenta"));
        t.put("ProgressBar", Nodes.map(
                "complete", Glyphs.SQUARE, "remaining", Glyphs.LIGHT_SQUARE,
                "completeColor", "magenta", "remainingColor", "gray"));
        t.put("UnorderedList", Nodes.map("marker", "─"));
        t.put("OrderedList", Nodes.map("marker", "."));
        return t;
    }

    // ---- Active theme access ----

    /** The current active theme (never null). */
    public static Map<String, Object> activeTheme() {
        return active;
    }

    /** Replace the active theme (used by {@code setTheme} / {@code ThemeProvider}). */
    public static void setActive(Map<String, Object> theme) {
        if (theme != null) {
            active = theme;
        }
    }

    /** Restore the default theme (used by tests to isolate global state). */
    public static void reset() {
        active = defaultTheme();
    }

    /** Read {@code active[component][key]}, or {@code fallback} if absent. */
    public static Object get(String component, String key, Object fallback) {
        Object comp = active.get(component);
        if (comp instanceof Map) {
            Object v = ((Map<?, ?>) comp).get(key);
            if (v != null) {
                return v;
            }
        }
        return fallback;
    }

    /** String form of {@link #get}. */
    public static String getString(String component, String key, String fallback) {
        Object v = get(component, key, null);
        return v == null ? fallback : String.valueOf(v);
    }

    // ---- Deep merge ----

    /** Deep-merge two theme records; {@code override} wins at leaf level; returns a new record. */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> deepMerge(Map<String, Object> base, Map<String, Object> override) {
        Map<String, Object> out = deepCopy(base);
        if (override != null) {
            for (Map.Entry<String, Object> e : override.entrySet()) {
                Object bv = out.get(e.getKey());
                Object ov = e.getValue();
                if (bv instanceof Map && ov instanceof Map) {
                    out.put(e.getKey(), deepMerge((Map<String, Object>) bv, (Map<String, Object>) ov));
                } else if (ov instanceof Map) {
                    out.put(e.getKey(), deepCopy((Map<String, Object>) ov));
                } else {
                    out.put(e.getKey(), ov);
                }
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> deepCopy(Map<String, Object> m) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (m != null) {
            for (Map.Entry<String, Object> e : m.entrySet()) {
                Object v = e.getValue();
                out.put(e.getKey(), v instanceof Map ? deepCopy((Map<String, Object>) v) : v);
            }
        }
        return out;
    }

    // ---- Native factories ----

    /** {@code defaultTheme()} -> the default theme record. */
    public static NativeFunction defaultThemeFn() {
        return (args, interp) -> defaultTheme();
    }

    /** {@code extendTheme(base, override)} -> deep-merged record. */
    public static NativeFunction extendThemeFn() {
        return (args, interp) -> {
            List<Map<String, Object>> maps = maps(args);
            Map<String, Object> base = maps.size() > 0 ? maps.get(0) : new LinkedHashMap<>();
            Map<String, Object> override = maps.size() > 1 ? maps.get(1) : new LinkedHashMap<>();
            return deepMerge(base, override);
        };
    }

    /** {@code setTheme(theme)} -> set global active theme (side effect); returns null. */
    public static NativeFunction setThemeFn() {
        return (args, interp) -> {
            List<Map<String, Object>> maps = maps(args);
            if (!maps.isEmpty()) {
                setActive(maps.get(0));
            }
            return null;
        };
    }

    /** {@code ThemeProvider(#{theme:t}, children)} -> sets active theme, returns children in a Box. */
    @SuppressWarnings("unchecked")
    public static NativeFunction themeProviderFn() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            Object t = Props.get(props, "theme");
            if (t instanceof Map) {
                setActive((Map<String, Object>) t);
            }
            List<TuiNode> children = NodeCoercion.extractChildren(args);
            return Nodes.box(Nodes.map(), children);
        };
    }

    /** All {@code Map} arguments, in order (for extendTheme's two records). */
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> maps(List<Object> args) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object a : args) {
            if (a instanceof Map) {
                out.add((Map<String, Object>) a);
            }
        }
        return out;
    }
}
