# Sprachreferenz

Diese Seite fasst in kompakter Form die **Schlüsselwörter**, die **Typen**, die **Operatoren** (mit Vorrangregeln) und eine **grammatikalische Übersicht** von NeoObjectPascal zusammen. Sie dient als Schnellreferenz — für Schritt-für-Schritt-Erklärungen folgen Sie den Links zu den Handbuchseiten.

::: info Quelle der Wahrheit
Der gesamte Inhalt dieser Seite ist direkt aus der ANTLR-Grammatik (`NeoObjectPascalLexer.g4` und `NeoObjectPascalParser.g4`) und dem Interpreter abgeleitet. Wo Grammatik und alte Dokumentation voneinander abweichen, gewinnt die Grammatik.
:::

## Schlüsselwörter

Die folgenden Schlüsselwörter sind vom Lexer reserviert. Sie sind nach Verwendungszweck gruppiert.

| Gruppe | Schlüsselwörter |
| --- | --- |
| Module | `uses`, `into` |
| Blöcke und Variablen | `begin`, `end`, `var` |
| Unterprogramme | `function`, `procedure`, `return`, `constructor` |
| Ablaufsteuerung | `if`, `then`, `else`, `while`, `do`, `for`, `to`, `in` |
| Ein-/Ausgabe | `WriteLn`, `ReadLn`, `showMenu` |
| Boolesch / logisch | `and`, `or`, `not` |
| Fehlerbehandlung | `try`, `catch`, `finally`, `raise` |
| Sammlungen | `array` |
| Objektorientierung | `class`, `interface`, `extends`, `implements`, `public`, `private`, `protected`, `virtual`, `override`, `self`, `new` |
| Eingebettetes Java | `java` |
| Datenparsing | `JSON.parse`, `CSV.parse` |
| Tests | `test`, `expect`, `toBe`, `toEqual`, `toBeTrue`, `toBeFalse`, `toBeNull` |
| Mocking | `mock`, `thenReturn`, `verify` |

::: tip Boolesche Literale
`true` und `false` sind keine reservierten Schlüsselwörter, werden aber als boolesche Literale erkannt (einschließlich `True`/`TRUE`, ohne Unterscheidung von Groß- und Kleinschreibung).
:::

Die praktische Verwendung finden Sie unter [Operatoren](../language/operators), [Klassen](../oop/classes), [Interne Bibliotheken](../features/internal-libraries) und [Unit-Tests](../testing/unit-testing).

## Typen

NeoObjectPascal hat sechs deklarierbare Typen:

| Typ | Beschreibung | Literalbeispiel |
| --- | --- | --- |
| `Integer` | Ganze Zahl | `42`, `0`, `-7` |
| `Real` | Gleitkommazahl (double) | `3.14`, `5.0` |
| `String` | Text | `"olá"`, `'mundo'` |
| `Boolean` | Wahr/falsch | `true`, `false` |
| `Array` | Liste von Elementen, nullbasiert | `[10, 20, 30]`, `[]` |
| `Object` | Generisch — enthält Instanzen, Ergebnisse von `JSON.parse`/`CSV.parse` | — |

::: warning Real-Literale benötigen Ziffern auf beiden Seiten
Eine reelle Zahl benötigt Ziffern vor **und** nach dem Punkt: `5.0` ist gültig, `5.` nicht. Ein `Integer` wird bei gemischter Arithmetik oder bei der Zuweisung an eine `Real`-Variable automatisch zu `Real` heraufgestuft.
:::

## Operatoren

Die Tabelle listet die Operatoren vom **höchsten** zum **niedrigsten** Vorrang auf. Operatoren in derselben Zeile haben gleichen Vorrang und sind linksassoziativ.

| Vorrang | Operator(en) | Beschreibung |
| --- | --- | --- |
| 1 (höchster) | `a[i]` | Array-Indizierung (nullbasiert) |
| 2 | `*`  `/` | Multiplikation, Division |
| 3 | `+`  `-` | Addition/Subtraktion; `+` verkettet auch Strings |
| 4 | `=`  `<>`  `<`  `>`  `<=`  `>=` | Vergleiche |
| 5 | `and` | Logisches UND |
| 6 | `or` | Logisches ODER |
| 7 (niedrigster) | `\|>` | Pipe (übergibt den linken Wert als 1. Argument der rechten Funktion) |
| unär | `not expr`  `-expr` | Logische Negation / numerische Negation (binden stärker als die binären Operatoren) |

