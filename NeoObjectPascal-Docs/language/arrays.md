# Arrays

Um `Array` é uma coleção ordenada de valores. Ele guarda números, strings ou qualquer valor, e sua indexação é **base 0**.

## Declarando e criando

Declare com o tipo `Array` e atribua um **literal** entre colchetes. Os elementos são separados por vírgula:

```npas
var numeros: Array;
var nomes: Array;

begin
    numeros := [10, 20, 30, 40, 50];
    nomes := ["Alice", "Bob", "Carlos", "Diana"];
    WriteLn("Arrays criados");
end.
```

<Output>
Arrays criados
</Output>

Um array vazio é `[]`:

```npas
var vazio: Array;

begin
    vazio := [];
    WriteLn("Array vazio pronto");
end.
```

<Output>
Array vazio pronto
</Output>

## Acessando por índice

Use `array[índice]` para ler um elemento. O **primeiro** elemento está no índice `0`:

```npas
var numeros: Array;
var nomes: Array;

begin
    numeros := [10, 20, 30, 40, 50];
    nomes := ["Alice", "Bob", "Carlos", "Diana"];

    WriteLn("Primeiro número: ", numeros[0]);
    WriteLn("Último nome: ", nomes[3]);
end.
```

<Output>
Primeiro número: 10
Último nome: Diana
</Output>

## Atribuindo elementos

Modifique um elemento com `array[i] := valor;`:

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    numeros[2] := 99;
    WriteLn("Terceiro após modificação: ", numeros[2]);
end.
```

<Output>
Terceiro após modificação: 99
</Output>

## Iterando com `for..in`

A forma mais direta de percorrer um array é o `for..in`, que entrega cada elemento diretamente:

```npas
var numeros: Array;
var item: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30, 40, 50];
    soma := 0;
    for item in numeros do
    begin
        WriteLn("  ", item);
        soma := soma + item;
    end;
    WriteLn("Soma: ", soma);
end.
```

<Output>
  10
  20
  30
  40
  50
Soma: 150
</Output>

Também funciona com arrays de strings:

```npas
var nomes: Array;
var nome: String;

begin
    nomes := ["Alice", "Bob", "Carlos"];
    for nome in nomes do
        WriteLn("Olá, ", nome, "!");
end.
```

<Output>
Olá, Alice!
Olá, Bob!
Olá, Carlos!
</Output>

## Índices fora do intervalo

Acessar um índice inexistente **lança um erro** em tempo de execução:

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    WriteLn(numeros[5]);   // erro: índice fora do intervalo
end.
```

::: warning Fora do intervalo é capturável
Um acesso fora dos limites gera um erro que você pode envolver em `try/catch`. Veja [Tratamento de erros](./error-handling).
:::

## Funções auxiliares

Para operações mais avançadas — ordenar, filtrar, mapear, somar, concatenar — importe a biblioteca `internal.collections`:

```npas
uses internal.collections;

var numeros: Array;

begin
    numeros := [3, 1, 2];
    WriteLn("Tamanho: ", arraySize(numeros));
    WriteLn("Soma: ", arraySum(numeros));
end.
```

<Output>
Tamanho: 3
Soma: 6
</Output>

::: tip Coleções completas
`internal.collections` traz `arraySort`, `arrayFilter`, `arrayMap`, `arrayReduce`, `arrayReverse`, `arrayContains` e muito mais. Veja [Bibliotecas internas](../features/internal-libraries).
:::

A seguir, aprenda a lidar com falhas de execução em [Tratamento de erros](./error-handling).
