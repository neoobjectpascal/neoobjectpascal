package com.neoobjectpascal.web;

import com.neoobjectpascal.Interpreter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-app WebInk state: the route table, the current route, the event-handler table, and the
 * server-side render / event-dispatch logic. UI state itself lives in NeoObjectPascal globals;
 * this runtime only maps browser events back to the NeoObjectPascal callbacks that mutate them.
 *
 * <p>The server is single-threaded, so a plain {@link #active} holder is enough for the
 * {@code navigate} native to reach the current runtime during a callback.
 */
final class WebRuntime {

    /** Result of dispatching a browser event: fresh HTML for the current route + optional nav. */
    static final class EventResult {
        final String html;
        final String navigate; // route to push into history, or null
        EventResult(String html, String navigate) { this.html = html; this.navigate = navigate; }
    }

    static WebRuntime active; // set around callback/render on the single server thread

    private final Interpreter interp;
    private final Map<String, Object> routes;
    private final String title;

    private String currentRoute = "/";
    private final Map<String, Object> handlers = new HashMap<>();
    private int handlerSeq = 0;
    private String pendingNavigate = null;

    WebRuntime(Interpreter interp, Map<String, Object> routes, String title) {
        this.interp = interp;
        this.routes = routes != null ? routes : new LinkedHashMap<>();
        this.title = title;
    }

    String getTitle() { return title; }

    boolean isCallable(Object fn) { return interp.isCallable(fn); }

    /** Register an event-handler function and return its per-render id (h1, h2, ...). */
    String registerHandler(Object fn) {
        String id = "h" + (++handlerSeq);
        handlers.put(id, fn);
        return id;
    }

    /** Build a route's UI to HTML. Resets the handler table so ids match the returned DOM. */
    synchronized String renderRoute(String route) {
        currentRoute = normalize(route);
        handlers.clear();
        handlerSeq = 0;

        Object fn = routes.get(currentRoute);
        if (fn == null) fn = routes.get("*"); // optional catch-all
        WebNode tree;
        if (fn != null && interp.isCallable(fn)) {
            active = this;
            try {
                tree = coerceRoot(interp.callCallback(fn, Collections.emptyList()));
            } finally {
                active = null;
            }
        } else {
            tree = notFound(currentRoute);
        }
        return HtmlRenderer.render(tree, this);
    }

    /**
     * Dispatch a browser event: look up the handler (from the last render the client is showing),
     * invoke it (state mutates; it may call {@code navigate}), then re-render the resulting route.
     */
    synchronized EventResult dispatch(String handlerId, Object value) {
        Object fn = handlers.get(handlerId);
        pendingNavigate = null;
        if (fn != null && interp.isCallable(fn)) {
            active = this;
            try {
                interp.callCallback(fn, value == null ? Collections.emptyList()
                        : Collections.singletonList(value));
            } finally {
                active = null;
            }
        }
        String nav = pendingNavigate;
        String html = renderRoute(nav != null ? nav : currentRoute);
        return new EventResult(html, nav);
    }

    /** Called by the {@code navigate} native during a callback. */
    void navigate(String route) {
        pendingNavigate = normalize(route);
    }

    // ---- helpers ----

    private WebNode coerceRoot(Object r) {
        if (r instanceof WebNode) return (WebNode) r;
        if (r instanceof List) {
            java.util.List<WebNode> kids = new java.util.ArrayList<>();
            for (Object e : (List<?>) r) if (e instanceof WebNode) kids.add((WebNode) e);
            return WebNode.of("Container", new LinkedHashMap<>(), kids);
        }
        if (r != null) return WebNode.text(WebNodes.fmt(r));
        return WebNode.of("Container", new LinkedHashMap<>(), new java.util.ArrayList<>());
    }

    private WebNode notFound(String route) {
        java.util.List<WebNode> kids = new java.util.ArrayList<>();
        Map<String, Object> h = new LinkedHashMap<>();
        h.put("level", 2);
        h.put("text", "404");
        kids.add(WebNode.of("Heading", h, new java.util.ArrayList<>()));
        kids.add(WebNode.text("Rota não encontrada: " + route));
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("class", "max-w-md mx-auto mt-16 text-center");
        return WebNode.of("Card", card, kids);
    }

    private static String normalize(String route) {
        if (route == null || route.isEmpty()) return "/";
        int cut = route.length();
        int q = route.indexOf('?'); if (q >= 0) cut = Math.min(cut, q);
        int h = route.indexOf('#'); if (h >= 0) cut = Math.min(cut, h);
        route = route.substring(0, cut);
        if (!route.startsWith("/")) route = "/" + route;
        if (route.length() > 1 && route.endsWith("/")) route = route.substring(0, route.length() - 1);
        return route;
    }
}
