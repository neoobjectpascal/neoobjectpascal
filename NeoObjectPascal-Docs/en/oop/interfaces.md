# Interfaces

An **interface** is a contract: it declares *which* methods a class must provide, without saying *how* they work. Classes that implement the interface commit to providing each of those methods. Interfaces let you write code that depends on a behavior, not on a specific implementation.

## Declaring an interface

Use the `interface` keyword, followed by the name and only the method **signatures** — no body. Each signature ends with `;`, and the block closes with `end;`:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;
```

::: info Signatures only
An interface has no fields and no method body. It describes *what* must exist; the class that implements it decides *how*.
:::

## Implementing an interface

A class declares that it fulfills a contract with `implements`. It must then provide an implementation for every method declared in the interface:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

class Soma implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

class Multiplicacao implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a * b;
    end;
end;
```

Both `Soma` and `Multiplicacao` honor the `Calculavel` contract, each in its own way:

```npas
var soma: Soma;
var mult: Multiplicacao;
var resultado: Integer;

begin
    soma := new Soma();
    resultado := soma.calcular(10, 5);
    WriteLn("Soma: 10 + 5 = ", resultado);

    mult := new Multiplicacao();
    resultado := mult.calcular(10, 5);
    WriteLn("Multiplicação: 10 * 5 = ", resultado);
end.
```

<Output>
Soma: 10 + 5 = 15
Multiplicação: 10 * 5 = 50
</Output>

## Multiple interfaces

A class can implement **more than one interface** at the same time, separating the names with commas. In that case it must provide all the methods of all the interfaces:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

interface Descritivel
    function descrever(): String;
end;

class Somador implements Calculavel, Descritivel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;

    public function descrever(): String
    begin
        return "Operação de soma de dois inteiros";
    end;
end;

var s: Somador;

begin
    s := new Somador();
    WriteLn(s.descrever());
    WriteLn("Resultado: ", s.calcular(3, 4));
end.
```

<Output>
Operação de soma de dois inteiros
Resultado: 7
</Output>

## Programming to an interface

The great benefit of interfaces is letting code depend on the **contract**, not on a concrete class. Since `Soma` and `Multiplicacao` fulfill `Calculavel`, a single generic variable can point to either of them and the correct method is called at run time:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;

class Soma implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a + b;
    end;
end;

class Multiplicacao implements Calculavel
    public function calcular(a: Integer, b: Integer): Integer
    begin
        return a * b;
    end;
end;

var operacao: Object;

begin
    operacao := new Soma();
    WriteLn("Soma: ", operacao.calcular(6, 2));

    operacao := new Multiplicacao();
    WriteLn("Produto: ", operacao.calcular(6, 2));
end.
```

<Output>
Soma: 8
Produto: 12
</Output>

::: tip Interface vs. inheritance
Use **inheritance** (`extends`) when classes share an "is a kind of" relationship and reuse implementation. Use **interfaces** (`implements`) when unrelated classes only need to guarantee the same set of operations. A class can extend only one superclass, but implement several interfaces.
:::

With classes, inheritance, polymorphism, and interfaces, you have mastered object orientation in NeoObjectPascal. The next topic introduces a complementary style of writing code. Continue to [Functional programming](../features/functional).
