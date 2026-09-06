# Funktionen und Prozeduren

Funktionen kapseln wiederverwendbare Logik und **geben einen Wert zurück**. Am Anfang der Datei deklarieren Sie Funktionen mit dem Schlüsselwort `function`.

## Eine Funktion deklarieren

Die allgemeine Form ist `function nome(parâmetros): Tipo begin ... return expr; end;`. Beachten Sie das abschließende `;` nach der Deklaration auf oberster Ebene:

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

Der Typ nach dem `:` ist der Typ des zurückgegebenen Werts. Die Anweisung `return expr;` gibt diesen Wert zurück und beendet die Funktion.

## Parameter

Die Parameter werden durch **Kommas** getrennt. Sie können **typisiert** oder **untypisiert** sein:

```npas
// parâmetros tipados
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

// parâmetros sem tipo explícito
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

::: tip Typisieren Sie Ihre Parameter
Die Angabe des Typs der Parameter macht die Absicht klar und hilft dem Interpreter, die Werte zu validieren. Bevorzugen Sie typisierte Parameter, wann immer möglich.
:::

## Funktionen aufrufen

Rufen Sie eine Funktion über ihren Namen auf und übergeben Sie die Argumente in Klammern. Wenn die Funktion **keine Argumente** hat, sind die Klammern optional:

```npas
function agora(): String
begin
    return "sempre agora";
end;

begin
    WriteLn(agora());   // com parênteses
    WriteLn(agora);     // sem parênteses — equivalente
end.
```

<Output>
sempre agora
sempre agora
</Output>

## Rekursion

Eine Funktion kann sich selbst aufrufen. Das klassische Beispiel ist die Fakultät:

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

::: warning Haben Sie immer einen Basisfall
Jede rekursive Funktion benötigt eine Abbruchbedingung (hier `n <= 1`). Ohne sie endet die Rekursion nie.
:::

## Und die Prozeduren?

Eine **Prozedur** ist wie eine Funktion, gibt aber **keinen Wert zurück** — sie dient dazu, Effekte auszuführen (drucken, Zustand ändern). In NeoObjectPascal existieren Prozeduren als **Mitglieder von Klassen**, die mit dem Schlüsselwort `procedure` deklariert werden:

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

Um Prozeduren, Methoden und Sichtbarkeit im Detail zu erkunden, siehe [Klassen](../oop/classes).

Als Nächstes lernen Sie, mit Sammlungen in [Arrays](./arrays) zu arbeiten.
