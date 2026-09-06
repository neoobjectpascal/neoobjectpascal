# Referência da linguagem

Esta página reúne, de forma condensada, as **palavras-chave**, os **tipos**, os **operadores** (com precedência) e um **resumo gramatical** do NeoObjectPascal. Ela serve como consulta rápida — para explicações passo a passo, siga os links para as páginas de guia.

::: info Fonte da verdade
Todo o conteúdo desta página é derivado diretamente da gramática ANTLR (`NeoObjectPascalLexer.g4` e `NeoObjectPascalParser.g4`) e do interpretador. Onde a gramática e a documentação antiga divergem, a gramática vence.
:::

## Palavras-chave

As palavras-chave abaixo são reservadas pelo lexer. Estão agrupadas por finalidade.

| Grupo | Palavras-chave |
| --- | --- |
| Módulos | `uses`, `into` |
| Blocos e variáveis | `begin`, `end`, `var` |
| Sub-rotinas | `function`, `procedure`, `return`, `constructor` |
| Controle de fluxo | `if`, `then`, `else`, `while`, `do`, `for`, `to`, `in` |
| E/S | `WriteLn`, `ReadLn`, `showMenu` |
| Booleanos / lógicos | `and`, `or`, `not` |
| Tratamento de erros | `try`, `catch`, `finally`, `raise` |
| Coleções | `array` |
| Orientação a objetos | `class`, `interface`, `extends`, `implements`, `public`, `private`, `protected`, `virtual`, `override`, `self`, `new` |
| Java embutido | `java` |
| Parsing de dados | `JSON.parse`, `CSV.parse` |
| Testes | `test`, `expect`, `toBe`, `toEqual`, `toBeTrue`, `toBeFalse`, `toBeNull` |
| Mocking | `mock`, `thenReturn`, `verify` |

::: tip Booleanos literais
`true` e `false` não são palavras-chave reservadas, mas são reconhecidos como literais booleanos (inclusive `True`/`TRUE`, sem distinção de maiúsculas/minúsculas).
:::

Veja o uso prático em [Operadores](../language/operators), [Classes](../oop/classes), [Bibliotecas internas](../features/internal-libraries) e [Testes unitários](../testing/unit-testing).

## Tipos

O NeoObjectPascal tem seis tipos declaráveis:

| Tipo | Descrição | Exemplo de literal |
| --- | --- | --- |
| `Integer` | Número inteiro | `42`, `0`, `-7` |
| `Real` | Número de ponto flutuante (double) | `3.14`, `5.0` |
| `String` | Texto | `"olá"`, `'mundo'` |
| `Boolean` | Verdadeiro/falso | `true`, `false` |
| `Array` | Lista de elementos, base 0 | `[10, 20, 30]`, `[]` |
| `Object` | Genérico — guarda instâncias, resultados de `JSON.parse`/`CSV.parse` | — |

::: warning Literais Real precisam de dígitos dos dois lados
Um número real precisa de dígitos antes **e** depois do ponto: `5.0` é válido, `5.` não é. Um `Integer` é promovido automaticamente a `Real` em aritmética mista ou ao ser atribuído a uma variável `Real`.
:::

## Operadores

A tabela lista os operadores da **maior** para a **menor** precedência. Operadores na mesma linha têm precedência igual e associam à esquerda.

| Precedência | Operador(es) | Descrição |
| --- | --- | --- |
| 1 (mais alta) | `a[i]` | Indexação de array (base 0) |
| 2 | `*`  `/` | Multiplicação, divisão |
| 3 | `+`  `-` | Adição/subtração; `+` também concatena strings |
| 4 | `=`  `<>`  `<`  `>`  `<=`  `>=` | Comparações |
| 5 | `and` | E lógico |
| 6 | `or` | OU lógico |
| 7 (mais baixa) | `\|>` | Pipe (passa o valor à esquerda como 1º argumento da função à direita) |
| unário | `not expr`  `-expr` | Negação lógica / negação numérica (ligam mais forte que os binários) |

::: warning `=` é igualdade, `:=` é atribuição
Diferente de C e derivados, a igualdade usa um **único** `=`. A atribuição usa `:=`. O "diferente de" é `<>` (não `!=`).
:::

Exemplos:

```npas
begin
  WriteLn(10 = 10);          // true  (igualdade)
  WriteLn(10 <> 5);          // true  (diferente)
  WriteLn(2 + 3 * 4);        // 14    (* antes de +)
  WriteLn("Neo" + "Pascal"); // NeoPascal (concatenação)
  WriteLn(5 |> square);      // 25    (pipe)
end.
```

Detalhes e exemplos completos em [Operadores](../language/operators) e [Programação funcional](../features/functional).

## Comentários

Existe **apenas** o comentário de linha `//`. Não há comentários de bloco: as chaves `{ }` são reservadas para [blocos de código Java](../features/java-integration).

```npas
begin
  // isto é um comentário de linha
  WriteLn("Oi"); // também funciona no fim da linha
end.
```

::: warning Sem `program` e sem `inherited`
O NeoObjectPascal **não** tem cabeçalho `program Nome;` — o arquivo começa direto na cláusula `uses`, nas declarações ou no bloco `begin`. Também **não** existe a palavra-chave `inherited`.
:::

## Resumo da gramática

Os blocos abaixo descrevem, em notação EBNF-like, as principais construções da linguagem, fiéis à gramática do parser. `?` = opcional, `*` = zero ou mais, `|` = alternativa.

