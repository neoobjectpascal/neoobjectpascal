# Date, orari e valuta

NeoObjectPascal offre quattro tipi integrati per gestire il mondo reale: `Date`, `Time`, `DateTime` e `Currency`. Sono tutti **sempre disponibili** — proprio come `Integer`, non ti serve alcun `uses` per usarli.

- `Date` — una data di calendario nel formato ISO `yyyy-MM-dd` (internamente, `java.time.LocalDate`).
- `Time` — un orario del giorno `HH:mm:ss` (`java.time.LocalTime`).
- `DateTime` — data e ora combinate (`java.time.LocalDateTime`).
- `Currency` — valori monetari decimali **esatti** (`java.math.BigDecimal`).

Inoltre, `Double` e `Float` sono **alias di `Real`** (virgola mobile a 64 bit): le tre parole indicano esattamente lo stesso tipo, quindi scegli quella che rende più chiaro il tuo codice.

::: tip Perché `Currency` e non `Real` per il denaro?
`Real` (virgola mobile) soffre del classico errore per cui `0.1 + 0.2` non dà esattamente `0.3`. `Currency` è decimale esatto: `0.1 + 0.2` è precisamente `0.3`. Usa `Currency` per il denaro e `Real`/`Double` per i calcoli scientifici o di uso generale.
:::

## I tipi di data e ora

### Creare date e orari

Le funzioni di creazione si dividono in due gruppi: quelle che catturano l'istante corrente e quelle che compongono un valore a partire da numeri. Non serve alcun `uses` per nessuna di esse.

```npas
var hoje: Date;
var agora: DateTime;
var abertura: Time;
var natal: Date;

begin
    hoje := today();            // la data di oggi
    agora := now();             // data e ora correnti
    abertura := currentTime();  // l'ora corrente

    natal := date(2026, 12, 25);
    WriteLn(natal);             // WriteLn stampa le date in ISO
end.
```

<Output>
2026-12-25
</Output>

Componi ogni tipo con la sua funzione dedicata: `date(anno, mese, giorno)`, `time(ora, min, sec)` e `dateTime(anno, mese, giorno, ora, min, sec)`. Per partire da testo, usa le funzioni `parse`:

```npas
var d: Date;
var t: Time;
var dt: DateTime;

begin
    d := parseDate("2026-01-15");
    t := parseTime("10:30");
    dt := parseDateTime("2026-01-15 10:30:00");
    WriteLn(d);
    WriteLn(t);
end.
```

<Output>
2026-01-15
10:30:00
</Output>

::: tip Coercizione automatica da String
Assegnando una **String** nel formato ISO a una variabile `Date`, `Time` o `DateTime`, il valore viene convertito automaticamente — non serve chiamare `parse`:

```npas
var feriado: Date;
feriado := "2026-12-25";   // la String diventa Date
```
:::

### Componenti

Estrai le parti di una qualsiasi data o ora con funzioni dirette. `dayOfWeek` restituisce `1` per lunedì fino a `7` per domenica.

```npas
var dt: DateTime;

begin
    dt := dateTime(2026, 7, 18, 14, 30, 0);
    WriteLn("Ano: ", year(dt));
    WriteLn("Mês: ", month(dt));
    WriteLn("Dia: ", day(dt));
    WriteLn("Hora: ", hour(dt));
    WriteLn("Minuto: ", minute(dt));
    WriteLn("Dia da semana (1=segunda): ", dayOfWeek(dt));
end.
```

<Output>
Ano: 2026
Mês: 7
Dia: 18
Hora: 14
Minuto: 30
Dia da semana (1=segunda): 6
</Output>

### Confronto

Gli operatori `<`, `>`, `<=`, `>=`, `=` e `<>` funzionano direttamente su `Date`, `Time` e `DateTime` — una data è "minore" di un'altra quando è precedente.

```npas
var vencimento: Date;
var hoje: Date;

begin
    vencimento := date(2026, 7, 10);
    hoje := date(2026, 7, 18);

    if hoje > vencimento then
        WriteLn("Prazo vencido!")
    else
        WriteLn("Ainda dentro do prazo");
end.
```

<Output>
Prazo vencido!
</Output>

### Aritmetica

Aggiungi o sottrai intervalli con `addDays`, `addMonths`, `addYears`, `addHours` e `addMinutes` (usa valori negativi per tornare indietro nel tempo). Per misurare la distanza tra due punti, usa `daysBetween` e `hoursBetween`.

