# WebInk — introduzione

**WebInk** è il framework per il frontend web di NeoObjectPascal — l'analogo web di [TerminalInk](../terminalink/introduction). Con `uses webink;` costruisci un'interfaccia web **professionale** in codice NeoObjectPascal, stilizzata con **Tailwind CSS** e con grafici tramite **Chart.js**, e navighi tra le schermate con URL reali.

A differenza di un generatore statico, WebInk è un **app viva**: `render(...)` avvia un **server locale** e apre il browser. Lo stato vive nelle variabili; a ogni interazione (clic, digitazione, invio) un callback gira sul server, cambia lo stato e la schermata viene ridisegnata — una UI **guidata dal server**, esattamente come il ciclo reattivo di TerminalInk, ma sul web.

::: warning TerminalInk e WebInk non si mescolano
Un programma è **interamente TerminalInk o interamente WebInk**. Usare `uses terminalink` e `uses webink` nello stesso programma genera un errore chiaro.
:::

## Prima app

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

Esegui e il browser si apre con il pannello; cliccare sul pulsante incrementa il contatore in tempo reale.

## Come funziona

1. `render(rotte)` avvia un server HTTP locale **single-thread** e apre il browser.
2. Il browser carica uno shell con Tailwind + Chart.js (incorporati, offline) e un piccolo runtime JS.
3. Il runtime chiede al server la schermata della rotta corrente; il server chiama la funzione di build, genera l'HTML e lo restituisce.
4. A ogni interazione, il runtime invia l'evento al server, che chiama il **callback NeoObjectPascal**, aggiorna lo stato, ridisegna la schermata e restituisce il nuovo HTML.

Lo stato vive in variabili globali, come in TerminalInk. I callback cambiano lo stato; la funzione della schermata corrente rilegge e ridisegna.

## Stato ed eventi

I widget interattivi ricevono i callback tramite le props: `onClick`, `onChange`, `onSubmit`. Il callback è una funzione che cambia lo stato e restituisce:

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

::: tip Props e parole riservate
`class` e `to` sono parole riservate — per questo i widget usano **`className`** (stile React) e **`href`** (nel `Link`). Qualsiasi altra chiave "riservata" può essere passata come stringa: `#{ "class": "..." }`.
:::

## Eseguire, compilare e fare debug

- **Eseguire:** `java -jar neoobjectpascal.jar app.npas` avvia il server e apre il browser; si termina con **Ctrl+C**. In VS Code, usa **Run**.
- **Compilare in nativo:** `--build` impacchetta l'app (con gli asset incorporati) — l'eseguibile autocontenuto avvia il server e apre il browser, **offline**. Vedi [Generare eseguibili nativi](../testing/building-executables).
- **Debug:** WebInk offre la **migliore esperienza di debug**. Il server è single-thread e il browser è solo un client — senza la finestra Swing del terminale. Metti un breakpoint in una funzione di schermata o in un callback: scatta quando interagisci nel browser, la pausa è deterministica e lo **Step Into (F7)** entra nelle tue funzioni normalmente. Vedi [Debugger, VS Code e cloud](../testing/debugging-tools).

## Prossimi passi

Scopri i [componenti di WebInk](./components) — layout, tipografia, form, tabelle, grafici Chart.js e navigazione con rotte.
