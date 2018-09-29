grammar Exp;


file: block EOF;

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
    :   binaryExpression
    |   functionCall
    |   Number
    |   Identifier
    |   LeftParen expression RightParen;

functionCall: Identifier LeftParen arguments RightParen;

arguments: (expression (Comma expression)*)?;

binaryExpression: logicalExp;

logicalExp: comparisonExp ((Or | And) comparisonExp)*;

comparisonExp: additionExp ((NotEquals
                           | IsEqual
                           | LessEqual
                           | GreaterEqual
                           | Less
                           | Greater) additionExp)?;


additionExp: multiplyExp ((Plus | Minus) multiplyExp)*;

multiplyExp: atomExp ((Mult | Div | Mod) atomExp)*;

atomExp: functionCall | Number | Identifier | LeftParen expression RightParen;

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

Number: '0' | Minus? ('1'..'9') ('0'..'9')*;

Identifier:   [a-zA-Z_0-9]+;

Comma : ',';

WS : (' ' | '\t' | '\r'| '\n' | '//' .*? ('\n'|EOF)) -> skip;