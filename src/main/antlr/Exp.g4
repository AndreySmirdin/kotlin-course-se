grammar Exp;

//
//eval returns [double value]
//    :    exp=additionExp {$value = $exp.value;}
//    ;
options {
    output=AST;
}

file: block;

block: (statement)*;

blockWithBraces: LeftBrace block RightBrace;

statement
    :   function
    |   variable
    |   expression
    |   while_
    |   if_
    |   assignment
    |   return_;

function: Fun Identifier LeftParen parameterNames RightParen blockWithBraces;

variable: Var Identifier (Equals expression)?;

parameterNames: (Identifier (Comma Identifier)*)?;

while_: While LeftParen expression RightParen blockWithBraces;

if_: If LeftParen expression RightParen blockWithBraces (Else blockWithBraces)?;

assignment: Identifier Equals expression;

return_: Return expression;

expression
    //:   functionCall
    :   binaryExpression;
    //|   Identifier
    //|   Number
    //|   LeftParen expression RightParen;

functionCall: Identifier LeftParen arguments RightParen;

arguments: (expression (Comma expression)*)?;

binaryExpression: logicalExp;

logicalExp
    :   comparisonExp (Or comparisonExp)*
    |   comparisonExp (And comparisonExp)*;

comparisonExp
    :   additionExp (NotEquals additionExp)*
    |   additionExp (IsEqual additionExp)*
    |   additionExp (LessEqual additionExp)*
    |   additionExp (GreaterEqual additionExp)*
    |   additionExp (Less additionExp)*
    |   additionExp (Greater additionExp)*;


additionExp
    :   multiplyExp (Plus multiplyExp)*
    |   multiplyExp (Minus multiplyExp)*;

multiplyExp
    :   atomExp (Mult atomExp)*
    //|   atomExp (Div atomExp)*
    |   atomExp (Mod expression)*;

atomExp: Number | Identifier | LeftParen expression RightParen | functionCall;

//////////////////////////////////////////////////////////////////////

While: 'while';
Fun: 'fun';
Return: 'return';
Var: 'var';
If: 'if';
Else: 'else';

LeftParen : '(';
RightParen : ')';
LeftBrace : '{';
RightBrace : '}';

IsEqual: '==';
NotEquals: '!=';
Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';

Plus: '+';
Minus: '-';
Mult : '*';
Div : '/';
Mod : '%';

Or: '||';
And: '&&';

Equals: '=';

Number: ('0'..'9')+ ('.' ('0'..'9')+)?;

Identifier:   [a-zA-Z_0-9]+;

Comma : ',';

WS : (' ' | '\t' | '\r'| '\n') -> skip;