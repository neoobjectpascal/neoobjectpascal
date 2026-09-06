package com.neoobjectpascal.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless, offline tests for {@link HttpModule}. A local {@link HttpServer} bound to
 * 127.0.0.1 on an ephemeral port echoes what it receives so request-side assertions are precise.
 */
class HttpModuleTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static HttpServer server;
    private static String baseUrl;

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);

        // GET /json -> a small JSON object.
        server.createContext("/json", exchange -> {
            drainBody(exchange);
            respond(exchange, 200, "application/json", "{\"id\":1,\"nome\":\"Alice\"}");
        });

        // Any method /echo -> reflect back method, content-type, body and Authorization,
        // plus the request path and raw query string.
        server.createContext("/echo", exchange -> {
            String body = readBody(exchange);
            Map<String, Object> reply = new LinkedHashMap<>();
            reply.put("method", exchange.getRequestMethod());
            reply.put("contentType", exchange.getRequestHeaders().getFirst("Content-Type"));
            reply.put("authorization", exchange.getRequestHeaders().getFirst("Authorization"));
            reply.put("body", body);
            reply.put("path", exchange.getRequestURI().toString());
            reply.put("query", exchange.getRequestURI().getRawQuery());
            respond(exchange, 200, "application/json", MAPPER.writeValueAsString(reply));
        });

        server.createContext("/status/404", exchange -> {
            drainBody(exchange);
            respond(exchange, 404, "text/plain", "not found");
        });

        server.createContext("/status/500", exchange -> {
            drainBody(exchange);
            respond(exchange, 500, "text/plain", "boom");
        });

        server.setExecutor(null);
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    // ---- Server helpers ----

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int n;
            while ((n = in.read(chunk)) != -1) {
                buf.write(chunk, 0, n);
            }
            return new String(buf.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static void drainBody(HttpExchange exchange) throws IOException {
        readBody(exchange);
    }

    private static void respond(HttpExchange exchange, int status, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static List<Object> args(Object... values) {
        return Arrays.asList(values);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asRecord(Object response) {
        assertInstanceOf(Map.class, response, "response record should be a Map");
        return (Map<String, Object>) response;
    }

    // ---- 1. GET returns parsed JSON ----

    @Test
    @DisplayName("GET returns 200/ok and json parsed as a Map with nome=Alice")
    void get_returnsParsedJson() {
        Map<String, Object> response = asRecord(
                HttpModule.requestNoBody("GET", args(baseUrl + "/json")));

        assertEquals(200, response.get("status"));
        assertEquals(Boolean.TRUE, response.get("ok"));
        assertInstanceOf(Map.class, response.get("json"), "json should be a Map");
        Map<?, ?> json = (Map<?, ?>) response.get("json");
        assertEquals("Alice", json.get("nome"));
    }

    // ---- 2. Error status codes ----

    @Test
    @DisplayName("GET on 404 and 500 report the status with ok=false")
    void get_errorStatuses_areNotOk() {
        Map<String, Object> notFound = asRecord(
                HttpModule.requestNoBody("GET", args(baseUrl + "/status/404")));
        assertEquals(404, notFound.get("status"));
        assertEquals(Boolean.FALSE, notFound.get("ok"));

        Map<String, Object> serverError = asRecord(
                HttpModule.requestNoBody("GET", args(baseUrl + "/status/500")));
        assertEquals(500, serverError.get("status"));
        assertEquals(Boolean.FALSE, serverError.get("ok"));
    }

    // ---- 3. POST with a Map body -> JSON ----

    @Test
    @DisplayName("POST with a Map body sends application/json with the serialized body")
    void post_mapBody_sendsJson() throws Exception {
        Map<String, Object> reqBody = new LinkedHashMap<>();
        reqBody.put("nome", "Alice");
        String expectedBody = MAPPER.writeValueAsString(reqBody);

        Map<String, Object> response = asRecord(
                HttpModule.requestWithBody("POST", args(baseUrl + "/echo", reqBody)));
        assertEquals(Boolean.TRUE, response.get("ok"));

        Map<?, ?> echo = (Map<?, ?>) response.get("json");
        assertEquals("POST", echo.get("method"));
        assertEquals("application/json", echo.get("contentType"));
        assertEquals(expectedBody, echo.get("body"));
    }

    // ---- 4. POST with a String body -> sent as-is ----

    @Test
    @DisplayName("POST with a String body is sent verbatim without a forced JSON content-type")
    void post_stringBody_sentAsIs() {
        String raw = "just plain text";
        Map<String, Object> response = asRecord(
                HttpModule.requestWithBody("POST", args(baseUrl + "/echo", raw)));

        Map<?, ?> echo = (Map<?, ?>) response.get("json");
        assertEquals(raw, echo.get("body"));
        assertNull(echo.get("contentType"), "no Content-Type should be forced for a String body");
    }

    // ---- 5. POST with a FormBody -> form-urlencoded ----

    @Test
    @DisplayName("POST with a FormBody sends application/x-www-form-urlencoded encoded pairs")
    void post_formBody_sendsUrlEncoded() {
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("user", "a b");
        form.put("pass", "x&y");

        Map<String, Object> response = asRecord(HttpModule.requestWithBody(
                "POST", args(baseUrl + "/echo", new HttpModule.FormBody(form))));

        Map<?, ?> echo = (Map<?, ?>) response.get("json");
        assertEquals("application/x-www-form-urlencoded", echo.get("contentType"));
        assertEquals("user=a+b&pass=x%26y", echo.get("body"));
    }

    // ---- 6. Headers are sent (3rd arg of requestWithBody) ----

    @Test
    @DisplayName("Request headers are forwarded to the server")
    void headers_areSent() {
        Map<String, Object> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer tok");

        Map<String, Object> response = asRecord(HttpModule.requestWithBody(
                "POST", args(baseUrl + "/echo", "x", headers)));

        Map<?, ?> echo = (Map<?, ?>) response.get("json");
        assertEquals("Bearer tok", echo.get("authorization"));
    }

    // ---- 7. requestFromOptions query params ----

    @Test
    @DisplayName("requestFromOptions appends query parameters to the URL")
    void requestFromOptions_appendsQuery() {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("page", 2);
        query.put("q", "a b");

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("url", baseUrl + "/echo");
        options.put("query", query);

        Map<String, Object> response = asRecord(
                HttpModule.requestFromOptions(args(options)));

        Map<?, ?> echo = (Map<?, ?>) response.get("json");
        assertEquals("page=2&q=a+b", echo.get("query"));
        assertTrue(String.valueOf(echo.get("path")).contains("page=2&q=a+b"));
    }

    // ---- 8. requestFromOptions auth merged into headers ----

    @Test
    @DisplayName("requestFromOptions merges auth headers into the request")
    void requestFromOptions_mergesAuth() {
        Map<String, Object> auth = new LinkedHashMap<>();
        auth.put("Authorization", "Bearer x");

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("url", baseUrl + "/echo");
        options.put("auth", auth);

        Map<String, Object> response = asRecord(
                HttpModule.requestFromOptions(args(options)));

        Map<?, ?> echo = (Map<?, ?>) response.get("json");
        assertEquals("Bearer x", echo.get("authorization"));
    }

    // ---- 9. Transport failure throws RuntimeException ----

    @Test
    @DisplayName("A request to a closed port throws a RuntimeException")
    void transportFailure_throws() {
        assertThrows(RuntimeException.class,
                () -> HttpModule.requestNoBody("GET", args("http://127.0.0.1:1/")));
    }

    // ---- 10. Unit tests for pure helpers ----

    @Test
    @DisplayName("formEncode URL-encodes keys and values joined with &")
    void formEncode_encodesPairs() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("a", "1");
        data.put("b", "x y");
        data.put("c", "x&y");
        assertEquals("a=1&b=x+y&c=x%26y", HttpModule.formEncode(data));
    }

    @Test
    @DisplayName("appendQuery uses ? without an existing query and & when one is present")
    void appendQuery_choosesSeparator() {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("page", 2);
        query.put("q", "a b");

        assertEquals("http://h/p?page=2&q=a+b",
                HttpModule.appendQuery("http://h/p", query));
        assertEquals("http://h/p?x=1&page=2&q=a+b",
                HttpModule.appendQuery("http://h/p?x=1", query));
    }

    @Test
    @DisplayName("appendQuery returns the URL unchanged for null or empty query")
    void appendQuery_noQuery_unchanged() {
        assertEquals("http://h/p", HttpModule.appendQuery("http://h/p", null));
        assertEquals("http://h/p",
                HttpModule.appendQuery("http://h/p", new LinkedHashMap<>()));
    }

    @Test
    @DisplayName("tryParseJson yields Map for objects, List for arrays, null for non-JSON")
    void tryParseJson_shapes() {
        assertInstanceOf(Map.class, HttpModule.tryParseJson("{\"a\":1}", null));
        assertInstanceOf(List.class, HttpModule.tryParseJson("[1,2,3]", null));
        assertNull(HttpModule.tryParseJson("plain", null));
        assertNull(HttpModule.tryParseJson("", null));
        assertNull(HttpModule.tryParseJson(null, null));
    }

    @Test
    @DisplayName("tryParseJson parses when the content-type says JSON even without braces")
    void tryParseJson_byContentType() {
        Object parsed = HttpModule.tryParseJson("{\"k\":\"v\"}", "application/json; charset=utf-8");
        assertInstanceOf(Map.class, parsed);
        assertFalse(((Map<?, ?>) parsed).isEmpty());
    }
}
