# Librerie interne

NeoObjectPascal include cinque librerie pronte all'uso, impacchettate all'interno dell'interprete stesso. Coprono attività comuni di **testo, matematica, date, file e collezioni**. Carica una qualsiasi con il prefisso `internal.`:

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

Poiché vengono caricate dall'interno del JAR, sono sempre disponibili, indipendentemente dalla directory del progetto. Consulta [Moduli e uses](./modules) per il funzionamento di `uses`.

## `internal.string`

Funzioni per ispezionare, formattare e trasformare le stringhe.

| Funzione | Descrizione |
| ------ | --------- |
| `isEmpty(s)` / `isNotEmpty(s)` | Verifica se la stringa è (o non è) vuota |
| `strLength(s)` | Lunghezza della stringa |
| `toUpperCase(s)` / `toLowerCase(s)` | Converte in maiuscolo / minuscolo |
| `trim(s)` | Rimuove gli spazi alle estremità |
| `repeat(s, count)` | Ripete la stringa `count` volte |
| `join(sep, s1, s2)` | Unisce 2 stringhe con un separatore |
| `join3(sep, s1, s2, s3)` / `join4(...)` | Unisce 3 o 4 stringhe |
| `quote(s)` / `singleQuote(s)` | Racchiude tra virgolette doppie / singole |
| `bracket(s)` / `parenthesize(s)` | Racchiude tra parentesi quadre / tonde |
| `inStr(haystack, needle)` | Posizione della sottostringa (1-based; 0 se assente) |
| `contains(haystack, needle)` | Se contiene la sottostringa |
| `startsWith(s, prefix)` / `endsWith(s, suffix)` | Se inizia / termina con il segmento |
| `subString(s, from, len)` | Estrae `len` caratteri a partire da `from` (1-based) |
| `replaceStr(s, from, to)` | Sostituisce tutte le occorrenze di `from` con `to` |
| `intToStr(n)` / `strToInt(s)` | Converte tra intero e stringa |

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

Funzioni matematiche per gli interi.

| Funzione | Descrizione |
| ------ | --------- |
| `abs(x)` | Valore assoluto |
| `max(a, b)` / `min(a, b)` | Maggiore / minore tra due valori |
| `square(x)` / `cube(x)` | Quadrato / cubo |
| `factorial(n)` | Fattoriale di `n` |
| `fibonacci(n)` | N-esimo numero di Fibonacci |
| `isEven(n)` / `isOdd(n)` | Se è pari / dispari |
| `gcd(a, b)` / `lcm(a, b)` | Massimo comune divisore / minimo comune multiplo |
| `sign(x)` | Segno del numero (-1, 0 o 1) |
| `clamp(value, minVal, maxVal)` | Limita `value` all'intervallo `[minVal, maxVal]` |

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

Funzioni per ottenere, validare, formattare e calcolare date e orari.

| Categoria | Funzioni |
| --------- | ------- |
| Data/ora corrente | `getCurrentYear`, `getCurrentMonth`, `getCurrentDay`, `getCurrentHour`, `getCurrentMinute`, `getCurrentSecond` |
| Formattazione | `formatDate(y, m, d)`, `formatTime(h, m, s)`, `getCurrentDate`, `getCurrentTime`, `getCurrentDateTime` |
| Validazione | `isLeapYear(year)`, `isValidDate(y, m, d)`, `isValidTime(h, m, s)` |
| Calcolo | `getDaysInMonth(y, m)`, `getAge(ano, mes, dia)`, `addDays(...)`, `addMonths(...)`, `addYears(...)` |
| Nomi | `getMonthName`, `getMonthNameEn`, `getDayOfWeekName`, `getDayOfWeekNameEn` |

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

Funzioni per lavorare con percorsi e file.

| Categoria | Funzioni |
| --------- | ------- |
| Componenti del percorso | `getFileExtension`, `getFileName`, `getFileNameWithoutExtension`, `getParentDirectory`, `joinPath` |
| Normalizzazione | `normalizePath`, `isAbsolutePath`, `isRelativePath` |
| Validazione del nome | `isValidFilename`, `sanitizeFilename`, `createTempFilename` |
| File system | `fileExists`, `directoryExists`, `isFile`, `isDirectory`, `getFileSize` |
| Classificazione | `isTextFile`, `isBinaryFile`, `formatFileSize` |

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

Funzioni per manipolare array e liste.

| Categoria | Funzioni |
| --------- | ------- |
| Di base | `createArray`, `arraySize`, `arrayIsEmpty`, `arrayFirst`, `arrayLast` |
| Ricerca | `arrayContains`, `arrayIndexOf` |
| Trasformazione | `arrayReverse`, `arraySlice`, `arraySort`, `arraySortDesc`, `arrayUnique`, `arrayConcat` |
| Aggregazione | `arraySum`, `arrayMax`, `arrayMin`, `arrayJoin`, `arrayReduce` |
| Ordine superiore | `arrayMap`, `arrayFilter`, `arrayRange` |

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

::: tip Array nativi
Nella maggior parte dei casi, gli array nativi del linguaggio (`[10, 20, 30]`, indice `a[0]`, `for x in a do`) sono già sufficienti. Vedi [Array](../language/arrays). La libreria `internal.collections` li completa con utility con nome.
:::

## Combinare le librerie

Nulla impedisce di caricare più librerie in una volta sola e combinarle liberamente:

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

Di seguito, garantisci la qualità del tuo codice con i [Test di unità](../testing/unit-testing).
