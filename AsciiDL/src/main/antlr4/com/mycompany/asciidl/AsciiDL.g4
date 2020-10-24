grammar AsciiDL;

/*Lexemas*/

/*palavras chave */

Start: 'start';
End: 'end';
Output: 'output';
Macro: 'macro';
Inteiro: 'inteiro';
SetBackground: 'setbackground';
Add: 'add';
To: 'to';
Print: 'print';
Println: 'println';
Printcenter: 'printcenter';
Printendln: 'printendln';
On: 'on';
Times: 'times';
For: 'for';
Endfor: 'endfor';
In: 'in';


/* simbolos */

Delim: ':';
Virgula: ',';

/* numeros */

Num_Inteiro: ('0'..'9')+;

/*nomes de variáveis*/

Identificador: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

/* strings e chars */

Caractere: '"' ~('"'|'\\'|'\n') '"';
String: '"' (~('"'|'\n') )* '"';

/*whitespace e comentarios*/

Comentario: '//' ~('\n'|'\r'|'}')* '\r'? '\n' {skip();};
Ws: ( ' ' | '\t' | '\r' | '\n') {skip();};


/*Simbolos Aritmeticos*/

AbrePar: '(';
FechaPar: ')';
AbreCol: '[';
FechaCol: ']';
Soma: '+';
Sub: '-';
Mult: '*';
Div: '/';
Atrib: '=';

/* Regras pra reconhecer erros lexicos */

StringSemFechar: '"' (~('"'|'\n'))*;
CaractereInvalido: .;



/*REGRAS SINTATICAS*/

programa: 'start' corpo 'end';

corpo: (comando)*;

comando: cmdDeclaracao | 
         cmdAtribuicao |
         cmdSetbg      |
         cmdAdd        |
         cmdPrint      |
         cmdFor;

cmdDeclaracao: decl_var |
             decl_output;

decl_var: tipo_var Identificador (',' Identificador)*; 

tipo_var: 'macro' |
          'inteiro';

decl_output: 'output' Identificador (',' Identificador)* (dimensao)?;

dimensao: '[' expressao ':' expressao ']';

cmdAtribuicao: Identificador '=' (cadeia | expressao);

cadeia: Caractere |
        String |
        '(' expressao ',' (Caractere | String | Identificador) ')';

cmdSetbg: 'setbackground' Identificador ',' Caractere;

cmdAdd: 'add' (cadeia | Identificador) 'to' Identificador;

cmdPrint : impressor (cadeia | Identificador) 'on' Identificador (expressao 'times')?;

impressor: 'print' |
           'println' |
           'printcenter' |
           'printendln';

cmdFor: 'for' Identificador 'in' dimensao (comando)* 'endfor';

expressao: termo (op1 termo)*;

termo: fator (op2 fator)*;

fator:  Identificador |
        Num_Inteiro |
        '(' expressao ')';

op1: '+' | '-';

op2: '*' | '/';


