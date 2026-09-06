# WebInk — Komponenten

Jedes Widget akzeptiert als erstes Argument ein Props-Record `#{}` und danach Kinder (weitere Knoten) und/oder Text. Alle akzeptieren `className`, um Tailwind-Klassen hinzuzufügen.

## Layout

| Widget | Beschreibung |
|--------|-----------|
| `Page` | Hülle der Seite (Hintergrund, Textfarbe) |
| `Section` | Abschnitt mit vertikalem Abstand |
| `Container` | zentrierte maximale Breite mit Padding |
| `Grid(#{ cols })` | responsives Raster mit `cols` Spalten |
| `Row` / `Col` | Flex-Zeile und flexible Spalte |
| `Card` | Karte mit Rahmen, Schatten und Padding |
| `Divider` · `Spacer` | Trenner · vertikaler Abstand |

```npas
Grid(#{ cols: 3 }, [
    Card(#{}, [ Heading(#{ level: 4 }, "A"), Text(#{}, "conteúdo") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "B") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "C") ])
])
```

## Typografie

| Widget | Beschreibung |
|--------|-----------|
| `Heading(#{ level: 1..6 })` | Überschrift (h1–h6) |
| `Text` | Absatz |
| `Badge(#{ color })` | farbiges Abzeichen |
| `StatCard(#{ label, value, delta })` | KPI-Karte (Bezeichnung, Wert, Veränderung) |

```npas
StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" })
```

## Navigation

| Widget | Beschreibung |
|--------|-----------|
| `Navbar` | obere Navigationsleiste |
| `Sidebar` | Seitenleiste |
| `Link(#{ href })` | Navigationslink (Route) |
| `Tabs` | Registerkartenleiste |

## Formulare

| Widget | Wichtigste Props |
|--------|------------------|
| `Button(#{ variant, onClick })` | `variant`: `primary` \| `secondary` \| `danger` |
| `TextInput(#{ placeholder, value, onChange, onSubmit })` | `onSubmit` löst bei Enter aus |
| `TextArea(#{ rows, value, onChange })` | Textbereich |
| `Select(#{ options, onChange })` | `options`: Array von Werten |
| `Checkbox(#{ label, checked, onChange })` | Kontrollkästchen |
| `Form(#{ onSubmit })` | gruppiert Felder; sendet beim Absenden |

```npas
Form(#{ onSubmit: enviar }, [
    TextInput(#{ placeholder: "E-mail", onChange: onEmail }),
    Button(#{ onClick: enviar }, "Entrar")
])
```

## Daten und Feedback

| Widget | Beschreibung |
|--------|-----------|
| `Table(#{ columns, rows })` | Tabelle; `columns` und `rows` sind Arrays |
| `List(#{ items, ordered })` | Liste mit Aufzählungszeichen oder nummeriert |
| `Chart(#{ type, data, options })` | Chart.js-Diagramm |
| `Alert(#{ variant })` | Hinweis (`info`/`success`/`warning`/`error`) |
| `ProgressBar(#{ value })` | Fortschrittsbalken (0–100) |
| `Spinner` | Ladeindikator |

```npas
Table(#{
    columns: ["Produto", "Qtd", "Total"],
    rows: [ ["Café", "120", "R$ 600"], ["Chá", "80", "R$ 320"] ]
})
```

## Diagramme (Chart.js)

Das Widget `Chart` bildet direkt auf die Konfiguration von [Chart.js](https://www.chartjs.org) ab: `type`, `data` und `options` sind Records `#{}`, die zum JSON des Diagramms werden.

```npas
Chart(#{
    type: "bar",
    data: #{
        labels: ["Jan", "Fev", "Mar"],
        datasets: [
            #{ label: "Vendas", data: [30, 45, 38] },
            #{ label: "Meta",   data: [40, 40, 40] }
        ]
    },
    options: #{ plugins: #{ legend: #{ position: "bottom" } } }
})
```

Unterstützte Typen: `bar`, `line`, `pie`, `doughnut`, `radar` und die übrigen von Chart.js. Die Diagramme werden bei jedem Neuzeichnen neu instanziiert.

## Navigation mit Routen

`render` erhält eine Zuordnung von Routen (URL → Bildschirmfunktion). Die URL des Browsers spiegelt den aktuellen Bildschirm wider, und die **Zurück**-Schaltfläche sowie die **Deep Links** funktionieren.

```npas
function home(): Object      begin return Page(#{}, [ /* ... */ ]); end;
function dashboard(): Object begin return Page(#{}, [ /* ... */ ]); end;

begin
    render(#{
        "/":          home,
        "/dashboard": dashboard
    });
end.
```

Zwei Wege zu navigieren:

- **`Link(#{ href: "/dashboard" }, "Ir")`** — deklarativer Link (`<a>`), im Client mit `pushState` abgefangen.
- **`navigate("/dashboard")`** — innerhalb eines Callbacks ändert es die Route programmatisch (der Server benachrichtigt den Browser).

```npas
function irParaPainel(): Boolean
begin
    navigate("/dashboard");
    return true;
end;
```

::: tip Routenschlüssel sind Strings
Die Schlüssel der Routenzuordnung sind Strings (`"/"`, `"/dashboard"`) — die Record-Literale akzeptieren neben Bezeichnern auch String-Schlüssel.
:::

## Nächste Schritte

Um Ihre App als native ausführbare Datei zu verteilen, siehe [Native ausführbare Dateien erzeugen](../testing/building-executables). Für die vollständige Sprachsyntax siehe die [Sprachreferenz](../reference/language-reference).
