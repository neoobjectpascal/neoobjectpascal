package com.neoobjectpascal.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import com.neoobjectpascal.Interpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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

    // Multi-screen navigation: when render() is given a #{ name: buildFn } map, each key is a
    // screen and navigate(name) switches which one the loop rebuilds. A single build function
    // (the classic render(ui) form) is the sole screen — fully backwards compatible.
    private final java.util.Map<String, Object> screens;
    private String currentScreen;

    @SuppressWarnings("unchecked")
    public TerminalRuntime(Object rootSpec, Interpreter interp) {
        this.rootSpec = rootSpec;
        this.interp = interp;
        this.context = new TuiContext(interp);
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

    /** Start the screen, run the loop until quit, then restore the terminal. */
    public void run(Screen screen) throws IOException {
        screen.startScreen();
        active = this;
        try {
            boolean running = true;
            while (running) {
                TerminalSize resized = screen.doResizeIfNecessary();
                TerminalSize size = resized != null ? resized : screen.getTerminalSize();

                TuiNode root = buildRoot();
                LaidOutNode laid = LayoutEngine.layout(root, size.getColumns(), size.getRows());

                // Focusables were collected during buildRoot(); clamp the persistent index.
                context.clampFocus();

                screen.clear();
                Renderer.render(screen.newTextGraphics(), laid, size);
                screen.refresh();

                KeyStroke key = screen.pollInput();
                if (key == null) {
                    sleep(FRAME_SLEEP_MS);
                    continue;
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
