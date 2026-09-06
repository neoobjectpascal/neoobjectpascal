package com.neoobjectpascal.web;

import java.util.Map;

/** Tolerant readers for widget prop maps (record literals {@code #{}}). Web analog of tui.Props. */
final class Props {

    private Props() {}

    static Object get(Map<String, Object> m, String key) {
        return m == null ? null : m.get(key);
    }

    static boolean has(Map<String, Object> m, String key) {
        return m != null && m.get(key) != null;
    }

    static String getString(Map<String, Object> m, String key, String def) {
        Object v = get(m, key);
        return v != null ? String.valueOf(v) : def;
    }

    static int getInt(Map<String, Object> m, String key, int def) {
        Object v = get(m, key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) {
            try { return Integer.parseInt(((String) v).trim()); } catch (NumberFormatException e) { return def; }
        }
        return def;
    }

    static boolean getBool(Map<String, Object> m, String key, boolean def) {
        Object v = get(m, key);
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof String) return Boolean.parseBoolean((String) v);
        return def;
    }
}
