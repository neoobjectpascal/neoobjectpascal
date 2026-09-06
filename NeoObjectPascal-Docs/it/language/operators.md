# Operatori

Gli operatori combinano i valori nelle espressioni. NeoObjectPascal dispone di operatori aritmetici, di concatenazione, di confronto, booleani, unari e dell'operatore pipe.

## Aritmetici

I quattro operatori aritmetici sono `+`, `-`, `*` e `/`:

```npas
begin
    WriteLn(7 + 3);
    WriteLn(7 - 3);
    WriteLn(7 * 3);
    WriteLn(10 / 4);
end.
```

<Output>
10
4
21
2.5
</Output>

L'operatore `/` è la **divisione**. Quando gli operandi producono un risultato frazionario, esso è `Real`.

::: warning Divisione per zero
Dividere per zero **genera un errore** in fase di esecuzione. Puoi catturarlo con `try/catch` — vedi [Gestione degli errori](./error-handling).
:::

## Concatenazione di stringhe

L'operatore `+` **concatena** anche le stringhe. Se uno dei due lati è una `String`, il risultato è l'unione dei testi:

```npas
begin
    var nome: String;
    nome := "Ada";
    WriteLn("Olá, " + nome);
end.
```

<Output>
Olá, Ada
</Output>

## Confronto

Gli operatori di confronto restituiscono un `Boolean`:

| Operatore | Significato       |
| --------- | ----------------- |
| `=`       | uguale a          |
| `<>`      | diverso da        |
| `<`       | minore di         |
| `>`       | maggiore di       |
| `<=`      | minore o uguale   |
| `>=`      | maggiore o uguale |

```npas
begin
    WriteLn(5 = 5);
    WriteLn(5 <> 3);
    WriteLn(2 < 10);
    WriteLn(10 >= 10);
end.
```

<Output>
true
true
true
true
</Output>

::: warning L'uguaglianza è `=`, l'assegnazione è `:=`
L'uguaglianza usa un **singolo segno di uguale** (`=`). L'assegnazione usa `:=`. E "diverso da" è `<>`, non `!=`.

```npas
if idade = 18 then ...   // comparação
idade := 18;             // atribuição
```
:::

## Booleani

Combina le condizioni con `and`, `or` e `not`:

```npas
var a: Integer;
var b: Integer;
var admin: Boolean;

begin
    a := 10;
    b := 20;
    admin := false;

    if a > 0 and b > 0 then
        WriteLn("Ambos positivos");

    if not admin then
        WriteLn("Não é administrador");

    if (a > 5 and b > 5) or admin then
        WriteLn("Condição composta verdadeira");
end.
```

<Output>
Ambos positivos
Não é administrador
Condição composta verdadeira
</Output>

## Unari

Il `-` unario nega un numero e il `not` nega un booleano:

```npas
begin
    var x: Integer;
    x := 7;
    WriteLn(0 - x);   // negação por subtração
    WriteLn(not true);
end.
```

<Output>
-7
false
</Output>

## L'operatore pipe `|>`

L'operatore `|>` passa il valore di sinistra come **primo argomento** della funzione a destra. Concatena le trasformazioni in modo leggibile:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

function incrementar(n: Integer): Integer
begin
    return n + 1;
end;

begin
    WriteLn(10 |> dobrar |> incrementar);
end.
```

<Output>
21
</Output>

Qui `10 |> dobrar` produce `20`, e `20 |> incrementar` produce `21`. Scopri di più in [Programmazione funzionale](../features/functional).

## Tabella di precedenza

Dalla precedenza maggiore alla minore (gli operatori dello stesso livello vengono valutati da sinistra a destra):

| Livello | Operatori                      |
| ------- | ------------------------------ |
| 1       | `a[i]` (indice), chiamate      |
| 2       | `*`, `/`                       |
| 3       | `+`, `-`                       |
| 4       | `=`, `<>`, `<`, `>`, `<=`, `>=`|
| 5       | `and`                          |
| 6       | `or`                           |
| 7       | `\|>` (pipe)                    |
| 8       | unario `not`, `-`              |

Usa le parentesi ogni volta che vuoi rendere esplicita l'intenzione, come in `(a > 5 and b > 5) or admin`.

Ora usa gli operatori all'interno di decisioni e cicli in [Strutture di controllo](./control-flow).
