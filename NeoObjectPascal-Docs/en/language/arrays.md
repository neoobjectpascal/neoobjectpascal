# Arrays

An `Array` is an ordered collection of values. It holds numbers, strings, or any value, and its indexing is **zero-based**.

## Declaring and creating

Declare with the `Array` type and assign a **literal** in square brackets. Elements are separated by commas:

```npas
var numeros: Array;
var nomes: Array;

begin
    numeros := [10, 20, 30, 40, 50];
    nomes := ["Alice", "Bob", "Carlos", "Diana"];
    WriteLn("Arrays criados");
end.
```

<Output>
Arrays criados
</Output>

An empty array is `[]`:

```npas
var vazio: Array;

begin
    vazio := [];
    WriteLn("Array vazio pronto");
end.
```

<Output>
Array vazio pronto
</Output>

## Accessing by index

Use `array[index]` to read an element. The **first** element is at index `0`:

```npas
var numeros: Array;
var nomes: Array;

begin
    numeros := [10, 20, 30, 40, 50];
    nomes := ["Alice", "Bob", "Carlos", "Diana"];

    WriteLn("Primeiro número: ", numeros[0]);
    WriteLn("Último nome: ", nomes[3]);
end.
```

<Output>
Primeiro número: 10
Último nome: Diana
</Output>

## Assigning elements

Modify an element with `array[i] := value;`:

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    numeros[2] := 99;
    WriteLn("Terceiro após modificação: ", numeros[2]);
end.
```

<Output>
Terceiro após modificação: 99
</Output>

## Iterating with `for..in`

The most direct way to traverse an array is `for..in`, which hands you each element directly:

```npas
var numeros: Array;
var item: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30, 40, 50];
    soma := 0;
    for item in numeros do
    begin
        WriteLn("  ", item);
        soma := soma + item;
    end;
    WriteLn("Soma: ", soma);
end.
```

<Output>
  10
  20
  30
  40
  50
Soma: 150
</Output>

It also works with string arrays:

```npas
var nomes: Array;
var nome: String;

begin
    nomes := ["Alice", "Bob", "Carlos"];
    for nome in nomes do
        WriteLn("Olá, ", nome, "!");
end.
```

<Output>
Olá, Alice!
Olá, Bob!
Olá, Carlos!
</Output>

## Out-of-range indexes

Accessing a nonexistent index **throws an error** at runtime:

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    WriteLn(numeros[5]);   // error: index out of range
end.
```

::: warning Out of range is catchable
An out-of-bounds access raises an error that you can wrap in `try/catch`. See [Error handling](./error-handling).
:::

## Helper functions

For more advanced operations — sorting, filtering, mapping, summing, concatenating — import the `internal.collections` library:

```npas
uses internal.collections;

var numeros: Array;

begin
    numeros := [3, 1, 2];
    WriteLn("Tamanho: ", arraySize(numeros));
    WriteLn("Soma: ", arraySum(numeros));
end.
```

<Output>
Tamanho: 3
Soma: 6
</Output>

::: tip Full collections
`internal.collections` provides `arraySort`, `arrayFilter`, `arrayMap`, `arrayReduce`, `arrayReverse`, `arrayContains`, and much more. See [Internal libraries](../features/internal-libraries).
:::

Next, learn how to handle runtime failures in [Error handling](./error-handling).
