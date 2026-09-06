# Inheritance and polymorphism

**Inheritance** lets one class reuse and specialize another, and **polymorphism** lets the same method call behave differently depending on the object's actual type at run time. Together, they make code extensible and expressive.

## Inheritance with `extends`

A class inherits from another using `extends`. The subclass receives all the fields and methods of the superclass and can add its own:

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

Here `Cachorro` inherits the `nome` field and the `descrever` method from `Animal`, and adds the `raca` field.

::: warning There is no `inherited`
NeoObjectPascal does **not** have the `inherited` keyword. To reuse the superclass's state, the subclass constructor assigns the inherited fields directly via `self` (like `self.nome := n;` above). There is no implicit call to the parent's constructor or method.
:::

## `virtual` and `override` methods

For a subclass to **replace** the behavior of a method, the superclass declares it as `virtual` and the subclass redefines it with `override`, keeping the same signature:

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

Each subclass defines its own version of `emitirSom`, replacing the implementation from `Animal`:

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

## Polymorphism

**Polymorphism** happens when the same method call produces different behaviors according to the object's actual type. The implementation that runs is chosen at run time — not by the declared type of the variable.

In the following example, `Forma` defines `virtual` methods that serve as a base, and each subclass redefines them with `override`:

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

Note two important details:

- `Quadrado` inherits from `Retangulo`, which in turn inherits from `Forma` — inheritance can span several levels.
- `Quadrado` redefines only `descrever`, but **reuses** the `calcularArea` from `Retangulo`. That is why a square with side 7 correctly computes an area of 49.

::: tip Program against the superclass
When you work with a collection of `Forma`, you can call `calcularArea()` on any element without knowing whether it is a `Retangulo` or a `Quadrado`. Each object responds with its own implementation — that is the essence of polymorphism.
:::

Inheritance fixes a hierarchy of classes; interfaces let you share a contract across unrelated classes. Continue to [Interfaces](./interfaces).
