# Programmazione funzionale

NeoObjectPascal incorpora idee della programmazione funzionale affinché tu possa scrivere codice più conciso, espressivo e facile da mantenere. La funzionalità centrale è l'**operatore pipe** `|>`, che concatena le trasformazioni in modo leggibile, da sinistra a destra.

## L'operatore pipe `|>`

L'operatore `|>` passa il valore di sinistra come **primo argomento** della funzione a destra. In altre parole, `valore |> funzione` è equivalente a `funzione(valore)`:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

begin
    WriteLn(dobrar(5));      // chamada tradicional
    WriteLn(5 |> dobrar);    // com pipe — mesmo resultado
end.
```

<Output>
10
10
</Output>

I due stili producono lo stesso risultato. Il vantaggio del pipe emerge quando concateniamo diverse trasformazioni.

## Concatenamento di funzioni

Poiché il pipe consegna il risultato di una funzione come input della successiva, puoi costruire una sequenza di passaggi senza annidare le chiamate:

```npas
function dobrar(n: Integer): Integer
begin
    return n * 2;
end;

function incrementar(n: Integer): Integer
begin
    return n + 1;
end;

function aoQuadrado(n: Integer): Integer
begin
    return n * n;
end;

begin
    // Leia da esquerda para a direita: 10 → 20 → 21 → 441
    WriteLn(10 |> dobrar |> incrementar |> aoQuadrado);
end.
```

<Output>
441
</Output>

Confronta con la forma annidata `aoQuadrado(incrementar(dobrar(10)))`. Il pipe elimina le parentesi e rende immediatamente visibile l'ordine delle operazioni.

::: tip Leggi come una frase
Un concatenamento con `|>` si legge nell'ordine in cui i passaggi avvengono. È come un "nastro trasportatore" di dati: ogni funzione riceve il risultato di quella precedente.
:::

## Funzioni piccole e componibili

Il pipe favorisce uno stile in cui crei **funzioni piccole con un'unica responsabilità** e le combini. Ogni funzione rimane semplice, testabile e riutilizzabile:

```npas
function limpar(texto: String): String
begin
    // Remove um prefixo de espaço e normaliza
    return "[" + texto + "]";
end;

function emMaiusculas(texto: String): String
begin
    return java:(texto) {
        return ((String)param0).toUpperCase();
    };
end;

begin
    WriteLn("relatorio" |> emMaiusculas |> limpar);
end.
```

<Output>
[RELATORIO]
</Output>

Nota che `emMaiusculas` delega il lavoro pesante a un blocco Java. Puoi mescolare liberamente funzioni NeoObjectPascal e blocchi `java:(...) { ... }` all'interno di una pipeline. Vedi i dettagli in [Integrazione con Java](./java-integration).

## Combinare il pipe con l'aritmetica

Il pipe ha una precedenza bassa, quindi le espressioni aritmetiche alla sua sinistra vengono valutate prima di essere convogliate:

```npas
function descrever(n: Integer): String
begin
    if n > 100 then
        return "grande"
    else
        return "pequeno";
end;

begin
    WriteLn(20 * 3 |> descrever);   // (20 * 3) = 60 → "pequeno"
    WriteLn(50 * 3 |> descrever);   // (50 * 3) = 150 → "grande"
end.
```

<Output>
pequeno
grande
</Output>

::: info Precedenza
Il `|>` si colloca appena sopra gli operatori unari nella tabella di precedenza. Consulta [Operatori](../language/operators) per la tabella completa e usa le parentesi ogni volta che vuoi rendere esplicita l'intenzione.
:::

## Buone pratiche

- Preferisci funzioni **pure** (senza effetti collaterali) nelle pipeline: dato lo stesso argomento, restituiscono sempre lo stesso risultato.
- Assegna nomi che descrivano la trasformazione (`dobrar`, `emMaiusculas`, `descrever`), non il passaggio (`passo1`).
- Mantieni ogni funzione breve. Se un passaggio cresce troppo, suddividilo in funzioni più piccole e collegale con `|>`.

Anche queste piccole funzioni sono facili da coprire con il framework di test nativo — vedi [Test di unità](../testing/unit-testing).

---

Di seguito, impara a trasformare testi strutturati in oggetti con [Manipolazione dei dati (JSON e CSV)](./data-parsing).
