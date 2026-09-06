# Sintaxe Básica do NeoObjectPascal

Neste capítulo, vamos explorar a sintaxe fundamental do NeoObjectPascal. Você aprenderá sobre a estrutura de um programa, como declarar variáveis e os tipos de dados disponíveis.

## Estrutura de um Programa

Um programa em NeoObjectPascal é composto por uma ou mais unidades de compilação. Cada unidade pode conter uma cláusula `uses` para importar outros módulos, declarações de funções e um bloco de código principal.

```pascal
program MeuPrimeiroPrograma;

uses
  MeuModulo;

var
  minhaVariavel: Integer;

begin
  // Seu código aqui
end.
```

O programa termina com um ponto (`.`).

## Comentários

Comentários são usados para adicionar notas explicativas ao seu código. Eles são ignorados pelo compilador. O NeoObjectPascal suporta comentários de uma linha e de várias linhas.

```pascal
{ Este é um comentário de várias linhas.
  Ele pode se estender por várias linhas. }

// Este é um comentário de uma linha.
```

## Variáveis e Tipos de Dados

As variáveis são usadas para armazenar dados. No NeoObjectPascal, você deve declarar uma variável antes de usá-la, especificando seu nome e tipo. A declaração de variáveis é feita na seção `var`.

```pascal
var
  idade: Integer;
  nome: String;
  ativo: Boolean;
  salario: Real;
  dados: Object;
```

O NeoObjectPascal suporta os seguintes tipos de dados básicos:

| Tipo      | Descrição                                       |
|-----------|---------------------------------------------------|
| `Integer` | Números inteiros                                  |
| `String`  | Sequências de caracteres                          |
| `Boolean` | Valores verdadeiro (`True`) ou falso (`False`)      |
| `Real`    | Números de ponto flutuante                        |
| `Object`  | Um tipo genérico para armazenar dados complexos, como de JSON. |

## Atribuição

O operador de atribuição `:=` é usado para atribuir um valor a uma variável.

```pascal
idade := 30;
nome := 'Carmen Sandiego';
ativo := True;
```

