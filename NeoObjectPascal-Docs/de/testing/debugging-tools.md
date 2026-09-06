# Debugger, VS Code und Ausführung in der Cloud

Neben dem Test-Framework bietet NeoObjectPascal Werkzeuge, um Code zu untersuchen, zu bearbeiten und in großem Maßstab auszuführen: einen **interaktiven Debugger** (mit DAP-Modus für Editoren), eine **Erweiterung für VS Code** und die **entfernte Ausführung in der Cloud** über NeoObjectPascalCloud. Dieser Leitfaden stellt alle drei vor.

## Interaktiver Debugger

Um ein Programm Schritt für Schritt zu debuggen, führen Sie es mit der Flag `-d` (oder `--debug`) aus:

```bash
java -jar neoobjectpascal.jar -d programa.npas
```

Der Interpreter öffnet eine interaktive REPL, in der Sie die Ausführung steuern. Die wichtigsten Befehle:

| Befehl | Aktion |
|---------|------|
| `b <linha>` | fügt einen Breakpoint in der Zeile hinzu |
| `d <linha>` | entfernt den Breakpoint aus der Zeile |
| `list` | listet die aktiven Breakpoints auf |
| `c`, `continue` | fährt bis zum nächsten Breakpoint fort |
| `s`, `step` | führt die nächste Zeile aus (step over) |
| `i`, `into` | springt in die aufgerufene Funktion (step into) |
| `o`, `out` | verlässt die aktuelle Funktion (step out) |
| `p <var>` | gibt den Wert einer Variablen aus |
| `vars` | listet alle Variablen des aktuellen Gültigkeitsbereichs auf |
| `w <var>` | überwacht (watch) Änderungen an einer Variablen |
| `set <var> <valor>` | ändert den Wert einer Variablen zur Laufzeit |
| `stack` | zeigt den Aufrufstapel |
| `q`, `quit` | beendet den Debugger und das Programm |

### Eine Beispielsitzung

Betrachten Sie das folgende Programm:

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

Wir setzen einen Breakpoint auf die Zeile der Summe, beobachten `x`, inspizieren Werte und ändern sogar eine Variable, bevor wir fortfahren:

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

::: tip Strategie für Breakpoints
Setzen Sie Breakpoints an den Anfang von Schleifen, auf wichtige `if`-Bedingungen, Funktionsaufrufe und Rückgabepunkte. Kombinieren Sie `w` (watch) mit `s` (step), um genau zu verfolgen, wann und wo sich eine Variable ändert.
:::

### DAP-Modus für Editoren

Für grafisches Debugging innerhalb eines Editors stellt der Interpreter einen **DAP**-Server (Debug Adapter Protocol) bereit — dasselbe Protokoll, das VS Code und andere Editoren für visuelle Breakpoints, das Inspizieren von Variablen und die schrittweise Steuerung verwenden:

```bash
java -jar neoobjectpascal.jar --dap
```

In diesem Modus führt der Interpreter keine Text-REPL aus: Er wartet darauf, dass sich ein DAP-Client (wie VS Code) verbindet. Sie setzen Breakpoints, indem Sie an den Rand des Editors klicken, und nutzen die Schaltflächen zum Fortfahren/Steppen/Inspizieren der Oberfläche selbst. Normalerweise starten Sie den DAP-Modus nicht von Hand — die VS-Code-Erweiterung übernimmt das für Sie.

**Step Into (F7)** springt in den Rumpf von Klassenmethoden und Funktionen Ihres Projekts — auch wenn diese in einer anderen, über `uses` importierten Datei definiert sind — und öffnet dabei die richtige Datei und Zeile. Der **Call Stack** zeigt jeden Frame des Stapels (`Klasse.methode`, Funktionen und `main`) mit seiner Datei und Zeile an; der Reiter **Variables** zeigt die lokalen Variablen des ausgewählten Frames an, einschließlich `self` innerhalb einer Methode. Verwenden Sie **Step Over**, um einen Aufruf auszuführen, ohne in ihn hineinzuspringen, und **Step Out**, um zum Aufrufer zurückzukehren.

::: tip Debuggen in VS Code
Beim Debuggen über VS Code erscheint die Programmausgabe (`WriteLn`) während der Ausführung in der **Debug-Konsole**. Die Modulauflösung nutzt das Verzeichnis des Programms als Wurzel, sodass das Debuggen eines Programms mit `uses ordner.modul` wie erwartet funktioniert. Die **interaktive Eingabe (`ReadLn`) ist beim Debuggen jedoch NICHT verfügbar** — sie liefert einen Standardwert zurück. Verwenden Sie für interaktive Programme **Run** statt des Debuggers.
:::

