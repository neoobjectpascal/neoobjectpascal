# Opérateurs

Les opérateurs combinent des valeurs au sein d'expressions. NeoObjectPascal propose des opérateurs arithmétiques, de concaténation, de comparaison, booléens, unaires ainsi que l'opérateur pipe.

## Arithmétiques

Les quatre opérateurs arithmétiques sont `+`, `-`, `*` et `/` :

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

L'opérateur `/` réalise la **division**. Lorsque les opérandes produisent un résultat fractionnaire, celui-ci est de type `Real`.

::: warning Division par zéro
Diviser par zéro **lève une erreur** à l'exécution. Vous pouvez la capturer avec `try/catch` — voir [Gestion des erreurs](./error-handling).
:::

## Concaténation de chaînes

L'opérateur `+` **concatène** également les chaînes. Si l'un des côtés est une `String`, le résultat est l'assemblage des textes :

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

## Comparaison

Les opérateurs de comparaison renvoient un `Boolean` :

| Opérateur | Signification     |
| --------- | ----------------- |
| `=`       | égal à            |
| `<>`      | différent de      |
| `<`       | inférieur à       |
| `>`       | supérieur à       |
| `<=`      | inférieur ou égal |
| `>=`      | supérieur ou égal |

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

::: warning L'égalité est `=`, l'affectation est `:=`
L'égalité utilise un **unique signe égal** (`=`). L'affectation utilise `:=`. Et « différent de » s'écrit `<>`, non `!=`.

```npas
if idade = 18 then ...   // comparação
idade := 18;             // atribuição
```
:::

## Booléens

Combinez des conditions avec `and`, `or` et `not` :

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

## Unaires

Le `-` unaire nie un nombre et le `not` nie un booléen :

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

## L'opérateur pipe `|>`

L'opérateur `|>` passe la valeur de gauche comme **premier argument** de la fonction à droite. Il enchaîne les transformations de manière lisible :

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

Ici `10 |> dobrar` produit `20`, et `20 |> incrementar` produit `21`. Pour en savoir plus, voir [Programmation fonctionnelle](../features/functional).

## Table de priorité

De la priorité la plus élevée à la plus faible (les opérateurs de même niveau s'évaluent de gauche à droite) :

| Niveau | Opérateurs                     |
| ------ | ------------------------------ |
| 1      | `a[i]` (index), appels         |
| 2      | `*`, `/`                       |
| 3      | `+`, `-`                       |
| 4      | `=`, `<>`, `<`, `>`, `<=`, `>=`|
| 5      | `and`                          |
| 6      | `or`                           |
| 7      | `\|>` (pipe)                    |
| 8      | `not`, `-` unaires             |

Utilisez des parenthèses chaque fois que vous souhaitez rendre l'intention explicite, comme dans `(a > 5 and b > 5) or admin`.

Ensuite, utilisez les opérateurs au sein des décisions et des boucles dans [Structures de contrôle](./control-flow).
