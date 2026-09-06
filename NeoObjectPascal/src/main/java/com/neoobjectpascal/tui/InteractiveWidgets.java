package com.neoobjectpascal.tui;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import com.neoobjectpascal.Interpreter;
import com.neoobjectpascal.NativeFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Stateful, keyboard-driven phase-2 widgets ({@code TextInput}, {@code Select}).
 *
 * <p>Each factory: derives a stable key from {@link TuiContext}, looks up (or creates) its
 * persistent state, registers itself as a focusable with a {@link KeyHandler} bound to that
 * state, and expands to a primitive {@code Text}/{@code Box} tree reflecting the current
 * state and whether it is focused. The key handlers and state types are {@code public} so
 * they can be unit-tested headlessly without a terminal.
 */
public final class InteractiveWidgets {

    /** Domains offered for {@code EmailInput} autocompletion when none are supplied. */
    public static final List<String> DEFAULT_DOMAINS = Arrays.asList(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "icloud.com", "aol.com", "live.com", "proton.me");

    private InteractiveWidgets() {}

    // ==================================================================== TextInput

    /** Mutable per-widget state for a {@code TextInput}. */
    public static final class TextInputState {
        public String value;
        public int cursor;

        public TextInputState(String value) {
            this.value = value != null ? value : "";
            this.cursor = this.value.length();
        }

        static TextInputState fromProps(Map<String, Object> props) {
            String initial = Props.getString(props, "value",
                    Props.getString(props, "defaultValue", ""));
            return new TextInputState(initial);
        }
    }

    /** {@code TextInput(props?)} — props: placeholder, value/defaultValue, onChange, onSubmit. */
    public static NativeFunction textInput() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            TuiContext ctx = TuiContext.currentOrTransient();
            String key = ctx.keyFor("TextInput", props);
            TextInputState state = ctx.stateFor(key, () -> TextInputState.fromProps(props));

            KeyHandler handler = k -> handleTextInputKey(state, props, interp, k);
            int index = ctx.registerFocusable(key, handler);
            boolean focused = ctx.isFocused(index);

