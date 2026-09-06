# Operadores

Operadores combinam valores em expressões. O NeoObjectPascal tem operadores aritméticos, de concatenação, de comparação, booleanos, unários e o operador pipe.

## Aritméticos

Os quatro operadores aritméticos são `+`, `-`, `*` e `/`:

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

O operador `/` é a **divisão**. Quando os operandos produzem um resultado fracionário, ele é `Real`.

::: warning Divisão por zero
Dividir por zero **lança um erro** em tempo de execução. Você pode capturá-lo com `try/catch` — veja [Tratamento de erros](./error-handling).
:::

## Concatenação de strings

O operador `+` também **concatena** strings. Se qualquer lado for `String`, o resultado é a junção dos textos:

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

## Comparação

Os operadores de comparação retornam um `Boolean`:

| Operador | Significado       |
| -------- | ----------------- |
| `=`      | igual a           |
| `<>`     | diferente de      |
| `<`      | menor que         |
| `>`      | maior que         |
| `<=`     | menor ou igual    |
| `>=`     | maior ou igual    |

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

::: warning Igualdade é `=`, atribuição é `:=`
A igualdade usa um **único sinal de igual** (`=`). A atribuição usa `:=`. E "diferente de" é `<>`, não `!=`.

```npas
if idade = 18 then ...   // comparação
idade := 18;             // atribuição
```
:::

## Booleanos

Combine condições com `and`, `or` e `not`:

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

## Unários

O `-` unário nega um número e o `not` nega um booleano:

```npas
begin
    var x: Integer;
    x := 7;
    WriteLn(0 - x);   // negação por subtração
    WriteLn(not true);
end.
```

<Output>
-7
false
</Output>

## O operador pipe `|>`

O operador `|>` passa o valor da esquerda como **primeiro argumento** da função à direita. Ele encadeia transformações de forma legível:

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

Aqui `10 |> dobrar` produz `20`, e `20 |> incrementar` produz `21`. Veja mais em [Programação funcional](../features/functional).

## Tabela de precedência

Da maior para a menor precedência (operadores no mesmo nível avaliam da esquerda para a direita):

| Nível | Operadores                     |
| ----- | ------------------------------ |
| 1     | `a[i]` (índice), chamadas      |
| 2     | `*`, `/`                       |
| 3     | `+`, `-`                       |
| 4     | `=`, `<>`, `<`, `>`, `<=`, `>=`|
| 5     | `and`                          |
| 6     | `or`                           |
| 7     | `\|>` (pipe)                    |
| 8     | unário `not`, `-`              |

Use parênteses sempre que quiser tornar a intenção explícita, como em `(a > 5 and b > 5) or admin`.

A seguir, use operadores dentro de decisões e laços em [Estruturas de controle](./control-flow).
