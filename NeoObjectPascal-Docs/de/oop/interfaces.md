# Schnittstellen

Eine **Schnittstelle** ist ein Vertrag: Sie deklariert, *welche* Methoden eine Klasse bereitstellen muss, ohne zu sagen, *wie* sie funktionieren. Klassen, die die Schnittstelle implementieren, verpflichten sich, jede dieser Methoden bereitzustellen. Schnittstellen ermöglichen es, Code zu schreiben, der von einem Verhalten abhängt und nicht von einer spezifischen Implementierung.

## Eine Schnittstelle deklarieren

Verwenden Sie das Schlüsselwort `interface`, gefolgt vom Namen und nur den **Signaturen** der Methoden — ohne Rumpf. Jede Signatur endet mit `;`, und der Block schließt mit `end;` ab:

```npas
interface Calculavel
    function calcular(a: Integer, b: Integer): Integer;
end;
```

::: info Nur Signaturen
Eine Schnittstelle hat weder Felder noch Methodenrumpf. Sie beschreibt, *was* existieren soll; die Klasse, die sie implementiert, entscheidet, *wie*.
:::

## Eine Schnittstelle implementieren

Eine Klasse deklariert mit `implements`, dass sie einen Vertrag erfüllt. Sie muss dann eine Implementierung für jede in der Schnittstelle deklarierte Methode bereitstellen:

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

Sowohl `Soma` als auch `Multiplicacao` erfüllen den Vertrag `Calculavel`, jede auf ihre eigene Weise:

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

## Mehrere Schnittstellen

Eine Klasse kann **mehr als eine Schnittstelle** gleichzeitig implementieren, indem die Namen durch Kommas getrennt werden. In diesem Fall muss sie alle Methoden aller Schnittstellen bereitstellen:

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

## Für eine Schnittstelle programmieren

Der große Vorteil von Schnittstellen besteht darin, dass der Code vom **Vertrag** abhängen kann und nicht von einer konkreten Klasse. Da `Soma` und `Multiplicacao` `Calculavel` erfüllen, kann ein und dieselbe generische Variable auf jede von ihnen zeigen, und die richtige Methode wird zur Laufzeit aufgerufen:

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

::: tip Schnittstelle vs. Vererbung
Verwenden Sie **Vererbung** (`extends`), wenn die Klassen eine „ist ein Typ von"-Beziehung teilen und die Implementierung wiederverwenden. Verwenden Sie **Schnittstellen** (`implements`), wenn nicht verwandte Klassen lediglich denselben Satz von Operationen garantieren müssen. Eine Klasse kann nur eine Oberklasse erweitern, aber mehrere Schnittstellen implementieren.
:::

Mit Klassen, Vererbung, Polymorphie und Schnittstellen beherrschen Sie die Objektorientierung von NeoObjectPascal. Das nächste Thema bringt einen ergänzenden Stil des Codeschreibens. Weiter zu [Funktionale Programmierung](../features/functional).
