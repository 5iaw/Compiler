import java.util.ArrayList;
import java.util.List;

public class RecursiveDescentParser {

    public int idx;
    public int flag = 0;
    public List<Token.TokenEntry> table;
    public token_num = table.size();
    public boolean pass = true;

    public Token.TokenEntry entry;

    public void parseList (Token token_table) {
        table = Token.getTable();
        idx = 0;

        pass = start_parser();
        //System.out.println(pass);
    }

    // match functions
    public boolean match_type(String expectedType, int f) {
        entry = table.get(idx);
        //System.out.println(idx);
        String token_type = entry.getTokenType();
        if(token_type.equals("comment")) {
            if(idx<table.size()-1) idx++;
          //  System.out.println(token_type + " ok");
            //return match_type(expectedType);
            entry = table.get(idx);
            //System.out.println(idx);
            token_type = entry.getTokenType();
        }
        if (token_type.equals(expectedType)) {
            if(idx+1<table.size()) idx++;
            //System.out.println(token_type + " ok");
            return true; // Match successful
        } else {
            //System.out.println(entry.getValue()+ " " + token_type + " bad, expected " + expectedType);
            if(f==1) {
                System.out.println("Parsing Failed");
                System.out.println(expectedType + " missing");
                //System.exit(0);
            }
            return false; // Match failed
        }
    }
    public boolean match_keyword(String expectedToken, int f) {
        entry = table.get(idx);
        //System.out.println(idx);
        String token = entry.getValue();
        if(entry.getTokenType().equals("comment")) {
            if(idx<table.size()-1) idx++;
            //System.out.println(entry.getTokenType() + " ok");
            //return match_type(expectedToken);
            entry = table.get(idx);
            //System.out.println(idx);
            token = entry.getValue();
        }
        if (token.equals(expectedToken)) {
            if(idx+1<table.size()) idx++;

            else {
                if(idx==59) return true;
                System.out.println("Parsing Failed");
                System.out.println(expectedToken + " missing");
                System.exit(0);
                return false;
            }
            //System.out.println(token + " ok");
            return true; // Match successful
        } else {
            System.out.println(token + " " + entry.getTokenType() + " bad, expected " + expectedToken);
            if(f==1) {
                System.out.println("Parsing Failed");
                System.out.println(expectedToken + " missing");
                System.exit(0);
            }
            return false; // Match failed
        }
    }


    // Terminals
    public boolean factor () {
        return (match_type("number literal",0) || match_type("identifier",0));
    }

    public boolean string_lit () {
        return match_type("string literal",1);
    }


    // Non-Terminals
    public boolean start_parser () {
        if(idx != 0) return false;

        match_keyword("program",1);
        match_type("identifier",1);
        boolean h = begin_stmt();
        System.out.println(h);
        return h;
    }

    public boolean begin_stmt () {
        match_keyword("begin",1);
        //stmt();
        while (!(table.get(idx).getValue().equals("end"))) {
            //idx--;
            //if(entry.getValue().equals("else_if") || entry.getValue().equals("else")) break;
            if(idx == table.size()-1) {
                System.out.println("Parsing Failed");
                System.out.println("end missing");
                System.exit(0);
            }
            stmt();
        }

        pass = match_keyword("end",1);
        //System.out.println(idx + " " + table.size() + "  " + pass);
        boolean parse = (idx == table.size()-1) && pass;
        //System.out.println(parse);
        return parse;
//        System.out.println("not here");
//        if(idx== table.size()) return true;
//        return false;
    }

    public boolean stmt () { // how to do several stmt

        if(table.get(idx).getValue().equals("if")) {
            match_keyword("if",1);
            match_keyword("(",1);
            com_stmt();
            match_keyword(")",1);
            begin_stmt();
            //System.out.println("done if");

            if(table.get(idx).getValue().equals("else_if")) {
                match_keyword("else_if",1);
                match_keyword("(",1);
                com_stmt();
                match_keyword(")",1);
                begin_stmt();
            }
            if(table.get(idx).getValue().equals("else")) {
                match_keyword("else",0);
                begin_stmt();
            }
            return true;
        }

        if(match_keyword("print_line",0)) {
            match_keyword("(",1);
            match_type("string literal",1);
            match_keyword(")",1);
            match_keyword(";",1);
            return true;
        }

        if(factor() || match_keyword("int",0)) {
            if(!(table.get(idx-1).getValue().equals("int"))) {
                idx--;
            }

            operator();
            while (match_keyword(",",0)) {
                operator();
            }
            match_keyword(";",1);
            return true;
        }


        System.out.println("Parsing Failed");
        System.out.println("end missing");
        System.exit(0);
        return false;
    }

    public boolean operator () {
        if(match_type("identifier",0)) {
            if(match_keyword("=",0)) flag=0;
        }
        op_stmt();
        return true;
    }

    public boolean op_stmt() {
        term();
        while(match_keyword("+",0)) {
            term();
        }
        return true;
    }

    public boolean term() {
        factor();
        while(match_keyword("*",0)) {
            factor();
        }
        return true;
    }

    public boolean com_stmt() {
        factor();
        match_keyword("<",1);
        return factor();
    }

}
