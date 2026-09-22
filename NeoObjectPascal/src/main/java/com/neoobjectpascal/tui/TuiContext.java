package com.neoobjectpascal.tui;

import com.googlecode.lanterna.input.KeyStroke;

import com.neoobjectpascal.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Per-runtime render context for interactive widgets.
 *
 * <p>The context lives for the whole {@link TerminalRuntime} lifetime (so widget state and
 * the focus index survive across frames) but is re-primed at the start of every build via
 * {@link #beginFrame()}. It is exposed to widget factories through a thread-local set by
 * the runtime around each build call.
 *
 * <p>Keying is index-based reconciliation (React/Ink style): because the build function
 * calls the widget factories in the same order every frame, a per-frame per-type counter
 * yields a stable key for each widget that has no explicit {@code key} prop.
 */
public final class TuiContext {

    private static final ThreadLocal<TuiContext> CURRENT = new ThreadLocal<>();

    /** The interpreter, so widgets can invoke NeoObjectPascal callbacks. May be null in tests. */
    public final Interpreter interp;

    /** Widget state, keyed by widget key; SURVIVES across frames. */
    public final Map<String, Object> stateStore = new HashMap<>();

    /** Per-frame per-type counter for deterministic auto-keys; reset each build. */
    private final Map<String, Integer> typeCounter = new HashMap<>();

    /** Per-frame ordered focusables; rebuilt each build in encounter order. */
    private final List<Focusable> focusables = new ArrayList<>();

    /** Currently focused index into {@link #focusables}; SURVIVES across frames. */
    public int focusIndex = 0;
    private String focusKey;
    private Set<String> focusScope;

    /** Whether an {@code autoFocus} request already ran (one-shot; never reset). */
    private boolean initialFocusApplied = false;

    public TuiContext(Interpreter interp) {
        this.interp = interp;
    }

    // ---- Thread-local access (set by the runtime around each build) ----

    /** The active context, or null if no build is in progress. */
    public static TuiContext current() {
        return CURRENT.get();
    }

    /** The active context, or a throwaway one so widgets never NPE when used stand-alone. */
    public static TuiContext currentOrTransient() {
        TuiContext c = CURRENT.get();
        return c != null ? c : new TuiContext(null);
    }

    static void set(TuiContext c) {
        CURRENT.set(c);
    }

    static void clear() {
        CURRENT.remove();
    }

    // ---- Per-frame lifecycle ----

    /** Reset per-frame data (counters + focusables) before a build. */
    void beginFrame() {
        typeCounter.clear();
        focusables.clear();
    }

    /** Stable key for a widget: its {@code key} prop, else {@code "<Type>#<n>"}. */
    public String keyFor(String type, Map<String, Object> props) {
        String explicit = Props.getString(props, "key", null);
        if (explicit != null) {
            return explicit;
        }
        int n = typeCounter.getOrDefault(type, 0);
        typeCounter.put(type, n + 1);
        return type + "#" + n;
    }

    /** Look up existing state for {@code key} or create and store it via {@code init}. */
    @SuppressWarnings("unchecked")
    public <T> T stateFor(String key, Supplier<T> init) {
        Object existing = stateStore.get(key);
        if (existing == null) {
            existing = init.get();
            stateStore.put(key, existing);
        }
        return (T) existing;
    }

    /** Register a focusable in encounter order; returns its focus index. */
    public int registerFocusable(String key, KeyHandler handler) {
        focusables.add(new Focusable(key, handler));
        return focusables.size() - 1;
    }

    /** Register a focusable, honouring an {@code autoFocus} prop for one-shot initial focus. */
    public int registerFocusable(String key, KeyHandler handler, Map<String, Object> props) {
        int idx = registerFocusable(key, handler);
        if (props != null && Props.getBool(props, "autoFocus", false)) {
            requestInitialFocus(idx);
        }
        return idx;
    }

    /** Move focus to the focusable whose {@code key} matches; returns true if found. */
    public boolean focusByKey(String key) {
        for (int i = 0; i < focusables.size(); i++) {
            if (java.util.Objects.equals(focusables.get(i).key, key) && inScope(focusables.get(i))) {
                focusIndex = i;
                focusKey = key;
                return true;
            }
        }
        return false;
    }

    /** One-shot initial focus (autoFocus): applies only the first time, then never again. */
    public void requestInitialFocus(int index) {
        if (!initialFocusApplied) {
            focusIndex = index;
            initialFocusApplied = true;
        }
    }

    /** True when {@code index} is the currently focused focusable. */
    public boolean isFocused(int index) {
        return index == focusIndex;
    }

    public int focusableCount() {
        int count = 0;
        for (Focusable focusable : focusables) if (inScope(focusable)) count++;
        return count;
    }

    /** Restrict keyboard focus to these keys while a modal is active. */
    void setFocusScope(Set<String> keys) { focusScope = keys; }

    void clearFocusScope() { focusScope = null; }

    String focusedKey() { return focusKey; }

    /** Clamp {@link #focusIndex} into range after a build. */
    void clampFocus() {
        if (focusables.isEmpty() || focusableCount() == 0) {
            focusIndex = 0;
            return;
        }
        if (focusKey != null && focusByKey(focusKey)) return;
        if (focusIndex >= 0 && focusIndex < focusables.size() && inScope(focusables.get(focusIndex))) {
            focusKey = focusables.get(focusIndex).key;
            return;
        }
        for (int i = 0; i < focusables.size(); i++) {
            if (inScope(focusables.get(i))) { focusIndex = i; focusKey = focusables.get(i).key; return; }
        }
    }

    /** Move focus by {@code delta} with wrap-around. */
    void cycleFocus(int delta) {
        List<Integer> eligible = new ArrayList<>();
        for (int i = 0; i < focusables.size(); i++) if (inScope(focusables.get(i))) eligible.add(i);
        if (eligible.isEmpty()) {
            return;
        }
        int current = eligible.indexOf(focusIndex);
        if (current < 0) current = 0;
        focusIndex = eligible.get(((current + delta) % eligible.size() + eligible.size()) % eligible.size());
        focusKey = focusables.get(focusIndex).key;
    }

    /** Offer a key to the focused focusable; true if it consumed it. */
    boolean dispatchToFocused(KeyStroke key) {
        if (focusIndex < 0 || focusIndex >= focusables.size() || !inScope(focusables.get(focusIndex))) {
            return false;
        }
        KeyHandler h = focusables.get(focusIndex).handler;
        return h != null && h.handleKey(key);
    }

    private boolean inScope(Focusable focusable) {
        return focusScope == null || focusScope.contains(focusable.key);
    }

    /** A registered focusable: its stable key and the key handler bound to its state. */
    static final class Focusable {
        final String key;
        final KeyHandler handler;

        Focusable(String key, KeyHandler handler) {
            this.key = key;
            this.handler = handler;
        }
    }
}
