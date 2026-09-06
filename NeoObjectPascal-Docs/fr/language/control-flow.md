# Structures de contrôle

Les structures de contrôle décident **quelles** instructions exécuter et **combien de fois**. NeoObjectPascal propose `if/else`, `while`, le `for` numérique et `for..in`.

Dans chacune d'elles, le corps peut être une instruction unique ou un bloc `begin ... end` contenant plusieurs instructions.

## `if ... then ... else`

Le `if` exécute une instruction lorsque la condition est vraie. Le `else` est facultatif :

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

Pour exécuter plusieurs instructions, utilisez un bloc comme corps :

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

::: info Enchaîner les conditions
Combinez `if` avec les opérateurs booléens (`and`, `or`, `not`) pour former des conditions composées — voir [Opérateurs](./operators).
:::

## `while ... do`

Le `while` répète le corps **tant que** la condition est vraie. La condition est testée avant chaque itération :

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

::: warning Attention aux boucles infinies
Assurez-vous qu'un élément à l'intérieur du `while` modifie la condition (comme `contador := contador + 1`), sinon la boucle ne se termine jamais.
:::

## `for i := début to fin do`

Le `for` numérique compte d'une valeur initiale jusqu'à une valeur finale, **les deux incluses** (intervalle inclusif), en incrémentant de 1 à chaque pas :

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

Un exemple additionnant les valeurs de l'intervalle avec un bloc :

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

La variante `for..in` itère sur **chaque élément** d'un tableau, sans avoir besoin d'indices :

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

Cela fonctionne aussi avec des blocs, idéal pour accumuler des résultats :

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

::: tip Les blocs comme corps
Chaque fois que vous avez besoin de plus d'une instruction à l'intérieur de `if`, `while` ou `for`, encadrez-les avec `begin ... end`. Une instruction unique n'a pas besoin de bloc.
:::

Ensuite, apprenez à organiser une logique réutilisable dans [Fonctions et procédures](./functions).
