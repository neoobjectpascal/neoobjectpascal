# Structure of a program

A NeoObjectPascal program has three parts, all optional except the ending:

1. A **`uses`** clause (module imports), terminated by `;`.
2. Zero or more **declarations** (variables, functions, classes, interfaces, tests), each terminated by `;`.
3. A **main block** `begin ... end.` — the final period `.` ends the program.

```npas
uses internal.string;

var mensagem: String;

function emMaiusculas(s: String): String
begin
    return toUpperCase(s);
end;

begin
    mensagem := emMaiusculas("olá");
    WriteLn(mensagem);
end.
```

<Output>
OLÁ
</Output>

::: warning No `program` header
Unlike some Pascal dialects, NeoObjectPascal does **not** use a `program Name;` header. The program starts directly with the `uses` clause, the declarations, or the `begin` block.
:::

## The main block

The `begin ... end.` block contains the statements executed when the program runs. Each statement ends with a semicolon `;`:

```npas
begin
    WriteLn("primeira linha");
    WriteLn("segunda linha");
end.
```

Note the **final period** after the `end` that closes the program — it is mandatory. Inner blocks (of functions, loops, etc.) end with just `end` (no period).

## Comments

NeoObjectPascal has **only single-line comments**, started by `//`:

```npas
begin
    // This is a comment
    WriteLn("Oi"); // also works at the end of a line
end.
```

::: warning No block comments
There are no `{ ... }` or `(* ... *)` comments. Curly braces `{ }` are reserved for [Java code blocks](../features/java-integration).
:::

## Declarations at the top vs. inside the block

You can declare variables both at the top of the file and inside a block:

```npas
var x: Integer;   // top-level declaration

begin
    x := 10;
    var y: Integer;   // declaration inside the block
    y := 20;
    WriteLn(x + y);
end.
```

<Output>
30
</Output>

With the structure in place, head over to [Variables and types](../language/variables-and-types).
