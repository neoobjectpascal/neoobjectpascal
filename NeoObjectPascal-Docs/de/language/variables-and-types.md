# Variablen und Typen

Jede Variable in NeoObjectPascal hat einen deklarierten **Typ**. Die Deklaration verwendet das Schlüsselwort `var`, gefolgt vom Namen, einem Doppelpunkt und dem Typ:

```npas
var idade: Integer;
var nome: String;
```

Die Wertzuweisung erfolgt mit dem Operator `:=` (nicht zu verwechseln mit `=`, dem Gleichheitsoperator):

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

## Wo deklarieren

Sie können Variablen an zwei Stellen deklarieren:

- **Am Anfang der Datei**, vor dem Hauptblock.
- **Innerhalb eines Blocks** `begin ... end`, als gewöhnliche Anweisung.

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

## Die zehn Typen

NeoObjectPascal hat zehn eingebaute Typen:

| Typ       | Beschreibung                                          | Literal-Beispiele            |
| --------- | ----------------------------------------------------- | ---------------------------- |
| `Integer` | Ganze Zahlen                                          | `0`, `42`, `-7`              |
| `Real`    | Gleitkommazahlen (doppelte Genauigkeit)               | `3.14159`, `5.0`, `-0.5`     |
| `String`  | Text                                                  | `"olá"`, `'mundo'`           |
| `Boolean` | Logischer Wert                                        | `true`, `false`              |
| `Array`   | Geordnete Sammlung von Werten                         | `[1, 2, 3]`, `["a", "b"]`    |
| `Object`  | Generischer Typ (Instanzen, JSON/CSV, diverse Werte)  | `new Pessoa("Ana")`          |
| `Date`    | Kalenderdatum (ISO `yyyy-MM-dd`)                     | `today()`, `date(2026,12,25)`|
| `Time`    | Uhrzeit (`HH:mm:ss`)                                 | `currentTime()`, `time(10,30,0)`|
| `DateTime`| Kombiniertes Datum und Uhrzeit                        | `now()`                      |
| `Currency`| Exakter Dezimalbetrag (kein Gleitkommafehler)         | `currency(199.90)`           |

Darüber hinaus sind `Double` und `Float` **Aliase von `Real`** (64-Bit-Gleitkomma) — verwenden Sie den Namen, den Sie bevorzugen. Die Typen `Date`, `Time`, `DateTime` und `Currency` werden ausführlich unter [Datum, Zeit & Währung](../features/dates-and-currency) behandelt.

## Literale und Details der einzelnen Typen

### Integer und Real

Ganze Zahlen werden direkt geschrieben: `10`, `-3`, `0`. `Real`-Zahlen benötigen Ziffern **auf beiden Seiten** des Dezimalpunkts:

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

::: warning Real benötigt vollständige Ziffern
Schreiben Sie `5.0`, niemals `5.` — die zweite Form ist ungültig. Ebenso verwenden Sie `0.5` und nicht `.5`.
:::

Wenn Sie `Integer` und `Real` in einem Ausdruck mischen, wird die ganze Zahl **automatisch** zu `Real` **hochgestuft**. Dasselbe geschieht bei der Zuweisung einer ganzen Zahl an eine `Real`-Variable:

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

Strings können **doppelte Anführungszeichen** oder **einfache Anführungszeichen** verwenden — beide funktionieren gleich:

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

Der Operator `+` **verkettet** Strings. Wenn eine der beiden Seiten ein `String` ist, verbindet `+` die Werte, anstatt sie zu addieren:

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

Ein `Boolean` speichert `true` oder `false` (auch in Großschreibung akzeptiert, wie `True` oder `TRUE`):

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

`Object` ist der generische Typ der Sprache. Er speichert Klasseninstanzen, Ergebnisse von `JSON.parse`/`CSV.parse` und jeden Wert, dessen spezifischen Typ Sie nicht benennen müssen:

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

::: tip Typwahl
Bevorzugen Sie den spezifischsten möglichen Typ (`Integer`, `String`, usw.) und reservieren Sie `Object` für Instanzen und dynamische Daten.
:::

Nachdem Sie nun wissen, wie man Variablen deklariert, sehen Sie, wie Sie sie in [Operatoren](./operators) kombinieren.
