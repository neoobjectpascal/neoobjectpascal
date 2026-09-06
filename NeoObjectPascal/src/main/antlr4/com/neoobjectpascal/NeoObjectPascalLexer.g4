lexer grammar NeoObjectPascalLexer;

// Keywords
USES: 'uses';
BEGIN: 'begin';
END: 'end';
VAR: 'var';
FUNCTION: 'function';
IF: 'if';
THEN: 'then';
ELSE: 'else';
WHILE: 'while';
DO: 'do';
FOR: 'for';
TO: 'to';
IN: 'in';
WRITELN: 'WriteLn';
READLN: 'ReadLn';
SHOWMENU: 'showMenu';
JSON_PARSE: 'JSON.parse';
CSV_PARSE: 'CSV.parse';
INTO: 'into';
RETURN: 'return';
JAVA: 'java';

// Boolean operators
AND: 'and';
OR: 'or';
NOT: 'not';

// Error handling
TRY: 'try';
CATCH: 'catch';
FINALLY: 'finally';
RAISE: 'raise';

// Array
ARRAY: 'array';

// OO Keywords
CLASS: 'class';
INTERFACE: 'interface';
EXTENDS: 'extends';
IMPLEMENTS: 'implements';
PUBLIC: 'public';
PRIVATE: 'private';
PROTECTED: 'protected';
VIRTUAL: 'virtual';
OVERRIDE: 'override';
CONSTRUCTOR: 'constructor';
DESTRUCTOR: 'destructor';
SELF: 'self';
NEW: 'new';
PROCEDURE: 'procedure';

// Test Keywords
TEST: 'test';
DESCRIBE: 'describe';
IT: 'it';
EXPECT: 'expect';
TO_BE: 'toBe';
TO_EQUAL: 'toEqual';
TO_BE_TRUE: 'toBeTrue';
TO_BE_FALSE: 'toBeFalse';
TO_BE_NULL: 'toBeNull';
MOCK: 'mock';
WHEN_CALL: 'whenCall';
THEN_RETURN: 'thenReturn';
VERIFY: 'verify';
ASSERT: 'assert';

// Types
TYPE_INTEGER: 'Integer';
TYPE_STRING: 'String';
TYPE_BOOLEAN: 'Boolean';
TYPE_REAL: 'Real';
TYPE_DOUBLE: 'Double';   // alias of Real
TYPE_FLOAT: 'Float';     // alias of Real
TYPE_DATETIME: 'DateTime';
TYPE_DATE: 'Date';
TYPE_TIME: 'Time';
TYPE_CURRENCY: 'Currency';
TYPE_OBJECT: 'Object';

// Operators and Punctuation
ASSIGN: ':=';
COLON: ':';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
// Record literals: '#{ key: value }'. The '#{' opener is distinct from JAVA_CODE's '{',
// and a standalone '}' (RBRACE) only ever closes a record — JAVA_CODE captures its own '}'
// inside a single token, so there is no collision with java:(){} blocks.
HASH_LBRACE: '#{';
RBRACE: '}';
SEMI: ';';
DOT: '.';
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
EQUAL: '=';
NOT_EQUAL: '<>';
LT: '<';
GT: '>';
LTE: '<=';
GTE: '>=';
PIPE: '|>';

// Numeric tokens — REAL_NUM must precede INTEGER (longest match wins)
REAL_NUM: [0-9]+ '.' [0-9]+;
INTEGER: [0-9]+;
STRING:
    '"' ( '\\' . | ~('"'|'\\') )* '"' |
    '\'' ( '\\' . | ~('\''|'\\') )* '\''
    ;
JAVA_CODE: '{' ( ~[{}] | '{' ~[{}]* '}' )* '}';
IDENTIFIER: [a-zA-Z_] [a-zA-Z_0-9]*;

WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;
