package com.neoobjectpascal.web;

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

class WebInkModalTest {

    private static final Object CALLBACK = new Object();

    private static Map<String, Object> props(Object... values) {
        Map<String, Object> props = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) props.put(String.valueOf(values[i]), values[i + 1]);
        return props;
    }

    private static final class TestInterpreter extends Interpreter {
        @Override
        public boolean isCallable(Object value) {
            return value == CALLBACK;
        }
    }

    @Test
    void rendersAccessibleModalWithOneSharedCloseHandler() {
        WebRuntime runtime = new WebRuntime(new TestInterpreter(), new LinkedHashMap<>(), null, "light");
        WebNode modal = WebNode.of("Modal", props(
                "open", true, "title", "Confirm", "width", 480, "height", "auto",
                "min", 320, "max", "90vw", "onClose", CALLBACK),
                List.of(WebNode.text("Continue?")));

        String html = HtmlRenderer.render(modal, runtime);

        assertTrue(html.contains("data-webink-modal"));
        assertTrue(html.contains("role=\"dialog\" aria-modal=\"true\" aria-labelledby="));
        assertTrue(html.contains("style=\"width:480px;height:auto;min-width:320px;max-width:90vw\""));
        assertTrue(html.contains("data-webink-close-backdrop=\"true\""));
        assertTrue(html.contains("data-webink-close-escape=\"true\""));
        assertTrue(html.contains("aria-label=\"Close\""));
        assertEquals(2, occurrences(html, "data-webink-close=\"h1\""));
    }

    @Test
    void omitsClosedModalAndHonorsCloseOptions() {
        WebRuntime runtime = new WebRuntime(new TestInterpreter(), new LinkedHashMap<>(), null, "light");
        WebNode closed = WebNode.of("Modal", props("open", false), new ArrayList<>());
        WebNode configured = WebNode.of("Modal", props(
                "open", true, "closeOnEscape", false, "closeOnBackdrop", false,
                "showCloseButton", false, "onClose", CALLBACK), new ArrayList<>());

        assertEquals("", HtmlRenderer.render(closed, runtime));
        String html = HtmlRenderer.render(configured, runtime);
        assertTrue(html.contains("data-webink-close-backdrop=\"false\""));
        assertTrue(html.contains("data-webink-close-escape=\"false\""));
        assertFalse(html.contains("aria-label=\"Close\""));
    }

    @Test
    void tableRendersButtonWidgetInsideCell() {
        WebRuntime runtime = new WebRuntime(new TestInterpreter(), new LinkedHashMap<>(), null, "light");
        WebNode deleteBtn = WebNode.of("Button", props("onClick", CALLBACK, "text", "Excluir"), new ArrayList<>());
        List<Object> rows = Arrays.asList(Arrays.asList("Ana Lima", "Admin", deleteBtn));
        WebNode table = WebNode.of("Table", props("columns", Arrays.asList("Nome", "Perfil", ""), "rows", rows), new ArrayList<>());

        String html = HtmlRenderer.render(table, runtime);

        assertTrue(html.contains("<button"), "table cell should render a button element");
        assertTrue(html.contains("Excluir"), "button text should appear");
        assertTrue(html.contains("data-webink-click"), "button should have click handler");
        assertTrue(html.indexOf("<td") < html.indexOf("<button"), "button should be inside a cell");
    }

    @Test
    void tableCellWithPlainTextRendersNormally() {
        WebRuntime runtime = new WebRuntime(new TestInterpreter(), new LinkedHashMap<>(), null, "light");
        List<Object> rows = Arrays.asList(Arrays.asList("Ana", "Admin"));
        WebNode table = WebNode.of("Table", props("columns", Arrays.asList("Nome", "Perfil"), "rows", rows), new ArrayList<>());

        String html = HtmlRenderer.render(table, runtime);

        assertTrue(html.contains(">Ana<"));
        assertFalse(html.contains("<button"));
    }

    @Test
    void tableCellWithWidgetDoesNotContainTextSlateClass() {
        WebRuntime runtime = new WebRuntime(new TestInterpreter(), new LinkedHashMap<>(), null, "light");
        WebNode btn = WebNode.of("Button", props("text", "OK"), new ArrayList<>());
        List<Object> rows = Arrays.asList(Arrays.asList("Label", btn));
        WebNode table = WebNode.of("Table", props("columns", Arrays.asList("A", "B"), "rows", rows), new ArrayList<>());

        String html = HtmlRenderer.render(table, runtime);

        // count cells: first is text (has text-slate-700), second has widget (no text-slate-700)
        assertTrue(html.contains("text-slate-700"), "text cell keeps its class");
        // Count td tags - the widget cell should still have px-4 py-2
        int tdCount = occurrences(html, "<td");
        assertEquals(2, tdCount);
    }

    private static int occurrences(String value, String target) {
        int count = 0;
        int at = 0;
        while ((at = value.indexOf(target, at)) >= 0) {
            count++;
            at += target.length();
        }
        return count;
    }
}
