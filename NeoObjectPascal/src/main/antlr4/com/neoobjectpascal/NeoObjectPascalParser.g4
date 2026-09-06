parser grammar NeoObjectPascalParser;

options { tokenVocab=NeoObjectPascalLexer; }

// Parser rules
program: (usesClause SEMI)? (declaration SEMI)* (block DOT)? EOF;

declaration:
      variableDeclaration
    | functionDeclaration
    | classDeclaration
    | interfaceDeclaration
    | testDeclaration
    ;

usesClause: USES modulePath (COMMA modulePath)*;

modulePath: IDENTIFIER (DOT IDENTIFIER)*;

block: BEGIN (statement SEMI)* END;

statement:
    variableDeclaration
    | arrayElementAssignment
    | assignment
    | methodCall
    | call
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
    | block;

variableDeclaration: VAR identifier COLON type;

assignment: (identifier | memberAccess) ASSIGN expression;

arrayElementAssignment: identifier LBRACKET expression RBRACKET ASSIGN expression;

call: identifier (LPAREN expressionList? RPAREN)?;

functionDeclaration: FUNCTION identifier (LPAREN parameterList? RPAREN )? COLON type block;

parameterList: parameter (COMMA parameter)*;

parameter: identifier (COLON type)?;

ifStatement: IF expression THEN statement (ELSE statement)?;

whileStatement: WHILE expression DO statement;

forStatement: FOR identifier ASSIGN expression TO expression DO statement;

forInStatement: FOR identifier IN expression DO statement;

tryStatement:
    TRY block
    CATCH LPAREN identifier RPAREN block
    (FINALLY block)?;

raiseStatement: RAISE expression;

writeLnStatement: WRITELN LPAREN expressionList? RPAREN;

readLnStatement: READLN LPAREN identifier RPAREN;

showMenuStatement: SHOWMENU LPAREN expressionList RPAREN;

jsonParseStatement: JSON_PARSE LPAREN expression RPAREN INTO identifier;

csvParseStatement: CSV_PARSE LPAREN expression RPAREN INTO identifier;

returnStatement: RETURN expression;

expressionList: expression (COMMA expression)*;

// Operator precedence: first listed = highest precedence among left-recursive alternatives.
// Non-left-recursive alternatives (atoms + unary) always bind tighter than binary operators.
expression:
    javaBlock
    | newExpression
    | methodCall
    | memberAccess
    | NOT expression
    | SUB expression
    | arrayLiteral
    | primary
    | expression LBRACKET expression RBRACKET
    | expression (MUL | DIV) expression
    | expression (ADD | SUB) expression
    | expression (EQUAL | NOT_EQUAL | LT | GT | LTE | GTE) expression
    | expression AND expression
    | expression OR expression
    | expression PIPE expression;

primary:
    INTEGER
    | REAL_NUM
    | STRING
    | SELF
    | recordLiteral
    | identifier
    | LPAREN expression RPAREN
    | call;

arrayLiteral: LBRACKET expressionList? RBRACKET;

recordLiteral: HASH_LBRACE (recordEntry (COMMA recordEntry)*)? RBRACE;

recordEntry: (identifier | STRING) COLON expression;

newExpression: NEW identifier LPAREN expressionList? RPAREN;

memberAccess: (identifier | SELF) (DOT identifier)+;

methodCall: memberAccess LPAREN expressionList? RPAREN;

type: TYPE_INTEGER | TYPE_STRING | TYPE_BOOLEAN | TYPE_REAL | TYPE_DOUBLE | TYPE_FLOAT
    | TYPE_DATE | TYPE_TIME | TYPE_DATETIME | TYPE_CURRENCY | TYPE_OBJECT | ARRAY | identifier;

identifier: IDENTIFIER;

javaBlock: JAVA COLON LPAREN expressionList? RPAREN JAVA_CODE;

// Class Declaration
classDeclaration:
    CLASS identifier
    (EXTENDS identifier)?
    (IMPLEMENTS identifier (COMMA identifier)*)?
    classBody
    END;

classBody: (classMember SEMI)*;

classMember:
      fieldDeclaration
    | methodDeclaration
    | constructorDeclaration
    | procedureDeclaration;

fieldDeclaration: VAR identifier COLON type;

methodDeclaration:
    (PUBLIC | PRIVATE | PROTECTED)?
    (VIRTUAL | OVERRIDE)?
    FUNCTION identifier LPAREN parameterList? RPAREN COLON type
    block;

procedureDeclaration:
    (PUBLIC | PRIVATE | PROTECTED)?
    (VIRTUAL | OVERRIDE)?
    PROCEDURE identifier LPAREN parameterList? RPAREN
    block;

constructorDeclaration:
    CONSTRUCTOR identifier LPAREN parameterList? RPAREN
    block;

// Interface Declaration
interfaceDeclaration:
    INTERFACE identifier
    (methodSignature SEMI)*
    END;

methodSignature:
    FUNCTION identifier LPAREN parameterList? RPAREN COLON type;

// Test Declaration
testDeclaration:
    TEST STRING
    block;

expectStatement:
    EXPECT LPAREN expression RPAREN DOT
    (TO_BE LPAREN expression RPAREN
    | TO_EQUAL LPAREN expression RPAREN
    | TO_BE_TRUE LPAREN RPAREN
    | TO_BE_FALSE LPAREN RPAREN
    | TO_BE_NULL LPAREN RPAREN);

mockStatement:
    MOCK identifier identifier THEN_RETURN expression
    | MOCK identifier DOT identifier THEN_RETURN expression;

verifyStatement:
    VERIFY identifier identifier
    | VERIFY identifier DOT identifier;
