# Gestione degli errori

Gli errori accadono: una divisione per zero, un indice non valido, una validazione che fallisce. NeoObjectPascal gestisce i fallimenti con `try/catch/finally` e consente di segnalare errori con `raise`.

## La struttura `try/catch/finally`

La forma generale usa blocchi `begin ... end` in ogni parte:

```npas
try
begin
    // código que pode falhar
end
catch (e)
begin
    // executa se um erro for lançado; e recebe o valor do erro
end
finally
begin
    // sempre executa (opcional)
end;
```

- Il blocco `try` contiene il codice monitorato.
- Il blocco `catch (e)` viene eseguito **solo se** si verifica un errore; la variabile `e` è associata al valore dell'errore.
- Il blocco `finally` è **opzionale** e viene **sempre eseguito**, con o senza errore.

## Sollevare errori con `raise`

Usa `raise expr;` per segnalare un errore. Normalmente l'espressione è un messaggio `String`:

```npas
function dividir(a: Integer, b: Integer): Real
begin
    if b = 0 then
        raise "Divisão por zero não permitida";
    return java:(a, b) {
        return ((Integer)param0).doubleValue() / ((Integer)param1).doubleValue();
    };
end;

var x: Real;
var erro: String;

begin
    try
    begin
        x := dividir(10, 2);
        WriteLn("10 / 2 = ", x);
    end
    catch (erro)
    begin
        WriteLn("Erro: ", erro);
    end;

    try
    begin
        x := dividir(5, 0);
        WriteLn("Nunca chegará aqui");
    end
    catch (erro)
    begin
        WriteLn("Capturado: ", erro);
    end;
end.
```

<Output>
10 / 2 = 5.0
Capturado: Divisão por zero não permitida
</Output>

Nota che la seconda chiamata solleva l'errore all'interno di `dividir`, quindi la riga `"Nunca chegará aqui"` non viene mai eseguita — il controllo salta direttamente al `catch`.

## La variabile del `catch`

Il nome tra parentesi in `catch (nome)` riceve il valore sollevato. Per gli errori che sollevi con `raise`, è esattamente l'espressione passata. Per gli **errori integrati** (divisione per zero, indice fuori dall'intervallo), è il messaggio di errore sotto forma di stringa:

```npas
var numeros: Array;
var erro: String;

begin
    numeros := [1, 2, 3];
    try
    begin
        WriteLn(numeros[10]);
    end
    catch (erro)
    begin
        WriteLn("Falhou ao acessar: ", erro);
    end;
end.
```

::: info Gli errori integrati sono catturabili
Fallimenti come la **divisione per zero** e l'**indice di array fuori dall'intervallo** generano errori che puoi catturare con `try/catch` come qualsiasi altro.
:::

## Il blocco `finally`

Il `finally` viene sempre eseguito — sia nel percorso di successo sia quando un errore viene catturato. È il posto ideale per la pulizia (chiudere risorse, registrare il completamento):

```npas
function validarIdade(idade: Integer): String
begin
    if idade < 0 then
        raise "Idade inválida: " + idade;
    if idade > 150 then
        raise "Idade improvável: " + idade;
    return "Idade válida: " + idade;
end;

var erro: String;

begin
    try
    begin
        WriteLn(validarIdade(25));
    end
    catch (erro)
    begin
        WriteLn("Erro de validação: ", erro);
    end
    finally
    begin
        WriteLn("Finally sempre executa");
    end;

    try
    begin
        WriteLn(validarIdade(0 - 5));
    end
    catch (erro)
    begin
        WriteLn("Capturado: ", erro);
    end
    finally
    begin
        WriteLn("Cleanup concluído");
    end;
end.
```

<Output>
Idade válida: 25
Finally sempre executa
Capturado: Idade inválida: -5
Cleanup concluído
</Output>

Nel primo `try` non c'è alcun errore, quindi viene eseguito il `try` e poi il `finally`. Nel secondo, `validarIdade` solleva l'errore, il `catch` lo cattura e il `finally` viene eseguito subito dopo.

::: tip `return` attraversa il `finally`
Se ritorni dall'interno di un `try`, il blocco `finally` viene comunque eseguito prima che la funzione ritorni effettivamente. Usalo per garantire la pulizia anche nelle uscite anticipate.
:::

Padroneggiata la gestione degli errori, prosegui verso la programmazione orientata agli oggetti in [Classi](../oop/classes).
