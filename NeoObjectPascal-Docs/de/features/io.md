# Ein- und Ausgabe

NeoObjectPascal bietet einfache Prozeduren für die Interaktion mit der Konsole: `WriteLn` zum Anzeigen von Informationen, `ReadLn` zum Einlesen von Benutzereingaben und `showMenu` zum Erstellen nummerierter Menüs.

## Schreiben mit `WriteLn`

Die Prozedur `WriteLn` schreibt eine Textzeile in die Konsole und fügt am Ende einen Zeilenumbruch hinzu:

```npas
begin
    WriteLn("Olá, Mundo!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Mehrere Argumente

`WriteLn` akzeptiert mehrere Argumente und **verkettet sie ohne Trennzeichen** in der Ausgabe. Das ist praktisch, um Meldungen mit dynamischen Werten zusammenzusetzen:

```npas
var nome: String;
var idade: Integer;

begin
    nome := "Carol";
    idade := 28;
    WriteLn("Olá, ", nome, "! Você tem ", idade, " anos.");
end.
```

<Output>
Olá, Carol! Você tem 28 anos.
</Output>

::: tip Leerzeile
Der Aufruf von `WriteLn()` ohne Argumente gibt lediglich einen Zeilenumbruch aus — praktisch, um Ausgabeblöcke voneinander zu trennen.
:::

## Lesen mit `ReadLn`

Die Prozedur `ReadLn` liest eine vom Benutzer eingegebene Zeile und speichert sie in einer Variablen. Der Wert wird in den Typ der Zielvariablen umgewandelt:

```npas
var nome: String;

begin
    WriteLn("Qual é o seu nome?");
    ReadLn(nome);
    WriteLn("Prazer, ", nome, "!");
end.
```

<Output>
Qual é o seu nome?
Prazer, Ada!
</Output>

Wenn die Zielvariable vom Typ `Integer` oder `Real` ist, wird der eingelesene Text automatisch in den entsprechenden numerischen Typ umgewandelt:

```npas
var idade: Integer;

begin
    WriteLn("Digite sua idade:");
    ReadLn(idade);

    if idade >= 18 then
        WriteLn("Maior de idade.")
    else
        WriteLn("Menor de idade.");
end.
```

<Output>
Digite sua idade:
Maior de idade.
</Output>

::: warning Typumwandlung
Wenn Sie in eine numerische Variable einlesen, stellen Sie sicher, dass die Eingabe tatsächlich eine Zahl ist. Unerwartete Eingaben können Umwandlungsfehler verursachen — behandeln Sie diese Fälle mit `try/catch`, wenn Sie nicht vertrauenswürdige Daten einlesen. Siehe [Fehlerbehandlung](../language/error-handling).
:::

## Menüs mit `showMenu`

Die Prozedur `showMenu` erhält eine Liste von Optionen und gibt automatisch ein nummeriertes Menü aus:

```npas
begin
    showMenu("Café", "Chá", "Suco");
end.
```

<Output>
1. Café
2. Chá
3. Suco
</Output>

Kombinieren Sie `showMenu` mit `ReadLn`, um eine Auswahloberfläche zu erstellen:

```npas
var opcao: Integer;

begin
    showMenu("Novo pedido", "Consultar", "Sair");
    WriteLn("Escolha uma opção:");
    ReadLn(opcao);

    if opcao = 1 then
        WriteLn("Iniciando novo pedido...")
    else if opcao = 2 then
        WriteLn("Consultando...")
    else
        WriteLn("Até logo!");
end.
```

<Output>
1. Novo pedido
2. Consultar
3. Sair
Escolha uma opção:
Iniciando novo pedido...
</Output>

## Bewährte Praktiken

- Verwenden Sie `WriteLn` mit mehreren Argumenten, anstatt alles mit `+` zu verketten: Das ist besser lesbar und vermeidet manuelle Umwandlungen.
- Zeigen Sie vor einem `ReadLn` stets eine Meldung an, damit der Benutzer weiß, was er eingeben soll.
- Berücksichtigen Sie beim Einlesen von Zahlen die Validierung oder Behandlung von Umwandlungsfehlern.

---

Als Nächstes erfahren Sie, wie Sie die Leistungsfähigkeit der JVM nutzen können, unter [Integration mit Java](./java-integration).
