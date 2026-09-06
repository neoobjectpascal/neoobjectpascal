package com.neoobjectpascal.tui;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import com.neoobjectpascal.Interpreter;

/**
 * Entry point for the TerminalInk terminal-UI framework. Registers the widget builder
 * functions and the render loop as NeoObjectPascal native functions when a program does
 * {@code uses terminalink;}.
 *
 * <p>Phase 1 provides the static layout components ({@code Text}, {@code Box},
 * {@code VBox}, {@code HBox}, {@code Spacer}) plus the {@code render} loop. Interactive
 * widgets are added in a later phase and slot in via {@link KeyHandler} /
 * {@link TerminalRuntime}'s focus hook.
 */
public final class TerminalInk {

    private TerminalInk() {}

    public static void register(Interpreter interp) {
        // Static layout components -> build a TuiNode tree.
        interp.registerNative("Text", Widgets.text());
        interp.registerNative("Box", Widgets.box());
        interp.registerNative("VBox", Widgets.vbox());
        interp.registerNative("HBox", Widgets.hbox());
        interp.registerNative("Spacer", Widgets.spacer());

        // Interactive components (stateful, keyboard-driven).
        interp.registerNative("TextInput", InteractiveWidgets.textInput());
        interp.registerNative("PasswordInput", InteractiveWidgets.passwordInput());
        interp.registerNative("EmailInput", InteractiveWidgets.emailInput());
        interp.registerNative("ConfirmInput", InteractiveWidgets.confirmInput());
        interp.registerNative("Select", InteractiveWidgets.select());
        interp.registerNative("MultiSelect", InteractiveWidgets.multiSelect());

        // Feedback / display components (stateless expansion).
        interp.registerNative("Spinner", FeedbackWidgets.spinner());
        interp.registerNative("ProgressBar", FeedbackWidgets.progressBar());
        interp.registerNative("Badge", FeedbackWidgets.badge());
        interp.registerNative("StatusMessage", FeedbackWidgets.statusMessage());
        interp.registerNative("Alert", FeedbackWidgets.alert());

        // List components (display-only).
        interp.registerNative("Item", ListWidgets.item());
        interp.registerNative("UnorderedList", ListWidgets.unorderedList());
        interp.registerNative("OrderedList", ListWidgets.orderedList());

        // Theming.
        interp.registerNative("defaultTheme", Theme.defaultThemeFn());
        interp.registerNative("extendTheme", Theme.extendThemeFn());
        interp.registerNative("ThemeProvider", Theme.themeProviderFn());
        interp.registerNative("setTheme", Theme.setThemeFn());

        // navigate(name): switch the active screen when render() was given a #{ name: buildFn } map.
        interp.registerNative("navigate", (args, i) -> {
            if (TerminalRuntime.active != null && !args.isEmpty() && args.get(0) != null) {
                TerminalRuntime.active.navigate(String.valueOf(args.get(0)));
            }
            return null;
        });

        // focus(key): move keyboard focus to the widget whose `key` prop matches (during a callback).
        interp.registerNative("focus", (args, i) -> {
            if (TerminalRuntime.active != null && !args.isEmpty() && args.get(0) != null) {
                TerminalRuntime.active.focus(String.valueOf(args.get(0)));
            }
            return null;
        });

        // render(rootOrBuildFn): drive the loop over a real terminal, degrading gracefully.
        interp.registerNative("render", (args, i) -> {
            if (args.isEmpty()) return null;
            Object spec = args.get(0);
            try {
                Screen screen = new DefaultTerminalFactory().createScreen();
                TerminalRuntime.render(spec, i, screen);
            } catch (Throwable t) {
                // Non-TTY / headless / IO failure: report and continue rather than crash.
                System.out.println("[TerminalInk] render skipped (no interactive terminal): "
                        + t.getMessage());
            }
            return null;
        });
    }
}
