# Herança e polimorfismo

A **herança** permite que uma classe reaproveite e especialize outra, e o **polimorfismo** permite que a mesma chamada de método se comporte de forma diferente conforme o tipo real do objeto em tempo de execução. Juntos, eles tornam o código extensível e expressivo.

## Herança com `extends`

Uma classe herda de outra usando `extends`. A subclasse recebe todos os campos e métodos da superclasse e pode acrescentar os seus:

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

Aqui `Cachorro` herda o campo `nome` e o método `descrever` de `Animal`, e adiciona o campo `raca`.

::: warning Não existe `inherited`
O NeoObjectPascal **não** possui a palavra-chave `inherited`. Para reaproveitar o estado da superclasse, o construtor da subclasse atribui diretamente os campos herdados via `self` (como `self.nome := n;` acima). Não há chamada implícita ao construtor ou método do pai.
:::

## Métodos `virtual` e `override`

Para que uma subclasse possa **substituir** o comportamento de um método, a superclasse o declara como `virtual` e a subclasse o redefine com `override`, mantendo a mesma assinatura:

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

Cada subclasse define sua própria versão de `emitirSom`, substituindo a implementação de `Animal`:

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

## Polimorfismo

O **polimorfismo** acontece quando uma mesma chamada de método produz comportamentos diferentes de acordo com o tipo real do objeto. A implementação executada é escolhida em tempo de execução — não pelo tipo declarado da variável.

No exemplo a seguir, `Forma` define métodos `virtual` que servem de base, e cada subclasse os redefine com `override`:

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

Repare em dois detalhes importantes:

- `Quadrado` herda de `Retangulo`, que por sua vez herda de `Forma` — a herança pode ter vários níveis.
- `Quadrado` redefine apenas `descrever`, mas **reaproveita** o `calcularArea` de `Retangulo`. É por isso que um quadrado de lado 7 calcula corretamente a área 49.

::: tip Programe pensando na superclasse
Quando você trabalha com uma coleção de `Forma`, pode chamar `calcularArea()` em qualquer elemento sem saber se é um `Retangulo` ou um `Quadrado`. Cada objeto responde com a sua própria implementação — essa é a essência do polimorfismo.
:::

Herança fixa uma hierarquia de classes; interfaces permitem compartilhar um contrato entre classes sem relação de parentesco. Siga para [Interfaces](./interfaces).
