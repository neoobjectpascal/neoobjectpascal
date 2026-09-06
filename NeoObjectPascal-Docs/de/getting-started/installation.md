# Installation und erstes Programm

Der NeoObjectPascal-Interpreter wird als **ausführbare JAR-Datei** ausgeliefert, sodass Sie lediglich **Java 11 oder höher** installiert haben müssen.

## Voraussetzungen

- **Java 11+** (JRE oder JDK). Überprüfen Sie dies mit:

```bash
java -version
```

- (Optional) **Maven**, falls Sie den Interpreter aus dem Quellcode kompilieren möchten.

## Den Interpreter beziehen

Sie können die bereits fertig gepackte JAR-Datei verwenden (zum Beispiel jene, die der VS-Code-Erweiterung beiliegt, unter `VS-Code-Extension/bin/`) oder aus dem Repository kompilieren:

```bash
cd NeoObjectPascal
mvn package -DskipTests
# Die fertige JAR-Datei befindet sich unter target/neoobjectpascal-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Hallo Welt

Erstellen Sie eine Datei `ola.npas`:

```npas
begin
    WriteLn("Olá, NeoObjectPascal!");
end.
```

Und führen Sie sie aus:

```bash
java -jar neoobjectpascal.jar ola.npas
```

<Output>
Olá, NeoObjectPascal!
</Output>

::: tip Dateierweiterung
Programme verwenden die Erweiterung `.npas`. Testdateien verwenden `.test.npas`.
:::

## Optionen der Befehlszeile

Der Interpreter akzeptiert mehrere Flags:

| Flag | Beschreibung |
|------|-----------|
| _(keine)_ | Führt die `.npas`-Datei aus |
| `-q`, `--no-warnings` | Unterdrückt Analysewarnungen |
| `-t`, `--test` | Testmodus (führt eine `.test.npas` aus) |
| `-ta`, `--test-all <dir>` | Führt alle Tests rekursiv aus, mit Abdeckung |
| `-d`, `--debug` | Interaktiver Debugger-Modus |
| `--dap` | DAP-Modus (Integration mit VS Code) |
| `--build <Datei> [--icon png] [--name] [--output] [--target]` | Erzeugt eine native ausführbare Datei (exe/app/bin) |
| `--execute-on-cloud <url> <proj> <user> <pass>` | Führt in der NeoObjectPascalCloud aus |
| `-h`, `--help` | Zeigt die Hilfe an |

Beispiele:

```bash
java -jar neoobjectpascal.jar --no-warnings ola.npas
java -jar neoobjectpascal.jar -t calculadora.test.npas
java -jar neoobjectpascal.jar --test-all ./examples
```

::: tip Empfohlener Editor
Installieren Sie die **VS-Code-Erweiterung** für NeoObjectPascal und erhalten Sie Syntaxhervorhebung, Ausführung, Tests und integriertes Debugging. Siehe [Debugger, VS Code und Cloud](../testing/debugging-tools).
:::

Jetzt, da Sie Code ausführen können, wollen wir die [Struktur eines Programms](./program-structure) verstehen.
