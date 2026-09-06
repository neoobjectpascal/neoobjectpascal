# WebInk — Einführung

**WebInk** ist das Web-Frontend-Framework von NeoObjectPascal — das Web-Pendant zu [TerminalInk](../terminalink/introduction). Mit `uses webink;` bauen Sie eine **professionelle** Web-Oberfläche in NeoObjectPascal-Code, gestylt mit **Tailwind CSS** und mit Diagrammen über **Chart.js**, und navigieren mit echten URLs zwischen den Bildschirmen.

Anders als ein statischer Generator ist WebInk eine **lebendige App**: `render(...)` startet einen **lokalen Server** und öffnet den Browser. Der Zustand lebt in den Variablen; bei jeder Interaktion (Klick, Tippen, Absenden) läuft ein Callback auf dem Server, ändert den Zustand und der Bildschirm wird neu gezeichnet — eine **servergesteuerte** Oberfläche, genau wie die reaktive Schleife von TerminalInk, aber im Web.

::: warning TerminalInk und WebInk lassen sich nicht mischen
Ein Programm ist **entweder ganz TerminalInk oder ganz WebInk**. `uses terminalink` und `uses webink` im selben Programm zu verwenden, erzeugt einen eindeutigen Fehler.
:::

## Erste App

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

Führen Sie es aus, und der Browser öffnet sich mit dem Dashboard; ein Klick auf den Button erhöht den Zähler live.

## Wie es funktioniert

1. `render(rotas)` startet einen lokalen **single-thread** HTTP-Server und öffnet den Browser.
2. Der Browser lädt eine Hülle mit Tailwind + Chart.js (eingebettet, offline) und eine kleine JS-Laufzeit.
3. Die Laufzeit fordert vom Server den Bildschirm der aktuellen Route an; der Server ruft die Build-Funktion auf, erzeugt das HTML und gibt es zurück.
4. Bei einer Interaktion sendet die Laufzeit das Ereignis an den Server, der den **NeoObjectPascal-Callback** aufruft, den Zustand aktualisiert, den Bildschirm neu zeichnet und das neue HTML zurückgibt.

Der Zustand lebt in globalen Variablen, wie in TerminalInk. Die Callbacks ändern den Zustand; die Funktion des aktuellen Bildschirms liest ihn erneut und zeichnet neu.

## Zustand und Ereignisse

Interaktive Widgets erhalten Callbacks über Props: `onClick`, `onChange`, `onSubmit`. Der Callback ist eine Funktion, die den Zustand ändert und zurückgibt:

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

::: tip Props und reservierte Wörter
`class` und `to` sind reservierte Wörter — deshalb verwenden die Widgets **`className`** (im React-Stil) und **`href`** (im `Link`). Jeder andere „reservierte" Schlüssel kann als String übergeben werden: `#{ "class": "..." }`.
:::

## Ausführen, bauen und debuggen

- **Ausführen:** `java -jar neoobjectpascal.jar app.npas` startet den Server und öffnet den Browser; beenden mit **Ctrl+C**. In VS Code verwenden Sie **Run**.
- **Nativ bauen:** `--build` packt die App (mit den eingebetteten Assets) — die eigenständige ausführbare Datei startet den Server und öffnet den Browser, **offline**. Siehe [Native ausführbare Dateien erzeugen](../testing/building-executables).
- **Debuggen:** WebInk bietet die **beste Debug-Erfahrung**. Der Server ist single-thread und der Browser ist nur ein Client — ohne das Swing-Fenster des Terminals. Setzen Sie einen Breakpoint in eine Bildschirmfunktion oder einen Callback: Er löst aus, wenn Sie im Browser interagieren, die Pause ist deterministisch, und **Step Into (F7)** springt ganz normal in Ihre Funktionen. Siehe [Debugger, VS Code und Cloud](../testing/debugging-tools).

## Nächste Schritte

Lernen Sie die [WebInk-Komponenten](./components) kennen — Layout, Typografie, Formulare, Tabellen, Chart.js-Diagramme und Navigation mit Routen.
