# WebInk — components

Every widget takes a props record `#{}` as its first argument and, after it, children (other nodes) and/or text. All of them accept `className` to add Tailwind classes.

## Layout

| Widget | Description |
|--------|-----------|
| `Page` | page shell (background, text color) |
| `Section` | section with vertical spacing |
| `Container` | centered max-width with padding |
| `Grid(#{ cols })` | responsive grid of `cols` columns |
| `Row` / `Col` | flex row and flexible column |
| `Card` | card with border, shadow, and padding |
| `Divider` · `Spacer` | separator · vertical space |

```npas
Grid(#{ cols: 3 }, [
    Card(#{}, [ Heading(#{ level: 4 }, "A"), Text(#{}, "conteúdo") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "B") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "C") ])
])
```

## Typography

| Widget | Description |
|--------|-----------|
| `Heading(#{ level: 1..6 })` | title (h1–h6) |
| `Text` | paragraph |
| `Badge(#{ color })` | colored badge |
| `StatCard(#{ label, value, delta })` | KPI card (label, value, change) |

```npas
StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" })
```

## Navigation

| Widget | Description |
|--------|-----------|
| `Navbar` | top navigation bar |
| `Sidebar` | side bar |
| `Link(#{ href })` | navigation link (route) |
| `Tabs` | tab strip |

## Forms

| Widget | Main props |
|--------|------------------|
| `Button(#{ variant, onClick })` | `variant`: `primary` \| `secondary` \| `danger` |
| `TextInput(#{ placeholder, value, onChange, onSubmit })` | `onSubmit` fires on Enter |
| `TextArea(#{ rows, value, onChange })` | text area |
| `Select(#{ options, onChange })` | `options`: array of values |
| `Checkbox(#{ label, checked, onChange })` | checkbox |
| `Form(#{ onSubmit })` | groups fields; submits on submit |

```npas
Form(#{ onSubmit: enviar }, [
    TextInput(#{ placeholder: "E-mail", onChange: onEmail }),
    Button(#{ onClick: enviar }, "Entrar")
])
```

## Data and feedback

| Widget | Description |
|--------|-----------|
| `Table(#{ columns, rows })` | table; `columns` and `rows` are arrays |
| `List(#{ items, ordered })` | bulleted or numbered list |
| `Chart(#{ type, data, options })` | Chart.js chart |
| `Alert(#{ variant })` | alert (`info`/`success`/`warning`/`error`) |
| `ProgressBar(#{ value })` | progress bar (0–100) |
| `Spinner` | loading indicator |

```npas
Table(#{
    columns: ["Produto", "Qtd", "Total"],
    rows: [ ["Café", "120", "R$ 600"], ["Chá", "80", "R$ 320"] ]
})
```

## Charts (Chart.js)

The `Chart` widget maps directly to the [Chart.js](https://www.chartjs.org) configuration: `type`, `data`, and `options` are `#{}` records that become the chart's JSON.

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

Supported types: `bar`, `line`, `pie`, `doughnut`, `radar`, and the rest of Chart.js. Charts are re-instantiated on every redraw.

## Navigation with routes

`render` takes a map of routes (URL → screen function). The browser URL reflects the current screen, and the **Back** button and **deep links** work.

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

Two ways to navigate:

- **`Link(#{ href: "/dashboard" }, "Ir")`** — declarative link (`<a>`), intercepted on the client with `pushState`.
- **`navigate("/dashboard")`** — inside a callback, changes the route programmatically (the server notifies the browser).

```npas
function irParaPainel(): Boolean
begin
    navigate("/dashboard");
    return true;
end;
```

::: tip Route keys are strings
The keys of the route map are strings (`"/"`, `"/dashboard"`) — record literals accept string keys in addition to identifiers.
:::

## Next steps

To distribute your app as a native executable, see [Building native executables](../testing/building-executables). For the complete language syntax, see the [Language reference](../reference/language-reference).
