# Tratamento de erros

Erros acontecem: uma divisão por zero, um índice inválido, uma validação que falha. O NeoObjectPascal trata falhas com `try/catch/finally` e permite sinalizar erros com `raise`.

## A estrutura `try/catch/finally`

A forma geral usa blocos `begin ... end` em cada parte:

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

- O bloco `try` contém o código monitorado.
- O bloco `catch (e)` executa **somente se** um erro ocorrer; a variável `e` fica ligada ao valor do erro.
- O bloco `finally` é **opcional** e **sempre executa**, com ou sem erro.

## Lançando erros com `raise`

Use `raise expr;` para sinalizar um erro. Normalmente a expressão é uma mensagem `String`:

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

Repare que a segunda chamada lança o erro dentro de `dividir`, então a linha `"Nunca chegará aqui"` nunca roda — o controle salta direto para o `catch`.

## A variável do `catch`

O nome entre parênteses em `catch (nome)` recebe o valor lançado. Para erros que você levanta com `raise`, é exatamente a expressão passada. Para **erros embutidos** (divisão por zero, índice fora do intervalo), é a mensagem de erro em forma de string:

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

::: info Erros embutidos são capturáveis
Falhas como **divisão por zero** e **índice de array fora do intervalo** geram erros que você pode capturar com `try/catch` como qualquer outro.
:::

## O bloco `finally`

O `finally` executa sempre — tanto no caminho de sucesso quanto quando um erro é capturado. É o lugar ideal para limpeza (fechar recursos, registrar conclusão):

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

No primeiro `try` não há erro, então roda o `try` e depois o `finally`. No segundo, `validarIdade` lança o erro, o `catch` o captura e o `finally` roda em seguida.

::: tip `return` atravessa o `finally`
Se você retornar de dentro de um `try`, o bloco `finally` ainda executa antes de a função de fato retornar. Use isso para garantir limpeza mesmo em saídas antecipadas.
:::

Com o tratamento de erros dominado, avance para a orientação a objetos em [Classes](../oop/classes).
