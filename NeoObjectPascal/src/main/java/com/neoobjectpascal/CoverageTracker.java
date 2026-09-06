package com.neoobjectpascal;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Records which PUBLIC class methods are invoked while running tests, so {@code --test-all} can
 * report real coverage (public methods exercised by tests) instead of a file-count ratio.
 * Keys are {@code "ClassName.methodName"}.
 */
final class CoverageTracker {

    private CoverageTracker() {}

    private static boolean enabled = false;
    private static final Set<String> invoked = new LinkedHashSet<>();

    static void enable() { enabled = true; invoked.clear(); }
    static void disable() { enabled = false; }

    /** Called from OOHandler when a public method executes. */
    static void recordInvocation(String className, String methodName) {
        if (enabled) invoked.add(className + "." + methodName);
    }

    static Set<String> getInvoked() { return invoked; }
}
