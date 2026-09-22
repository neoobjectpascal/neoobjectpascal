package com.neoobjectpascal.desktop;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.UIManager;

/** Semantic shadcn-inspired tokens shared by every Swing component. */
public final class Theme {
    public enum Token { BACKGROUND, FOREGROUND, CARD, CARD_FOREGROUND, MUTED, MUTED_FOREGROUND,
        PRIMARY, PRIMARY_FOREGROUND, SECONDARY, BORDER, INPUT, DESTRUCTIVE, RING }

    private final Map<Token, Color> colors;

    private Theme(Map<Token, Color> colors) {
        this.colors = colors;
    }

    public Color color(Token token) {
        return colors.get(token);
    }

    public static Theme resolve(String value) {
        if ("dark".equalsIgnoreCase(value)) return dark();
        if ("system".equalsIgnoreCase(value)) return system();
        return light();
    }

    /** Uses the active Swing Look and Feel surface to select a semantic palette. */
    public static Theme system() {
        Color background = UIManager.getColor("Panel.background");
        if (background == null) return light();
        int luminance = (background.getRed() * 299 + background.getGreen() * 587 + background.getBlue() * 114) / 1000;
        return luminance < 128 ? dark() : light();
    }

    public static Theme light() {
        return of("#ffffff", "#09090b", "#ffffff", "#09090b", "#f4f4f5", "#71717a",
                "#2563eb", "#ffffff", "#f4f4f5", "#e4e4e7", "#ffffff", "#dc2626", "#2563eb");
    }

    public static Theme dark() {
        return of("#09090b", "#fafafa", "#18181b", "#fafafa", "#27272a", "#a1a1aa",
                "#3b82f6", "#ffffff", "#27272a", "#27272a", "#27272a", "#f87171", "#60a5fa");
    }

    private static Theme of(String... values) {
        Token[] tokens = Token.values();
        Map<Token, Color> colors = new EnumMap<>(Token.class);
        for (int index = 0; index < tokens.length; index++) colors.put(tokens[index], Color.decode(values[index]));
        return new Theme(colors);
    }
}
