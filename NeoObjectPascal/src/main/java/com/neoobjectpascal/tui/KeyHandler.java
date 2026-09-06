package com.neoobjectpascal.tui;

import com.googlecode.lanterna.input.KeyStroke;

/**
 * Extension point for interactive widgets (added in a later phase).
 *
 * <p>The render loop consults an ordered list of focusable handlers before applying its
 * own global quit keys. A handler returns {@code true} when it has consumed the key,
 * stopping further dispatch. Phase 1 registers no handlers, but the wiring in
 * {@link TerminalRuntime} is already in place so focus/input dispatch slots in cleanly.
 */
@FunctionalInterface
public interface KeyHandler {

    /** Handle a key; return true if consumed (dispatch stops). */
    boolean handleKey(KeyStroke key);
}
