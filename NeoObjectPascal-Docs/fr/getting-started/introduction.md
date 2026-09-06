# Introduction

**NeoObjectPascal** est un langage de programmation moderne inspiré d'Object Pascal, doté d'une syntaxe claire et familière, mais équipé de fonctionnalités contemporaines : orientation objet complète, un framework de tests natif, gestion des erreurs avec `try/catch/finally`, tableaux natifs, programmation fonctionnelle et intégration directe avec Java.

Il est interprété par un runtime écrit en Java (avec ANTLR4), ce qui apporte deux avantages : la portabilité de la JVM et la possibilité d'**incorporer du code Java directement** dans votre programme lorsque vous avez besoin de quelque chose que seul l'écosystème Java offre.

## Philosophie

- **Facile à apprendre** — la syntaxe de style Pascal est explicite et lisible, idéale pour les débutants.
- **Puissant** — l'orientation objet, les tests, le mocking, la manipulation de JSON/CSV et l'opérateur pipe permettent d'écrire un code concis et expressif.
- **Moderne** — les tableaux, `for..in`, l'arithmétique `Real`, les opérateurs booléens et la gestion des erreurs rendent le code robuste et facile à maintenir.

## Un premier aperçu

```npas
// Un programme complet en NeoObjectPascal
class Pessoa
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public function saudar(): String
    begin
        return "Olá, eu sou " + self.nome;
    end;
end;

var p: Object;

begin
    p := new Pessoa("Alice");
    WriteLn(p.saudar());
end.
```

<Output>
Olá, eu sou Alice
</Output>

## Ce que vous allez apprendre dans ce guide

- Écrire et exécuter vos premiers programmes.
- Déclarer des variables et utiliser les types `Integer`, `String`, `Boolean`, `Real`, `Object` et `Array`.
- Utiliser les structures de contrôle : `if`, `while`, `for` et `for..in`.
- Créer des fonctions, des procédures et des classes avec héritage, interfaces et polymorphisme.
- Gérer les erreurs avec `try/catch/finally` et `raise`.
- Organiser le code en modules avec `uses` et utiliser les bibliothèques internes.
- Écrire des tests unitaires et des mocks avec le framework natif.
- Intégrer du code Java et exécuter dans le cloud.

::: tip Prêt à commencer ?
Passez à [Installation et premier programme](./installation) et lancez l'interpréteur.
:::
