# Variabili e tipi

Ogni variabile in NeoObjectPascal ha un **tipo** dichiarato. La dichiarazione usa la parola chiave `var`, seguita dal nome, dai due punti e dal tipo:

```npas
var idade: Integer;
var nome: String;
```

L'assegnazione dei valori avviene con l'operatore `:=` (da non confondere con `=`, che è l'operatore di uguaglianza):

```npas
begin
    var idade: Integer;
    idade := 30;
    WriteLn("Idade: ", idade);
end.
```

<Output>
Idade: 30
</Output>

## Dove dichiarare

Puoi dichiarare le variabili in due punti:

- **All'inizio del file**, prima del blocco principale.
- **All'interno di un blocco** `begin ... end`, come una normale istruzione.

```npas
var total: Integer;   // declaração no topo

begin
    total := 100;
    var imposto: Integer;   // declaração dentro do bloco
    imposto := total / 10;
    WriteLn("Imposto: ", imposto);
end.
```

<Output>
Imposto: 10
</Output>

## I dieci tipi

NeoObjectPascal dispone di dieci tipi integrati:

| Tipo      | Descrizione                                            | Esempi di letterale          |
| --------- | ----------------------------------------------------- | ---------------------------- |
| `Integer` | Numeri interi                                         | `0`, `42`, `-7`              |
| `Real`    | Numeri in virgola mobile (doppia precisione)          | `3.14159`, `5.0`, `-0.5`     |
| `String`  | Testo                                                 | `"olá"`, `'mundo'`           |
| `Boolean` | Valore logico                                         | `true`, `false`              |
| `Array`   | Collezione ordinata di valori                         | `[1, 2, 3]`, `["a", "b"]`    |
| `Object`  | Tipo generico (istanze, JSON/CSV, valori vari)        | `new Pessoa("Ana")`          |
| `Date`    | Data di calendario (ISO `yyyy-MM-dd`)                | `today()`, `date(2026,12,25)`|
| `Time`    | Ora del giorno (`HH:mm:ss`)                          | `currentTime()`, `time(10,30,0)`|
| `DateTime`| Data e ora combinate                                  | `now()`                      |
| `Currency`| Importo decimale esatto (nessun errore di virgola mobile)| `currency(199.90)`        |

Oltre a questi, `Double` e `Float` sono **alias di `Real`** (virgola mobile a 64 bit) — usa il nome che preferisci. I tipi `Date`, `Time`, `DateTime` e `Currency` sono descritti in dettaglio in [Date, ore e valuta](../features/dates-and-currency).

## Letterali e dettagli di ciascun tipo

### Integer e Real

Gli interi si scrivono direttamente: `10`, `-3`, `0`. I numeri `Real` richiedono cifre **da entrambi i lati** del punto decimale:

```npas
begin
    var meio: Real;
    meio := 0.5;    // correto
    WriteLn(meio);
end.
```

<Output>
0.5
</Output>

::: warning Real richiede cifre complete
Scrivi `5.0`, mai `5.` — la seconda forma non è valida. Allo stesso modo, usa `0.5` e non `.5`.
:::

Quando mescoli `Integer` e `Real` in un'espressione, l'intero viene **promosso automaticamente** a `Real`. Lo stesso avviene quando assegni un intero a una variabile `Real`:

```npas
begin
    var x: Real;
    x := 5;          // 5 vira 5.0
    var y: Real;
    y := 2 * 3.5;    // resultado Real
    WriteLn("x = ", x);
    WriteLn("y = ", y);
end.
```

<Output>
x = 5.0
y = 7.0
</Output>

### String

Le stringhe possono usare **virgolette doppie** o **virgolette singole** — entrambe funzionano allo stesso modo:

```npas
begin
    var a: String;
    var b: String;
    a := "aspas duplas";
    b := 'aspas simples';
    WriteLn(a);
    WriteLn(b);
end.
```

<Output>
aspas duplas
aspas simples
</Output>

L'operatore `+` **concatena** le stringhe. Se uno dei due lati è una `String`, il `+` unisce i valori invece di sommarli:

```npas
begin
    var nome: String;
    nome := "Mundo";
    WriteLn("Olá, " + nome + "!");
end.
```

<Output>
Olá, Mundo!
</Output>

### Boolean

Un `Boolean` contiene `true` o `false` (accettati anche in maiuscolo, come `True` o `TRUE`):

```npas
begin
    var ativo: Boolean;
    ativo := true;
    WriteLn("Ativo: ", ativo);
end.
```

<Output>
Ativo: true
</Output>

### Object

`Object` è il tipo generico del linguaggio. Contiene istanze di classi, risultati di `JSON.parse`/`CSV.parse` e qualsiasi valore il cui tipo specifico non hai bisogno di nominare:

```npas
class Pessoa
    var nome: String;

    constructor Create(n: String)
    begin
        self.nome := n;
    end;

    public function saudar(): String
    begin
        return "Sou " + self.nome;
    end;
end;

var p: Object;

begin
    p := new Pessoa("Alice");
    WriteLn(p.saudar());
end.
```

<Output>
Sou Alice
</Output>

::: tip Scelta del tipo
Preferisci il tipo più specifico possibile (`Integer`, `String`, ecc.) e riserva `Object` alle istanze e ai dati dinamici.
:::

Ora che sai dichiarare le variabili, scopri come combinarle in [Operatori](./operators).
