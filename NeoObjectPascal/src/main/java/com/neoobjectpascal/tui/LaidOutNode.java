package com.neoobjectpascal.tui;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TuiNode} annotated with absolute layout geometry, produced by
 * {@link LayoutEngine} and consumed by {@link Renderer}.
 *
 * <ul>
 *   <li>{@link #outer} — the full box the node occupies (border + padding + content).</li>
 *   <li>{@link #content} — the inner area after border/padding insets, where children
 *       and text are drawn.</li>
 *   <li>{@link #children} — laid-out child subtrees (empty for leaves).</li>
 * </ul>
 */
public final class LaidOutNode {

    public final TuiNode node;
    public final LayoutBox outer;
    public final LayoutBox content;
    public final List<LaidOutNode> children;

    public LaidOutNode(TuiNode node, LayoutBox outer, LayoutBox content) {
        this.node = node;
        this.outer = outer;
        this.content = content;
        this.children = new ArrayList<>();
    }
}
