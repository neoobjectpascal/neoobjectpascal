# Interne Bibliotheken

NeoObjectPascal wird mit fünf einsatzbereiten Bibliotheken ausgeliefert, die im Interpreter selbst eingebettet sind. Sie decken gängige Aufgaben rund um **Text, Mathematik, Datumsangaben, Dateien und Sammlungen** ab. Laden Sie eine beliebige davon mit dem Präfix `internal.`:

```npas
uses internal.string, internal.math, internal.datetime;

begin
    WriteLn(toUpperCase("neo"));
    WriteLn(factorial(5));
    WriteLn(getMonthName(7));
end.
```

<Output>
NEO
120
Julho
</Output>

Da sie aus dem JAR heraus geladen werden, sind sie unabhängig vom Projektverzeichnis immer verfügbar. Siehe [Module und uses](./modules) für die Funktionsweise von `uses`.

## `internal.string`

Funktionen zum Inspizieren, Formatieren und Umwandeln von Zeichenketten.

| Funktion | Beschreibung |
| ------ | --------- |
| `isEmpty(s)` / `isNotEmpty(s)` | Prüft, ob die Zeichenkette (nicht) leer ist |
| `strLength(s)` | Länge der Zeichenkette |
| `toUpperCase(s)` / `toLowerCase(s)` | Wandelt in Groß- / Kleinbuchstaben um |
| `trim(s)` | Entfernt Leerzeichen an den Rändern |
| `repeat(s, count)` | Wiederholt die Zeichenkette `count`-mal |
| `join(sep, s1, s2)` | Verbindet 2 Zeichenketten mit einem Trennzeichen |
| `join3(sep, s1, s2, s3)` / `join4(...)` | Verbindet 3 oder 4 Zeichenketten |
| `quote(s)` / `singleQuote(s)` | Umschließt mit doppelten / einfachen Anführungszeichen |
| `bracket(s)` / `parenthesize(s)` | Umschließt mit eckigen Klammern / runden Klammern |
| `inStr(haystack, needle)` | Position der Teilzeichenkette (1-basiert; 0 wenn nicht vorhanden) |
| `contains(haystack, needle)` | Ob die Teilzeichenkette enthalten ist |
| `startsWith(s, prefix)` / `endsWith(s, suffix)` | Ob mit dem Abschnitt beginnt / endet |
| `subString(s, from, len)` | Extrahiert `len` Zeichen ab `from` (1-basiert) |
| `replaceStr(s, from, to)` | Ersetzt alle Vorkommen von `from` durch `to` |
| `intToStr(n)` / `strToInt(s)` | Wandelt zwischen Ganzzahl und Zeichenkette um |

```npas
uses internal.string;

begin
    WriteLn(quote("NeoObjectPascal"));
    WriteLn(repeat("*", 3));
    WriteLn(contains("NeoObjectPascal", "Pascal"));
    WriteLn(join(" ", "João", "Silva"));
end.
```

<Output>
"NeoObjectPascal"
***
true
João Silva
</Output>

## `internal.math`

Mathematische Funktionen für Ganzzahlen.

| Funktion | Beschreibung |
| ------ | --------- |
| `abs(x)` | Absolutwert |
| `max(a, b)` / `min(a, b)` | Größerer / kleinerer von zwei Werten |
| `square(x)` / `cube(x)` | Quadrat / Kubus |
| `factorial(n)` | Fakultät von `n` |
| `fibonacci(n)` | N-te Fibonacci-Zahl |
| `isEven(n)` / `isOdd(n)` | Ob gerade / ungerade |
| `gcd(a, b)` / `lcm(a, b)` | Größter gemeinsamer Teiler / kleinstes gemeinsames Vielfaches |
| `sign(x)` | Vorzeichen der Zahl (-1, 0 oder 1) |
| `clamp(value, minVal, maxVal)` | Begrenzt `value` auf das Intervall `[minVal, maxVal]` |

```npas
uses internal.math;

begin
    WriteLn(abs(-15));
    WriteLn(factorial(5));
    WriteLn(gcd(24, 36));
    WriteLn(clamp(120, 0, 100));
end.
```

<Output>
15
120
12
100
</Output>

## `internal.datetime`

Funktionen zum Abrufen, Validieren, Formatieren und Berechnen von Datumsangaben und Uhrzeiten.

