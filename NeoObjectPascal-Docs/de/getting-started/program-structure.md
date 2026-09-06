# Struktur eines Programms

Ein Programm in NeoObjectPascal besteht aus drei Teilen, die alle optional sind, mit Ausnahme des Abschlusses:

1. Eine **`uses`**-Klausel (Import von Modulen), abgeschlossen durch `;`.
2. Null oder mehr **Deklarationen** (Variablen, Funktionen, Klassen, Schnittstellen, Tests), jeweils abgeschlossen durch `;`.
3. Ein **Hauptblock** `begin ... end.` — der abschließende Punkt `.` beendet das Programm.

```npas
uses internal.string;

var mensagem: String;

function emMaiusculas(s: String): String
begin
    return toUpperCase(s);
end;

begin
    mensagem := emMaiusculas("olá");
    WriteLn(mensagem);
end.
```

<Output>
OLÁ
</Output>

::: warning Kein `program`-Header
Anders als in einigen Pascal-Dialekten verwendet NeoObjectPascal **keinen** `program Name;`-Header. Das Programm beginnt direkt mit der `uses`-Klausel, den Deklarationen oder dem `begin`-Block.
:::

## Der Hauptblock

Der Block `begin ... end.` enthält die Anweisungen, die beim Programmablauf ausgeführt werden. Jede Anweisung endet mit einem Semikolon `;`:

```npas
begin
    WriteLn("primeira linha");
    WriteLn("segunda linha");
end.
```

Beachten Sie den **abschließenden Punkt** nach dem `end`, das das Programm schließt — er ist obligatorisch. Innere Blöcke (von Funktionen, Schleifen usw.) enden nur mit `end` (ohne Punkt).

## Kommentare

NeoObjectPascal kennt **ausschließlich einzeilige Kommentare**, eingeleitet durch `//`:

```npas
begin
    // Dies ist ein Kommentar
    WriteLn("Oi"); // funktioniert auch am Zeilenende
end.
```

::: warning Keine Blockkommentare
Es gibt keine Kommentare der Form `{ ... }` oder `(* ... *)`. Die geschweiften Klammern `{ }` sind für [Java-Codeblöcke](../features/java-integration) reserviert.
:::

## Deklarationen an oberster Stelle vs. innerhalb des Blocks

Sie können Variablen sowohl am Anfang der Datei als auch innerhalb eines Blocks deklarieren:

```npas
var x: Integer;   // Deklaration an oberster Stelle

begin
    x := 10;
    var y: Integer;   // Deklaration innerhalb des Blocks
    y := 20;
    WriteLn(x + y);
end.
```

<Output>
30
</Output>

Nachdem die Struktur nun steht, fahren Sie fort mit [Variablen und Typen](../language/variables-and-types).
