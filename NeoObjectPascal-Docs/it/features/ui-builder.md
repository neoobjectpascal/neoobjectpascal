# Editor visivo di interfacce (.xnpas)

L'estensione VS Code include un **editor visivo (WYSIWYG)** per creare interfacce **senza scrivere codice** — sia per il terminale (**TerminalInk**) sia per il web (**WebInk**). Componi la schermata trascinando i componenti, regoli proprietà ed eventi, e l'editor genera automaticamente il file `.npas` corrispondente.

## Il file `.xnpas`

L'editor lavora con file **`.xnpas`** — un JSON che contiene il progetto della schermata. Quando salvi `test.xnpas`, l'editor **(ri)genera** il `test.npas` associato. La sincronizzazione è **unidirezionale**: il `.xnpas` è la fonte di verità e il `.npas` generato riporta un'intestazione che avvisa di **non modificarlo a mano**.

## Creare una schermata

1. Apri la palette dei comandi (`Cmd/Ctrl+Maiusc+P`) ed esegui **"New UI Builder File (.xnpas)"**.
2. Scegli un modello: *WebInk — vuoto*, *WebInk — Dashboard*, *TerminalInk — vuoto* o *TerminalInk — Modulo*.
3. Il file si apre direttamente nell'editor visivo.

## Il layout dell'editor

- **Tavolozza** (a sinistra) — i componenti del target scelto, raggruppati. Trascinane uno sulla tela.
- **Tela** (al centro) — un'anteprima fedele. Clicca per selezionare; trascina un componente già posizionato per **riordinarlo** (o spostarlo in un altro contenitore).
- **Ispettore** (a destra) — schede **Proprietà**, **Stato** ed **Eventi**.
- **Barra degli strumenti** — l'interruttore **WebInk / TerminalInk**, il selettore di schermata/rotta e **Esegui dal vivo** (genera ed esegue il `.npas`).

## Stato ed eventi

- **Stato** — dichiara variabili globali (nome, tipo e valore iniziale) condivise tra schermate ed eventi.
- **Eventi** — per props come `onClick`, `onChange`, `onSubmit`, `onConfirm` e `onCancel`, usa l'**editor ibrido**: un'**azione senza codice** (incrementare una variabile, impostare una variabile, navigare a una schermata, usare il valore digitato) **oppure** passa alla modalità **Codice** e scrivi NeoObjectPascal liberamente. La funzione generata viene mostrata in anteprima lì stesso.

## Target: WebInk o TerminalInk

Un `.xnpas` è interamente **WebInk** o **TerminalInk**. L'interruttore cambia il target (reimposta l'albero, poiché gli insiemi di componenti differiscono).

- **WebInk** — `Page`, `Container`, `Grid`, `Card`, `Navbar`, `Heading`, `Text`, `Badge`, `StatCard`, `Button`, `TextInput`, `Select`, `Checkbox`, `Table`, `List`, `Chart`, `Alert`, `ProgressBar`…
- **TerminalInk** — `VBox`, `HBox`, `Box`, `Text`, `Badge`, `TextInput`, `Select`, `MultiSelect`, `ConfirmInput`, `ProgressBar`, `StatusMessage`, `Alert`, liste…

## Sincronizzazione e il `.npas` generato

Salvando il `.xnpas` si rigenera il `.npas` associato a partire dal progetto. **Non modificare il `.npas` generato a mano** — verrà sovrascritto al salvataggio successivo. Se apri un `.npas` generato, l'editor ti avvisa e propone di aprire il `.xnpas` corrispondente.
## Visibilità, dati dinamici e focus

Tre funzionalità permettono alle schermate di reagire allo stato in fase di esecuzione.

### La proprietà `visible`

Ogni componente dispone, nell'Ispettore, del controllo **Visibile**: *Sempre* (predefinito) o *Condizione (fx)*. In modalità *Condizione*, inserisci un'espressione booleana — il componente (e il suo sottoalbero) appare solo quando è vera. Ad esempio, un `ConfirmInput` che compare solo dopo che il nome è stato compilato, con la condizione `nome <> ""`.

### Dati da variabili (fx)

I campi dati — `options` (Select/MultiSelect), `columns`/`rows` (Table), il grafico (Chart), `value` (ProgressBar), `items` (List) e i campi valore — hanno un pulsante **fx**. Attivato, il campo non accetta più un valore fisso ma si lega al nome di una **variabile o espressione**. Così puoi popolare un componente con dati da una API, ad esempio, invece che con valori digitati a mano.

### Focus in TerminalInk

I componenti di input di TerminalInk (`TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`) acquisiscono due campi:

- **Chiave** (`key`) — un identificatore stabile del componente.
- **Focus iniziale** (`autoFocus`) — mette il focus su di esso già dal primo frame.

Inoltre, l'editor di eventi offre l'azione **Focalizza componente**, che genera `focus("chiave")` — utile, ad esempio, per riportare il focus su un campo quando una conferma viene annullata.

## Lingua dell'editor

L'editor visivo è **multilingue**, nelle stesse 5 lingue della documentazione: **Português, English, Deutsch, Français, Italiano**. Un selettore di lingua (icona del globo) nella barra superiore cambia al volo l'intera interfaccia dell'editor — palette, proprietà, eventi, suggerimenti e messaggi.

La preferenza si trova nell'impostazione **`neoobjectpascal.uiBuilder.language`** (globale, persistente). Il valore predefinito è **`auto`**, che segue la lingua di visualizzazione di VS Code e ricade sul portoghese quando non è una delle cinque. Selezionare una lingua nel selettore scrive questa impostazione.
