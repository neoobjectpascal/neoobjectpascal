package com.neoobjectpascal.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless tests for the TerminalInk layout engine and renderer. All rendering targets a
 * {@link DefaultVirtualTerminal} so the suite is deterministic and needs no real TTY.
 */
class LayoutRenderTest {

    private static Map<String, Object> props(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    private static TuiNode vbox(Map<String, Object> p, TuiNode... kids) {
        Map<String, Object> pp = new LinkedHashMap<>(p);
        pp.put("flexDirection", "column");
        return TuiNode.of("Box", pp, new ArrayList<>(Arrays.asList(kids)));
    }

    private static TuiNode hbox(Map<String, Object> p, TuiNode... kids) {
        Map<String, Object> pp = new LinkedHashMap<>(p);
        pp.put("flexDirection", "row");
        return TuiNode.of("Box", pp, new ArrayList<>(Arrays.asList(kids)));
    }

    // ---- LayoutEngine unit tests ----

    @Test
    @DisplayName("VBox with gap stacks children on the column axis")
    void vboxColumnMathWithGap() {
        TuiNode root = vbox(props("gap", 1), TuiNode.text("AB"), TuiNode.text("CDE"));
        LaidOutNode laid = LayoutEngine.layout(root, 80, 24);

        assertEquals(2, laid.children.size());
        LayoutBox first = laid.children.get(0).outer;
        LayoutBox second = laid.children.get(1).outer;

        // "AB" -> width 2, height 1, at origin.
        assertEquals(0, first.x);
        assertEquals(0, first.y);
        assertEquals(2, first.width);
        assertEquals(1, first.height);

        // "CDE" -> width 3, height 1, one row of gap below the first.
        assertEquals(0, second.x);
        assertEquals(2, second.y); // row 0 (first) + 1 gap = row 2
        assertEquals(3, second.width);
        assertEquals(1, second.height);
    }

    @Test
    @DisplayName("HBox with gap places children along the row axis")
    void hboxRowMathWithGap() {
        TuiNode root = hbox(props("gap", 2), TuiNode.text("A"), TuiNode.text("B"));
        LaidOutNode laid = LayoutEngine.layout(root, 80, 24);

        LayoutBox first = laid.children.get(0).outer;
        LayoutBox second = laid.children.get(1).outer;

        assertEquals(0, first.x);
        assertEquals(0, first.y);
        assertEquals(1, first.width);

        // col 0 (width 1) + gap 2 = col 3
        assertEquals(3, second.x);
        assertEquals(0, second.y);
        assertEquals(1, second.width);
    }

    @Test
    @DisplayName("Border and padding push the content area inward by their insets")
    void borderAndPaddingInsets() {
        TuiNode root = vbox(props("border", "round", "padding", 1), TuiNode.text("Hi"));
        LaidOutNode laid = LayoutEngine.layout(root, 80, 24);

        // border(1) + padding(1) = inset 2 on each side.
        assertEquals(2, laid.content.x);
        assertEquals(2, laid.content.y);
        assertEquals(76, laid.content.width);
        assertEquals(20, laid.content.height);

        LayoutBox child = laid.children.get(0).outer;
        assertEquals(2, child.x); // placed at the content origin
        assertEquals(2, child.y);
        assertEquals(2, child.width); // "Hi"
        assertEquals(1, child.height);
    }

    @Test
    @DisplayName("Spacer grows to push siblings apart along the main axis")
    void spacerGrowsInRow() {
        TuiNode spacer = TuiNode.of("Spacer", props(), new ArrayList<>());
        TuiNode root = hbox(props(), TuiNode.text("L"), spacer, TuiNode.text("R"));
        LaidOutNode laid = LayoutEngine.layout(root, 40, 3);

        LayoutBox left = laid.children.get(0).outer;
        LayoutBox right = laid.children.get(2).outer;

        assertEquals(0, left.x);
        // Spacer eats the leftover so the right label sits flush at the far edge.
        assertEquals(39, right.x); // width 40 -> last column index 39
        assertEquals(1, right.width);
    }

    // ---- Renderer test over a virtual terminal ----

    @Test
    @DisplayName("Renderer paints text inside a rounded border at the expected cells")
    void rendersTextInsideBorder() throws Exception {
        int cols = 20;
        int rows = 10;
        DefaultVirtualTerminal vt = new DefaultVirtualTerminal(new TerminalSize(cols, rows));
        TerminalScreen screen = new TerminalScreen(vt);
        screen.startScreen();
        try {
            TuiNode root = vbox(props("border", "round"), TuiNode.text("Hello"));
            LaidOutNode laid = LayoutEngine.layout(root, cols, rows);
            Renderer.render(screen.newTextGraphics(), laid, new TerminalSize(cols, rows));
            screen.refresh();

            // Text sits at content origin (1,1) after the 1-cell border.
            String hello = readString(vt, 1, 1, 5);
            assertEquals("Hello", hello);

            // Rounded border corners at the outer box corners.
            assertEquals("╭", charAt(vt, 0, 0));          // top-left  ╭
            assertEquals("╮", charAt(vt, cols - 1, 0));   // top-right ╮
            assertEquals("╰", charAt(vt, 0, rows - 1));   // bot-left  ╰
            assertEquals("╯", charAt(vt, cols - 1, rows - 1)); // bot-right ╯

            // A horizontal edge cell between the corners.
            assertEquals("─", charAt(vt, 5, 0));          // ─
        } finally {
            screen.stopScreen();
        }
    }

    @Test
    @DisplayName("Renderer clips text that overflows the screen width")
    void clipsOverflowingText() throws Exception {
        int cols = 4;
        int rows = 3;
        DefaultVirtualTerminal vt = new DefaultVirtualTerminal(new TerminalSize(cols, rows));
        TerminalScreen screen = new TerminalScreen(vt);
        screen.startScreen();
        try {
            TuiNode root = TuiNode.of("Box", props(), new ArrayList<>(
                    Arrays.asList(TuiNode.text("ABCDEFGH"))));
            LaidOutNode laid = LayoutEngine.layout(root, cols, rows);
            Renderer.render(screen.newTextGraphics(), laid, new TerminalSize(cols, rows));
            screen.refresh();
            // Only the first 4 chars fit; nothing was drawn out of bounds (no exception).
            assertEquals("ABCD", readString(vt, 0, 0, 4));
            assertTrue(true);
        } finally {
            screen.stopScreen();
        }
    }

    private static String charAt(DefaultVirtualTerminal vt, int col, int row) {
        return vt.getCharacter(col, row).getCharacterString();
    }

    private static String readString(DefaultVirtualTerminal vt, int col, int row, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(charAt(vt, col + i, row));
        }
        return sb.toString();
    }
}
