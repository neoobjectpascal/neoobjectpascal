package com.neoobjectpascal;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Entry point for a program packaged as a native executable by {@code --build} (jpackage).
 *
 * <p>The bundled {@code .npas} program (and any {@code uses} modules) sit next to this jar inside
 * the app image. jpackage passes the program's file name as the launcher argument; we resolve it
 * against the jar's own directory and hand off to {@link Main}, so module resolution and every
 * runtime feature (TerminalInk, HTTP, ...) behave exactly as when run from source.
 */
public final class AppLauncher {

    private AppLauncher() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("NeoObjectPascal app: no program bundled (missing launcher argument).");
            System.exit(2);
            return;
        }

        File dir = jarDirectory();
        File program = new File(dir, args[0]);
        if (!program.isFile()) {
            System.err.println("NeoObjectPascal app: bundled program not found: " + program);
            System.exit(2);
            return;
        }

        // Reuse the standard interpreter entry point (module base = program directory).
        Main.main(new String[]{ program.getAbsolutePath() });
    }

    /** Directory containing this jar inside the packaged app image. */
    private static File jarDirectory() throws URISyntaxException {
        File self = new File(AppLauncher.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        return self.isFile() ? self.getParentFile() : self; // jar -> its dir; classes dir -> itself
    }
}
