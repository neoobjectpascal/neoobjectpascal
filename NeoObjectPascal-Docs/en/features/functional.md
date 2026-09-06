# Functional Programming

NeoObjectPascal incorporates ideas from functional programming so you can write code that is more concise, expressive, and easier to maintain. The central feature is the **pipe operator** `|>`, which chains transformations in a readable, left-to-right way.

## The pipe operator `|>`

The `|>` operator passes the value on the left as the **first argument** of the function on the right. In other words, `value |> func` is equivalent to `func(value)`:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

begin
    WriteLn(dobrar(5));      // chamada tradicional
    WriteLn(5 |> dobrar);    // com pipe — mesmo resultado
end.
```

<Output>
10
10
</Output>

Both styles produce the same result. The advantage of the pipe becomes clear when you chain several transformations.

## Chaining functions

Because the pipe hands the result of one function as the input to the next, you can build a sequence of steps without nesting calls:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

function incrementar(n: Integer): Integer
begin
    return n + 1;
end;

function aoQuadrado(n: Integer): Integer
begin
    return n * n;
end;

begin
    // Leia da esquerda para a direita: 10 → 20 → 21 → 441
    WriteLn(10 |> dobrar |> incrementar |> aoQuadrado);
end.
```

<Output>
441
</Output>

Compare this with the nested form `aoQuadrado(incrementar(dobrar(10)))`. The pipe removes the parentheses and makes the order of operations immediately visible.

::: tip Read it like a sentence
A chain built with `|>` reads in the order the steps happen. It is like a data "conveyor belt": each function receives the result of the previous one.
:::

## Small, composable functions

The pipe favors a style in which you build **small functions with a single responsibility** and combine them. Each function stays simple, testable, and reusable:

```npas
function limpar(texto: String): String
begin
    // Remove um prefixo de espaço e normaliza
    return "[" + texto + "]";
end;

function emMaiusculas(texto: String): String
begin
    return java:(texto) {
        return ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn("relatorio" |> emMaiusculas |> limpar);
end.
```

<Output>
[RELATORIO]
</Output>

Notice that `emMaiusculas` delegates the heavy lifting to a Java block. You can freely mix NeoObjectPascal functions and `java:(...) { ... }` blocks within a pipeline. See the details in [Java Integration](./java-integration).

## Combining pipe with arithmetic

The pipe has low precedence, so arithmetic expressions to its left are evaluated before being piped:

```npas
function descrever(n: Integer): String
begin
    if n > 100 then
        return "grande"
    else
        return "pequeno";
end;

begin
    WriteLn(20 * 3 |> descrever);   // (20 * 3) = 60 → "pequeno"
    WriteLn(50 * 3 |> descrever);   // (50 * 3) = 150 → "grande"
end.
```

<Output>
pequeno
grande
</Output>

::: info Precedence
The `|>` sits just above the unary operators in the precedence table. See [Operators](../language/operators) for the full table, and use parentheses whenever you want to make the intent explicit.
:::

## Best practices

- Prefer **pure** functions (no side effects) in your pipelines: given the same argument, they always return the same result.
- Use names that describe the transformation (`dobrar`, `emMaiusculas`, `descrever`), not the step (`passo1`).
- Keep each function short. If a step grows too large, break it into smaller functions and link them with `|>`.

These small functions are also easy to cover with the native testing framework — see [Unit Testing](../testing/unit-testing).

---

Next, learn how to turn structured text into objects in [Data Handling (JSON and CSV)](./data-parsing).
