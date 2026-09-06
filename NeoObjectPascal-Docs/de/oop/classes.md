# Klassen und Objekte

Die **Objektorientierung** ist eine der Säulen von NeoObjectPascal. Eine **Klasse** ist eine Vorlage, die Daten (Felder) und Verhalten (Methoden) zusammenfasst; ein **Objekt** ist eine konkrete Instanz dieser Vorlage, die mit `new` erstellt wird.

## Eine Klasse deklarieren

Eine Klasse beginnt mit dem Schlüsselwort `class`, gefolgt vom Namen, ihren Mitgliedern und dem Abschluss `end;`. Jedes Mitglied (Feld, Konstruktor, Methode) endet mit `;`.

```npas
class Pessoa
    var nome: String;
    var idade: Integer;

    constructor Create(n: String, i: Integer)
    begin
        self.nome := n;
        self.idade := i;
    end;

    public function apresentar(): String
    begin
        return "Olá, meu nome é " + self.nome + " e tenho " + self.idade + " anos.";
    end;
end;
```

::: info Namenskonvention
Klassennamen verwenden **PascalCase** (`Pessoa`, `ContaBancaria`). Es ist dieselbe Konvention, die bei Schnittstellen verwendet wird.
:::

## Felder

Die Felder speichern den **Zustand** jedes Objekts. Sie werden mit `var nome: Tipo;` deklariert, genau wie eine gewöhnliche Variable, aber innerhalb des Klassenrumpfes:

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;
end;
```

::: warning Felder sind standardmäßig privat
Jedes Feld ist standardmäßig **privat** — die Absicht ist, dass es nur von den Methoden der eigenen Klasse aus zugänglich ist. Legen Sie den Zustand über öffentliche Methoden offen, anstatt das Feld direkt zugänglich zu lassen.
:::

## Der Konstruktor `Create`

Der **Konstruktor** initialisiert ein neues Objekt. Per Konvention heißt er `Create`, ist öffentlich und erhält die Anfangswerte der Felder:

```npas
constructor Create(saldoInicial: Real)
begin
    self.saldo := saldoInicial;
end;
```

## `self` — die aktuelle Instanz

Innerhalb jeder Methode oder des Konstruktors bezieht sich `self` auf das **aktuelle Objekt**. Verwenden Sie `self.campo`, um ein Feld zu lesen oder zuzuweisen und es von einem Parameter mit demselben Namen zu unterscheiden:

```npas
constructor Create(nome: String)
begin
    self.nome := nome;   // self.nome é o campo; nome é o parâmetro
end;
```

## Methoden: Funktionen und Prozeduren

Das Verhalten der Klasse wird durch **Funktionen** (geben mit `return` einen Wert zurück) und **Prozeduren** (geben nichts zurück) definiert. Beide können durch Kommas getrennte Parameter erhalten:

```npas
public function saldoAtual(): Real
begin
    return self.saldo;
end;

public procedure depositar(valor: Real)
begin
    self.saldo := self.saldo + valor;
end;
```

## Sichtbarkeit: `public`, `private`, `protected`

Die Sichtbarkeitsmodifizierer dokumentieren die **Zugriffsabsicht** jedes Mitglieds:

- `public` — Teil der öffentlichen Schnittstelle; kann von außerhalb des Objekts aufgerufen werden.
- `private` — internes Detail, gedacht nur zur Verwendung innerhalb der Klasse.
- `protected` — intern, aber mit Unterklassen geteilt.

```npas
public function saldoAtual(): Real
begin
    return self.saldo;
end;

private procedure registrarLog(mensagem: String)
begin
    WriteLn("[LOG] " + mensagem);
end;
```

::: info Sichtbarkeit als Absicht
Die Modifizierer werden von der Sprache erkannt und kommunizieren den Vertrag der Klasse. Behandeln Sie sie als Design-Dokumentation: private Felder und Methoden beschreiben interne Details, von denen andere Teile des Codes nicht abhängen sollten.
:::

## Instanziieren mit `new`

Ein Objekt wird mit `new NomeDaClasse(argumentos)` erstellt, wobei die vom Konstruktor erwarteten Werte übergeben werden. Variablen, die Objekte speichern, werden mit dem Klassentyp oder mit dem generischen Typ `Object` deklariert:

```npas
var conta: ContaBancaria;

begin
    conta := new ContaBancaria(100.0);
end.
```

## Zugriff auf Felder und Methoden

Verwenden Sie den Punkt, um Methoden aufzurufen und (wenn erlaubt) Felder eines Objekts zu lesen: `obj.metodo(args)` und `obj.campo`.

```npas
conta.depositar(50.0);
WriteLn(conta.saldoAtual());
```

## Vollständiges Beispiel: Bankkonto

Das folgende Beispiel vereint private Felder, Konstruktor, öffentliche Methoden, `self` und Instanziierung:

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;

    constructor Create(titular: String, saldoInicial: Real)
    begin
        self.titular := titular;
        self.saldo := saldoInicial;
    end;

    public procedure depositar(valor: Real)
    begin
        self.saldo := self.saldo + valor;
    end;

    public function sacar(valor: Real): Boolean
    begin
        if valor > self.saldo then
            return false;
        self.saldo := self.saldo - valor;
        return true;
    end;

    public function extrato(): String
    begin
        return "Titular: " + self.titular + " | Saldo: " + self.saldo;
    end;
end;

var conta: ContaBancaria;

begin
    conta := new ContaBancaria("Ana", 100.0);
    conta.depositar(50.0);

    if conta.sacar(30.0) then
        WriteLn("Saque realizado.")
    else
        WriteLn("Saldo insuficiente.");

    WriteLn(conta.extrato());
end.
```

<Output>
Saque realizado.
Titular: Ana | Saldo: 120.0
</Output>

::: tip Objekte als `Object`
Sie können die Variable auch als `var conta: Object;` deklarieren. Der Typ `Object` ist generisch und akzeptiert jede Instanz — nützlich, wenn derselbe Code mit Objekten verschiedener Klassen arbeiten muss.
:::

Mit einer Klasse an Ort und Stelle besteht der nächste Schritt darin, Verhalten wiederzuverwenden und zu spezialisieren. Weiter zu [Vererbung und Polymorphie](./inheritance-polymorphism).
