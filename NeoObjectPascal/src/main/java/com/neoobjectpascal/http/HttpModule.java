package com.neoobjectpascal.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoobjectpascal.Interpreter;
import com.neoobjectpascal.NativeFunction;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP client for NeoObjectPascal, registered by {@code uses http;}. Backed by the Java 11
 * {@link java.net.http.HttpClient} (no external dependencies); JSON via Jackson.
 *
 * <p>Design (fetch-style): HTTP responses — including 4xx/5xx — return a response record with
 * {@code ok} reflecting 2xx; transport failures (connect/timeout/DNS/bad URL) throw a
 * {@link RuntimeException} that NeoObjectPascal {@code try/catch} can bind. Request bodies that are
 * records/arrays are auto-serialized to JSON; {@link #httpForm} wraps a body as form-urlencoded.
 */
public final class HttpModule {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    private HttpModule() {}

    /** Marker for a form-urlencoded request body produced by {@code httpForm(#{...})}. */
    static final class FormBody {
        final Map<?, ?> data;
        FormBody(Map<?, ?> data) { this.data = data; }
    }

    public static void register(Interpreter interp) {
        interp.registerNative("httpGet",    (args, i) -> requestNoBody("GET", args));
        interp.registerNative("httpDelete", (args, i) -> requestNoBody("DELETE", args));
        interp.registerNative("httpPost",   (args, i) -> requestWithBody("POST", args));
        interp.registerNative("httpPut",    (args, i) -> requestWithBody("PUT", args));
        interp.registerNative("httpPatch",  (args, i) -> requestWithBody("PATCH", args));
        interp.registerNative("httpRequest", (args, i) -> requestFromOptions(args));

        // Auth helpers — return a headers record usable as the `headers` argument.
        interp.registerNative("httpBearer", (args, i) -> {
            Map<String, Object> h = new LinkedHashMap<>();
            h.put("Authorization", "Bearer " + str(arg(args, 0)));
            return h;
        });
        interp.registerNative("httpBasic", (args, i) -> {
            String creds = str(arg(args, 0)) + ":" + str(arg(args, 1));
            String token = Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
            Map<String, Object> h = new LinkedHashMap<>();
            h.put("Authorization", "Basic " + token);
            return h;
        });

        // Wrap a record as a form-urlencoded body.
        interp.registerNative("httpForm", (args, i) -> {
            Object o = arg(args, 0);
            return new FormBody(o instanceof Map ? (Map<?, ?>) o : new LinkedHashMap<>());
        });
    }

    // ---- Argument shapes ----

    // GET / DELETE: (url [, headers])
    static Object requestNoBody(String method, List<Object> args) {
        String url = str(arg(args, 0));
        Map<?, ?> headers = asMap(arg(args, 1));
        return send(method, url, headers, null, null, null, null);
    }

    // POST / PUT / PATCH: (url [, body [, headers]])
    static Object requestWithBody(String method, List<Object> args) {
        String url = str(arg(args, 0));
        Object body = arg(args, 1);
        Map<?, ?> headers = asMap(arg(args, 2));
        return send(method, url, headers, body, null, null, null);
    }

    // httpRequest(#{ method, url, headers, body, form, query, auth, timeout })
    static Object requestFromOptions(List<Object> args) {
        Map<?, ?> o = asMap(arg(args, 0));
        if (o == null) throw new RuntimeException("httpRequest requires a record: #{ url: ... }");
        String method = o.containsKey("method") ? str(o.get("method")).toUpperCase() : "GET";
        String url = str(o.get("url"));
        Map<?, ?> headers = asMap(o.get("headers"));
        Map<?, ?> auth = asMap(o.get("auth"));
        Map<?, ?> query = asMap(o.get("query"));
        Object body = o.containsKey("form") ? new FormBody(asMapOrEmpty(o.get("form"))) : o.get("body");
        Integer timeout = o.get("timeout") instanceof Number ? ((Number) o.get("timeout")).intValue() : null;
        return send(method, url, headers, body, query, auth, timeout);
    }

    // ---- Core ----

    private static Object send(String method, String url, Map<?, ?> headers, Object body,
                               Map<?, ?> query, Map<?, ?> auth, Integer timeoutSeconds) {
        try {
            String finalUrl = appendQuery(url, query);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(finalUrl))
                    .timeout(timeoutSeconds != null ? Duration.ofSeconds(timeoutSeconds) : DEFAULT_TIMEOUT);

            // Body publisher + implicit content-type
            String implicitContentType = null;
            HttpRequest.BodyPublisher publisher;
            if (body == null) {
                publisher = HttpRequest.BodyPublishers.noBody();
            } else if (body instanceof FormBody) {
                publisher = HttpRequest.BodyPublishers.ofString(formEncode(((FormBody) body).data));
                implicitContentType = "application/x-www-form-urlencoded";
            } else if (body instanceof Map || body instanceof List) {
                publisher = HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(body));
                implicitContentType = "application/json";
            } else {
                publisher = HttpRequest.BodyPublishers.ofString(String.valueOf(body));
            }
            builder.method(method, publisher);

            // Headers (auth merged in), tracking whether Content-Type was supplied
            boolean hasContentType = false;
            hasContentType |= applyHeaders(builder, auth);
            hasContentType |= applyHeaders(builder, headers);
            if (implicitContentType != null && !hasContentType) {
                builder.header("Content-Type", implicitContentType);
            }

            HttpResponse<String> response = CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return toResponseRecord(response);
        } catch (RuntimeException e) {
            throw e; // already a NeoObjectPascal-catchable error (e.g. bad URL)
        } catch (Exception e) {
            // Transport failure — surface as a catchable error (fetch-style).
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new RuntimeException("HTTP request failed: " + msg);
        }
    }

    private static Map<String, Object> toResponseRecord(HttpResponse<String> response) {
        int status = response.statusCode();
        String body = response.body() != null ? response.body() : "";

        Map<String, Object> headers = new LinkedHashMap<>();
        response.headers().map().forEach((k, v) -> headers.put(k, v.isEmpty() ? "" : v.get(0)));

        Map<String, Object> record = new LinkedHashMap<>();
        record.put("status", status);
        record.put("ok", status >= 200 && status < 300);
        record.put("body", body);
        record.put("json", tryParseJson(body, str(headers.get("content-type"))));
        record.put("headers", headers);
        record.put("url", response.uri() != null ? response.uri().toString() : "");
        return record;
    }

    // ---- Helpers ----

    private static boolean applyHeaders(HttpRequest.Builder builder, Map<?, ?> headers) {
        boolean hasContentType = false;
        if (headers != null) {
            for (Map.Entry<?, ?> e : headers.entrySet()) {
                String name = str(e.getKey());
                builder.header(name, str(e.getValue()));
                if (name.equalsIgnoreCase("Content-Type")) hasContentType = true;
            }
        }
        return hasContentType;
    }

    static String appendQuery(String url, Map<?, ?> query) {
        if (query == null || query.isEmpty()) return url;
        StringBuilder sb = new StringBuilder(url);
        sb.append(url.contains("?") ? '&' : '?');
        boolean first = true;
        for (Map.Entry<?, ?> e : query.entrySet()) {
            if (!first) sb.append('&');
            sb.append(enc(str(e.getKey()))).append('=').append(enc(str(e.getValue())));
            first = false;
        }
        return sb.toString();
    }

    static String formEncode(Map<?, ?> data) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<?, ?> e : data.entrySet()) {
            if (!first) sb.append('&');
            sb.append(enc(str(e.getKey()))).append('=').append(enc(str(e.getValue())));
            first = false;
        }
        return sb.toString();
    }

    static Object tryParseJson(String body, String contentType) {
        if (body == null) return null;
        String trimmed = body.trim();
        boolean looksJson = (contentType != null && contentType.toLowerCase().contains("json"))
                || trimmed.startsWith("{") || trimmed.startsWith("[");
        if (!looksJson || trimmed.isEmpty()) return null;
        try {
            return MAPPER.readValue(trimmed, Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static Object arg(List<Object> args, int i) {
        return args != null && i < args.size() ? args.get(i) : null;
    }

    private static Map<?, ?> asMap(Object o) {
        return o instanceof Map ? (Map<?, ?>) o : null;
    }

    private static Map<?, ?> asMapOrEmpty(Object o) {
        return o instanceof Map ? (Map<?, ?>) o : new LinkedHashMap<>();
    }

    private static String str(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}
