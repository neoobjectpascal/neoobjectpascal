# Control structures

Control structures decide **which** statements run and **how many times**. NeoObjectPascal offers `if/else`, `while`, numeric `for`, and `for..in`.

In all of them, the body can be a single statement or a `begin ... end` block with several statements.

## `if ... then ... else`

The `if` runs a statement when the condition is true. The `else` is optional:

```npas
var idade: Integer;

begin
    idade := 20;

    if idade >= 18 then
        WriteLn("Maior de idade")
    else
        WriteLn("Menor de idade");
end.
```

<Output>
Maior de idade
</Output>

To run several statements, use a block as the body:

```npas
var saldo: Integer;

begin
    saldo := 100;

    if saldo > 0 then
    begin
        WriteLn("Saldo positivo");
        WriteLn("Valor: ", saldo);
    end;
end.
```

<Output>
Saldo positivo
Valor: 100
</Output>

::: info Chaining conditions
Combine `if` with boolean operators (`and`, `or`, `not`) for compound conditions — see [Operators](./operators).
:::

## `while ... do`

The `while` repeats the body **as long as** the condition is true. The condition is tested before each iteration:

```npas
var contador: Integer;

begin
    contador := 1;
    while contador <= 3 do
    begin
        WriteLn("Iteração ", contador);
        contador := contador + 1;
    end;
end.
```

<Output>
Iteração 1
Iteração 2
Iteração 3
</Output>

::: warning Beware of infinite loops
Make sure something inside the `while` modifies the condition (such as `contador := contador + 1`), otherwise the loop never ends.
:::

## `for i := start to end do`

The numeric `for` counts from a starting value up to an ending value, **including both** (inclusive range), incrementing by 1:

```npas
var i: Integer;

begin
    for i := 1 to 5 do
        WriteLn("Número ", i);
end.
```

<Output>
Número 1
Número 2
Número 3
Número 4
Número 5
</Output>

An example summing the values in the range with a block:

```npas
var i: Integer;
var soma: Integer;

begin
    soma := 0;
    for i := 1 to 10 do
    begin
        soma := soma + i;
    end;
    WriteLn("Soma de 1 a 10: ", soma);
end.
```

<Output>
Soma de 1 a 10: 55
</Output>

## `for x in array do`

The `for..in` variation iterates over **each element** of an array, without needing indexes:

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

It also works with blocks, ideal for accumulating results:

```npas
var numeros: Array;
var n: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30];
    soma := 0;
    for n in numeros do
    begin
        WriteLn("  ", n);
        soma := soma + n;
    end;
    WriteLn("Total: ", soma);
end.
```

<Output>
  10
  20
  30
Total: 60
</Output>

::: tip Blocks as the body
Whenever you need more than one statement inside `if`, `while`, or `for`, wrap them in `begin ... end`. A single statement doesn't need a block.
:::

Next, learn how to organize reusable logic in [Functions and procedures](./functions).
