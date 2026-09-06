package com.neoobjectpascal.tui;

import java.util.List;

/**
 * A small flexbox subset that assigns an absolute {@link LayoutBox} to every node.
 *
 * <p>Pipeline: compute each node's intrinsic (natural) outer size bottom-up, then place
 * nodes top-down within an assigned box, distributing free main-axis space to
 * {@code flexGrow} children and honouring {@code justifyContent}/{@code alignItems}.
 *
 * <p>Model notes:
 * <ul>
 *   <li>Insets = border (1 cell/side when present) + padding (paddingX/paddingY, or
 *       {@code padding} for both).</li>
 *   <li>Main axis is width for rows, height for columns; cross axis is the other.</li>
 *   <li>Text is a leaf sized {@code (length, 1)} with no wrapping in v1.</li>
 *   <li>No cross-axis stretch in v1 — children keep their intrinsic cross size.</li>
 * </ul>
 */
public final class LayoutEngine {

    private LayoutEngine() {}

    // ---- Public entry point ----

    /**
     * Lay out {@code root} at the screen origin. The root fills the screen unless it
     * declares an explicit {@code width}/{@code height}, in which case that wins.
     */
    public static LaidOutNode layout(TuiNode root, int screenW, int screenH) {
        int w = Props.has(root.props, "width") ? Props.getInt(root.props, "width", screenW) : screenW;
        int h = Props.has(root.props, "height") ? Props.getInt(root.props, "height", screenH) : screenH;
        return layoutInto(root, 0, 0, Math.max(0, w), Math.max(0, h));
    }

    // ---- Insets ----

    static boolean hasBorder(TuiNode n) {
        Object b = Props.get(n.props, "border");
        if (b == null) return false;
        if (b instanceof Boolean) return (Boolean) b;
        // Any non-empty string ("round"/"single"/...) means bordered.
        String s = String.valueOf(b).trim();
        return !s.isEmpty() && !s.equalsIgnoreCase("false") && !s.equals("0");
    }

    static int padX(TuiNode n) {
        return Props.getInt(n.props, "paddingX", Props.getInt(n.props, "padding", 0));
    }

    static int padY(TuiNode n) {
        return Props.getInt(n.props, "paddingY", Props.getInt(n.props, "padding", 0));
    }

    private static int insetX(TuiNode n) {
        return (hasBorder(n) ? 1 : 0) + padX(n);
    }

    private static int insetY(TuiNode n) {
        return (hasBorder(n) ? 1 : 0) + padY(n);
    }

    static boolean isColumn(TuiNode n) {
        return "column".equalsIgnoreCase(Props.getString(n.props, "flexDirection", "row"));
    }

    static int flexGrow(TuiNode n) {
        if ("Spacer".equals(n.type)) return Props.getInt(n.props, "flexGrow", 1);
        // A ProgressBar stretches to fill its row/column by default.
        if ("ProgressBar".equals(n.type)) return Props.getInt(n.props, "flexGrow", 1);
        return Props.getInt(n.props, "flexGrow", 0);
    }

    // ---- Intrinsic sizing (outer size, bottom-up) ----

    /** Natural outer size {width, height} of a node given no external constraints. */
    static int[] intrinsicSize(TuiNode n) {
        if (n.isText()) {
            int[] sz = {n.textContent().length(), 1};
            return applyExplicit(n, sz);
        }
        if ("Spacer".equals(n.type)) {
            return applyExplicit(n, new int[]{0, 0});
        }
        if ("ProgressBar".equals(n.type)) {
            // Flexible width (grows via flexGrow), intrinsic height 1.
            return applyExplicit(n, new int[]{0, 1});
        }

        boolean column = isColumn(n);
        int gap = Props.getInt(n.props, "gap", 0);
        List<TuiNode> kids = n.children;

        int mainSum = 0;
        int crossMax = 0;
        int count = 0;
        for (TuiNode c : kids) {
            int[] cs = intrinsicSize(c);
            int cMain = column ? cs[1] : cs[0];
            int cCross = column ? cs[0] : cs[1];
            mainSum += cMain;
            crossMax = Math.max(crossMax, cCross);
            count++;
        }
        if (count > 1) mainSum += gap * (count - 1);

        int contentW = column ? crossMax : mainSum;
        int contentH = column ? mainSum : crossMax;
        int outerW = contentW + 2 * insetX(n);
        int outerH = contentH + 2 * insetY(n);
        return applyExplicit(n, new int[]{outerW, outerH});
    }

