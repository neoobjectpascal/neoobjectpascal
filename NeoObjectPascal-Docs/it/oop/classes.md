# Classi e oggetti

La **programmazione orientata agli oggetti** è uno dei pilastri di NeoObjectPascal. Una **classe** è uno stampo che raggruppa dati (campi) e comportamento (metodi); un **oggetto** è un'istanza concreta di quello stampo, creata con `new`.

## Dichiarare una classe

Una classe inizia con la parola chiave `class`, seguita dal nome, dai suoi membri e dalla chiusura `end;`. Ogni membro (campo, costruttore, metodo) termina con `;`.

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

::: info Convenzione di denominazione
I nomi delle classi usano il **PascalCase** (`Pessoa`, `ContaBancaria`). È la stessa convenzione usata nelle interfacce.
:::

## Campi

I campi contengono lo **stato** di ciascun oggetto. Sono dichiarati con `var nome: Tipo;`, esattamente come una normale variabile, ma all'interno del corpo della classe:

```npas
class ContaBancaria
    var titular: String;
    var saldo: Real;
end;
```

::: warning I campi sono privati per impostazione predefinita
Ogni campo è **privato** per impostazione predefinita — l'intenzione è che venga acceduto solo dai metodi della classe stessa. Esponi lo stato attraverso metodi pubblici invece di lasciare il campo accessibile direttamente.
:::

## Il costruttore `Create`

Il **costruttore** inizializza un nuovo oggetto. Per convenzione si chiama `Create`, è pubblico e riceve i valori iniziali dei campi:

```npas
constructor Create(saldoInicial: Real)
begin
    self.saldo := saldoInicial;
end;
```

## `self` — l'istanza corrente

All'interno di qualsiasi metodo o del costruttore, `self` si riferisce all'**oggetto corrente**. Usa `self.campo` per leggere o assegnare un campo e distinguerlo da un parametro con lo stesso nome:

```npas
constructor Create(nome: String)
begin
    self.nome := nome;   // self.nome é o campo; nome é o parâmetro
end;
```

## Metodi: funzioni e procedure

Il comportamento della classe è definito da **funzioni** (restituiscono un valore con `return`) e **procedure** (non restituiscono nulla). Entrambe possono ricevere parametri separati da virgola:

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

## Visibilità: `public`, `private`, `protected`

I modificatori di visibilità documentano l'**intenzione** di accesso di ciascun membro:

- `public` — parte dell'interfaccia pubblica; può essere chiamato dall'esterno dell'oggetto.
- `private` — dettaglio interno, pensato per l'uso solo all'interno della classe.
- `protected` — interno, ma condiviso con le sottoclassi.

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

::: info La visibilità come intenzione
I modificatori sono riconosciuti dal linguaggio e comunicano il contratto della classe. Trattali come documentazione di progettazione: i campi e i metodi privati descrivono dettagli interni da cui altre parti del codice non dovrebbero dipendere.
:::

## Istanziare con `new`

Un oggetto viene creato con `new NomeDellaClasse(argomenti)`, passando i valori attesi dal costruttore. Le variabili che contengono oggetti sono dichiarate con il tipo della classe o con il tipo generico `Object`:

```npas
var conta: ContaBancaria;

begin
    conta := new ContaBancaria(100.0);
end.
```

## Accedere a campi e metodi

Usa il punto per chiamare i metodi e (quando consentito) leggere i campi di un oggetto: `obj.metodo(args)` e `obj.campo`.

```npas
conta.depositar(50.0);
WriteLn(conta.saldoAtual());
```

## Esempio completo: conto bancario

L'esempio seguente riunisce campi privati, costruttore, metodi pubblici, `self` e istanziazione:

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

::: tip Oggetti come `Object`
Puoi anche dichiarare la variabile come `var conta: Object;`. Il tipo `Object` è generico e accetta qualsiasi istanza — utile quando lo stesso codice deve lavorare con oggetti di classi diverse.
:::

Una volta pronta una classe, il passo successivo è riutilizzare e specializzare il comportamento. Prosegui verso [Ereditarietà e polimorfismo](./inheritance-polymorphism).
