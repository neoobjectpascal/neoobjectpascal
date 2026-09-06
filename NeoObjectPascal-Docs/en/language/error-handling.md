# Error handling

Errors happen: a division by zero, an invalid index, a validation that fails. NeoObjectPascal handles failures with `try/catch/finally` and lets you signal errors with `raise`.

## The `try/catch/finally` structure

The general form uses `begin ... end` blocks in each part:

```npas
try
begin
    // code that may fail
end
catch (e)
begin
    // runs if an error is thrown; e receives the error value
end
finally
begin
    // always runs (optional)
end;
```

- The `try` block contains the monitored code.
- The `catch (e)` block runs **only if** an error occurs; the variable `e` is bound to the error value.
- The `finally` block is **optional** and **always runs**, with or without an error.

## Throwing errors with `raise`

Use `raise expr;` to signal an error. Typically the expression is a `String` message:

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

Notice that the second call throws the error inside `dividir`, so the `"Nunca chegará aqui"` line never runs — control jumps straight to the `catch`.

## The `catch` variable

The name in parentheses in `catch (name)` receives the thrown value. For errors you raise with `raise`, it is exactly the expression passed. For **built-in errors** (division by zero, index out of range), it is the error message as a string:

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

::: info Built-in errors are catchable
Failures such as **division by zero** and **array index out of range** raise errors that you can catch with `try/catch` like any other.
:::

## The `finally` block

The `finally` always runs — both on the success path and when an error is caught. It is the ideal place for cleanup (closing resources, logging completion):

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

In the first `try` there is no error, so the `try` runs and then the `finally`. In the second, `validarIdade` throws the error, the `catch` catches it, and the `finally` runs afterward.

::: tip `return` passes through `finally`
If you return from inside a `try`, the `finally` block still runs before the function actually returns. Use this to guarantee cleanup even on early exits.
:::

With error handling mastered, move on to object orientation in [Classes](../oop/classes).
