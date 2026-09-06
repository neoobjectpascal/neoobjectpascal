package com.neoobjectpascal.tui;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless unit tests for the phase-2 interactive input variants: PasswordInput,
 * EmailInput, ConfirmInput and MultiSelect. State handlers are driven directly with
 * synthetic key strokes; expansion is asserted on the primitive tree.
 */
class InteractiveVariantsTest {

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

    private static final Object CALLBACK = new Object();

    /** Interpreter stub: treats CALLBACK as callable and records the last call. */
    private static final class CapturingInterp extends Interpreter {
        Object lastValue;
        int calls;
        int confirmCalls;
        int cancelCalls;
        Object lastCallback;

        @Override
        public boolean isCallable(Object value) {
            return value == CALLBACK;
        }

        @Override
        public Object callCallback(Object fnValue, List<Object> args) {
            calls++;
            lastCallback = fnValue;
            lastValue = args.isEmpty() ? null : args.get(0);
            return null;
        }
    }

    // ---- PasswordInput ----------------------------------------------------------

    @Test
    @DisplayName("PasswordInput masks the value with asterisks")
    void passwordMasks() {
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("secret");
        TuiNode node = InteractiveWidgets.expandPasswordInput(st, props(), false);
        assertTrue(node.isText());
        assertEquals("******", node.textContent());
    }

    @Test
    @DisplayName("PasswordInput focused shows an inverse cursor over asterisks")
    void passwordFocusedCursor() {
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("ab");
        TuiNode node = InteractiveWidgets.expandPasswordInput(st, props(), true);
        assertEquals("Box", node.type);
        TuiNode last = node.children.get(node.children.size() - 1);
        assertTrue(Props.getBool(last.props, "inverse", false));
    }

    @Test
    @DisplayName("PasswordInput empty + unfocused shows a dim placeholder")
    void passwordPlaceholder() {
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("");
        TuiNode node = InteractiveWidgets.expandPasswordInput(st, props("placeholder", "pw"), false);
        assertEquals("pw", node.textContent());
        assertTrue(Props.getBool(node.props, "dim", false));
    }

    // ---- EmailInput -------------------------------------------------------------

    @Test
    @DisplayName("EmailInput suggests the remaining domain tail for a partial domain")
    void emailSuggestionTail() {
        String tail = InteractiveWidgets.emailSuggestionTail("a@gm", InteractiveWidgets.DEFAULT_DOMAINS);
        assertEquals("ail.com", tail);
    }

    @Test
    @DisplayName("EmailInput has no suggestion before an '@' is typed")
    void emailNoSuggestionBeforeAt() {
        assertEquals("", InteractiveWidgets.emailSuggestionTail("abc", InteractiveWidgets.DEFAULT_DOMAINS));
    }

    @Test
    @DisplayName("EmailInput Enter autocompletes the domain then submits the completed value")
    void emailEnterCompletesAndSubmits() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onSubmit", CALLBACK, "onChange", CALLBACK);
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("a@gm");

