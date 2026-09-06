package com.neoobjectpascal.web;

import java.awt.Desktop;
import java.net.URI;
import java.util.Locale;

/** Opens a URL in the system default browser, with per-OS fallbacks. */
final class BrowserLauncher {

    private BrowserLauncher() {}

    static void open(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                return;
            }
        } catch (Throwable ignored) {
            // fall through to CLI openers
        }
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String[] cmd;
        if (os.contains("mac")) {
            cmd = new String[]{"open", url};
        } else if (os.contains("win")) {
            cmd = new String[]{"rundll32", "url.dll,FileProtocolHandler", url};
        } else {
            cmd = new String[]{"xdg-open", url};
        }
        try {
            new ProcessBuilder(cmd).start();
        } catch (Exception e) {
            System.out.println("[WebInk] abra no navegador: " + url);
        }
    }
}
