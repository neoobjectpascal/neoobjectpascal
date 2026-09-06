package com.neoobjectpascal.tui;

import com.googlecode.lanterna.TextColor;

import java.util.Map;

/**
 * Type-tolerant readers for the {@code props} map of a {@link TuiNode}.
 *
 * <p>Values arrive from NeoObjectPascal loosely typed (Integer/Double/String/Boolean),
 * so every getter coerces and falls back to a default rather than throwing. All methods
 * are null-safe on the map itself.
 */
public final class Props {

    private Props() {}

    /** Raw value for {@code key}, or null. */
    public static Object get(Map<String, Object> m, String key) {
        return m == null ? null : m.get(key);
    }

    /** True when {@code key} is present with a non-null value. */
    public static boolean has(Map<String, Object> m, String key) {
        return m != null && m.get(key) != null;
    }

    public static String getString(Map<String, Object> m, String key, String def) {
        Object v = get(m, key);
        return v == null ? def : String.valueOf(v);
    }

    /** Coerces Integer/Double/Number/numeric-String to int; else {@code def}. */
    public static int getInt(Map<String, Object> m, String key, int def) {
        Object v = get(m, key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof Boolean) return ((Boolean) v) ? 1 : 0;
        if (v instanceof String) {
            try {
                return (int) Double.parseDouble(((String) v).trim());
            } catch (NumberFormatException ignored) {
                return def;
            }
        }
        return def;
    }

    /** Coerces Boolean/non-zero-Number/"true"/"1"/"yes" to boolean; else {@code def}. */
    public static boolean getBool(Map<String, Object> m, String key, boolean def) {
        Object v = get(m, key);
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof Number) return ((Number) v).doubleValue() != 0.0;
        if (v instanceof String) {
            String s = ((String) v).trim().toLowerCase();
            if (s.equals("true") || s.equals("1") || s.equals("yes")) return true;
            if (s.equals("false") || s.equals("0") || s.equals("no")) return false;
        }
        return def;
    }

    /** Resolves a color-name prop to a {@link TextColor}; unknown/absent -> {@code def}. */
    public static TextColor getColor(Map<String, Object> m, String key, TextColor def) {
        Object v = get(m, key);
        if (v == null) return def;
        return colorByName(String.valueOf(v), def);
    }

    /**
     * Maps a color name to a Lanterna ANSI color. Recognised:
     * red, green, yellow, blue, magenta, cyan, white, black, gray/grey, default.
     * Anything else falls back to {@code def}.
     */
    public static TextColor colorByName(String name, TextColor def) {
        if (name == null) return def;
        switch (name.trim().toLowerCase()) {
            case "red": return TextColor.ANSI.RED;
            case "green": return TextColor.ANSI.GREEN;
            case "yellow": return TextColor.ANSI.YELLOW;
            case "blue": return TextColor.ANSI.BLUE;
            case "magenta": return TextColor.ANSI.MAGENTA;
            case "cyan": return TextColor.ANSI.CYAN;
            case "white": return TextColor.ANSI.WHITE;
            case "black": return TextColor.ANSI.BLACK;
            case "gray":
            case "grey": return TextColor.ANSI.BLACK_BRIGHT;
            case "default": return TextColor.ANSI.DEFAULT;
            default: return def;
        }
    }
}