    /** Apply explicit width/height/minWidth overrides to an intrinsic size. */
    private static int[] applyExplicit(TuiNode n, int[] sz) {
        int w = sz[0];
        int h = sz[1];
        if (Props.has(n.props, "width")) w = Props.getInt(n.props, "width", w);
        if (Props.has(n.props, "height")) h = Props.getInt(n.props, "height", h);
        if (Props.has(n.props, "minWidth")) w = Math.max(w, Props.getInt(n.props, "minWidth", 0));
        return new int[]{Math.max(0, w), Math.max(0, h)};
    }

    // ---- Placement (top-down) ----

    private static LaidOutNode layoutInto(TuiNode n, int x, int y, int width, int height) {
        int ix = insetX(n);
        int iy = insetY(n);
        LayoutBox outer = new LayoutBox(x, y, width, height);
        LayoutBox content = new LayoutBox(x + ix, y + iy,
                Math.max(0, width - 2 * ix), Math.max(0, height - 2 * iy));
        LaidOutNode laid = new LaidOutNode(n, outer, content);

        if (n.isText() || n.children.isEmpty()) {
            return laid;
        }

        boolean column = isColumn(n);
        int gap = Props.getInt(n.props, "gap", 0);
        int mainExtent = column ? content.height : content.width;
        int crossExtent = column ? content.width : content.height;

        int count = n.children.size();
        int[] mainSizes = new int[count];
        int[] crossSizes = new int[count];
        int[] grow = new int[count];
        int totalGrow = 0;
        int sumMain = 0;
        for (int i = 0; i < count; i++) {
            TuiNode c = n.children.get(i);
            int[] cs = intrinsicSize(c);
            mainSizes[i] = column ? cs[1] : cs[0];
            crossSizes[i] = column ? cs[0] : cs[1];
            grow[i] = flexGrow(c);
            totalGrow += grow[i];
            sumMain += mainSizes[i];
        }
        int totalGap = count > 1 ? gap * (count - 1) : 0;
        int freeSpace = mainExtent - sumMain - totalGap;

        // Distribute leftover main space to flex-growing children.
        if (totalGrow > 0 && freeSpace > 0) {
            int distributed = 0;
            int lastGrow = -1;
            for (int i = 0; i < count; i++) {
                if (grow[i] > 0) {
                    int add = (int) Math.floor((double) freeSpace * grow[i] / totalGrow);
                    mainSizes[i] += add;
                    distributed += add;
                    lastGrow = i;
                }
            }
            if (lastGrow >= 0) mainSizes[lastGrow] += (freeSpace - distributed); // remainder
        }

        // Justify content along the main axis when nothing grew.
        int startOffset = 0;
        int extraBetween = 0;
        if (totalGrow == 0 && freeSpace > 0) {
            String justify = Props.getString(n.props, "justifyContent", "start");
            switch (justify.toLowerCase()) {
                case "end":
                    startOffset = freeSpace;
                    break;
                case "center":
                    startOffset = freeSpace / 2;
                    break;
                case "space-between":
                    if (count > 1) extraBetween = freeSpace / (count - 1);
                    break;
                default: // "start"
                    break;
            }
        }

        String align = Props.getString(n.props, "alignItems", "start").toLowerCase();
        int pos = startOffset;
        for (int i = 0; i < count; i++) {
            TuiNode c = n.children.get(i);
            int crossOffset;
            switch (align) {
                case "center":
                    crossOffset = Math.max(0, (crossExtent - crossSizes[i]) / 2);
                    break;
                case "end":
                    crossOffset = Math.max(0, crossExtent - crossSizes[i]);
                    break;
                default: // "start"
                    crossOffset = 0;
                    break;
            }

            int childX, childY, childW, childH;
            if (column) {
                childX = content.x + crossOffset;
                childY = content.y + pos;
                childW = crossSizes[i];
                childH = mainSizes[i];
            } else {
                childX = content.x + pos;
                childY = content.y + crossOffset;
                childW = mainSizes[i];
                childH = crossSizes[i];
            }
            laid.children.add(layoutInto(c, childX, childY, childW, childH));
            pos += mainSizes[i] + gap + extraBetween;
        }
        return laid;
    }
}
