# Riferimento del linguaggio

Questa pagina raccoglie, in forma condensata, le **parole chiave**, i **tipi**, gli **operatori** (con la precedenza) e un **riepilogo grammaticale** di NeoObjectPascal. Serve come consultazione rapida — per spiegazioni passo passo, segui i link alle pagine della guida.

::: info Fonte di verità
Tutto il contenuto di questa pagina deriva direttamente dalla grammatica ANTLR (`NeoObjectPascalLexer.g4` e `NeoObjectPascalParser.g4`) e dall'interprete. Dove la grammatica e la vecchia documentazione divergono, vince la grammatica.
:::

## Parole chiave

Le parole chiave elencate di seguito sono riservate dal lexer. Sono raggruppate per finalità.

| Gruppo | Parole chiave |
| --- | --- |
| Moduli | `uses`, `into` |
| Blocchi e variabili | `begin`, `end`, `var` |
| Sottoprogrammi | `function`, `procedure`, `return`, `constructor` |
| Controllo del flusso | `if`, `then`, `else`, `while`, `do`, `for`, `to`, `in` |
| I/O | `WriteLn`, `ReadLn`, `showMenu` |
| Booleani / logici | `and`, `or`, `not` |
| Gestione degli errori | `try`, `catch`, `finally`, `raise` |
| Collezioni | `array` |
| Orientamento agli oggetti | `class`, `interface`, `extends`, `implements`, `public`, `private`, `protected`, `virtual`, `override`, `self`, `new` |
| Java incorporato | `java` |
| Parsing dei dati | `JSON.parse`, `CSV.parse` |
| Test | `test`, `expect`, `toBe`, `toEqual`, `toBeTrue`, `toBeFalse`, `toBeNull` |
| Mocking | `mock`, `thenReturn`, `verify` |

::: tip Booleani letterali
`true` e `false` non sono parole chiave riservate, ma sono riconosciuti come letterali booleani (compresi `True`/`TRUE`, senza distinzione tra maiuscole e minuscole).
:::

Vedi l'uso pratico in [Operatori](../language/operators), [Classi](../oop/classes), [Librerie interne](../features/internal-libraries) e [Test unitari](../testing/unit-testing).

## Tipi

NeoObjectPascal ha sei tipi dichiarabili:

| Tipo | Descrizione | Esempio di letterale |
| --- | --- | --- |
| `Integer` | Numero intero | `42`, `0`, `-7` |
| `Real` | Numero in virgola mobile (double) | `3.14`, `5.0` |
| `String` | Testo | `"olá"`, `'mundo'` |
| `Boolean` | Vero/falso | `true`, `false` |
| `Array` | Lista di elementi, base 0 | `[10, 20, 30]`, `[]` |
| `Object` | Generico — contiene istanze, risultati di `JSON.parse`/`CSV.parse` | — |

::: warning I letterali Real richiedono cifre da entrambi i lati
Un numero reale richiede cifre prima **e** dopo il punto: `5.0` è valido, `5.` no. Un `Integer` viene promosso automaticamente a `Real` nell'aritmetica mista o quando viene assegnato a una variabile `Real`.
:::

## Operatori

La tabella elenca gli operatori dalla precedenza **più alta** alla **più bassa**. Gli operatori sulla stessa riga hanno la stessa precedenza e associano a sinistra.

| Precedenza | Operatore/i | Descrizione |
| --- | --- | --- |
| 1 (più alta) | `a[i]` | Indicizzazione di array (base 0) |
| 2 | `*`  `/` | Moltiplicazione, divisione |
| 3 | `+`  `-` | Addizione/sottrazione; `+` concatena anche le stringhe |
| 4 | `=`  `<>`  `<`  `>`  `<=`  `>=` | Confronti |
| 5 | `and` | E logico |
| 6 | `or` | O logico |
| 7 (più bassa) | `\|>` | Pipe (passa il valore a sinistra come 1º argomento della funzione a destra) |
| unario | `not expr`  `-expr` | Negazione logica / negazione numerica (legano più forte dei binari) |

::: warning `=` è uguaglianza, `:=` è assegnazione
A differenza di C e dei suoi derivati, l'uguaglianza usa un **singolo** `=`. L'assegnazione usa `:=`. Il "diverso da" è `<>` (non `!=`).
:::

Esempi:

```npas
begin
  WriteLn(10 = 10);          // true  (igualdade)
  WriteLn(10 <> 5);          // true  (diferente)
  WriteLn(2 + 3 * 4);        // 14    (* antes de +)
  WriteLn("Neo" + "Pascal"); // NeoPascal (concatenação)
  WriteLn(5 |> square);      // 25    (pipe)
end.
```

Dettagli ed esempi completi in [Operatori](../language/operators) e [Programmazione funzionale](../features/functional).

## Commenti

Esiste **solo** il commento di riga `//`. Non ci sono commenti di blocco: le parentesi graffe `{ }` sono riservate ai [blocchi di codice Java](../features/java-integration).

```npas
begin
  // isto é um comentário de linha
  WriteLn("Oi"); // também funciona no fim da linha
end.
```

::: warning Niente `program` e niente `inherited`
NeoObjectPascal **non** ha un'intestazione `program Nome;` — il file inizia direttamente dalla clausola `uses`, dalle dichiarazioni o dal blocco `begin`. Inoltre **non** esiste la parola chiave `inherited`.
:::

## Riepilogo della grammatica

I blocchi seguenti descrivono, in notazione EBNF-like, i principali costrutti del linguaggio, fedeli alla grammatica del parser. `?` = opzionale, `*` = zero o più, `|` = alternativa.

### Programma e blocchi

```text
program   ::= (usesClause ";")? (declaration ";")* (block ".")? EOF

usesClause ::= "uses" modulePath ("," modulePath)*
modulePath ::= IDENT ("." IDENT)*

block     ::= "begin" (statement ";")* "end"
```

Un programma è: una clausola `uses` opzionale, seguita da zero o più dichiarazioni, seguita da un blocco principale opzionale chiuso da un punto finale `.`.

### Dichiarazioni di livello superiore

```text
declaration ::= variableDeclaration
              | functionDeclaration
              | classDeclaration
              | interfaceDeclaration
              | testDeclaration

variableDeclaration ::= "var" IDENT ":" type
```

### Istruzioni

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

### I/O e dati

```text
writeLnStatement  ::= "WriteLn" "(" expressionList? ")"
readLnStatement   ::= "ReadLn" "(" IDENT ")"
showMenuStatement ::= "showMenu" "(" expressionList ")"
jsonParseStatement::= "JSON.parse" "(" expression ")" "into" IDENT
csvParseStatement ::= "CSV.parse"  "(" expression ")" "into" IDENT
```

### Espressioni

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

### Funzioni e procedure

```text
functionDeclaration ::= "function" IDENT ("(" parameterList? ")")? ":" type block

parameterList ::= parameter ("," parameter)*
parameter     ::= IDENT (":" type)?
```

I parametri possono essere tipizzati o meno; il separatore è la **virgola**. Vedi [Funzioni](../language/functions).

### Classi e interfacce

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

I campi sono privati per impostazione predefinita; i modificatori di visibilità vengono analizzati e documentano l'intenzione, ma non sono rigorosamente imposti in fase di esecuzione. Vedi [Classi](../oop/classes) e [Interfacce](../oop/interfaces).

### Test e mocking

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

Vedi [Test unitari](../testing/unit-testing).

## Prossimi passi

Per vedere tutti questi costrutti in azione, esplora la [Galleria di esempi](./examples).
