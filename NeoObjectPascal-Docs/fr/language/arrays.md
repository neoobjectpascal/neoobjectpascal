# Tableaux

Un `Array` est une collection ordonnée de valeurs. Il contient des nombres, des chaînes ou n'importe quelle valeur, et son indexation commence à **zéro** (base 0).

## Déclarer et créer

Déclarez avec le type `Array` et affectez un **littéral** entre crochets. Les éléments sont séparés par une virgule :

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

Un tableau vide s'écrit `[]` :

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

## Accéder par indice

Utilisez `array[indice]` pour lire un élément. Le **premier** élément se trouve à l'indice `0` :

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

## Affecter des éléments

Modifiez un élément avec `array[i] := valeur;` :

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

## Itérer avec `for..in`

La manière la plus directe de parcourir un tableau est le `for..in`, qui fournit chaque élément directement :

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

Cela fonctionne aussi avec des tableaux de chaînes :

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

## Indices hors intervalle

Accéder à un indice inexistant **lève une erreur** à l'exécution :

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    WriteLn(numeros[5]);   // erro: índice fora do intervalo
end.
```

::: warning Le dépassement d'intervalle est capturable
Un accès hors des limites génère une erreur que vous pouvez encadrer avec `try/catch`. Voir [Gestion des erreurs](./error-handling).
:::

## Fonctions auxiliaires

Pour des opérations plus avancées — trier, filtrer, mapper, additionner, concaténer — importez la bibliothèque `internal.collections` :

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

::: tip Des collections complètes
`internal.collections` fournit `arraySort`, `arrayFilter`, `arrayMap`, `arrayReduce`, `arrayReverse`, `arrayContains` et bien plus encore. Voir [Bibliothèques internes](../features/internal-libraries).
:::

Ensuite, apprenez à gérer les défaillances d'exécution dans [Gestion des erreurs](./error-handling).
