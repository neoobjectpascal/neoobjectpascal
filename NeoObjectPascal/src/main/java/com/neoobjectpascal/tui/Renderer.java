package com.neoobjectpascal.tui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Draws a laid-out {@link LaidOutNode} tree onto a Lanterna {@link TextGraphics} surface.
 *
 * <p>Every write is clipped to both the node's box and the screen bounds, so nodes that
 * overflow the terminal are truncated rather than throwing. Borders are drawn first, then
 * children, so child content appears inside its container's frame.
 */
public final class Renderer {

    private Renderer() {}

    // Border glyph sets: [topLeft, topRight, bottomLeft, bottomRight, horizontal, vertical]
    private static final char[] ROUND = {'╭', '╮', '╰', '╯', '─', '│'};
    private static final char[] SINGLE = {'┌', '┐', '└', '┘', '─', '│'};

    /** Render the whole tree. {@code size} is the current terminal size for clipping. */
    public static void render(TextGraphics graphics, LaidOutNode root, TerminalSize size) {
        render(graphics, root, size, 0, 0);
    }

    /** Render logical coordinates translated into a physical terminal viewport. */
    public static void render(TextGraphics graphics, LaidOutNode root, TerminalSize size, int offsetX, int offsetY) {
        draw(graphics, root, size.getColumns(), size.getRows(), offsetX, offsetY);
    }

    private static void draw(TextGraphics graphics, LaidOutNode laid, int cols, int rows, int offsetX, int offsetY) {
        TuiNode n = laid.node;
        if (n.isText()) {
            drawText(graphics, laid, cols, rows, offsetX, offsetY);
            return;
        }
        if ("ProgressBar".equals(n.type)) {
            drawProgressBar(graphics, laid, cols, rows, offsetX, offsetY);
            return;
        }
        if (LayoutEngine.hasBorder(n)) {
            drawBorder(graphics, laid, cols, rows, offsetX, offsetY);
        }
        for (LaidOutNode child : laid.children) {
            draw(graphics, child, cols, rows, offsetX, offsetY);
        }
    }

    private static void drawText(TextGraphics graphics, LaidOutNode laid, int cols, int rows, int offsetX, int offsetY) {
        TuiNode n = laid.node;
        LayoutBox b = laid.content;
        String text = n.textContent();
        int row = b.y + offsetY;
        if (row < 0 || row >= rows) return;

        TextColor fg = Props.getColor(n.props, "color", TextColor.ANSI.DEFAULT);
        boolean dim = Props.getBool(n.props, "dim", false);
        if (dim) fg = TextColor.ANSI.BLACK_BRIGHT; // no reliable dim SGR; approximate with gray
        TextColor bg = Props.getColor(n.props, "backgroundColor",
                Props.getColor(n.props, "bg", TextColor.ANSI.DEFAULT));

        graphics.setForegroundColor(fg);
        graphics.setBackgroundColor(bg);
        if (Props.getBool(n.props, "bold", false)) graphics.enableModifiers(SGR.BOLD);
        if (Props.getBool(n.props, "inverse", false)) graphics.enableModifiers(SGR.REVERSE);

        int x = b.x + offsetX;
        int maxWidth = Math.min(b.width, cols - x);
        if (x >= 0 && maxWidth > 0 && !text.isEmpty()) {
            String clipped = text.length() > maxWidth ? text.substring(0, maxWidth) : text;
            graphics.putString(x, row, clipped);
        }

        graphics.disableModifiers(SGR.BOLD, SGR.REVERSE);
        graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }

    private static void drawProgressBar(TextGraphics graphics, LaidOutNode laid, int cols, int rows, int offsetX, int offsetY) {
        TuiNode n = laid.node;
        LayoutBox b = laid.content;
        int row = b.y + offsetY;
        if (row < 0 || row >= rows) return;

        int x = b.x + offsetX;
        int width = Math.min(b.width, cols - x);
        if (x < 0 || width <= 0) return;

        int value = Math.max(0, Math.min(100, Props.getInt(n.props, "value", 0)));
        int complete = Math.round(value / 100f * width);

        // Glyphs + colors resolve through the active theme, falling back to the defaults.
        char completeCh = firstChar(Theme.getString("ProgressBar", "complete", Glyphs.SQUARE), Glyphs.SQUARE_CH);
        char remainingCh = firstChar(Theme.getString("ProgressBar", "remaining", Glyphs.LIGHT_SQUARE), Glyphs.LIGHT_SQUARE_CH);
        TextColor completeColor = Props.colorByName(
                Theme.getString("ProgressBar", "completeColor", "magenta"), TextColor.ANSI.MAGENTA);
        TextColor remainingColor = Props.colorByName(
                Theme.getString("ProgressBar", "remainingColor", "gray"), TextColor.ANSI.BLACK_BRIGHT);

        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        for (int i = 0; i < width; i++) {
            boolean filled = i < complete;
            graphics.setForegroundColor(filled ? completeColor : remainingColor);
            put(graphics, x + i, row, filled ? completeCh : remainingCh, cols, rows);
        }
        graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    private static void drawBorder(TextGraphics graphics, LaidOutNode laid, int cols, int rows, int offsetX, int offsetY) {
        LayoutBox b = laid.outer;
        if (b.width < 2 || b.height < 2) return; // no room for a frame

        char[] g = "round".equalsIgnoreCase(Props.getString(laid.node.props, "border", "")) ? ROUND : SINGLE;
        TextColor color = Props.getColor(laid.node.props, "borderColor", TextColor.ANSI.DEFAULT);
        graphics.setForegroundColor(color);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);

        int left = b.x + offsetX;
        int right = left + b.width - 1;
        int top = b.y + offsetY;
        int bottom = top + b.height - 1;

        // Corners
        put(graphics, left, top, g[0], cols, rows);
        put(graphics, right, top, g[1], cols, rows);
        put(graphics, left, bottom, g[2], cols, rows);
        put(graphics, right, bottom, g[3], cols, rows);
        // Horizontal edges
        for (int x = left + 1; x < right; x++) {
            put(graphics, x, top, g[4], cols, rows);
            put(graphics, x, bottom, g[4], cols, rows);
        }
        // Vertical edges
        for (int y = top + 1; y < bottom; y++) {
            put(graphics, left, y, g[5], cols, rows);
            put(graphics, right, y, g[5], cols, rows);
        }
        graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    /** First char of {@code s}, or {@code fallback} when {@code s} is null/empty. */
    private static char firstChar(String s, char fallback) {
        return (s == null || s.isEmpty()) ? fallback : s.charAt(0);
    }

    private static void put(TextGraphics graphics, int x, int y, char c, int cols, int rows) {
        if (x < 0 || y < 0 || x >= cols || y >= rows) return;
        graphics.setCharacter(x, y, c);
    }
}
