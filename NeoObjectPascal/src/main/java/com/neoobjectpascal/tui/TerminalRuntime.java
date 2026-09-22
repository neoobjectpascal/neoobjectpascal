package com.neoobjectpascal.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import com.neoobjectpascal.Interpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The TerminalInk render loop: repeatedly builds the UI tree, lays it out, and paints it,
 * then polls for input until a quit key is pressed.
 *
 * <p>The root argument is either a NeoObjectPascal build function (called every frame to
 * produce the current tree) or a static {@link TuiNode} tree (rebuilt-identical each
 * frame). Interactive input dispatch is a later phase; the {@link #keyHandlers} list and
 * {@link #collectKeyHandlers} hook are the extension points it will populate.
 */
public final class TerminalRuntime {

    /** Poll interval so a future animated loop can tick without busy-spinning. */
    private static final long FRAME_SLEEP_MS = 60L;

    /** Current runtime, so the {@code navigate} native can switch screens during a callback. */
    static TerminalRuntime active;

    private final Object rootSpec;
    private final Interpreter interp;
    private final TuiContext context;
    private final Map<String, Object> renderOptions;
    private String activeModalKey;
    private String focusBeforeModal;

    // Multi-screen navigation: when render() is given a #{ name: buildFn } map, each key is a
    // screen and navigate(name) switches which one the loop rebuilds. A single build function
    // (the classic render(ui) form) is the sole screen — fully backwards compatible.
    private final java.util.Map<String, Object> screens;
    private String currentScreen;

    @SuppressWarnings("unchecked")
    public TerminalRuntime(Object rootSpec, Interpreter interp) {
        this(rootSpec, null, interp);
    }

    @SuppressWarnings("unchecked")
    public TerminalRuntime(Object rootSpec, Object renderOptions, Interpreter interp) {
        this.rootSpec = rootSpec;
        this.interp = interp;
        this.context = new TuiContext(interp);
        this.renderOptions = renderOptions instanceof Map
                ? (Map<String, Object>) renderOptions : Collections.emptyMap();
        if (rootSpec instanceof java.util.Map) {
            this.screens = (java.util.Map<String, Object>) rootSpec;
            this.currentScreen = screens.isEmpty() ? null : screens.keySet().iterator().next();
        } else {
            this.screens = null;
        }
    }

    /** Switch the active screen (no-op if the name isn't a registered screen). */
    void navigate(String name) {
        if (screens != null && name != null && screens.containsKey(name)) {
            currentScreen = name;
        }
    }

    /** Move keyboard focus to the widget whose {@code key} prop equals {@code key}. */
    void focus(String key) {
        if (key != null) {
            context.focusByKey(key);
        }
    }

    /** Convenience: construct and run in one call. */
    public static void render(Object rootSpec, Interpreter interp, Screen screen) throws IOException {
        new TerminalRuntime(rootSpec, interp).run(screen);
    }

    /** Render with optional logical-screen dimensions: {@code #{ width, height }}. */
    public static void render(Object rootSpec, Object renderOptions, Interpreter interp, Screen screen) throws IOException {
        new TerminalRuntime(rootSpec, renderOptions, interp).run(screen);
    }

    /** Start the screen, run the loop until quit, then restore the terminal. */
    public void run(Screen screen) throws IOException {
        screen.startScreen();
        active = this;
        try {
            boolean running = true;
            while (running) {
                TerminalSize resized = screen.doResizeIfNecessary();
                TerminalSize size = resized != null ? resized : screen.getTerminalSize();

                int logicalWidth = Math.max(0, Math.min(size.getColumns(),
                        Props.getInt(renderOptions, "width", size.getColumns())));
                int logicalHeight = Math.max(0, Math.min(size.getRows(),
                        Props.getInt(renderOptions, "height", size.getRows())));
                int offsetX = Math.max(0, (size.getColumns() - logicalWidth) / 2);
                int offsetY = Math.max(0, (size.getRows() - logicalHeight) / 2);

                TuiNode root = buildRoot();
                List<TuiNode> modals = openModals(root);
                TuiNode modal = modals.isEmpty() ? null : modals.get(modals.size() - 1);
                synchronizeModalFocus(modal);
                LaidOutNode laid = LayoutEngine.layout(withoutModals(root), logicalWidth, logicalHeight);

                // Focusables were collected during buildRoot(); clamp the persistent index.
                context.clampFocus();

                screen.clear();
                Renderer.render(screen.newTextGraphics(), laid, size, offsetX, offsetY);
                if (modal != null) {
                    LaidOutNode overlay = LayoutEngine.layout(ModalWidgets.overlay(modal, logicalWidth, logicalHeight),
                            logicalWidth, logicalHeight);
                    Renderer.render(screen.newTextGraphics(), overlay, size, offsetX, offsetY);
                }
                screen.refresh();

                KeyStroke key = screen.pollInput();
                if (key == null) {
                    sleep(FRAME_SLEEP_MS);
                    continue;
                }
                if (modal != null && key.getKeyType() == KeyType.Escape) {
                    closeOnEscape(modal, interp, key);
                    continue; // A modal owns Escape even when closing is disabled.
                }
                // Tab / Shift-Tab cycle focus when more than one focusable exists.
                if (context.focusableCount() > 1) {
                    if (key.getKeyType() == KeyType.Tab) {
                        context.cycleFocus(1);
                        continue;
                    }
                    if (key.getKeyType() == KeyType.ReverseTab) {
                        context.cycleFocus(-1);
                        continue;
                    }
                }
                if (context.dispatchToFocused(key)) continue; // consumed by focused widget
                if (isQuit(key)) running = false;             // global quit
            }
        } finally {
            active = null;
            screen.stopScreen();
        }
    }

    /**
     * Build the current root node from the spec (build-fn or static tree). The {@link
     * TuiContext} is made active for the duration so interactive widget factories can read
     * persistent state, derive stable keys, and register focusables in encounter order.
     */
    private TuiNode buildRoot() {
        context.beginFrame();
        TuiContext.set(context);
        try {
            // The active spec is the current screen's build fn (multi-screen) or the root spec.
            Object spec = (screens != null) ? (currentScreen != null ? screens.get(currentScreen) : null) : rootSpec;
            Object result = spec;
            if (interp != null && interp.isCallable(spec)) {
                result = interp.callCallback(spec, Collections.emptyList());
            }
            TuiNode node = NodeCoercion.coerceChild(result);
            if (node == null) {
                node = TuiNode.of("Box", new java.util.LinkedHashMap<>(), new ArrayList<>());
            }
            return node;
        } finally {
            TuiContext.clear();
        }
    }

    private void synchronizeModalFocus(TuiNode modal) {
        if (modal == null) {
            context.clearFocusScope();
            if (activeModalKey != null && focusBeforeModal != null) context.focusByKey(focusBeforeModal);
            activeModalKey = null;
            focusBeforeModal = null;
            context.clampFocus();
            return;
        }
        String key = Props.getString(modal.props, "_modalKey", modal.key);
        Set<String> scope = new LinkedHashSet<>();
        collectFocusKeys(modal, scope);
        if (!java.util.Objects.equals(activeModalKey, key)) {
            // Resolve the base-frame index to a stable key before applying the modal scope.
            context.clearFocusScope();
            context.clampFocus();
            focusBeforeModal = context.focusedKey();
            activeModalKey = key;
        }
        context.setFocusScope(scope);
        context.clampFocus();
    }

    static boolean closeOnEscape(TuiNode modal, Interpreter interp, KeyStroke key) {
        if (modal == null || key.getKeyType() != KeyType.Escape
                || !Props.getBool(modal.props, "closeOnEscape", true)) return false;
        Object callback = Props.get(modal.props, "onClose");
        if (interp != null && interp.isCallable(callback)) interp.callCallback(callback, Collections.emptyList());
        return true;
    }

    private static List<TuiNode> openModals(TuiNode node) {
        List<TuiNode> result = new ArrayList<>();
        collectOpenModals(node, result);
        return result;
    }

    private static void collectOpenModals(TuiNode node, List<TuiNode> result) {
        if (node == null) return;
        if ("Modal".equals(node.type)) {
            if (Props.getBool(node.props, "open", false)) result.add(node);
            return;
        }
        for (TuiNode child : node.children) collectOpenModals(child, result);
    }

    private static void collectFocusKeys(TuiNode node, Set<String> result) {
        if (node == null) return;
        String key = Props.getString(node.props, "_focusKey", null);
        if (key != null) result.add(key);
        for (TuiNode child : node.children) collectFocusKeys(child, result);
    }

    /** Copy the regular tree while removing modal declarations from the base layer. */
    private static TuiNode withoutModals(TuiNode node) {
        if (node == null || "Modal".equals(node.type)) return null;
        List<TuiNode> children = new ArrayList<>();
        for (TuiNode child : node.children) {
            TuiNode copied = withoutModals(child);
            if (copied != null) children.add(copied);
        }
        return new TuiNode(node.type, new java.util.LinkedHashMap<>(node.props), children);
    }

    /** Global quit keys: Escape, EOF, Ctrl+C, or 'q'. */
    static boolean isQuit(KeyStroke key) {
        KeyType t = key.getKeyType();
        if (t == KeyType.Escape || t == KeyType.EOF) return true;
        if (t == KeyType.Character && key.getCharacter() != null) {
            char c = key.getCharacter();
            if (key.isCtrlDown() && (c == 'c' || c == 'C')) return true;
            if (c == 'q' || c == 'Q') return true;
        }
        return false;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
