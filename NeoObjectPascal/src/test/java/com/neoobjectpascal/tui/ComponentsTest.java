package com.neoobjectpascal.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;

import com.neoobjectpascal.Interpreter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless unit tests for the phase-2 interactive and feedback/display components. State
 * handlers are driven directly with synthetic {@link KeyStroke}s; expansion is asserted on
 * the primitive tree; the progress bar is asserted against a {@link DefaultVirtualTerminal}
 * buffer (the same pattern as {@code LayoutRenderTest}).
 */
class ComponentsTest {

    // ---- helpers ----------------------------------------------------------------

    private static Map<String, Object> props(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    private static KeyStroke ch(char c) {
        return new KeyStroke(c, false, false);
    }

    private static KeyStroke type(KeyType t) {
        return new KeyStroke(t);
    }

    private static String charAt(DefaultVirtualTerminal vt, int col, int row) {
        return vt.getCharacter(col, row).getCharacterString();
    }

    /** Sentinel used as a "callable" prop value; the stub interpreter recognises it. */
    private static final Object CALLBACK = new Object();

    /** Interpreter stub that treats {@link #CALLBACK} as callable and captures its args. */
    private static final class CapturingInterp extends Interpreter {
        Object lastValue;
        int calls;

        @Override
        public boolean isCallable(Object value) {
            return value == CALLBACK;
        }

        @Override
        public Object callCallback(Object fnValue, List<Object> args) {
            calls++;
            lastValue = args.isEmpty() ? null : args.get(0);
            return null;
        }
    }

    // ---- TextInput --------------------------------------------------------------

    @Test
    @DisplayName("TextInput inserts printable chars at the cursor and fires onChange")
    void textInputInsertsAndFiresOnChange() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onChange", CALLBACK);
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("");

        assertTrue(InteractiveWidgets.handleTextInputKey(st, p, interp, ch('h')));
        assertTrue(InteractiveWidgets.handleTextInputKey(st, p, interp, ch('i')));

        assertEquals("hi", st.value);
        assertEquals(2, st.cursor);
        assertEquals(2, interp.calls);
        assertEquals("hi", interp.lastValue);
    }

    @Test
    @DisplayName("TextInput cursor moves left/right and inserts mid-string")
    void textInputCursorMovementAndMidInsert() {
        Interpreter interp = new CapturingInterp();
        Map<String, Object> p = props();
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("ac");

        // cursor starts at end (2); move left once -> between 'a' and 'c'.
        assertTrue(InteractiveWidgets.handleTextInputKey(st, p, interp, type(KeyType.ArrowLeft)));
        assertEquals(1, st.cursor);
        InteractiveWidgets.handleTextInputKey(st, p, interp, ch('b'));

        assertEquals("abc", st.value);
        assertEquals(2, st.cursor);
    }

    @Test
    @DisplayName("TextInput backspace removes the char before the cursor")
    void textInputBackspace() {
        Interpreter interp = new CapturingInterp();
        Map<String, Object> p = props();
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("abc");

        assertTrue(InteractiveWidgets.handleTextInputKey(st, p, interp, type(KeyType.Backspace)));
        assertEquals("ab", st.value);
        assertEquals(2, st.cursor);
    }

    @Test
    @DisplayName("TextInput Enter fires onSubmit with the current value")
    void textInputEnterFiresOnSubmit() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onSubmit", CALLBACK);
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("done");

