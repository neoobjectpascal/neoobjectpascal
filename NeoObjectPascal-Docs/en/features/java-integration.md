# Java Integration

Because NeoObjectPascal is interpreted on top of the JVM, you can **embed Java code directly** into your program. This gives you access to the entire Java ecosystem — date, math, text libraries, and much more — without leaving Pascal syntax.

## Syntax of a Java block

A Java block is an expression that produces a value. The general form is:

```npas
java:(arg0, arg1) { <código Java que retorna um valor> }
```

- Inside the parentheses go the NeoObjectPascal values you want to make available to the block.
- Inside the braces `{ }` goes Java code. It must end with a `return`.
- The value returned by the block becomes the value of the expression in NeoObjectPascal.

::: info The braces belong to Java
In NeoObjectPascal, `{ }` delimits **exclusively** the body of a Java block. There are no block comments — comments are only `// single line`.
:::

## Accessing the parameters: `param0`, `param1`, ...

Inside the block, arguments are accessed by **position**, using the names `param0`, `param1`, `param2`, and so on. Since Java is typed, you must **cast** to the appropriate type:

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        return "Olá, " + ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));
end.
```

<Output>
Olá, ADA
</Output>

The `nome` argument was passed to the block as `param0` and cast to `String` before calling `.toUpperCase()`.

## Accessing arguments by name

When a Java-block argument is a **simple identifier** — as in `java:(nome)` — you can reference it inside the block **by that name**, not only as `param0`. The named alias is declared with the argument's runtime type, so for `String`, `Integer`, `Boolean`, and `Real` the cast becomes unnecessary.

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        // 'nome' is already a String — no need for (String)param0
        return "Olá, " + nome.toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));   // Olá, ADA
end.
```

<Output>
Olá, ADA
</Output>

Compare it with the old positional style, `((String)param0).toUpperCase()`: the named alias drops the cast and makes the Java body easier to read.

`param0`, `param1`, ... remain available (fully backward compatible) and are the way to access arguments that are **not** simple identifiers — such as `java:(a + b)`, a literal, or a function call — since those have no name.

::: warning Reserved words and collisions
If the argument name matches a Java reserved word (`class`, `int`, ...) or a local variable declared inside the block, the named alias is skipped — use `param0` in that case.
:::

## Multiple parameters

Each additional argument gets the next index. Here `a` is `param0` and `b` is `param1`:

```npas
function somaDeQuadrados(a: Integer, b: Integer): Integer
begin
    return java:(a, b) {
        Integer x = (Integer)param0;
        Integer y = (Integer)param1;
        return x * x + y * y;
    };
end;

begin
    WriteLn(somaDeQuadrados(3, 4));
end.
```

<Output>
25
</Output>

## Using JVM libraries

The real gain lies in calling Java classes. Always use the **fully qualified name** of the class, since you cannot declare an `import`:

```npas
function raizQuadrada(numero: Integer): Real
begin
    return java:(numero) {
        Double n = ((Integer)param0).doubleValue();
        return Math.sqrt(n);
    };
end;

function agora(): String
begin
    return java:() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        return hoje.toString();
    };
end;

begin
    WriteLn("Raiz de 144: ", raizQuadrada(144));
    WriteLn("Data de hoje: ", agora());
end.
```

<Output>
Raiz de 144: 12.0
Data de hoje: 2026-07-17
</Output>

Notice that `agora` uses `java:()` **without parameters** — a Java block can be self-contained.

## Direct use in expressions

A Java block does not have to be inside a function: it can appear directly in any expression, including as an argument to `WriteLn`:

```npas
begin
    WriteLn("Cálculo direto: ", java:(10, 20) {
        Integer a = (Integer)param0;
        Integer b = (Integer)param1;
        return a + b;
    });
end.
```

<Output>
Cálculo direto: 30
</Output>

## How the internal libraries use this

The [internal libraries](./internal-libraries) of NeoObjectPascal are, for the most part, written with Java blocks. For example, `toUpperCase` from the `internal.string` library is literally:

```npas
function toUpperCase(s): String
begin
    return java:(s) {
        return ((String)param0).toUpperCase();
    };
end;
```

In other words, you can write your own libraries following the same pattern.

## Considerations

- **Explicit cast**: parameters arrive as `Object`; cast them to `Integer`, `String`, `Boolean`, etc.
- **No `import`**: use qualified names like `java.time.LocalDate`.
- **Exceptions**: Java exceptions thrown in the block are caught by the runtime and can be handled with `try/catch`.
- **Performance**: there is a small dynamic-compilation cost the first time each block runs.

::: tip Combine it with the pipe
Functions that wrap Java blocks fit naturally into `|>` pipelines. See [Functional Programming](./functional).
:::

---

Next, organize your code into [Modules and uses](./modules).
