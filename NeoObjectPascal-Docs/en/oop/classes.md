# Classes and objects

**Object orientation** is one of the pillars of NeoObjectPascal. A **class** is a template that groups data (fields) and behavior (methods); an **object** is a concrete instance of that template, created with `new`.

## Declaring a class

A class begins with the `class` keyword, followed by its name, its members, and the closing `end;`. Each member (field, constructor, method) ends with `;`.

```npas
class Pessoa
    var nome: String;
    var idade: Integer;

    constructor Create(n: String, i: Integer)
    begin
        self.nome := n;
        self.idade := i;
    end;

    public function apresentar(): String
    begin
        return "Olá, meu nome é " + self.nome + " e tenho " + self.idade + " anos.";
    end;
end;
```

::: info Naming convention
Class names use **PascalCase** (`Pessoa`, `ContaBancaria`). It is the same convention used for interfaces.
:::

## Fields

Fields hold the **state** of each object. They are declared with `var name: Type;`, exactly like an ordinary variable, but inside the body of the class:

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;
end;
```

::: warning Fields are private by default
Every field is **private** by default — the intent is for it to be accessed only by the methods of its own class. Expose state through public methods rather than leaving the field directly accessible.
:::

## The `Create` constructor

The **constructor** initializes a new object. By convention it is called `Create`, is public, and receives the initial values of the fields:

```npas
constructor Create(saldoInicial: Real)
begin
    self.saldo := saldoInicial;
end;
```

## `self` — the current instance

Inside any method or the constructor, `self` refers to the **current object**. Use `self.field` to read or assign a field and to distinguish it from a parameter with the same name:

```npas
constructor Create(nome: String)
begin
    self.nome := nome;   // self.nome is the field; nome is the parameter
end;
```

## Methods: functions and procedures

The behavior of a class is defined by **functions** (which return a value with `return`) and **procedures** (which return nothing). Both can take comma-separated parameters:

```npas
public function saldoAtual(): Real
begin
    return self.saldo;
end;

public procedure depositar(valor: Real)
begin
    self.saldo := self.saldo + valor;
end;
```

## Visibility: `public`, `private`, `protected`

The visibility modifiers document the **intended** access of each member:

- `public` — part of the public interface; can be called from outside the object.
- `private` — internal detail, meant for use only within the class.
- `protected` — internal, but shared with subclasses.

```npas
public function saldoAtual(): Real
begin
    return self.saldo;
end;

private procedure registrarLog(mensagem: String)
begin
    WriteLn("[LOG] " + mensagem);
end;
```

::: info Visibility as intent
The modifiers are recognized by the language and communicate the contract of the class. Treat them as design documentation: private fields and methods describe internal details that other parts of the code should not depend on.
:::

## Instantiating with `new`

An object is created with `new ClassName(arguments)`, passing the values expected by the constructor. Variables that hold objects are declared with the class type or with the generic type `Object`:

```npas
var conta: ContaBancaria;

begin
    conta := new ContaBancaria(100.0);
end.
```

## Accessing fields and methods

Use the dot to call methods and (when allowed) read fields of an object: `obj.method(args)` and `obj.field`.

```npas
conta.depositar(50.0);
WriteLn(conta.saldoAtual());
```

## Complete example: bank account

The example below brings together private fields, a constructor, public methods, `self`, and instantiation:

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;

    constructor Create(titular: String, saldoInicial: Real)
    begin
        self.titular := titular;
        self.saldo := saldoInicial;
    end;

    public procedure depositar(valor: Real)
    begin
        self.saldo := self.saldo + valor;
    end;

    public function sacar(valor: Real): Boolean
    begin
        if valor > self.saldo then
            return false;
        self.saldo := self.saldo - valor;
        return true;
    end;

    public function extrato(): String
    begin
        return "Titular: " + self.titular + " | Saldo: " + self.saldo;
    end;
end;

var conta: ContaBancaria;

begin
    conta := new ContaBancaria("Ana", 100.0);
    conta.depositar(50.0);

    if conta.sacar(30.0) then
        WriteLn("Saque realizado.")
    else
        WriteLn("Saldo insuficiente.");

    WriteLn(conta.extrato());
end.
```

<Output>
Saque realizado.
Titular: Ana | Saldo: 120.0
</Output>

::: tip Objects as `Object`
You can also declare the variable as `var conta: Object;`. The `Object` type is generic and accepts any instance — useful when the same code needs to work with objects of different classes.
:::

With a class in place, the next step is to reuse and specialize behavior. Continue to [Inheritance and polymorphism](./inheritance-polymorphism).
