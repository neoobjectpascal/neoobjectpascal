# Data Handling (JSON and CSV)

NeoObjectPascal provides native support for two of the most common data formats: **JSON** and **CSV**. Both use the same declarative syntax `parse(...) into variable;`, which parses a string and stores the result in a variable of type `Object`.

## Parsing JSON

Use `JSON.parse(jsonText) into variable;` to convert a JSON string into an object. Then access the fields with dot notation:

```npas
var dados: Object;
var jsonString: String;

begin
    jsonString := '{"nome": "Carmen", "idade": 30}';
    JSON.parse(jsonString) into dados;

    WriteLn("Nome: ", dados.nome);
    WriteLn("Idade: ", dados.idade);
end.
```

<Output>
Nome: Carmen
Idade: 30
</Output>

::: tip Single quotes help
Because JSON uses double quotes internally, writing the string with **single quotes** (`'...'`) avoids conflicts. Both quote forms are equivalent for string literals in NeoObjectPascal.
:::

### Nested objects

Dot access works at any depth of the structure:

```npas
var config: Object;

begin
    config := '{"servidor": {"host": "localhost", "porta": 8080}}';
    JSON.parse(config) into config;

    WriteLn("Host: ", config.servidor.host);
    WriteLn("Porta: ", config.servidor.porta);
end.
```

<Output>
Host: localhost
Porta: 8080
</Output>

## Parsing CSV

Use `CSV.parse(csvText) into variable;` to convert a CSV string into a list of rows, where each row is a list of columns. The result is an `Object` you can iterate over:

```npas
var dados: Object;
var csvString: String;

begin
    csvString := "nome,idade
Carina,30
Bruno,25";
    CSV.parse(csvString) into dados;

    // dados[0] é a primeira linha; dados[0][0] é a primeira coluna
    WriteLn("Primeira pessoa: ", dados[0][0]);
    WriteLn("Idade dela: ", dados[0][1]);
end.
```

<Output>
Primeira pessoa: Carina
Idade dela: 30
</Output>

### Iterating over the rows

Because the result of `CSV.parse` is iterable, you can use `for..in` to process each row:

```npas
var linhas: Object;
var linha: Object;

begin
    linhas := "produto,preco
Cafe,12
Cha,8";
    CSV.parse(linhas) into linhas;

    for linha in linhas do
        WriteLn(linha[0], " custa ", linha[1]);
end.
```

<Output>
produto custa preco
Cafe custa 12
Cha custa 8
</Output>

::: warning The header is a row too
`CSV.parse` does not distinguish the header from the data rows: the first row (`produto,preco`) is returned like any other. If your CSV has a header, skip index `0` when processing the data.
:::

## Combining with pipe and Java

The parsed data are ordinary values and can feed a functional pipeline or a Java block. For example, transforming a field right after parsing:

```npas
function emMaiusculas(texto: String): String
begin
    return java:(texto) {
        return ((String)param0).toUpperCase();
    };
end;

var pedido: Object;

begin
    pedido := '{"cliente": "ana silva"}';
    JSON.parse(pedido) into pedido;

    WriteLn(pedido.cliente |> emMaiusculas);
end.
```

<Output>
ANA SILVA
</Output>

## Best practices

- Validate the origin of the data: strings coming from files or the network may be malformed. Wrap the parse in `try/catch` when the input is not trustworthy — see [Error Handling](../language/error-handling).
- Declare the target variable as `Object`, since JSON and CSV produce dynamic structures.
- For CSV with a header, handle row `0` separately from the rest.

---

Next, see how to interact with the console in [Input and Output](./io).
