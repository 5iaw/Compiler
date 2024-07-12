%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void yyerror(const char *s);
extern int yylex(void);

int yylineno;
int yylex_destroy(void);

typedef struct {
    char *name;
    int value;
} symbol;

#define MAX_SYMBOLS 100
#define UNDECLARED_IDENTIFIER_ERROR -999999

symbol symbol_table[MAX_SYMBOLS];
int symbol_count = 0;
int error_flag = 0; // Global error flag

int lookup_symbol(const char *name) {
    for (int i = 0; i < symbol_count; i++) {
        if (strcmp(symbol_table[i].name, name) == 0) {
            return symbol_table[i].value;
        }
    }
    yyerror("undeclared identifier");
    error_flag = 1; // Set error flag
    return UNDECLARED_IDENTIFIER_ERROR;
}

void update_symbol(const char *name, int value) {
    for (int i = 0; i < symbol_count; i++) {
        if (strcmp(symbol_table[i].name, name) == 0) {
            symbol_table[i].value = value;
            return;
        }
    }
    symbol_table[symbol_count].name = strdup(name);
    symbol_table[symbol_count].value = value;
    symbol_count++;
}

%}

%union {
    int ival;
    char *sval;
}

%token PROGRAM BEGINN END INT IF ELSE PRINT_LINE
%token <ival> NUMBER
%token <sval> SEMICOLON ASSIGN COMMA LPAREN RPAREN PLUS MINUS TIMES DIVIDE
%token <sval> EQ NEQ LEQ GEQ
%token <sval> IDENTIFIER STRING

%type <ival> expression
%type <sval> declaration declarations assignment print if_statement block statement
%type <sval> program
%type <sval> empty
%type <sval> statements

%left PLUS MINUS
%left TIMES DIVIDE
%left EQ NEQ LEQ GEQ '>' '<'

%%

program:
    PROGRAM IDENTIFIER BEGINN statements END {
        //if (!error_flag) printf("Program parsed successfully.\n");
    }
;

statements:
    empty
    | statements statement
;

statement:
    declaration SEMICOLON
    | assignment SEMICOLON
    | print
    | if_statement
;

declaration:
    INT declarations { /*if (!error_flag) printf("Declare variables\n");*/ }
;

declarations:
    IDENTIFIER { update_symbol($1, 0);  }
    | IDENTIFIER ASSIGN expression { update_symbol($1, $3); }
    | declarations COMMA IDENTIFIER { update_symbol($3, 0);  }
    | declarations COMMA IDENTIFIER ASSIGN expression { update_symbol($3, $5); }
;

assignment:
    IDENTIFIER ASSIGN expression {
        if (!error_flag) {
            int val = lookup_symbol($1);
            if (val == UNDECLARED_IDENTIFIER_ERROR) {
                yyerror("assignment to undeclared identifier");
                error_flag = 1;
            } else {
                //printf("Assign %d to %s\n", $3, $1);
                update_symbol($1, $3);
            }
        }
    }
;

print:
    PRINT_LINE LPAREN STRING RPAREN SEMICOLON {
        if (!error_flag) {
            // Remove quotes from the string
            char *str = $3;
            str++; // Move past the opening quote
            str[strlen(str) - 1] = '\0'; // Replace closing quote with null terminator
            printf("%s\n", str);
        }
    }
    | PRINT_LINE LPAREN IDENTIFIER RPAREN SEMICOLON {
        if (!error_flag) {
            int val = lookup_symbol($3);
            if (val == UNDECLARED_IDENTIFIER_ERROR) {
                yyerror("print of undeclared identifier");
                error_flag = 1;
            } else {
                printf("%d\n", val);
            }
        }
    }
;

if_statement:
    IF LPAREN expression RPAREN block {
        if (!error_flag) {
            if ($3 == UNDECLARED_IDENTIFIER_ERROR) {
                yyerror("if condition with undeclared identifier");
                error_flag = 1;
            } else {
                //printf("If condition with value %d\n", $3);
            }
        }
    }
    | IF LPAREN expression RPAREN block ELSE block {
        if (!error_flag) {
            if ($3 == UNDECLARED_IDENTIFIER_ERROR) {
                yyerror("if-else condition with undeclared identifier");
                error_flag = 1;
            } else {
                //printf("If-Else condition with value %d\n", $3);
            }
        }
    }
;

block:
    BEGINN statements END
;

expression:
    NUMBER { $$ = $1; /*printf("Parsed number %d\n", $1); */ }
    | IDENTIFIER {
        if (!error_flag) {
            int val = lookup_symbol($1);
            if (val == UNDECLARED_IDENTIFIER_ERROR) {
                $$ = UNDECLARED_IDENTIFIER_ERROR;
                error_flag = 1;
            } else {
                $$ = val;
                //printf("Parsed identifier %s with value %d\n", $1, val);
            }
        } else {
            $$ = UNDECLARED_IDENTIFIER_ERROR;
        }
    }
    | expression PLUS expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 + $3;  }
    | expression MINUS expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 - $3;}
    | expression TIMES expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 * $3;  }
    | expression DIVIDE expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 / $3; }
    | expression '>' expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 > $3;  }
    | expression '<' expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 < $3;  }
    | expression EQ expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 == $3; }
    | expression NEQ expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 != $3; }
    | expression LEQ expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 <= $3; }
    | expression GEQ expression { $$ = ($1 == UNDECLARED_IDENTIFIER_ERROR || $3 == UNDECLARED_IDENTIFIER_ERROR) ? UNDECLARED_IDENTIFIER_ERROR : $1 >= $3;  }
    | LPAREN expression RPAREN { $$ = $2; }
;

empty:
    { $$ = NULL; }
;

%%

void yyerror(const char *s) {
    fprintf(stderr, "Error: %s \n", s);

}

int main(int argc, char **argv) {
    yyparse();
    yylex_destroy();
    return 0;
}