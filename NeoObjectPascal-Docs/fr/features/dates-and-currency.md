# Dates, heures et monnaie

NeoObjectPascal fournit quatre types intégrés pour gérer le monde réel : `Date`, `Time`, `DateTime` et `Currency`. Ils sont tous **toujours disponibles** — tout comme `Integer`, vous n'avez besoin d'aucun `uses` pour les utiliser.

- `Date` — une date de calendrier au format ISO `yyyy-MM-dd` (sous le capot, `java.time.LocalDate`).
- `Time` — une heure du jour `HH:mm:ss` (`java.time.LocalTime`).
- `DateTime` — date et heure combinées (`java.time.LocalDateTime`).
- `Currency` — des valeurs monétaires décimales **exactes** (`java.math.BigDecimal`).

De plus, `Double` et `Float` sont des **alias de `Real`** (virgule flottante 64 bits) : les trois mots nomment exactement le même type, alors choisissez celui qui rend votre code le plus clair.

::: tip Pourquoi `Currency` et non `Real` pour l'argent ?
`Real` (virgule flottante) souffre de l'erreur classique où `0.1 + 0.2` ne donne pas exactement `0.3`. `Currency` est décimal exact : `0.1 + 0.2` vaut précisément `0.3`. Utilisez `Currency` pour l'argent et `Real`/`Double` pour les calculs scientifiques ou d'usage général.
:::

## Les types de date et d'heure

### Créer des dates et des heures

Les fonctions de création se répartissent en deux groupes : celles qui capturent l'instant actuel et celles qui construisent une valeur à partir de nombres. Aucun `uses` n'est nécessaire pour l'une ou l'autre.

```npas
var hoje: Date;
var agora: DateTime;
var abertura: Time;
var natal: Date;

begin
    hoje := today();            // la date d'aujourd'hui
    agora := now();             // date et heure actuelles
    abertura := currentTime();  // l'heure actuelle

    natal := date(2026, 12, 25);
    WriteLn(natal);             // WriteLn imprime les dates en ISO
end.
```

<Output>
2026-12-25
</Output>

Vous construisez chaque type avec sa fonction dédiée : `date(annee, mois, jour)`, `time(heure, min, sec)` et `dateTime(annee, mois, jour, heure, min, sec)`. Pour partir d'un texte, utilisez les fonctions `parse` :

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

::: tip Coercition automatique à partir d'une String
Lorsque vous affectez une **String** au format ISO à une variable `Date`, `Time` ou `DateTime`, la valeur est convertie automatiquement — nul besoin d'appeler `parse` :

```npas
var feriado: Date;
feriado := "2026-12-25";   // la String devient Date
```
:::

### Composants

Extrayez les parties de n'importe quelle date ou heure avec des fonctions directes. `dayOfWeek` renvoie `1` pour lundi jusqu'à `7` pour dimanche.

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

### Comparaison

Les opérateurs `<`, `>`, `<=`, `>=`, `=` et `<>` fonctionnent directement sur `Date`, `Time` et `DateTime` — une date est « plus petite » qu'une autre lorsqu'elle lui est antérieure.

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

### Arithmétique

Ajoutez ou soustrayez des intervalles avec `addDays`, `addMonths`, `addYears`, `addHours` et `addMinutes` (utilisez des valeurs négatives pour remonter dans le temps). Pour mesurer la distance entre deux points, utilisez `daysBetween` et `hoursBetween`.

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

### Formatage

`format(valeur, motif)` renvoie une `String` dans le format que vous demandez, en utilisant les mêmes motifs que `java.time` (`dd`, `MM`, `yyyy`, `HH`, `mm`, `ss`). Cet exemple combine tout : âge en jours, arithmétique et formatage — dans l'esprit de `examples/54`.

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

## Argent exact avec `Currency`

`Currency` conserve les valeurs monétaires sans aucune erreur de virgule flottante. Créez une valeur avec `currency(...)`, à partir d'un nombre ou d'une chaîne.

### Arithmétique exacte

Les opérateurs `+`, `-`, `*` et `/` sont exacts sur `Currency`. Le cas classique qui échoue avec `Real` fonctionne parfaitement ici :

```npas
var a: Currency;
var b: Currency;

begin
    a := currency(0.1);
    b := currency(0.2);
    WriteLn(a + b);   // exact, sans erreur de virgule flottante
end.
```

<Output>
0.3
</Output>

La comparaison est également valable : `<`, `>`, `<=`, `>=`, `=` et `<>` fonctionnent sur `Currency`.

### Formatage et arrondi

`formatCurrency(valeur, symbole)` renvoie une `String` au style brésilien — le point comme séparateur de milliers et la virgule comme séparateur décimal. `roundCurrency(valeur, decimales)` arrondit au nombre de décimales souhaité.

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

### Exemple : calcul d'impôt

Un impôt de 2 % sur une valeur, dans l'esprit du calcul d'ITBI de `examples/55`. Comme tout est en `Currency`, le résultat est exact au centime près :

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

::: tip Choisissez le bon type
Utilisez `Currency` pour toute valeur en argent — prix, impôts, soldes — où le centime doit tomber juste. Réservez `Real`/`Double` aux mathématiques scientifiques et aux calculs d'usage général, où une petite imprécision de virgule flottante est acceptable.
:::

::: warning Impression par défaut
`WriteLn` imprime les dates en ISO (`2026-12-25`) et les valeurs `Currency` sous forme de décimal simple (`10400.00`). Pour une sortie au format de votre pays, utilisez toujours `format` et `formatCurrency`.
:::

---

Ensuite, assurez la qualité de votre code avec [Tests unitaires](../testing/unit-testing).
