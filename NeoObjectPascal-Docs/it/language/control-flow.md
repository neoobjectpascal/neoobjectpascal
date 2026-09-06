# Strutture di controllo

Le strutture di controllo decidono **quali** istruzioni eseguire e **quante volte**. NeoObjectPascal offre `if/else`, `while`, `for` numerico e `for..in`.

In ognuna di esse, il corpo può essere una singola istruzione o un blocco `begin ... end` con più istruzioni.

## `if ... then ... else`

L'`if` esegue un'istruzione quando la condizione è vera. L'`else` è opzionale:

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

Per eseguire più istruzioni, usa un blocco come corpo:

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

::: info Concatenare le condizioni
Combina `if` con gli operatori booleani (`and`, `or`, `not`) per condizioni composte — vedi [Operatori](./operators).
:::

## `while ... do`

Il `while` ripete il corpo **finché** la condizione è vera. La condizione viene verificata prima di ogni iterazione:

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

::: warning Attenzione ai cicli infiniti
Assicurati che qualcosa all'interno del `while` modifichi la condizione (come `contador := contador + 1`), altrimenti il ciclo non termina mai.
:::

## `for i := início to fim do`

Il `for` numerico conta da un valore iniziale fino a uno finale, **includendo entrambi** (intervallo inclusivo), incrementando di 1 alla volta:

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

Un esempio che somma i valori dell'intervallo con un blocco:

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

La variante `for..in` itera **ogni elemento** di un array, senza bisogno di indici:

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

Funziona anche con i blocchi, ideale per accumulare risultati:

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

::: tip Blocchi come corpo
Ogni volta che ti servono più istruzioni all'interno di `if`, `while` o `for`, racchiudile in `begin ... end`. Una singola istruzione non richiede il blocco.
:::

Ora impara a organizzare la logica riutilizzabile in [Funzioni e procedure](./functions).
