# Funzioni e procedure

Le funzioni incapsulano logica riutilizzabile e **restituiscono un valore**. All'inizio del file, dichiari le funzioni con la parola chiave `function`.

## Dichiarare una funzione

La forma generale è `function nome(parametri): Tipo begin ... return expr; end;`. Nota il `;` finale dopo la dichiarazione a livello superiore:

```npas
function saudar(nome: String): String
begin
    return "Olá, " + nome + "!";
end;

begin
    WriteLn(saudar("Ana"));
end.
```

<Output>
Olá, Ana!
</Output>

Il tipo dopo i `:` è il tipo del valore restituito. L'istruzione `return expr;` restituisce quel valore e termina la funzione.

## Parametri

I parametri sono separati da **virgola**. Possono essere **tipizzati** o **non tipizzati**:

```npas
// parâmetros tipados
function somar(a: Integer, b: Integer): Integer
begin
    return a + b;
end;

// parâmetros sem tipo explícito
function juntar(x, y): String
begin
    return x + " " + y;
end;

begin
    WriteLn(somar(3, 4));
    WriteLn(juntar("bom", "dia"));
end.
```

<Output>
7
bom dia
</Output>

::: tip Tipizza i tuoi parametri
Dichiarare il tipo dei parametri rende chiara l'intenzione e aiuta l'interprete a validare i valori. Preferisci i parametri tipizzati ogni volta che è possibile.
:::

## Chiamare le funzioni

Chiama una funzione tramite il suo nome, passando gli argomenti tra parentesi. Se la funzione **non ha argomenti**, le parentesi sono opzionali:

```npas
function agora(): String
begin
    return "sempre agora";
end;

begin
    WriteLn(agora());   // com parênteses
    WriteLn(agora);     // sem parênteses — equivalente
end.
```

<Output>
sempre agora
sempre agora
</Output>

## Ricorsione

Una funzione può chiamare sé stessa. L'esempio classico è il fattoriale:

```npas
function fatorial(n: Integer): Integer
begin
    if n <= 1 then
        return 1;
    return n * fatorial(n - 1);
end;

begin
    WriteLn("5! = ", fatorial(5));
    WriteLn("6! = ", fatorial(6));
end.
```

<Output>
5! = 120
6! = 720
</Output>

::: warning Prevedi sempre un caso base
Ogni funzione ricorsiva necessita di una condizione di arresto (qui, `n <= 1`). Senza di essa, la ricorsione non termina mai.
:::

## E le procedure?

Una **procedura** è come una funzione, ma **non restituisce alcun valore** — serve a eseguire effetti (stampare, modificare lo stato). In NeoObjectPascal, le procedure esistono come **membri di classi**, dichiarate con la parola chiave `procedure`:

```npas
class Logger
    public procedure registrar(msg: String)
    begin
        WriteLn("[LOG] " + msg);
    end;
end;

var log: Object;

begin
    log := new Logger();
    log.registrar("iniciando");
end.
```

<Output>
[LOG] iniciando
</Output>

Per approfondire procedure, metodi e visibilità, vedi [Classi](../oop/classes).

Ora impara a lavorare con le collezioni in [Array](./arrays).
