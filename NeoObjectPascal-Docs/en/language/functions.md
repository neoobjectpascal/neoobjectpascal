# Functions and procedures

Functions encapsulate reusable logic and **return a value**. At the top of the file, you declare functions with the `function` keyword.

## Declaring a function

The general form is `function name(parameters): Type begin ... return expr; end;`. Note the trailing `;` after a top-level declaration:

```npas
function saudar(nome: String): String
begin
    return "Olá, " + nome + "!";
end;

begin
    WriteLn(saudar("Ana"));
end.
```

<Output>
Olá, Ana!
</Output>

The type after the `:` is the type of the returned value. The statement `return expr;` returns that value and ends the function.

## Parameters

Parameters are separated by a **comma**. They can be **typed** or **untyped**:

```npas
// typed parameters
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

// parameters without an explicit type
function juntar(x, y): String
begin
    return x + " " + y;
end;

begin
    WriteLn(somar(3, 4));
    WriteLn(juntar("bom", "dia"));
end.
```

<Output>
7
bom dia
</Output>

::: tip Type your parameters
Declaring parameter types makes the intent clear and helps the interpreter validate values. Prefer typed parameters whenever possible.
:::

## Calling functions

Call a function by its name, passing the arguments in parentheses. If the function **takes no arguments**, the parentheses are optional:

```npas
function agora(): String
begin
    return "sempre agora";
end;

begin
    WriteLn(agora());   // with parentheses
    WriteLn(agora);     // without parentheses — equivalent
end.
```

<Output>
sempre agora
sempre agora
</Output>

## Recursion

A function can call itself. The classic example is the factorial:

```npas
function fatorial(n: Integer): Integer
begin
    if n <= 1 then
        return 1;
    return n * fatorial(n - 1);
end;

begin
    WriteLn("5! = ", fatorial(5));
    WriteLn("6! = ", fatorial(6));
end.
```

<Output>
5! = 120
6! = 720
</Output>

::: warning Always have a base case
Every recursive function needs a stopping condition (here, `n <= 1`). Without one, the recursion never ends.
:::

## What about procedures?

A **procedure** is like a function, but it **returns no value** — it exists to perform effects (printing, changing state). In NeoObjectPascal, procedures exist as **class members**, declared with the `procedure` keyword:

```npas
class Logger
    public procedure registrar(msg: String)
    begin
        WriteLn("[LOG] " + msg);
    end;
end;

var log: Object;

begin
    log := new Logger();
    log.registrar("iniciando");
end.
```

<Output>
[LOG] iniciando
</Output>

To explore procedures, methods, and visibility in depth, see [Classes](../oop/classes).

Next, learn how to work with collections in [Arrays](./arrays).
