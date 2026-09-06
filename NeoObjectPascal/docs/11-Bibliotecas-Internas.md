# Bibliotecas Internas do NeoObjectPascal

O NeoObjectPascal inclui um sistema de bibliotecas internas que fornece funcionalidades comuns prontas para uso. Essas bibliotecas são empacotadas dentro do JAR e podem ser importadas usando a sintaxe `uses internal.[nome]`.

## Como Usar

```pascal
uses internal.math, internal.string, internal.datetime;

begin
    WriteLn("Valor absoluto de -5: ", abs(-5));
    WriteLn("Texto entre aspas: ", quote("Hello"));
    WriteLn("Ano atual: ", getCurrentYear());
end.
```

## Bibliotecas Disponíveis

### 1. **internal.math** - Funções Matemáticas

Biblioteca com funções matemáticas básicas e utilitárias.

#### Funções Básicas:
- `abs(x: Integer): Integer` - Valor absoluto
- `max(a: Integer, b: Integer): Integer` - Valor máximo
- `min(a: Integer, b: Integer): Integer` - Valor mínimo
- `sign(x: Integer): Integer` - Sinal do número (-1, 0, 1)

#### Operações Avançadas:
- `square(x: Integer): Integer` - Quadrado do número
- `cube(x: Integer): Integer` - Cubo do número
- `factorial(n: Integer): Integer` - Fatorial
- `fibonacci(n: Integer): Integer` - Sequência de Fibonacci

#### Funções de Teste:
- `isEven(n: Integer): Boolean` - Verifica se é par
- `isOdd(n: Integer): Boolean` - Verifica se é ímpar

#### Funções Matemáticas:
- `gcd(a: Integer, b: Integer): Integer` - Máximo Divisor Comum
- `lcm(a: Integer, b: Integer): Integer` - Mínimo Múltiplo Comum
- `clamp(value: Integer, minVal: Integer, maxVal: Integer): Integer` - Limita valor entre min e max

**Exemplo:**
```pascal
uses internal.math;

var numero: Integer;
begin
    numero := -15;
    WriteLn("Valor absoluto: ", abs(numero));
    WriteLn("Quadrado: ", square(5));
    WriteLn("Fatorial de 5: ", factorial(5));
    WriteLn("É par? ", isEven(numero));
    WriteLn("Máximo entre 10 e 20: ", max(10, 20));
end.
```

### 2. **internal.string** - Manipulação de Strings

Biblioteca com funções para trabalhar com strings.

#### Funções de Teste:
- `isEmpty(s: String): Boolean` - Verifica se string está vazia
- `isNotEmpty(s: String): Boolean` - Verifica se string não está vazia

#### Funções de Busca:
- `inStr(haystack: String, needle: String): Integer` - Encontra posição da substring
- `inStrChar(s: String, ch: String): Integer` - Encontra posição do caractere
- `contains(haystack: String, needle: String): Boolean` - Verifica se contém substring
- `startsWith(s: String, prefix: String): Boolean` - Verifica se começa com prefixo
- `endsWith(s: String, suffix: String): Boolean` - Verifica se termina com sufixo

#### Funções de Formatação:
- `quote(s: String): String` - Adiciona aspas duplas
- `singleQuote(s: String): String` - Adiciona aspas simples
- `bracket(s: String): String` - Adiciona colchetes
- `parenthesize(s: String): String` - Adiciona parênteses

#### Funções de Construção:
- `repeat(s: String, count: Integer): String` - Repete string N vezes
- `join(separator: String, s1: String, s2: String): String` - Junta 2 strings
- `join3(separator: String, s1: String, s2: String, s3: String): String` - Junta 3 strings
- `join4(separator: String, s1: String, s2: String, s3: String, s4: String): String` - Junta 4 strings

**Exemplo:**
```pascal
uses internal.string;

var texto: String;
begin
    texto := "NeoObjectPascal";
    WriteLn("Está vazio? ", isEmpty(texto));
    WriteLn("Entre aspas: ", quote(texto));
    WriteLn("Repetir 3 vezes: ", repeat("*", 3));
    WriteLn("Posição de 'Object': ", inStr(texto, "Object"));
    WriteLn("Contém 'Pascal'? ", contains(texto, "Pascal"));
end.
```

### 3. **internal.datetime** - Data e Hora

Biblioteca com funções para trabalhar com datas e horas.

#### Funções de Data/Hora Atual:
- `getCurrentYear(): Integer` - Ano atual
- `getCurrentMonth(): Integer` - Mês atual (1-12)
- `getCurrentDay(): Integer` - Dia atual (1-31)
- `getCurrentHour(): Integer` - Hora atual (0-23)
- `getCurrentMinute(): Integer` - Minuto atual (0-59)
- `getCurrentSecond(): Integer` - Segundo atual (0-59)

#### Funções de Formatação:
- `formatDate(year: Integer, month: Integer, day: Integer): String` - Formata data
- `formatTime(hour: Integer, minute: Integer, second: Integer): String` - Formata hora
- `getCurrentDate(): String` - Data atual formatada
- `getCurrentTime(): String` - Hora atual formatada
- `getCurrentDateTime(): String` - Data e hora atual

#### Funções de Validação:
- `isLeapYear(year: Integer): Boolean` - Verifica se é ano bissexto
- `isValidDate(year: Integer, month: Integer, day: Integer): Boolean` - Valida data
- `isValidTime(hour: Integer, minute: Integer, second: Integer): Boolean` - Valida hora

