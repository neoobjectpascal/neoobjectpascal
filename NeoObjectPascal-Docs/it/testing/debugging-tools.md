# Debugger, VS Code e esecuzione nel cloud

Oltre al framework di test, NeoObjectPascal offre strumenti per investigare, modificare ed eseguire il codice su larga scala: un **debugger interattivo** (con modalità DAP per gli editor), un'**estensione per VS Code** e l'**esecuzione remota nel cloud** tramite NeoObjectPascalCloud. Questa guida presenta tutti e tre.

## Debugger interattivo

Per eseguire il debug di un programma passo passo, avvialo con il flag `-d` (o `--debug`):

```bash
java -jar neoobjectpascal.jar -d programa.npas
```

L'interprete apre un REPL interattivo in cui controlli l'esecuzione. I comandi principali:

| Comando | Azione |
|---------|------|
| `b <linha>` | aggiunge un breakpoint alla riga |
| `d <linha>` | rimuove il breakpoint dalla riga |
| `list` | elenca i breakpoint attivi |
| `c`, `continue` | continua fino al prossimo breakpoint |
| `s`, `step` | esegue la riga successiva (step over) |
| `i`, `into` | entra nella funzione chiamata (step into) |
| `o`, `out` | esce dalla funzione corrente (step out) |
| `p <var>` | stampa il valore di una variabile |
| `vars` | elenca tutte le variabili dello scope corrente |
| `w <var>` | monitora (watch) le modifiche di una variabile |
| `set <var> <valor>` | modifica il valore di una variabile a runtime |
| `stack` | mostra lo stack delle chiamate |
| `q`, `quit` | termina il debugger e il programma |

### Una sessione di esempio

Considera il programma seguente:

```npas
var x: Integer;
var y: Integer;
var resultado: Integer;

begin
    x := 10;
    y := 20;
    resultado := x + y;
    WriteLn("resultado = ", resultado);
end.
```

Mettiamo un breakpoint sulla riga della somma, osserviamo `x`, ispezioniamo i valori e modifichiamo persino una variabile prima di continuare:

<Output>
debug> b 8
✓ Breakpoint adicionado na linha 8

debug> w x
✓ Watching variável: x

debug> c

⏸ PAUSADO na linha 8
  resultado := x + y;

debug> p x
x = 10 (tipo: INTEGER)

debug> vars
Variáveis locais:
  x = 10 (tipo: INTEGER)
  y = 20 (tipo: INTEGER)
  resultado = null (tipo: INTEGER)

debug> set x 50
✓ x = 50 (anterior: 10)

debug> c
resultado = 70
</Output>

::: tip Strategia dei breakpoint
Metti i breakpoint all'inizio dei loop, sulle condizioni `if` importanti, sulle chiamate di funzione e sui punti di ritorno. Combina `w` (watch) con `s` (step) per seguire esattamente quando e dove una variabile cambia.
:::

### Modalità DAP per gli editor

Per il debug grafico all'interno di un editor, l'interprete espone un server **DAP** (Debug Adapter Protocol) — lo stesso protocollo che VS Code e altri editor usano per i breakpoint visivi, l'ispezione delle variabili e il controllo passo passo:

```bash
java -jar neoobjectpascal.jar --dap
```

In questa modalità l'interprete non esegue un REPL testuale: rimane in attesa che un client DAP (come VS Code) si connetta. Definisci i breakpoint facendo clic sul margine dell'editor, e usi i pulsanti di continua/step/ispeziona dell'interfaccia stessa. Di norma non avvii la modalità DAP a mano — lo fa per te l'estensione di VS Code.

Lo **Step Into (F7)** entra nel corpo dei metodi di classe e delle funzioni del tuo progetto — anche quando sono definiti in un altro file importato tramite `uses` —, aprendo il file e la riga corretti. La **Call Stack** mostra ogni frame dello stack (`Classe.metodo`, funzioni e `main`) con il relativo file e riga; la scheda **Variables** mostra le variabili locali del frame selezionato, incluso `self` all'interno di un metodo. Usa **Step Over** per eseguire una chiamata senza entrarci e **Step Out** per tornare al chiamante.

::: tip Debug in VS Code
Durante il debug da VS Code, l'output del programma (`WriteLn`) compare nella **Debug Console** mentre il programma è in esecuzione. La risoluzione dei moduli usa come radice la directory del programma, quindi effettuare il debug di un programma che fa `uses cartella.modulo` funziona come previsto. Tuttavia, l'**input interattivo (`ReadLn`) NON è disponibile durante il debug** — restituisce un valore predefinito. Per i programmi interattivi, usa **Run** invece del debugger.
:::