        assertTrue(InteractiveWidgets.handleEmailInputKey(
                st, p, InteractiveWidgets.DEFAULT_DOMAINS, interp, type(KeyType.Enter)));
        assertEquals("a@gmail.com", st.value);
        // Last call is onSubmit with the completed value.
        assertEquals("a@gmail.com", interp.lastValue);
    }

    @Test
    @DisplayName("EmailInput blocks typing a second '@'")
    void emailBlocksSecondAt() {
        Interpreter interp = new CapturingInterp();
        Map<String, Object> p = props();
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("a@b");

        assertTrue(InteractiveWidgets.handleEmailInputKey(
                st, p, InteractiveWidgets.DEFAULT_DOMAINS, interp, ch('@')));
        assertEquals("a@b", st.value); // unchanged
    }

    @Test
    @DisplayName("EmailInput inserts ordinary characters and fires onChange")
    void emailInsertsChars() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onChange", CALLBACK);
        InteractiveWidgets.TextInputState st = new InteractiveWidgets.TextInputState("a@g");

        InteractiveWidgets.handleEmailInputKey(st, p, InteractiveWidgets.DEFAULT_DOMAINS, interp, ch('m'));
        assertEquals("a@gm", st.value);
        assertEquals("a@gm", interp.lastValue);
    }

    // ---- ConfirmInput -----------------------------------------------------------

    @Test
    @DisplayName("ConfirmInput 'y' fires onConfirm and 'n' fires onCancel")
    void confirmYesNo() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onConfirm", CALLBACK, "onCancel", CALLBACK);

        assertTrue(InteractiveWidgets.handleConfirmInputKey(p, interp, ch('y')));
        assertTrue(InteractiveWidgets.handleConfirmInputKey(p, interp, ch('n')));
        assertEquals(2, interp.calls); // both callbacks fired, no args
    }

    @Test
    @DisplayName("ConfirmInput Enter uses the default choice (confirm by default)")
    void confirmEnterDefaultConfirm() {
        Object confirmFn = new Object();
        Object cancelFn = new Object();
        final Object[] fired = {null};
        Interpreter interp = new Interpreter() {
            @Override public boolean isCallable(Object v) { return v == confirmFn || v == cancelFn; }
            @Override public Object callCallback(Object fn, List<Object> args) { fired[0] = fn; return null; }
        };
        Map<String, Object> p = props("onConfirm", confirmFn, "onCancel", cancelFn);

        InteractiveWidgets.handleConfirmInputKey(p, interp, type(KeyType.Enter));
        assertEquals(confirmFn, fired[0]);
    }

    @Test
    @DisplayName("ConfirmInput Enter respects defaultChoice=cancel")
    void confirmEnterDefaultCancel() {
        Object confirmFn = new Object();
        Object cancelFn = new Object();
        final Object[] fired = {null};
        Interpreter interp = new Interpreter() {
            @Override public boolean isCallable(Object v) { return v == confirmFn || v == cancelFn; }
            @Override public Object callCallback(Object fn, List<Object> args) { fired[0] = fn; return null; }
        };
        Map<String, Object> p = props("defaultChoice", "cancel", "onConfirm", confirmFn, "onCancel", cancelFn);

        InteractiveWidgets.handleConfirmInputKey(p, interp, type(KeyType.Enter));
        assertEquals(cancelFn, fired[0]);
    }

    @Test
    @DisplayName("ConfirmInput expansion reflects the default choice hint")
    void confirmExpansion() {
        assertEquals("(Y/n)", InteractiveWidgets.expandConfirmInput(props()).textContent());
        assertEquals("(y/N)", InteractiveWidgets.expandConfirmInput(props("defaultChoice", "cancel")).textContent());
    }

    // ---- MultiSelect ------------------------------------------------------------

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
    @DisplayName("MultiSelect Space toggles the focused option and fires onChange with the list")
    void multiSelectSpaceToggles() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onChange", CALLBACK);
        List<Map<String, Object>> opts = options();
        InteractiveWidgets.MultiSelectState st = new InteractiveWidgets.MultiSelectState();

        InteractiveWidgets.handleMultiSelectKey(st, opts, p, interp, type(KeyType.ArrowDown)); // focus 1
        assertTrue(InteractiveWidgets.handleMultiSelectKey(st, opts, p, interp, ch(' ')));       // toggle on
        assertTrue(st.selected.contains(2));
        assertTrue(interp.lastValue instanceof List);
        assertEquals(Arrays.asList(2), interp.lastValue);

        InteractiveWidgets.handleMultiSelectKey(st, opts, p, interp, ch(' ')); // toggle off
        assertFalse(st.selected.contains(2));
    }

    @Test
    @DisplayName("MultiSelect Down navigation scrolls the window past visibleCount")
    void multiSelectScrolls() {
        Interpreter interp = new CapturingInterp();
        Map<String, Object> p = props("visibleCount", 3);
        List<Map<String, Object>> opts = options();
        InteractiveWidgets.MultiSelectState st = new InteractiveWidgets.MultiSelectState();

        for (int i = 0; i < 3; i++) {
            InteractiveWidgets.handleMultiSelectKey(st, opts, p, interp, type(KeyType.ArrowDown));
        }
        assertEquals(3, st.focused);
        assertEquals(1, st.offset);
    }

    @Test
    @DisplayName("MultiSelect Enter fires onSubmit with the selected list")
    void multiSelectEnterSubmits() {
        CapturingInterp interp = new CapturingInterp();
        Map<String, Object> p = props("onSubmit", CALLBACK);
        List<Map<String, Object>> opts = options();
        InteractiveWidgets.MultiSelectState st = new InteractiveWidgets.MultiSelectState();
        st.selected.add(1);
        st.selected.add(3);

        assertTrue(InteractiveWidgets.handleMultiSelectKey(st, opts, p, interp, type(KeyType.Enter)));
        assertEquals(Arrays.asList(1, 3), interp.lastValue);
    }

    @Test
    @DisplayName("MultiSelect defaultValue seeds the selected set")
    void multiSelectDefaultValue() {
        Map<String, Object> p = props("defaultValue", new ArrayList<>(Arrays.asList(2, 4)));
        InteractiveWidgets.MultiSelectState st = InteractiveWidgets.MultiSelectState.fromProps(p);
        assertTrue(st.selected.contains(2));
        assertTrue(st.selected.contains(4));
    }

    @Test
    @DisplayName("MultiSelect expansion marks selected rows with a green tick")
    void multiSelectExpansion() {
        InteractiveWidgets.MultiSelectState st = new InteractiveWidgets.MultiSelectState();
        st.selected.add(1); // first option selected
        TuiNode node = InteractiveWidgets.expandMultiSelect(st, options(), props("visibleCount", 3));
        TuiNode firstRow = node.children.get(0);
        TuiNode check = firstRow.children.get(1); // [pointer, check, label]
        assertEquals(Glyphs.TICK, check.textContent());
    }
}
