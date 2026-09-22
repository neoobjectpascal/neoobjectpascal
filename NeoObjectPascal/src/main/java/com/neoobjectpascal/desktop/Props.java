package com.neoobjectpascal.desktop;

import java.util.Map;

/** Null-safe coercion for values supplied by the dynamically typed interpreter. */
public final class Props {
    private Props() {}

    public static Object get(Map<String, Object> props, String key) {
        return props == null ? null : props.get(key);
    }

    public static String getString(Map<String, Object> props, String key, String fallback) {
        Object value = get(props, key);
        return value == null ? fallback : String.valueOf(value);
    }

    public static int getInt(Map<String, Object> props, String key, int fallback) {
        Object value = get(props, key);
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof Boolean) return (Boolean) value ? 1 : 0;
        if (value instanceof String) {
            try { return (int) Double.parseDouble(((String) value).trim()); }
            catch (NumberFormatException ignored) { return fallback; }
        }
        return fallback;
    }

    public static boolean getBool(Map<String, Object> props, String key, boolean fallback) {
        Object value = get(props, key);
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) {
            String text = ((String) value).trim().toLowerCase();
            if (text.equals("true") || text.equals("1") || text.equals("yes")) return true;
            if (text.equals("false") || text.equals("0") || text.equals("no")) return false;
        }
        return fallback;
    }
}
