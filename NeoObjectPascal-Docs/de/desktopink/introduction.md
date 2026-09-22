# DesktopInk — Einführung

**DesktopInk** ist das native Desktop-Anwendungs-Framework von NeoObjectPascal. Mit `uses desktopink;` erstellen Sie native Fenster mit deklarativen visuellen Komponenten, hellem/dunklem Design und demselben reaktiven Modell wie die anderen Laufzeiten.

Anders als TerminalInk (Terminal) und WebInk (Webserver) öffnet DesktopInk ein **echtes Swing/Java2D-Fenster** — ohne externe Abhängigkeiten. Jede Komponente wird vom Framework mit abgerundeten Ecken, Systemtypografie und einem professionellen, von [shadcn/ui](https://ui.shadcn.com) inspirierten Erscheinungsbild gezeichnet.

```npas
uses desktopink;

function haupt(): Object
begin
    return Window(#{ padding: 24 }, [
        Heading(#{ level: 1, text: "Hallo, DesktopInk!" }),
        Text(#{}, "Dies ist ein natives Fenster.")
    ]);
end;

begin
    render(#{ haupt: haupt }, #{
        title: "Meine App",
        width: 800,
        height: 600,
        centered: true,
        theme: "auto"
    });
end.
```

:::tip TerminalInk, WebInk und DesktopInk mischen sich nicht
Ein Programm ist **entweder vollständig TerminalInk, WebInk oder DesktopInk**. Die Verwendung zweier verschiedener UI-`uses` im selben Programm erzeugt einen klaren Fehler.
:::

## Optionen für render

Das zweite Argument von `render` akzeptiert diese Optionen:

| Option     | Typ     | Standard | Beschreibung                          |
|------------|---------|----------|---------------------------------------|
| `title`    | String  | App-Name | Fenstertitel                          |
| `width`    | Integer | 960      | Anfangsbreite                         |
| `height`   | Integer | 720      | Anfangshöhe                           |
| `centered` | Boolean | `true`   | Fenster zentrieren                    |
| `maximized`| Boolean | `false`  | Fenster maximiert öffnen              |
| `theme`    | String  | `"auto"` | `"light"`, `"dark"` oder `"auto"` (System) |

## Design

DesktopInk bietet zwei vollständige Designs — hell und dunkel — plus den Modus `auto`, der der Systemeinstellung folgt. Die visuellen Tokens werden mit WebInk geteilt, sodass beide Laufzeiten wie dasselbe Produkt aussehen.

Wechseln Sie das Design mit:

```npas
setTheme("dark");
setTheme("light");
setTheme("auto");
```

## Komponenten

DesktopInk bietet über 25 Komponenten, von Layout bis zu Formularen, Tabellen, Diagrammen und Modalen. Siehe die [vollständige Komponentenliste](./components).

## Nächste Schritte

Erkunden Sie alle [DesktopInk-Komponenten](./components).