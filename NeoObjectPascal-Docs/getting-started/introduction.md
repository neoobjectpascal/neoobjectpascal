# Introdução

**NeoObjectPascal** é uma linguagem de programação moderna inspirada no Object Pascal, com uma sintaxe limpa e familiar, mas equipada com recursos contemporâneos: orientação a objetos completa, um framework de testes nativo, tratamento de erros com `try/catch/finally`, arrays nativos, programação funcional e integração direta com Java.

Ela é interpretada por um runtime escrito em Java (com ANTLR4), o que traz duas vantagens: portabilidade da JVM e a possibilidade de **incorporar código Java diretamente** no seu programa quando você precisar de algo que só o ecossistema Java oferece.

## Filosofia

- **Fácil de aprender** — a sintaxe estilo Pascal é explícita e legível, ótima para quem está começando.
- **Poderosa** — orientação a objetos, testes, mocking, manipulação de JSON/CSV e o operador pipe permitem escrever código conciso e expressivo.
- **Moderna** — arrays, `for..in`, aritmética `Real`, operadores booleanos e tratamento de erros tornam o código robusto e de fácil manutenção.

## Um primeiro olhar

```npas
// Um programa completo em NeoObjectPascal
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

## O que você vai aprender neste guia

- Escrever e executar seus primeiros programas.
- Declarar variáveis e usar os tipos `Integer`, `String`, `Boolean`, `Real`, `Object` e `Array`.
- Usar estruturas de controle: `if`, `while`, `for` e `for..in`.
- Criar funções, procedimentos e classes com herança, interfaces e polimorfismo.
- Tratar erros com `try/catch/finally` e `raise`.
- Organizar código em módulos com `uses` e usar as bibliotecas internas.
- Escrever testes de unidade e mocks com o framework nativo.
- Integrar código Java e executar na nuvem.

::: tip Pronto para começar?
Siga para [Instalação e primeiro programa](./installation) e coloque o interpretador para rodar.
:::
