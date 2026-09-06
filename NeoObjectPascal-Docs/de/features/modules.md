# Module und uses

Wenn ein Programm wächst, ist es sinnvoll, es in kleinere, wiederverwendbare Dateien aufzuteilen. In NeoObjectPascal geschieht das über die `uses`-Klausel, die sowohl **interne Bibliotheken** als auch **Module in Dateien** lädt, die Sie selbst schreiben.

## Die `uses`-Klausel

Die `uses`-Klausel steht am **Anfang** des Programms, vor jeder Deklaration. Sie listet die zu ladenden Module durch Kommas getrennt auf und endet mit `;`:

```npas
uses internal.math, internal.string;

begin
    WriteLn("Máximo: ", max(10, 20));
    WriteLn("Maiúsculas: ", toUpperCase("neo"));
end.
```

<Output>
Máximo: 20
Maiúsculas: NEO
</Output>

Nach dem `uses` stehen alle Funktionen der Module zur Verfügung, als wären sie in Ihrer eigenen Datei deklariert worden.

## Interne Bibliotheken vs. Module in Dateien

Es gibt zwei Arten von Modulen, unterschieden durch das Präfix:

| Form                   | Herkunft                                          | Beispiel                   |
| ---------------------- | ------------------------------------------------- | -------------------------- |
| `internal.<name>`      | In den Interpreter selbst eingebettete Bibliotheken | `uses internal.datetime;`  |
| `<ordner>.<modul>`     | `.npas`-Datei relativ zu Ihrem Programm           | `uses utils.matematica;`   |

Das Präfix `internal.` ist reserviert: Die Laufzeitumgebung erkennt es und lädt die Bibliothek aus dem JAR heraus. Jeder andere Pfad wird als Datei in Ihrem Projekt behandelt.

## Ein Modul in einer Datei erstellen

Ein Modul ist einfach eine `.npas`-Datei mit Deklarationen von Funktionen, Prozeduren oder Variablen. Erstellen Sie zum Beispiel `matematica.npas`:

```npas
// matematica.npas
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

function subtrair(a: Integer, b: Integer): Integer
begin
    return a - b;
end;
```

Und verwenden Sie es im Hauptprogramm, im selben Verzeichnis:

```npas
// programa.npas
uses matematica;

begin
    WriteLn(somar(10, 5));
    WriteLn(subtrair(10, 5));
end.
```

<Output>
15
5
</Output>

Der Interpreter sucht nach `matematica.npas` ausgehend vom Verzeichnis der Hauptdatei.

::: warning Der Parametertrenner ist das Komma
Trennen Sie bei der Deklaration typisierter Parameter diese mit einem **Komma**: `function somar(a: Integer, b: Integer)`. Verwenden Sie zwischen Parametern kein Semikolon.
:::

## Hierarchische Module

Sie können Module in Unterordnern organisieren und dabei den Punkt als Pfadtrenner verwenden. `uses utils.matematica;` lädt die Datei `utils/matematica.npas`, relativ zum Hauptprogramm:

```text
projeto/
├── programa.npas
└── utils/
    └── matematica.npas
```

```npas
// programa.npas
uses utils.matematica;

begin
    WriteLn(somar(2, 3));
end.
```

<Output>
5
</Output>

## Mehrere Module laden

Eine einzige `uses`-Klausel kann interne Bibliotheken und eigene Module mischen, alle durch Kommas getrennt:

```npas
uses internal.math, internal.string, utils.matematica;

begin
    WriteLn(square(4));              // de internal.math
    WriteLn(quote("olá"));          // de internal.string
    WriteLn(somar(1, 1));           // do seu módulo utils.matematica
end.
```

<Output>
16
"olá"
2
</Output>

## Bewährte Praktiken

- Fassen Sie verwandte Funktionen in einem gemeinsamen Modul zusammen (zum Beispiel alles rund um Text in einer `texto.npas`).
- Bevorzugen Sie viele kleine, kohärente Dateien gegenüber einer einzigen riesigen Datei.
- Verwenden Sie Unterordner (`utils.`, `dominio.`), um Module nach Systembereich zu organisieren.
- Es gibt nur eine `uses`-Klausel pro Programm: Listen Sie alle Module darin auf.

---

Lernen Sie als Nächstes jede der [internen Bibliotheken](./internal-libraries) im Detail kennen.
