# Introduzione a TerminalInk

**TerminalInk** è il framework per le interfacce di terminale di NeoObjectPascal. Ispirato a [React Ink](https://github.com/vadimdemedes/ink), ti permette di costruire applicazioni di terminale (TUI) in modo **dichiarativo**: descrivi come deve apparire lo schermo e il framework si occupa di disegnarlo, ridisegnarlo e decodificare la tastiera.

Dietro le quinte, TerminalInk si appoggia alla libreria Java [Lanterna](https://github.com/mabe02/lanterna), che offre un terminale multipiattaforma, uno schermo con buffer e la decodifica dei tasti. Non devi mai intervenire direttamente su tutto questo: ti basta scrivere componenti.

## Abilitare il modulo

Per usare TerminalInk, dichiara il modulo nella clausola `uses`:

```npas
uses terminalink;
```

Questo rende disponibili tutti i componenti (`Text`, `VBox`, `TextInput`, `Spinner`, ...) e le funzioni di rendering e di tema.

## Il modello di rendering (modalità immediata)

TerminalInk lavora in **modalità immediata**. Invece di costruire l'albero dei componenti una volta sola, scrivi una **funzione di costruzione** che **restituisce** l'albero dell'interfaccia. La funzione `render` riceve questa funzione e la chiama ripetutamente — a ogni fotogramma (~60 ms) — per ridisegnare lo schermo.

Lo stato dell'applicazione vive in **variabili normali** di NeoObjectPascal. I *callback* degli eventi (passati tramite il nome della funzione) modificano queste variabili e, nel fotogramma successivo, l'interfaccia viene ricostruita riflettendo già il nuovo stato.

```npas
uses terminalink;

var nome: String;

function onNome(v): Boolean
begin
    nome := v;
    return true;
end;

function ui(): Object
begin
    return VBox(#{ gap: 1, padding: 1, border: "round" }, [
        Text(#{ bold: true, color: "cyan" }, "Cadastro"),
        TextInput(#{ placeholder: "Nome...", onChange: onNome }),
        Text(#{}, "Olá, " + nome)
    ]);
end;

begin
    render(ui);
end.
```

Questo programma disegna un riquadro con titolo, un campo di testo e un saluto. Ogni tasto premuto attiva `onNome`, che aggiorna la variabile `nome`; nel fotogramma successivo, la riga "Olá, ..." appare aggiornata.

::: tip Il ciclo in una frase
`stato` (variabili) → `ui()` restituisce l'albero → `render` disegna → l'utente interagisce → il *callback* cambia lo `stato` → si ripete.
:::

## Props e literal di record

Ogni componente riceve le sue **props** come **literal di record** — la sintassi `#{ chiave: valore, ... }`:

```npas
Text(#{ bold: true, color: "yellow" }, "Atenção")
```

I record sono una funzionalità del linguaggio stesso. Leggi un campo con `record.campo`, il che vale anche per i valori che arrivano nei *callback*. Vedi [Variabili e tipi](../language/variables-and-types) per saperne di più sui record.

## Callback: funzioni passate per nome

I gestori di evento (`onChange`, `onSubmit`, `onConfirm`, ...) ricevono il **nome di una funzione**. Questa funzione viene chiamata dal framework quando l'evento si verifica:

```npas
function aoEnviar(v): Boolean
begin
    WriteLn("Enviado: " + v);
    return true;
end;

// ... dentro do ui():
TextInput(#{ placeholder: "Digite e Enter", onSubmit: aoEnviar })
```

Per convenzione, i *callback* restituiscono `Boolean` (di solito `true`). L'importante è l'effetto collaterale: modificare le variabili di stato.

## Tasti di uscita

Per terminare un'applicazione TerminalInk:

- **Esc** o **Ctrl+C** escono sempre.
- Anche il tasto **`q`** esce — **ma solo** quando nessun `TextInput` (o campo di input) è a fuoco. Se un campo di testo è a fuoco, `q` viene digitato normalmente nel campo.

::: warning Fuoco e il tasto `q`
Se la tua interfaccia ha campi di input, è preferibile istruire l'utente a uscire con **Esc**. Il tasto `q` è una scorciatoia di uscita solo quando il fuoco non è su un campo modificabile.
:::

## Come funziona dietro le quinte

- La base è **Lanterna**, che fornisce un terminale multipiattaforma con **schermo con buffer** e decodifica dei tasti.
- Ogni componente di alto livello (`VBox`, `Badge`, `Spinner`, ...) **si espande** in un albero di riquadri e testi (un layout in stile *flexbox*).
- Un piccolo **motore di layout** calcola posizioni e dimensioni; in seguito, un **renderizzatore** disegna l'albero sullo schermo con buffer, fotogramma per fotogramma.

Non hai bisogno di comprendere questi dettagli per scrivere applicazioni — ma spiegano perché il modello è a modalità immediata e perché le props di layout come `flexDirection`, `gap` e `flexGrow` si comportano come nel *flexbox*.

---

Di seguito, scopri tutti i [Componenti](./components) disponibili.
