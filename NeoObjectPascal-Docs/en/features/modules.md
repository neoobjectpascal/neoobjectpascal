# Modules and uses

As a program grows, it makes sense to split it into smaller, reusable files. In NeoObjectPascal this is done with the `uses` clause, which loads both **internal libraries** and **file-based modules** that you write yourself.

## The `uses` clause

The `uses` clause appears at the **beginning** of the program, before any declaration. It lists the modules to load, separated by commas, and ends with `;`:

```npas
uses internal.math, internal.string;

begin
    WriteLn("Máximo: ", max(10, 20));
    WriteLn("Maiúsculas: ", toUpperCase("neo"));
end.
```

<Output>
Máximo: 20
Maiúsculas: NEO
</Output>

After the `uses`, all functions from the modules become available as if they had been declared in your own file.

## Internal libraries vs. file-based modules

There are two kinds of modules, distinguished by their prefix:

| Form                   | Origin                                            | Example                    |
| ---------------------- | ------------------------------------------------- | -------------------------- |
| `internal.<name>`      | Libraries bundled inside the interpreter itself   | `uses internal.datetime;`  |
| `<folder>.<module>`    | `.npas` file relative to your program             | `uses utils.matematica;`   |

The `internal.` prefix is reserved: the runtime recognizes it and loads the library from inside the JAR. Any other path is treated as a file in your project.

## Creating a file-based module

A module is simply a `.npas` file with declarations of functions, procedures, or variables. For example, create `matematica.npas`:

```npas
// matematica.npas
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

function subtrair(a: Integer, b: Integer): Integer
begin
    return a - b;
end;
```

And use it in the main program, in the same directory:

```npas
// programa.npas
uses matematica;

begin
    WriteLn(somar(10, 5));
    WriteLn(subtrair(10, 5));
end.
```

<Output>
15
5
</Output>

The interpreter looks for `matematica.npas` starting from the directory of the main file.

::: warning The parameter separator is the comma
When declaring typed parameters, separate them with a **comma**: `function somar(a: Integer, b: Integer)`. Do not use a semicolon between parameters.
:::

## Hierarchical modules

You can organize modules into subfolders using a dot as the path separator. `uses utils.matematica;` loads the file `utils/matematica.npas`, relative to the main program:

```text
projeto/
├── programa.npas
└── utils/
    └── matematica.npas
```

```npas
// programa.npas
uses utils.matematica;

begin
    WriteLn(somar(2, 3));
end.
```

<Output>
5
</Output>

## Loading multiple modules

A single `uses` clause can mix internal libraries and your own modules, all separated by commas:

```npas
uses internal.math, internal.string, utils.matematica;

begin
    WriteLn(square(4));              // de internal.math
    WriteLn(quote("olá"));          // de internal.string
    WriteLn(somar(1, 1));           // do seu módulo utils.matematica
end.
```

<Output>
16
"olá"
2
</Output>

## Best practices

- Group related functions in the same module (for example, all text functions in a `texto.npas`).
- Prefer many small, cohesive files over a single giant file.
- Use subfolders (`utils.`, `dominio.`) to organize modules by area of the system.
- There is only one `uses` clause per program: list all modules in it.

---

Next, get to know each of the [Internal Libraries](./internal-libraries) in detail.
