# Introduction

**NeoObjectPascal** is a modern programming language inspired by Object Pascal, with a clean and familiar syntax but equipped with contemporary features: full object orientation, a native testing framework, error handling with `try/catch/finally`, native arrays, functional programming, and direct integration with Java.

It is interpreted by a runtime written in Java (with ANTLR4), which brings two advantages: JVM portability and the ability to **embed Java code directly** in your program when you need something only the Java ecosystem offers.

## Philosophy

- **Easy to learn** — the Pascal-style syntax is explicit and readable, great for beginners.
- **Powerful** — object orientation, testing, mocking, JSON/CSV manipulation, and the pipe operator let you write concise, expressive code.
- **Modern** — arrays, `for..in`, `Real` arithmetic, boolean operators, and error handling make code robust and easy to maintain.

## A first look

```npas
// A complete program in NeoObjectPascal
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

## What you'll learn in this guide

- Write and run your first programs.
- Declare variables and use the `Integer`, `String`, `Boolean`, `Real`, `Object`, and `Array` types.
- Use control structures: `if`, `while`, `for`, and `for..in`.
- Create functions, procedures, and classes with inheritance, interfaces, and polymorphism.
- Handle errors with `try/catch/finally` and `raise`.
- Organize code into modules with `uses` and use the internal libraries.
- Write unit tests and mocks with the native framework.
- Integrate Java code and run in the cloud.

::: tip Ready to start?
Head over to [Installation and your first program](./installation) and get the interpreter running.
:::
