# Bibliotecas internas

O NeoObjectPascal acompanha cinco bibliotecas prontas para uso, empacotadas dentro do próprio interpretador. Elas cobrem tarefas comuns de **texto, matemática, datas, arquivos e coleções**. Carregue qualquer uma com o prefixo `internal.`:

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

Como são carregadas de dentro do JAR, estão sempre disponíveis, independentemente do diretório do projeto. Consulte [Módulos e uses](./modules) para a mecânica do `uses`.

## `internal.string`

Funções para inspecionar, formatar e transformar strings.

| Função | Descrição |
| ------ | --------- |
| `isEmpty(s)` / `isNotEmpty(s)` | Verifica se a string está (ou não) vazia |
| `strLength(s)` | Comprimento da string |
| `toUpperCase(s)` / `toLowerCase(s)` | Converte para maiúsculas / minúsculas |
| `trim(s)` | Remove espaços nas pontas |
| `repeat(s, count)` | Repete a string `count` vezes |
| `join(sep, s1, s2)` | Junta 2 strings com um separador |
| `join3(sep, s1, s2, s3)` / `join4(...)` | Junta 3 ou 4 strings |
| `quote(s)` / `singleQuote(s)` | Envolve em aspas duplas / simples |
| `bracket(s)` / `parenthesize(s)` | Envolve em colchetes / parênteses |
| `inStr(haystack, needle)` | Posição da substring (1-based; 0 se ausente) |
| `contains(haystack, needle)` | Se contém a substring |
| `startsWith(s, prefix)` / `endsWith(s, suffix)` | Se começa / termina com o trecho |
| `subString(s, from, len)` | Extrai `len` caracteres a partir de `from` (1-based) |
| `replaceStr(s, from, to)` | Substitui todas as ocorrências de `from` por `to` |
| `intToStr(n)` / `strToInt(s)` | Converte entre inteiro e string |

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

Funções matemáticas para inteiros.

| Função | Descrição |
| ------ | --------- |
| `abs(x)` | Valor absoluto |
| `max(a, b)` / `min(a, b)` | Maior / menor entre dois valores |
| `square(x)` / `cube(x)` | Quadrado / cubo |
| `factorial(n)` | Fatorial de `n` |
| `fibonacci(n)` | N-ésimo número de Fibonacci |
| `isEven(n)` / `isOdd(n)` | Se é par / ímpar |
| `gcd(a, b)` / `lcm(a, b)` | Máximo divisor comum / mínimo múltiplo comum |
| `sign(x)` | Sinal do número (-1, 0 ou 1) |
| `clamp(value, minVal, maxVal)` | Limita `value` ao intervalo `[minVal, maxVal]` |

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

Funções para obter, validar, formatar e calcular datas e horas.

| Categoria | Funções |
| --------- | ------- |
| Data/hora atual | `getCurrentYear`, `getCurrentMonth`, `getCurrentDay`, `getCurrentHour`, `getCurrentMinute`, `getCurrentSecond` |
| Formatação | `formatDate(y, m, d)`, `formatTime(h, m, s)`, `getCurrentDate`, `getCurrentTime`, `getCurrentDateTime` |
| Validação | `isLeapYear(year)`, `isValidDate(y, m, d)`, `isValidTime(h, m, s)` |
| Cálculo | `getDaysInMonth(y, m)`, `getAge(ano, mes, dia)`, `addDays(...)`, `addMonths(...)`, `addYears(...)` |
| Nomes | `getMonthName`, `getMonthNameEn`, `getDayOfWeekName`, `getDayOfWeekNameEn` |

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

Funções para trabalhar com caminhos e arquivos.

| Categoria | Funções |
| --------- | ------- |
| Componentes do caminho | `getFileExtension`, `getFileName`, `getFileNameWithoutExtension`, `getParentDirectory`, `joinPath` |
| Normalização | `normalizePath`, `isAbsolutePath`, `isRelativePath` |
| Validação de nome | `isValidFilename`, `sanitizeFilename`, `createTempFilename` |
| Sistema de arquivos | `fileExists`, `directoryExists`, `isFile`, `isDirectory`, `getFileSize` |
| Classificação | `isTextFile`, `isBinaryFile`, `formatFileSize` |

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

Funções para manipular arrays e listas.

| Categoria | Funções |
| --------- | ------- |
| Básicas | `createArray`, `arraySize`, `arrayIsEmpty`, `arrayFirst`, `arrayLast` |
| Busca | `arrayContains`, `arrayIndexOf` |
| Transformação | `arrayReverse`, `arraySlice`, `arraySort`, `arraySortDesc`, `arrayUnique`, `arrayConcat` |
| Agregação | `arraySum`, `arrayMax`, `arrayMin`, `arrayJoin`, `arrayReduce` |
| Ordem superior | `arrayMap`, `arrayFilter`, `arrayRange` |

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

::: tip Arrays nativos
Para a maioria dos casos, os arrays nativos da linguagem (`[10, 20, 30]`, índice `a[0]`, `for x in a do`) já resolvem. Veja [Arrays](../language/arrays). A biblioteca `internal.collections` complementa com utilitários nomeados.
:::

## Combinando bibliotecas

Nada impede carregar várias bibliotecas de uma vez e combiná-las livremente:

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

A seguir, garanta a qualidade do seu código com [Testes de unidade](../testing/unit-testing).
