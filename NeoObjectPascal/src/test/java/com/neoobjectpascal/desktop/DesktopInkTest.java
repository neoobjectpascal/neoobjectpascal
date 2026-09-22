package com.neoobjectpascal.desktop;

import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DesktopInkTest {

    @Test
    void nodeKeepsIdentityTextAndChildren() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("key", "welcome");
        DesktopNode node = DesktopNode.of("Card", props, Arrays.asList(DesktopNode.text("Hello")));

        assertEquals("welcome", node.key);
        assertEquals("Hello", node.children.get(0).textContent());
    }

    @Test
    void propsCoerceCommonInterpreterValues() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("width", "640.8");
        props.put("disabled", "yes");

        assertEquals(640, Props.getInt(props, "width", 0));
        assertTrue(Props.getBool(props, "disabled", false));
    }

    @Test
    void semanticThemesProvideDistinctLightAndDarkSurfaces() {
        Theme light = Theme.light();
        Theme dark = Theme.dark();

        assertNotEquals(light.color(Theme.Token.BACKGROUND), dark.color(Theme.Token.BACKGROUND));
        assertNotEquals(light.color(Theme.Token.FOREGROUND), dark.color(Theme.Token.FOREGROUND));
    }

    @Test
    void rendererBuildsSwingComponentsWithoutAWindow() {
        DesktopNode tree = DesktopNode.of("Card", new LinkedHashMap<>(),
                Arrays.asList(DesktopNode.of("Button", new LinkedHashMap<>(), Arrays.asList(DesktopNode.text("Save")))));

        assertEquals(1, ((javax.swing.JPanel) DesktopRenderer.render(tree, Theme.light(), null)).getComponentCount());
    }

    @Test
    void closedModalAndHiddenNodeDoNotParticipateInTheLayout() {
        DesktopNode modal = DesktopNode.of("Modal", new LinkedHashMap<>(), Arrays.asList(DesktopNode.text("Hidden")));
        DesktopNode hidden = DesktopNode.of("Text", java.util.Collections.singletonMap("visible", false),
                Arrays.asList(DesktopNode.text("Hidden")));

        assertFalse(DesktopRenderer.render(modal, Theme.light(), null).isVisible());
        assertFalse(DesktopRenderer.render(hidden, Theme.light(), null).isVisible());
    }

    @Test
    void dataGridCanEnableEditingAndUseASelectEditorPerColumn() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("columns", Arrays.asList("Name", "Status"));
        props.put("rows", Arrays.asList(Arrays.asList("Ana", "Active")));
        props.put("editable", true);
        Map<String, Object> statusEditor = new LinkedHashMap<>();
        statusEditor.put("options", Arrays.asList("Active", "Inactive"));
        props.put("editors", java.util.Collections.singletonMap("Status", statusEditor));

        javax.swing.JScrollPane pane = (javax.swing.JScrollPane) DesktopRenderer.render(
                DesktopNode.of("Table", props, Arrays.asList()), Theme.dark(), null);
        javax.swing.JTable table = (javax.swing.JTable) pane.getViewport().getView();

        assertTrue(table.isCellEditable(0, 0));
        assertTrue(table.getColumnModel().getColumn(1).getCellEditor() instanceof javax.swing.DefaultCellEditor);
    }

    @Test
    void readOnlyDataGridSelectsRowsInsteadOfIndividualCells() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("columns", Arrays.asList("Name"));
        props.put("rows", Arrays.asList(Arrays.asList("Ana")));
        props.put("selectedRow", 0);

        javax.swing.JScrollPane pane = (javax.swing.JScrollPane) DesktopRenderer.render(
                DesktopNode.of("Table", props, Arrays.asList()), Theme.dark(), null);
        javax.swing.JTable table = (javax.swing.JTable) pane.getViewport().getView();

        assertTrue(table.getRowSelectionAllowed());
        assertFalse(table.getColumnSelectionAllowed());
        assertFalse(table.getCellSelectionEnabled());
        assertEquals(0, table.getSelectedRow());
        table.changeSelection(0, 0, false, false);
        assertEquals(0, table.getSelectedRow());
        assertTrue(table.getColumnModel().getSelectionModel().isSelectionEmpty());
    }

    @Test
    void headlessRenderIsSkippedWithoutCreatingAWindow() {
        assumeTrue(GraphicsEnvironment.isHeadless());
        boolean rendered = DesktopRuntime.render(DesktopNode.text("safe"), new LinkedHashMap<>());

        assertFalse(rendered);
    }
}
