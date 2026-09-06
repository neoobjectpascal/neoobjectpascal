# Ereditarietà e polimorfismo

L'**ereditarietà** permette a una classe di riutilizzare e specializzare un'altra, mentre il **polimorfismo** permette che la stessa chiamata di metodo si comporti in modo diverso a seconda del tipo reale dell'oggetto in fase di esecuzione. Insieme, rendono il codice estensibile ed espressivo.

## Ereditarietà con `extends`

Una classe eredita da un'altra usando `extends`. La sottoclasse riceve tutti i campi e i metodi della superclasse e può aggiungere i propri:

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

Qui `Cachorro` eredita il campo `nome` e il metodo `descrever` da `Animal`, e aggiunge il campo `raca`.

::: warning Non esiste `inherited`
NeoObjectPascal **non** possiede la parola chiave `inherited`. Per riutilizzare lo stato della superclasse, il costruttore della sottoclasse assegna direttamente i campi ereditati tramite `self` (come `self.nome := n;` sopra). Non c'è alcuna chiamata implicita al costruttore o al metodo del genitore.
:::

## Metodi `virtual` e `override`

Affinché una sottoclasse possa **sostituire** il comportamento di un metodo, la superclasse lo dichiara come `virtual` e la sottoclasse lo ridefinisce con `override`, mantenendo la stessa firma:

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

Ogni sottoclasse definisce la propria versione di `emitirSom`, sostituendo l'implementazione di `Animal`:

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

Il **polimorfismo** si verifica quando una stessa chiamata di metodo produce comportamenti diversi a seconda del tipo reale dell'oggetto. L'implementazione eseguita viene scelta in fase di esecuzione — non in base al tipo dichiarato della variabile.

Nell'esempio seguente, `Forma` definisce metodi `virtual` che fungono da base, e ogni sottoclasse li ridefinisce con `override`:

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

Nota due dettagli importanti:

- `Quadrado` eredita da `Retangulo`, che a sua volta eredita da `Forma` — l'ereditarietà può avere più livelli.
- `Quadrado` ridefinisce solo `descrever`, ma **riutilizza** il `calcularArea` di `Retangulo`. È per questo che un quadrato di lato 7 calcola correttamente l'area 49.

::: tip Programma pensando alla superclasse
Quando lavori con una collezione di `Forma`, puoi chiamare `calcularArea()` su qualsiasi elemento senza sapere se è un `Retangulo` o un `Quadrado`. Ogni oggetto risponde con la propria implementazione — questa è l'essenza del polimorfismo.
:::

L'ereditarietà fissa una gerarchia di classi; le interfacce permettono di condividere un contratto tra classi senza relazione di parentela. Prosegui verso [Interfacce](./interfaces).
