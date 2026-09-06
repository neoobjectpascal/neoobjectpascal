# Référence du langage

Cette page réunit, de manière condensée, les **mots-clés**, les **types**, les **opérateurs** (avec leur précédence) et un **résumé grammatical** de NeoObjectPascal. Elle sert de consultation rapide — pour des explications pas à pas, suivez les liens vers les pages de guide.

::: info Source de vérité
Tout le contenu de cette page est dérivé directement de la grammaire ANTLR (`NeoObjectPascalLexer.g4` et `NeoObjectPascalParser.g4`) et de l'interpréteur. Là où la grammaire et l'ancienne documentation divergent, la grammaire l'emporte.
:::

## Mots-clés

Les mots-clés ci-dessous sont réservés par le lexer. Ils sont regroupés par finalité.

| Groupe | Mots-clés |
| --- | --- |
| Modules | `uses`, `into` |
| Blocs et variables | `begin`, `end`, `var` |
| Sous-routines | `function`, `procedure`, `return`, `constructor` |
| Contrôle de flux | `if`, `then`, `else`, `while`, `do`, `for`, `to`, `in` |
| E/S | `WriteLn`, `ReadLn`, `showMenu` |
| Booléens / logiques | `and`, `or`, `not` |
| Gestion des erreurs | `try`, `catch`, `finally`, `raise` |
| Collections | `array` |
| Orientation objet | `class`, `interface`, `extends`, `implements`, `public`, `private`, `protected`, `virtual`, `override`, `self`, `new` |
| Java embarqué | `java` |
| Parsing de données | `JSON.parse`, `CSV.parse` |
| Tests | `test`, `expect`, `toBe`, `toEqual`, `toBeTrue`, `toBeFalse`, `toBeNull` |
| Mocking | `mock`, `thenReturn`, `verify` |

::: tip Littéraux booléens
`true` et `false` ne sont pas des mots-clés réservés, mais ils sont reconnus comme des littéraux booléens (y compris `True`/`TRUE`, sans distinction de casse).
:::

Voir l'usage pratique dans [Opérateurs](../language/operators), [Classes](../oop/classes), [Bibliothèques internes](../features/internal-libraries) et [Tests unitaires](../testing/unit-testing).

## Types

NeoObjectPascal comporte six types déclarables :

| Type | Description | Exemple de littéral |
| --- | --- | --- |
| `Integer` | Nombre entier | `42`, `0`, `-7` |
| `Real` | Nombre à virgule flottante (double) | `3.14`, `5.0` |
| `String` | Texte | `"olá"`, `'mundo'` |
| `Boolean` | Vrai/faux | `true`, `false` |
| `Array` | Liste d'éléments, base 0 | `[10, 20, 30]`, `[]` |
| `Object` | Générique — contient des instances, des résultats de `JSON.parse`/`CSV.parse` | — |

::: warning Les littéraux Real ont besoin de chiffres des deux côtés
Un nombre réel a besoin de chiffres avant **et** après le point : `5.0` est valide, `5.` ne l'est pas. Un `Integer` est automatiquement promu en `Real` dans une arithmétique mixte ou lorsqu'il est affecté à une variable `Real`.
:::

## Opérateurs

Le tableau liste les opérateurs de la précédence la **plus haute** à la **plus basse**. Les opérateurs d'une même ligne ont une précédence égale et s'associent à gauche.

| Précédence | Opérateur(s) | Description |
| --- | --- | --- |
| 1 (la plus haute) | `a[i]` | Indexation de tableau (base 0) |
| 2 | `*`  `/` | Multiplication, division |
| 3 | `+`  `-` | Addition/soustraction ; `+` concatène aussi les chaînes |
| 4 | `=`  `<>`  `<`  `>`  `<=`  `>=` | Comparaisons |
| 5 | `and` | ET logique |
| 6 | `or` | OU logique |
| 7 (la plus basse) | `\|>` | Pipe (passe la valeur de gauche comme 1er argument de la fonction à droite) |
| unaire | `not expr`  `-expr` | Négation logique / négation numérique (lient plus fort que les binaires) |

::: warning `=` est l'égalité, `:=` est l'affectation
Contrairement à C et ses dérivés, l'égalité utilise un **unique** `=`. L'affectation utilise `:=`. Le « différent de » est `<>` (pas `!=`).
:::

Exemples :

```npas
begin
  WriteLn(10 = 10);          // true  (igualdade)
  WriteLn(10 <> 5);          // true  (diferente)
  WriteLn(2 + 3 * 4);        // 14    (* antes de +)
  WriteLn("Neo" + "Pascal"); // NeoPascal (concatenação)
  WriteLn(5 |> square);      // 25    (pipe)
end.
```

Détails et exemples complets dans [Opérateurs](../language/operators) et [Programmation fonctionnelle](../features/functional).

## Commentaires

Il n'existe **que** le commentaire de ligne `//`. Il n'y a pas de commentaires de bloc : les accolades `{ }` sont réservées aux [blocs de code Java](../features/java-integration).

```npas
begin
  // isto é um comentário de linha
  WriteLn("Oi"); // também funciona no fim da linha
end.
```

::: warning Pas de `program` ni d'`inherited`
NeoObjectPascal **n'a pas** d'en-tête `program Nome;` — le fichier commence directement par la clause `uses`, par les déclarations ou par le bloc `begin`. Le mot-clé `inherited` **n'existe pas** non plus.
:::

## Résumé de la grammaire

Les blocs ci-dessous décrivent, en notation de type EBNF, les principales constructions du langage, fidèles à la grammaire du parser. `?` = optionnel, `*` = zéro ou plus, `|` = alternative.

### Programme et blocs

```text
program   ::= (usesClause ";")? (declaration ";")* (block ".")? EOF

usesClause ::= "uses" modulePath ("," modulePath)*
modulePath ::= IDENT ("." IDENT)*

block     ::= "begin" (statement ";")* "end"
```

Un programme est : une clause `uses` optionnelle, suivie de zéro ou plusieurs déclarations, suivie d'un bloc principal optionnel terminé par un point final `.`.

### Déclarations de premier niveau

```text
declaration ::= variableDeclaration
              | functionDeclaration
              | classDeclaration
              | interfaceDeclaration
              | testDeclaration

variableDeclaration ::= "var" IDENT ":" type
```

### Instructions

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

### E/S et données

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

### Fonctions et procédures

```text
functionDeclaration ::= "function" IDENT ("(" parameterList? ")")? ":" type block

parameterList ::= parameter ("," parameter)*
parameter     ::= IDENT (":" type)?
```

Les paramètres peuvent être typés ou non ; le séparateur est la **virgule**. Voir [Fonctions](../language/functions).

### Classes et interfaces

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

Les champs sont privés par défaut ; les modificateurs de visibilité sont analysés et documentent l'intention, mais ne sont pas strictement imposés à l'exécution. Voir [Classes](../oop/classes) et [Interfaces](../oop/interfaces).

### Tests et mocking

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

Voir [Tests unitaires](../testing/unit-testing).

## Prochaines étapes

Pour voir toutes ces constructions en action, explorez la [Galerie d'exemples](./examples).
