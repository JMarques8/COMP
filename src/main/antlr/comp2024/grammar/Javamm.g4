grammar Javamm;

@header {
    package pt.up.fe.comp2024;
}

EQUALS : '=' ;
DOT : '.' ;
COMMA : ',' ;
SEMI : ';' ;
ELLIPSIS : '...' ;
LCURLY : '{' ;
RCURLY : '}' ;
LBRACK : '[' ;
RBRACK : ']' ;
LPAREN : '(' ;
RPAREN : ')' ;
MUL : '*' ;
ADD : '+' ;
DIV : '/' ;  // Added lexer rule for division
SUB : '-' ;  // Added lexer rule for subtraction
NOT : '!' ;  // Added lexer rule for logical negation
AND : '&&' ; // Added lexer rule for logical AND
LT : '<' ;   // Added lexer rule for less than

IMPORT : 'import' ;
CLASS : 'class' ;
VOID : 'void' ;
INT : 'int' ;
BOOLEAN : 'boolean' ;
STRING : 'String' ;
PUBLIC : 'public' ;
MAIN : 'main' ;
STATIC : 'static' ;
RETURN : 'return' ;
THIS : 'this' ; // Added lexer rule for 'this'
EXTENDS : 'extends' ; //Added lexer rule for 'extends'
IF : 'if' ;
ELSE : 'else' ;
WHILE : 'while' ;
NEW : 'new' ;
LENGTH : 'length' ;

TRUE : 'true' ;
FALSE : 'false' ;
INTEGER : '0'|[1-9][0-9]* ; // Modified to allow multi-digit integers
ID : [a-zA-Z_$]([a-zA-Z0-9_$])* ;

WS : [ \t\n\r\f]+ -> skip ;
LINECOMMENT : '//' .*? '\n' -> skip ;
MULTICOMMENT : '/*' .*? '*/' -> skip;

program
    : (importDecl)* classDecl EOF // Updated to allow import declarations
    ;

importDecl
    : IMPORT name+=ID (DOT name+=ID)* SEMI // Added import declaration rule
    ;

classDecl
    : CLASS name=ID (EXTENDS superClass=ID)? // Added option to specify extends clause
        LCURLY
        varDecl*
        methodDecl*
        RCURLY
    ;

varDecl
    : type name=ID SEMI
    | type MAIN SEMI
    ;

type locals[boolean isArray=false, boolean varArgs=false]
    : name= INT LBRACK RBRACK {$isArray=true;} // Added support for int array types
    | name= INT ELLIPSIS {$varArgs=true;}
    | name= BOOLEAN
    | name= INT
    | name= STRING
    | name= VOID
    | name= ID
    ;

methodDecl locals[boolean isPublic=false]
    : (PUBLIC {$isPublic=true;})?
        type name=ID
        LPAREN (param (COMMA param)*)? RPAREN
        LCURLY varDecl* stmt* RETURN expr SEMI RCURLY
    | (PUBLIC {$isPublic=true;})? STATIC
        VOID name=MAIN
        LPAREN STRING LBRACK RBRACK args=ID RPAREN
        LCURLY varDecl* stmt* RCURLY
    ;

param
    : type name=ID
    ;

stmt
    : LCURLY stmt* RCURLY #BracketStmt //
    | IF LPAREN expr RPAREN stmt ELSE stmt #IfElseStmt //
    | WHILE LPAREN expr RPAREN stmt #WhileStmt //
    | expr EQUALS expr SEMI #AssignStmt //
    | expr SEMI #DeclarationStmt
    ;

expr
    : expr op= MUL expr #BinaryExpr //
    | expr op= DIV expr #BinaryExpr // Added division expression
    | expr op= ADD expr #BinaryExpr //
    | expr op= SUB expr #BinaryExpr // Added subtraction expression
    | expr op= LT expr #BinaryExpr // Added less than expression
    | expr op= AND expr #BinaryExpr // Added logical AND expression
    | op= NOT expr #UnaryExpr // Added logical negation expression
    | value= INTEGER #IntegerLiteral //
    | name= ID #VarRefExpr //
    | name= THIS #ThisExpr //
    | name= TRUE #BoolExpr //
    | name= FALSE #BoolExpr //
    | expr LBRACK expr RBRACK #ArrayAccessExpr// Added array access production rule
    | expr DOT LENGTH #ArrayLengthExpr//
    | THIS (DOT name= ID)? #ThisRefExpr// Added 'this' reference expression
    | expr DOT name= ID #FieldAccessExpr // Added for field access
    | expr DOT ID LPAREN (expr (COMMA expr)*)? RPAREN #MethodCallExpr //
    | NEW INT LBRACK expr RBRACK #NewArrayExpr //
    | NEW ID LPAREN RPAREN #NewClassExpr //
    | LPAREN expr RPAREN #ParensExpr //
    | LBRACK (expr (COMMA expr)*)? RBRACK #ArrayInit
    | expr DOT member=ID LPAREN (expr (COMMA expr)*)? RPAREN #MemberCall
    ;





