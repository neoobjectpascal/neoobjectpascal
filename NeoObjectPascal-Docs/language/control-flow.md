# Estruturas de controle

As estruturas de controle decidem **quais** instruções executar e **quantas vezes**. O NeoObjectPascal oferece `if/else`, `while`, `for` numérico e `for..in`.

Em qualquer uma delas, o corpo pode ser uma única instrução ou um bloco `begin ... end` com várias instruções.

## `if ... then ... else`

O `if` executa uma instrução quando a condição é verdadeira. O `else` é opcional:

```npas
var idade: Integer;

begin
    idade := 20;

    if idade >= 18 then
        WriteLn("Maior de idade")
    else
        WriteLn("Menor de idade");
end.
```

<Output>
Maior de idade
</Output>

Para executar várias instruções, use um bloco como corpo:

```npas
var saldo: Integer;

begin
    saldo := 100;

    if saldo > 0 then
    begin
        WriteLn("Saldo positivo");
        WriteLn("Valor: ", saldo);
    end;
end.
```

<Output>
Saldo positivo
Valor: 100
</Output>

::: info Encadeando condições
Combine `if` com operadores booleanos (`and`, `or`, `not`) para condições compostas — veja [Operadores](./operators).
:::

## `while ... do`

O `while` repete o corpo **enquanto** a condição for verdadeira. A condição é testada antes de cada iteração:

```npas
var contador: Integer;

begin
    contador := 1;
    while contador <= 3 do
    begin
        WriteLn("Iteração ", contador);
        contador := contador + 1;
    end;
end.
```

<Output>
Iteração 1
Iteração 2
Iteração 3
</Output>

::: warning Cuidado com laços infinitos
Garanta que algo dentro do `while` modifique a condição (como `contador := contador + 1`), senão o laço nunca termina.
:::

## `for i := início to fim do`

O `for` numérico conta de um valor inicial até um final, **incluindo ambos** (intervalo inclusivo), incrementando de 1 em 1:

```npas
var i: Integer;

begin
    for i := 1 to 5 do
        WriteLn("Número ", i);
end.
```

<Output>
Número 1
Número 2
Número 3
Número 4
Número 5
</Output>

Um exemplo somando os valores do intervalo com um bloco:

```npas
var i: Integer;
var soma: Integer;

begin
    soma := 0;
    for i := 1 to 10 do
    begin
        soma := soma + i;
    end;
    WriteLn("Soma de 1 a 10: ", soma);
end.
```

<Output>
Soma de 1 a 10: 55
</Output>

## `for x in array do`

A variação `for..in` itera **cada elemento** de um array, sem precisar de índices:

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

Também funciona com blocos, ideal para acumular resultados:

```npas
var numeros: Array;
var n: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30];
    soma := 0;
    for n in numeros do
    begin
        WriteLn("  ", n);
        soma := soma + n;
    end;
    WriteLn("Total: ", soma);
end.
```

<Output>
  10
  20
  30
Total: 60
</Output>

::: tip Blocos como corpo
Sempre que precisar de mais de uma instrução dentro de `if`, `while` ou `for`, envolva-as em `begin ... end`. Uma única instrução dispensa o bloco.
:::

A seguir, aprenda a organizar lógica reutilizável em [Funções e procedimentos](./functions).
