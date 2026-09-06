# Moduli e uses

Man mano che un programma cresce, conviene suddividerlo in file più piccoli e riutilizzabili. In NeoObjectPascal questo si fa con la clausola `uses`, che carica sia le **librerie interne** sia i **moduli in file** che scrivi tu stesso.

## La clausola `uses`

La clausola `uses` compare all'**inizio** del programma, prima di qualsiasi dichiarazione. Elenca i moduli da caricare, separati da virgola, e termina con `;`:

```npas
uses internal.math, internal.string;

begin
    WriteLn("Máximo: ", max(10, 20));
    WriteLn("Maiúsculas: ", toUpperCase("neo"));
end.
```

<Output>
Máximo: 20
Maiúsculas: NEO
</Output>

Dopo `uses`, tutte le funzioni dei moduli sono disponibili come se fossero state dichiarate nel tuo stesso file.

## Librerie interne vs. moduli in file

Esistono due tipi di moduli, distinti dal prefisso:

| Forma                  | Origine                                            | Esempio                    |
| ---------------------- | ------------------------------------------------- | -------------------------- |
| `internal.<nome>`      | Librerie incluse nell'interprete stesso           | `uses internal.datetime;`  |
| `<cartella>.<modulo>`  | File `.npas` relativo al tuo programma            | `uses utils.matematica;`   |

Il prefisso `internal.` è riservato: il runtime lo riconosce e carica la libreria dall'interno del JAR. Qualsiasi altro percorso è trattato come un file nel tuo progetto.

## Creare un modulo in file

Un modulo è semplicemente un file `.npas` con dichiarazioni di funzioni, procedure o variabili. Ad esempio, crea `matematica.npas`:

```npas
// matematica.npas
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

function subtrair(a: Integer, b: Integer): Integer
begin
    return a - b;
end;
```

E usalo nel programma principale, nella stessa directory:

```npas
// programa.npas
uses matematica;

begin
    WriteLn(somar(10, 5));
    WriteLn(subtrair(10, 5));
end.
```

<Output>
15
5
</Output>

L'interprete cerca `matematica.npas` a partire dalla directory del file principale.

::: warning Il separatore dei parametri è la virgola
Quando dichiari parametri tipizzati, separali con la **virgola**: `function somar(a: Integer, b: Integer)`. Non usare il punto e virgola tra i parametri.
:::

## Moduli gerarchici

Puoi organizzare i moduli in sottocartelle usando il punto come separatore di percorso. `uses utils.matematica;` carica il file `utils/matematica.npas`, relativo al programma principale:

```text
projeto/
├── programa.npas
└── utils/
    └── matematica.npas
```

```npas
// programa.npas
uses utils.matematica;

begin
    WriteLn(somar(2, 3));
end.
```

<Output>
5
</Output>

## Caricare più moduli

Un'unica clausola `uses` può combinare librerie interne e moduli propri, tutti separati da virgola:

```npas
uses internal.math, internal.string, utils.matematica;

begin
    WriteLn(square(4));              // de internal.math
    WriteLn(quote("olá"));          // de internal.string
    WriteLn(somar(1, 1));           // do seu módulo utils.matematica
end.
```

<Output>
16
"olá"
2
</Output>

## Buone pratiche

- Raggruppa le funzioni correlate in uno stesso modulo (ad esempio, tutto ciò che riguarda il testo in un `texto.npas`).
- Preferisci molti file piccoli e coesi a un unico file gigante.
- Usa sottocartelle (`utils.`, `dominio.`) per organizzare i moduli per area del sistema.
- C'è una sola clausola `uses` per programma: elenca tutti i moduli al suo interno.

---

Di seguito, scopri in dettaglio ciascuna delle [Librerie interne](./internal-libraries).
