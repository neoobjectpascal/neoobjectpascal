# Estruturas de Controle

As estruturas de controle permitem que você controle o fluxo de execução do seu programa. O NeoObjectPascal oferece as estruturas de controle `if`, `while` e `for`.

## A Estrutura `if`

A estrutura `if` permite que você execute um bloco de código somente se uma determinada condição for verdadeira. Você também pode fornecer um bloco `else` para ser executado se a condição for falsa.

### Sintaxe

```pascal
if (condição) then
  statement
else
  statement;
```

### Exemplo

```pascal
var idade: Integer;
begin
  idade := 20;
  if (idade >= 18) then
    WriteLn('Você é maior de idade.')
  else
    WriteLn('Você é menor de idade.');
end.
```

### Exemplo com Bloco

```pascal
var nota: Integer;
begin
  nota := 85;
  if (nota >= 70) then
  begin
    WriteLn('Parabéns!');
    WriteLn('Você foi aprovado.');
  end
  else
  begin
    WriteLn('Infelizmente você foi reprovado.');
    WriteLn('Estude mais na próxima vez.');
  end;
end.
```

## A Estrutura `while`

A estrutura `while` executa um bloco de código repetidamente enquanto uma condição for verdadeira.

### Sintaxe

```pascal
while (condição) do
  statement;
```

### Exemplo Simples

```pascal
var i: Integer;
begin
  i := 1;
  while (i <= 5) do
  begin
    WriteLn(i);
    i := i + 1;
  end;
end.
```

### Exemplo com Contador

```pascal
var contador: Integer;
var soma: Integer;
begin
  contador := 1;
  soma := 0;
  
  while (contador <= 10) do
  begin
    soma := soma + contador;
    WriteLn('Contador: ', contador, ', Soma: ', soma);
    contador := contador + 1;
  end;
  
  WriteLn('Soma final: ', soma);
end.
```

## A Estrutura `for`

A estrutura `for` é usada para iterar sobre uma sequência de números de forma mais concisa que o `while`.

### Sintaxe

```pascal
for variável := valor_inicial to valor_final do
  statement;
```

### Exemplo Simples

```pascal
var i: Integer;
begin
  for i := 1 to 5 do
    WriteLn(i);
end.
```

### Exemplo com Cálculos

```pascal
var i: Integer;
var quadrado: Integer;
begin
  WriteLn('Tabela de quadrados:');
  for i := 1 to 10 do
  begin
    quadrado := i * i;
    WriteLn(i, ' ao quadrado = ', quadrado);
  end;
end.
```

### Exemplo com Contagem Regressiva

```pascal
var i: Integer;
begin
  WriteLn('Contagem regressiva:');
  for i := 10 to 1 do
    WriteLn(i);
  WriteLn('Fim!');
end.
```

## Estruturas Aninhadas

Você pode combinar diferentes estruturas de controle para criar lógicas mais complexas.

### Exemplo: `if` dentro de `for`

```pascal
var i: Integer;
begin
  for i := 1 to 10 do
  begin
    if (i % 2 = 0) then
      WriteLn(i, ' é par')
    else
      WriteLn(i, ' é ímpar');
  end;
end.
```

### Exemplo: `while` dentro de `if`

```pascal
var opcao: Integer;
var contador: Integer;
begin
  opcao := 1;
  
  if (opcao = 1) then
  begin
    contador := 1;
    while (contador <= 3) do
    begin
      WriteLn('Executando opção 1, iteração ', contador);
      contador := contador + 1;
    end;
  end;
end.
```

## Operadores de Comparação

Para usar as estruturas de controle efetivamente, você precisa conhecer os operadores de comparação:

| Operador | Descrição | Exemplo |
|----------|-----------|---------|
| `=` | Igual a | `if (x = 5)` |
| `<>` | Diferente de | `if (x <> 0)` |
| `<` | Menor que | `if (x < 10)` |
| `>` | Maior que | `if (x > 0)` |
| `<=` | Menor ou igual a | `if (x <= 100)` |
| `>=` | Maior ou igual a | `if (x >= 18)` |

## Boas Práticas

1. **Use parênteses**: Sempre coloque a condição entre parênteses para maior clareza
2. **Indentação**: Mantenha uma indentação consistente para facilitar a leitura
3. **Blocos begin/end**: Use `begin` e `end` quando tiver múltiplas instruções
4. **Evite loops infinitos**: Certifique-se de que a condição do `while` eventualmente se torne falsa
5. **Nomes descritivos**: Use nomes de variáveis que descrevam seu propósito

## Exemplo Completo

```pascal
var numero: Integer;
var tentativas: Integer;
var acertou: Boolean;
begin
  numero := 42;
  tentativas := 0;
  acertou := false;
  
  WriteLn('Jogo de adivinhação!');
  WriteLn('Tente adivinhar o número entre 1 e 100');
  
  while (tentativas < 5) do
  begin
    tentativas := tentativas + 1;
    WriteLn('Tentativa ', tentativas, ':');
    
    if (numero = 42) then
    begin
      WriteLn('Parabéns! Você acertou!');
      acertou := true;
    end
    else
    begin
      if (numero < 42) then
        WriteLn('O número é maior!')
      else
        WriteLn('O número é menor!');
    end;
  end;
  
  if (acertou = false) then
    WriteLn('Fim de jogo! O número era 42.');
end.
```

Este exemplo demonstra o uso combinado de `while`, `if` aninhados e operadores de comparação para criar um programa mais complexo e interativo.
