# Intégration avec Java

Comme NeoObjectPascal est interprété au-dessus de la JVM, vous pouvez **incorporer du code Java directement** dans votre programme. Cela donne accès à tout l'écosystème Java — bibliothèques de dates, de mathématiques, de texte et bien plus — sans quitter la syntaxe Pascal.

## Syntaxe d'un bloc Java

Un bloc Java est une expression qui produit une valeur. La forme générale est :

```npas
java:(arg0, arg1) { <código Java que retorna um valor> }
```

- Entre parenthèses, on place les valeurs NeoObjectPascal que vous voulez rendre disponibles au bloc.
- À l'intérieur des accolades `{ }` se trouve du code Java. Il doit se terminer par un `return`.
- La valeur renvoyée par le bloc devient la valeur de l'expression en NeoObjectPascal.

::: info Les accolades appartiennent à Java
En NeoObjectPascal, `{ }` délimite **exclusivement** le corps d'un bloc Java. Il n'existe pas de commentaires de bloc — les commentaires sont uniquement `// ligne unique`.
:::

## Accéder aux paramètres : `param0`, `param1`, ...

À l'intérieur du bloc, les arguments sont accédés par **position**, avec les noms `param0`, `param1`, `param2`, et ainsi de suite. Comme Java est typé, il faut effectuer le **cast** vers le type approprié :

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        return "Olá, " + ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));
end.
```

<Output>
Olá, ADA
</Output>

L'argument `nome` a été transmis au bloc en tant que `param0` puis converti en `String` avant d'appeler `.toUpperCase()`.

## Accéder aux arguments par leur nom

Lorsqu'un argument d'un bloc Java est un **identifiant simple** — comme dans `java:(nome)` —, vous pouvez le référencer à l'intérieur du bloc **par son nom**, et pas seulement en tant que `param0`. Cet alias nommé est déclaré avec le type d'exécution de l'argument ; ainsi, pour `String`, `Integer`, `Boolean` et `Real`, le cast devient inutile.

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        // 'nome' est déjà une String — pas besoin de (String)param0
        return "Olá, " + nome.toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));   // Olá, ADA
end.
```

<Output>
Olá, ADA
</Output>

Comparez avec l'ancien style positionnel `((String)param0).toUpperCase()` : l'alias nommé supprime le cast et rend le corps Java plus lisible.

`param0`, `param1`, ... restent disponibles (entièrement rétrocompatible) et constituent le moyen d'accéder aux arguments qui ne sont **pas** des identifiants simples — comme `java:(a + b)`, un littéral ou un appel de fonction —, car ceux-ci n'ont pas de nom.

::: warning Mots réservés et collisions
Si le nom de l'argument correspond à un mot réservé de Java (`class`, `int`, ...) ou à une variable locale déclarée à l'intérieur du bloc, l'alias nommé est ignoré — utilisez `param0` dans ce cas.
:::

## Plusieurs paramètres

Chaque argument supplémentaire reçoit l'index suivant. Ici `a` est `param0` et `b` est `param1` :

```npas
function somaDeQuadrados(a: Integer, b: Integer): Integer
begin
    return java:(a, b) {
        Integer x = (Integer)param0;
        Integer y = (Integer)param1;
        return x * x + y * y;
    };
end;

begin
    WriteLn(somaDeQuadrados(3, 4));
end.
```

<Output>
25
</Output>

## Utiliser les bibliothèques de la JVM

Le véritable gain réside dans l'appel de classes Java. Utilisez toujours le **nom pleinement qualifié** de la classe, car il n'est pas possible de déclarer un `import` :

```npas
function raizQuadrada(numero: Integer): Real
begin
    return java:(numero) {
        Double n = ((Integer)param0).doubleValue();
        return Math.sqrt(n);
    };
end;

function agora(): String
begin
    return java:() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        return hoje.toString();
    };
end;

begin
    WriteLn("Raiz de 144: ", raizQuadrada(144));
    WriteLn("Data de hoje: ", agora());
end.
```

<Output>
Raiz de 144: 12.0
Data de hoje: 2026-07-17
</Output>

Remarquez que `agora` utilise `java:()` **sans paramètres** — le bloc Java peut être autosuffisant.

## Utilisation directe dans les expressions

Un bloc Java n'a pas besoin de se trouver dans une fonction : il peut apparaître directement dans n'importe quelle expression, y compris comme argument de `WriteLn` :

```npas
begin
    WriteLn("Cálculo direto: ", java:(10, 20) {
        Integer a = (Integer)param0;
        Integer b = (Integer)param1;
        return a + b;
    });
end.
```

<Output>
Cálculo direto: 30
</Output>

## Comment les bibliothèques internes s'en servent

Les [bibliothèques internes](./internal-libraries) de NeoObjectPascal sont, en grande partie, écrites avec des blocs Java. Par exemple, `toUpperCase` de la bibliothèque `internal.string` est littéralement :

```npas
function toUpperCase(s): String
begin
    return java:(s) {
        return ((String)param0).toUpperCase();
    };
end;
```

Autrement dit, vous pouvez écrire vos propres bibliothèques en suivant le même modèle.

## Considérations

- **Cast explicite** : les paramètres arrivent en tant qu'`Object` ; effectuez le cast vers `Integer`, `String`, `Boolean`, etc.
- **Pas d'`import`** : utilisez des noms qualifiés comme `java.time.LocalDate`.
- **Exceptions** : les exceptions Java levées dans le bloc sont capturées par le runtime et peuvent être traitées avec `try/catch`.
- **Performance** : il y a un léger coût de compilation dynamique lors de la première exécution de chaque bloc.

::: tip Combinez avec le pipe
Les fonctions qui enveloppent des blocs Java s'intègrent naturellement dans des pipelines `|>`. Voir [Programmation fonctionnelle](./functional).
:::

---

Ensuite, organisez votre code en [Modules et uses](./modules).