::: warning `=` ist Gleichheit, `:=` ist Zuweisung
Anders als in C und dessen Ableitungen verwendet die Gleichheit ein **einzelnes** `=`. Die Zuweisung verwendet `:=`. Das „ungleich" ist `<>` (nicht `!=`).
:::

Beispiele:

```npas
begin
  WriteLn(10 = 10);          // true  (igualdade)
  WriteLn(10 <> 5);          // true  (diferente)
  WriteLn(2 + 3 * 4);        // 14    (* antes de +)
  WriteLn("Neo" + "Pascal"); // NeoPascal (concatenação)
  WriteLn(5 |> square);      // 25    (pipe)
end.
```

Details und vollständige Beispiele finden Sie unter [Operatoren](../language/operators) und [Funktionale Programmierung](../features/functional).

## Kommentare

Es gibt **nur** den Zeilenkommentar `//`. Es gibt keine Blockkommentare: die geschweiften Klammern `{ }` sind für [Java-Codeblöcke](../features/java-integration) reserviert.

```npas
begin
  // isto é um comentário de linha
  WriteLn("Oi"); // também funciona no fim da linha
end.
```

::: warning Kein `program` und kein `inherited`
NeoObjectPascal hat **keinen** `program Name;`-Header — die Datei beginnt direkt mit der `uses`-Klausel, den Deklarationen oder dem `begin`-Block. Es gibt außerdem **kein** Schlüsselwort `inherited`.
:::

## Grammatikübersicht

Die folgenden Blöcke beschreiben in EBNF-ähnlicher Notation die wichtigsten Konstrukte der Sprache, getreu der Parser-Grammatik. `?` = optional, `*` = null oder mehr, `|` = Alternative.

### Programm und Blöcke

```text
program   ::= (usesClause ";")? (declaration ";")* (block ".")? EOF

usesClause ::= "uses" modulePath ("," modulePath)*
modulePath ::= IDENT ("." IDENT)*

block     ::= "begin" (statement ";")* "end"
```

Ein Programm besteht aus: einer optionalen `uses`-Klausel, gefolgt von null oder mehr Deklarationen, gefolgt von einem optionalen Hauptblock, der durch einen abschließenden Punkt `.` beendet wird.

### Deklarationen auf oberster Ebene

```text
declaration ::= variableDeclaration
              | functionDeclaration
              | classDeclaration
              | interfaceDeclaration
              | testDeclaration

variableDeclaration ::= "var" IDENT ":" type
```

### Anweisungen

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

### Ein-/Ausgabe und Daten

```text
writeLnStatement  ::= "WriteLn" "(" expressionList? ")"
readLnStatement   ::= "ReadLn" "(" IDENT ")"
showMenuStatement ::= "showMenu" "(" expressionList ")"
jsonParseStatement::= "JSON.parse" "(" expression ")" "into" IDENT
csvParseStatement ::= "CSV.parse"  "(" expression ")" "into" IDENT
```

### Ausdrücke

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

### Funktionen und Prozeduren

```text
functionDeclaration ::= "function" IDENT ("(" parameterList? ")")? ":" type block

parameterList ::= parameter ("," parameter)*
parameter     ::= IDENT (":" type)?
```

Parameter können typisiert oder untypisiert sein; das Trennzeichen ist das **Komma**. Siehe [Funktionen](../language/functions).

### Klassen und Interfaces

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

Felder sind standardmäßig privat; die Sichtbarkeitsmodifikatoren werden geparst und dokumentieren die Absicht, werden aber zur Laufzeit nicht streng durchgesetzt. Siehe [Klassen](../oop/classes) und [Interfaces](../oop/interfaces).

### Tests und Mocking

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

Siehe [Unit-Tests](../testing/unit-testing).

## Nächste Schritte

Um all diese Konstrukte in Aktion zu sehen, erkunden Sie die [Beispielgalerie](./examples).
