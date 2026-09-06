# Input and Output

NeoObjectPascal offers simple procedures for interacting with the console: `WriteLn` to display information, `ReadLn` to read data from the user, and `showMenu` to build numbered menus.

## Writing with `WriteLn`

The `WriteLn` procedure writes a line of text to the console and adds a line break at the end:

```npas
begin
    WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Multiple arguments

`WriteLn` accepts several arguments and **concatenates them with no separator** in the output. This is useful for building messages with dynamic values:

```npas
var nome: String;
var idade: Integer;

begin
    nome := "Carol";
    idade := 28;
    WriteLn("Olá, ", nome, "! Você tem ", idade, " anos.");
end.
```

<Output>
Olá, Carol! Você tem 28 anos.
</Output>

::: tip Blank line
Calling `WriteLn()` with no arguments prints just a line break — handy for separating blocks of output.
:::

## Reading with `ReadLn`

The `ReadLn` procedure reads a line typed by the user and stores it in a variable. The value is converted to the type of the target variable:

```npas
var nome: String;

begin
    WriteLn("Qual é o seu nome?");
    ReadLn(nome);
    WriteLn("Prazer, ", nome, "!");
end.
```

<Output>
Qual é o seu nome?
Prazer, Ada!
</Output>

When the target variable is `Integer` or `Real`, the text read is automatically converted to the corresponding numeric type:

```npas
var idade: Integer;

begin
    WriteLn("Digite sua idade:");
    ReadLn(idade);

    if idade >= 18 then
        WriteLn("Maior de idade.")
    else
        WriteLn("Menor de idade.");
end.
```

<Output>
Digite sua idade:
Maior de idade.
</Output>

::: warning Type conversion
When reading into a numeric variable, make sure the input is actually a number. Unexpected input can cause conversion errors — handle these cases with `try/catch` when reading untrusted data. See [Error Handling](../language/error-handling).
:::

## Menus with `showMenu`

The `showMenu` procedure takes a list of options and prints a numbered menu automatically:

```npas
begin
    showMenu("Café", "Chá", "Suco");
end.
```

<Output>
1. Café
2. Chá
3. Suco
</Output>

Combine `showMenu` with `ReadLn` to build a selection interface:

```npas
var opcao: Integer;

begin
    showMenu("Novo pedido", "Consultar", "Sair");
    WriteLn("Escolha uma opção:");
    ReadLn(opcao);

    if opcao = 1 then
        WriteLn("Iniciando novo pedido...")
    else if opcao = 2 then
        WriteLn("Consultando...")
    else
        WriteLn("Até logo!");
end.
```

<Output>
1. Novo pedido
2. Consultar
3. Sair
Escolha uma opção:
Iniciando novo pedido...
</Output>

## Best practices

- Use `WriteLn` with multiple arguments instead of concatenating everything with `+`: it is more readable and avoids manual conversions.
- Always display a message before a `ReadLn` so the user knows what to type.
- When reading numbers, consider validating or handling conversion errors.

---

Next, discover how to tap into the power of the JVM in [Java Integration](./java-integration).
