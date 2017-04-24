/**
derived from http://svn.r-project.org/R/trunk/src/main/gram.y
http://cran.r-project.org/doc/manuals/R-lang.html#Parser
*/
grammar StockLang;


prog:   (expr_or_assign EOS)+ EOF;

expr_or_assign
    :   expr ASSN_OP expr_or_assign	      # expr_aop_exprAsn
    |   expr 							  # expr_only
	|	declare (ASSN_OP expr_or_assign)? # declaration
    ;

declare
	:	TYPE ID	# decl_id
	|	'Stock' STOCK # decl_stock
	|   'function' ID '(' sublist? ')' '{' exprlist 	# fxn_def // define function
	;
	

expr:   expr '[[' sublist ']' ']'	# expr_sub_sublist // '[[' follows R's yacc grammar
    |   expr '[' sublist ']'		# expr_sublist
    |   <assoc=right> expr '^' expr # exponent
    |   op=('-'|'+') expr				# pos_neg
    |   expr op=('*'|'/') expr			# mul_div
    |   expr op=('+'|'-') expr			# add_sub
    |   expr op=('>'|'>='|'<'|'<='|'=='|'!=') expr # bool_op
    |   '!' expr					# not
    |   expr ('&'|'&&') expr		# and
    |   expr ('|'|'||') expr		# or
    |   'return' expr_or_assign				# fxn_ret // return result
    |   expr '(' sublist ')'                # params // call function
    |   '{' exprlist 						# compoundStatement // compound statement
    |   'if' '(' expr ')' expr_or_assign	# if
    |   'if' '(' expr ')' expr_or_assign 'else' expr_or_assign	# if_else
    |   'for' '(' ID 'in' expr ')' expr		# for
    |   'while' '(' expr ')' expr_or_assign	# while
    |   'next' EOS							# next
    |   'break' EOS							# break
    |   '(' expr ')'						# parens
    |   ID									# id
    |   STRING								# string	
    |   INT									# int	
    |   DOUBLE								# double
	|	NL									# blank	
	|	EOS									# eos
    |   STOCK								# stock
    |   'NULL'								# null
    |   'Inf'								# inf
    |    BOOLEAN							# boolean
    |	'PRINT(' expr ')'					# print_fxn //predefined print function
    |	'PRINT_STOCK(' STOCK ')'			# print_stock_fxn //predefined print stock function
    ;

exprlist
    :   (expr_or_assign EOS)* '}'		# mult_expr_assn
    ;

sublist : sub (',' sub)* ;

sub	:   expr			# sub_id
    ;

TYPE : 'Stock' | 'int' | 'double' | 'String';

BOOLEAN: 'true' | 'false' ;

STOCK  :   '$' (LETTER)+ 
	   |	'$' (LETTER)+ '.' (LETTER)+ ;

INT :   DIGIT+ [Ll]? ;

DOUBLE:  DIGIT+ '.' DIGIT* EXP? [Ll]?
    |   DIGIT+ EXP? [Ll]?
    |   '.' DIGIT+ EXP? [Ll]?
    ;

fragment
DIGIT:  '0'..'9' ;

fragment
EXP :   ('E' | 'e') ('+' | '-')? INT ;

STRING
    :   '\"' ( ESC | ~[\\"] )*? '\"' ;

fragment
ESC :   '\\' ([abtnfrv]|'"'|'\'') ;

ID  :   '.' (LETTER|'_'|'.') (LETTER|DIGIT|'_'|'.')*
    |   LETTER (LETTER|DIGIT|'_'|'.')*
    ;

fragment LETTER  : [a-zA-Z] ;

USER_OP :   '%' .*? '%' ;

ASSN_OP :  '=' ;

LP      :  '(' ;

RP      :  ')' ;

MUL : '*' ;
DIV : '/' ;
ADD : '+' ;
SUB : '-' ;

COMMENT :   '/' '/' .*? '\r'? '\n' -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;

EOS     :   ';' ; // End of statement

// Match both UNIX and Windows newlines
NL      :   '\r'? '\n' -> skip ;

WS      :   [ \t]+ -> skip ;
