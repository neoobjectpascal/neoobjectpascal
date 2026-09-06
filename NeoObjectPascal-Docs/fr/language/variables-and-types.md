# Variables et types

Toute variable en NeoObjectPascal possède un **type** déclaré. La déclaration utilise le mot-clé `var`, suivi du nom, de deux-points et du type :

```npas
var idade: Integer;
var nome: String;
```

L'affectation de valeurs se fait avec l'opérateur `:=` (à ne pas confondre avec `=`, qui est l'opérateur d'égalité) :

```npas
begin
    var idade: Integer;
    idade := 30;
    WriteLn("Idade: ", idade);
end.
```

<Output>
Idade: 30
</Output>

## Où déclarer

Vous pouvez déclarer des variables à deux endroits :

- **En tête de fichier**, avant le bloc principal.
- **À l'intérieur d'un bloc** `begin ... end`, comme une instruction ordinaire.

```npas
var total: Integer;   // declaração no topo

begin
    total := 100;
    var imposto: Integer;   // declaração dentro do bloco
    imposto := total / 10;
    WriteLn("Imposto: ", imposto);
end.
```

<Output>
Imposto: 10
</Output>

## Les dix types

NeoObjectPascal dispose de dix types intégrés :

| Type      | Description                                            | Exemples de littéraux        |
| --------- | ----------------------------------------------------- | ---------------------------- |
| `Integer` | Nombres entiers                                       | `0`, `42`, `-7`              |
| `Real`    | Nombres à virgule flottante (double précision)        | `3.14159`, `5.0`, `-0.5`     |
| `String`  | Texte                                                 | `"olá"`, `'mundo'`           |
| `Boolean` | Valeur logique                                        | `true`, `false`              |
| `Array`   | Collection ordonnée de valeurs                        | `[1, 2, 3]`, `["a", "b"]`    |
| `Object`  | Type générique (instances, JSON/CSV, valeurs diverses)| `new Pessoa("Ana")`          |
| `Date`    | Date calendaire (ISO `yyyy-MM-dd`)                   | `today()`, `date(2026,12,25)`|
| `Time`    | Heure de la journée (`HH:mm:ss`)                     | `currentTime()`, `time(10,30,0)`|
| `DateTime`| Date et heure combinées                               | `now()`                      |
| `Currency`| Montant décimal exact (sans erreur de virgule flottante)| `currency(199.90)`         |

En plus de ceux-ci, `Double` et `Float` sont des **alias de `Real`** (virgule flottante 64 bits) — utilisez le nom que vous préférez. Les types `Date`, `Time`, `DateTime` et `Currency` sont détaillés dans [Dates, heures et monnaie](../features/dates-and-currency).

## Littéraux et détails de chaque type

### Integer et Real

Les entiers s'écrivent directement : `10`, `-3`, `0`. Les nombres `Real` nécessitent des chiffres **des deux côtés** du point décimal :

```npas
begin
    var meio: Real;
    meio := 0.5;    // correto
    WriteLn(meio);
end.
```

<Output>
0.5
</Output>

::: warning Real nécessite des chiffres complets
Écrivez `5.0`, jamais `5.` — la seconde forme est invalide. De même, utilisez `0.5` et non `.5`.
:::

Lorsque vous mélangez `Integer` et `Real` dans une expression, l'entier est **promu automatiquement** en `Real`. Il en va de même lorsque vous affectez un entier à une variable `Real` :

```npas
begin
    var x: Real;
    x := 5;          // 5 vira 5.0
    var y: Real;
    y := 2 * 3.5;    // resultado Real
    WriteLn("x = ", x);
    WriteLn("y = ", y);
end.
```

<Output>
x = 5.0
y = 7.0
</Output>

### String

Les chaînes peuvent utiliser des **guillemets doubles** ou des **guillemets simples** — les deux fonctionnent de la même manière :

```npas
begin
    var a: String;
    var b: String;
    a := "aspas duplas";
    b := 'aspas simples';
    WriteLn(a);
    WriteLn(b);
end.
```

<Output>
aspas duplas
aspas simples
</Output>

L'opérateur `+` **concatène** les chaînes. Si l'un des côtés est une `String`, le `+` assemble les valeurs au lieu de les additionner :

```npas
begin
    var nome: String;
    nome := "Mundo";
    WriteLn("Olá, " + nome + "!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Boolean

Un `Boolean` contient `true` ou `false` (également acceptés en majuscules, comme `True` ou `TRUE`) :

```npas
begin
    var ativo: Boolean;
    ativo := true;
    WriteLn("Ativo: ", ativo);
end.
```

<Output>
Ativo: true
</Output>

### Object

`Object` est le type générique du langage. Il contient des instances de classes, des résultats de `JSON.parse`/`CSV.parse` et toute valeur dont vous n'avez pas besoin de nommer le type précis :

```npas
class Pessoa
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public function saudar(): String
    begin
        return "Sou " + self.nome;
    end;
end;

var p: Object;

begin
    p := new Pessoa("Alice");
    WriteLn(p.saudar());
end.
```

<Output>
Sou Alice
</Output>

::: tip Choix du type
Préférez le type le plus spécifique possible (`Integer`, `String`, etc.) et réservez `Object` aux instances et aux données dynamiques.
:::

Maintenant que vous savez déclarer des variables, découvrez comment les combiner dans [Opérateurs](./operators).
