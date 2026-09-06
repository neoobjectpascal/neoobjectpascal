package com.neoobjectpascal.web;

import com.neoobjectpascal.Interpreter;
import com.neoobjectpascal.NativeFunction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WebInk module: a web-frontend framework for NeoObjectPascal (the web analog of TerminalInk).
 * Registered when a program does {@code uses webink;}. Provides the widget natives plus the
 * {@code render} (start a local server + open the browser) and {@code navigate} natives.
 *
 * <p>State lives in NeoObjectPascal globals; browser events call server-side callbacks that mutate
 * state and re-render the current route. A program is entirely TerminalInk or entirely WebInk —
 * mixing the two is rejected at load time by the interpreter.
 */
public final class WebInk {

    private WebInk() {}

    public static void register(Interpreter interp) {
        WebWidgets.register(interp);

        // render(routes)  or  render(routes, #{ title, port })
        interp.registerNative("render", (NativeFunction) (args, i) -> {
            Map<String, Object> routes = mapAt(args, 0);
            Map<String, Object> opts = mapAt(args, 1);
            if (routes == null) {
                System.err.println("[WebInk] render(...) requer um mapa de rotas, ex.: render(#{ \"/\": home });");
                return null;
            }
            String title = Props.getString(opts, "title", null);
            int port = Props.getInt(opts, "port", 0); // 0 = porta livre efêmera

            WebRuntime runtime = new WebRuntime(i, routes, title);
            WebServer server = new WebServer(runtime);
            try {
                int actual = server.start(port);
                String url = "http://127.0.0.1:" + actual + "/";
                System.out.println("[WebInk] servindo em " + url + "  (Ctrl+C para sair)");
                if (!"1".equals(System.getenv("WEBINK_NO_BROWSER"))) {
                    BrowserLauncher.open(url);
                }
                server.awaitShutdown();
            } catch (Exception e) {
                System.err.println("[WebInk] falha ao iniciar o servidor: " + e.getMessage());
            }
            return null;
        });

        // navigate("/rota") — muda a rota atual a partir de um callback (o servidor avisa o browser)
        interp.registerNative("navigate", (NativeFunction) (args, i) -> {
            if (WebRuntime.active != null && !args.isEmpty() && args.get(0) != null) {
                WebRuntime.active.navigate(String.valueOf(args.get(0)));
            }
            return null;
        });
    }

    /** The n-th {@code Map} argument (routes = 0th map, opts = 1st map), or null if absent. */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> mapAt(List<Object> args, int index) {
        int seen = 0;
        for (Object a : args) {
            if (a instanceof Map) {
                if (seen == index) return (Map<String, Object>) a;
                seen++;
            }
        }
        return index == 1 ? new LinkedHashMap<>() : null;
    }
}
