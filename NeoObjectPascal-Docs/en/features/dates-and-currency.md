# Dates, times, and currency

NeoObjectPascal ships four built-in types for dealing with the real world: `Date`, `Time`, `DateTime`, and `Currency`. All of them are **always available** — just like `Integer`, you don't need any `uses` to work with them.

- `Date` — a calendar date in ISO format `yyyy-MM-dd` (backed by `java.time.LocalDate`).
- `Time` — a time of day `HH:mm:ss` (`java.time.LocalTime`).
- `DateTime` — combined date and time (`java.time.LocalDateTime`).
- `Currency` — **exact** decimal monetary values (`java.math.BigDecimal`).

In addition, `Double` and `Float` are **aliases of `Real`** (64-bit floating point): all three words name exactly the same type, so pick whichever reads more clearly in your code.

::: tip Why `Currency` and not `Real` for money?
`Real` (floating point) suffers from the classic error where `0.1 + 0.2` doesn't yield exactly `0.3`. `Currency` is exact decimal: `0.1 + 0.2` is precisely `0.3`. Use `Currency` for money and `Real`/`Double` for scientific or general-purpose calculations.
:::

## The date and time types

### Creating dates and times

The creation functions come in two groups: those that capture the current instant and those that build a value from numbers. None of them require `uses`.

```npas
var hoje: Date;
var agora: DateTime;
var abertura: Time;
var natal: Date;

begin
    hoje := today();            // today's date
    agora := now();             // current date and time
    abertura := currentTime();  // the current time

    natal := date(2026, 12, 25);
    WriteLn(natal);             // WriteLn prints dates in ISO
end.
```

<Output>
2026-12-25
</Output>

You build each type with its dedicated function: `date(year, month, day)`, `time(hour, min, sec)`, and `dateTime(year, month, day, hour, min, sec)`. To start from text, use the `parse` functions:

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

::: tip Automatic coercion from String
When you assign a **String** in ISO format to a `Date`, `Time`, or `DateTime` variable, the value is converted automatically — there's no need to call `parse`:

```npas
var feriado: Date;
feriado := "2026-12-25";   // String becomes Date
```
:::

### Components

Extract parts of any date or time with direct functions. `dayOfWeek` returns `1` for Monday through `7` for Sunday.

```npas
var dt: DateTime;

begin
    dt := dateTime(2026, 7, 18, 14, 30, 0);
    WriteLn("Year: ", year(dt));
    WriteLn("Month: ", month(dt));
    WriteLn("Day: ", day(dt));
    WriteLn("Hour: ", hour(dt));
    WriteLn("Minute: ", minute(dt));
    WriteLn("Day of week (1=Monday): ", dayOfWeek(dt));
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

### Comparison

The `<`, `>`, `<=`, `>=`, `=`, and `<>` operators work directly on `Date`, `Time`, and `DateTime` — one date is "less than" another when it comes earlier.

```npas
var vencimento: Date;
var hoje: Date;

begin
    vencimento := date(2026, 7, 10);
    hoje := date(2026, 7, 18);

    if hoje > vencimento then
        WriteLn("Deadline passed!")
    else
        WriteLn("Still within the deadline");
end.
```

<Output>
Prazo vencido!
</Output>

### Arithmetic

Add or subtract intervals with `addDays`, `addMonths`, `addYears`, `addHours`, and `addMinutes` (use negative values to go back in time). To measure the distance between two points, use `daysBetween` and `hoursBetween`.

```npas
var inicio: Date;

begin
    inicio := date(2026, 7, 18);
    WriteLn("30 days from now: ", addDays(inicio, 30));
    WriteLn("Next month: ", addMonths(inicio, 1));
    WriteLn("Days until year-end: ", daysBetween(inicio, date(2026, 12, 31)));
end.
```

<Output>
Daqui a 30 dias: 2026-08-17
No mês que vem: 2026-08-18
Dias até o fim do ano: 166
</Output>

### Formatting

`format(value, pattern)` returns a `String` in the format you ask for, using the same `java.time` patterns (`dd`, `MM`, `yyyy`, `HH`, `mm`, `ss`). This example combines everything: age in days, arithmetic, and formatting — in the spirit of `examples/54`.

```npas
var nascimento: Date;
var referencia: Date;

begin
    nascimento := date(1990, 5, 20);
    referencia := date(2026, 7, 18);

    WriteLn("Birth: ", format(nascimento, "dd/MM/yyyy"));
    WriteLn("Age in days: ", daysBetween(nascimento, referencia));
    WriteLn("30 days from now: ", format(addDays(referencia, 30), "dd/MM/yyyy"));
    WriteLn("Day of week (1=Monday): ", dayOfWeek(referencia));
end.
```

<Output>
Nascimento: 20/05/1990
Idade em dias: 13208
Daqui a 30 dias: 17/08/2026
Dia da semana (1=segunda): 6
</Output>

## Exact money with `Currency`

`Currency` holds monetary values without any floating-point error. Create a value with `currency(...)`, from a number or a string.

### Exact arithmetic

The `+`, `-`, `*`, and `/` operators are exact on `Currency`. The classic case that fails with `Real` works perfectly here:

```npas
var a: Currency;
var b: Currency;

begin
    a := currency(0.1);
    b := currency(0.2);
    WriteLn(a + b);   // exact, no floating-point error
end.
```

<Output>
0.3
</Output>

Comparison holds too: `<`, `>`, `<=`, `>=`, `=`, and `<>` work on `Currency`.

### Formatting and rounding

`formatCurrency(value, symbol)` returns a `String` in the Brazilian style — a dot as the thousands separator and a comma as the decimal. `roundCurrency(value, places)` rounds to the desired number of places.

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

### Example: tax calculation

A 2% tax on an amount, in the spirit of the ITBI calculation in `examples/55`. Since everything is `Currency`, the result is exact down to the cent:

```npas
var baseCalculo: Currency;
var aliquota: Real;
var imposto: Currency;

begin
    baseCalculo := currency(520000.00);
    aliquota := 0.02;   // 2%
    imposto := baseCalculo * currency(aliquota);

    WriteLn("Tax base:     ", formatCurrency(baseCalculo, "R$"));
    WriteLn("Rate:         2%");
    WriteLn("Tax due:      ", formatCurrency(imposto, "R$"));
end.
```

<Output>
Base de cálculo: R$ 520.000,00
Alíquota:        2%
Imposto devido:  R$ 10.400,00
</Output>

::: tip Choose the right type
Use `Currency` for any monetary value — prices, taxes, balances — where the cents have to add up exactly. Reserve `Real`/`Double` for scientific math and general-purpose calculations, where a small floating-point imprecision is acceptable.
:::

::: warning Default printing
`WriteLn` prints dates in ISO (`2026-12-25`) and `Currency` values as a plain decimal (`10400.00`). For output in your country's format, always use `format` and `formatCurrency`.
:::

---

Next, ensure the quality of your code with [Unit Testing](../testing/unit-testing).
