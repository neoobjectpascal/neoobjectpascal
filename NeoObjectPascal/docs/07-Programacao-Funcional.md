# Programação Funcional com NeoObjectPascal

O NeoObjectPascal incorpora conceitos de programação funcional, permitindo que você escreva um código mais conciso, expressivo e de fácil manutenção.

## Funções de Primeira Classe

No NeoObjectPascal, as funções são cidadãos de primeira classe. Isso significa que você pode tratá-las como qualquer outro valor, como atribuí-las a variáveis, passá-las como argumentos para outras funções e retorná-las de outras funções.

```pascal
function quadrado(x: Integer): Integer;
begin
  return x * x;
end;

var
  minhaFuncao: Object;

begin
  minhaFuncao := quadrado;
  WriteLn(minhaFuncao(5)); // Saída: 25
end.
```

## O Operador de Pipeline

O operador de pipeline `|>` permite encadear chamadas de função de uma forma mais legível. Ele pega o resultado da expressão à sua esquerda e o passa como o primeiro argumento para a função à sua direita.

```pascal
function dobrar(x: Integer): Integer;
begin
  return x * 2;
end;

function adicionarUm(x: Integer): Integer;
begin
  return x + 1;
end;

begin
  WriteLn(5 |> dobrar |> adicionarUm); // Saída: 11
end.
```


