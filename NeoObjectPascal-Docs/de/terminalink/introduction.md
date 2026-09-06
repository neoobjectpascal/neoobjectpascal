# Einführung in TerminalInk

**TerminalInk** ist das Framework für Terminal-Oberflächen von NeoObjectPascal. Inspiriert von [React Ink](https://github.com/vadimdemedes/ink), erlaubt es Ihnen, Terminal-Anwendungen (TUIs) **deklarativ** zu erstellen — Sie beschreiben, wie der Bildschirm aussehen soll, und das Framework kümmert sich darum, ihn zu zeichnen, neu zu zeichnen und die Tastatureingaben zu dekodieren.

Unter der Haube stützt sich TerminalInk auf die Java-Bibliothek [Lanterna](https://github.com/mabe02/lanterna), die ein plattformübergreifendes Terminal, einen gepufferten Bildschirm und die Tastendekodierung bereitstellt. Sie müssen damit nie direkt hantieren: Sie schreiben einfach Komponenten.

## Das Modul aktivieren

Um TerminalInk zu verwenden, deklarieren Sie das Modul in der `uses`-Klausel:

```npas
uses terminalink;
```

Damit stehen alle Komponenten (`Text`, `VBox`, `TextInput`, `Spinner`, ...) sowie die Render- und Theme-Funktionen zur Verfügung.

## Das Rendering-Modell (Immediate Mode)

TerminalInk arbeitet im **Immediate Mode**. Statt den Komponentenbaum einmalig aufzubauen, schreiben Sie eine **Konstruktionsfunktion**, die den Baum der Oberfläche **zurückgibt**. Die Funktion `render` erhält diese Funktion und ruft sie wiederholt auf — bei jedem Frame (~60 ms) —, um den Bildschirm neu zu zeichnen.

Der Zustand der Anwendung lebt in **normalen Variablen** von NeoObjectPascal. Die Ereignis-*Callbacks* (übergeben über den Funktionsnamen) ändern diese Variablen, und im nächsten Frame wird die Oberfläche neu aufgebaut und spiegelt bereits den neuen Zustand wider.

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

Dieses Programm zeichnet einen Kasten mit Titel, ein Textfeld und eine Begrüßung. Jede getippte Taste löst `onNome` aus, das die Variable `nome` aktualisiert; im nächsten Frame erscheint die Zeile „Olá, ..." aktualisiert.

::: tip Der Zyklus in einem Satz
`Zustand` (Variablen) → `ui()` gibt den Baum zurück → `render` zeichnet → der Benutzer interagiert → der *Callback* ändert den `Zustand` → wiederholt.
:::

## Props und Record-Literale

Jede Komponente erhält ihre **Props** als **Record-Literal** — die Syntax `#{ schlüssel: wert, ... }`:

```npas
Text(#{ bold: true, color: "yellow" }, "Atenção")
```

Records sind ein Feature der Sprache selbst. Ein Feld lesen Sie mit `record.feld`, was auch für Werte gilt, die in *Callbacks* ankommen. Siehe [Variablen und Typen](../language/variables-and-types) für mehr über Records.

## Callbacks: über den Namen übergebene Funktionen

Die Ereignis-Handler (`onChange`, `onSubmit`, `onConfirm`, ...) erhalten den **Namen einer Funktion**. Diese Funktion wird vom Framework aufgerufen, wenn das Ereignis eintritt:

```npas
function aoEnviar(v): Boolean
begin
    WriteLn("Enviado: " + v);
    return true;
end;

// ... dentro do ui():
TextInput(#{ placeholder: "Digite e Enter", onSubmit: aoEnviar })
```

Per Konvention geben die *Callbacks* `Boolean` zurück (normalerweise `true`). Wichtig ist der Nebeneffekt: das Ändern der Zustandsvariablen.

## Beenden-Tasten

Um eine TerminalInk-Anwendung zu beenden:

- **Esc** oder **Strg+C** beenden immer.
- Die Taste **`q`** beendet ebenfalls — **aber nur** dann, wenn kein `TextInput` (oder Eingabefeld) den Fokus hat. Wenn ein Textfeld fokussiert ist, wird `q` ganz normal in das Feld eingegeben.

::: warning Fokus und die Taste `q`
Wenn Ihre Oberfläche Eingabefelder hat, weisen Sie den Benutzer besser an, mit **Esc** zu beenden. Die Taste `q` ist nur dann ein Beenden-Kürzel, wenn der Fokus nicht auf einem editierbaren Feld liegt.
:::

## Wie es unter der Haube funktioniert

- Die Grundlage ist **Lanterna**, das ein plattformübergreifendes Terminal mit **gepuffertem Bildschirm** und Tastendekodierung bereitstellt.
- Jede höherwertige Komponente (`VBox`, `Badge`, `Spinner`, ...) **expandiert** zu einem Baum aus Kästen und Texten (ein Layout im *Flexbox*-Stil).
- Eine kleine **Layout-Engine** berechnet Positionen und Größen; anschließend zeichnet ein **Renderer** den Baum Frame für Frame auf den gepufferten Bildschirm.

Sie müssen diese Details nicht verstehen, um Anwendungen zu schreiben — aber sie erklären, warum das Modell ein Immediate Mode ist und warum sich Layout-Props wie `flexDirection`, `gap` und `flexGrow` wie bei *Flexbox* verhalten.

---

Lernen Sie als Nächstes alle verfügbaren [Komponenten](./components) kennen.
