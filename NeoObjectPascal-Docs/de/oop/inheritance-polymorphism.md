# Vererbung und Polymorphie

Die **Vererbung** ermöglicht es einer Klasse, eine andere wiederzuverwenden und zu spezialisieren, und die **Polymorphie** ermöglicht es, dass sich derselbe Methodenaufruf je nach dem tatsächlichen Typ des Objekts zur Laufzeit unterschiedlich verhält. Zusammen machen sie den Code erweiterbar und ausdrucksstark.

## Vererbung mit `extends`

Eine Klasse erbt von einer anderen mit `extends`. Die Unterklasse erhält alle Felder und Methoden der Oberklasse und kann ihre eigenen hinzufügen:

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

Hier erbt `Cachorro` das Feld `nome` und die Methode `descrever` von `Animal` und fügt das Feld `raca` hinzu.

::: warning Es gibt kein `inherited`
NeoObjectPascal besitzt **nicht** das Schlüsselwort `inherited`. Um den Zustand der Oberklasse wiederzuverwenden, weist der Konstruktor der Unterklasse die geerbten Felder direkt über `self` zu (wie `self.nome := n;` oben). Es gibt keinen impliziten Aufruf des Konstruktors oder der Methode der Elternklasse.
:::

## `virtual`- und `override`-Methoden

Damit eine Unterklasse das Verhalten einer Methode **ersetzen** kann, deklariert die Oberklasse sie als `virtual` und die Unterklasse definiert sie mit `override` neu, wobei dieselbe Signatur beibehalten wird:

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

Jede Unterklasse definiert ihre eigene Version von `emitirSom` und ersetzt damit die Implementierung von `Animal`:

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

## Polymorphie

Die **Polymorphie** tritt auf, wenn ein und derselbe Methodenaufruf je nach tatsächlichem Typ des Objekts unterschiedliches Verhalten erzeugt. Die ausgeführte Implementierung wird zur Laufzeit gewählt — nicht anhand des deklarierten Typs der Variable.

Im folgenden Beispiel definiert `Forma` `virtual`-Methoden, die als Basis dienen, und jede Unterklasse definiert sie mit `override` neu:

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

Beachten Sie zwei wichtige Details:

- `Quadrado` erbt von `Retangulo`, das wiederum von `Forma` erbt — die Vererbung kann mehrere Ebenen haben.
- `Quadrado` definiert nur `descrever` neu, **verwendet** aber das `calcularArea` von `Retangulo` **wieder**. Deshalb berechnet ein Quadrat mit Seitenlänge 7 korrekt die Fläche 49.

::: tip Programmieren Sie mit Blick auf die Oberklasse
Wenn Sie mit einer Sammlung von `Forma` arbeiten, können Sie `calcularArea()` auf jedem Element aufrufen, ohne zu wissen, ob es ein `Retangulo` oder ein `Quadrado` ist. Jedes Objekt antwortet mit seiner eigenen Implementierung — das ist die Essenz der Polymorphie.
:::

Die Vererbung legt eine Klassenhierarchie fest; Schnittstellen ermöglichen es, einen Vertrag zwischen Klassen ohne Verwandtschaftsbeziehung zu teilen. Weiter zu [Schnittstellen](./interfaces).