## Estensione di VS Code

L'estensione ufficiale per **VS Code** trasforma l'editor in un ambiente completo per NeoObjectPascal. Include:

- **Evidenziazione della sintassi** per i file `.npas` e `.test.npas`.
- **Interprete integrato** — l'estensione include il JAR impacchettato (in `VS-Code-Extension/bin/`), quindi non devi configurare manualmente il percorso dell'interprete.
- **Comandi** per eseguire, testare ed eseguire il debug senza uscire dall'editor.

I comandi sono disponibili tramite la palette dei comandi (`Cmd/Ctrl+Shift+P`):

| Comando | Azione |
|---------|------|
| `neoobjectpascal.run` | esegue il file `.npas` corrente |
| `neoobjectpascal.debug` | avvia il debugger (tramite DAP) sul file corrente |
| `neoobjectpascal.runTest` | esegue il file `.test.npas` corrente |
| `neoobjectpascal.runAllTests` | esegue tutti i test del progetto in modo ricorsivo |
| `neoobjectpascal.build` | genera un eseguibile nativo (exe/app/bin) del progetto — vedi [Creare eseguibili nativi](./building-executables) |

::: info Esecuzione nel cloud
L'esecuzione nel cloud si effettua da riga di comando tramite il flag `--execute-on-cloud` dell'interprete (vedi la sezione qui sotto).
:::

::: tip Flusso consigliato
Scrivi il codice con evidenziazione e completamento automatico, esegui `neoobjectpascal.runTest` per validare il file aperto, e usa `neoobjectpascal.debug` per seguire l'esecuzione con breakpoint visivi. Il tutto usando il JAR già incluso nell'estensione.
:::

## Esecuzione nel cloud

NeoObjectPascal si integra nativamente con **NeoObjectPascalCloud**, permettendo di eseguire un progetto su un server remoto con un unico comando. Questo è utile per condividere le esecuzioni tramite URL, mantenere lo storico dei log ed eseguire senza dipendere dall'ambiente locale.

### Sintassi

```bash
java -jar neoobjectpascal.jar --execute-on-cloud <url> <projeto> <usuario> <senha> <arquivo.npas>
```

| Parametro | Descrizione |
|-----------|-----------|
| `<url>` | URL base dell'API del cloud (es.: `http://localhost:8000`) |
| `<projeto>` | nome del progetto (cartella base) |
| `<usuario>` | utente del cloud |
| `<senha>` | password del cloud |
| `<arquivo.npas>` | file principale da eseguire |

### Esempio

Dato il programma `hello.npas`:

```npas
var mensagem: String;
var numero: Integer;

begin
    mensagem := "Olá do NeoObjectPascal Cloud!";
    numero := 42;

    WriteLn("=================================");
    WriteLn(mensagem);
    WriteLn("Número mágico: ", numero);
    WriteLn("=================================");
end.
```

Lo eseguiamo nel cloud:

```bash
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://localhost:8000 \
  hello_project \
  usuario@email.com \
  senha123 \
  hello.npas
```

L'interprete si autentica, raccoglie **tutti i file `.npas`** della directory del progetto (mantenendo la struttura delle cartelle), effettua l'upload, avvia l'esecuzione remota e restituisce un link per seguire i log:

<Output>
🔐 Autenticando no cloud...
✓ Autenticado com sucesso!

📁 Coletando arquivos do projeto...
✓ Encontrados 1 arquivo(s)
  - hello.npas

⬆️  Fazendo upload dos arquivos...
  [1/1] hello.npas
✓ Upload concluído!

🚀 Executando projeto no cloud...
✅ Projeto executado com sucesso!

🔗 Link da execução:
   http://localhost:8000/executions/123
</Output>

::: warning Non esporre le password
Evita di passare la password direttamente nella riga di comando, perché rimane visibile nella cronologia della shell e nell'elenco dei processi. Preferisci le variabili d'ambiente:

```bash
export CLOUD_PASSWORD="senha123"
java -jar neoobjectpascal.jar --execute-on-cloud \
  http://localhost:8000 meu_projeto usuario@email.com "$CLOUD_PASSWORD" main.npas
```
:::

::: info Progetti con più file
La raccolta è ricorsiva: se il progetto ha sottocartelle (`helpers/`, `utils/`), tutti i file `.npas` vengono rilevati e inviati automaticamente, preservando i percorsi relativi. Basta indicare il file principale nel comando.
:::

## Prossimi passi

Ora conosci i tre strumenti di investigazione ed esecuzione di NeoObjectPascal. Per consultare la sintassi completa del linguaggio in un unico posto, prosegui verso il [Riferimento del linguaggio](../reference/language-reference).