## VS-Code-Erweiterung

Die offizielle Erweiterung für **VS Code** macht aus dem Editor eine vollständige Umgebung für NeoObjectPascal. Sie umfasst:

- **Syntaxhervorhebung** für `.npas`- und `.test.npas`-Dateien.
- **Eingebetteter Interpreter** — die Erweiterung bringt das gebündelte JAR mit (in `VS-Code-Extension/bin/`), sodass Sie den Pfad zum Interpreter nicht manuell konfigurieren müssen.
- **Befehle** zum Ausführen, Testen und Debuggen, ohne den Editor zu verlassen.

Die Befehle sind über die Befehlspalette (`Cmd/Ctrl+Shift+P`) verfügbar:

| Befehl | Aktion |
|---------|------|
| `neoobjectpascal.run` | führt die aktuelle `.npas`-Datei aus |
| `neoobjectpascal.debug` | startet den Debugger (über DAP) für die aktuelle Datei |
| `neoobjectpascal.runTest` | führt die aktuelle `.test.npas`-Datei aus |
| `neoobjectpascal.runAllTests` | führt alle Tests des Projekts rekursiv aus |
| `neoobjectpascal.build` | erzeugt eine native ausführbare Datei (exe/app/bin) des Projekts — siehe [Native ausführbare Dateien erstellen](./building-executables) |

::: info Ausführung in der Cloud
Die Ausführung in der Cloud erfolgt über die Kommandozeile mit der Flag `--execute-on-cloud` des Interpreters (siehe den Abschnitt unten).
:::

::: tip Empfohlener Ablauf
Schreiben Sie den Code mit Hervorhebung und Autovervollständigung, führen Sie `neoobjectpascal.runTest` aus, um die geöffnete Datei zu prüfen, und verwenden Sie `neoobjectpascal.debug`, um die Ausführung mit visuellen Breakpoints zu verfolgen. Alles mit dem JAR, das bereits mit der Erweiterung geliefert wird.
:::

## Ausführung in der Cloud

NeoObjectPascal ist nativ in **NeoObjectPascalCloud** integriert und ermöglicht es, ein Projekt mit einem einzigen Befehl auf einem entfernten Server auszuführen. Das ist nützlich, um Ausführungen per URL zu teilen, eine Log-Historie zu führen und ohne Abhängigkeit von der lokalen Umgebung zu arbeiten.

### Syntax

```bash
java -jar neoobjectpascal.jar --execute-on-cloud <url> <projeto> <usuario> <senha> <arquivo.npas>
```

| Parameter | Beschreibung |
|-----------|-----------|
| `<url>` | Basis-URL der Cloud-API (z. B. `http://localhost:8000`) |
| `<projeto>` | Name des Projekts (Basisordner) |
| `<usuario>` | Cloud-Benutzer |
| `<senha>` | Cloud-Passwort |
| `<arquivo.npas>` | auszuführende Hauptdatei |

### Beispiel

Gegeben sei das Programm `hello.npas`:

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

Wir führen es in der Cloud aus:

```bash
java -jar neoobjectpascal.jar \
  --execute-on-cloud \
  http://localhost:8000 \
  hello_project \
  usuario@email.com \
  senha123 \
  hello.npas
```

Der Interpreter authentifiziert sich, sammelt **alle `.npas`-Dateien** aus dem Projektverzeichnis (unter Beibehaltung der Ordnerstruktur), lädt sie hoch, startet die entfernte Ausführung und liefert einen Link, über den sich die Logs verfolgen lassen:

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

::: warning Geben Sie keine Passwörter preis
Vermeiden Sie es, das Passwort direkt in der Kommandozeile zu übergeben, denn es ist im Shell-Verlauf und in der Prozessliste sichtbar. Bevorzugen Sie Umgebungsvariablen:

```bash
export CLOUD_PASSWORD="senha123"
java -jar neoobjectpascal.jar --execute-on-cloud \
  http://localhost:8000 meu_projeto usuario@email.com "$CLOUD_PASSWORD" main.npas
```
:::

::: info Projekte mit mehreren Dateien
Die Erfassung erfolgt rekursiv: Hat das Projekt Unterordner (`helpers/`, `utils/`), werden alle `.npas`-Dateien automatisch erkannt und hochgeladen, wobei die relativen Pfade erhalten bleiben. Sie müssen im Befehl nur die Hauptdatei angeben.
:::

## Nächste Schritte

Sie kennen nun die drei Werkzeuge von NeoObjectPascal zur Untersuchung und Ausführung. Um die vollständige Syntax der Sprache an einem Ort nachzuschlagen, gehen Sie weiter zur [Sprachreferenz](../reference/language-reference).
