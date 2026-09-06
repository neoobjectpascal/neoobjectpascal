# Datumsangaben, Uhrzeiten und Währung

NeoObjectPascal bringt vier eingebaute Typen mit, um mit der realen Welt umzugehen: `Date`, `Time`, `DateTime` und `Currency`. Alle sind **immer verfügbar** — genau wie `Integer` benötigen Sie kein `uses`, um sie zu nutzen.

- `Date` — ein Kalenderdatum im ISO-Format `yyyy-MM-dd` (intern `java.time.LocalDate`).
- `Time` — eine Tageszeit `HH:mm:ss` (`java.time.LocalTime`).
- `DateTime` — Datum und Uhrzeit kombiniert (`java.time.LocalDateTime`).
- `Currency` — **exakte** dezimale Geldbeträge (`java.math.BigDecimal`).

Darüber hinaus sind `Double` und `Float` **Aliase für `Real`** (64-Bit-Gleitkomma): Alle drei Wörter bezeichnen genau denselben Typ, wählen Sie also das, was in Ihrem Code am klarsten ist.

::: tip Warum `Currency` und nicht `Real` für Geld?
`Real` (Gleitkomma) leidet unter dem klassischen Fehler, bei dem `0.1 + 0.2` nicht exakt `0.3` ergibt. `Currency` ist exakt dezimal: `0.1 + 0.2` ist genau `0.3`. Verwenden Sie `Currency` für Geld und `Real`/`Double` für wissenschaftliche oder allgemeine Berechnungen.
:::

## Die Datums- und Uhrzeittypen

### Datumsangaben und Uhrzeiten erzeugen

Die Erzeugungsfunktionen kommen in zwei Gruppen: die, die den aktuellen Zeitpunkt erfassen, und die, die einen Wert aus Zahlen zusammensetzen. Für keine davon ist ein `uses` nötig.

```npas
var hoje: Date;
var agora: DateTime;
var abertura: Time;
var natal: Date;

begin
    hoje := today();            // das heutige Datum
    agora := now();             // aktuelles Datum und Uhrzeit
    abertura := currentTime();  // die aktuelle Uhrzeit

    natal := date(2026, 12, 25);
    WriteLn(natal);             // WriteLn gibt Datumsangaben in ISO aus
end.
```

<Output>
2026-12-25
</Output>

Sie setzen jeden Typ mit seiner eigenen Funktion zusammen: `date(jahr, monat, tag)`, `time(stunde, min, sek)` und `dateTime(jahr, monat, tag, stunde, min, sek)`. Um von Text auszugehen, verwenden Sie die `parse`-Funktionen:

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

::: tip Automatische Umwandlung aus String
Wenn Sie einer `Date`-, `Time`- oder `DateTime`-Variable eine **String** im ISO-Format zuweisen, wird der Wert automatisch umgewandelt — ein Aufruf von `parse` ist nicht nötig:

```npas
var feriado: Date;
feriado := "2026-12-25";   // String wird zu Date
```
:::

### Komponenten

Extrahieren Sie Bestandteile eines beliebigen Datums oder einer Uhrzeit mit direkten Funktionen. `dayOfWeek` liefert `1` für Montag bis `7` für Sonntag.

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

### Vergleich

Die Operatoren `<`, `>`, `<=`, `>=`, `=` und `<>` funktionieren direkt auf `Date`, `Time` und `DateTime` — ein Datum ist "kleiner" als ein anderes, wenn es davor liegt.

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

### Arithmetik

Addieren oder subtrahieren Sie Intervalle mit `addDays`, `addMonths`, `addYears`, `addHours` und `addMinutes` (verwenden Sie negative Werte, um in der Zeit zurückzugehen). Um den Abstand zwischen zwei Punkten zu messen, nutzen Sie `daysBetween` und `hoursBetween`.

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

### Formatierung

`format(wert, muster)` liefert eine `String` in dem von Ihnen gewünschten Format und verwendet dieselben Muster wie `java.time` (`dd`, `MM`, `yyyy`, `HH`, `mm`, `ss`). Dieses Beispiel vereint alles: Alter in Tagen, Arithmetik und Formatierung — im Geiste von `examples/54`.

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

## Exaktes Geld mit `Currency`

`Currency` speichert Geldbeträge ganz ohne Gleitkommafehler. Erzeugen Sie einen Wert mit `currency(...)`, aus einer Zahl oder aus einer Zeichenkette.

### Exakte Arithmetik

Die Operatoren `+`, `-`, `*` und `/` sind auf `Currency` exakt. Der klassische Fall, der mit `Real` fehlschlägt, funktioniert hier einwandfrei:

```npas
var a: Currency;
var b: Currency;

begin
    a := currency(0.1);
    b := currency(0.2);
    WriteLn(a + b);   // exakt, ohne Gleitkommafehler
end.
```

<Output>
0.3
</Output>

Ebenso gilt der Vergleich: `<`, `>`, `<=`, `>=`, `=` und `<>` funktionieren auf `Currency`.

### Formatieren und Runden

`formatCurrency(wert, symbol)` liefert eine `String` im brasilianischen Stil — Punkt als Tausendertrennzeichen und Komma als Dezimaltrennzeichen. `roundCurrency(wert, stellen)` rundet auf die gewünschte Anzahl von Stellen.

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

### Beispiel: Steuerberechnung

Eine Steuer von 2 % auf einen Betrag, im Geiste der ITBI-Berechnung aus `examples/55`. Da alles `Currency` ist, ist das Ergebnis auf den Cent genau:

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

::: tip Wählen Sie den richtigen Typ
Verwenden Sie `Currency` für jeden Geldbetrag — Preise, Steuern, Salden —, bei dem der Cent exakt aufgehen muss. Reservieren Sie `Real`/`Double` für wissenschaftliche Mathematik und allgemeine Berechnungen, bei denen eine kleine Gleitkomma-Ungenauigkeit akzeptabel ist.
:::

::: warning Standardausgabe
`WriteLn` gibt Datumsangaben in ISO aus (`2026-12-25`) und `Currency`-Werte als einfache Dezimalzahl (`10400.00`). Für eine Ausgabe im Format Ihres Landes verwenden Sie stets `format` und `formatCurrency`.
:::

---

Stellen Sie als Nächstes die Qualität Ihres Codes mit [Unit-Tests](../testing/unit-testing) sicher.
