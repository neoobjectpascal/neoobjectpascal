# Internal Libraries

NeoObjectPascal ships with five ready-to-use libraries, bundled inside the interpreter itself. They cover common tasks involving **text, math, dates, files, and collections**. Load any of them with the `internal.` prefix:

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

Because they are loaded from inside the JAR, they are always available, regardless of the project directory. See [Modules and uses](./modules) for the mechanics of `uses`.

## `internal.string`

Functions to inspect, format, and transform strings.

| Function | Description |
| ------ | --------- |
| `isEmpty(s)` / `isNotEmpty(s)` | Checks whether the string is (or is not) empty |
| `strLength(s)` | Length of the string |
| `toUpperCase(s)` / `toLowerCase(s)` | Converts to upper / lower case |
| `trim(s)` | Removes surrounding whitespace |
| `repeat(s, count)` | Repeats the string `count` times |
| `join(sep, s1, s2)` | Joins 2 strings with a separator |
| `join3(sep, s1, s2, s3)` / `join4(...)` | Joins 3 or 4 strings |
| `quote(s)` / `singleQuote(s)` | Wraps in double / single quotes |
| `bracket(s)` / `parenthesize(s)` | Wraps in brackets / parentheses |
| `inStr(haystack, needle)` | Position of the substring (1-based; 0 if absent) |
| `contains(haystack, needle)` | Whether it contains the substring |
| `startsWith(s, prefix)` / `endsWith(s, suffix)` | Whether it starts / ends with the given piece |
| `subString(s, from, len)` | Extracts `len` characters starting at `from` (1-based) |
| `replaceStr(s, from, to)` | Replaces all occurrences of `from` with `to` |
| `intToStr(n)` / `strToInt(s)` | Converts between integer and string |

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

Mathematical functions for integers.

| Function | Description |
| ------ | --------- |
| `abs(x)` | Absolute value |
| `max(a, b)` / `min(a, b)` | Larger / smaller of two values |
| `square(x)` / `cube(x)` | Square / cube |
| `factorial(n)` | Factorial of `n` |
| `fibonacci(n)` | The n-th Fibonacci number |
| `isEven(n)` / `isOdd(n)` | Whether it is even / odd |
| `gcd(a, b)` / `lcm(a, b)` | Greatest common divisor / least common multiple |
| `sign(x)` | Sign of the number (-1, 0, or 1) |
| `clamp(value, minVal, maxVal)` | Constrains `value` to the range `[minVal, maxVal]` |

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

Functions to obtain, validate, format, and compute dates and times.

| Category | Functions |
| --------- | ------- |
| Current date/time | `getCurrentYear`, `getCurrentMonth`, `getCurrentDay`, `getCurrentHour`, `getCurrentMinute`, `getCurrentSecond` |
| Formatting | `formatDate(y, m, d)`, `formatTime(h, m, s)`, `getCurrentDate`, `getCurrentTime`, `getCurrentDateTime` |
| Validation | `isLeapYear(year)`, `isValidDate(y, m, d)`, `isValidTime(h, m, s)` |
| Computation | `getDaysInMonth(y, m)`, `getAge(ano, mes, dia)`, `addDays(...)`, `addMonths(...)`, `addYears(...)` |
| Names | `getMonthName`, `getMonthNameEn`, `getDayOfWeekName`, `getDayOfWeekNameEn` |

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

Functions to work with paths and files.

| Category | Functions |
| --------- | ------- |
| Path components | `getFileExtension`, `getFileName`, `getFileNameWithoutExtension`, `getParentDirectory`, `joinPath` |
| Normalization | `normalizePath`, `isAbsolutePath`, `isRelativePath` |
| Name validation | `isValidFilename`, `sanitizeFilename`, `createTempFilename` |
| File system | `fileExists`, `directoryExists`, `isFile`, `isDirectory`, `getFileSize` |
| Classification | `isTextFile`, `isBinaryFile`, `formatFileSize` |

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

Functions to manipulate arrays and lists.

| Category | Functions |
| --------- | ------- |
| Basic | `createArray`, `arraySize`, `arrayIsEmpty`, `arrayFirst`, `arrayLast` |
| Search | `arrayContains`, `arrayIndexOf` |
| Transformation | `arrayReverse`, `arraySlice`, `arraySort`, `arraySortDesc`, `arrayUnique`, `arrayConcat` |
| Aggregation | `arraySum`, `arrayMax`, `arrayMin`, `arrayJoin`, `arrayReduce` |
| Higher-order | `arrayMap`, `arrayFilter`, `arrayRange` |

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

::: tip Native arrays
For most cases, the language's native arrays (`[10, 20, 30]`, indexing `a[0]`, `for x in a do`) already do the job. See [Arrays](../language/arrays). The `internal.collections` library complements them with named utilities.
:::

## Combining libraries

Nothing stops you from loading several libraries at once and combining them freely:

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

Next, ensure the quality of your code with [Unit Testing](../testing/unit-testing).
