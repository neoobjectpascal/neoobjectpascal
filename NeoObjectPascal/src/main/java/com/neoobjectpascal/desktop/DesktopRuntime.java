package com.neoobjectpascal.desktop;

import com.neoobjectpascal.Interpreter;

import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Owns native Swing windows and deliberately does nothing where a display is unavailable. */
public final class DesktopRuntime {
    private static DesktopRuntime active;

    private final Object screenSource;
    private final Map<String, Object> options;
    private final Interpreter interpreter;
    private JFrame frame;

    private DesktopRuntime(Object screenSource, Map<String, Object> options, Interpreter interpreter) {
        this.screenSource = screenSource;
        this.options = options;
        this.interpreter = interpreter;
    }

    public static boolean render(DesktopNode root, Map<String, Object> options) {
        return render((Object) root, options, null);
    }

    static boolean render(DesktopNode root, Map<String, Object> options, Interpreter interpreter) {
        return render((Object) root, options, interpreter);
    }

    static boolean render(Object screenSource, Map<String, Object> options, Interpreter interpreter) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[DesktopInk] render skipped (headless environment)");
            return false;
        }
        DesktopRuntime runtime = new DesktopRuntime(screenSource, new LinkedHashMap<>(options), interpreter);
        active = runtime;
        SwingUtilities.invokeLater(runtime::show);
        return true;
    }

    static void refreshActive() {
        DesktopRuntime runtime = active;
        if (runtime != null) SwingUtilities.invokeLater(runtime::rebuild);
    }

    private void show() {
        Theme theme = Theme.resolve(Props.getString(options, "theme", "light"));
        frame = new JFrame(Props.getString(options, "title", "DesktopInk"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(Props.getInt(options, "width", 960), Props.getInt(options, "height", 720));
        if (Props.getBool(options, "maximized", false)) frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        else if (Props.getBool(options, "centered", true)) frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        rebuild();
    }

    private void rebuild() {
        if (frame == null || !frame.isDisplayable()) return;
        DesktopNode root = buildRoot();
        if (root == null) return;
        Theme theme = Theme.resolve(Props.getString(options, "theme", "light"));
        DesktopNode modal = openModal(root);
        JComponent content = DesktopRenderer.render(withoutModals(root), theme, interpreter);
        content.setOpaque(true);
        content.setBackground(theme.color(Theme.Token.BACKGROUND));
        frame.getContentPane().removeAll();
        frame.add(content, BorderLayout.CENTER);
        frame.getGlassPane().setVisible(false);
        if (modal != null) showModal(frame, modal, theme, interpreter);
        frame.revalidate();
        frame.repaint();
    }

    @SuppressWarnings("unchecked")
    private DesktopNode buildRoot() {
        if (screenSource instanceof DesktopNode) return (DesktopNode) screenSource;
        if (screenSource instanceof Map && interpreter != null) {
            for (Object candidate : ((Map<String, Object>) screenSource).values()) {
                if (!interpreter.isCallable(candidate)) continue;
                Object result = interpreter.callCallback(candidate, java.util.Collections.emptyList());
                if (result instanceof DesktopNode) return (DesktopNode) result;
            }
        }
        return null;
    }

    private static void showModal(JFrame frame, DesktopNode modal, Theme theme, Interpreter interpreter) {
        Object onClose = Props.get(modal.props, "onClose");
        Runnable close = () -> {
            if (onClose != null && interpreter != null && interpreter.isCallable(onClose)) {
                interpreter.callCallback(onClose, java.util.Collections.emptyList());
            }
            frame.getGlassPane().setVisible(false);
        };
        ModalOverlay overlay = new ModalOverlay(close, Props.getBool(modal.props, "closeOnEscape", true));
        JComponent dialog = DesktopRenderer.render(modal, theme, interpreter);
        wireCloseButtons(dialog, close);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        overlay.add(dialog, constraints);
        frame.setGlassPane(overlay);
        overlay.setVisible(true);
    }

    private static void wireCloseButtons(java.awt.Component component, Runnable close) {
        if (component instanceof javax.swing.JButton
                && Boolean.TRUE.equals(((JComponent) component).getClientProperty("desktopink.closeModal"))) {
            ((javax.swing.JButton) component).addActionListener(event -> close.run());
        }
        if (component instanceof java.awt.Container) {
            for (java.awt.Component child : ((java.awt.Container) component).getComponents()) {
                wireCloseButtons(child, close);
            }
        }
    }

    private static DesktopNode openModal(DesktopNode node) {
        if (node == null) return null;
        DesktopNode result = "Modal".equals(node.type) && Props.getBool(node.props, "open", false) ? node : null;
        for (DesktopNode child : node.children) {
            DesktopNode nested = openModal(child);
            if (nested != null) result = nested;
        }
        return result;
    }

    private static DesktopNode withoutModals(DesktopNode node) {
        List<DesktopNode> children = new ArrayList<>();
        for (DesktopNode child : node.children) if (!"Modal".equals(child.type)) children.add(withoutModals(child));
        return DesktopNode.of(node.type, new LinkedHashMap<>(node.props), children);
    }

    /** Visible glass pane consumes mouse and keyboard events before they reach the application. */
    private static final class ModalOverlay extends JPanel {
        ModalOverlay(Runnable close, boolean closeOnEscape) {
            super(new GridBagLayout());
            setOpaque(false);
            setFocusable(true);
            MouseAdapter consumeMouse = new MouseAdapter() {
                @Override public void mousePressed(MouseEvent event) { event.consume(); }
                @Override public void mouseReleased(MouseEvent event) { event.consume(); }
                @Override public void mouseClicked(MouseEvent event) { event.consume(); }
            };
            addMouseListener(consumeMouse);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseDragged(MouseEvent event) { event.consume(); }
                @Override public void mouseMoved(MouseEvent event) { event.consume(); }
            });
            addMouseWheelListener(new MouseWheelListener() {
                @Override public void mouseWheelMoved(MouseWheelEvent event) { event.consume(); }
            });
            if (closeOnEscape) {
                getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close-modal");
                getActionMap().put("close-modal", new javax.swing.AbstractAction() {
                    @Override public void actionPerformed(java.awt.event.ActionEvent event) { close.run(); }
                });
            }
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
        }
    }
}
