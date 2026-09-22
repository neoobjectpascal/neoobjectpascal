package com.neoobjectpascal.desktop;

import com.neoobjectpascal.Interpreter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.Component;

/** Converts the portable node tree to styled native Swing components. */
final class DesktopRenderer {
    private DesktopRenderer() {}

    static JComponent render(DesktopNode node, Theme theme, Interpreter interpreter) {
        if (node == null) return panel(theme, false);
        if (!Props.getBool(node.props, "visible", true)
                || "Modal".equals(node.type) && !Props.getBool(node.props, "open", false)) {
            JPanel hidden = new JPanel();
            hidden.setVisible(false);
            hidden.setPreferredSize(new Dimension(0, 0));
            hidden.setMaximumSize(new Dimension(0, 0));
            return hidden;
        }
        JComponent component;
        switch (node.type) {
            case "Text": component = label(node, theme, Font.PLAIN, 14); break;
            case "Heading": component = label(node, theme, Font.BOLD,
                    Props.getInt(node.props, "level", 1) == 1 ? 24 : 18); break;
            case "Badge": component = badge(node, theme); break;
            case "Button": component = button(node, theme, interpreter); break;
            case "TextInput": component = input(node, theme, false, interpreter); break;
            case "PasswordInput": component = input(node, theme, true, interpreter); break;
            case "TextArea": component = textArea(node, theme, interpreter); break;
            case "Select": component = select(node, theme, interpreter); break;
            case "Checkbox": component = checkbox(node, theme, interpreter); break;
            case "ProgressBar": component = progress(node, theme); break;
            case "Spinner": component = spinner(node, theme); break;
            case "StatCard": component = statCard(node, theme); break;
            case "Divider": component = divider(theme); break;
            case "Spacer": component = spacer(node); break;
            case "Table": component = table(node, theme, interpreter); break;
            case "List": component = list(node, theme); break;
            default: component = container(node, theme, interpreter); break;
        }
        applySize(component, node.props);
        return component;
    }

