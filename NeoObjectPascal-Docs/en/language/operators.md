# Operators

Operators combine values into expressions. NeoObjectPascal has arithmetic, concatenation, comparison, boolean, and unary operators, plus the pipe operator.

## Arithmetic

The four arithmetic operators are `+`, `-`, `*`, and `/`:

```npas
begin
    WriteLn(7 + 3);
    WriteLn(7 - 3);
    WriteLn(7 * 3);
    WriteLn(10 / 4);
end.
```

<Output>
10
4
21
2.5
</Output>

The `/` operator is **division**. When the operands produce a fractional result, it is a `Real`.

::: warning Division by zero
Dividing by zero **throws an error** at runtime. You can catch it with `try/catch` — see [Error handling](./error-handling).
:::

## String concatenation

The `+` operator also **concatenates** strings. If either side is a `String`, the result is the joined text:

```npas
begin
    var nome: String;
    nome := "Ada";
    WriteLn("Olá, " + nome);
end.
```

<Output>
Olá, Ada
</Output>

## Comparison

The comparison operators return a `Boolean`:

| Operator | Meaning           |
| -------- | ----------------- |
| `=`      | equal to          |
| `<>`     | not equal to      |
| `<`      | less than         |
| `>`      | greater than      |
| `<=`     | less than or equal |
| `>=`     | greater than or equal |

```npas
begin
    WriteLn(5 = 5);
    WriteLn(5 <> 3);
    WriteLn(2 < 10);
    WriteLn(10 >= 10);
end.
```

<Output>
true
true
true
true
</Output>

::: warning Equality is `=`, assignment is `:=`
Equality uses a **single equals sign** (`=`). Assignment uses `:=`. And "not equal to" is `<>`, not `!=`.

```npas
if idade = 18 then ...   // comparison
idade := 18;             // assignment
```
:::

## Booleans

Combine conditions with `and`, `or`, and `not`:

```npas
var a: Integer;
var b: Integer;
var admin: Boolean;

begin
    a := 10;
    b := 20;
    admin := false;

    if a > 0 and b > 0 then
        WriteLn("Ambos positivos");

    if not admin then
        WriteLn("Não é administrador");

    if (a > 5 and b > 5) or admin then
        WriteLn("Condição composta verdadeira");
end.
```

<Output>
Ambos positivos
Não é administrador
Condição composta verdadeira
</Output>

## Unary

The unary `-` negates a number and `not` negates a boolean:

```npas
begin
    var x: Integer;
    x := 7;
    WriteLn(0 - x);   // negation by subtraction
    WriteLn(not true);
end.
```

<Output>
-7
false
</Output>

## The pipe operator `|>`

The `|>` operator passes the value on the left as the **first argument** of the function on the right. It chains transformations in a readable way:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

function incrementar(n: Integer): Integer
begin
    return n + 1;
end;

begin
    WriteLn(10 |> dobrar |> incrementar);
end.
```

<Output>
21
</Output>

Here `10 |> dobrar` produces `20`, and `20 |> incrementar` produces `21`. See more in [Functional programming](../features/functional).

## Precedence table

From highest to lowest precedence (operators at the same level evaluate left to right):

| Level | Operators                      |
| ----- | ------------------------------ |
| 1     | `a[i]` (indexing), calls       |
| 2     | `*`, `/`                       |
| 3     | `+`, `-`                       |
| 4     | `=`, `<>`, `<`, `>`, `<=`, `>=`|
| 5     | `and`                          |
| 6     | `or`                           |
| 7     | `\|>` (pipe)                    |
| 8     | unary `not`, `-`               |

Use parentheses whenever you want to make your intent explicit, as in `(a > 5 and b > 5) or admin`.

Next, use operators inside decisions and loops in [Control structures](./control-flow).
