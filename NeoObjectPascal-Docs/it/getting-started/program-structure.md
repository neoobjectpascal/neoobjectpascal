# Struttura di un programma

Un programma in NeoObjectPascal ha tre parti, tutte facoltative tranne la chiusura:

1. Una clausola **`uses`** (importazione di moduli), terminata da `;`.
2. Zero o più **dichiarazioni** (variabili, funzioni, classi, interfacce, test), ciascuna terminata da `;`.
3. Un **blocco principale** `begin ... end.` — il punto finale `.` chiude il programma.

```npas
uses internal.string;

var mensagem: String;

function emMaiusculas(s: String): String
begin
    return toUpperCase(s);
end;

begin
    mensagem := emMaiusculas("olá");
    WriteLn(mensagem);
end.
```

<Output>
OLÁ
</Output>

::: warning Nessuna intestazione `program`
A differenza di alcuni dialetti Pascal, NeoObjectPascal **non** usa un'intestazione `program Nome;`. Il programma inizia direttamente dalla clausola `uses`, dalle dichiarazioni o dal blocco `begin`.
:::

## Il blocco principale

Il blocco `begin ... end.` contiene le istruzioni eseguite quando il programma viene avviato. Ogni istruzione termina con un punto e virgola `;`:

```npas
begin
    WriteLn("primeira linha");
    WriteLn("segunda linha");
end.
```

Nota il **punto finale** dopo l'`end` che chiude il programma — è obbligatorio. I blocchi interni (di funzioni, cicli, ecc.) terminano solo con `end` (senza punto).

## Commenti

NeoObjectPascal ha **solo commenti su una riga**, iniziati da `//`:

```npas
begin
    // Questo è un commento
    WriteLn("Oi"); // funziona anche alla fine della riga
end.
```

::: warning Niente commenti a blocco
Non esistono commenti `{ ... }` o `(* ... *)`. Le parentesi graffe `{ }` sono riservate per i [blocchi di codice Java](../features/java-integration).
:::

## Dichiarazioni in cima vs. all'interno del blocco

Puoi dichiarare variabili sia in cima al file sia all'interno di un blocco:

```npas
var x: Integer;   // dichiarazione in cima

begin
    x := 10;
    var y: Integer;   // dichiarazione all'interno del blocco
    y := 20;
    WriteLn(x + y);
end.
```

<Output>
30
</Output>

Con la struttura al suo posto, prosegui con [Variabili e tipi](../language/variables-and-types).
