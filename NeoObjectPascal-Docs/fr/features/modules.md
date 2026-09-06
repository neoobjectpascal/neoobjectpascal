# Modules et uses

À mesure qu'un programme grandit, il est judicieux de le diviser en fichiers plus petits et réutilisables. En NeoObjectPascal, cela se fait avec la clause `uses`, qui charge aussi bien des **bibliothèques internes** que des **modules dans des fichiers** que vous écrivez vous-même.

## La clause `uses`

La clause `uses` apparaît au **début** du programme, avant toute déclaration. Elle liste les modules à charger, séparés par des virgules, et se termine par `;` :

```npas
uses internal.math, internal.string;

begin
    WriteLn("Máximo: ", max(10, 20));
    WriteLn("Maiúsculas: ", toUpperCase("neo"));
end.
```

<Output>
Máximo: 20
Maiúsculas: NEO
</Output>

Après le `uses`, toutes les fonctions des modules deviennent disponibles comme si elles avaient été déclarées dans votre propre fichier.

## Bibliothèques internes vs modules en fichier

Il existe deux types de modules, distingués par leur préfixe :

| Forme                  | Origine                                            | Exemple                    |
| ---------------------- | ------------------------------------------------- | -------------------------- |
| `internal.<nome>`      | Bibliothèques empaquetées dans l'interpréteur lui-même | `uses internal.datetime;`  |
| `<pasta>.<modulo>`     | Fichier `.npas` relatif à votre programme          | `uses utils.matematica;`   |

Le préfixe `internal.` est réservé : le runtime le reconnaît et charge la bibliothèque depuis l'intérieur du JAR. Tout autre chemin est traité comme un fichier de votre projet.

## Créer un module en fichier

Un module est simplement un fichier `.npas` contenant des déclarations de fonctions, procédures ou variables. Par exemple, créez `matematica.npas` :

```npas
// matematica.npas
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

function subtrair(a: Integer, b: Integer): Integer
begin
    return a - b;
end;
```

Et utilisez-le dans le programme principal, dans le même répertoire :

```npas
// programa.npas
uses matematica;

begin
    WriteLn(somar(10, 5));
    WriteLn(subtrair(10, 5));
end.
```

<Output>
15
5
</Output>

L'interpréteur recherche `matematica.npas` à partir du répertoire du fichier principal.

::: warning Le séparateur de paramètres est la virgule
Lors de la déclaration de paramètres typés, séparez-les par une **virgule** : `function somar(a: Integer, b: Integer)`. N'utilisez pas de point-virgule entre les paramètres.
:::

## Modules hiérarchiques

Vous pouvez organiser les modules en sous-dossiers en utilisant le point comme séparateur de chemin. `uses utils.matematica;` charge le fichier `utils/matematica.npas`, relatif au programme principal :

```text
projeto/
├── programa.npas
└── utils/
    └── matematica.npas
```

```npas
// programa.npas
uses utils.matematica;

begin
    WriteLn(somar(2, 3));
end.
```

<Output>
5
</Output>

## Charger plusieurs modules

Une seule clause `uses` peut mélanger des bibliothèques internes et vos propres modules, tous séparés par des virgules :

```npas
uses internal.math, internal.string, utils.matematica;

begin
    WriteLn(square(4));              // de internal.math
    WriteLn(quote("olá"));          // de internal.string
    WriteLn(somar(1, 1));           // do seu módulo utils.matematica
end.
```

<Output>
16
"olá"
2
</Output>

## Bonnes pratiques

- Regroupez les fonctions apparentées dans un même module (par exemple, tout ce qui concerne le texte dans un `texto.npas`).
- Préférez de nombreux petits fichiers cohérents à un unique fichier géant.
- Utilisez des sous-dossiers (`utils.`, `dominio.`) pour organiser les modules par domaine du système.
- Il n'y a qu'une seule clause `uses` par programme : listez-y tous les modules.

---

Ensuite, découvrez en détail chacune des [Bibliothèques internes](./internal-libraries).
