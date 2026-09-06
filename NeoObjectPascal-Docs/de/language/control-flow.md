# Kontrollstrukturen

Kontrollstrukturen entscheiden, **welche** Anweisungen ausgeführt werden und **wie oft**. NeoObjectPascal bietet `if/else`, `while`, numerisches `for` und `for..in`.

In allen kann der Rumpf eine einzelne Anweisung oder ein Block `begin ... end` mit mehreren Anweisungen sein.

## `if ... then ... else`

Das `if` führt eine Anweisung aus, wenn die Bedingung wahr ist. Das `else` ist optional:

```npas
var idade: Integer;

begin
    idade := 20;

    if idade >= 18 then
        WriteLn("Maior de idade")
    else
        WriteLn("Menor de idade");
end.
```

<Output>
Maior de idade
</Output>

Um mehrere Anweisungen auszuführen, verwenden Sie einen Block als Rumpf:

```npas
var saldo: Integer;

begin
    saldo := 100;

    if saldo > 0 then
    begin
        WriteLn("Saldo positivo");
        WriteLn("Valor: ", saldo);
    end;
end.
```

<Output>
Saldo positivo
Valor: 100
</Output>

::: info Bedingungen verketten
Kombinieren Sie `if` mit booleschen Operatoren (`and`, `or`, `not`) für zusammengesetzte Bedingungen — siehe [Operatoren](./operators).
:::

## `while ... do`

Das `while` wiederholt den Rumpf, **solange** die Bedingung wahr ist. Die Bedingung wird vor jeder Iteration geprüft:

```npas
var contador: Integer;

begin
    contador := 1;
    while contador <= 3 do
    begin
        WriteLn("Iteração ", contador);
        contador := contador + 1;
    end;
end.
```

<Output>
Iteração 1
Iteração 2
Iteração 3
</Output>

::: warning Vorsicht vor Endlosschleifen
Stellen Sie sicher, dass etwas innerhalb des `while` die Bedingung ändert (wie `contador := contador + 1`), sonst endet die Schleife nie.
:::

## `for i := início to fim do`

Das numerische `for` zählt von einem Anfangswert bis zu einem Endwert, **einschließlich beider** (inklusives Intervall), und erhöht in Schritten von 1:

```npas
var i: Integer;

begin
    for i := 1 to 5 do
        WriteLn("Número ", i);
end.
```

<Output>
Número 1
Número 2
Número 3
Número 4
Número 5
</Output>

Ein Beispiel, das die Werte des Intervalls mit einem Block summiert:

```npas
var i: Integer;
var soma: Integer;

begin
    soma := 0;
    for i := 1 to 10 do
    begin
        soma := soma + i;
    end;
    WriteLn("Soma de 1 a 10: ", soma);
end.
```

<Output>
Soma de 1 a 10: 55
</Output>

## `for x in array do`

Die Variante `for..in` iteriert über **jedes Element** eines Arrays, ohne dass Indizes benötigt werden:

```npas
var nomes: Array;
var nome: String;

begin
    nomes := ["Alice", "Bob", "Carlos"];
    for nome in nomes do
        WriteLn("Olá, ", nome, "!");
end.
```

<Output>
Olá, Alice!
Olá, Bob!
Olá, Carlos!
</Output>

Es funktioniert auch mit Blöcken, ideal zum Ansammeln von Ergebnissen:

```npas
var numeros: Array;
var n: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30];
    soma := 0;
    for n in numeros do
    begin
        WriteLn("  ", n);
        soma := soma + n;
    end;
    WriteLn("Total: ", soma);
end.
```

<Output>
  10
  20
  30
Total: 60
</Output>

::: tip Blöcke als Rumpf
Wann immer Sie mehr als eine Anweisung innerhalb von `if`, `while` oder `for` benötigen, umschließen Sie sie mit `begin ... end`. Eine einzelne Anweisung kommt ohne den Block aus.
:::

Als Nächstes lernen Sie, wiederverwendbare Logik in [Funktionen und Prozeduren](./functions) zu organisieren.