| Kategorie | Funktionen |
| --------- | ------- |
| Aktuelles Datum/Uhrzeit | `getCurrentYear`, `getCurrentMonth`, `getCurrentDay`, `getCurrentHour`, `getCurrentMinute`, `getCurrentSecond` |
| Formatierung | `formatDate(y, m, d)`, `formatTime(h, m, s)`, `getCurrentDate`, `getCurrentTime`, `getCurrentDateTime` |
| Validierung | `isLeapYear(year)`, `isValidDate(y, m, d)`, `isValidTime(h, m, s)` |
| Berechnung | `getDaysInMonth(y, m)`, `getAge(ano, mes, dia)`, `addDays(...)`, `addMonths(...)`, `addYears(...)` |
| Namen | `getMonthName`, `getMonthNameEn`, `getDayOfWeekName`, `getDayOfWeekNameEn` |

```npas
uses internal.datetime;

begin
    WriteLn("Ano atual: ", getCurrentYear());
    WriteLn("2024 é bissexto? ", isLeapYear(2024));
    WriteLn("Dias em fevereiro/2024: ", getDaysInMonth(2024, 2));
    WriteLn("Nome do mês 10: ", getMonthName(10));
end.
```

<Output>
Ano atual: 2026
2024 é bissexto? true
Dias em fevereiro/2024: 29
Nome do mês 10: Outubro
</Output>

## `internal.file`

Funktionen zum Arbeiten mit Pfaden und Dateien.

| Kategorie | Funktionen |
| --------- | ------- |
| Pfadbestandteile | `getFileExtension`, `getFileName`, `getFileNameWithoutExtension`, `getParentDirectory`, `joinPath` |
| Normalisierung | `normalizePath`, `isAbsolutePath`, `isRelativePath` |
| Namensvalidierung | `isValidFilename`, `sanitizeFilename`, `createTempFilename` |
| Dateisystem | `fileExists`, `directoryExists`, `isFile`, `isDirectory`, `getFileSize` |
| Klassifizierung | `isTextFile`, `isBinaryFile`, `formatFileSize` |

```npas
uses internal.file;

begin
    WriteLn(getFileExtension("relatorio.pdf"));
    WriteLn(getFileName("/docs/notas.txt"));
    WriteLn(joinPath("docs", "notas.txt"));
    WriteLn(isTextFile("dados.csv"));
end.
```

<Output>
pdf
notas.txt
docs/notas.txt
true
</Output>

## `internal.collections`

Funktionen zum Bearbeiten von Arrays und Listen.

| Kategorie | Funktionen |
| --------- | ------- |
| Grundlegend | `createArray`, `arraySize`, `arrayIsEmpty`, `arrayFirst`, `arrayLast` |
| Suche | `arrayContains`, `arrayIndexOf` |
| Transformation | `arrayReverse`, `arraySlice`, `arraySort`, `arraySortDesc`, `arrayUnique`, `arrayConcat` |
| Aggregation | `arraySum`, `arrayMax`, `arrayMin`, `arrayJoin`, `arrayReduce` |
| Höhere Ordnung | `arrayMap`, `arrayFilter`, `arrayRange` |

```npas
uses internal.collections;

var lista: Object;

begin
    lista := createArray();
    WriteLn("Vazio? ", arrayIsEmpty(lista));
    WriteLn("Tamanho: ", arraySize(lista));
end.
```

<Output>
Vazio? true
Tamanho: 0
</Output>

::: tip Native Arrays
Für die meisten Fälle reichen die nativen Arrays der Sprache (`[10, 20, 30]`, Index `a[0]`, `for x in a do`) bereits aus. Siehe [Arrays](../language/arrays). Die Bibliothek `internal.collections` ergänzt sie um benannte Hilfsfunktionen.
:::

## Bibliotheken kombinieren

Nichts hindert Sie daran, mehrere Bibliotheken auf einmal zu laden und frei zu kombinieren:

```npas
uses internal.string, internal.math;

begin
    WriteLn(quote(intToStr(square(8))));
end.
```

<Output>
"64"
</Output>

---

Stellen Sie als Nächstes die Qualität Ihres Codes mit [Unit-Tests](../testing/unit-testing) sicher.
