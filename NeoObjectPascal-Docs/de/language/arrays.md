# Arrays

Ein `Array` ist eine geordnete Sammlung von Werten. Es speichert Zahlen, Strings oder beliebige Werte, und seine Indizierung ist **nullbasiert**.

## Deklarieren und erstellen

Deklarieren Sie mit dem Typ `Array` und weisen Sie ein **Literal** in eckigen Klammern zu. Die Elemente werden durch Kommas getrennt:

```npas
var numeros: Array;
var nomes: Array;

begin
    numeros := [10, 20, 30, 40, 50];
    nomes := ["Alice", "Bob", "Carlos", "Diana"];
    WriteLn("Arrays criados");
end.
```

<Output>
Arrays criados
</Output>

Ein leeres Array ist `[]`:

```npas
var vazio: Array;

begin
    vazio := [];
    WriteLn("Array vazio pronto");
end.
```

<Output>
Array vazio pronto
</Output>

## Zugriff über den Index

Verwenden Sie `array[índice]`, um ein Element zu lesen. Das **erste** Element befindet sich am Index `0`:

```npas
var numeros: Array;
var nomes: Array;

begin
    numeros := [10, 20, 30, 40, 50];
    nomes := ["Alice", "Bob", "Carlos", "Diana"];

    WriteLn("Primeiro número: ", numeros[0]);
    WriteLn("Último nome: ", nomes[3]);
end.
```

<Output>
Primeiro número: 10
Último nome: Diana
</Output>

## Elemente zuweisen

Ändern Sie ein Element mit `array[i] := valor;`:

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    numeros[2] := 99;
    WriteLn("Terceiro após modificação: ", numeros[2]);
end.
```

<Output>
Terceiro após modificação: 99
</Output>

## Iterieren mit `for..in`

Der direkteste Weg, ein Array zu durchlaufen, ist `for..in`, das jedes Element direkt liefert:

```npas
var numeros: Array;
var item: Integer;
var soma: Integer;

begin
    numeros := [10, 20, 30, 40, 50];
    soma := 0;
    for item in numeros do
    begin
        WriteLn("  ", item);
        soma := soma + item;
    end;
    WriteLn("Soma: ", soma);
end.
```

<Output>
  10
  20
  30
  40
  50
Soma: 150
</Output>

Es funktioniert auch mit String-Arrays:

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

## Indizes außerhalb des Bereichs

Der Zugriff auf einen nicht existierenden Index **löst einen Fehler** zur Laufzeit aus:

```npas
var numeros: Array;

begin
    numeros := [10, 20, 30];
    WriteLn(numeros[5]);   // erro: índice fora do intervalo
end.
```

::: warning Außerhalb des Bereichs ist abfangbar
Ein Zugriff außerhalb der Grenzen erzeugt einen Fehler, den Sie in `try/catch` einschließen können. Siehe [Fehlerbehandlung](./error-handling).
:::

## Hilfsfunktionen

Für fortgeschrittenere Operationen — sortieren, filtern, abbilden, summieren, verketten — importieren Sie die Bibliothek `internal.collections`:

```npas
uses internal.collections;

var numeros: Array;

begin
    numeros := [3, 1, 2];
    WriteLn("Tamanho: ", arraySize(numeros));
    WriteLn("Soma: ", arraySum(numeros));
end.
```

<Output>
Tamanho: 3
Soma: 6
</Output>

::: tip Vollständige Sammlungen
`internal.collections` bietet `arraySort`, `arrayFilter`, `arrayMap`, `arrayReduce`, `arrayReverse`, `arrayContains` und vieles mehr. Siehe [Interne Bibliotheken](../features/internal-libraries).
:::

Als Nächstes lernen Sie, mit Ausführungsfehlern in [Fehlerbehandlung](./error-handling) umzugehen.