### Programa e blocos

```text
program   ::= (usesClause ";")? (declaration ";")* (block ".")? EOF

usesClause ::= "uses" modulePath ("," modulePath)*
modulePath ::= IDENT ("." IDENT)*

block     ::= "begin" (statement ";")* "end"
```

Um programa é: uma cláusula `uses` opcional, seguida de zero ou mais declarações, seguida de um bloco principal opcional encerrado por um ponto final `.`.

### Declarações de topo

```text
declaration ::= variableDeclaration
              | functionDeclaration
              | classDeclaration
              | interfaceDeclaration
              | testDeclaration

variableDeclaration ::= "var" IDENT ":" type
```

### Instruções

```text
statement ::= variableDeclaration
            | arrayElementAssignment      // a[i] := expr
            | assignment                  // (IDENT | memberAccess) := expr
            | methodCall                  // obj.metodo(args)
            | call                        // nome | nome(args)
            | ifStatement
            | whileStatement
            | forInStatement
            | forStatement
            | writeLnStatement
            | readLnStatement
            | showMenuStatement
            | jsonParseStatement
            | csvParseStatement
            | returnStatement
            | tryStatement
            | raiseStatement
            | functionDeclaration
            | expectStatement
            | mockStatement
            | verifyStatement
            | block

ifStatement    ::= "if" expression "then" statement ("else" statement)?
whileStatement ::= "while" expression "do" statement
forStatement   ::= "for" IDENT ":=" expression "to" expression "do" statement
forInStatement ::= "for" IDENT "in" expression "do" statement
returnStatement::= "return" expression
raiseStatement ::= "raise" expression

tryStatement   ::= "try" block "catch" "(" IDENT ")" block ("finally" block)?
```

### E/S e dados

```text
writeLnStatement  ::= "WriteLn" "(" expressionList? ")"
readLnStatement   ::= "ReadLn" "(" IDENT ")"
showMenuStatement ::= "showMenu" "(" expressionList ")"
jsonParseStatement::= "JSON.parse" "(" expression ")" "into" IDENT
csvParseStatement ::= "CSV.parse"  "(" expression ")" "into" IDENT
```

### Expressões

```text
expression ::= javaBlock
             | newExpression
             | methodCall
             | memberAccess
             | "not" expression
             | "-" expression
             | arrayLiteral
             | primary
             | expression "[" expression "]"
             | expression ("*" | "/") expression
             | expression ("+" | "-") expression
             | expression ("=" | "<>" | "<" | ">" | "<=" | ">=") expression
             | expression "and" expression
             | expression "or" expression
             | expression "|>" expression

primary     ::= INTEGER | REAL | STRING | "self" | IDENT
              | "(" expression ")" | call

arrayLiteral   ::= "[" expressionList? "]"
newExpression  ::= "new" IDENT "(" expressionList? ")"
memberAccess   ::= (IDENT | "self") ("." IDENT)+
methodCall     ::= memberAccess "(" expressionList? ")"
javaBlock      ::= "java" ":" "(" expressionList? ")" JAVA_CODE
```

### Funções e procedimentos

```text
functionDeclaration ::= "function" IDENT ("(" parameterList? ")")? ":" type block

parameterList ::= parameter ("," parameter)*
parameter     ::= IDENT (":" type)?
```

Parâmetros podem ser tipados ou não; o separador é a **vírgula**. Ver [Funções](../language/functions).

### Classes e interfaces

```text
classDeclaration ::= "class" IDENT
                     ("extends" IDENT)?
                     ("implements" IDENT ("," IDENT)*)?
                     (classMember ";")*
                     "end"

classMember ::= fieldDeclaration
              | methodDeclaration
              | constructorDeclaration
              | procedureDeclaration

fieldDeclaration      ::= "var" IDENT ":" type
methodDeclaration     ::= (visibility)? (virtualOrOverride)?
                          "function" IDENT "(" parameterList? ")" ":" type block
procedureDeclaration  ::= (visibility)? (virtualOrOverride)?
                          "procedure" IDENT "(" parameterList? ")" block
constructorDeclaration::= "constructor" IDENT "(" parameterList? ")" block

visibility        ::= "public" | "private" | "protected"
virtualOrOverride ::= "virtual" | "override"

interfaceDeclaration ::= "interface" IDENT (methodSignature ";")* "end"
methodSignature      ::= "function" IDENT "(" parameterList? ")" ":" type
```

Campos são privados por padrão; os modificadores de visibilidade são analisados e documentam a intenção, mas não são estritamente impostos em tempo de execução. Ver [Classes](../oop/classes) e [Interfaces](../oop/interfaces).

### Testes e mocking

```text
testDeclaration ::= "test" STRING block

expectStatement ::= "expect" "(" expression ")" "."
                    ( "toBe"     "(" expression ")"
                    | "toEqual"  "(" expression ")"
                    | "toBeTrue"  "(" ")"
                    | "toBeFalse" "(" ")"
                    | "toBeNull"  "(" ")" )

mockStatement   ::= "mock" IDENT IDENT "thenReturn" expression
                  | "mock" IDENT "." IDENT "thenReturn" expression

verifyStatement ::= "verify" IDENT IDENT
                  | "verify" IDENT "." IDENT
```

Ver [Testes unitários](../testing/unit-testing).

## Próximos passos

Para ver todas essas construções em ação, explore a [Galeria de exemplos](./examples).
