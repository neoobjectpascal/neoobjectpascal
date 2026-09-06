# Héritage et polymorphisme

L'**héritage** permet à une classe de réutiliser et de spécialiser une autre, et le **polymorphisme** permet à un même appel de méthode de se comporter différemment selon le type réel de l'objet à l'exécution. Ensemble, ils rendent le code extensible et expressif.

## Héritage avec `extends`

Une classe hérite d'une autre à l'aide de `extends`. La sous-classe reçoit tous les champs et méthodes de la superclasse et peut ajouter les siens :

```npas
class Animal
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public function descrever(): String
    begin
        return "Sou um animal chamado " + self.nome;
    end;
end;

class Cachorro extends Animal
    var raca: String;

    constructor Create(n: String, r: String)
    begin
        self.nome := n;
        self.raca := r;
    end;
end;
```

Ici, `Cachorro` hérite du champ `nome` et de la méthode `descrever` d'`Animal`, et ajoute le champ `raca`.

::: warning `inherited` n'existe pas
NeoObjectPascal **ne** possède **pas** le mot-clé `inherited`. Pour réutiliser l'état de la superclasse, le constructeur de la sous-classe affecte directement les champs hérités via `self` (comme `self.nome := n;` ci-dessus). Il n'y a aucun appel implicite au constructeur ou à la méthode du parent.
:::

## Méthodes `virtual` et `override`

Pour qu'une sous-classe puisse **remplacer** le comportement d'une méthode, la superclasse la déclare comme `virtual` et la sous-classe la redéfinit avec `override`, en conservant la même signature :

```npas
class Animal
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public virtual function emitirSom(): String
    begin
        return "Som genérico";
    end;
end;

class Cachorro extends Animal
    var raca: String;

    constructor Create(n: String, r: String)
    begin
        self.nome := n;
        self.raca := r;
    end;

    public override function emitirSom(): String
    begin
        return "Au au! Meu nome é " + self.nome;
    end;
end;

class Gato extends Animal
    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public override function emitirSom(): String
    begin
        return "Miau! Eu sou " + self.nome;
    end;
end;
```

Chaque sous-classe définit sa propre version de `emitirSom`, remplaçant l'implémentation d'`Animal` :

```npas
var animal: Animal;
var cachorro: Cachorro;
var gato: Gato;

begin
    animal := new Animal("Animal Genérico");
    WriteLn(animal.emitirSom());

    cachorro := new Cachorro("Rex", "Labrador");
    WriteLn(cachorro.emitirSom());

    gato := new Gato("Mimi");
    WriteLn(gato.emitirSom());
end.
```

<Output>
Som genérico
Au au! Meu nome é Rex
Miau! Eu sou Mimi
</Output>

## Polymorphisme

Le **polymorphisme** se produit lorsqu'un même appel de méthode produit des comportements différents selon le type réel de l'objet. L'implémentation exécutée est choisie à l'exécution — et non selon le type déclaré de la variable.

Dans l'exemple suivant, `Forma` définit des méthodes `virtual` qui servent de base, et chaque sous-classe les redéfinit avec `override` :

```npas
class Forma
    public virtual function calcularArea(): Integer
    begin
        return 0;
    end;

    public virtual function descrever(): String
    begin
        return "Forma genérica";
    end;
end;

class Retangulo extends Forma
    var largura: Integer;
    var altura: Integer;

    constructor Create(l: Integer, a: Integer)
    begin
        self.largura := l;
        self.altura := a;
    end;

    public override function calcularArea(): Integer
    begin
        return self.largura * self.altura;
    end;

    public override function descrever(): String
    begin
        return "Retângulo " + self.largura + "x" + self.altura;
    end;
end;

class Quadrado extends Retangulo
    constructor Create(lado: Integer)
    begin
        self.largura := lado;
        self.altura := lado;
    end;

    public override function descrever(): String
    begin
        return "Quadrado " + self.largura + "x" + self.largura;
    end;
end;

var forma: Forma;
var retangulo: Retangulo;
var quadrado: Quadrado;

begin
    forma := new Forma();
    WriteLn(forma.descrever(), " - Área: ", forma.calcularArea());

    retangulo := new Retangulo(5, 10);
    WriteLn(retangulo.descrever(), " - Área: ", retangulo.calcularArea());

    quadrado := new Quadrado(7);
    WriteLn(quadrado.descrever(), " - Área: ", quadrado.calcularArea());
end.
```

<Output>
Forma genérica - Área: 0
Retângulo 5x10 - Área: 50
Quadrado 7x7 - Área: 49
</Output>

Remarquez deux détails importants :

- `Quadrado` hérite de `Retangulo`, qui à son tour hérite de `Forma` — l'héritage peut comporter plusieurs niveaux.
- `Quadrado` ne redéfinit que `descrever`, mais **réutilise** le `calcularArea` de `Retangulo`. C'est pourquoi un carré de côté 7 calcule correctement l'aire 49.

::: tip Programmez en pensant à la superclasse
Lorsque vous travaillez avec une collection de `Forma`, vous pouvez appeler `calcularArea()` sur n'importe quel élément sans savoir s'il s'agit d'un `Retangulo` ou d'un `Quadrado`. Chaque objet répond avec sa propre implémentation — c'est l'essence même du polymorphisme.
:::

L'héritage fixe une hiérarchie de classes ; les interfaces permettent de partager un contrat entre des classes sans lien de parenté. Continuez avec [Interfaces](./interfaces).
