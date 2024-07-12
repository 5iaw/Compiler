%{
#include "calcParser.tab.h"
#include <string.h>
%}

%%

[ \t\n]+                  { /* ignore whitespace */ }
"--".*                    { /* ignore comments */ }
"program"                 { return PROGRAM; }
"begin"                   { return BEGINN; }
"end"                     { return END; }
"int"                     { return INT; }
"print_line"              { return PRINT_LINE; }
"if"                      { return IF; }
"else"                    { return ELSE; }
">="                      { return GEQ; }
"<="                      { return LEQ; }
"=="                      { return EQ; }
"!="                      { return NEQ; }
"<"                       { return '<'; }
">"                       { return '>'; }
"="                       { return ASSIGN; }
"("                       { return LPAREN; }
")"                       { return RPAREN; }
";"                       { return SEMICOLON; }
","                       { return COMMA; }
"+"                       { return PLUS; }
"-"                       { return MINUS; }
"*"                       { return TIMES; }
"/"                       { return DIVIDE; }
[0-9]+                    { yylval.ival = atoi(yytext); return NUMBER; }
[a-zA-Z_][a-zA-Z0-9_]*    { yylval.sval = strdup(yytext); return IDENTIFIER; }
\"[^\"]*\"                { yylval.sval = strdup(yytext); return STRING; }
.                         { return yytext[0]; }

%%

int yywrap(void) {
    return 1;
}
