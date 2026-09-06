# Fehlerbehandlung

Fehler passieren: eine Division durch Null, ein ungültiger Index, eine fehlschlagende Validierung. NeoObjectPascal behandelt Fehler mit `try/catch/finally` und ermöglicht das Signalisieren von Fehlern mit `raise`.

## Die Struktur `try/catch/finally`

Die allgemeine Form verwendet Blöcke `begin ... end` in jedem Teil:

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

- Der Block `try` enthält den überwachten Code.
- Der Block `catch (e)` wird **nur dann** ausgeführt, wenn ein Fehler auftritt; die Variable `e` ist an den Fehlerwert gebunden.
- Der Block `finally` ist **optional** und wird **immer ausgeführt**, mit oder ohne Fehler.

## Fehler mit `raise` auslösen

Verwenden Sie `raise expr;`, um einen Fehler zu signalisieren. Normalerweise ist der Ausdruck eine `String`-Nachricht:

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

Beachten Sie, dass der zweite Aufruf den Fehler innerhalb von `dividir` auslöst, sodass die Zeile `"Nunca chegará aqui"` nie ausgeführt wird — die Steuerung springt direkt zum `catch`.

## Die Variable des `catch`

Der Name in Klammern in `catch (nome)` erhält den ausgelösten Wert. Für Fehler, die Sie mit `raise` auslösen, ist es genau der übergebene Ausdruck. Für **eingebaute Fehler** (Division durch Null, Index außerhalb des Bereichs) ist es die Fehlermeldung in Form eines Strings:

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

::: info Eingebaute Fehler sind abfangbar
Fehler wie **Division durch Null** und **Array-Index außerhalb des Bereichs** erzeugen Fehler, die Sie wie jeden anderen mit `try/catch` abfangen können.
:::

## Der Block `finally`

Das `finally` wird immer ausgeführt — sowohl auf dem Erfolgspfad als auch dann, wenn ein Fehler abgefangen wird. Es ist der ideale Ort für Aufräumarbeiten (Ressourcen schließen, Abschluss protokollieren):

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

Im ersten `try` gibt es keinen Fehler, also wird das `try` und danach das `finally` ausgeführt. Im zweiten löst `validarIdade` den Fehler aus, das `catch` fängt ihn ab und das `finally` läuft anschließend.

::: tip `return` durchläuft das `finally`
Wenn Sie aus einem `try` heraus zurückkehren, wird der Block `finally` dennoch ausgeführt, bevor die Funktion tatsächlich zurückkehrt. Nutzen Sie dies, um auch bei vorzeitigen Ausstiegen die Aufräumarbeiten sicherzustellen.
:::

Nachdem Sie die Fehlerbehandlung beherrschen, gehen Sie weiter zur Objektorientierung in [Klassen](../oop/classes).
