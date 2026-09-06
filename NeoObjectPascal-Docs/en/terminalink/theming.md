# Theming in TerminalInk

TerminalInk has a **theming system** that lets you define default styles for components in a single place, instead of repeating color props on every call. A theme is a **record** that maps component names to sets of styles.

## Available colors

The colors used in props and themes are strings:

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## The theme functions

| Function                     | Role                                                                  |
| ---------------------------- | --------------------------------------------------------------------- |
| `defaultTheme()`             | Returns the default theme record.                                     |
| `extendTheme(base, override)`| Performs a **deep merge**: `override` wins over `base`.               |
| `setTheme(theme)`            | Sets the active global theme. Call it **before** `render`.           |
| `ThemeProvider(#{ theme }, [children])` | Sets the active theme and wraps the children.              |

### defaultTheme and extendTheme

Start from the default theme and override only what you want. A theme record maps component names to styles:

```npas
uses terminalink;

var t: Object;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1 }, [
        Spinner(#{ label: "Processing..." }),
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

Here the `Spinner` becomes magenta and the `Badge` becomes cyan **without** those colors appearing in the component calls — they come from the theme.

### setTheme vs. ThemeProvider

- `setTheme(t)` sets the global theme. It is the most direct approach: call it once, before `render`.
- `ThemeProvider(#{ theme: t }, [ ... ])` sets the active theme and wraps a subtree. It is also useful when you prefer to express the theme inside the UI tree itself:

```npas
function ui(): Object
begin
    return ThemeProvider(#{ theme: t }, [
        VBox(#{ padding: 1 }, [ Badge(#{}, "PRO") ])
    ]);
end;
```

::: warning The theme is global in immediate mode
In immediate mode, the active theme is **global** — it is not scoped per subtree. Even inside a `ThemeProvider`, the theme you set applies to the entire rendering of that frame. Treat the theme as an application setting, not a local style.
:::

## Full example: a form

This program builds a small registration form with visual validation, combining inputs and feedback.

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
        return StatusMessage(#{ variant: "success" }, "Registration sent!");
    return StatusMessage(#{ variant: "info" }, "Fill in and press Enter on the email.");
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round", borderColor: "cyan" }, [
        Text(#{ bold: true, color: "cyan" }, "Registration"),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "Name:"), TextInput(#{ placeholder: "Name...", onChange: onNome }) ]),
        HBox(#{ gap: 1 }, [ Text(#{ dim: true }, "Email:"), EmailInput(#{ placeholder: "email...", domains: ["gmail.com"], onChange: onEmail, onSubmit: aoEnviar }) ]),
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

Quit with **Esc** (there are input fields in focus, so avoid `q`).

## Full example: a dashboard

A monitoring panel that brings together `Spinner`, `ProgressBar`, and `Select`, with a theme applied.

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
        Spinner(#{ type: "dots", label: "Publishing artifacts..." }),
        ProgressBar(#{ value: progresso }),
        Text(#{ dim: true }, "Target environment:"),
        Select(#{ options: [
            #{ label: "Production", value: "prod" },
            #{ label: "Staging", value: "staging" },
            #{ label: "Development", value: "dev" }
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

::: tip Combine with the rest of the language
UI build functions are ordinary NeoObjectPascal functions. You can compose the tree with [functions](../language/functions), reuse logic, and even call [Java blocks](../features/java-integration) to fetch data. TerminalInk is just another library of the language.
:::

---

Go back to the start at [Introduction to TerminalInk](./introduction) or review all the [Components](./components).