    private static JComponent container(DesktopNode node, Theme theme, Interpreter interpreter) {
        boolean row = "Row".equals(node.type);
        boolean grid = "Grid".equals(node.type);
        boolean sidebar = "Sidebar".equals(node.type);
        JPanel panel = panel(theme, "Card".equals(node.type) || "Modal".equals(node.type)
                || "StatCard".equals(node.type) || "Alert".equals(node.type));
        if (sidebar) {
            panel.setBackground(theme.color(Theme.Token.MUTED));
            int width = Props.getInt(node.props, "width", 220);
            panel.setPreferredSize(new Dimension(width, 1));
            panel.setMinimumSize(new Dimension(width, 1));
            panel.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        }
        if (grid) panel.setLayout(new GridLayout(0, Math.max(1,
                Props.getInt(node.props, "cols", Props.getInt(node.props, "columns", 2))),
                Props.getInt(node.props, "gap", 12), Props.getInt(node.props, "gap", 12)));
        else if (row) panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        else panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if ("Alert".equals(node.type)) {
            String variant = Props.getString(node.props, "variant", "info");
            panel.setBackground("destructive".equals(variant) || "error".equals(variant)
                    ? theme.color(Theme.Token.DESTRUCTIVE) : theme.color(Theme.Token.MUTED));
        }
        if ("Modal".equals(node.type)) panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.color(Theme.Token.RING), 2), panel.getBorder()));
        String title = "Modal".equals(node.type) ? Props.getString(node.props, "title", "") : "";
        if (!title.isEmpty()) {
            JLabel heading = label(DesktopNode.text(title), theme, Font.BOLD, 20);
            heading.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            panel.add(heading);
            panel.add(javax.swing.Box.createVerticalStrut(12));
        }
        int gap = Props.getInt(node.props, "gap", row ? 16 : 10);
        for (int index = 0; index < node.children.size(); index++) {
            JComponent child = render(node.children.get(index), theme, interpreter);
            if (!row) {
                child.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                if (!(child instanceof JScrollPane)) {
                    int requestedWidth = Props.getInt(node.children.get(index).props, "width", -1);
                    child.setMaximumSize(new Dimension(requestedWidth > 0 ? requestedWidth : Integer.MAX_VALUE,
                            child.getPreferredSize().height));
                }
            }
            panel.add(child);
            if (index + 1 < node.children.size()) {
                panel.add(row ? javax.swing.Box.createHorizontalStrut(gap) : javax.swing.Box.createVerticalStrut(gap));
            }
        }
        return panel;
    }

    private static JPanel panel(Theme theme, boolean card) {
        JPanel panel = card ? new RoundedPanel(theme.color(Theme.Token.CARD), theme.color(Theme.Token.BORDER), 10)
                : new JPanel();
        panel.setOpaque(!card);
        panel.setBackground(card ? theme.color(Theme.Token.CARD) : theme.color(Theme.Token.BACKGROUND));
        panel.setForeground(theme.color(Theme.Token.FOREGROUND));
        panel.setBorder(BorderFactory.createEmptyBorder(card ? 16 : 8, card ? 16 : 8, card ? 16 : 8, card ? 16 : 8));
        return panel;
    }

    private static JLabel label(DesktopNode node, Theme theme, int style, int size) {
        JLabel label = new JLabel(node.textContent());
        label.setForeground(theme.color(Theme.Token.FOREGROUND));
        label.setFont(label.getFont().deriveFont(style, (float) size));
        return label;
    }

    private static JLabel badge(DesktopNode node, Theme theme) {
        JLabel badge = label(node, theme, Font.BOLD, 12);
        badge.setOpaque(true);
        badge.setBackground(theme.color(Theme.Token.SECONDARY));
        badge.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
        return badge;
    }

    private static JButton button(DesktopNode node, Theme theme, Interpreter interpreter) {
        boolean primary = !"secondary".equals(Props.getString(node.props, "variant", "primary"));
        JButton button = new StyledButton(node.textContent(), primary ? theme.color(Theme.Token.PRIMARY)
                : theme.color(Theme.Token.SECONDARY), primary ? theme.color(Theme.Token.PRIMARY_FOREGROUND)
                : theme.color(Theme.Token.FOREGROUND));
        button.setFocusPainted(false);
        button.setEnabled(!Props.getBool(node.props, "disabled", false));
        button.putClientProperty("desktopink.closeModal", Props.getBool(node.props, "closeModal", false));
        Object callback = Props.get(node.props, "onClick");
        if (callback != null && interpreter != null && interpreter.isCallable(callback)) {
            button.addActionListener(event -> {
                interpreter.callCallback(callback, new ArrayList<>());
                DesktopRuntime.refreshActive();
            });
        }
        return button;
    }

    private static JComponent input(DesktopNode node, Theme theme, boolean password, Interpreter interpreter) {
        JTextField input = password ? new RoundedPasswordField(theme) : new RoundedTextField(theme);
        input.setText(Props.getString(node.props, "value", ""));
        input.setCaretPosition(input.getText().length());
        input.putClientProperty("JTextField.placeholderText", Props.getString(node.props, "placeholder", ""));
        input.setPreferredSize(new Dimension(260, 52));
        styleInput(input, theme, node.props);
        if (Props.getBool(node.props, "autoFocus", false)) {
            javax.swing.SwingUtilities.invokeLater(input::requestFocusInWindow);
        }
        bindTextChange(input, node, interpreter);
        return input;
    }

    private static JComponent textArea(DesktopNode node, Theme theme, Interpreter interpreter) {
        JTextArea area = new JTextArea(Props.getInt(node.props, "rows", 4), 20);
        area.setText(Props.getString(node.props, "value", ""));
        area.setCaretPosition(area.getText().length());
        styleInput(area, theme, node.props);
        bindTextChange(area, node, interpreter);
        return scroll(area, theme);
    }

    private static JComponent select(DesktopNode node, Theme theme, Interpreter interpreter) {
        List<Object> choices = options(node.props);
        String value = Props.getString(node.props, "value", choices.isEmpty() ? "" : String.valueOf(choices.get(0)));
        Object callback = Props.get(node.props, "onChange");
        StyledSelect select = new StyledSelect(value, choices, theme, callback, interpreter);
        select.setPreferredSize(new Dimension(220, 52));
        select.setEnabled(!Props.getBool(node.props, "disabled", false));
        return select;
    }

    private static JComponent checkbox(DesktopNode node, Theme theme, Interpreter interpreter) {
        JCheckBox checkbox = new JCheckBox(node.textContent(), Props.getBool(node.props, "checked", false));
        checkbox.setBackground(theme.color(Theme.Token.BACKGROUND));
        checkbox.setForeground(theme.color(Theme.Token.FOREGROUND));
        Object callback = Props.get(node.props, "onChange");
        if (callback != null && interpreter != null && interpreter.isCallable(callback))
            checkbox.addActionListener(event -> interpreter.callCallback(callback,
                    java.util.Collections.singletonList(checkbox.isSelected())));
        return checkbox;
    }

    private static JComponent progress(DesktopNode node, Theme theme) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(Math.max(0, Math.min(100, Props.getInt(node.props, "value", 0))));
        bar.setStringPainted(Props.getBool(node.props, "showValue", false));
        bar.setForeground(theme.color(Theme.Token.PRIMARY));
        bar.setBackground(theme.color(Theme.Token.MUTED));
        return bar;
    }

    private static JComponent spinner(DesktopNode node, Theme theme) {
        JLabel spinner = label(node, theme, Font.PLAIN, 14);
        spinner.setText("Loading".equals(node.textContent()) || node.textContent().isEmpty()
                ? "Loading..." : node.textContent());
        return spinner;
    }

    private static JComponent statCard(DesktopNode node, Theme theme) {
        JPanel card = panel(theme, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(label(DesktopNode.text(Props.getString(node.props, "label", "")), theme, Font.PLAIN, 12));
        card.add(label(DesktopNode.text(Props.getString(node.props, "value", node.textContent())), theme,
                Font.BOLD, 24));
        return card;
    }

    private static JComponent divider(Theme theme) {
        JPanel divider = new JPanel();
        divider.setBackground(theme.color(Theme.Token.BORDER));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(1, 1));
        return divider;
    }

    private static JComponent spacer(DesktopNode node) {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(Props.getInt(node.props, "width", 1),
                Props.getInt(node.props, "height", 12)));
        return spacer;
    }

    private static JComponent list(DesktopNode node, Theme theme) {
        List<Object> items = options(node.props);
        if (items.isEmpty()) for (DesktopNode child : node.children) items.add(child.textContent());
        JList<Object> list = new JList<>(items.toArray());
        list.setBackground(theme.color(Theme.Token.CARD));
        list.setForeground(theme.color(Theme.Token.CARD_FOREGROUND));
        return scroll(list, theme);
    }

    public static final Set<String> modifiedCells = new HashSet<>();

    private static JComponent table(DesktopNode node, Theme theme, Interpreter interpreter) {
        List<Object> headers = valueList(node.props.containsKey("columns")
                ? node.props.get("columns") : node.props.get("headers"));
        List<Object> rows = valueList(node.props.get("rows"));
        boolean editable = Props.getBool(node.props, "editable", false);
        DefaultTableModel model = new DefaultTableModel(headers.toArray(), 0) {
            @Override public boolean isCellEditable(int row, int column) { return editable; }
            @Override public void setValueAt(Object value, int row, int column) {
                modifiedCells.add(row + "," + column);
                Object old = getValueAt(row, column);
                super.setValueAt(value, row, column);
                Object onCellChange = node.props.get("onChange");
                if (onCellChange != null && interpreter != null && interpreter.isCallable(onCellChange)) {
                    java.util.List<Object> args = new java.util.ArrayList<>();
                    args.add(row); args.add(column);
                    args.add(old != null ? String.valueOf(old) : "");
                    args.add(String.valueOf(value));
                    interpreter.callCallback(onCellChange, args);
                }
            }
        };
        for (Object row : rows) model.addRow(valueList(row).toArray());
        JTable table = new JTable(model) {
            @Override public void changeSelection(int row, int column, boolean toggle, boolean extend) {
                if (!editable) {
                    getSelectionModel().setSelectionInterval(row, row);
                    getColumnModel().getSelectionModel().clearSelection();
                    return;
                }
                super.changeSelection(row, column, toggle, extend);
            }
        };
        table.setBackground(theme.color(Theme.Token.CARD));
        table.setForeground(theme.color(Theme.Token.CARD_FOREGROUND));
        table.setGridColor(theme.color(Theme.Token.BORDER));
        table.setRowHeight(32);
        table.setSelectionBackground(theme.color(Theme.Token.PRIMARY));
        table.setSelectionForeground(theme.color(Theme.Token.FOREGROUND));
        if (editable) {
            table.setRowSelectionAllowed(true);
            table.setColumnSelectionAllowed(true);
            table.setCellSelectionEnabled(true);
        } else {
            table.setCellSelectionEnabled(false);
            table.setColumnSelectionAllowed(false);
            table.setRowSelectionAllowed(true);
        }
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.clearSelection();
        int selectedRow = Props.getInt(node.props, "selectedRow", -1);
        if (selectedRow >= 0 && selectedRow < table.getRowCount()) {
            table.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
        }
        table.getTableHeader().setBackground(theme.color(Theme.Token.MUTED));
        table.getTableHeader().setForeground(theme.color(Theme.Token.FOREGROUND));
        configureEditors(table, headers, node.props.get("editors"), theme);
        Object rowClick = Props.get(node.props, "onRowClick");
        if (rowClick != null && interpreter != null && interpreter.isCallable(rowClick)) {
            table.getSelectionModel().addListSelectionListener(event -> {
                if (event.getValueIsAdjusting() || table.getSelectedRow() < 0) return;
                interpreter.callCallback(rowClick, Collections.singletonList(table.getSelectedRow()));
                DesktopRuntime.refreshActive();
            });
        }
        javax.swing.table.DefaultTableCellRenderer highlightRenderer =
                new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                String key = row + "," + col;
                if (selected) {
                    c.setBackground(theme.color(Theme.Token.PRIMARY));
                    c.setForeground(theme.color(Theme.Token.PRIMARY_FOREGROUND));
                } else if (modifiedCells.contains(key)) {
                    c.setBackground(new Color(200, 225, 255));
                    c.setForeground(theme.color(Theme.Token.FOREGROUND));
                } else {
                    c.setBackground(theme.color(Theme.Token.CARD));
                    c.setForeground(theme.color(Theme.Token.CARD_FOREGROUND));
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, highlightRenderer);
        JScrollPane pane = scroll(table, theme);

        Object onSave = Props.get(node.props, "onSave");
        if (onSave != null && interpreter != null && interpreter.isCallable(onSave)) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            pane.setBorder(null);
            wrapper.add(pane, BorderLayout.CENTER);
            JButton saveBtn = new JButton("Salvar alteracoes");
            saveBtn.setBackground(theme.color(Theme.Token.PRIMARY));
            saveBtn.setForeground(theme.color(Theme.Token.PRIMARY_FOREGROUND));
            saveBtn.setFocusPainted(false);
            saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            saveBtn.addActionListener(event -> {
                StringBuilder data = new StringBuilder();
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < model.getColumnCount(); c++) {
                        Object val = model.getValueAt(r, c);
                        data.append(val == null ? "" : String.valueOf(val));
                        if (c < model.getColumnCount() - 1) data.append(" | ");
                    }
                    if (r < model.getRowCount() - 1) data.append("\n");
                }
                interpreter.callCallback(onSave, Collections.singletonList(data.toString()));
                modifiedCells.clear();
                DesktopRuntime.refreshActive();
            });
            JPanel savePanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            savePanel.setOpaque(false);
            savePanel.add(saveBtn);
            wrapper.add(savePanel, BorderLayout.SOUTH);
            return wrapper;
        }
        return pane;
    }

    private static void styleInput(JComponent component, Theme theme, Map<String, Object> props) {
        component.setBackground(theme.color(Theme.Token.INPUT));
        component.setForeground(theme.color(Theme.Token.FOREGROUND));
        component.setEnabled(!Props.getBool(props, "disabled", false));
        component.setBorder(new RoundedBorder(theme));
        if (component instanceof javax.swing.text.JTextComponent) {
            ((javax.swing.text.JTextComponent) component).setCaretColor(theme.color(Theme.Token.FOREGROUND));
        }
    }

    private static void applySize(JComponent component, Map<String, Object> props) {
        int width = Props.getInt(props, "width", -1);
        int height = Props.getInt(props, "height", -1);
        if (width > 0 || height > 0) component.setPreferredSize(new Dimension(
                width > 0 ? width : component.getPreferredSize().width,
                height > 0 ? height : component.getPreferredSize().height));
        if (component instanceof JLabel) ((JLabel) component).setHorizontalAlignment(SwingConstants.LEFT);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> valueList(Object value) {
        return value instanceof List ? new ArrayList<>((List<Object>) value) : new ArrayList<>();
    }

    private static List<Object> options(Map<String, Object> props) {
        return valueList(props.get("options"));
    }

    @SuppressWarnings("unchecked")
    private static void configureEditors(JTable table, List<Object> headers, Object source, Theme theme) {
        if (!(source instanceof Map)) return;
        Map<String, Object> editors = (Map<String, Object>) source;
        for (int index = 0; index < headers.size(); index++) {
            Object definition = editors.get(String.valueOf(headers.get(index)));
            if (!(definition instanceof Map)) continue;
            Object choices = ((Map<String, Object>) definition).get("options");
            if (!(choices instanceof List)) continue;
            JComboBox<Object> combo = new JComboBox<>(valueList(choices).toArray());
            combo.setBackground(theme.color(Theme.Token.INPUT));
            combo.setForeground(theme.color(Theme.Token.FOREGROUND));
            table.getColumnModel().getColumn(index).setCellEditor(new DefaultCellEditor(combo));
        }
    }

    private static void bindTextChange(javax.swing.text.JTextComponent input, DesktopNode node, Interpreter interpreter) {
        Object callback = Props.get(node.props, "onChange");
        if (callback == null || interpreter == null || !interpreter.isCallable(callback)) return;
        input.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent event) { changed(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent event) { changed(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent event) { changed(); }

            private void changed() {
                interpreter.callCallback(callback, java.util.Collections.singletonList(input.getText()));
            }
        });
    }

    private static JScrollPane scroll(JComponent component, Theme theme) {
        JScrollPane pane = new JScrollPane(component);
        pane.setBorder(BorderFactory.createLineBorder(theme.color(Theme.Token.BORDER)));
        pane.getViewport().setBackground(theme.color(Theme.Token.CARD));
        pane.setBackground(theme.color(Theme.Token.CARD));
        return pane;
    }

    private static final class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color stroke;
        private final int radius;

        RoundedPanel(Color fill, Color stroke, int radius) {
            this.fill = fill;
            this.stroke = stroke;
            this.radius = radius;
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.setColor(stroke);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class StyledButton extends JButton {
        private final Color fill;
        private final Color text;

        StyledButton(String label, Color fill, Color text) {
            super(label);
            this.fill = fill;
            this.text = text;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            setForeground(text);
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(getModel().isPressed() ? fill.darker() : getModel().isRollover() ? fill.brighter() : fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class RoundedBorder extends AbstractBorder {
        private final Theme theme;

        RoundedBorder(Theme theme) {
            this.theme = theme;
        }

        @Override public java.awt.Insets getBorderInsets(java.awt.Component component) {
            return new java.awt.Insets(0, 12, 0, 12);
        }

        @Override public void paintBorder(java.awt.Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(component.hasFocus() ? theme.color(Theme.Token.RING) : theme.color(Theme.Token.BORDER));
            g.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            g.dispose();
        }
    }

    private static final class RoundedTextField extends JTextField {
        private final Theme theme;

        RoundedTextField(Theme theme) {
            this.theme = theme;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(theme.color(Theme.Token.INPUT));
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class RoundedPasswordField extends JPasswordField {
        private final Theme theme;

        RoundedPasswordField(Theme theme) {
            this.theme = theme;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(theme.color(Theme.Token.INPUT));
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class StyledSelect extends JButton {
        private final List<Object> choices;
        private final Theme theme;
        private final Object callback;
        private final Interpreter interpreter;

        StyledSelect(String value, List<Object> choices, Theme theme, Object callback, Interpreter interpreter) {
            super(value);
            this.choices = choices;
            this.theme = theme;
            this.callback = callback;
            this.interpreter = interpreter;
            setHorizontalAlignment(SwingConstants.LEFT);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 30));
            setForeground(theme.color(Theme.Token.FOREGROUND));
            addActionListener(event -> openMenu());
        }

        private void openMenu() {
            JPopupMenu menu = new JPopupMenu();
            menu.setBorder(BorderFactory.createLineBorder(theme.color(Theme.Token.BORDER)));
            for (Object choice : choices) {
                String text = String.valueOf(choice);
                JMenuItem item = new JMenuItem(text);
                item.setBackground(theme.color(Theme.Token.CARD));
                item.setForeground(theme.color(Theme.Token.FOREGROUND));
                item.addActionListener(event -> {
                    setText(text);
                    if (callback != null && interpreter != null && interpreter.isCallable(callback)) {
                        interpreter.callCallback(callback, java.util.Collections.singletonList(text));
                        DesktopRuntime.refreshActive();
                    }
                });
                menu.add(item);
            }
            menu.setPopupSize(getWidth(), menu.getPreferredSize().height);
            menu.show(this, 0, getHeight() + 4);
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(theme.color(Theme.Token.INPUT));
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g.setColor(hasFocus() ? theme.color(Theme.Token.RING) : theme.color(Theme.Token.BORDER));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            int x = getWidth() - 18;
            int y = getHeight() / 2 - 2;
            g.setColor(theme.color(Theme.Token.MUTED_FOREGROUND));
            g.drawLine(x, y, x + 5, y + 5);
            g.drawLine(x + 5, y + 5, x + 10, y);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
