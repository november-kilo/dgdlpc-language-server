grammar LPC;

program
    : (preprocessorDirective | programElement)* EOF
    ;

programElement
    : variableDeclaration
    | functionDeclaration
    | inheritDeclaration
    | statement
    ;

preprocessorDirective
    : includeDirective
    | defineDirective
    | ifdefDirective
    | ifndefDirective
    | elseDirective
    | endifDirective
    | undefDirective
    ;

includeDirective
    : INCLUDE
    ;

defineDirective   : DEFINE;
ifdefDirective    : IFDEF;
ifndefDirective   : IFNDEF;
elseDirective     : ELSEDIR;
endifDirective    : ENDIF;
undefDirective    : UNDEF;

// Lexer rules for preprocessor
INCLUDE     : '#' WS* 'include' WS* (('<' ~[>\r\n]* '>' | '"' ~["\r\n]* '"') | IDENTIFIER);
DEFINE      : '#' WS* 'define' WS* ~[\r\n]*;
IFDEF       : '#' WS* 'ifdef' WS* ~[\r\n]*;
IFNDEF      : '#' WS* 'ifndef' WS* ~[\r\n]*;
ELSEDIR     : '#' WS* 'else' WS*;
ENDIF       : '#' WS* 'endif' WS*;
UNDEF       : '#' WS* 'undef' WS* ~[\r\n]*;

inheritDeclaration
    : PRIVATE? INHERIT IDENTIFIER? stringExpression ';'
    ;

declaration
    : functionDeclaration
    | inheritDeclaration
    | variableDeclaration
    ;

functionDeclaration
    : modifiers? typeSpecifier functionDeclarator (block | ';')
    ;

functionDeclarator
    : (IDENTIFIER | operatorName) '(' formalParameters? ')'
    ;

operatorName
    : OPERATOR
      ( '+' | '-' | '*' | '/' | '%'
      | '&' | '^' | '|' | '~'
      | '<' | '>' | '>=' | '<='
      | '<<' | '>>'
      | '++' | '--'
      | '[' ']'
      | '[' ']' '='
      | '[' '..' ']'
      )
    ;

formalParameters
    : VOID
    | parameterList (',' '...')?
    ;

parameterList
    : parameterDeclaration (',' parameterDeclaration)*
    ;

parameterDeclaration
    : VARARGS? typeSpecifier IDENTIFIER ('...')?
    | VARARGS? typeSpecifier IDENTIFIER
    ;

variableDeclaration
    : modifiers? typeSpecifier variableDeclarators ';'
    ;

variableDeclarators
    : IDENTIFIER (',' IDENTIFIER)*
    ;

typeSpecifier
    : (VOID | INT | FLOAT | STRING | OBJECT | MAPPING | MIXED | IDENTIFIER) arraySpecifier?
    ;

arraySpecifier
    : '*'+
    ;


modifiers
    : modifier+
    ;

modifier
    : PRIVATE
    | STATIC
    | ATOMIC
    | NOMASK
    | VARARGS
    ;

block
    : '{' statement* '}'
    ;


statement
    : variableDeclaration
    | block
    | expressionStatement
    | selectionStatement
    | iterationStatement
    | jumpStatement
    | tryStatement
    | catchStandAloneStatement
    | rlimitsStatement
    | labeledStatement
    ;

selectionStatement
    : IF '(' expression ')' statement (ELSE statement)?
    | SWITCH '(' expression ')' '{' switchSection* '}'
    ;

switchSection
    : switchLabel+ statement+
    ;

switchLabel
    : CASE expression '..' expression ':'    // Range case
    | CASE expression ':'                    // Single case
    | DEFAULT ':'
    ;

iterationStatement
    : WHILE '(' expression ')' statement
    | DO statement WHILE '(' expression ')' ';'
    | FOR '(' forControl ')' statement
    ;

forControl
    : (expression)? ';' (expression)? ';' (expression)?
    ;

tryStatement
    : TRY block catchStatement
    ;

catchStatement
    : CATCH block
    | CATCH '(' '...' ')' block
    | CATCH '(' IDENTIFIER ')' block
    ;

catchStandAloneStatement
    : CATCH block
    ;

rlimitsStatement
    : RLIMITS '(' expression ';' expression ')' block
    ;

labeledStatement
    : IDENTIFIER ':' statement
    | CASE expression ':' statement
    | DEFAULT ':' statement
    ;

expressionStatement
    : expression? ';'
    ;

jumpStatement
    : (BREAK | CONTINUE | GOTO IDENTIFIER | RETURN expression?) ';'
    ;

expression
    : primary                                                      #primaryExpression
    | expression '(' expressionList? ')'                          #functionCall
    | expression '->' IDENTIFIER '(' expressionList? ')'          #methodCall
    | expression '<-' stringExpression                            #instanceOf
    | NEW IDENTIFIER ('(' expressionList? ')')?                   #newObject
    | '(' typeSpecifier '*'* ')' expression                      #castExpression
    | expression '[' expression ']'                               #arrayAccess
    | expression '[' expression? '..' expression? ']'             #rangeAccess
    | ('++' | '--') expression                                    #prefixIncDec
    | expression ('++' | '--')                                    #postfixIncDec
    | ('+' | '-' | '~' | '!') expression                         #unaryExpression
    | expression ('*' | '/' | '%') expression                     #multiplicative
    | expression ('+' | '-') expression                          #additive
    | expression ('<<' | '>>') expression                        #shift
    | expression ('<' | '>' | '<=' | '>=') expression            #relational
    | expression ('==' | '!=') expression                        #equality
    | expression '&' expression                                   #bitwiseAnd
    | expression '^' expression                                   #bitwiseXor
    | expression '|' expression                                   #bitwiseOr
    | expression '&&' expression                                  #logicalAnd
    | expression '||' expression                                  #logicalOr
    | expression '?' expression ':' expression                    #conditional
    | expression
      ('=' | '+=' | '-=' | '*=' | '/=' | '%=' | '&=' | '^=' | '|=' | '<<=' | '>>=')
      expression                                                  #assignment
    | CATCH '(' expression ')'                                    #catchExpression
    ;

primary
    : '(' expression ')'
    | literal
    | IDENTIFIER
    | '::' IDENTIFIER
    | IDENTIFIER '::' IDENTIFIER
    ;

literal
    : NIL
    | INTEGER
    | FLOAT_LITERAL
    | STRING_LITERAL
    | CHAR_LITERAL
    | '(' '{' expressionList? '}' ')'
    | '(' '[' mappingList? ']' ')'
    ;

stringExpression
    : STRING_LITERAL
    | '(' stringExpression ')'
    | stringExpression '+' STRING_LITERAL
    ;

expressionList
    : expression (',' expression)*
    ;

mappingList
    : mappingEntry (',' mappingEntry)*
    ;

mappingEntry
    : expression ':' expression
    ;


// Lexer rules
INHERIT     : 'inherit';
PRIVATE     : 'private';
STATIC      : 'static';
ATOMIC      : 'atomic';
NOMASK      : 'nomask';
VARARGS     : 'varargs';
OPERATOR    : 'operator';
VOID        : 'void';
INT         : 'int';
FLOAT       : 'float';
STRING      : 'string';
OBJECT      : 'object';
MAPPING     : 'mapping';
MIXED       : 'mixed';
IF          : 'if';
ELSE        : 'else';
SWITCH      : 'switch';
CASE        : 'case';
DEFAULT     : 'default';
WHILE       : 'while';
DO          : 'do';
FOR         : 'for';
BREAK       : 'break';
CONTINUE    : 'continue';
RETURN      : 'return';
NEW         : 'new';
TRY         : 'try';
CATCH       : 'catch';
RLIMITS     : 'rlimits';
GOTO        : 'goto';
NIL         : 'nil';

INTEGER
    : DecimalNumber
    | OctalNumber
    | HexNumber
    ;

fragment DecimalNumber
    : [1-9][0-9]*
    | '0'
    ;

fragment OctalNumber
    : '0' [0-7]+
    ;

fragment HexNumber
    : '0' [xX] [0-9a-fA-F]+
    ;

IDENTIFIER
    : [a-zA-Z_] [a-zA-Z0-9_]*
    ;


FLOAT_LITERAL: [0-9]+ '.' [0-9]* EXPONENT?
             | '.' [0-9]+ EXPONENT?
             | [0-9]+ EXPONENT;
CHAR_LITERAL: '\'' ( ~['\\\r\n] | ESCAPE_SEQUENCE ) '\'';
STRING_LITERAL: '"' ( ~["\\\r\n] | ESCAPE_SEQUENCE )* '"';

fragment ESCAPE_SEQUENCE
    : '\\' [btnfr"'\\]
    | '\\' ([0-3]? [0-7])? [0-7]
    | '\\' 'x' HEX_DIGIT HEX_DIGIT
    ;

fragment EXPONENT
    : [eE] [+-]? [0-9]+
    ;

fragment HEX_DIGIT
    : [0-9a-fA-F]
    ;

WS          : [ \t\r\n\f]+            -> skip;
COMMENT     : '/*' .*? '*/'           -> skip;
LINE_COMMENT: '//' ~[\r\n]*          -> skip;
