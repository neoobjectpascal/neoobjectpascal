package com.neoobjectpascal.web;

/** Shared default theme used until a render option or active WebInk runtime overrides it. */
final class WebTheme {

    private static String defaultTheme = "light";

    private WebTheme() {}

    static String defaultTheme() {
        return defaultTheme;
    }

    static void setDefault(Object theme) {
        defaultTheme = normalize(theme);
    }

    static String normalize(Object theme) {
        String value = theme == null ? "light" : String.valueOf(theme).toLowerCase();
        return "dark".equals(value) || "auto".equals(value) ? value : "light";
    }
}
