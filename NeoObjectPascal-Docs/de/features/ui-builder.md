# Visueller UI-Builder (.xnpas)

Die VS-Code-Erweiterung enthält einen **visuellen (WYSIWYG-)Editor**, um Oberflächen **ohne Code** zu erstellen — sowohl für das Terminal (**TerminalInk**) als auch für das Web (**WebInk**). Sie bauen den Bildschirm per Drag-and-drop zusammen, passen Eigenschaften und Ereignisse an, und der Editor erzeugt automatisch die passende `.npas`-Datei.

## Die `.xnpas`-Datei

Der Editor arbeitet mit **`.xnpas`**-Dateien — ein JSON, das das Bildschirmdesign enthält. Beim Speichern von `test.xnpas` **(re)generiert** der Editor die zugehörige `test.npas`. Die Synchronisierung ist **einseitig**: die `.xnpas` ist die Quelle der Wahrheit, und die generierte `.npas` trägt einen Hinweis, dass sie **nicht von Hand bearbeitet** werden sollte.

## Einen Bildschirm erstellen

1. Öffnen Sie die Befehlspalette (`Cmd/Strg+Umschalt+P`) und führen Sie **„New UI Builder File (.xnpas)"** aus.
2. Wählen Sie eine Vorlage: *WebInk — leer*, *WebInk — Dashboard*, *TerminalInk — leer* oder *TerminalInk — Formular*.
3. Die Datei öffnet sich direkt im visuellen Editor.

## Aufbau des Editors

- **Palette** (links) — die Komponenten des gewählten Ziels, gruppiert. Ziehen Sie eine auf die Arbeitsfläche.
- **Arbeitsfläche** (Mitte) — eine originalgetreue Vorschau. Klicken zum Auswählen; eine platzierte Komponente ziehen, um sie **neu anzuordnen** (oder in einen anderen Container zu verschieben).
- **Inspektor** (rechts) — Reiter **Eigenschaften**, **Zustand** und **Ereignisse**.
- **Symbolleiste** — der Umschalter **WebInk / TerminalInk**, die Bildschirm-/Routenauswahl und **Live ausführen** (erzeugt und startet die `.npas`).

## Zustand und Ereignisse

- **Zustand** — deklarieren Sie globale Variablen (Name, Typ und Anfangswert), die von Bildschirmen und Ereignissen gemeinsam genutzt werden.
- **Ereignisse** — für Props wie `onClick`, `onChange`, `onSubmit`, `onConfirm` und `onCancel` nutzen Sie den **hybriden Editor**: eine **No-Code-Aktion** (Variable erhöhen, Variable setzen, zu einem Bildschirm navigieren, den eingegebenen Wert verwenden) **oder** wechseln Sie zu **Code** und schreiben Sie NeoObjectPascal frei. Die generierte Funktion wird dort als Vorschau angezeigt.

## Ziele: WebInk oder TerminalInk

Eine `.xnpas` ist vollständig **WebInk** oder **TerminalInk**. Der Umschalter wechselt das Ziel (setzt den Baum zurück, da sich die Komponentensätze unterscheiden).

- **WebInk** — `Page`, `Container`, `Grid`, `Card`, `Navbar`, `Heading`, `Text`, `Badge`, `StatCard`, `Button`, `TextInput`, `Select`, `Checkbox`, `Table`, `List`, `Chart`, `Alert`, `ProgressBar` …
- **TerminalInk** — `VBox`, `HBox`, `Box`, `Text`, `Badge`, `TextInput`, `Select`, `MultiSelect`, `ConfirmInput`, `ProgressBar`, `StatusMessage`, `Alert`, Listen …

## Synchronisierung und die generierte `.npas`

Beim Speichern der `.xnpas` wird die zugehörige `.npas` aus dem Design neu erzeugt. **Bearbeiten Sie die generierte `.npas` nicht von Hand** — sie wird beim nächsten Speichern überschrieben. Öffnen Sie eine generierte `.npas`, warnt der Editor und bietet an, die zugehörige `.xnpas` zu öffnen.
## Sichtbarkeit, dynamische Daten und Fokus

Drei Funktionen lassen Bildschirme zur Laufzeit auf den Zustand reagieren.

### Die Eigenschaft `visible`

Jede Komponente hat im Inspektor das Steuerelement **Sichtbar**: *Immer* (Standard) oder *Bedingung (fx)*. Im Modus *Bedingung* geben Sie einen booleschen Ausdruck ein — die Komponente (und ihr Teilbaum) erscheint nur, wenn er wahr ist. Beispiel: ein `ConfirmInput`, das erst erscheint, nachdem der Name ausgefüllt wurde, mit der Bedingung `nome <> ""`.

### Daten aus Variablen (fx)

Datenfelder — `options` (Select/MultiSelect), `columns`/`rows` (Table), das Diagramm (Chart), `value` (ProgressBar), `items` (List) und die Wertfelder — haben eine **fx**-Schaltfläche. Aktiviert, nimmt das Feld keinen festen Wert mehr an, sondern bindet an den Namen einer **Variablen oder eines Ausdrucks**. So können Sie eine Komponente z. B. mit Daten aus einer API füllen statt mit von Hand eingegebenen Werten.

### Fokus in TerminalInk

TerminalInk-Eingabekomponenten (`TextInput`, `PasswordInput`, `EmailInput`, `ConfirmInput`, `Select`, `MultiSelect`) erhalten zwei Felder:

- **Schlüssel** (`key`) — ein stabiler Bezeichner der Komponente.
- **Anfangsfokus** (`autoFocus`) — setzt den Fokus bereits im ersten Frame darauf.

Außerdem bietet der Ereignis-Editor die Aktion **Komponente fokussieren**, die `focus("schlüssel")` erzeugt — praktisch, um z. B. den Fokus auf ein Feld zurückzugeben, wenn eine Bestätigung abgebrochen wird.

## Editorsprache

Der visuelle Editor ist **mehrsprachig**, in denselben 5 Sprachen wie die Dokumentation: **Português, English, Deutsch, Français, Italiano**. Ein Sprachauswahlfeld (Globus-Symbol) in der oberen Leiste schaltet die gesamte Editoroberfläche sofort um — Palette, Eigenschaften, Ereignisse, Hinweise und Meldungen.

Die Einstellung liegt unter **`neoobjectpascal.uiBuilder.language`** (global, dauerhaft). Der Standard ist **`auto`**, das der Anzeigesprache von VS Code folgt und auf Portugiesisch zurückfällt, wenn diese keine der fünf ist. Die Auswahl im Selektor schreibt diese Einstellung.
