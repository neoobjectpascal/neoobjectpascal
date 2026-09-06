# Datenverarbeitung (JSON und CSV)

NeoObjectPascal bietet native Unterstützung für zwei der gängigsten Datenformate: **JSON** und **CSV**. Beide verwenden dieselbe deklarative Syntax `parse(...) into variavel;`, die eine Zeichenkette analysiert und das Ergebnis in einer Variablen vom Typ `Object` ablegt.

## JSON analysieren

Verwenden Sie `JSON.parse(textoJson) into variavel;`, um eine JSON-Zeichenkette in ein Objekt umzuwandeln. Anschließend greifen Sie über die Punktnotation auf die Felder zu:

```npas
var dados: Object;
var jsonString: String;

begin
    jsonString := '{"nome": "Carmen", "idade": 30}';
    JSON.parse(jsonString) into dados;

    WriteLn("Nome: ", dados.nome);
    WriteLn("Idade: ", dados.idade);
end.
```

<Output>
Nome: Carmen
Idade: 30
</Output>

::: tip Einfache Anführungszeichen helfen
Da JSON intern doppelte Anführungszeichen verwendet, vermeidet das Schreiben der Zeichenkette mit **einfachen Anführungszeichen** (`'...'`) Konflikte. Beide Arten von Anführungszeichen sind für String-Literale in NeoObjectPascal gleichwertig.
:::

### Verschachtelte Objekte

Der Zugriff über den Punkt funktioniert in beliebiger Tiefe der Struktur:

```npas
var config: Object;

begin
    config := '{"servidor": {"host": "localhost", "porta": 8080}}';
    JSON.parse(config) into config;

    WriteLn("Host: ", config.servidor.host);
    WriteLn("Porta: ", config.servidor.porta);
end.
```

<Output>
Host: localhost
Porta: 8080
</Output>

## CSV analysieren

Verwenden Sie `CSV.parse(textoCsv) into variavel;`, um eine CSV-Zeichenkette in eine Liste von Zeilen umzuwandeln, wobei jede Zeile eine Liste von Spalten ist. Das Ergebnis ist ein `Object`, das Sie durchlaufen können:

```npas
var dados: Object;
var csvString: String;

begin
    csvString := "nome,idade
Carina,30
Bruno,25";
    CSV.parse(csvString) into dados;

    // dados[0] é a primeira linha; dados[0][0] é a primeira coluna
    WriteLn("Primeira pessoa: ", dados[0][0]);
    WriteLn("Idade dela: ", dados[0][1]);
end.
```

<Output>
Primeira pessoa: Carina
Idade dela: 30
</Output>

### Die Zeilen durchlaufen

Da das Ergebnis von `CSV.parse` iterierbar ist, können Sie `for..in` verwenden, um jede Zeile zu verarbeiten:

```npas
var linhas: Object;
var linha: Object;

begin
    linhas := "produto,preco
Cafe,12
Cha,8";
    CSV.parse(linhas) into linhas;

    for linha in linhas do
        WriteLn(linha[0], " custa ", linha[1]);
end.
```

<Output>
produto custa preco
Cafe custa 12
Cha custa 8
</Output>

::: warning Auch die Kopfzeile ist eine Zeile
`CSV.parse` unterscheidet nicht zwischen der Kopfzeile und den Datenzeilen: Die erste Zeile (`produto,preco`) wird wie jede andere zurückgegeben. Wenn Ihre CSV-Datei eine Kopfzeile hat, überspringen Sie beim Verarbeiten der Daten den Index `0`.
:::

## Kombination mit Pipe und Java

Die analysierten Daten sind gewöhnliche Werte und können eine funktionale Pipeline oder einen Java-Block speisen. Zum Beispiel, um ein Feld direkt nach dem Parsen umzuwandeln:

```npas
function emMaiusculas(texto: String): String
begin
    return java:(texto) {
        return ((String)param0).toUpperCase();
    };
end;

var pedido: Object;

begin
    pedido := '{"cliente": "ana silva"}';
    JSON.parse(pedido) into pedido;

    WriteLn(pedido.cliente |> emMaiusculas);
end.
```

<Output>
ANA SILVA
</Output>

## Bewährte Praktiken

- Prüfen Sie die Herkunft der Daten: Zeichenketten aus Dateien oder aus dem Netzwerk können fehlerhaft sein. Umschließen Sie das Parsen mit `try/catch`, wenn die Eingabe nicht vertrauenswürdig ist — siehe [Fehlerbehandlung](../language/error-handling).
- Deklarieren Sie die Zielvariable als `Object`, da JSON und CSV dynamische Strukturen erzeugen.
- Behandeln Sie bei CSV-Dateien mit Kopfzeile die Zeile `0` getrennt von den übrigen.

---

Als Nächstes erfahren Sie, wie Sie mit der Konsole interagieren, unter [Ein- und Ausgabe](./io).
