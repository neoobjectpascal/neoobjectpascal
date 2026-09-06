# Integration mit Java

Da NeoObjectPascal auf der JVM interpretiert wird, können Sie **Java-Code direkt einbetten** in Ihr Programm. Das verschafft Ihnen Zugang zum gesamten Java-Ökosystem — Bibliotheken für Datum, Mathematik, Text und vieles mehr — ohne die Pascal-Syntax zu verlassen.

## Syntax eines Java-Blocks

Ein Java-Block ist ein Ausdruck, der einen Wert erzeugt. Die allgemeine Form lautet:

```npas
java:(arg0, arg1) { <código Java que retorna um valor> }
```

- In die Klammern kommen die NeoObjectPascal-Werte, die Sie dem Block zur Verfügung stellen möchten.
- Innerhalb der geschweiften Klammern `{ }` steht Java-Code. Er muss mit einem `return` enden.
- Der vom Block zurückgegebene Wert wird zum Wert des Ausdrucks in NeoObjectPascal.

::: info Die geschweiften Klammern gehören zu Java
In NeoObjectPascal begrenzt `{ }` **ausschließlich** den Rumpf eines Java-Blocks. Blockkommentare gibt es nicht — Kommentare sind ausschließlich `// einzelne Zeile`.
:::

## Zugriff auf die Parameter: `param0`, `param1`, ...

Innerhalb des Blocks wird auf die Argumente über ihre **Position** zugegriffen, mit den Namen `param0`, `param1`, `param2` und so weiter. Da Java typisiert ist, muss ein **Cast** in den passenden Typ erfolgen:

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        return "Olá, " + ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));
end.
```

<Output>
Olá, ADA
</Output>

Das Argument `nome` wurde dem Block als `param0` übergeben und vor dem Aufruf von `.toUpperCase()` in `String` umgewandelt.

## Zugriff auf Argumente über den Namen

Wenn ein Argument eines Java-Blocks ein **einfacher Bezeichner** ist — wie in `java:(nome)` —, können Sie es innerhalb des Blocks **über seinen Namen** ansprechen und nicht nur als `param0`. Dieser benannte Alias wird mit dem Laufzeittyp des Arguments deklariert, sodass bei `String`, `Integer`, `Boolean` und `Real` der Cast entfällt.

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        // 'nome' ist bereits ein String — kein (String)param0 nötig
        return "Olá, " + nome.toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));   // Olá, ADA
end.
```

<Output>
Olá, ADA
</Output>

Vergleichen Sie dies mit dem alten positionsbasierten Stil `((String)param0).toUpperCase()`: Der benannte Alias erspart den Cast und macht den Java-Rumpf lesbarer.

`param0`, `param1`, ... bleiben verfügbar (vollständig abwärtskompatibel) und sind der Weg, um auf Argumente zuzugreifen, die **keine** einfachen Bezeichner sind — etwa `java:(a + b)`, ein Literal oder ein Funktionsaufruf —, da diese keinen Namen haben.

::: warning Reservierte Wörter und Namenskonflikte
Stimmt der Argumentname mit einem reservierten Wort von Java (`class`, `int`, ...) oder mit einer innerhalb des Blocks deklarierten lokalen Variable überein, wird der benannte Alias übersprungen — verwenden Sie in diesem Fall `param0`.
:::

## Mehrere Parameter

Jedes weitere Argument erhält den nächsten Index. Hier ist `a` gleich `param0` und `b` gleich `param1`:

```npas
function somaDeQuadrados(a: Integer, b: Integer): Integer
begin
    return java:(a, b) {
        Integer x = (Integer)param0;
        Integer y = (Integer)param1;
        return x * x + y * y;
    };
end;

begin
    WriteLn(somaDeQuadrados(3, 4));
end.
```

<Output>
25
</Output>

## Nutzung von JVM-Bibliotheken

Der eigentliche Gewinn liegt im Aufruf von Java-Klassen. Verwenden Sie stets den **vollständig qualifizierten Namen** der Klasse, da sich kein `import` deklarieren lässt:

```npas
function raizQuadrada(numero: Integer): Real
begin
    return java:(numero) {
        Double n = ((Integer)param0).doubleValue();
        return Math.sqrt(n);
    };
end;

function agora(): String
begin
    return java:() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        return hoje.toString();
    };
end;

begin
    WriteLn("Raiz de 144: ", raizQuadrada(144));
    WriteLn("Data de hoje: ", agora());
end.
```

<Output>
Raiz de 144: 12.0
Data de hoje: 2026-07-17
</Output>

Beachten Sie, dass `agora` `java:()` **ohne Parameter** verwendet — ein Java-Block kann durchaus eigenständig sein.

## Direkte Verwendung in Ausdrücken

Ein Java-Block muss sich nicht innerhalb einer Funktion befinden: Er kann direkt in jedem beliebigen Ausdruck auftauchen, auch als Argument von `WriteLn`:

```npas
begin
    WriteLn("Cálculo direto: ", java:(10, 20) {
        Integer a = (Integer)param0;
        Integer b = (Integer)param1;
        return a + b;
    });
end.
```

<Output>
Cálculo direto: 30
</Output>

## Wie die internen Bibliotheken dies nutzen

Die [internen Bibliotheken](./internal-libraries) von NeoObjectPascal sind größtenteils mit Java-Blöcken geschrieben. Zum Beispiel ist `toUpperCase` aus der Bibliothek `internal.string` buchstäblich:

```npas
function toUpperCase(s): String
begin
    return java:(s) {
        return ((String)param0).toUpperCase();
    };
end;
```

Mit anderen Worten: Sie können Ihre eigenen Bibliotheken nach demselben Muster schreiben.

## Hinweise

- **Expliziter Cast**: Die Parameter kommen als `Object` an; wandeln Sie sie in `Integer`, `String`, `Boolean` usw. um.
- **Kein `import`**: Verwenden Sie qualifizierte Namen wie `java.time.LocalDate`.
- **Ausnahmen**: Java-Ausnahmen, die im Block ausgelöst werden, werden von der Laufzeitumgebung abgefangen und können mit `try/catch` behandelt werden.
- **Leistung**: Bei der ersten Ausführung jedes Blocks entstehen geringe Kosten durch die dynamische Kompilierung.

::: tip Kombinieren Sie es mit der Pipe
Funktionen, die Java-Blöcke umschließen, fügen sich auf natürliche Weise in `|>`-Pipelines ein. Siehe [Funktionale Programmierung](./functional).
:::

---

Als Nächstes strukturieren Sie Ihren Code in [Module und uses](./modules).