#### Funções de Cálculo:
- `getDaysInMonth(year: Integer, month: Integer): Integer` - Dias no mês
- `getAge(birthYear: Integer, birthMonth: Integer, birthDay: Integer): Integer` - Calcula idade
- `addDays(year: Integer, month: Integer, day: Integer, daysToAdd: Integer): String` - Adiciona dias
- `addMonths(year: Integer, month: Integer, day: Integer, monthsToAdd: Integer): String` - Adiciona meses
- `addYears(year: Integer, month: Integer, day: Integer, yearsToAdd: Integer): String` - Adiciona anos

#### Funções de Nomes:
- `getMonthName(month: Integer): String` - Nome do mês em português
- `getMonthNameEn(month: Integer): String` - Nome do mês em inglês
- `getDayOfWeekName(dayOfWeek: Integer): String` - Nome do dia da semana em português
- `getDayOfWeekNameEn(dayOfWeek: Integer): String` - Nome do dia da semana em inglês

**Exemplo:**
```pascal
uses internal.datetime;

begin
    WriteLn("Ano atual: ", getCurrentYear());
    WriteLn("Data atual: ", getCurrentDate());
    WriteLn("2024 é bissexto? ", isLeapYear(2024));
    WriteLn("Nome do mês 10: ", getMonthName(10));
    WriteLn("Idade de quem nasceu em 1990: ", getAge(1990, 5, 15));
end.
```

### 4. **internal.collections** - Coleções e Arrays

Biblioteca com funções para trabalhar com arrays e listas (implementação conceitual).

#### Funções Básicas:
- `createArray(): Object` - Cria array vazio
- `arraySize(arr: Object): Integer` - Tamanho do array
- `arrayIsEmpty(arr: Object): Boolean` - Verifica se está vazio
- `arrayFirst(arr: Object): Object` - Primeiro elemento
- `arrayLast(arr: Object): Object` - Último elemento

#### Funções de Busca:
- `arrayContains(arr: Object, value: Object): Boolean` - Verifica se contém valor
- `arrayIndexOf(arr: Object, value: Object): Integer` - Índice do valor

#### Funções de Transformação:
- `arrayReverse(arr: Object): Object` - Inverte array
- `arraySort(arr: Object): Object` - Ordena array
- `arrayUnique(arr: Object): Object` - Remove duplicatas

### 5. **internal.file** - Operações com Arquivos

Biblioteca com funções para trabalhar com arquivos e caminhos (implementação conceitual).

#### Funções de Caminho:
- `getFileExtension(filename: String): String` - Extensão do arquivo
- `getFileName(filepath: String): String` - Nome do arquivo
- `getDirectoryPath(filepath: String): String` - Diretório do arquivo
- `joinPath(path1: String, path2: String): String` - Junta caminhos

#### Funções de Validação:
- `isValidFilename(filename: String): Boolean` - Valida nome de arquivo
- `fileExists(filepath: String): Boolean` - Verifica se arquivo existe
- `isTextFile(filename: String): Boolean` - Verifica se é arquivo de texto

## Implementação Técnica

### Arquitetura

As bibliotecas internas são armazenadas como recursos no JAR:

```
src/main/resources/internal/
├── math.npas
├── string.npas
├── datetime.npas
├── collections.npas
└── file.npas
```

### Carregamento

O interpretador detecta o prefixo `internal.` e carrega os recursos do classpath:

```java
private void loadInternalLibrary(String modulePath) {
    String libraryName = modulePath.substring("internal.".length());
    String resourcePath = "/internal/" + libraryName + ".npas";
    
    InputStream resourceStream = getClass().getResourceAsStream(resourcePath);
    // ... processa o recurso como um arquivo .npas normal
}
```

### Vantagens

1. **Portabilidade**: Bibliotecas empacotadas no JAR
2. **Consistência**: Sempre disponíveis, independente do ambiente
3. **Performance**: Carregamento rápido do classpath
4. **Manutenibilidade**: Código organizado e versionado
5. **Extensibilidade**: Fácil adicionar novas bibliotecas

## Exemplos Práticos

### Calculadora Matemática
```pascal
uses internal.math;

var a, b, resultado: Integer;
begin
    a := 15;
    b := 25;
    
    WriteLn("Números: ", a, " e ", b);
    WriteLn("Máximo: ", max(a, b));
    WriteLn("Mínimo: ", min(a, b));
    WriteLn("MDC: ", gcd(a, b));
    WriteLn("MMC: ", lcm(a, b));
end.
```

### Processamento de Texto
```pascal
uses internal.string;

var nome, sobrenome, completo: String;
begin
    nome := "João";
    sobrenome := "Silva";
    
    completo := join(" ", nome, sobrenome);
    WriteLn("Nome completo: ", quote(completo));
    WriteLn("Iniciais: ", inStrChar(nome, "J"), inStrChar(sobrenome, "S"));
end.
```

### Sistema de Data
```pascal
uses internal.datetime;

begin
    WriteLn("=== Sistema de Data ===");
    WriteLn("Hoje: ", getCurrentDateTime());
    WriteLn("Mês: ", getMonthName(getCurrentMonth()));
    
    if isLeapYear(getCurrentYear()) then
        WriteLn("Este é um ano bissexto!")
    else
        WriteLn("Este não é um ano bissexto.");
end.
```

## Próximos Passos

1. **Implementações Nativas**: Algumas funções podem ser implementadas nativamente em Java para melhor performance
2. **Novas Bibliotecas**: `internal.network`, `internal.crypto`, `internal.regex`
3. **Documentação Interativa**: Exemplos executáveis na documentação
4. **Testes Automatizados**: Suite de testes para todas as bibliotecas

As bibliotecas internas tornam o NeoObjectPascal muito mais poderoso e prático para desenvolvimento de aplicações reais!