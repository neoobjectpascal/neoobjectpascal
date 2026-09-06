# Temi in TerminalInk

TerminalInk ha un **sistema di temi** che permette di definire stili predefiniti per i componenti in un unico posto, invece di ripetere le props di colore in ogni chiamata. Un tema è un **record** che associa i nomi dei componenti a insiemi di stili.

## Colori disponibili

I colori usati nelle props e nei temi sono stringhe:

`red`, `green`, `yellow`, `blue`, `magenta`, `cyan`, `white`, `black`, `gray`, `default`.

## Le funzioni di tema

| Funzione                     | Ruolo                                                                 |
| ---------------------------- | --------------------------------------------------------------------- |
| `defaultTheme()`             | Restituisce il record del tema predefinito.                          |
| `extendTheme(base, override)`| Esegue una **fusione profonda**: `override` prevale su `base`.        |
| `setTheme(theme)`            | Imposta il tema globale attivo. Chiamala **prima** di `render`.       |
| `ThemeProvider(#{ theme }, [figli])` | Imposta il tema attivo e avvolge i figli.                    |

### defaultTheme ed extendTheme

Parti dal tema predefinito e sovrascrivi solo ciò che desideri. Un record di tema associa i nomi dei componenti agli stili:

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

Qui lo `Spinner` diventa magenta e il `Badge` diventa ciano **senza** che questi colori compaiano nelle chiamate dei componenti — provengono dal tema.

### setTheme vs. ThemeProvider

- `setTheme(t)` imposta il tema globale. È il modo più diretto: chiamalo una volta, prima di `render`.
- `ThemeProvider(#{ theme: t }, [ ... ])` imposta il tema attivo e avvolge un sottoalbero. È utile anche quando preferisci esprimere il tema all'interno dell'albero della UI stesso:

```npas
function ui(): Object
begin
    return ThemeProvider(#{ theme: t }, [
        VBox(#{ padding: 1 }, [ Badge(#{}, "PRO") ])
    ]);
end;
```

::: warning Il tema è globale in modalità immediata
In modalità immediata, il tema attivo è **globale** — non è isolato per sottoalbero. Anche all'interno di un `ThemeProvider`, il tema definito vale per l'intera renderizzazione di quel fotogramma. Tratta il tema come una configurazione dell'applicazione, non come uno stile locale.
:::

## Esempio completo: un modulo

Questo programma costruisce un piccolo modulo di registrazione con validazione visiva, combinando input e feedback.

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

Esci con **Esc** (ci sono campi di input a fuoco, quindi evita `q`).

## Esempio completo: una dashboard

Un pannello di monitoraggio che riunisce `Spinner`, `ProgressBar` e `Select`, con un tema applicato.

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

::: tip Combinalo con il resto del linguaggio
Le funzioni di costruzione della UI sono normali funzioni di NeoObjectPascal. Puoi comporre l'albero con [funzioni](../language/functions), riutilizzare la logica e persino chiamare [blocchi Java](../features/java-integration) per ottenere dati. TerminalInk è solo un'altra libreria del linguaggio.
:::

---

Torna all'inizio in [Introduzione a TerminalInk](./introduction) o rivedi tutti i [Componenti](./components).
