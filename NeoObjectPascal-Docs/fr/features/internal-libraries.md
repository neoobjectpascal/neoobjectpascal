# Bibliothèques internes

NeoObjectPascal est livré avec cinq bibliothèques prêtes à l'emploi, empaquetées dans l'interpréteur lui-même. Elles couvrent des tâches courantes de **texte, mathématiques, dates, fichiers et collections**. Chargez-en n'importe laquelle avec le préfixe `internal.` :

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

Comme elles sont chargées depuis l'intérieur du JAR, elles sont toujours disponibles, quel que soit le répertoire du projet. Consultez [Modules et uses](./modules) pour la mécanique de `uses`.

## `internal.string`

Fonctions pour inspecter, formater et transformer des chaînes.

| Fonction | Description |
| ------ | --------- |
| `isEmpty(s)` / `isNotEmpty(s)` | Vérifie si la chaîne est (ou non) vide |
| `strLength(s)` | Longueur de la chaîne |
| `toUpperCase(s)` / `toLowerCase(s)` | Convertit en majuscules / minuscules |
| `trim(s)` | Supprime les espaces aux extrémités |
| `repeat(s, count)` | Répète la chaîne `count` fois |
| `join(sep, s1, s2)` | Joint 2 chaînes avec un séparateur |
| `join3(sep, s1, s2, s3)` / `join4(...)` | Joint 3 ou 4 chaînes |
| `quote(s)` / `singleQuote(s)` | Entoure de guillemets doubles / simples |
| `bracket(s)` / `parenthesize(s)` | Entoure de crochets / parenthèses |
| `inStr(haystack, needle)` | Position de la sous-chaîne (base 1 ; 0 si absente) |
| `contains(haystack, needle)` | Si elle contient la sous-chaîne |
| `startsWith(s, prefix)` / `endsWith(s, suffix)` | Si elle commence / termine par le fragment |
| `subString(s, from, len)` | Extrait `len` caractères à partir de `from` (base 1) |
| `replaceStr(s, from, to)` | Remplace toutes les occurrences de `from` par `to` |
| `intToStr(n)` / `strToInt(s)` | Convertit entre entier et chaîne |

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

Fonctions mathématiques pour les entiers.

| Fonction | Description |
| ------ | --------- |
| `abs(x)` | Valeur absolue |
| `max(a, b)` / `min(a, b)` | Plus grand / plus petit entre deux valeurs |
| `square(x)` / `cube(x)` | Carré / cube |
| `factorial(n)` | Factorielle de `n` |
| `fibonacci(n)` | N-ième nombre de Fibonacci |
| `isEven(n)` / `isOdd(n)` | Si pair / impair |
| `gcd(a, b)` / `lcm(a, b)` | Plus grand commun diviseur / plus petit commun multiple |
| `sign(x)` | Signe du nombre (-1, 0 ou 1) |
| `clamp(value, minVal, maxVal)` | Borne `value` à l'intervalle `[minVal, maxVal]` |

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

Fonctions pour obtenir, valider, formater et calculer des dates et des heures.

| Catégorie | Fonctions |
| --------- | ------- |
| Date/heure actuelle | `getCurrentYear`, `getCurrentMonth`, `getCurrentDay`, `getCurrentHour`, `getCurrentMinute`, `getCurrentSecond` |
| Formatage | `formatDate(y, m, d)`, `formatTime(h, m, s)`, `getCurrentDate`, `getCurrentTime`, `getCurrentDateTime` |
| Validation | `isLeapYear(year)`, `isValidDate(y, m, d)`, `isValidTime(h, m, s)` |
| Calcul | `getDaysInMonth(y, m)`, `getAge(ano, mes, dia)`, `addDays(...)`, `addMonths(...)`, `addYears(...)` |
| Noms | `getMonthName`, `getMonthNameEn`, `getDayOfWeekName`, `getDayOfWeekNameEn` |

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

Fonctions pour travailler avec les chemins et les fichiers.

| Catégorie | Fonctions |
| --------- | ------- |
| Composants du chemin | `getFileExtension`, `getFileName`, `getFileNameWithoutExtension`, `getParentDirectory`, `joinPath` |
| Normalisation | `normalizePath`, `isAbsolutePath`, `isRelativePath` |
| Validation de nom | `isValidFilename`, `sanitizeFilename`, `createTempFilename` |
| Système de fichiers | `fileExists`, `directoryExists`, `isFile`, `isDirectory`, `getFileSize` |
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

Fonctions pour manipuler les tableaux et les listes.

| Catégorie | Fonctions |
| --------- | ------- |
| Basiques | `createArray`, `arraySize`, `arrayIsEmpty`, `arrayFirst`, `arrayLast` |
| Recherche | `arrayContains`, `arrayIndexOf` |
| Transformation | `arrayReverse`, `arraySlice`, `arraySort`, `arraySortDesc`, `arrayUnique`, `arrayConcat` |
| Agrégation | `arraySum`, `arrayMax`, `arrayMin`, `arrayJoin`, `arrayReduce` |
| Ordre supérieur | `arrayMap`, `arrayFilter`, `arrayRange` |

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

::: tip Tableaux natifs
Dans la plupart des cas, les tableaux natifs du langage (`[10, 20, 30]`, index `a[0]`, `for x in a do`) suffisent. Voir [Tableaux](../language/arrays). La bibliothèque `internal.collections` les complète avec des utilitaires nommés.
:::

## Combiner les bibliothèques

Rien n'empêche de charger plusieurs bibliothèques à la fois et de les combiner librement :

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

Ensuite, assurez la qualité de votre code avec [Tests unitaires](../testing/unit-testing).
