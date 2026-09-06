# Installazione e primo programma

L'interprete di NeoObjectPascal è distribuito come **file JAR eseguibile**, quindi tutto ciò di cui hai bisogno è **Java 11 o superiore** installato.

## Prerequisiti

- **Java 11+** (JRE o JDK). Verifica con:

```bash
java -version
```

- (Facoltativo) **Maven**, nel caso tu voglia compilare l'interprete a partire dal codice sorgente.

## Ottenere l'interprete

Puoi usare il JAR già pacchettizzato (ad esempio, quello che accompagna l'estensione di VS Code, in `VS-Code-Extension/bin/`) oppure compilare dal repository:

```bash
cd NeoObjectPascal
mvn package -DskipTests
# Il JAR finale si trova in target/neoobjectpascal-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Ciao, mondo

Crea un file `ola.npas`:

```npas
begin
    WriteLn("Olá, NeoObjectPascal!");
end.
```

Ed esegui:

```bash
java -jar neoobjectpascal.jar ola.npas
```

<Output>
Olá, NeoObjectPascal!
</Output>

::: tip Estensione del file
I programmi usano l'estensione `.npas`. I file di test usano `.test.npas`.
:::

## Opzioni della riga di comando

L'interprete accetta diverse flag:

| Flag | Descrizione |
|------|-----------|
| _(nessuna)_ | Esegue il file `.npas` |
| `-q`, `--no-warnings` | Sopprime gli avvisi di analisi |
| `-t`, `--test` | Modalità di test (esegue un `.test.npas`) |
| `-ta`, `--test-all <dir>` | Esegue tutti i test ricorsivamente, con copertura |
| `-d`, `--debug` | Modalità debugger interattiva |
| `--dap` | Modalità DAP (integrazione con VS Code) |
| `--build <file> [--icon png] [--name] [--output] [--target]` | Genera un eseguibile nativo (exe/app/bin) |
| `--execute-on-cloud <url> <proj> <user> <pass>` | Esegue su NeoObjectPascalCloud |
| `-h`, `--help` | Mostra l'aiuto |

Esempi:

```bash
java -jar neoobjectpascal.jar --no-warnings ola.npas
java -jar neoobjectpascal.jar -t calculadora.test.npas
java -jar neoobjectpascal.jar --test-all ./examples
```

::: tip Editor consigliato
Installa l'**estensione di VS Code** per NeoObjectPascal e ottieni evidenziazione della sintassi, esecuzione, test e debug integrati. Vedi [Debugger, VS Code e cloud](../testing/debugging-tools).
:::

Ora che riesci a eseguire codice, comprendiamo la [struttura di un programma](./program-structure).
