# Manipulação de Dados com JSON e CSV

O NeoObjectPascal oferece suporte integrado para manipulação de dados nos formatos JSON e CSV, tornando mais fácil trabalhar com fontes de dados comuns.

## Manipulação de JSON

Para analisar uma string JSON, você pode usar a função `JSON.parse`. Ela converte a string JSON em um objeto que você pode acessar em seu código.

```pascal
var
  dados: Object;
  jsonString: String;

begin
  jsonString := '{"nome": "Carmen", "idade": 30}';
  JSON.parse(jsonString) into dados;
  WriteLn(dados.nome);
end.
```

## Manipulação de CSV

Da mesma forma, você pode analisar uma string CSV usando a função `CSV.parse`. Ela converte a string CSV em uma lista de listas de strings.

```pascal
var
  dados: Object;
  csvString: String;

begin
  csvString := 'nome,idade\nCarina,30\nBrito,25';
  CSV.parse(csvString) into dados;
  // Agora, 'dados' contém uma lista de linhas, onde cada linha é uma lista de colunas.
end.
```