            return expandTextInput(state, props, focused);
        };
    }

    /**
     * Apply a key to a {@code TextInput}'s state. Returns true when consumed. Fires
     * {@code onChange(value)} on any edit and {@code onSubmit(value)} on Enter.
     */
    public static boolean handleTextInputKey(TextInputState st, Map<String, Object> props,
                                             Interpreter interp, KeyStroke key) {
        KeyType t = key.getKeyType();
        switch (t) {
            case ArrowLeft:
                if (st.cursor > 0) st.cursor--;
                return true;
            case ArrowRight:
                if (st.cursor < st.value.length()) st.cursor++;
                return true;
            case Backspace:
                if (st.cursor > 0) {
                    st.value = st.value.substring(0, st.cursor - 1) + st.value.substring(st.cursor);
                    st.cursor--;
                    fire(interp, props, "onChange", st.value);
                }
                return true;
            case Delete:
                if (st.cursor < st.value.length()) {
                    st.value = st.value.substring(0, st.cursor) + st.value.substring(st.cursor + 1);
                    fire(interp, props, "onChange", st.value);
                }
                return true;
            case Enter:
                fire(interp, props, "onSubmit", st.value);
                return true;
            case Character:
                Character ch = key.getCharacter();
                if (ch != null) {
                    st.value = st.value.substring(0, st.cursor) + ch + st.value.substring(st.cursor);
                    st.cursor++;
                    fire(interp, props, "onChange", st.value);
                }
                return true;
            default:
                return false;
        }
    }

    /** Expand a {@code TextInput} to a Text node (placeholder / value / cursored value). */
    static TuiNode expandTextInput(TextInputState st, Map<String, Object> props, boolean focused) {
        String value = st.value;
        if (value.isEmpty() && !focused) {
            String placeholder = Props.getString(props, "placeholder", "");
            return Nodes.text(placeholder, "dim", true);
        }
        if (!focused) {
            return Nodes.text(value);
        }
        // Focused: render an inverse block at the cursor position.
        int cursor = Math.max(0, Math.min(st.cursor, value.length()));
        String before = value.substring(0, cursor);
        String cursorChar = cursor < value.length() ? value.substring(cursor, cursor + 1) : " ";
        String after = cursor < value.length() ? value.substring(cursor + 1) : "";
        List<TuiNode> parts = new ArrayList<>();
        if (!before.isEmpty()) parts.add(Nodes.text(before));
        parts.add(Nodes.text(cursorChar, "inverse", true));
        if (!after.isEmpty()) parts.add(Nodes.text(after));
        return Nodes.hbox(0, parts);
    }

    // ======================================================================= Select

    /** Mutable per-widget state for a {@code Select}. */
    public static final class SelectState {
        public int focused;
        public int offset;
        public Object selectedValue;
        public boolean hasSelection;

        public SelectState() {
            this.focused = 0;
            this.offset = 0;
            this.selectedValue = null;
            this.hasSelection = false;
        }

        static SelectState fromProps(Map<String, Object> props) {
            SelectState s = new SelectState();
            if (Props.has(props, "defaultValue")) {
                s.selectedValue = Props.get(props, "defaultValue");
                s.hasSelection = true;
            }
            return s;
        }
    }

    /** {@code Select(props?)} — props: options(list of #{label,value}), onChange, visibleCount, defaultValue. */
    public static NativeFunction select() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            List<Map<String, Object>> options = optionsOf(props);
            TuiContext ctx = TuiContext.currentOrTransient();
            String key = ctx.keyFor("Select", props);
            SelectState state = ctx.stateFor(key, () -> SelectState.fromProps(props));

            KeyHandler handler = k -> handleSelectKey(state, options, props, interp, k);
            ctx.registerFocusable(key, handler);

            return expandSelect(state, options, props);
        };
    }

    /**
     * Apply a key to a {@code Select}'s state. Returns true when consumed. Enter selects the
     * focused option and fires {@code onChange(value)}; Up/Down move focus and scroll the
     * window by {@code visibleCount}.
     */
    public static boolean handleSelectKey(SelectState st, List<Map<String, Object>> options,
                                          Map<String, Object> props, Interpreter interp, KeyStroke key) {
        int size = options.size();
        int visible = Math.max(1, Props.getInt(props, "visibleCount", 5));
        switch (key.getKeyType()) {
            case ArrowUp:
                if (st.focused > 0) {
                    st.focused--;
                    if (st.focused < st.offset) st.offset = st.focused;
                }
                return true;
            case ArrowDown:
                if (st.focused < size - 1) {
                    st.focused++;
                    if (st.focused >= st.offset + visible) st.offset = st.focused - visible + 1;
                }
                return true;
            case Enter:
                if (size > 0) {
                    Object value = options.get(st.focused).get("value");
                    st.selectedValue = value;
                    st.hasSelection = true;
                    fire(interp, props, "onChange", value);
                }
                return true;
            default:
                return false;
        }
    }

    /** Expand a {@code Select} to a VBox of the visible option rows (windowed by offset). */
    static TuiNode expandSelect(SelectState st, List<Map<String, Object>> options,
                                Map<String, Object> props) {
        int size = options.size();
        int visible = Math.max(1, Props.getInt(props, "visibleCount", 5));
        int start = Math.max(0, Math.min(st.offset, Math.max(0, size - 1)));
        int end = Math.min(size, start + visible);

        String pointerColor = Theme.getString("Select", "pointerColor", "cyan");
        String selectedColor = Theme.getString("Select", "selectedColor", "green");

        List<TuiNode> rows = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Map<String, Object> opt = options.get(i);
            String label = Props.getString(opt, "label", String.valueOf(opt.get("value")));
            boolean isFocusedRow = i == st.focused;
            boolean isSelected = st.hasSelection && Objects.equals(opt.get("value"), st.selectedValue);

            String pointer;
            String color;
            if (isFocusedRow) {
                pointer = Glyphs.POINTER;
                color = pointerColor;
            } else if (isSelected) {
                pointer = Glyphs.TICK;
                color = selectedColor;
            } else {
                pointer = " ";
                color = "default";
            }
            List<TuiNode> cells = Arrays.asList(
                    Nodes.text(pointer, "color", color),
                    Nodes.text(label, "color", color));
            rows.add(Nodes.hbox(1, cells));
        }
        return Nodes.vbox(0, rows);
    }

    // ====================================================================== helpers

    /** Coerce the {@code options} prop into a list of record maps. */
    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> optionsOf(Map<String, Object> props) {
        List<Map<String, Object>> out = new ArrayList<>();
        Object raw = Props.get(props, "options");
        if (raw instanceof List) {
            for (Object o : (List<Object>) raw) {
                if (o instanceof Map) {
                    out.add((Map<String, Object>) o);
                }
            }
        }
        return out;
    }

    /** Invoke a callback prop with a single argument, guarding on callability. */
    private static void fire(Interpreter interp, Map<String, Object> props, String propName, Object value) {
        if (interp == null) return;
        Object cb = Props.get(props, propName);
        if (interp.isCallable(cb)) {
            interp.callCallback(cb, new ArrayList<>(Arrays.asList(value)));
        }
    }

    /** Invoke a callback prop with no arguments, guarding on callability. */
    private static void fireNoArg(Interpreter interp, Map<String, Object> props, String propName) {
        if (interp == null) return;
        Object cb = Props.get(props, propName);
        if (interp.isCallable(cb)) {
            interp.callCallback(cb, new ArrayList<>());
        }
    }

    // =================================================================== PasswordInput

    /** {@code PasswordInput(props?)} — same editing as TextInput, masked with '*'. */
    public static NativeFunction passwordInput() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            TuiContext ctx = TuiContext.currentOrTransient();
            String key = ctx.keyFor("PasswordInput", props);
            TextInputState state = ctx.stateFor(key, () -> TextInputState.fromProps(props));

            KeyHandler handler = k -> handleTextInputKey(state, props, interp, k);
            int index = ctx.registerFocusable(key, handler);
            boolean focused = ctx.isFocused(index);

            return expandPasswordInput(state, props, focused);
        };
    }

    /** Expand a {@code PasswordInput}: masked value, cursored when focused, placeholder when empty. */
    static TuiNode expandPasswordInput(TextInputState st, Map<String, Object> props, boolean focused) {
        String value = st.value;
        if (value.isEmpty() && !focused) {
            return Nodes.text(Props.getString(props, "placeholder", ""), "dim", true);
        }
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < value.length(); i++) masked.append('*');
        String m = masked.toString();
        if (!focused) {
            return Nodes.text(m);
        }
        int cursor = Math.max(0, Math.min(st.cursor, m.length()));
        String before = m.substring(0, cursor);
        String cursorChar = cursor < m.length() ? "*" : " ";
        String after = cursor < m.length() ? m.substring(cursor + 1) : "";
        List<TuiNode> parts = new ArrayList<>();
        if (!before.isEmpty()) parts.add(Nodes.text(before));
        parts.add(Nodes.text(cursorChar, "inverse", true));
        if (!after.isEmpty()) parts.add(Nodes.text(after));
        return Nodes.hbox(0, parts);
    }

    // ====================================================================== EmailInput

    /** {@code EmailInput(props?)} — TextInput plus '@' guard and domain autocompletion. */
    public static NativeFunction emailInput() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            List<String> domains = domainsOf(props);
            TuiContext ctx = TuiContext.currentOrTransient();
            String key = ctx.keyFor("EmailInput", props);
            TextInputState state = ctx.stateFor(key, () -> TextInputState.fromProps(props));

            KeyHandler handler = k -> handleEmailInputKey(state, props, domains, interp, k);
            int index = ctx.registerFocusable(key, handler);
            boolean focused = ctx.isFocused(index);

            return expandEmailInput(state, props, domains, focused);
        };
    }

    /**
     * Apply a key to an {@code EmailInput}. Blocks a second '@'; on Enter autocompletes the
     * domain suggestion (if any) then fires {@code onSubmit} with the completed value.
     */
    public static boolean handleEmailInputKey(TextInputState st, Map<String, Object> props,
                                              List<String> domains, Interpreter interp, KeyStroke key) {
        KeyType t = key.getKeyType();
        if (t == KeyType.Character) {
            Character ch = key.getCharacter();
            if (ch != null && ch == '@' && st.value.indexOf('@') >= 0) {
                return true; // block a second '@'
            }
            return handleTextInputKey(st, props, interp, key); // normal insert + onChange
        }
        if (t == KeyType.Enter) {
            String tail = emailSuggestionTail(st.value, domains);
            if (!tail.isEmpty()) {
                st.value = st.value + tail;
                st.cursor = st.value.length();
                fire(interp, props, "onChange", st.value);
            }
            fire(interp, props, "onSubmit", st.value);
            return true;
        }
        return handleTextInputKey(st, props, interp, key); // arrows / backspace / delete
    }

    /** The remaining tail of the first domain matching the part typed after '@' (or ""). */
    public static String emailSuggestionTail(String value, List<String> domains) {
        int at = value.indexOf('@');
        if (at < 0) return "";
        String typed = value.substring(at + 1);
        for (String d : domains) {
            if (d.startsWith(typed) && d.length() > typed.length()) {
                return d.substring(typed.length());
            }
        }
        return "";
    }

    /** Expand an {@code EmailInput}: value (cursored when focused) plus a dim suggestion tail. */
    static TuiNode expandEmailInput(TextInputState st, Map<String, Object> props,
                                    List<String> domains, boolean focused) {
        String value = st.value;
        if (value.isEmpty() && !focused) {
            return Nodes.text(Props.getString(props, "placeholder", ""), "dim", true);
        }
        String tail = emailSuggestionTail(value, domains);
        List<TuiNode> parts = new ArrayList<>();
        if (!focused) {
            parts.add(Nodes.text(value));
        } else {
            int cursor = Math.max(0, Math.min(st.cursor, value.length()));
            String before = value.substring(0, cursor);
            String cursorChar = cursor < value.length() ? value.substring(cursor, cursor + 1) : " ";
            String after = cursor < value.length() ? value.substring(cursor + 1) : "";
            if (!before.isEmpty()) parts.add(Nodes.text(before));
            parts.add(Nodes.text(cursorChar, "inverse", true));
            if (!after.isEmpty()) parts.add(Nodes.text(after));
        }
        if (!tail.isEmpty()) {
            parts.add(Nodes.text(tail, "dim", true));
        }
        return Nodes.hbox(0, parts);
    }

    /** Coerce the {@code domains} prop into a list of strings, else the default list. */
    @SuppressWarnings("unchecked")
    static List<String> domainsOf(Map<String, Object> props) {
        Object raw = Props.get(props, "domains");
        if (raw instanceof List && !((List<?>) raw).isEmpty()) {
            List<String> out = new ArrayList<>();
            for (Object o : (List<Object>) raw) {
                if (o != null) out.add(String.valueOf(o));
            }
            return out;
        }
        return DEFAULT_DOMAINS;
    }

    // ==================================================================== ConfirmInput

    /** {@code ConfirmInput(props?)} — y/n prompt with a default choice on Enter. */
    public static NativeFunction confirmInput() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            TuiContext ctx = TuiContext.currentOrTransient();
            String key = ctx.keyFor("ConfirmInput", props);

            KeyHandler handler = k -> handleConfirmInputKey(props, interp, k);
            ctx.registerFocusable(key, handler);

            return expandConfirmInput(props);
        };
    }

    /**
     * Apply a key to a {@code ConfirmInput}: 'y'->onConfirm, 'n'->onCancel, Enter->the default
     * choice's callback when {@code submitOnEnter}. Non-decision keys are not consumed.
     */
    public static boolean handleConfirmInputKey(Map<String, Object> props, Interpreter interp, KeyStroke key) {
        KeyType t = key.getKeyType();
        if (t == KeyType.Character) {
            Character ch = key.getCharacter();
            if (ch == null) return false;
            char c = Character.toLowerCase(ch);
            if (c == 'y') {
                fireNoArg(interp, props, "onConfirm");
                return true;
            }
            if (c == 'n') {
                fireNoArg(interp, props, "onCancel");
                return true;
            }
            return false;
        }
        if (t == KeyType.Enter) {
            boolean submitOnEnter = Props.getBool(props, "submitOnEnter", true);
            if (submitOnEnter) {
                boolean cancelDefault = "cancel".equalsIgnoreCase(
                        Props.getString(props, "defaultChoice", "confirm"));
                fireNoArg(interp, props, cancelDefault ? "onCancel" : "onConfirm");
            }
            return true;
        }
        return false;
    }

    /** Expand a {@code ConfirmInput}: "(Y/n)" for a confirm default, "(y/N)" for a cancel default. */
    static TuiNode expandConfirmInput(Map<String, Object> props) {
        boolean cancelDefault = "cancel".equalsIgnoreCase(
                Props.getString(props, "defaultChoice", "confirm"));
        return Nodes.text(cancelDefault ? "(y/N)" : "(Y/n)");
    }

    // ===================================================================== MultiSelect

    /** Mutable per-widget state for a {@code MultiSelect}. */
    public static final class MultiSelectState {
        public int focused;
        public int offset;
        public final LinkedHashSet<Object> selected = new LinkedHashSet<>();

        static MultiSelectState fromProps(Map<String, Object> props) {
            MultiSelectState s = new MultiSelectState();
            Object dv = Props.get(props, "defaultValue");
            if (dv instanceof List) {
                for (Object o : (List<?>) dv) s.selected.add(o);
            }
            return s;
        }
    }

    /** {@code MultiSelect(props?)} — checkbox list; Space toggles, Enter submits the selection. */
    public static NativeFunction multiSelect() {
        return (args, interp) -> {
            Map<String, Object> props = NodeCoercion.extractProps(args);
            List<Map<String, Object>> options = optionsOf(props);
            TuiContext ctx = TuiContext.currentOrTransient();
            String key = ctx.keyFor("MultiSelect", props);
            MultiSelectState state = ctx.stateFor(key, () -> MultiSelectState.fromProps(props));

            KeyHandler handler = k -> handleMultiSelectKey(state, options, props, interp, k);
            ctx.registerFocusable(key, handler);

            return expandMultiSelect(state, options, props);
        };
    }

    /**
     * Apply a key to a {@code MultiSelect}: Up/Down move + scroll, Space toggles the focused
     * option (fires {@code onChange} with the current list), Enter fires {@code onSubmit}.
     */
    public static boolean handleMultiSelectKey(MultiSelectState st, List<Map<String, Object>> options,
                                               Map<String, Object> props, Interpreter interp, KeyStroke key) {
        int size = options.size();
        int visible = Math.max(1, Props.getInt(props, "visibleCount", 5));
        KeyType t = key.getKeyType();
        switch (t) {
            case ArrowUp:
                if (st.focused > 0) {
                    st.focused--;
                    if (st.focused < st.offset) st.offset = st.focused;
                }
                return true;
            case ArrowDown:
                if (st.focused < size - 1) {
                    st.focused++;
                    if (st.focused >= st.offset + visible) st.offset = st.focused - visible + 1;
                }
                return true;
            case Enter:
                fire(interp, props, "onSubmit", new ArrayList<>(st.selected));
                return true;
            case Character:
                Character ch = key.getCharacter();
                if (ch != null && ch == ' ' && size > 0) {
                    Object value = options.get(st.focused).get("value");
                    if (!st.selected.remove(value)) st.selected.add(value);
                    fire(interp, props, "onChange", new ArrayList<>(st.selected));
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /** Expand a {@code MultiSelect} to a VBox of visible rows: pointer + checkbox + label. */
    static TuiNode expandMultiSelect(MultiSelectState st, List<Map<String, Object>> options,
                                     Map<String, Object> props) {
        int size = options.size();
        int visible = Math.max(1, Props.getInt(props, "visibleCount", 5));
        int start = Math.max(0, Math.min(st.offset, Math.max(0, size - 1)));
        int end = Math.min(size, start + visible);

        String pointerColor = Theme.getString("MultiSelect", "pointerColor", "cyan");
        String selectedColor = Theme.getString("MultiSelect", "selectedColor", "green");

        List<TuiNode> rows = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Map<String, Object> opt = options.get(i);
            String label = Props.getString(opt, "label", String.valueOf(opt.get("value")));
            boolean isFocusedRow = i == st.focused;
            boolean isSelected = st.selected.contains(opt.get("value"));

            String pointer = isFocusedRow ? Glyphs.POINTER : " ";
            String check = isSelected ? Glyphs.TICK : " ";
            String labelColor = isFocusedRow ? pointerColor : (isSelected ? selectedColor : "default");

            List<TuiNode> cells = Arrays.asList(
                    Nodes.text(pointer, "color", pointerColor),
                    Nodes.text(check, "color", selectedColor),
                    Nodes.text(label, "color", labelColor));
            rows.add(Nodes.hbox(1, cells));
        }
        return Nodes.vbox(0, rows);
    }
}
