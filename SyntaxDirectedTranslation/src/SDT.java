import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDT {

    public int idx;
    private static List<Token.TokenEntry> table;
    private static Map<String, String> var;
    public int max_len;
    public Token.TokenEntry token;

    private Token.TokenEntry getToken() {
//        if(idx > max_len - 1) {
//            if(stack.pop().equals("$")) {
//                //System.out.println("[MATCH] [" + top + ", " + token.getValue() + "]"/*+ stack.get(0) + ": " + token.getValue() + "] ["*/);
//                System.out.println("Parse OK");
//                System.exit(0);
//            }
//            error("index overbound");
//        }
        //System.out.println(idx);
        if(table.get(idx).getTokenType().equals("comment")) {
            idx++;
            return getToken();
        }
        token = table.get(idx);
        return token;
    }

    public void error(String top) {
        System.out.println("ERROR: expected " + top + ", got " + token.getValue() + " " + token.getTokenType());
        System.out.println("Parsing failed.");
        System.exit(0);
    }

    public void expect (String token, String expected) {
        if(token.equals(expected)) {
            //System.out.println("check " + token + " " + expected);
            idx++;
            if(idx == max_len) return;
            getToken();
        }
        else error(expected);
    }

    public void parseList () {
        table = Token.getTable();
        idx = 0;
        max_len = table.size();
        var = new HashMap<>();

        start_parser();
    }

    public void start_parser() {
        idx = 0;
        token = getToken();
        int count = 0;

        parse_init();

        while (!token.getValue().equals("end")) { //while stack not empty
            //System.out.println(count++);
            //System.out.println("start begin");
            if(idx >= max_len) break;
            token = getToken();

            if (token.getValue().equals("int")) {
                //System.out.println("check " + token.getValue());
                parse_declaration();
            }
            else if(token.getTokenType().equals("identifier")) {
                parse_assignment();
            }
            else if (token.getValue().equals("print_line")) {
                parse_print();
            }
            else error(token.getValue());
        }

        parse_end();
        System.exit(0);
    }

    public void parse_init () {
        //System.out.println(token.getValue());
        expect(token.getValue(), "program");
        //System.out.println(token.getValue());
        expect(token.getTokenType(), "identifier");
        //System.out.println(token.getValue());
        expect(token.getValue(), "begin");
    }

    public void parse_declaration () {
        String variable_name, value = "";
        //System.out.println(token.getValue());
        idx++;
        token = getToken();
        while(!token.getTokenType().equals("statement terminator")) {
            if(token.getValue().equals("int") || token.getValue().equals(",")) idx++;
            token = getToken();
            variable_name = token.getValue();
            expect(token.getTokenType(), "identifier");
            if(!token.getTokenType().equals("statement terminator")) {
                expect(token.getTokenType(), "assignment operator");
                value = token.getValue();                                                     //Integer.parseInt(token.getValue());
                expect(token.getTokenType(), "number_literal");
            }
            //System.out.println("declare " + variable_name + " " + value);
            var.put(variable_name, value);
            //variable_name = "";
            value = "";
        }
        expect(token.getValue(), ";");
    }

    public void parse_assignment() {
        String variable_name;
        //String val;
        int value, var1;

        variable_name = token.getValue();
        idx++;
        token = getToken();
        expect(token.getTokenType(), "assignment operator");
        value = Integer.parseInt(var.get(token.getValue()));
        idx++;
        token = getToken();
        while (token.getTokenType().equals("multiplication operator")) {
            expect(token.getValue(), "*");
            //System.out.println(token.getValue());
            var1 = Integer.parseInt(var.get(token.getValue()));
            expect(token.getTokenType(), "identifier");
            value = get_multiplication(value, var1);
        }
        var.put(variable_name, String.valueOf(value));
        expect(token.getValue(), ";");
    }

    public int get_multiplication (int var1, int var2) {
        return var1 * var2;
    }

    public void parse_print() {
        expect(token.getValue(), "print_line");
        expect(token.getValue(), "(");
        if(token.getTokenType().equals("identifier")) {
            System.out.println(var.get(token.getValue()));
            idx++;
            token = getToken();
        }
        else {
            String line_to_print = token.getValue();
            int length = line_to_print.length();
            System.out.println(line_to_print.substring(1, length-1));
            idx++;
            token = getToken();
        }
        expect(token.getValue(), ")");
        expect(token.getValue(), ";");

    }

    public void parse_end() {
        expect(token.getValue(), "end");
    }

}
