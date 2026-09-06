# Módulos e a Cláusula `uses`

O NeoObjectPascal permite que você organize seu código em módulos, que são arquivos `.npas` separados. Isso ajuda a manter seu código organizado e reutilizável.

## Criando um Módulo

Um módulo é simplesmente um arquivo NeoObjectPascal que contém declarações de funções, procedimentos, variáveis, etc. Por exemplo, você pode criar um arquivo `matematica.npas` com funções matemáticas:

```pascal
// matematica.npas

function somar(a: Integer; b: Integer): Integer;
begin
  return a + b;
end;

function subtrair(a: Integer; b: Integer): Integer;
begin
  return a - b;
end;
```

## Usando a Cláusula `uses`

Para usar as funções e procedimentos de um módulo em outro arquivo, você usa a cláusula `uses` no início do seu programa.

```pascal
// meu_programa.npas

uses
  matematica;

var
  resultado: Integer;

begin
  resultado := somar(10, 5);
  WriteLn(resultado);
end.
```

O compilador procurará o arquivo `matematica.npas` no mesmo diretório do arquivo principal.