        assertTrue(InteractiveWidgets.handleTextInputKey(st, p, interp, type(KeyType.Enter)));
        assertEquals(1, interp.calls);
        assertEquals("done", interp.lastValue);
    }

    @Test
    @DisplayName("TextInput focused expansion shows an inverse cursor block")
    void textInputExpansionCursor() {
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("ab");
        // cursor at end -> before="ab", cursor char is a trailing space rendered inverse.
        TuiNode node = InteractiveWidgets.expandTextInput(st, props(), true);
        assertEquals("Box", node.type);
        TuiNode last = node.children.get(node.children.size() - 1);
        assertTrue(Props.getBool(last.props, "inverse", false));
        assertEquals(" ", last.textContent());
    }

    @Test
    @DisplayName("TextInput empty + unfocused expands to a dim placeholder")
    void textInputPlaceholder() {
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("");
        TuiNode node = InteractiveWidgets.expandTextInput(st, props("placeholder", "name"), false);
        assertTrue(node.isText());
        assertEquals("name", node.textContent());
        assertTrue(Props.getBool(node.props, "dim", false));
    }

    // ---- Select -----------------------------------------------------------------

    private static List<Map<String, Object>> options() {
        return new ArrayList<>(Arrays.asList(
                props("label", "One", "value", 1),
                props("label", "Two", "value", 2),
                props("label", "Three", "value", 3),
                props("label", "Four", "value", 4),
                props("label", "Five", "value", 5),
                props("label", "Six", "value", 6)));
    }

    @Test
    @DisplayName("Select Down navigation scrolls the window past visibleCount")
    void selectDownScrollsWindow() {
        Interpreter interp = new CapturingInterp();
        Map<String, Object> p = props("visibleCount", 3);
        List<Map<String, Object>> opts = options();
        InteractiveWidgets.SelectState st = new InteractiveWidgets.SelectState();

        // Move down 3 times: focus 0->3, window offset should follow to keep it visible.
        for (int i = 0; i < 3; i++) {
            assertTrue(InteractiveWidgets.handleSelectKey(st, opts, p, interp, type(KeyType.ArrowDown)));
        }
        assertEquals(3, st.focused);
        assertEquals(1, st.offset); // focused(3) - visible(3) + 1 = 1
    }

    @Test
    @DisplayName("Select Up navigation scrolls the window back up")
    void selectUpScrollsWindow() {
        Interpreter interp = new CapturingInterp();
        Map<String, Object> p = props("visibleCount", 3);
        List<Map<String, Object>> opts = options();
        InteractiveWidgets.SelectState st = new InteractiveWidgets.SelectState();
        st.focused = 3;
        st.offset = 1;

        InteractiveWidgets.handleSelectKey(st, opts, p, interp, type(KeyType.ArrowUp)); // ->2 (still visible)
        assertEquals(2, st.focused);
        assertEquals(1, st.offset);
        InteractiveWidgets.handleSelectKey(st, opts, p, interp, type(KeyType.ArrowUp)); // ->1 (still visible)
        InteractiveWidgets.handleSelectKey(st, opts, p, interp, type(KeyType.ArrowUp)); // ->0, scroll up
        assertEquals(0, st.focused);
        assertEquals(0, st.offset);
    }

    @Test
    @DisplayName("Select Enter selects the focused option and fires onChange with its value")
    void selectEnterSelectsAndFires() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onChange", CALLBACK);
        List<Map<String, Object>> opts = options();
        InteractiveWidgets.SelectState st = new InteractiveWidgets.SelectState();

        InteractiveWidgets.handleSelectKey(st, opts, p, interp, type(KeyType.ArrowDown)); // focus 1
        assertTrue(InteractiveWidgets.handleSelectKey(st, opts, p, interp, type(KeyType.Enter)));

        assertTrue(st.hasSelection);
        assertEquals(2, st.selectedValue);
        assertEquals(1, interp.calls);
        assertEquals(2, interp.lastValue);
    }

    @Test
    @DisplayName("Select ignores unrelated keys")
    void selectIgnoresOtherKeys() {
        Interpreter interp = new CapturingInterp();
        InteractiveWidgets.SelectState st = new InteractiveWidgets.SelectState();
        assertFalse(InteractiveWidgets.handleSelectKey(st, options(), props(), interp, ch('x')));
    }

    @Test
    @DisplayName("Select expansion marks the focused row with a cyan pointer")
    void selectExpansionPointer() {
        InteractiveWidgets.SelectState st = new InteractiveWidgets.SelectState();
        st.focused = 1;
        TuiNode node = InteractiveWidgets.expandSelect(st, options(), props("visibleCount", 5));
        assertEquals("Box", node.type);
        TuiNode row = node.children.get(1); // second visible row is the focused one
        TuiNode pointer = row.children.get(0);
        assertEquals(Glyphs.POINTER, pointer.textContent());
        assertEquals("cyan", Props.getString(pointer.props, "color", ""));
    }

    // ---- Badge / StatusMessage / Alert / Spinner --------------------------------

    @Test
    @DisplayName("Badge uppercases and pads, with color bg and black fg")
    void badgeExpansion() {
        TuiNode node = (TuiNode) FeedbackWidgets.badge()
                .call(new ArrayList<>(Arrays.asList(props("color", "green"), "ok")), null);
        assertTrue(node.isText());
        assertEquals(" OK ", node.textContent());
        assertEquals("green", Props.getString(node.props, "backgroundColor", ""));
        assertEquals("black", Props.getString(node.props, "color", ""));
    }

    @Test
    @DisplayName("StatusMessage success expands to an HBox led by a green tick")
    void statusMessageExpansion() {
        TuiNode node = (TuiNode) FeedbackWidgets.statusMessage()
                .call(new ArrayList<>(Arrays.asList(props("variant", "success"), "done")), null);
        assertEquals("Box", node.type);
        TuiNode icon = node.children.get(0);
        assertEquals(Glyphs.TICK, icon.textContent());
        assertEquals("green", Props.getString(icon.props, "color", ""));
        assertEquals("done", node.children.get(1).textContent());
    }

    @Test
    @DisplayName("Alert error expands to a rounded bordered box with a bold title")
    void alertExpansion() {
        TuiNode node = (TuiNode) FeedbackWidgets.alert()
                .call(new ArrayList<>(Arrays.asList(props("variant", "error", "title", "Erro"), "x")), null);
        assertEquals("Box", node.type);
        assertEquals("round", Props.getString(node.props, "border", ""));
        assertEquals("red", Props.getString(node.props, "borderColor", ""));

        TuiNode icon = node.children.get(0);
        assertEquals(Glyphs.CROSS, icon.textContent());

        TuiNode body = node.children.get(1); // VBox with title + message
        TuiNode title = body.children.get(0);
        assertEquals("Erro", title.textContent());
        assertTrue(Props.getBool(title.props, "bold", false));
    }

    @Test
    @DisplayName("Alert without a title has just the message line")
    void alertWithoutTitle() {
        TuiNode node = (TuiNode) FeedbackWidgets.alert()
                .call(new ArrayList<>(Arrays.asList(props("variant", "info"), "hello")), null);
        TuiNode body = node.children.get(1);
        assertEquals(1, body.children.size());
        assertEquals("hello", body.children.get(0).textContent());
    }

    @Test
    @DisplayName("Spinner expands to an HBox led by a dots-set frame char")
    void spinnerExpansion() {
        TuiNode node = (TuiNode) FeedbackWidgets.spinner()
                .call(new ArrayList<>(Arrays.asList(props("label", "loading"))), null);
        assertEquals("Box", node.type);
        String frame = node.children.get(0).textContent();
        assertTrue(Arrays.asList(Glyphs.SPINNER_DOTS).contains(frame),
                "spinner frame should come from the dots set, got: " + frame);
        assertEquals("loading", node.children.get(1).textContent());
    }

    @Test
    @DisplayName("Spinner frame advances with time")
    void spinnerFrameByTime() {
        assertEquals(Glyphs.SPINNER_DOTS[0], FeedbackWidgets.spinnerFrame(0));
        assertEquals(Glyphs.SPINNER_DOTS[1], FeedbackWidgets.spinnerFrame(80));
        assertEquals(Glyphs.SPINNER_DOTS[2], FeedbackWidgets.spinnerFrame(160));
    }

    // ---- ProgressBar (via layout + virtual terminal) ----------------------------

    @Test
    @DisplayName("ProgressBar fills about half its width with filled squares at value 50")
    void progressBarRendersHalf() throws Exception {
        int cols = 10;
        int rows = 1;
        DefaultVirtualTerminal vt = new DefaultVirtualTerminal(new TerminalSize(cols, rows));
        TerminalScreen screen = new TerminalScreen(vt);
        screen.startScreen();
        try {
            TuiNode bar = (TuiNode) FeedbackWidgets.progressBar()
                    .call(new ArrayList<>(Arrays.asList(props("value", 50))), null);
            // Wrap in a row Box so the bar's flexGrow stretches to the full width.
            TuiNode root = TuiNode.of("Box", props("flexDirection", "row"),
                    new ArrayList<>(Arrays.asList(bar)));

            LaidOutNode laid = LayoutEngine.layout(root, cols, rows);
            Renderer.render(screen.newTextGraphics(), laid, new TerminalSize(cols, rows));
            screen.refresh();

            // width 10, value 50 -> 5 filled squares, then 5 light squares.
            for (int i = 0; i < 5; i++) {
                assertEquals(Glyphs.SQUARE, charAt(vt, i, 0), "cell " + i + " should be filled");
            }
            for (int i = 5; i < 10; i++) {
                assertEquals(Glyphs.LIGHT_SQUARE, charAt(vt, i, 0), "cell " + i + " should be empty");
            }
        } finally {
            screen.stopScreen();
        }
    }

    @Test
    @DisplayName("ProgressBar clamps out-of-range values")
    void progressBarClampsValue() throws Exception {
        int cols = 4;
        int rows = 1;
        DefaultVirtualTerminal vt = new DefaultVirtualTerminal(new TerminalSize(cols, rows));
        TerminalScreen screen = new TerminalScreen(vt);
        screen.startScreen();
        try {
            TuiNode bar = (TuiNode) FeedbackWidgets.progressBar()
                    .call(new ArrayList<>(Arrays.asList(props("value", 999))), null);
            TuiNode root = TuiNode.of("Box", props("flexDirection", "row"),
                    new ArrayList<>(Arrays.asList(bar)));
            LaidOutNode laid = LayoutEngine.layout(root, cols, rows);
            Renderer.render(screen.newTextGraphics(), laid, new TerminalSize(cols, rows));
            screen.refresh();
            for (int i = 0; i < cols; i++) {
                assertEquals(Glyphs.SQUARE, charAt(vt, i, 0));
            }
        } finally {
            screen.stopScreen();
        }
    }

    // ---- TuiContext keying + focus ----------------------------------------------

    @Test
    @DisplayName("TuiContext auto-keys widgets deterministically per type per frame")
    void contextAutoKeys() {
        TuiContext ctx = new TuiContext(null);
        assertEquals("TextInput#0", ctx.keyFor("TextInput", props()));
        assertEquals("TextInput#1", ctx.keyFor("TextInput", props()));
        assertEquals("Select#0", ctx.keyFor("Select", props()));
        assertEquals("explicit", ctx.keyFor("TextInput", props("key", "explicit")));
    }

    @Test
    @DisplayName("TuiContext state survives across frames under the same key")
    void contextStatePersists() {
        TuiContext ctx = new TuiContext(null);
        ctx.beginFrame();
        Object first = ctx.stateFor("k", () -> new InteractiveWidgets.TextInputState("x"));
        ctx.beginFrame(); // new frame resets counters, not the store
        Object second = ctx.stateFor("k", () -> new InteractiveWidgets.TextInputState("y"));
        assertNotNull(first);
        assertTrue(first == second, "same key must return the same state instance");
    }

    @Test
    @DisplayName("TuiContext focus registration and dispatch reach the focused handler")
    void contextFocusDispatch() {
        TuiContext ctx = new TuiContext(null);
        ctx.beginFrame();
        boolean[] hit = {false, false};
        ctx.registerFocusable("a", k -> {
            hit[0] = true;
            return true;
        });
        ctx.registerFocusable("b", k -> {
            hit[1] = true;
            return true;
        });
        ctx.focusIndex = 1;
        ctx.clampFocus();

        assertTrue(ctx.dispatchToFocused(type(KeyType.Enter)));
        assertFalse(hit[0]);
        assertTrue(hit[1]);

        ctx.cycleFocus(1); // wrap from 1 -> 0
        assertEquals(0, ctx.focusIndex);
    }

    @Test
    @DisplayName("TuiContext.current is null outside a build")
    void contextCurrentNullOutsideBuild() {
        assertNull(TuiContext.current());
        assertNotNull(TuiContext.currentOrTransient());
    }
}
