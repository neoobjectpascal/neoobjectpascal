# Operatoren

Operatoren kombinieren Werte in Ausdrücken. NeoObjectPascal hat arithmetische Operatoren, Verkettungs-, Vergleichs-, boolesche, unäre Operatoren sowie den Pipe-Operator.

## Arithmetische Operatoren

Die vier arithmetischen Operatoren sind `+`, `-`, `*` und `/`:

```npas
begin
    WriteLn(7 + 3);
    WriteLn(7 - 3);
    WriteLn(7 * 3);
    WriteLn(10 / 4);
end.
```

<Output>
10
4
21
2.5
</Output>

Der Operator `/` ist die **Division**. Wenn die Operanden ein gebrochenes Ergebnis erzeugen, ist es `Real`.

::: warning Division durch Null
Die Division durch Null **löst einen Fehler** zur Laufzeit aus. Sie können ihn mit `try/catch` abfangen — siehe [Fehlerbehandlung](./error-handling).
:::

## String-Verkettung

Der Operator `+` **verkettet** auch Strings. Wenn eine der Seiten ein `String` ist, ist das Ergebnis die Verbindung der Texte:

```npas
begin
    var nome: String;
    nome := "Ada";
    WriteLn("Olá, " + nome);
end.
```

<Output>
Olá, Ada
</Output>

## Vergleich

Die Vergleichsoperatoren geben einen `Boolean` zurück:

| Operator | Bedeutung           |
| -------- | ------------------- |
| `=`      | gleich              |
| `<>`     | ungleich            |
| `<`      | kleiner als         |
| `>`      | größer als          |
| `<=`     | kleiner oder gleich |
| `>=`     | größer oder gleich  |

```npas
begin
    WriteLn(5 = 5);
    WriteLn(5 <> 3);
    WriteLn(2 < 10);
    WriteLn(10 >= 10);
end.
```

<Output>
true
true
true
true
</Output>

::: warning Gleichheit ist `=`, Zuweisung ist `:=`
Die Gleichheit verwendet ein **einzelnes Gleichheitszeichen** (`=`). Die Zuweisung verwendet `:=`. Und „ungleich" ist `<>`, nicht `!=`.

```npas
if idade = 18 then ...   // comparação
idade := 18;             // atribuição
```
:::

## Boolesche Operatoren

Kombinieren Sie Bedingungen mit `and`, `or` und `not`:

```npas
var a: Integer;
var b: Integer;
var admin: Boolean;

begin
    a := 10;
    b := 20;
    admin := false;

    if a > 0 and b > 0 then
        WriteLn("Ambos positivos");

    if not admin then
        WriteLn("Não é administrador");

    if (a > 5 and b > 5) or admin then
        WriteLn("Condição composta verdadeira");
end.
```

<Output>
Ambos positivos
Não é administrador
Condição composta verdadeira
</Output>

## Unäre Operatoren

Das unäre `-` negiert eine Zahl und `not` negiert einen booleschen Wert:

```npas
begin
    var x: Integer;
    x := 7;
    WriteLn(0 - x);   // negação por subtração
    WriteLn(not true);
end.
```

<Output>
-7
false
</Output>

## Der Pipe-Operator `|>`

Der Operator `|>` übergibt den Wert von links als **erstes Argument** der Funktion auf der rechten Seite. Er verkettet Transformationen auf lesbare Weise:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

function incrementar(n: Integer): Integer
begin
    return n + 1;
end;

begin
    WriteLn(10 |> dobrar |> incrementar);
end.
```

<Output>
21
</Output>

Hier ergibt `10 |> dobrar` den Wert `20`, und `20 |> incrementar` ergibt `21`. Mehr dazu unter [Funktionale Programmierung](../features/functional).

## Vorrangtabelle

Vom höchsten zum niedrigsten Vorrang (Operatoren auf derselben Ebene werden von links nach rechts ausgewertet):

| Ebene | Operatoren                     |
| ----- | ------------------------------ |
| 1     | `a[i]` (Index), Aufrufe        |
| 2     | `*`, `/`                       |
| 3     | `+`, `-`                       |
| 4     | `=`, `<>`, `<`, `>`, `<=`, `>=`|
| 5     | `and`                          |
| 6     | `or`                           |
| 7     | `\|>` (Pipe)                   |
| 8     | unär `not`, `-`                |

Verwenden Sie Klammern immer dann, wenn Sie die Absicht explizit machen möchten, wie in `(a > 5 and b > 5) or admin`.

Als Nächstes verwenden Sie Operatoren innerhalb von Entscheidungen und Schleifen in [Kontrollstrukturen](./control-flow).
