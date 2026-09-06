# Native ausführbare Dateien erstellen

NeoObjectPascal packt ein `.npas`-Programm in eine **eigenständige native ausführbare Datei** — ohne dass der Endanwender Java installiert haben muss. Ein einziger Befehl erzeugt:

- **macOS** → ein `.app`-Paket
- **Windows** → einen Ordner mit einer `.exe`
- **Linux** → einen Ordner mit einer Binärdatei in `bin/`

Im Hintergrund nutzt der Build **`jpackage`** (Teil des JDK 14+), das den Interpreter, Ihr Projekt und eine schlanke Java-Laufzeitumgebung (JRE) in die App packt.

## Voraussetzungen

- Ein **JDK 14 oder höher** im PATH (`jpackage` gehört zum JDK).
- Die Ausführung aus dem **gebündelten JAR** des Interpreters (`neoobjectpascal.jar`).

## Verwendung

```bash
java -jar neoobjectpascal.jar --build programa.npas [opções]
```

| Option | Beschreibung |
|-------|-----------|
| `--icon <png>` | App-Icon, aus einem **PNG** (pro Plattform konvertiert) |
| `--name <Nome>` | Name der App/ausführbaren Datei (Standard: Name des Programms) |
| `--output <dir>` | Ausgabeverzeichnis (Standard: `./dist`) |
| `--target <mac\|windows\|linux>` | Zielplattform (Standard: aktuelles Betriebssystem) |

### Beispiel

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

## Das gesamte Projekt wird gepackt

Das gesamte Verzeichnis des Programms wird in die App aufgenommen — **Dateien und Unterordner**, unter Beibehaltung der Struktur. Das heißt:

- Mit `uses ordner.modul` importierte Module (zum Beispiel `helpers/calculoitbi.npas`) kommen mit.
- Datendateien und Assets, die das Programm zur Laufzeit liest, werden ebenfalls eingebunden.

So läuft die gepackte App genau so, wie das Programm aus dem Quellcode lief. „Müll"-Verzeichnisse (`.git`, `node_modules`, das Ausgabeverzeichnis selbst, ...) werden ignoriert.

## Icon aus einem PNG

Übergeben Sie ein einzelnes **PNG** mit `--icon`; der Build konvertiert es in das passende Format jeder Plattform:

| Plattform | Icon-Format | Konvertierung |
|-----------|------------------|-----------|
| macOS | `.icns` | über `iconset` + `iconutil` (nativ unter macOS) |
| Windows | `.ico` | in Java eingebettet (ohne externe Werkzeuge) |
| Linux | `.png` | direkt verwendet |

Verwenden Sie für das beste Ergebnis ein quadratisches PNG (zum Beispiel 512×512 oder 1024×1024).

## Eine Plattform nach der anderen

`jpackage` **führt kein Cross-Compile durch**: Jedes Artefakt wird auf dem jeweils eigenen Betriebssystem erzeugt. Unter macOS erzeugen Sie das `.app`; die `.exe` wird unter Windows und die `bin` unter Linux erzeugt. Um alle drei zu erzeugen, führen Sie den Build auf jedem Betriebssystem aus (oder verwenden Sie eine CI-Matrix). Wenn Sie ein `--target` übergeben, das vom aktuellen Betriebssystem abweicht, warnt der Befehl mit einer klaren Fehlermeldung.

## Über VS Code

Mit installierter Erweiterung klicken Sie mit der rechten Maustaste auf eine `.npas`-Datei (oder auf einen Projektordner) und wählen **Build Native Executable**. Die Erweiterung fragt nach dem Namen der App und optional nach einem PNG-Icon und erzeugt das Ergebnis in `dist/`.

## Das Ergebnis ausführen

- **macOS:** `open dist/CalculoITBI.app` (oder doppelklicken).
- **Windows:** `dist\CalculoITBI\CalculoITBI.exe`.
- **Linux:** `dist/CalculoITBI/bin/CalculoITBI`.

::: info Größe der App
Jede ausführbare Datei ist eigenständig und enthält eine JRE, belegt daher einige Dutzend MB — das liegt in der Natur einer nativen Java-App, im Gegenzug dafür, dass auf dem Rechner des Anwenders kein Java erforderlich ist.
:::

::: tip Interaktive Programme (TerminalInk)
Terminal-Apps, die mit [TerminalInk](../terminalink/introduction) erstellt wurden, funktionieren auch gepackt: Werden sie per Doppelklick (ohne Terminal) geöffnet, öffnet TerminalInk sein eigenes Fenster. Reine Konsolenprogramme (`WriteLn`) führt man besser aus einem Terminal aus, um die Ausgabe zu sehen.
:::

## Nächste Schritte

Mit dem nativen Build verteilen Sie Ihre Programme als echte Apps. Um die vollständige Syntax der Sprache noch einmal durchzugehen, siehe die [Sprachreferenz](../reference/language-reference).
