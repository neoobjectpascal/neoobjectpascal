package com.neoobjectpascal.tui;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.neoobjectpascal.Interpreter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModalTest {

    private static Map<String, Object> props(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) result.put(String.valueOf(values[i]), values[i + 1]);
        return result;
    }

    @Test
    void modalFactoryRetainsContractPropsAndChildren() {
        TuiNode modal = (TuiNode) ModalWidgets.modal().call(new ArrayList<>(Arrays.asList(
                props("open", true, "title", "Confirm", "width", 20, "closeOnEscape", false),
                TuiNode.text("Body"))), null);

        assertEquals("Modal", modal.type);
        assertTrue(Props.getBool(modal.props, "open", false));
        assertEquals("Confirm", Props.getString(modal.props, "title", ""));
        assertEquals(20, Props.getInt(modal.props, "width", 0));
        assertFalse(Props.getBool(modal.props, "closeOnEscape", true));
        assertEquals("Body", modal.children.get(0).textContent());
    }

    @Test
    void openModalOverlayIsCenteredAndHonoursBounds() {
        TuiNode modal = new TuiNode("Modal", props("open", true, "title", "Title", "minWidth", 16,
                "maxWidth", 18, "minHeight", 5, "maxHeight", 6),
                new ArrayList<>(Arrays.asList(TuiNode.text("Body"))));

        LaidOutNode laid = LayoutEngine.layout(ModalWidgets.overlay(modal, 40, 20), 40, 20);
        LaidOutNode panel = laid.children.get(0);

        assertEquals(16, panel.outer.width);
        assertEquals(5, panel.outer.height);
        assertEquals(12, panel.outer.x);
        assertEquals(7, panel.outer.y);
        assertEquals("Title", panel.children.get(0).node.textContent());
    }

    @Test
    void focusScopeBlocksBackgroundHandlersAndRestoresPreviousFocus() {
        TuiContext context = new TuiContext(null);
        boolean[] calls = {false, false};
        context.beginFrame();
        context.registerFocusable("background", key -> { calls[0] = true; return true; });
        context.registerFocusable("modal", key -> { calls[1] = true; return true; });
        context.focusByKey("background");

        context.setFocusScope(java.util.Collections.singleton("modal"));
        context.clampFocus();
        assertTrue(context.dispatchToFocused(new KeyStroke(KeyType.Enter)));
        assertFalse(calls[0]);
        assertTrue(calls[1]);

        context.clearFocusScope();
        assertTrue(context.focusByKey("background"));
        assertEquals("background", context.focusedKey());
    }

    @Test
    void escapeInvokesCloseCallbackBeforeItCanQuit() {
        Object callback = new Object();
        int[] calls = {0};
        Interpreter interpreter = new Interpreter() {
            @Override public boolean isCallable(Object value) { return value == callback; }
            @Override public Object callCallback(Object value, List<Object> args) { calls[0]++; return null; }
        };
        TuiNode modal = new TuiNode("Modal", props("open", true, "onClose", callback), new ArrayList<>());

        assertTrue(TerminalRuntime.closeOnEscape(modal, interpreter, new KeyStroke(KeyType.Escape)));
        assertEquals(1, calls[0]);
        assertFalse(TerminalRuntime.closeOnEscape(new TuiNode("Modal", props("open", true,
                "closeOnEscape", false), new ArrayList<>()), interpreter, new KeyStroke(KeyType.Escape)));
    }
}
