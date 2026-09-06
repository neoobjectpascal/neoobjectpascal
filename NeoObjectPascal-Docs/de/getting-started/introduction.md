# Einführung

**NeoObjectPascal** ist eine moderne Programmiersprache, inspiriert von Object Pascal, mit einer klaren und vertrauten Syntax, jedoch ausgestattet mit zeitgemäßen Funktionen: vollständige Objektorientierung, ein natives Test-Framework, Fehlerbehandlung mit `try/catch/finally`, native Arrays, funktionale Programmierung und direkte Java-Integration.

Sie wird von einer in Java geschriebenen Laufzeitumgebung (mit ANTLR4) interpretiert, was zwei Vorteile mit sich bringt: die Portabilität der JVM und die Möglichkeit, **Java-Code direkt einzubetten**, wenn Sie etwas benötigen, das nur das Java-Ökosystem bietet.

## Philosophie

- **Leicht zu erlernen** — die Pascal-artige Syntax ist explizit und gut lesbar, ideal für Einsteiger.
- **Leistungsstark** — Objektorientierung, Tests, Mocking, JSON-/CSV-Verarbeitung und der Pipe-Operator ermöglichen es, prägnanten und ausdrucksstarken Code zu schreiben.
- **Modern** — Arrays, `for..in`, `Real`-Arithmetik, boolesche Operatoren und Fehlerbehandlung machen den Code robust und leicht wartbar.

## Ein erster Blick

```npas
// Ein vollständiges Programm in NeoObjectPascal
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

## Was Sie in diesem Leitfaden lernen

- Ihre ersten Programme schreiben und ausführen.
- Variablen deklarieren und die Typen `Integer`, `String`, `Boolean`, `Real`, `Object` und `Array` verwenden.
- Kontrollstrukturen nutzen: `if`, `while`, `for` und `for..in`.
- Funktionen, Prozeduren und Klassen mit Vererbung, Schnittstellen und Polymorphie erstellen.
- Fehler mit `try/catch/finally` und `raise` behandeln.
- Code in Modulen mit `uses` organisieren und die internen Bibliotheken verwenden.
- Unit-Tests und Mocks mit dem nativen Framework schreiben.
- Java-Code integrieren und in der Cloud ausführen.

::: tip Bereit loszulegen?
Fahren Sie fort mit [Installation und erstes Programm](./installation) und bringen Sie den Interpreter zum Laufen.
:::
