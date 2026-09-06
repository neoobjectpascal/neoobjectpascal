# Manipulação de dados (JSON e CSV)

O NeoObjectPascal traz suporte nativo para dois dos formatos de dados mais comuns: **JSON** e **CSV**. Ambos usam a mesma sintaxe declarativa `parse(...) into variavel;`, que analisa uma string e armazena o resultado em uma variável do tipo `Object`.

## Analisando JSON

Use `JSON.parse(textoJson) into variavel;` para converter uma string JSON em um objeto. Depois, acesse os campos com a notação de ponto:

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

::: tip Aspas simples ajudam
Como o JSON usa aspas duplas internamente, escrever a string com **aspas simples** (`'...'`) evita conflitos. As duas formas de aspas são equivalentes para literais de string no NeoObjectPascal.
:::

### Objetos aninhados

O acesso por ponto funciona em qualquer nível de profundidade da estrutura:

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

## Analisando CSV

Use `CSV.parse(textoCsv) into variavel;` para converter uma string CSV em uma lista de linhas, onde cada linha é uma lista de colunas. O resultado é um `Object` que você pode percorrer:

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

### Percorrendo as linhas

Como o resultado de `CSV.parse` é iterável, você pode usar `for..in` para processar cada linha:

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

::: warning O cabeçalho também é uma linha
`CSV.parse` não distingue o cabeçalho das linhas de dados: a primeira linha (`produto,preco`) é retornada como qualquer outra. Se o seu CSV tem cabeçalho, ignore o índice `0` ao processar os dados.
:::

## Combinando com pipe e Java

Os dados analisados são valores comuns e podem alimentar um pipeline funcional ou um bloco Java. Por exemplo, transformar um campo logo após o parse:

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

## Boas práticas

- Valide a origem dos dados: strings vindas de arquivos ou da rede podem estar malformadas. Envolva o parse em `try/catch` quando a entrada não for confiável — veja [Tratamento de erros](../language/error-handling).
- Declare a variável de destino como `Object`, pois JSON e CSV produzem estruturas dinâmicas.
- Para CSV com cabeçalho, trate a linha `0` separadamente das demais.

---

A seguir, veja como interagir com o console em [Entrada e saída](./io).
