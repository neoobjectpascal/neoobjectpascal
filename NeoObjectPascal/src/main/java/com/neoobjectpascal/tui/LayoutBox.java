package com.neoobjectpascal.tui;

/**
 * An immutable absolute rectangle in terminal cells (top-left origin).
 * Used to describe both a node's outer box and its inner content area.
 */
public final class LayoutBox {

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public LayoutBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    @Override
    public String toString() {
        return "LayoutBox{x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + "}";
    }
}
