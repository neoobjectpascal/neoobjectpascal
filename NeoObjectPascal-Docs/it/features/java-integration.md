# Integrazione Java

Poiché NeoObjectPascal viene interpretato sulla JVM, puoi **incorporare codice Java direttamente** nel tuo programma. Questo dà accesso all'intero ecosistema Java — librerie di date, matematica, testo e molto altro — senza uscire dalla sintassi Pascal.

## Sintassi di un blocco Java

Un blocco Java è un'espressione che produce un valore. La forma generale è:

```npas
java:(arg0, arg1) { <código Java que retorna um valor> }
```

- Tra parentesi vanno i valori NeoObjectPascal che vuoi rendere disponibili al blocco.
- All'interno delle graffe `{ }` va codice Java. Deve terminare con un `return`.
- Il valore restituito dal blocco diventa il valore dell'espressione in NeoObjectPascal.

::: info Le graffe sono di Java
In NeoObjectPascal, `{ }` delimita **esclusivamente** il corpo di un blocco Java. Non esistono commenti di blocco — i commenti sono solo `// riga singola`.
:::

## Accedere ai parametri: `param0`, `param1`, ...

All'interno del blocco, gli argomenti sono accessibili per **posizione**, con i nomi `param0`, `param1`, `param2` e così via. Poiché Java è tipizzato, è necessario eseguire il **cast** al tipo appropriato:

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        return "Olá, " + ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));
end.
```

<Output>
Olá, ADA
</Output>

L'argomento `nome` è stato consegnato al blocco come `param0` e convertito in `String` prima di chiamare `.toUpperCase()`.

## Accedere agli argomenti per nome

Quando un argomento di un blocco Java è un **identificatore semplice** — come in `java:(nome)` —, puoi farvi riferimento all'interno del blocco **con il suo nome**, e non solo come `param0`. Questo alias nominato è dichiarato con il tipo a runtime dell'argomento, quindi per `String`, `Integer`, `Boolean` e `Real` il cast diventa superfluo.

```npas
function saudar(nome: String): String
begin
    return java:(nome) {
        // 'nome' è già una String — nessun bisogno di (String)param0
        return "Olá, " + nome.toUpperCase();
    };
end;

begin
    WriteLn(saudar("ada"));   // Olá, ADA
end.
```

<Output>
Olá, ADA
</Output>

Confronta con il vecchio stile posizionale `((String)param0).toUpperCase()`: l'alias nominato elimina il cast e rende il corpo Java più leggibile.

`param0`, `param1`, ... restano disponibili (pienamente retrocompatibile) e sono il modo per accedere agli argomenti che **non** sono identificatori semplici — come `java:(a + b)`, un letterale o una chiamata di funzione —, poiché questi non hanno un nome.

::: warning Parole riservate e collisioni
Se il nome dell'argomento coincide con una parola riservata di Java (`class`, `int`, ...) o con una variabile locale dichiarata all'interno del blocco, l'alias nominato viene ignorato — in tal caso usa `param0`.
:::

## Parametri multipli

Ogni argomento aggiuntivo riceve l'indice successivo. Qui `a` è `param0` e `b` è `param1`:

```npas
function somaDeQuadrados(a: Integer, b: Integer): Integer
begin
    return java:(a, b) {
        Integer x = (Integer)param0;
        Integer y = (Integer)param1;
        return x * x + y * y;
    };
end;

begin
    WriteLn(somaDeQuadrados(3, 4));
end.
```

<Output>
25
</Output>

## Usare le librerie della JVM

Il vero vantaggio sta nel chiamare classi Java. Usa sempre il **nome completamente qualificato** della classe, poiché non è possibile dichiarare `import`:

```npas
function raizQuadrada(numero: Integer): Real
begin
    return java:(numero) {
        Double n = ((Integer)param0).doubleValue();
        return Math.sqrt(n);
    };
end;

function agora(): String
begin
    return java:() {
        java.time.LocalDate hoje = java.time.LocalDate.now();
        return hoje.toString();
    };
end;

begin
    WriteLn("Raiz de 144: ", raizQuadrada(144));
    WriteLn("Data de hoje: ", agora());
end.
```

<Output>
Raiz de 144: 12.0
Data de hoje: 2026-07-17
</Output>

Nota che `agora` usa `java:()` **senza parametri** — il blocco Java può essere autosufficiente.

## Uso diretto nelle espressioni

Un blocco Java non deve necessariamente trovarsi all'interno di una funzione: può comparire direttamente in qualsiasi espressione, anche come argomento di `WriteLn`:

```npas
begin
    WriteLn("Cálculo direto: ", java:(10, 20) {
        Integer a = (Integer)param0;
        Integer b = (Integer)param1;
        return a + b;
    });
end.
```

<Output>
Cálculo direto: 30
</Output>

## Come le librerie interne lo usano

Le [librerie interne](./internal-libraries) di NeoObjectPascal sono, in gran parte, scritte con blocchi Java. Per esempio, `toUpperCase` della libreria `internal.string` è letteralmente:

```npas
function toUpperCase(s): String
begin
    return java:(s) {
        return ((String)param0).toUpperCase();
    };
end;
```

In altre parole, puoi scrivere le tue librerie seguendo lo stesso schema.

## Considerazioni

- **Cast esplicito**: i parametri arrivano come `Object`; esegui il cast a `Integer`, `String`, `Boolean`, ecc.
- **Nessun `import`**: usa nomi qualificati come `java.time.LocalDate`.
- **Eccezioni**: le eccezioni Java lanciate nel blocco vengono catturate dal runtime e possono essere gestite con `try/catch`.
- **Prestazioni**: c'è un piccolo costo di compilazione dinamica alla prima esecuzione di ciascun blocco.

::: tip Combina con il pipe
Le funzioni che incapsulano blocchi Java entrano naturalmente nelle pipeline `|>`. Vedi [Programmazione funzionale](./functional).
:::

---

Successivamente, organizza il tuo codice in [Moduli e uses](./modules).
