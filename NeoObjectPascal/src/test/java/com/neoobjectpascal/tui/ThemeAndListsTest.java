package com.neoobjectpascal.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless tests for theming ({@code defaultTheme}/{@code extendTheme}/{@code setTheme}/
 * {@code ThemeProvider}) and the list widgets ({@code Item}/{@code UnorderedList}/
 * {@code OrderedList}). The global active theme is reset around every test so the shared
 * static state never leaks between tests or classes.
 */
class ThemeAndListsTest {

    @BeforeEach
    void resetBefore() {
        Theme.reset();
    }

    @AfterEach
    void resetAfter() {
        Theme.reset();
    }

    private static Map<String, Object> props(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    private static List<Object> args(Object... a) {
        return new ArrayList<>(Arrays.asList(a));
    }

    private static String charAt(DefaultVirtualTerminal vt, int col, int row) {
        return vt.getCharacter(col, row).getCharacterString();
    }

    // ---- Theme basics -----------------------------------------------------------

    @Test
    @DisplayName("defaultTheme exposes the hardcoded defaults as data")
    void defaultThemeShape() {
        Map<String, Object> t = Theme.defaultTheme();
        assertEquals("blue", ((Map<?, ?>) t.get("Spinner")).get("color"));
        assertEquals("cyan", ((Map<?, ?>) t.get("Select")).get("pointerColor"));
        assertEquals("magenta", ((Map<?, ?>) t.get("Badge")).get("color"));
    }

    @Test
    @DisplayName("extendTheme deep-merges overrides while preserving untouched leaves and the base")
    @SuppressWarnings("unchecked")
    void extendThemeDeepMerges() {
        Map<String, Object> base = Theme.defaultTheme();
        Map<String, Object> override = props("Spinner", props("color", "red"));

        Map<String, Object> merged =
                (Map<String, Object>) Theme.extendThemeFn().call(args(base, override), null);

        assertEquals("red", ((Map<String, Object>) merged.get("Spinner")).get("color"));
        // Untouched component survives the merge.
        assertEquals("cyan", ((Map<String, Object>) merged.get("Select")).get("pointerColor"));
        // The base record is not mutated.
        assertEquals("blue", ((Map<String, Object>) base.get("Spinner")).get("color"));
    }

    @Test
    @DisplayName("setTheme changes a component's rendered color (Spinner)")
    @SuppressWarnings("unchecked")
    void setThemeChangesSpinnerColor() {
        Map<String, Object> theme = (Map<String, Object>) Theme.extendThemeFn()
                .call(args(Theme.defaultTheme(), props("Spinner", props("color", "red"))), null);
        Theme.setThemeFn().call(args(theme), null);

        TuiNode spinner = (TuiNode) FeedbackWidgets.spinner().call(args(props()), null);
        assertEquals("red", Props.getString(spinner.children.get(0).props, "color", ""));
    }

    @Test
    @DisplayName("ThemeProvider sets the active theme and returns children wrapped in a Box")
    @SuppressWarnings("unchecked")
    void themeProviderAppliesAndWraps() {
        Map<String, Object> theme = (Map<String, Object>) Theme.extendThemeFn()
                .call(args(Theme.defaultTheme(), props("Badge", props("color", "cyan"))), null);

        Object result = Theme.themeProviderFn().call(args(props("theme", theme), "hello"), null);

        // Side effect: active theme now reflects the override.
        assertEquals("cyan", Theme.getString("Badge", "color", "x"));
        // Passthrough: a Box wrapping the child content.
        TuiNode box = (TuiNode) result;
        assertEquals("Box", box.type);
        assertEquals(1, box.children.size());
        assertEquals("hello", box.children.get(0).textContent());
    }

    @Test
    @DisplayName("A prop still overrides the active theme (Badge color)")
    @SuppressWarnings("unchecked")
    void propOverridesTheme() {
        Map<String, Object> theme = (Map<String, Object>) Theme.extendThemeFn()
                .call(args(Theme.defaultTheme(), props("Badge", props("color", "cyan"))), null);
        Theme.setActive(theme);

        TuiNode badge = (TuiNode) FeedbackWidgets.badge().call(args(props("color", "green"), "ok"), null);
        assertEquals("green", Props.getString(badge.props, "backgroundColor", ""));
    }

    @Test
    @DisplayName("ProgressBar honours themed glyphs")
    @SuppressWarnings("unchecked")
    void progressBarThemedGlyphs() throws Exception {
        Map<String, Object> theme = (Map<String, Object>) Theme.extendThemeFn()
                .call(args(Theme.defaultTheme(), props("ProgressBar", props("complete", "#", "remaining", "-"))), null);
        Theme.setActive(theme);

        int cols = 4;
        int rows = 1;
        DefaultVirtualTerminal vt = new DefaultVirtualTerminal(new TerminalSize(cols, rows));
        TerminalScreen screen = new TerminalScreen(vt);
        screen.startScreen();
        try {
            TuiNode bar = (TuiNode) FeedbackWidgets.progressBar().call(args(props("value", 100)), null);
            TuiNode root = TuiNode.of("Box", props("flexDirection", "row"),
                    new ArrayList<>(Arrays.asList(bar)));
            LaidOutNode laid = LayoutEngine.layout(root, cols, rows);
            Renderer.render(screen.newTextGraphics(), laid, new TerminalSize(cols, rows));
            screen.refresh();
            for (int i = 0; i < cols; i++) {
                assertEquals("#", charAt(vt, i, 0));
            }
        } finally {
            screen.stopScreen();
        }
    }

    @Test
    @DisplayName("A themed value differs from the default (sanity)")
    void themeActuallyChanges() {
        String before = Theme.getString("Spinner", "color", "x");
        Theme.setActive(Theme.deepMerge(Theme.defaultTheme(), props("Spinner", props("color", "red"))));
        assertNotEquals(before, Theme.getString("Spinner", "color", "x"));
    }

    // ---- Lists ------------------------------------------------------------------

    private static TuiNode item(Object... content) {
        List<Object> a = new ArrayList<>();
        a.addAll(Arrays.asList(content));
        return (TuiNode) ListWidgets.item().call(a, null);
    }

    @Test
    @DisplayName("Item produces a ListItem node carrying its content and no marker")
    void itemNode() {
        TuiNode it = item("First");
        assertEquals("ListItem", it.type);
        assertEquals(1, it.children.size());
        assertEquals("First", it.children.get(0).textContent());
    }

    @Test
    @DisplayName("UnorderedList prefixes each Item with a dim marker glyph")
    void unorderedListMarkers() {
        TuiNode ul = (TuiNode) ListWidgets.unorderedList()
                .call(args(item("First"), item("Second")), null);
        assertEquals("Box", ul.type);
        assertEquals(2, ul.children.size());

        TuiNode row0 = ul.children.get(0);
        TuiNode marker = row0.children.get(0);
        assertEquals("─", marker.textContent());
        assertTrue(Props.getBool(marker.props, "dim", false));
        assertEquals("First", row0.children.get(1).textContent());
    }

    @Test
    @DisplayName("UnorderedList honours a themed marker")
    @SuppressWarnings("unchecked")
    void unorderedListThemedMarker() {
        Map<String, Object> theme = (Map<String, Object>) Theme.extendThemeFn()
                .call(args(Theme.defaultTheme(), props("UnorderedList", props("marker", "*"))), null);
        Theme.setActive(theme);

        TuiNode ul = (TuiNode) ListWidgets.unorderedList().call(args(item("x")), null);
        assertEquals("*", ul.children.get(0).children.get(0).textContent());
    }

    @Test
    @DisplayName("OrderedList numbers Items incrementally")
    void orderedListNumbers() {
        TuiNode ol = (TuiNode) ListWidgets.orderedList()
                .call(args(item("A"), item("B"), item("C")), null);
        assertEquals("1.", ol.children.get(0).children.get(0).textContent());
        assertEquals("2.", ol.children.get(1).children.get(0).textContent());
        assertEquals("3.", ol.children.get(2).children.get(0).textContent());
        assertEquals("A", ol.children.get(0).children.get(1).textContent());
    }

    @Test
    @DisplayName("A non-Item child renders as-is inside a list")
    void nonItemChildPassesThrough() {
        TuiNode plain = TuiNode.text("plain");
        TuiNode ul = (TuiNode) ListWidgets.unorderedList().call(args(item("bulleted"), plain), null);
        // Second child is the plain text node, unwrapped (no marker row).
        assertTrue(ul.children.get(1).isText());
        assertEquals("plain", ul.children.get(1).textContent());
    }
}
