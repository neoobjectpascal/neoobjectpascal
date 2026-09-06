package com.neoobjectpascal.web;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Renders a {@link WebNode} tree to HTML with tasteful default Tailwind classes.
 *
 * <p>Each node {@code type} maps to an HTML element plus default utility classes; the
 * {@code #{ class: "..." }} prop is appended so users extend/override. Event props
 * (onClick/onChange/onSubmit) are registered with the {@link WebRuntime} and emitted as
 * {@code data-webink-*} attributes that the client runtime wires. All text is HTML-escaped.
 */
final class HtmlRenderer {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final WebRuntime runtime;
    private final StringBuilder out = new StringBuilder();

    HtmlRenderer(WebRuntime runtime) {
        this.runtime = runtime;
    }

    static String render(WebNode root, WebRuntime runtime) {
        HtmlRenderer r = new HtmlRenderer(runtime);
        r.node(root);
        return r.out.toString();
    }

    private void node(WebNode n) {
        if (n == null) return;
        if (!Props.getBool(n.props, "visible", true)) return;   // visible:false hides node + subtree
        switch (n.type) {
            case "Text":        text(n); break;
            case "Heading":     heading(n); break;
            case "Badge":       badge(n); break;
            case "Button":      button(n); break;
            case "Link":        link(n); break;
            case "TextInput":   input(n, "text"); break;
            case "TextArea":    textarea(n); break;
            case "Select":      select(n); break;
            case "Checkbox":    checkbox(n); break;
            case "Form":        form(n); break;
            case "Chart":       chart(n); break;
            case "Table":       table(n); break;
            case "List":        list(n); break;
            case "Alert":       alert(n); break;
            case "ProgressBar": progress(n); break;
            case "Spinner":     spinner(n); break;
            case "Divider":     out.append("<hr class=\"").append(cls(n, "border-slate-200 my-4")).append("\"/>"); break;
            case "Spacer":      out.append("<div class=\"").append(cls(n, "h-4")).append("\"></div>"); break;
            case "Stat":
            case "StatCard":    statCard(n); break;
            case "Navbar":      container(n, "nav", "bg-white border-b border-slate-200 px-6 py-3 flex items-center gap-4"); break;
            case "Sidebar":     container(n, "aside", "w-60 shrink-0 bg-white border-r border-slate-200 p-4 flex flex-col gap-1"); break;
            case "Tabs":        container(n, "div", "flex gap-1 border-b border-slate-200 mb-4"); break;
            case "Page":        container(n, "div", "min-h-screen bg-slate-50 text-slate-800"); break;
            case "Section":     container(n, "section", "py-8"); break;
            case "Container":   container(n, "div", "max-w-6xl mx-auto px-4"); break;
            case "Row":         container(n, "div", "flex flex-wrap gap-4"); break;
            case "Col":         container(n, "div", "flex-1 min-w-0"); break;
            case "Card":        container(n, "div", "bg-white rounded-xl shadow-sm border border-slate-200 p-6"); break;
            case "Grid":        grid(n); break;
            default:            container(n, "div", ""); break;
        }
    }

    // ---- containers ----

    private void container(WebNode n, String tag, String base) {
        out.append('<').append(tag).append(" class=\"").append(cls(n, base)).append("\">");
        children(n);
        out.append("</").append(tag).append('>');
    }

    private void grid(WebNode n) {
        int cols = Props.getInt(n.props, "cols", 3);
        String base = "grid gap-4 grid-cols-1 md:grid-cols-" + Math.max(1, Math.min(12, cols));
        container(n, "div", base);
    }

    private void children(WebNode n) {
        for (WebNode c : n.children) node(c);
    }

    // ---- leaves ----

    private void text(WebNode n) {
        out.append("<p class=\"").append(cls(n, "text-slate-600 leading-relaxed")).append("\">")
           .append(esc(n.textContent())).append("</p>");
    }

    private void heading(WebNode n) {
        int level = Math.max(1, Math.min(6, Props.getInt(n.props, "level", 2)));
        String[] sizes = {"text-4xl", "text-3xl", "text-2xl", "text-xl", "text-lg", "text-base"};
        out.append("<h").append(level).append(" class=\"")
           .append(cls(n, "font-bold tracking-tight text-slate-900 " + sizes[level - 1])).append("\">")
           .append(esc(textOf(n))).append("</h").append(level).append('>');
    }

    private void badge(WebNode n) {
        String color = Props.getString(n.props, "color", "slate");
        out.append("<span class=\"").append(cls(n,
                "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-"
                        + color + "-100 text-" + color + "-700")).append("\">")
           .append(esc(textOf(n))).append("</span>");
    }

    private void button(WebNode n) {
        String variant = Props.getString(n.props, "variant", "primary");
        String base = "inline-flex items-center justify-center rounded-lg px-4 py-2 text-sm font-medium "
                + "transition focus:outline-none focus:ring-2 focus:ring-offset-1 ";
        base += variant.equals("secondary")
                ? "bg-slate-100 text-slate-800 hover:bg-slate-200 focus:ring-slate-400"
                : variant.equals("danger")
                ? "bg-red-600 text-white hover:bg-red-700 focus:ring-red-400"
                : "bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-400";
        out.append("<button class=\"").append(cls(n, base)).append('"')
           .append(handlerAttr(n, "onClick", "data-webink-click"))
           .append('>').append(esc(textOf(n))).append("</button>");
    }

    private void link(WebNode n) {
        // `href` is the primary key (`to` is a reserved word, usable only as a string key).
        String to = Props.getString(n.props, "href", Props.getString(n.props, "to", "#"));
        out.append("<a href=\"").append(esc(to)).append("\" data-webink-nav=\"").append(esc(to)).append('"')
           .append(" class=\"").append(cls(n, "text-indigo-600 hover:text-indigo-800 hover:underline font-medium")).append("\">")
           .append(esc(textOf(n))).append("</a>");
    }

    private void input(WebNode n, String htmlType) {
        String base = "w-full rounded-lg border border-slate-300 px-3 py-2 text-sm "
                + "focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400";
        out.append("<input type=\"").append(htmlType).append('"')
           .append(" placeholder=\"").append(esc(Props.getString(n.props, "placeholder", ""))).append('"')
           .append(" value=\"").append(esc(Props.getString(n.props, "value", ""))).append('"')
           .append(handlerAttr(n, "onChange", "data-webink-change"))
           .append(handlerAttr(n, "onSubmit", "data-webink-submit"))
           .append(" class=\"").append(cls(n, base)).append("\"/>");
    }

    private void textarea(WebNode n) {
        String base = "w-full rounded-lg border border-slate-300 px-3 py-2 text-sm "
                + "focus:outline-none focus:ring-2 focus:ring-indigo-400";
        out.append("<textarea rows=\"").append(Props.getInt(n.props, "rows", 3)).append('"')
           .append(" placeholder=\"").append(esc(Props.getString(n.props, "placeholder", ""))).append('"')
           .append(handlerAttr(n, "onChange", "data-webink-change"))
           .append(" class=\"").append(cls(n, base)).append("\">")
           .append(esc(Props.getString(n.props, "value", ""))).append("</textarea>");
    }

    private void select(WebNode n) {
        String base = "w-full rounded-lg border border-slate-300 px-3 py-2 text-sm bg-white "
                + "focus:outline-none focus:ring-2 focus:ring-indigo-400";
        out.append("<select").append(handlerAttr(n, "onChange", "data-webink-change"))
           .append(" class=\"").append(cls(n, base)).append("\">");
        Object opts = n.props.get("options");
        if (opts instanceof List) {
            for (Object o : (List<?>) opts) {
                String v = WebNodes.fmt(o);
                out.append("<option value=\"").append(esc(v)).append("\">").append(esc(v)).append("</option>");
            }
        }
        out.append("</select>");
    }

    private void checkbox(WebNode n) {
        boolean checked = Props.getBool(n.props, "checked", false);
        out.append("<label class=\"").append(cls(n, "inline-flex items-center gap-2 text-sm text-slate-700")).append("\">")
           .append("<input type=\"checkbox\"").append(checked ? " checked" : "")
           .append(handlerAttr(n, "onChange", "data-webink-change"))
           .append(" class=\"rounded border-slate-300 text-indigo-600 focus:ring-indigo-400\"/>")
           .append(esc(Props.getString(n.props, "label", textOf(n)))).append("</label>");
    }

    private void form(WebNode n) {
        out.append("<form").append(handlerAttr(n, "onSubmit", "data-webink-submit"))
           .append(" class=\"").append(cls(n, "flex flex-col gap-3")).append("\">");
        children(n);
        out.append("</form>");
    }

    private void chart(WebNode n) {
        Map<String, Object> config = new java.util.LinkedHashMap<>();
        config.put("type", Props.getString(n.props, "type", "bar"));
        if (n.props.get("data") != null) config.put("data", n.props.get("data"));
        if (n.props.get("options") != null) config.put("options", n.props.get("options"));
        String json;
        try { json = JSON.writeValueAsString(config); } catch (Exception e) { json = "{}"; }
        String h = Props.getString(n.props, "height", "h-72");
        out.append("<div class=\"").append(cls(n, h + " relative")).append("\">")
           .append("<canvas data-webink-chart='").append(esc(json)).append("'></canvas></div>");
    }

    private void table(WebNode n) {
        out.append("<div class=\"overflow-x-auto\"><table class=\"")
           .append(cls(n, "min-w-full text-sm text-left")).append("\">");
        Object cols = n.props.get("columns");
        if (cols instanceof List) {
            out.append("<thead class=\"text-xs uppercase text-slate-500 border-b border-slate-200\"><tr>");
            for (Object c : (List<?>) cols) out.append("<th class=\"px-4 py-2\">").append(esc(WebNodes.fmt(c))).append("</th>");
            out.append("</tr></thead>");
        }
        Object rows = n.props.get("rows");
        out.append("<tbody>");
        if (rows instanceof List) {
            for (Object row : (List<?>) rows) {
                out.append("<tr class=\"border-b border-slate-100 hover:bg-slate-50\">");
                if (row instanceof List) {
                    for (Object cell : (List<?>) row)
                        out.append("<td class=\"px-4 py-2 text-slate-700\">").append(esc(WebNodes.fmt(cell))).append("</td>");
                }
                out.append("</tr>");
            }
        }
        out.append("</tbody></table></div>");
    }

    private void list(WebNode n) {
        boolean ordered = Props.getBool(n.props, "ordered", false);
        String tag = ordered ? "ol" : "ul";
        out.append('<').append(tag).append(" class=\"")
           .append(cls(n, (ordered ? "list-decimal" : "list-disc") + " pl-6 space-y-1 text-slate-700")).append("\">");
        Object items = n.props.get("items");
        if (items instanceof List) {
            for (Object it : (List<?>) items) out.append("<li>").append(esc(WebNodes.fmt(it))).append("</li>");
        }
        for (WebNode c : n.children) { out.append("<li>"); node(c); out.append("</li>"); }
        out.append("</").append(tag).append('>');
    }

    private void alert(WebNode n) {
        String v = Props.getString(n.props, "variant", "info");
        String color = v.equals("success") ? "green" : v.equals("warning") ? "amber"
                : v.equals("error") || v.equals("danger") ? "red" : "blue";
        out.append("<div class=\"").append(cls(n,
                "rounded-lg border p-4 text-sm bg-" + color + "-50 border-" + color + "-200 text-" + color + "-800")).append("\">")
           .append(esc(textOf(n)));
        children(n);
        out.append("</div>");
    }

    private void progress(WebNode n) {
        int value = Math.max(0, Math.min(100, Props.getInt(n.props, "value", 0)));
        out.append("<div class=\"").append(cls(n, "w-full bg-slate-200 rounded-full h-2.5")).append("\">")
           .append("<div class=\"bg-indigo-600 h-2.5 rounded-full transition-all\" style=\"width:")
           .append(value).append("%\"></div></div>");
    }

    private void spinner(WebNode n) {
        out.append("<div class=\"").append(cls(n,
                "inline-block h-6 w-6 animate-spin rounded-full border-2 border-slate-300 border-t-indigo-600")).append("\"></div>");
    }

    private void statCard(WebNode n) {
        out.append("<div class=\"").append(cls(n, "bg-white rounded-xl shadow-sm border border-slate-200 p-5")).append("\">")
           .append("<p class=\"text-xs uppercase tracking-wide text-slate-500\">")
           .append(esc(Props.getString(n.props, "label", ""))).append("</p>")
           .append("<p class=\"mt-1 text-3xl font-bold text-slate-900\">")
           .append(esc(Props.getString(n.props, "value", textOf(n)))).append("</p>");
        String delta = Props.getString(n.props, "delta", null);
        if (delta != null) {
            out.append("<p class=\"mt-1 text-xs font-medium text-emerald-600\">").append(esc(delta)).append("</p>");
        }
        out.append("</div>");
    }

    // ---- helpers ----

    /** Text of a node from its `text` prop or its first text child (widgets accept either). */
    private String textOf(WebNode n) {
        String t = n.textContent();
        if (!t.isEmpty()) return t;
        for (WebNode c : n.children) if (c.isTextLike()) return c.textContent();
        return "";
    }

    private String cls(WebNode n, String base) {
        // `className` (React-style) is the primary key — `class` is a reserved word, so it can only
        // be supplied as a string key (#{ "class": ... }); we accept both.
        String extra = Props.getString(n.props, "className", Props.getString(n.props, "class", ""));
        return extra.isEmpty() ? base : (base.isEmpty() ? extra : base + " " + extra);
    }

    /** Register an event handler function and emit its data attribute, if the prop is callable. */
    private String handlerAttr(WebNode n, String propName, String dataAttr) {
        Object fn = n.props.get(propName);
        if (fn == null || !runtime.isCallable(fn)) return "";
        String id = runtime.registerHandler(fn);
        return " " + dataAttr + "=\"" + id + "\"";
    }

    static String esc(String s) {
        if (s == null) return "";
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': b.append("&amp;"); break;
                case '<': b.append("&lt;"); break;
                case '>': b.append("&gt;"); break;
                case '"': b.append("&quot;"); break;
                case '\'': b.append("&#39;"); break;
                default: b.append(c);
            }
        }
        return b.toString();
    }
}
