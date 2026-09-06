# Structure d'un programme

Un programme en NeoObjectPascal comporte trois parties, toutes optionnelles à l'exception de la clôture :

1. Une clause **`uses`** (importation de modules), terminée par `;`.
2. Zéro ou plusieurs **déclarations** (variables, fonctions, classes, interfaces, tests), chacune terminée par `;`.
3. Un **bloc principal** `begin ... end.` — le point final `.` termine le programme.

```npas
uses internal.string;

var mensagem: String;

function emMaiusculas(s: String): String
begin
    return toUpperCase(s);
end;

begin
    mensagem := emMaiusculas("olá");
    WriteLn(mensagem);
end.
```

<Output>
OLÁ
</Output>

::: warning Pas d'en-tête `program`
Contrairement à certains dialectes Pascal, NeoObjectPascal **n'utilise pas** d'en-tête `program Nom;`. Le programme commence directement par la clause `uses`, par les déclarations ou par le bloc `begin`.
:::

## Le bloc principal

Le bloc `begin ... end.` contient les instructions exécutées lorsque le programme s'exécute. Chaque instruction se termine par un point-virgule `;` :

```npas
begin
    WriteLn("primeira linha");
    WriteLn("segunda linha");
end.
```

Notez le **point final** après le `end` qui ferme le programme — il est obligatoire. Les blocs internes (de fonctions, de boucles, etc.) se terminent uniquement par `end` (sans point).

## Commentaires

NeoObjectPascal ne dispose **que de commentaires sur une seule ligne**, introduits par `//` :

```npas
begin
    // Ceci est un commentaire
    WriteLn("Oi"); // fonctionne aussi en fin de ligne
end.
```

::: warning Pas de commentaires de bloc
Il n'existe pas de commentaires `{ ... }` ni `(* ... *)`. Les accolades `{ }` sont réservées aux [blocs de code Java](../features/java-integration).
:::

## Déclarations en haut vs. à l'intérieur du bloc

Vous pouvez déclarer des variables aussi bien en haut du fichier qu'à l'intérieur d'un bloc :

```npas
var x: Integer;   // déclaration en haut

begin
    x := 10;
    var y: Integer;   // déclaration à l'intérieur du bloc
    y := 20;
    WriteLn(x + y);
end.
```

<Output>
30
</Output>

Avec la structure en place, passez à [Variables et types](../language/variables-and-types).
