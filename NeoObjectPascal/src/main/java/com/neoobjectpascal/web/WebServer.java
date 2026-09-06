package com.neoobjectpascal.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * The single-threaded WebInk HTTP server. Serves the HTML shell, the bundled JS/CSS assets, and
 * the two app endpoints ({@code /webink/render}, {@code /webink/event}). Single-threaded so events
 * are processed one at a time — which keeps the debugger's pause/step model deterministic.
 */
final class WebServer {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final WebRuntime runtime;
    private HttpServer server;
    private final CountDownLatch shutdown = new CountDownLatch(1);

    WebServer(WebRuntime runtime) {
        this.runtime = runtime;
    }

    /** Bind + start on the given port (0 = ephemeral). Returns the actual port. Non-blocking. */
    int start(int preferredPort) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", preferredPort), 0);
        server.createContext("/webink/render", this::handleRender);
        server.createContext("/webink/event", this::handleEvent);
        server.createContext("/webink/", this::handleAsset);
        server.createContext("/", this::handleShell); // SPA: any other path returns the shell
        server.setExecutor(Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "webink-server");
            t.setDaemon(true);
            return t;
        }));
        server.start();
        return server.getAddress().getPort();
    }

    /** Block until interrupted (Ctrl+C) or {@link #stop()} is called, keeping the server alive. */
    void awaitShutdown() {
        try {
            shutdown.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            stop();
        }
    }

    void stop() {
        if (server != null) server.stop(0);
    }

    // ---- handlers ----

    private void handleShell(HttpExchange ex) throws IOException {
        try {
            String shell = Assets.text("shell.html")
                    .replace("__TITLE__", HtmlRenderer.esc(
                            runtime.getTitle() != null ? runtime.getTitle() : "NeoObjectPascal · WebInk"));
            send(ex, 200, "text/html; charset=utf-8", shell.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            send(ex, 500, "text/plain; charset=utf-8", ("shell error: " + e.getMessage()).getBytes());
        }
    }

    private void handleAsset(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath(); // /webink/<file>
        String file = path.substring("/webink/".length());
        String ct = file.endsWith(".js") ? "application/javascript; charset=utf-8"
                : file.endsWith(".css") ? "text/css; charset=utf-8"
                : "application/octet-stream";
        try {
            send(ex, 200, ct, Assets.bytes(file));
        } catch (Exception e) {
            send(ex, 404, "text/plain", ("asset not found: " + file).getBytes());
        }
    }

    private void handleRender(HttpExchange ex) throws IOException {
        try {
            JsonNode body = readJson(ex);
            String route = body.has("route") ? body.get("route").asText() : "/";
            String html = runtime.renderRoute(route);
            ObjectNode resp = JSON.createObjectNode();
            resp.put("html", html);
            sendJson(ex, resp);
        } catch (Exception e) {
            sendError(ex, e);
        }
    }

    private void handleEvent(HttpExchange ex) throws IOException {
        try {
            JsonNode body = readJson(ex);
            String handlerId = body.has("handlerId") ? body.get("handlerId").asText() : null;
            Object value = jsonValue(body.get("value"));
            WebRuntime.EventResult result = runtime.dispatch(handlerId, value);
            ObjectNode resp = JSON.createObjectNode();
            resp.put("html", result.html);
            if (result.navigate != null) resp.put("navigate", result.navigate);
            sendJson(ex, resp);
        } catch (Exception e) {
            sendError(ex, e);
        }
    }

    // ---- io helpers ----

    private JsonNode readJson(HttpExchange ex) throws IOException {
        byte[] in = ex.getRequestBody().readAllBytes();
        if (in.length == 0) return JSON.createObjectNode();
        return JSON.readTree(in);
    }

    /** Coerce a JSON event value to a NeoObjectPascal-friendly scalar (String/Double/Boolean/null). */
    private Object jsonValue(JsonNode v) {
        if (v == null || v.isNull()) return null;
        if (v.isBoolean()) return v.asBoolean();
        if (v.isNumber()) return v.asDouble();
        return v.asText();
    }

    private void sendJson(HttpExchange ex, ObjectNode node) throws IOException {
        send(ex, 200, "application/json; charset=utf-8",
                JSON.writeValueAsBytes(node));
    }

    private void sendError(HttpExchange ex, Exception e) throws IOException {
        ObjectNode resp = JSON.createObjectNode();
        String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        resp.put("html", "<div class=\"m-8 p-4 rounded-lg bg-red-50 border border-red-200 text-red-800\">"
                + "WebInk erro: " + HtmlRenderer.esc(msg) + "</div>");
        try {
            send(ex, 200, "application/json; charset=utf-8", JSON.writeValueAsBytes(resp));
        } catch (Exception ignored) { /* nothing more we can do */ }
        System.err.println("[WebInk] erro ao renderizar: " + msg);
    }

    private void send(HttpExchange ex, int code, String contentType, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(code, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }
}
