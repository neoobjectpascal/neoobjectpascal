# Array

Un `Array` è una collezione ordinata di valori. Contiene numeri, stringhe o qualsiasi valore, e la sua indicizzazione è **in base 0**.

## Dichiarare e creare

Dichiara con il tipo `Array` e assegna un **letterale** tra parentesi quadre. Gli elementi sono separati da virgola:

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

Un array vuoto è `[]`:

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

## Accedere per indice

Usa `array[indice]` per leggere un elemento. Il **primo** elemento si trova all'indice `0`:

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

## Assegnare elementi

Modifica un elemento con `array[i] := valore;`:

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

## Iterare con `for..in`

Il modo più diretto di percorrere un array è il `for..in`, che fornisce direttamente ogni elemento:

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

Funziona anche con array di stringhe:

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

## Indici fuori dall'intervallo

Accedere a un indice inesistente **genera un errore** in fase di esecuzione:

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    WriteLn(numeros[5]);   // erro: índice fora do intervalo
end.
```

::: warning Fuori intervallo è catturabile
Un accesso fuori dai limiti genera un errore che puoi racchiudere in `try/catch`. Vedi [Gestione degli errori](./error-handling).
:::

## Funzioni ausiliarie

Per operazioni più avanzate — ordinare, filtrare, mappare, sommare, concatenare — importa la libreria `internal.collections`:

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

::: tip Collezioni complete
`internal.collections` offre `arraySort`, `arrayFilter`, `arrayMap`, `arrayReduce`, `arrayReverse`, `arrayContains` e molto altro. Vedi [Librerie interne](../features/internal-libraries).
:::

Ora impara a gestire i fallimenti in fase di esecuzione in [Gestione degli errori](./error-handling).
