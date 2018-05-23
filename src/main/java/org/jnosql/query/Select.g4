grammar Select;
query   : 'select' fields 'from' entity where? start? limit? order_clause? EOF;


fields: star | name (',' name)*;
star: '*';
start: 'start' INT;
limit: 'limit' INT;
order_clause: 'order by' order_name (order_name)*;
order_name: name | name 'asc' | name 'desc';
where: 'where' condition ('and' condition| 'or' condition)* ;
condition: eq | gt | gte | lt | lte | between | in | betweens | ins | like;
eq: not? name '=' (number | string);
gt: not? name '>' (number | string);
gte: not? name '>=' (number | string);
lt: not? name '<' (number| string);
lte: not? name '<=' (number| string);
between: not? name 'between' number 'and' number;
in: name not? 'in' '('number (',' number)* ')';
betweens: not? name 'between' string 'and' string;
ins: name not? 'in' '('string (',' string)* ')';
like: not? name 'like' string;
not: 'not';
name: ANY_NAME;
entity: ANY_NAME;
number: '-'? INT .? INT?;
string: STRING;
STRING :  '"' (ESC | ~["\\])* '"' ;
INT: [0-9]+;
ANY_NAME: [a-zA-Z_.] [a-zA-Z._0-9]*;
WS: [ \t\r\n]+ -> skip ;
SL_COMMENT: '//' .*? '\n' -> skip;
fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;