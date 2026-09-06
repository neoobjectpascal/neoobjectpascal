# WebInk — introduction

**WebInk** is the web frontend framework of NeoObjectPascal — the web counterpart of [TerminalInk](../terminalink/introduction). With `uses webink;` you build a **professional** web interface in NeoObjectPascal code, styled with **Tailwind CSS** and with charts via **Chart.js**, and you navigate between screens with real URLs.

Unlike a static generator, WebInk is a **live app**: `render(...)` starts a **local server** and opens the browser. State lives in variables; on every interaction (click, typing, submit) a callback runs on the server, changes the state, and the screen is redrawn — a **server-driven** UI, exactly like TerminalInk's reactive loop, but on the web.

::: warning TerminalInk and WebInk don't mix
A program is **entirely TerminalInk or entirely WebInk**. Using `uses terminalink` and `uses webink` in the same program raises a clear error.
:::

## First app

```npas
uses webink;

var cliques: Integer;

function registrar(): Boolean
begin
    cliques := cliques + 1;
    return true;
end;

function home(): Object
begin
    return Page(#{}, [
        Navbar(#{}, [
            Heading(#{ level: 3 }, "Acme Inc."),
            Link(#{ href: "/relatorio" }, "Relatório")
        ]),
        Container(#{ className: "py-8 space-y-6" }, [
            Heading(#{ level: 1 }, "Painel"),
            Grid(#{ cols: 3 }, [
                StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" }),
                StatCard(#{ label: "Usuários", value: "3.420" }),
                StatCard(#{ label: "Cliques", value: cliques })
            ]),
            Card(#{}, [
                Heading(#{ level: 4 }, "Vendas por mês"),
                Chart(#{ type: "line", data: #{
                    labels: ["Jan", "Fev", "Mar", "Abr"],
                    datasets: [ #{ label: "R$ mil", data: [30, 45, 38, 52] } ]
                } })
            ]),
            Button(#{ onClick: registrar }, "Registrar clique")
        ])
    ]);
end;

begin
    cliques := 0;
    render(#{ "/": home });
end.
```

Run it and the browser opens with the dashboard; clicking the button increments the counter live.

## How it works

1. `render(routes)` starts a **single-thread** local HTTP server and opens the browser.
2. The browser loads a shell with Tailwind + Chart.js (bundled, offline) and a small JS runtime.
3. The runtime asks the server for the current route's screen; the server calls the build function, generates the HTML, and returns it.
4. On an interaction, the runtime sends the event to the server, which calls the **NeoObjectPascal callback**, updates the state, redraws the screen, and returns the new HTML.

State lives in global variables, just like in TerminalInk. Callbacks change the state; the current screen function re-reads it and redraws.

## State and events

Interactive widgets receive callbacks via props: `onClick`, `onChange`, `onSubmit`. The callback is a function that changes the state and returns:

```npas
var nome: String;

function onNome(v): Boolean
begin
    nome := v;
    return true;
end;

// ...
TextInput(#{ placeholder: "Seu nome", value: nome, onChange: onNome })
```

::: tip Props and reserved words
`class` and `to` are reserved words — that's why widgets use **`className`** (React style) and **`href`** (on `Link`). Any other "reserved" key can be passed as a string: `#{ "class": "..." }`.
:::

## Run, build, and debug

- **Run:** `java -jar neoobjectpascal.jar app.npas` starts the server and opens the browser; stop it with **Ctrl+C**. In VS Code, use **Run**.
- **Build native:** `--build` packages the app (with the bundled assets) — the self-contained executable starts the server and opens the browser, **offline**. See [Building native executables](../testing/building-executables).
- **Debug:** WebInk has the **best debugging experience**. The server is single-thread and the browser is just a client — no terminal Swing window. Set a breakpoint in a screen function or a callback: it fires when you interact in the browser, the pause is deterministic, and **Step Into (F7)** steps into your functions normally. See [Debugger, VS Code, and cloud](../testing/debugging-tools).

## Next steps

Get to know the [WebInk components](./components) — layout, typography, forms, tables, Chart.js charts, and navigation with routes.
