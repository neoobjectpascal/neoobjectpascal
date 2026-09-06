# Language reference

This page gathers, in condensed form, the **keywords**, the **types**, the **operators** (with precedence) and a **grammar summary** of NeoObjectPascal. It serves as a quick reference — for step-by-step explanations, follow the links to the guide pages.

::: info Source of truth
All content on this page is derived directly from the ANTLR grammar (`NeoObjectPascalLexer.g4` and `NeoObjectPascalParser.g4`) and from the interpreter. Where the grammar and the older documentation diverge, the grammar wins.
:::

## Keywords

The keywords below are reserved by the lexer. They are grouped by purpose.

| Group | Keywords |
| --- | --- |
| Modules | `uses`, `into` |
| Blocks and variables | `begin`, `end`, `var` |
| Subroutines | `function`, `procedure`, `return`, `constructor` |
| Control flow | `if`, `then`, `else`, `while`, `do`, `for`, `to`, `in` |
| I/O | `WriteLn`, `ReadLn`, `showMenu` |
| Boolean / logical | `and`, `or`, `not` |
| Error handling | `try`, `catch`, `finally`, `raise` |
| Collections | `array` |
| Object orientation | `class`, `interface`, `extends`, `implements`, `public`, `private`, `protected`, `virtual`, `override`, `self`, `new` |
| Embedded Java | `java` |
| Data parsing | `JSON.parse`, `CSV.parse` |
| Testing | `test`, `expect`, `toBe`, `toEqual`, `toBeTrue`, `toBeFalse`, `toBeNull` |
| Mocking | `mock`, `thenReturn`, `verify` |

::: tip Boolean literals
`true` and `false` are not reserved keywords, but they are recognized as boolean literals (including `True`/`TRUE`, case-insensitive).
:::

See practical usage in [Operators](../language/operators), [Classes](../oop/classes), [Internal libraries](../features/internal-libraries) and [Unit testing](../testing/unit-testing).

## Types

NeoObjectPascal has six declarable types:

| Type | Description | Literal example |
| --- | --- | --- |
| `Integer` | Whole number | `42`, `0`, `-7` |
| `Real` | Floating-point number (double) | `3.14`, `5.0` |
| `String` | Text | `"olá"`, `'mundo'` |
| `Boolean` | True/false | `true`, `false` |
| `Array` | List of elements, 0-based | `[10, 20, 30]`, `[]` |
| `Object` | Generic — holds instances, results of `JSON.parse`/`CSV.parse` | — |

::: warning Real literals need digits on both sides
A real number needs digits before **and** after the point: `5.0` is valid, `5.` is not. An `Integer` is automatically promoted to `Real` in mixed arithmetic or when assigned to a `Real` variable.
:::

## Operators

The table lists operators from **highest** to **lowest** precedence. Operators on the same row have equal precedence and associate to the left.

| Precedence | Operator(s) | Description |
| --- | --- | --- |
| 1 (highest) | `a[i]` | Array indexing (0-based) |
| 2 | `*`  `/` | Multiplication, division |
| 3 | `+`  `-` | Addition/subtraction; `+` also concatenates strings |
| 4 | `=`  `<>`  `<`  `>`  `<=`  `>=` | Comparisons |
| 5 | `and` | Logical AND |
| 6 | `or` | Logical OR |
| 7 (lowest) | `\|>` | Pipe (passes the left-hand value as the 1st argument of the function on the right) |
| unary | `not expr`  `-expr` | Logical negation / numeric negation (bind more tightly than the binary operators) |

::: warning `=` is equality, `:=` is assignment
Unlike C and its derivatives, equality uses a **single** `=`. Assignment uses `:=`. "Not equal" is `<>` (not `!=`).
:::

Examples:

```npas
begin
  WriteLn(10 = 10);          // true  (igualdade)
  WriteLn(10 <> 5);          // true  (diferente)
  WriteLn(2 + 3 * 4);        // 14    (* antes de +)
  WriteLn("Neo" + "Pascal"); // NeoPascal (concatenação)
  WriteLn(5 |> square);      // 25    (pipe)
end.
```

Full details and examples in [Operators](../language/operators) and [Functional programming](../features/functional).

## Comments

There is **only** the line comment `//`. There are no block comments: the braces `{ }` are reserved for [Java code blocks](../features/java-integration).

```npas
begin
  // isto é um comentário de linha
  WriteLn("Oi"); // também funciona no fim da linha
end.
```

::: warning No `program` and no `inherited`
NeoObjectPascal does **not** have a `program Name;` header — the file starts directly with the `uses` clause, the declarations, or the `begin` block. The `inherited` keyword also does **not** exist.
:::

## Grammar summary

The blocks below describe, in EBNF-like notation, the language's main constructs, faithful to the parser grammar. `?` = optional, `*` = zero or more, `|` = alternative.

### Program and blocks

```text
program   ::= (usesClause ";")? (declaration ";")* (block ".")? EOF

usesClause ::= "uses" modulePath ("," modulePath)*
modulePath ::= IDENT ("." IDENT)*

block     ::= "begin" (statement ";")* "end"
```

A program is: an optional `uses` clause, followed by zero or more declarations, followed by an optional main block ended by a final period `.`.

### Top-level declarations

```text
declaration ::= variableDeclaration
              | functionDeclaration
              | classDeclaration
              | interfaceDeclaration
              | testDeclaration

variableDeclaration ::= "var" IDENT ":" type
```

### Statements

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

### I/O and data

```text
writeLnStatement  ::= "WriteLn" "(" expressionList? ")"
readLnStatement   ::= "ReadLn" "(" IDENT ")"
showMenuStatement ::= "showMenu" "(" expressionList ")"
jsonParseStatement::= "JSON.parse" "(" expression ")" "into" IDENT
csvParseStatement ::= "CSV.parse"  "(" expression ")" "into" IDENT
```

### Expressions

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

### Functions and procedures

```text
functionDeclaration ::= "function" IDENT ("(" parameterList? ")")? ":" type block

parameterList ::= parameter ("," parameter)*
parameter     ::= IDENT (":" type)?
```

Parameters may be typed or untyped; the separator is the **comma**. See [Functions](../language/functions).

### Classes and interfaces

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

Fields are private by default; the visibility modifiers are parsed and document intent, but are not strictly enforced at runtime. See [Classes](../oop/classes) and [Interfaces](../oop/interfaces).

### Testing and mocking

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

See [Unit testing](../testing/unit-testing).

## Next steps

To see all these constructs in action, explore the [Example gallery](./examples).
