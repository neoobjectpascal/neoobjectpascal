# Variables and types

Every variable in NeoObjectPascal has a declared **type**. The declaration uses the `var` keyword, followed by the name, a colon, and the type:

```npas
var idade: Integer;
var nome: String;
```

Values are assigned with the `:=` operator (don't confuse it with `=`, which is the equality operator):

```npas
begin
    var idade: Integer;
    idade := 30;
    WriteLn("Idade: ", idade);
end.
```

<Output>
Idade: 30
</Output>

## Where to declare

You can declare variables in two places:

- **At the top of the file**, before the main block.
- **Inside a block** `begin ... end`, as an ordinary statement.

```npas
var total: Integer;   // top-level declaration

begin
    total := 100;
    var imposto: Integer;   // declaration inside the block
    imposto := total / 10;
    WriteLn("Imposto: ", imposto);
end.
```

<Output>
Imposto: 10
</Output>

## The ten types

NeoObjectPascal has ten built-in types:

| Type      | Description                                           | Literal examples             |
| --------- | ----------------------------------------------------- | ---------------------------- |
| `Integer` | Whole numbers                                         | `0`, `42`, `-7`              |
| `Real`    | Floating-point numbers (double precision)             | `3.14159`, `5.0`, `-0.5`     |
| `String`  | Text                                                  | `"olá"`, `'mundo'`           |
| `Boolean` | Logical value                                         | `true`, `false`              |
| `Array`   | Ordered collection of values                          | `[1, 2, 3]`, `["a", "b"]`    |
| `Object`  | Generic type (instances, JSON/CSV, assorted values)   | `new Pessoa("Ana")`          |
| `Date`    | Calendar date (ISO `yyyy-MM-dd`)                      | `today()`, `date(2026,12,25)`|
| `Time`    | Time of day (`HH:mm:ss`)                             | `currentTime()`, `time(10,30,0)`|
| `DateTime`| Combined date and time                                | `now()`                      |
| `Currency`| Exact decimal money (no floating-point error)         | `currency(199.90)`           |

On top of these, `Double` and `Float` are **aliases of `Real`** (64-bit floating point) — use whichever name you prefer. The `Date`, `Time`, `DateTime` and `Currency` types are covered in detail in [Dates, times & currency](../features/dates-and-currency).

## Literals and details of each type

### Integer and Real

Integers are written directly: `10`, `-3`, `0`. `Real` numbers require digits on **both sides** of the decimal point:

```npas
begin
    var meio: Real;
    meio := 0.5;    // correct
    WriteLn(meio);
end.
```

<Output>
0.5
</Output>

::: warning Real requires complete digits
Write `5.0`, never `5.` — the second form is invalid. Likewise, use `0.5` and not `.5`.
:::

When you mix `Integer` and `Real` in an expression, the integer is **automatically promoted** to `Real`. The same happens when assigning an integer to a `Real` variable:

```npas
begin
    var x: Real;
    x := 5;          // 5 becomes 5.0
    var y: Real;
    y := 2 * 3.5;    // Real result
    WriteLn("x = ", x);
    WriteLn("y = ", y);
end.
```

<Output>
x = 5.0
y = 7.0
</Output>

### String

Strings can use **double quotes** or **single quotes** — both work the same way:

```npas
begin
    var a: String;
    var b: String;
    a := "aspas duplas";
    b := 'aspas simples';
    WriteLn(a);
    WriteLn(b);
end.
```

<Output>
aspas duplas
aspas simples
</Output>

The `+` operator **concatenates** strings. If either side is a `String`, `+` joins the values instead of adding them:

```npas
begin
    var nome: String;
    nome := "Mundo";
    WriteLn("Olá, " + nome + "!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Boolean

A `Boolean` holds `true` or `false` (also accepted in uppercase, such as `True` or `TRUE`):

```npas
begin
    var ativo: Boolean;
    ativo := true;
    WriteLn("Ativo: ", ativo);
end.
```

<Output>
Ativo: true
</Output>

### Object

`Object` is the language's generic type. It holds class instances, results of `JSON.parse`/`CSV.parse`, and any value whose specific type you don't need to name:

```npas
class Pessoa
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public function saudar(): String
    begin
        return "Sou " + self.nome;
    end;
end;

var p: Object;

begin
    p := new Pessoa("Alice");
    WriteLn(p.saudar());
end.
```

<Output>
Sou Alice
</Output>

::: tip Choosing the type
Prefer the most specific type possible (`Integer`, `String`, etc.) and reserve `Object` for instances and dynamic data.
:::

Now that you know how to declare variables, see how to combine them in [Operators](./operators).
