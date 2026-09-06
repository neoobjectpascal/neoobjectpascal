# Creare eseguibili nativi

NeoObjectPascal impacchetta un programma `.npas` in un **eseguibile nativo autocontenuto** — senza richiedere che l'utente finale abbia Java installato. Un singolo comando produce:

- **macOS** → un pacchetto `.app`
- **Windows** → una cartella con un `.exe`
- **Linux** → una cartella con un binario in `bin/`

Sotto il cofano, il build usa **`jpackage`** (parte del JDK 14+), che impacchetta l'interprete, il tuo progetto e un runtime Java (JRE) minimale all'interno dell'app.

## Prerequisiti

- Un **JDK 14 o superiore** nel PATH (`jpackage` accompagna il JDK).
- Eseguire a partire dal **JAR impacchettato** dell'interprete (`neoobjectpascal.jar`).

## Uso

```bash
java -jar neoobjectpascal.jar --build programa.npas [opzioni]
```

| Opzione | Descrizione |
|-------|-----------|
| `--icon <png>` | icona dell'app, a partire da un **PNG** (convertita per piattaforma) |
| `--name <Nome>` | nome dell'app/eseguibile (predefinito: nome del programma) |
| `--output <dir>` | directory di output (predefinito: `./dist`) |
| `--target <mac\|windows\|linux>` | piattaforma di destinazione (predefinito: il SO corrente) |

### Esempio

```bash
java -jar neoobjectpascal.jar --build main.npas --icon icon.png --name CalculoITBI --output dist
```

<Output>
📦 Empacotando 'main.npas' → macOS (.app) (CalculoITBI)
   • 5 arquivo(s) do projeto incluídos (a partir de .../NeoObjectPascal-Examples)
   • ícone: app-icon.icns
   • rodando jpackage...
✅ Build concluído: dist/CalculoITBI.app
   Executar: open "dist/CalculoITBI.app"
</Output>

## L'intero progetto viene impacchettato

L'intera directory del programma è inclusa nell'app — **file e sottocartelle**, preservando la struttura. In altre parole:

- I moduli importati con `uses cartella.modulo` (ad esempio `helpers/calculoitbi.npas`) vengono inclusi.
- Anche i file di dati e gli asset che il programma legge a runtime sono inclusi.

Così l'app impacchettata gira esattamente come il programma girava a partire dal codice sorgente. Le directory "spazzatura" (`.git`, `node_modules`, la stessa directory di output, ...) vengono ignorate.

## Icona a partire da un PNG

Passa un singolo **PNG** con `--icon`; il build lo converte nel formato corretto di ciascuna piattaforma:

| Piattaforma | Formato dell'icona | Conversione |
|-----------|------------------|-----------|
| macOS | `.icns` | tramite `iconset` + `iconutil` (nativi di macOS) |
| Windows | `.ico` | integrata in Java (senza strumenti esterni) |
| Linux | `.png` | usato direttamente |

Usa un PNG quadrato (ad esempio 512×512 o 1024×1024) per il risultato migliore.

## Una piattaforma alla volta

`jpackage` **non fa cross-compile**: ogni artefatto viene generato sul proprio sistema operativo. Su macOS generi il `.app`; il `.exe` viene generato su Windows e il `bin` su Linux. Per produrre tutti e tre, esegui il build su ciascun SO (oppure usa una matrice di CI). Se passi un `--target` diverso dal SO corrente, il comando avvisa con un errore chiaro.

## Dal VS Code

Con l'estensione installata, fai clic con il tasto destro su un file `.npas` (o su una cartella del progetto) e scegli **Build Native Executable**. L'estensione chiede il nome dell'app e, facoltativamente, un'icona PNG, e genera il risultato in `dist/`.

## Eseguire il risultato

- **macOS:** `open dist/CalculoITBI.app` (oppure fai doppio clic).
- **Windows:** `dist\CalculoITBI\CalculoITBI.exe`.
- **Linux:** `dist/CalculoITBI/bin/CalculoITBI`.

::: info Dimensione dell'app
Ogni eseguibile è autocontenuto e include un JRE, quindi occupa alcune decine di MB — è la natura di un'app Java nativa, in cambio del non richiedere Java sulla macchina dell'utente.
:::

::: tip Programmi interattivi (TerminalInk)
Le app da terminale realizzate con [TerminalInk](../terminalink/introduction) funzionano una volta impacchettate: quando vengono aperte con un doppio clic (senza un terminale), TerminalInk apre la propria finestra. I programmi da console puri (`WriteLn`) è meglio eseguirli da un terminale, per vedere l'output.
:::

## Prossimi passi

Con il build nativo distribuisci i tuoi programmi come vere e proprie app. Per rivedere la sintassi completa del linguaggio, vedi il [Riferimento del linguaggio](../reference/language-reference).
