# Funções e Procedimentos

Funções e procedimentos são blocos de código reutilizáveis que executam uma tarefa específica. A principal diferença entre eles é que as funções retornam um valor, enquanto os procedimentos não.

## Declaração de Funções

Para declarar uma função, você usa a palavra-chave `function`, seguida pelo nome da função, uma lista de parâmetros (opcional), o tipo de retorno e o bloco de código da função.

```pascal
function somar(a: Integer; b: Integer): Integer;
begin
  return a + b;
end;
```

## Declaração de Procedimentos

A declaração de um procedimento é semelhante à de uma função, mas você usa a palavra-chave `procedure` e não há tipo de retorno.

```pascal
procedure saudacao(nome: String);
begin
  WriteLn("Olá, " + nome);
end;
```

## Chamando Funções e Procedimentos

Para chamar uma função ou procedimento, basta usar seu nome seguido pelos argumentos entre parênteses.

```pascal
var resultado: Integer;
begin
  resultado := somar(5, 10);
  saudacao("Mundo");
end.
```

