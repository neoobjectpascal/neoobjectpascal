# Programmation fonctionnelle

NeoObjectPascal intègre des idées issues de la programmation fonctionnelle afin que vous puissiez écrire un code plus concis, expressif et facile à maintenir. La fonctionnalité centrale est l'**opérateur pipe** `|>`, qui enchaîne les transformations de manière lisible, de gauche à droite.

## L'opérateur pipe `|>`

L'opérateur `|>` passe la valeur de gauche comme **premier argument** de la fonction à droite. Autrement dit, `valeur |> fonction` équivaut à `fonction(valeur)` :

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

begin
    WriteLn(dobrar(5));      // chamada tradicional
    WriteLn(5 |> dobrar);    // com pipe — mesmo resultado
end.
```

<Output>
10
10
</Output>

Les deux styles produisent le même résultat. L'avantage du pipe apparaît lorsqu'on enchaîne plusieurs transformations.

## Enchaîner des fonctions

Comme le pipe transmet le résultat d'une fonction comme entrée de la suivante, vous pouvez composer une séquence d'étapes sans imbriquer les appels :

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

function incrementar(n: Integer): Integer
begin
    return n + 1;
end;

function aoQuadrado(n: Integer): Integer
begin
    return n * n;
end;

begin
    // Leia da esquerda para a direita: 10 → 20 → 21 → 441
    WriteLn(10 |> dobrar |> incrementar |> aoQuadrado);
end.
```

<Output>
441
</Output>

Comparez avec la forme imbriquée `aoQuadrado(incrementar(dobrar(10)))`. Le pipe élimine les parenthèses et rend l'ordre des opérations immédiatement visible.

::: tip Lisez comme une phrase
Un enchaînement avec `|>` se lit dans l'ordre où les étapes se déroulent. C'est comme un « tapis roulant » de données : chaque fonction reçoit le résultat de la précédente.
:::

## Fonctions petites et composables

Le pipe favorise un style dans lequel vous créez de **petites fonctions à responsabilité unique** et les combinez. Chaque fonction reste simple, testable et réutilisable :

```npas
function limpar(texto: String): String
begin
    // Remove um prefixo de espaço e normaliza
    return "[" + texto + "]";
end;

function emMaiusculas(texto: String): String
begin
    return java:(texto) {
        return ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn("relatorio" |> emMaiusculas |> limpar);
end.
```

<Output>
[RELATORIO]
</Output>

Remarquez que `emMaiusculas` délègue le gros du travail à un bloc Java. Vous pouvez librement mélanger des fonctions NeoObjectPascal et des blocs `java:(...) { ... }` au sein d'un pipeline. Voir les détails dans [Intégration avec Java](./java-integration).

## Combiner le pipe avec l'arithmétique

Le pipe a une faible précédence, si bien que les expressions arithmétiques à sa gauche sont évaluées avant d'être canalisées :

```npas
function descrever(n: Integer): String
begin
    if n > 100 then
        return "grande"
    else
        return "pequeno";
end;

begin
    WriteLn(20 * 3 |> descrever);   // (20 * 3) = 60 → "pequeno"
    WriteLn(50 * 3 |> descrever);   // (50 * 3) = 150 → "grande"
end.
```

<Output>
pequeno
grande
</Output>

::: info Précédence
Le `|>` se situe juste au-dessus des opérateurs unaires dans la table de précédence. Consultez [Opérateurs](../language/operators) pour la table complète et utilisez des parenthèses chaque fois que vous voulez rendre l'intention explicite.
:::

## Bonnes pratiques

- Privilégiez les fonctions **pures** (sans effets de bord) dans les pipelines : pour un même argument, elles renvoient toujours le même résultat.
- Donnez des noms qui décrivent la transformation (`dobrar`, `emMaiusculas`, `descrever`), et non l'étape (`passo1`).
- Gardez chaque fonction courte. Si une étape devient trop grande, découpez-la en fonctions plus petites et reliez-les avec `|>`.

Ces petites fonctions sont également faciles à couvrir avec le framework de tests natif — voir [Tests unitaires](../testing/unit-testing).

---

Ensuite, apprenez à transformer des textes structurés en objets avec [Manipulation de données (JSON et CSV)](./data-parsing).
