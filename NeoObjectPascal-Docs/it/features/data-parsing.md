# Manipolazione dei dati (JSON e CSV)

NeoObjectPascal offre supporto nativo per due dei formati di dati più comuni: **JSON** e **CSV**. Entrambi usano la stessa sintassi dichiarativa `parse(...) into variabile;`, che analizza una stringa e memorizza il risultato in una variabile di tipo `Object`.

## Analisi del JSON

Usa `JSON.parse(testoJson) into variabile;` per convertire una stringa JSON in un oggetto. Successivamente, accedi ai campi con la notazione a punto:

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

::: tip Le virgolette singole aiutano
Poiché il JSON usa internamente le virgolette doppie, scrivere la stringa con **virgolette singole** (`'...'`) evita conflitti. Le due forme di virgolette sono equivalenti per i letterali di stringa in NeoObjectPascal.
:::

### Oggetti annidati

L'accesso tramite punto funziona a qualsiasi livello di profondità della struttura:

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

## Analisi del CSV

Usa `CSV.parse(testoCsv) into variabile;` per convertire una stringa CSV in una lista di righe, dove ogni riga è una lista di colonne. Il risultato è un `Object` che puoi percorrere:

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

### Percorrere le righe

Poiché il risultato di `CSV.parse` è iterabile, puoi usare `for..in` per elaborare ogni riga:

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

::: warning Anche l'intestazione è una riga
`CSV.parse` non distingue l'intestazione dalle righe di dati: la prima riga (`produto,preco`) viene restituita come qualsiasi altra. Se il tuo CSV ha un'intestazione, ignora l'indice `0` durante l'elaborazione dei dati.
:::

## Combinare con pipe e Java

I dati analizzati sono valori comuni e possono alimentare una pipeline funzionale o un blocco Java. Ad esempio, trasformare un campo subito dopo il parse:

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

## Buone pratiche

- Valida l'origine dei dati: le stringhe provenienti da file o dalla rete possono essere malformate. Racchiudi il parse in `try/catch` quando l'input non è affidabile — vedi [Gestione degli errori](../language/error-handling).
- Dichiara la variabile di destinazione come `Object`, poiché JSON e CSV producono strutture dinamiche.
- Per CSV con intestazione, tratta la riga `0` separatamente dalle altre.

---

Di seguito, scopri come interagire con la console in [Input e output](./io).
