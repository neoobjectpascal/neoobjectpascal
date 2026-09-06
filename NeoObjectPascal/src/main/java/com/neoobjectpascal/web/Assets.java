package com.neoobjectpascal.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Loads the bundled WebInk assets (Tailwind, Chart.js, the runtime JS, the HTML shell) from the jar. */
final class Assets {

    private Assets() {}

    static byte[] bytes(String name) throws IOException {
        try (InputStream in = Assets.class.getResourceAsStream("/webink/" + name)) {
            if (in == null) throw new IOException("WebInk asset not found: " + name);
            return in.readAllBytes();
        }
    }

    static String text(String name) throws IOException {
        return new String(bytes(name), StandardCharsets.UTF_8);
    }
}
