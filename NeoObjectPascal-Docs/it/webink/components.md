# WebInk — componenti

Ogni widget accetta un record di props `#{}` come primo argomento e, dopo, i figli (altri nodi) e/o testo. Tutti accettano `className` per aggiungere classi Tailwind.

## Layout

| Widget | Descrizione |
|--------|-------------|
| `Page` | guscio della pagina (sfondo, colore del testo) |
| `Section` | sezione con spaziatura verticale |
| `Container` | larghezza massima centrata con padding |
| `Grid(#{ cols })` | griglia responsiva di `cols` colonne |
| `Row` / `Col` | riga flex e colonna flessibile |
| `Card` | scheda con bordo, ombra e padding |
| `Divider` · `Spacer` | separatore · spazio verticale |

```npas
Grid(#{ cols: 3 }, [
    Card(#{}, [ Heading(#{ level: 4 }, "A"), Text(#{}, "conteúdo") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "B") ]),
    Card(#{}, [ Heading(#{ level: 4 }, "C") ])
])
```

## Tipografia

| Widget | Descrizione |
|--------|-------------|
| `Heading(#{ level: 1..6 })` | titolo (h1–h6) |
| `Text` | paragrafo |
| `Badge(#{ color })` | badge colorato |
| `StatCard(#{ label, value, delta })` | scheda KPI (etichetta, valore, variazione) |

```npas
StatCard(#{ label: "Receita", value: "R$ 128k", delta: "+12%" })
```

## Navigazione

| Widget | Descrizione |
|--------|-------------|
| `Navbar` | barra di navigazione superiore |
| `Sidebar` | barra laterale |
| `Link(#{ href })` | link di navigazione (rotta) |
| `Tabs` | fascia di schede |

## Form

| Widget | Props principali |
|--------|------------------|
| `Button(#{ variant, onClick })` | `variant`: `primary` \| `secondary` \| `danger` |
| `TextInput(#{ placeholder, value, onChange, onSubmit })` | `onSubmit` scatta con Invio |
| `TextArea(#{ rows, value, onChange })` | area di testo |
| `Select(#{ options, onChange })` | `options`: array di valori |
| `Checkbox(#{ label, checked, onChange })` | casella di selezione |
| `Form(#{ onSubmit })` | raggruppa i campi; invia al submit |

```npas
Form(#{ onSubmit: enviar }, [
    TextInput(#{ placeholder: "E-mail", onChange: onEmail }),
    Button(#{ onClick: enviar }, "Entrar")
])
```

## Dati e feedback

| Widget | Descrizione |
|--------|-------------|
| `Table(#{ columns, rows })` | tabella; `columns` e `rows` sono array |
| `List(#{ items, ordered })` | lista con elenco puntato o numerata |
| `Chart(#{ type, data, options })` | grafico Chart.js |
| `Alert(#{ variant })` | avviso (`info`/`success`/`warning`/`error`) |
| `ProgressBar(#{ value })` | barra di avanzamento (0–100) |
| `Spinner` | indicatore di caricamento |

```npas
Table(#{
    columns: ["Produto", "Qtd", "Total"],
    rows: [ ["Café", "120", "R$ 600"], ["Chá", "80", "R$ 320"] ]
})
```

## Grafici (Chart.js)

Il widget `Chart` si mappa direttamente sulla configurazione di [Chart.js](https://www.chartjs.org): `type`, `data` e `options` sono record `#{}` che diventano il JSON del grafico.

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

Tipi supportati: `bar`, `line`, `pie`, `doughnut`, `radar`, e gli altri di Chart.js. I grafici vengono reistanziati a ogni ridisegno.

## Navigazione con rotte

`render` riceve una mappa di rotte (URL → funzione di schermata). L'URL del browser riflette la schermata corrente, e il pulsante **Voltar** e i **deep link** funzionano.

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

Due modi per navigare:

- **`Link(#{ href: "/dashboard" }, "Ir")`** — link dichiarativo (`<a>`), intercettato sul client con `pushState`.
- **`navigate("/dashboard")`** — dentro un callback, cambia la rotta in modo programmatico (il server avvisa il browser).

```npas
function irParaPainel(): Boolean
begin
    navigate("/dashboard");
    return true;
end;
```

::: tip Le chiavi di rotta sono stringhe
Le chiavi della mappa di rotte sono stringhe (`"/"`, `"/dashboard"`) — i record literal accettano chiavi stringa oltre agli identificatori.
:::

## Prossimi passi

Per distribuire la tua app come eseguibile nativo, vedi [Generare eseguibili nativi](../testing/building-executables). Per la sintassi completa del linguaggio, vedi il [Riferimento del linguaggio](../reference/language-reference).