```npas
var inicio: Date;

begin
    inicio := date(2026, 7, 18);
    WriteLn("Daqui a 30 dias: ", addDays(inicio, 30));
    WriteLn("No mês que vem: ", addMonths(inicio, 1));
    WriteLn("Dias até o fim do ano: ", daysBetween(inicio, date(2026, 12, 31)));
end.
```

<Output>
Daqui a 30 dias: 2026-08-17
No mês que vem: 2026-08-18
Dias até o fim do ano: 166
</Output>

### Formattazione

`format(valore, pattern)` restituisce una `String` nel formato che richiedi, usando gli stessi pattern di `java.time` (`dd`, `MM`, `yyyy`, `HH`, `mm`, `ss`). Questo esempio combina tutto: età in giorni, aritmetica e formattazione — nello spirito di `examples/54`.

```npas
var nascimento: Date;
var referencia: Date;

begin
    nascimento := date(1990, 5, 20);
    referencia := date(2026, 7, 18);

    WriteLn("Nascimento: ", format(nascimento, "dd/MM/yyyy"));
    WriteLn("Idade em dias: ", daysBetween(nascimento, referencia));
    WriteLn("Daqui a 30 dias: ", format(addDays(referencia, 30), "dd/MM/yyyy"));
    WriteLn("Dia da semana (1=segunda): ", dayOfWeek(referencia));
end.
```

<Output>
Nascimento: 20/05/1990
Idade em dias: 13208
Daqui a 30 dias: 17/08/2026
Dia da semana (1=segunda): 6
</Output>

## Denaro esatto con `Currency`

`Currency` conserva i valori monetari senza alcun errore di virgola mobile. Crea un valore con `currency(...)`, a partire da un numero o da una stringa.

### Aritmetica esatta

Gli operatori `+`, `-`, `*` e `/` sono esatti su `Currency`. Il caso classico che fallisce con `Real` qui funziona perfettamente:

```npas
var a: Currency;
var b: Currency;

begin
    a := currency(0.1);
    b := currency(0.2);
    WriteLn(a + b);   // esatto, senza errore di virgola mobile
end.
```

<Output>
0.3
</Output>

Vale anche per il confronto: `<`, `>`, `<=`, `>=`, `=` e `<>` funzionano su `Currency`.

### Formattare e arrotondare

`formatCurrency(valore, simbolo)` restituisce una `String` in stile brasiliano — il punto come separatore delle migliaia e la virgola come decimale. `roundCurrency(valore, cifre)` arrotonda al numero di cifre desiderato.

```npas
var preco: Currency;

begin
    preco := currency(1234.56);
    WriteLn(formatCurrency(preco, "R$"));
end.
```

<Output>
R$ 1.234,56
</Output>

### Esempio: calcolo di un'imposta

Un'imposta del 2% su un valore, nello spirito del calcolo dell'ITBI di `examples/55`. Poiché è tutto `Currency`, il risultato è esatto fino al centesimo:

```npas
var baseCalculo: Currency;
var aliquota: Real;
var imposto: Currency;

begin
    baseCalculo := currency(520000.00);
    aliquota := 0.02;   // 2%
    imposto := baseCalculo * currency(aliquota);

    WriteLn("Base de cálculo: ", formatCurrency(baseCalculo, "R$"));
    WriteLn("Alíquota:        2%");
    WriteLn("Imposto devido:  ", formatCurrency(imposto, "R$"));
end.
```

<Output>
Base de cálculo: R$ 520.000,00
Alíquota:        2%
Imposto devido:  R$ 10.400,00
</Output>

::: tip Scegli il tipo giusto
Usa `Currency` per qualsiasi valore in denaro — prezzi, imposte, saldi — dove il centesimo deve tornare esattamente. Riserva `Real`/`Double` alla matematica scientifica e ai calcoli di uso generale, dove una piccola imprecisione di virgola mobile è accettabile.
:::

::: warning Stampa predefinita
`WriteLn` stampa le date in ISO (`2026-12-25`) e i valori `Currency` come un semplice decimale (`10400.00`). Per un output nel formato del tuo Paese, usa sempre `format` e `formatCurrency`.
:::

---

Di seguito, garantisci la qualità del tuo codice con i [Test di unità](../testing/unit-testing).
