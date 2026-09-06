# Themes in TerminalInk

TerminalInk verfügt über ein **Theme-System**, mit dem Sie Standardstile für die Komponenten an einer einzigen Stelle festlegen können, statt Farb-Props bei jedem Aufruf zu wiederholen. Ein Theme ist ein **Record**, der Komponentennamen auf Gruppen von Stilen abbildet.

## Verfügbare Farben

Die in Props und Themes verwendeten Farben sind Strings:

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Die Theme-Funktionen

| Funktion                     | Rolle                                                                 |
| ---------------------------- | --------------------------------------------------------------------- |
| `defaultTheme()`             | Gibt den Record des Standard-Themes zurück.                           |
| `extendTheme(base, override)`| Führt eine **tiefe Zusammenführung** durch: `override` gewinnt über `base`. |
| `setTheme(theme)`            | Legt das global aktive Theme fest. Rufen Sie es **vor** `render` auf. |
| `ThemeProvider(#{ theme }, [filhos])` | Legt das aktive Theme fest und umschließt die Kinder.        |

### defaultTheme und extendTheme

Beginnen Sie beim Standard-Theme und überschreiben Sie nur das, was Sie möchten. Ein Theme-Record bildet Komponentennamen auf Stile ab:

```npas
uses terminalink;

var t: Object;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1 }, [
        Spinner(#{ label: "Processando..." }),
        Badge(#{}, "PRO")
    ]);
end;

begin
    t := extendTheme(defaultTheme(), #{
        Spinner: #{ color: "magenta" },
        Badge: #{ color: "cyan" }
    });
    setTheme(t);
    render(ui);
end.
```

Hier wird der `Spinner` magenta und das `Badge` cyan — **ohne** dass diese Farben in den Aufrufen der Komponenten erscheinen; sie stammen aus dem Theme.

### setTheme vs. ThemeProvider

- `setTheme(t)` legt das globale Theme fest. Das ist der direkteste Weg: einmal aufrufen, vor `render`.
- `ThemeProvider(#{ theme: t }, [ ... ])` legt das aktive Theme fest und umschließt einen Teilbaum. Es ist auch nützlich, wenn Sie das Theme lieber innerhalb des UI-Baums selbst ausdrücken möchten:

```npas
function ui(): Object
begin
    return ThemeProvider(#{ theme: t }, [
        VBox(#{ padding: 1 }, [ Badge(#{}, "PRO") ])
    ]);
end;
```

::: warning Das Theme ist im Immediate Mode global
Im Immediate Mode ist das aktive Theme **global** — es ist nicht pro Teilbaum isoliert. Selbst innerhalb eines `ThemeProvider` gilt das festgelegte Theme für das gesamte Rendering jenes Frames. Behandeln Sie das Theme als eine Anwendungskonfiguration, nicht als einen lokalen Stil.
:::

## Vollständiges Beispiel: ein Formular

Dieses Programm baut ein kleines Registrierungsformular mit visueller Validierung auf und kombiniert Eingaben und Feedback.

```npas
uses terminalink;

var nome: String;
var email: String;
var enviado: Boolean;

function onNome(v): Boolean begin nome := v; return true; end;
function onEmail(v): Boolean begin email := v; return true; end;
function aoEnviar(v): Boolean begin enviado := true; return true; end;

function statusEmail(): Object
begin
    if enviado then
        return StatusMessage(#{ variant: "success" }, "Cadastro enviado!");
    return StatusMessage(#{ variant: "info" }, "Preencha e pressione Enter no e-mail.");
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "cyan" }, [
        Text(#{ bold: true, color: "cyan" }, "Cadastro"),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "Nome:"), TextInput(#{ placeholder: "Nome...", onChange: onNome }) ]),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "E-mail:"), EmailInput(#{ placeholder: "email...", domains: ["gmail.com"], onChange: onEmail, onSubmit: aoEnviar }) ]),
        Text(#{}, "Olá, " + nome),
        statusEmail()
    ]);
end;

begin
    enviado := false;
    setTheme(extendTheme(defaultTheme(), #{ StatusMessage: #{ } }));
    render(ui);
end.
```

Mit **Esc** beenden (es sind Eingabefelder im Fokus, also vermeiden Sie `q`).

## Vollständiges Beispiel: ein Dashboard

Ein Überwachungspanel, das `Spinner`, `ProgressBar` und `Select` mit einem angewandten Theme vereint.

```npas
uses terminalink;

var progresso: Integer;
var ambiente: String;

function onAmbiente(v): Boolean begin ambiente := v; return true; end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "blue" }, [
        HBox(#{ gap: 2 }, [
            Text(#{ bold: true }, "Deploy"),
            Spacer(),
            Badge(#{ color: "green" }, ambiente)
        ]),
        Spinner(#{ type: "dots", label: "Publicando artefatos..." }),
        ProgressBar(#{ value: progresso }),
        Text(#{ dim: true }, "Ambiente de destino:"),
        Select(#{ options: [
            #{ label: "Produção", value: "prod" },
            #{ label: "Homologação", value: "staging" },
            #{ label: "Desenvolvimento", value: "dev" }
        ], onChange: onAmbiente, visibleCount: 3 })
    ]);
end;

begin
    progresso := 65;
    ambiente := "prod";
    setTheme(extendTheme(defaultTheme(), #{
        Spinner: #{ color: "cyan" },
        ProgressBar: #{ color: "green" },
        Badge: #{ color: "green" }
    }));
    render(ui);
end.
```

::: tip Kombinieren Sie es mit dem Rest der Sprache
Die UI-Konstruktionsfunktionen sind ganz normale Funktionen von NeoObjectPascal. Sie können den Baum mit [Funktionen](../language/functions) zusammensetzen, Logik wiederverwenden und sogar [Java-Blöcke](../features/java-integration) aufrufen, um Daten zu beziehen. TerminalInk ist nur eine weitere Bibliothek der Sprache.
:::

---

Kehren Sie zum Anfang unter [Einführung in TerminalInk](./introduction) zurück oder sehen Sie sich noch einmal alle [Komponenten](./components) an.
