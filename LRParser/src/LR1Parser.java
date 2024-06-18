import java.util.*;

public class LR1Parser {

    public int idx;
    private Stack<String> stack;
    private HashMap<String, HashMap<String, List<String>>> parsingTable;
    public List<Token.TokenEntry> table;
    public int max_len;
    public Token.TokenEntry token;
    public List<String> declared_variables;
    private HashMap<Integer, HashMap<String, Pair<String, Integer>>> actionTable;
    private HashMap<Integer, HashMap<String, Integer>> goToTable;
    public List<String> declared_state;

    public Token.TokenEntry entry;


    private void generate_stack() {
        stack = new Stack<>();
        stack.push("$");
        stack.push("S");
        //printStack(stack, "initial");
    }

    public static List<String> generateRule(String inputString) {
        String[] tokenArray = inputString.split("\\s+");
        //System.out.println(tokenArray[1]);
        return Arrays.asList(tokenArray);
    }


    private void generate_parsingTable() {
        parsingTable = new HashMap<>();

        // Define rules according to the new BNF
        List<String> S = generateRule("BEGIN_BLOCK BEGIN_STMT");

        List<String> BEGIN_BLOCK1 = generateRule("program id");

        List<String> BEGIN_STMT1 = generateRule("begin STMTS end");

        List<String> STMTS1 = generateRule("STMT STMTS");
        List<String> STMTS2 = generateRule(""); // epsilon (empty)

        List<String> STMT1 = generateRule("DECLARATION_LIST");
        List<String> STMT2 = generateRule("IF_STMT");
        List<String> STMT3 = generateRule("PRINT_STMT");
        List<String> STMT4 = generateRule("WHILE_STMT");
        List<String> STMT5 = generateRule("FOR_STMT");
        List<String> STMT6 = generateRule("BREAK_STMT");

        List<String> DECLARATION_LIST1 = generateRule("DECLARATION DECLARATION_LIST_P ;");

        List<String> DECLARATION_LIST_P1 = generateRule(", DECLARATION DECLARATION_LIST_P");
        List<String> DECLARATION_LIST_P2 = generateRule(""); // epsilon (empty)

        List<String> DECLARATION1 = generateRule("TYPE ID_LIST");
        List<String> DECLARATION2 = generateRule("ID_LIST");

        List<String> TYPE1 = generateRule("int");
        List<String> TYPE2 = generateRule("integer");

        List<String> ASSIGNMENT1 = generateRule("= OP_STMT");
        List<String> ASSIGNMENT2 = generateRule(""); // epsilon (empty)

        List<String> ID_LIST1 = generateRule("id ASSIGNMENT ID_LIST_P");
        List<String> ID_LIST_P1 = generateRule(""); // epsilon (empty)

        List<String> OP_STMT1 = generateRule("TERM OP_STMT_P");

        List<String> OP_STMT_P1 = generateRule("+ TERM OP_STMT_P");
        List<String> OP_STMT_P2 = generateRule("++");
        List<String> OP_STMT_P3 = generateRule(""); // epsilon (empty)

        List<String> TERM1 = generateRule("FACTOR TERM_P");

        List<String> TERM_P1 = generateRule("* FACTOR TERM_P");
        List<String> TERM_P2 = generateRule(""); // epsilon (empty)

        List<String> FACTOR1 = generateRule("number_literal");
        List<String> FACTOR2 = generateRule("id");

        List<String> PRINT_CONTENT1 = generateRule("string_literal");
        List<String> PRINT_CONTENT2 = generateRule("id");

        List<String> IF_STMT1 = generateRule("if ( COM_STMT ) BEGIN_STMT IF_STMT_P");

        List<String> IF_STMT_P1 = generateRule("else_if ( COM_STMT ) BEGIN_STMT IF_STMT_P");
        List<String> IF_STMT_P2 = generateRule("else BEGIN_STMT");
        List<String> IF_STMT_P3 = generateRule(""); // epsilon (empty)

        List<String> PRINT1 = generateRule("print_line ( PRINT_CONTENT ) ;");
        List<String> PRINT2 = generateRule("display ( PRINT_CONTENT ) ;");

        List<String> WHILE_STMT1 = generateRule("while ( COM_STMT ) BEGIN_STMT");

        List<String> FOR_STMT1 = generateRule("for ( DECLARATION COM_STMT ; OP_STMT ) BEGIN_STMT");

        List<String> BREAK_STMT1 = generateRule("break ;");

        List<String> COM_STMT1 = generateRule("TERM COM_STMT_P");

        List<String> COM_STMT_P1 = generateRule("< FACTOR");
        List<String> COM_STMT_P2 = generateRule("== FACTOR");
        List<String> COM_STMT_P3 = generateRule(""); // epsilon (empty)

        // Fill parsing table
        parsingTable.put("S", new HashMap<>());
        parsingTable.get("S").put("program", S);

        parsingTable.put("BEGIN_BLOCK", new HashMap<>());
        parsingTable.get("BEGIN_BLOCK").put("program", BEGIN_BLOCK1);

        parsingTable.put("BEGIN_STMT", new HashMap<>());
        parsingTable.get("BEGIN_STMT").put("begin", BEGIN_STMT1);

        parsingTable.put("STMTS", new HashMap<>());
        parsingTable.get("STMTS").put("int", STMTS1);
        parsingTable.get("STMTS").put("integer", STMTS1);
        parsingTable.get("STMTS").put("id", STMTS1);
        parsingTable.get("STMTS").put("if", STMTS1);
        parsingTable.get("STMTS").put("print_line", STMTS1);
        parsingTable.get("STMTS").put("display", STMTS1);
        parsingTable.get("STMTS").put("while", STMTS1);
        parsingTable.get("STMTS").put("for", STMTS1);
        parsingTable.get("STMTS").put("break", STMTS1);
        parsingTable.get("STMTS").put("end", STMTS2);

        parsingTable.put("STMT", new HashMap<>());
        parsingTable.get("STMT").put("int", STMT1);
        parsingTable.get("STMT").put("integer", STMT1);
        parsingTable.get("STMT").put("id", STMT1);
        parsingTable.get("STMT").put("if", STMT2);
        parsingTable.get("STMT").put("print_line", STMT3);
        parsingTable.get("STMT").put("display", STMT3);
        parsingTable.get("STMT").put("while", STMT4);
        parsingTable.get("STMT").put("for", STMT5);
        parsingTable.get("STMT").put("break", STMT6);

        parsingTable.put("DECLARATION_LIST", new HashMap<>());
        parsingTable.get("DECLARATION_LIST").put("int", DECLARATION_LIST1);
        parsingTable.get("DECLARATION_LIST").put("integer", DECLARATION_LIST1);
        parsingTable.get("DECLARATION_LIST").put("id", DECLARATION_LIST1);

        parsingTable.put("DECLARATION_LIST_P", new HashMap<>());
        parsingTable.get("DECLARATION_LIST_P").put(",", DECLARATION_LIST_P1);
        parsingTable.get("DECLARATION_LIST_P").put(";", DECLARATION_LIST_P2);

        parsingTable.put("DECLARATION", new HashMap<>());
        parsingTable.get("DECLARATION").put("int", DECLARATION1);
        parsingTable.get("DECLARATION").put("integer", DECLARATION1);
        parsingTable.get("DECLARATION").put("id", DECLARATION2);

        parsingTable.put("TYPE", new HashMap<>());
        parsingTable.get("TYPE").put("int", TYPE1);
        parsingTable.get("TYPE").put("integer", TYPE2);

        parsingTable.put("ASSIGNMENT", new HashMap<>());
        parsingTable.get("ASSIGNMENT").put("=", ASSIGNMENT1);
        parsingTable.get("ASSIGNMENT").put(",", ASSIGNMENT2);
        parsingTable.get("ASSIGNMENT").put(";", ASSIGNMENT2);

        parsingTable.put("ID_LIST", new HashMap<>());
        parsingTable.get("ID_LIST").put("id", ID_LIST1);

        parsingTable.put("ID_LIST_P", new HashMap<>());
        parsingTable.get("ID_LIST_P").put(",", ID_LIST_P1);
        parsingTable.get("ID_LIST_P").put(";", ID_LIST_P1);

        parsingTable.put("OP_STMT", new HashMap<>());
        parsingTable.get("OP_STMT").put("id", OP_STMT1);
        parsingTable.get("OP_STMT").put("number_literal", OP_STMT1);

        parsingTable.put("OP_STMT_P", new HashMap<>());
        parsingTable.get("OP_STMT_P").put("+", OP_STMT_P1);
        parsingTable.get("OP_STMT_P").put("++", OP_STMT_P2);
        parsingTable.get("OP_STMT_P").put(";", OP_STMT_P3);
        parsingTable.get("OP_STMT_P").put(",", OP_STMT_P3);
        parsingTable.get("OP_STMT_P").put(")", OP_STMT_P3);

        parsingTable.put("TERM", new HashMap<>());
        parsingTable.get("TERM").put("id", TERM1);
        parsingTable.get("TERM").put("number_literal", TERM1);

        parsingTable.put("TERM_P", new HashMap<>());
        parsingTable.get("TERM_P").put("*", TERM_P1);
        parsingTable.get("TERM_P").put(";", TERM_P2);
        parsingTable.get("TERM_P").put(",", TERM_P2);
        parsingTable.get("TERM_P").put(")", TERM_P2);

        parsingTable.put("FACTOR", new HashMap<>());
        parsingTable.get("FACTOR").put("number_literal", FACTOR1);
        parsingTable.get("FACTOR").put("id", FACTOR2);

        parsingTable.put("PRINT_CONTENT", new HashMap<>());
        parsingTable.get("PRINT_CONTENT").put("string_literal", PRINT_CONTENT1);
        parsingTable.get("PRINT_CONTENT").put("id", PRINT_CONTENT2);

        parsingTable.put("IF_STMT", new HashMap<>());
        parsingTable.get("IF_STMT").put("if", IF_STMT1);

        parsingTable.put("IF_STMT_P", new HashMap<>());
        parsingTable.get("IF_STMT_P").put("else_if", IF_STMT_P1);
        parsingTable.get("IF_STMT_P").put("else", IF_STMT_P2);
        parsingTable.get("IF_STMT_P").put(";", IF_STMT_P3);
        parsingTable.get("IF_STMT_P").put("end", IF_STMT_P3);

        parsingTable.put("PRINT_STMT", new HashMap<>());
        parsingTable.get("PRINT_STMT").put("print_line", PRINT1);
        parsingTable.get("PRINT_STMT").put("display", PRINT2);

        parsingTable.put("WHILE_STMT", new HashMap<>());
        parsingTable.get("WHILE_STMT").put("while", WHILE_STMT1);

        parsingTable.put("FOR_STMT", new HashMap<>());
        parsingTable.get("FOR_STMT").put("for", FOR_STMT1);

        parsingTable.put("BREAK_STMT", new HashMap<>());
        parsingTable.get("BREAK_STMT").put("break", BREAK_STMT1);

        parsingTable.put("COM_STMT", new HashMap<>());
        parsingTable.get("COM_STMT").put("id", COM_STMT1);
        parsingTable.get("COM_STMT").put("number_literal", COM_STMT1);

        parsingTable.put("COM_STMT_P", new HashMap<>());
        parsingTable.get("COM_STMT_P").put("<", COM_STMT_P1);
        parsingTable.get("COM_STMT_P").put("==", COM_STMT_P2);
        parsingTable.get("COM_STMT_P").put(")", COM_STMT_P3);
    }


    public void generateActionTable() {
        // Example of populating the action table
        // (State, Symbol) -> (Action, Number)

            actionTable = new HashMap<>();

            addAction(0, "program", "SHIFT", 3);
            addAction(0, "S", "ERROR", None);
            addAction(0, "BEGIN_BLOCK", "ERROR", None);
            addAction(1, "$", "ACCEPT", None);
            addAction(2, "begin", "SHIFT", 5);
            addAction(2, "BEGIN_STMT", "ERROR", None);
            addAction(3, "id", "SHIFT", 6);
            addAction(4, "$", "REDUCE", 1);
            addAction(5, "id", "SHIFT", 26);
            addAction(5, "end", "REDUCE", 5);
            addAction(5, "int", "SHIFT", 24);
            addAction(5, "integer", "SHIFT", 25);
            addAction(5, "if", "SHIFT", 16);
            addAction(5, "print_line", "SHIFT", 17);
            addAction(5, "display", "SHIFT", 18);
            addAction(5, "while", "SHIFT", 19);
            addAction(5, "for", "SHIFT", 20);
            addAction(5, "break", "SHIFT", 21);
            addAction(5, "STMTS", "ERROR", None);
            addAction(5, "STMT", "ERROR", None);
            addAction(5, "DECLARATION_LIST", "ERROR", None);
            addAction(5, "DECLARATION", "ERROR", None);
            addAction(5, "TYPE", "ERROR", None);
            addAction(5, "ID_LIST", "ERROR", None);
            addAction(5, "IF_STMT", "ERROR", None);
            addAction(5, "PRINT_STMT", "ERROR", None);
            addAction(5, "WHILE_STMT", "ERROR", None);
            addAction(5, "FOR_STMT", "ERROR", None);
            addAction(5, "BREAK_STMT", "ERROR", None);
            addAction(6, "begin", "REDUCE", 2);
            addAction(7, "end", "SHIFT", 27);
            addAction(8, "id", "SHIFT", 26);
            addAction(8, "end", "REDUCE", 5);
            addAction(8, "int", "SHIFT", 24);
            addAction(8, "integer", "SHIFT", 25);
            addAction(8, "if", "SHIFT", 16);
            addAction(8, "print_line", "SHIFT", 17);
            addAction(8, "display", "SHIFT", 18);
            addAction(8, "while", "SHIFT", 19);
            addAction(8, "for", "SHIFT", 20);
            addAction(8, "break", "SHIFT", 21);
            addAction(8, "STMTS", "ERROR", None);
            addAction(8, "STMT", "ERROR", None);
            addAction(8, "DECLARATION_LIST", "ERROR", None);
            addAction(8, "DECLARATION", "ERROR", None);
            addAction(8, "TYPE", "ERROR", None);
            addAction(8, "ID_LIST", "ERROR", None);
            addAction(8, "IF_STMT", "ERROR", None);
            addAction(8, "PRINT_STMT", "ERROR", None);
            addAction(8, "WHILE_STMT", "ERROR", None);
            addAction(8, "FOR_STMT", "ERROR", None);
            addAction(8, "BREAK_STMT", "ERROR", None);
            addAction(9, "id", "REDUCE", 6);
            addAction(9, "end", "REDUCE", 6);
            addAction(9, "int", "REDUCE", 6);
            addAction(9, "integer", "REDUCE", 6);
            addAction(9, "if", "REDUCE", 6);
            addAction(9, "print_line", "REDUCE", 6);
            addAction(9, "display", "REDUCE", 6);
            addAction(9, "while", "REDUCE", 6);
            addAction(9, "for", "REDUCE", 6);
            addAction(9, "break", "REDUCE", 6);
            addAction(10, "id", "REDUCE", 7);
            addAction(10, "end", "REDUCE", 7);
            addAction(10, "int", "REDUCE", 7);
            addAction(10, "integer", "REDUCE", 7);
            addAction(10, "if", "REDUCE", 7);
            addAction(10, "print_line", "REDUCE", 7);
            addAction(10, "display", "REDUCE", 7);
            addAction(10, "while", "REDUCE", 7);
            addAction(10, "for", "REDUCE", 7);
            addAction(10, "break", "REDUCE", 7);
            addAction(11, "id", "REDUCE", 8);
            addAction(11, "end", "REDUCE", 8);
            addAction(11, "int", "REDUCE", 8);
            addAction(11, "integer", "REDUCE", 8);
            addAction(11, "if", "REDUCE", 8);
            addAction(11, "print_line", "REDUCE", 8);
            addAction(11, "display", "REDUCE", 8);
            addAction(11, "while", "REDUCE", 8);
            addAction(11, "for", "REDUCE", 8);
            addAction(11, "break", "REDUCE", 8);
            addAction(12, "id", "REDUCE", 9);
            addAction(12, "end", "REDUCE", 9);
            addAction(12, "int", "REDUCE", 9);
            addAction(12, "integer", "REDUCE", 9);
            addAction(12, "if", "REDUCE", 9);
            addAction(12, "print_line", "REDUCE", 9);
            addAction(12, "display", "REDUCE", 9);
            addAction(12, "while", "REDUCE", 9);
            addAction(12, "for", "REDUCE", 9);
            addAction(12, "break", "REDUCE", 9);
            addAction(13, "id", "REDUCE", 10);
            addAction(13, "end", "REDUCE", 10);
            addAction(13, "int", "REDUCE", 10);
            addAction(13, "integer", "REDUCE", 10);
            addAction(13, "if", "REDUCE", 10);
            addAction(13, "print_line", "REDUCE", 10);
            addAction(13, "display", "REDUCE", 10);
            addAction(13, "while", "REDUCE", 10);
            addAction(13, "for", "REDUCE", 10);
            addAction(13, "break", "REDUCE", 10);
            addAction(14, "id", "REDUCE", 11);
            addAction(14, "end", "REDUCE", 11);
            addAction(14, "int", "REDUCE", 11);
            addAction(14, "integer", "REDUCE", 11);
            addAction(14, "if", "REDUCE", 11);
            addAction(14, "print_line", "REDUCE", 11);
            addAction(14, "display", "REDUCE", 11);
            addAction(14, "while", "REDUCE", 11);
            addAction(14, "for", "REDUCE", 11);
            addAction(14, "break", "REDUCE", 11);
            addAction(15, ";", "REDUCE", 14);
            addAction(15, ",", "SHIFT", 30);
            addAction(15, "DECLARATION_LIST'", "ERROR", None);
            addAction(16, "(", "SHIFT", 31);
            addAction(17, "(", "SHIFT", 32);
            addAction(18, "(", "SHIFT", 33);
            addAction(19, "(", "SHIFT", 34);
            addAction(20, "(", "SHIFT", 35);
            addAction(21, ";", "SHIFT", 36);
            addAction(22, "id", "SHIFT", 26);
            addAction(22, "ID_LIST", "ERROR", None);
            addAction(23, "id", "REDUCE", 16);
            addAction(23, "end", "REDUCE", 16);
            addAction(23, ";", "REDUCE", 16);
            addAction(23, ",", "REDUCE", 16);
            addAction(23, "int", "REDUCE", 16);
            addAction(23, "integer", "REDUCE", 16);
            addAction(23, "if", "REDUCE", 16);
            addAction(23, "print_line", "REDUCE", 16);
            addAction(23, "display", "REDUCE", 16);
            addAction(23, "while", "REDUCE", 16);
            addAction(23, "for", "REDUCE", 16);
            addAction(23, "break", "REDUCE", 16);
            addAction(24, "id", "REDUCE", 17);
            addAction(25, "id", "REDUCE", 18);
            addAction(26, "id", "REDUCE", 20);
            addAction(26, "end", "REDUCE", 20);
            addAction(26, ";", "REDUCE", 20);
            addAction(26, ",", "REDUCE", 20);
            addAction(26, "int", "REDUCE", 20);
            addAction(26, "integer", "REDUCE", 20);
            addAction(26, "=", "SHIFT", 39);
            addAction(26, "if", "REDUCE", 20);
            addAction(26, "print_line", "REDUCE", 20);
            addAction(26, "display", "REDUCE", 20);
            addAction(26, "while", "REDUCE", 20);
            addAction(26, "for", "REDUCE", 20);
            addAction(26, "break", "REDUCE", 20);
            addAction(26, "ASSIGNMENT", "ERROR", None);
            addAction(27, "$", "REDUCE", 3);
            addAction(28, "end", "REDUCE", 4);
            addAction(29, ";", "SHIFT", 40);
            addAction(30, "id", "SHIFT", 44);
            addAction(30, "int", "SHIFT", 24);
            addAction(30, "integer", "SHIFT", 25);
            addAction(30, "DECLARATION", "ERROR", None);
            addAction(30, "TYPE", "ERROR", None);
            addAction(30, "ID_LIST", "ERROR", None);
            addAction(31, "id", "SHIFT", 49);
            addAction(31, "number_literal", "SHIFT", 48);
            addAction(31, "TERM", "ERROR", None);
            addAction(31, "FACTOR", "ERROR", None);
            addAction(31, "COM_STMT", "ERROR", None);
            addAction(32, "string_literal", "SHIFT", 51);
            addAction(32, "identifier", "SHIFT", 52);
            addAction(32, "PRINT_CONTENT", "ERROR", None);
            addAction(33, "string_literal", "SHIFT", 51);
            addAction(33, "identifier", "SHIFT", 52);
            addAction(33, "PRINT_CONTENT", "ERROR", None);
            addAction(34, "id", "SHIFT", 49);
            addAction(34, "number_literal", "SHIFT", 48);
            addAction(34, "TERM", "ERROR", None);
            addAction(34, "FACTOR", "ERROR", None);
            addAction(34, "COM_STMT", "ERROR", None);
            addAction(35, "id", "SHIFT", 58);
            addAction(35, "int", "SHIFT", 24);
            addAction(35, "integer", "SHIFT", 25);
            addAction(35, "DECLARATION", "ERROR", None);
            addAction(35, "TYPE", "ERROR", None);
            addAction(35, "ID_LIST", "ERROR", None);
            addAction(36, "id", "REDUCE", 42);
            addAction(36, "end", "REDUCE", 42);
            addAction(36, "int", "REDUCE", 42);
            addAction(36, "integer", "REDUCE", 42);
            addAction(36, "if", "REDUCE", 42);
            addAction(36, "print_line", "REDUCE", 42);
            addAction(36, "display", "REDUCE", 42);
            addAction(36, "while", "REDUCE", 42);
            addAction(36, "for", "REDUCE", 42);
            addAction(36, "break", "REDUCE", 42);
            addAction(37, "id", "REDUCE", 15);
            addAction(37, "end", "REDUCE", 15);
            addAction(37, ";", "REDUCE", 15);
            addAction(37, ",", "REDUCE", 15);
            addAction(37, "int", "REDUCE", 15);
            addAction(37, "integer", "REDUCE", 15);
            addAction(37, "if", "REDUCE", 15);
            addAction(37, "print_line", "REDUCE", 15);
            addAction(37, "display", "REDUCE", 15);
            addAction(37, "while", "REDUCE", 15);
            addAction(37, "for", "REDUCE", 15);
            addAction(37, "break", "REDUCE", 15);
            addAction(38, "id", "REDUCE", 22);
            addAction(38, "end", "REDUCE", 22);
            addAction(38, ";", "REDUCE", 22);
            addAction(38, ",", "REDUCE", 22);
            addAction(38, "int", "REDUCE", 22);
            addAction(38, "integer", "REDUCE", 22);
            addAction(38, "if", "REDUCE", 22);
            addAction(38, "print_line", "REDUCE", 22);
            addAction(38, "display", "REDUCE", 22);
            addAction(38, "while", "REDUCE", 22);
            addAction(38, "for", "REDUCE", 22);
            addAction(38, "break", "REDUCE", 22);
            addAction(38, "ID_LIST'", "ERROR", None);
            addAction(39, "id", "SHIFT", 64);
            addAction(39, "number_literal", "SHIFT", 63);
            addAction(39, "OP_STMT", "ERROR", None);
            addAction(39, "TERM", "ERROR", None);
            addAction(39, "FACTOR", "ERROR", None);
            addAction(40, "id", "REDUCE", 12);
            addAction(40, "end", "REDUCE", 12);
            addAction(40, "int", "REDUCE", 12);
            addAction(40, "integer", "REDUCE", 12);
            addAction(40, "if", "REDUCE", 12);
            addAction(40, "print_line", "REDUCE", 12);
            addAction(40, "display", "REDUCE", 12);
            addAction(40, "while", "REDUCE", 12);
            addAction(40, "for", "REDUCE", 12);
            addAction(40, "break", "REDUCE", 12);
            addAction(41, ";", "REDUCE", 14);
            addAction(41, ",", "SHIFT", 30);
            addAction(41, "DECLARATION_LIST'", "ERROR", None);
            addAction(42, "id", "SHIFT", 44);
            addAction(42, "ID_LIST", "ERROR", None);
            addAction(43, ";", "REDUCE", 16);
            addAction(43, ",", "REDUCE", 16);
            addAction(44, ";", "REDUCE", 20);
            addAction(44, ",", "REDUCE", 20);
            addAction(44, "=", "SHIFT", 68);
            addAction(44, "ASSIGNMENT", "ERROR", None);
            addAction(45, ")", "SHIFT", 69);
            addAction(46, ")", "REDUCE", 46);
            addAction(46, "<", "SHIFT", 71);
            addAction(46, "==", "SHIFT", 72);
            addAction(46, "COM_STMT'", "ERROR", None);
            addAction(47, "*", "SHIFT", 74);
            addAction(47, ")", "REDUCE", 29);
            addAction(47, "<", "REDUCE", 29);
            addAction(47, "==", "REDUCE", 29);
            addAction(47, "TERM'", "ERROR", None);
            addAction(48, "*", "REDUCE", 30);
            addAction(48, ")", "REDUCE", 30);
            addAction(48, "<", "REDUCE", 30);
            addAction(48, "==", "REDUCE", 30);
            addAction(49, "*", "REDUCE", 31);
            addAction(49, ")", "REDUCE", 31);
            addAction(49, "<", "REDUCE", 31);
            addAction(49, "==", "REDUCE", 31);
            addAction(50, ")", "SHIFT", 75);
            addAction(51, ")", "REDUCE", 32);
            addAction(52, ")", "REDUCE", 33);
            addAction(53, ")", "SHIFT", 76);
            addAction(54, ")", "SHIFT", 77);
            addAction(55, "id", "SHIFT", 82);
            addAction(55, "number_literal", "SHIFT", 81);
            addAction(55, "TERM", "ERROR", None);
            addAction(55, "FACTOR", "ERROR", None);
            addAction(55, "COM_STMT", "ERROR", None);
            addAction(56, "id", "SHIFT", 58);
            addAction(56, "ID_LIST", "ERROR", None);
            addAction(57, "id", "REDUCE", 16);
            addAction(57, "number_literal", "REDUCE", 16);
            addAction(58, "id", "REDUCE", 20);
            addAction(58, "=", "SHIFT", 85);
            addAction(58, "number_literal", "REDUCE", 20);
            addAction(58, "ASSIGNMENT", "ERROR", None);
            addAction(59, "id", "REDUCE", 21);
            addAction(59, "end", "REDUCE", 21);
            addAction(59, ";", "REDUCE", 21);
            addAction(59, ",", "REDUCE", 21);
            addAction(59, "int", "REDUCE", 21);
            addAction(59, "integer", "REDUCE", 21);
            addAction(59, "if", "REDUCE", 21);
            addAction(59, "print_line", "REDUCE", 21);
            addAction(59, "display", "REDUCE", 21);
            addAction(59, "while", "REDUCE", 21);
            addAction(59, "for", "REDUCE", 21);
            addAction(59, "break", "REDUCE", 21);
            addAction(60, "id", "REDUCE", 19);
            addAction(60, "end", "REDUCE", 19);
            addAction(60, ";", "REDUCE", 19);
            addAction(60, ",", "REDUCE", 19);
            addAction(60, "int", "REDUCE", 19);
            addAction(60, "integer", "REDUCE", 19);
            addAction(60, "if", "REDUCE", 19);
            addAction(60, "print_line", "REDUCE", 19);
            addAction(60, "display", "REDUCE", 19);
            addAction(60, "while", "REDUCE", 19);
            addAction(60, "for", "REDUCE", 19);
            addAction(60, "break", "REDUCE", 19);
            addAction(61, "id", "REDUCE", 26);
            addAction(61, "end", "REDUCE", 26);
            addAction(61, ";", "REDUCE", 26);
            addAction(61, ",", "REDUCE", 26);
            addAction(61, "int", "REDUCE", 26);
            addAction(61, "integer", "REDUCE", 26);
            addAction(61, "+", "SHIFT", 87);
            addAction(61, "++", "SHIFT", 88);
            addAction(61, "if", "REDUCE", 26);
            addAction(61, "print_line", "REDUCE", 26);
            addAction(61, "display", "REDUCE", 26);
            addAction(61, "while", "REDUCE", 26);
            addAction(61, "for", "REDUCE", 26);
            addAction(61, "break", "REDUCE", 26);
            addAction(61, "OP_STMT'", "ERROR", None);
            addAction(62, "id", "REDUCE", 29);
            addAction(62, "end", "REDUCE", 29);
            addAction(62, ";", "REDUCE", 29);
            addAction(62, ",", "REDUCE", 29);
            addAction(62, "int", "REDUCE", 29);
            addAction(62, "integer", "REDUCE", 29);
            addAction(62, "+", "REDUCE", 29);
            addAction(62, "++", "REDUCE", 29);
            addAction(62, "*", "SHIFT", 90);
            addAction(62, "if", "REDUCE", 29);
            addAction(62, "print_line", "REDUCE", 29);
            addAction(62, "display", "REDUCE", 29);
            addAction(62, "while", "REDUCE", 29);
            addAction(62, "for", "REDUCE", 29);
            addAction(62, "break", "REDUCE", 29);
            addAction(62, "TERM'", "ERROR", None);
            addAction(63, "id", "REDUCE", 30);
            addAction(63, "end", "REDUCE", 30);
            addAction(63, ";", "REDUCE", 30);
            addAction(63, ",", "REDUCE", 30);
            addAction(63, "int", "REDUCE", 30);
            addAction(63, "integer", "REDUCE", 30);
            addAction(63, "+", "REDUCE", 30);
            addAction(63, "++", "REDUCE", 30);
            addAction(63, "*", "REDUCE", 30);
            addAction(63, "if", "REDUCE", 30);
            addAction(63, "print_line", "REDUCE", 30);
            addAction(63, "display", "REDUCE", 30);
            addAction(63, "while", "REDUCE", 30);
            addAction(63, "for", "REDUCE", 30);
            addAction(63, "break", "REDUCE", 30);
            addAction(64, "id", "REDUCE", 31);
            addAction(64, "end", "REDUCE", 31);
            addAction(64, ";", "REDUCE", 31);
            addAction(64, ",", "REDUCE", 31);
            addAction(64, "int", "REDUCE", 31);
            addAction(64, "integer", "REDUCE", 31);
            addAction(64, "+", "REDUCE", 31);
            addAction(64, "++", "REDUCE", 31);
            addAction(64, "*", "REDUCE", 31);
            addAction(64, "if", "REDUCE", 31);
            addAction(64, "print_line", "REDUCE", 31);
            addAction(64, "display", "REDUCE", 31);
            addAction(64, "while", "REDUCE", 31);
            addAction(64, "for", "REDUCE", 31);
            addAction(64, "break", "REDUCE", 31);
            addAction(65, ";", "REDUCE", 13);
            addAction(66, ";", "REDUCE", 15);
            addAction(66, ",", "REDUCE", 15);
            addAction(67, ";", "REDUCE", 22);
            addAction(67, ",", "REDUCE", 22);
            addAction(67, "ID_LIST'", "ERROR", None);
            addAction(68, "id", "SHIFT", 96);
            addAction(68, "number_literal", "SHIFT", 95);
            addAction(68, "OP_STMT", "ERROR", None);
            addAction(68, "TERM", "ERROR", None);
            addAction(68, "FACTOR", "ERROR", None);
            addAction(69, "begin", "SHIFT", 98);
            addAction(69, "BEGIN_STMT", "ERROR", None);
            addAction(70, ")", "REDUCE", 43);
            addAction(71, "id", "SHIFT", 101);
            addAction(71, "number_literal", "SHIFT", 100);
            addAction(71, "FACTOR", "ERROR", None);
            addAction(72, "id", "SHIFT", 101);
            addAction(72, "number_literal", "SHIFT", 100);
            addAction(72, "FACTOR", "ERROR", None);
            addAction(73, ")", "REDUCE", 27);
            addAction(73, "<", "REDUCE", 27);
            addAction(73, "==", "REDUCE", 27);
            addAction(74, "id", "SHIFT", 49);
            addAction(74, "number_literal", "SHIFT", 48);
            addAction(74, "FACTOR", "ERROR", None);
            addAction(75, ";", "SHIFT", 104);
            addAction(76, ";", "SHIFT", 105);
            addAction(77, "begin", "SHIFT", 107);
            addAction(77, "BEGIN_STMT", "ERROR", None);
            addAction(78, ";", "SHIFT", 108);
            addAction(79, ";", "REDUCE", 46);
            addAction(79, "<", "SHIFT", 110);
            addAction(79, "==", "SHIFT", 111);
            addAction(79, "COM_STMT'", "ERROR", None);
            addAction(80, ";", "REDUCE", 29);
            addAction(80, "*", "SHIFT", 113);
            addAction(80, "<", "REDUCE", 29);
            addAction(80, "==", "REDUCE", 29);
            addAction(80, "TERM'", "ERROR", None);
            addAction(81, ";", "REDUCE", 30);
            addAction(81, "*", "REDUCE", 30);
            addAction(81, "<", "REDUCE", 30);
            addAction(81, "==", "REDUCE", 30);
            addAction(82, ";", "REDUCE", 31);
            addAction(82, "*", "REDUCE", 31);
            addAction(82, "<", "REDUCE", 31);
            addAction(82, "==", "REDUCE", 31);
            addAction(83, "id", "REDUCE", 15);
            addAction(83, "number_literal", "REDUCE", 15);
            addAction(84, "id", "REDUCE", 22);
            addAction(84, "number_literal", "REDUCE", 22);
            addAction(84, "ID_LIST'", "ERROR", None);
            addAction(85, "id", "SHIFT", 119);
            addAction(85, "number_literal", "SHIFT", 118);
            addAction(85, "OP_STMT", "ERROR", None);
            addAction(85, "TERM", "ERROR", None);
            addAction(85, "FACTOR", "ERROR", None);
            addAction(86, "id", "REDUCE", 23);
            addAction(86, "end", "REDUCE", 23);
            addAction(86, ";", "REDUCE", 23);
            addAction(86, ",", "REDUCE", 23);
            addAction(86, "int", "REDUCE", 23);
            addAction(86, "integer", "REDUCE", 23);
            addAction(86, "if", "REDUCE", 23);
            addAction(86, "print_line", "REDUCE", 23);
            addAction(86, "display", "REDUCE", 23);
            addAction(86, "while", "REDUCE", 23);
            addAction(86, "for", "REDUCE", 23);
            addAction(86, "break", "REDUCE", 23);
            addAction(87, "id", "SHIFT", 64);
            addAction(87, "number_literal", "SHIFT", 63);
            addAction(87, "TERM", "ERROR", None);
            addAction(87, "FACTOR", "ERROR", None);
            addAction(88, "id", "REDUCE", 25);
            addAction(88, "end", "REDUCE", 25);
            addAction(88, ";", "REDUCE", 25);
            addAction(88, ",", "REDUCE", 25);
            addAction(88, "int", "REDUCE", 25);
            addAction(88, "integer", "REDUCE", 25);
            addAction(88, "if", "REDUCE", 25);
            addAction(88, "print_line", "REDUCE", 25);
            addAction(88, "display", "REDUCE", 25);
            addAction(88, "while", "REDUCE", 25);
            addAction(88, "for", "REDUCE", 25);
            addAction(88, "break", "REDUCE", 25);
            addAction(89, "id", "REDUCE", 27);
            addAction(89, "end", "REDUCE", 27);
            addAction(89, ";", "REDUCE", 27);
            addAction(89, ",", "REDUCE", 27);
            addAction(89, "int", "REDUCE", 27);
            addAction(89, "integer", "REDUCE", 27);
            addAction(89, "+", "REDUCE", 27);
            addAction(89, "++", "REDUCE", 27);
            addAction(89, "if", "REDUCE", 27);
            addAction(89, "print_line", "REDUCE", 27);
            addAction(89, "display", "REDUCE", 27);
            addAction(89, "while", "REDUCE", 27);
            addAction(89, "for", "REDUCE", 27);
            addAction(89, "break", "REDUCE", 27);
            addAction(90, "id", "SHIFT", 64);
            addAction(90, "number_literal", "SHIFT", 63);
            addAction(90, "FACTOR", "ERROR", None);
            addAction(91, ";", "REDUCE", 21);
            addAction(91, ",", "REDUCE", 21);
            addAction(92, ";", "REDUCE", 19);
            addAction(92, ",", "REDUCE", 19);
            addAction(93, ";", "REDUCE", 26);
            addAction(93, ",", "REDUCE", 26);
            addAction(93, "+", "SHIFT", 123);
            addAction(93, "++", "SHIFT", 124);
            addAction(93, "OP_STMT'", "ERROR", None);
            addAction(94, ";", "REDUCE", 29);
            addAction(94, ",", "REDUCE", 29);
            addAction(94, "+", "REDUCE", 29);
            addAction(94, "++", "REDUCE", 29);
            addAction(94, "*", "SHIFT", 126);
            addAction(94, "TERM'", "ERROR", None);
            addAction(95, ";", "REDUCE", 30);
            addAction(95, ",", "REDUCE", 30);
            addAction(95, "+", "REDUCE", 30);
            addAction(95, "++", "REDUCE", 30);
            addAction(95, "*", "REDUCE", 30);
            addAction(96, ";", "REDUCE", 31);
            addAction(96, ",", "REDUCE", 31);
            addAction(96, "+", "REDUCE", 31);
            addAction(96, "++", "REDUCE", 31);
            addAction(96, "*", "REDUCE", 31);
            addAction(97, "id", "REDUCE", 37);
            addAction(97, "end", "REDUCE", 37);
            addAction(97, "int", "REDUCE", 37);
            addAction(97, "integer", "REDUCE", 37);
            addAction(97, "if", "REDUCE", 37);
            addAction(97, "else_if", "SHIFT", 128);
            addAction(97, "else", "SHIFT", 129);
            addAction(97, "print_line", "REDUCE", 37);
            addAction(97, "display", "REDUCE", 37);
            addAction(97, "while", "REDUCE", 37);
            addAction(97, "for", "REDUCE", 37);
            addAction(97, "break", "REDUCE", 37);
            addAction(97, "IF_STMT'", "ERROR", None);
            addAction(98, "id", "SHIFT", 26);
            addAction(98, "end", "REDUCE", 5);
            addAction(98, "int", "SHIFT", 24);
            addAction(98, "integer", "SHIFT", 25);
            addAction(98, "if", "SHIFT", 16);
            addAction(98, "print_line", "SHIFT", 17);
            addAction(98, "display", "SHIFT", 18);
            addAction(98, "while", "SHIFT", 19);
            addAction(98, "for", "SHIFT", 20);
            addAction(98, "break", "SHIFT", 21);
            addAction(98, "STMTS", "ERROR", None);
            addAction(98, "STMT", "ERROR", None);
            addAction(98, "DECLARATION_LIST", "ERROR", None);
            addAction(98, "DECLARATION", "ERROR", None);
            addAction(98, "TYPE", "ERROR", None);
            addAction(98, "ID_LIST", "ERROR", None);
            addAction(98, "IF_STMT", "ERROR", None);
            addAction(98, "PRINT_STMT", "ERROR", None);
            addAction(98, "WHILE_STMT", "ERROR", None);
            addAction(98, "FOR_STMT", "ERROR", None);
            addAction(98, "BREAK_STMT", "ERROR", None);
            addAction(99, ")", "REDUCE", 44);
            addAction(100, ")", "REDUCE", 30);
            addAction(101, ")", "REDUCE", 31);
            addAction(102, ")", "REDUCE", 45);
            addAction(103, "*", "SHIFT", 74);
            addAction(103, ")", "REDUCE", 29);
            addAction(103, "<", "REDUCE", 29);
            addAction(103, "==", "REDUCE", 29);
            addAction(103, "TERM'", "ERROR", None);
            addAction(104, "id", "REDUCE", 38);
            addAction(104, "end", "REDUCE", 38);
            addAction(104, "int", "REDUCE", 38);
            addAction(104, "integer", "REDUCE", 38);
            addAction(104, "if", "REDUCE", 38);
            addAction(104, "print_line", "REDUCE", 38);
            addAction(104, "display", "REDUCE", 38);
            addAction(104, "while", "REDUCE", 38);
            addAction(104, "for", "REDUCE", 38);
            addAction(104, "break", "REDUCE", 38);
            addAction(105, "id", "REDUCE", 39);
            addAction(105, "end", "REDUCE", 39);
            addAction(105, "int", "REDUCE", 39);
            addAction(105, "integer", "REDUCE", 39);
            addAction(105, "if", "REDUCE", 39);
            addAction(105, "print_line", "REDUCE", 39);
            addAction(105, "display", "REDUCE", 39);
            addAction(105, "while", "REDUCE", 39);
            addAction(105, "for", "REDUCE", 39);
            addAction(105, "break", "REDUCE", 39);
            addAction(106, "id", "REDUCE", 40);
            addAction(106, "end", "REDUCE", 40);
            addAction(106, "int", "REDUCE", 40);
            addAction(106, "integer", "REDUCE", 40);
            addAction(106, "if", "REDUCE", 40);
            addAction(106, "print_line", "REDUCE", 40);
            addAction(106, "display", "REDUCE", 40);
            addAction(106, "while", "REDUCE", 40);
            addAction(106, "for", "REDUCE", 40);
            addAction(106, "break", "REDUCE", 40);
            addAction(107, "id", "SHIFT", 26);
            addAction(107, "end", "REDUCE", 5);
            addAction(107, "int", "SHIFT", 24);
            addAction(107, "integer", "SHIFT", 25);
            addAction(107, "if", "SHIFT", 16);
            addAction(107, "print_line", "SHIFT", 17);
            addAction(107, "display", "SHIFT", 18);
            addAction(107, "while", "SHIFT", 19);
            addAction(107, "for", "SHIFT", 20);
            addAction(107, "break", "SHIFT", 21);
            addAction(107, "STMTS", "ERROR", None);
            addAction(107, "STMT", "ERROR", None);
            addAction(107, "DECLARATION_LIST", "ERROR", None);
            addAction(107, "DECLARATION", "ERROR", None);
            addAction(107, "TYPE", "ERROR", None);
            addAction(107, "ID_LIST", "ERROR", None);
            addAction(107, "IF_STMT", "ERROR", None);
            addAction(107, "PRINT_STMT", "ERROR", None);
            addAction(107, "WHILE_STMT", "ERROR", None);
            addAction(107, "FOR_STMT", "ERROR", None);
            addAction(107, "BREAK_STMT", "ERROR", None);
            addAction(108, "id", "SHIFT", 137);
            addAction(108, "number_literal", "SHIFT", 136);
            addAction(108, "OP_STMT", "ERROR", None);
            addAction(108, "TERM", "ERROR", None);
            addAction(108, "FACTOR", "ERROR", None);
            addAction(109, ";", "REDUCE", 43);
            addAction(110, "id", "SHIFT", 140);
            addAction(110, "number_literal", "SHIFT", 139);
            addAction(110, "FACTOR", "ERROR", None);
            addAction(111, "id", "SHIFT", 140);
            addAction(111, "number_literal", "SHIFT", 139);
            addAction(111, "FACTOR", "ERROR", None);
            addAction(112, ";", "REDUCE", 27);
            addAction(112, "<", "REDUCE", 27);
            addAction(112, "==", "REDUCE", 27);
            addAction(113, "id", "SHIFT", 82);
            addAction(113, "number_literal", "SHIFT", 81);
            addAction(113, "FACTOR", "ERROR", None);
            addAction(114, "id", "REDUCE", 21);
            addAction(114, "number_literal", "REDUCE", 21);
            addAction(115, "id", "REDUCE", 19);
            addAction(115, "number_literal", "REDUCE", 19);
            addAction(116, "id", "REDUCE", 26);
            addAction(116, "+", "SHIFT", 144);
            addAction(116, "++", "SHIFT", 145);
            addAction(116, "number_literal", "REDUCE", 26);
            addAction(116, "OP_STMT'", "ERROR", None);
            addAction(117, "id", "REDUCE", 29);
            addAction(117, "+", "REDUCE", 29);
            addAction(117, "++", "REDUCE", 29);
            addAction(117, "*", "SHIFT", 147);
            addAction(117, "number_literal", "REDUCE", 29);
            addAction(117, "TERM'", "ERROR", None);
            addAction(118, "id", "REDUCE", 30);
            addAction(118, "+", "REDUCE", 30);
            addAction(118, "++", "REDUCE", 30);
            addAction(118, "*", "REDUCE", 30);
            addAction(118, "number_literal", "REDUCE", 30);
            addAction(119, "id", "REDUCE", 31);
            addAction(119, "+", "REDUCE", 31);
            addAction(119, "++", "REDUCE", 31);
            addAction(119, "*", "REDUCE", 31);
            addAction(119, "number_literal", "REDUCE", 31);
            addAction(120, "id", "REDUCE", 26);
            addAction(120, "end", "REDUCE", 26);
            addAction(120, ";", "REDUCE", 26);
            addAction(120, ",", "REDUCE", 26);
            addAction(120, "int", "REDUCE", 26);
            addAction(120, "integer", "REDUCE", 26);
            addAction(120, "+", "SHIFT", 87);
            addAction(120, "++", "SHIFT", 88);
            addAction(120, "if", "REDUCE", 26);
            addAction(120, "print_line", "REDUCE", 26);
            addAction(120, "display", "REDUCE", 26);
            addAction(120, "while", "REDUCE", 26);
            addAction(120, "for", "REDUCE", 26);
            addAction(120, "break", "REDUCE", 26);
            addAction(120, "OP_STMT'", "ERROR", None);
            addAction(121, "id", "REDUCE", 29);
            addAction(121, "end", "REDUCE", 29);
            addAction(121, ";", "REDUCE", 29);
            addAction(121, ",", "REDUCE", 29);
            addAction(121, "int", "REDUCE", 29);
            addAction(121, "integer", "REDUCE", 29);
            addAction(121, "+", "REDUCE", 29);
            addAction(121, "++", "REDUCE", 29);
            addAction(121, "*", "SHIFT", 90);
            addAction(121, "if", "REDUCE", 29);
            addAction(121, "print_line", "REDUCE", 29);
            addAction(121, "display", "REDUCE", 29);
            addAction(121, "while", "REDUCE", 29);
            addAction(121, "for", "REDUCE", 29);
            addAction(121, "break", "REDUCE", 29);
            addAction(121, "TERM'", "ERROR", None);
            addAction(122, ";", "REDUCE", 23);
            addAction(122, ",", "REDUCE", 23);
            addAction(123, "id", "SHIFT", 96);
            addAction(123, "number_literal", "SHIFT", 95);
            addAction(123, "TERM", "ERROR", None);
            addAction(123, "FACTOR", "ERROR", None);
            addAction(124, ";", "REDUCE", 25);
            addAction(124, ",", "REDUCE", 25);
            addAction(125, ";", "REDUCE", 27);
            addAction(125, ",", "REDUCE", 27);
            addAction(125, "+", "REDUCE", 27);
            addAction(125, "++", "REDUCE", 27);
            addAction(126, "id", "SHIFT", 96);
            addAction(126, "number_literal", "SHIFT", 95);
            addAction(126, "FACTOR", "ERROR", None);
            addAction(127, "id", "REDUCE", 34);
            addAction(127, "end", "REDUCE", 34);
            addAction(127, "int", "REDUCE", 34);
            addAction(127, "integer", "REDUCE", 34);
            addAction(127, "if", "REDUCE", 34);
            addAction(127, "print_line", "REDUCE", 34);
            addAction(127, "display", "REDUCE", 34);
            addAction(127, "while", "REDUCE", 34);
            addAction(127, "for", "REDUCE", 34);
            addAction(127, "break", "REDUCE", 34);
            addAction(128, "(", "SHIFT", 152);
            addAction(129, "begin", "SHIFT", 107);
            addAction(129, "BEGIN_STMT", "ERROR", None);
            addAction(130, "end", "SHIFT", 154);
            addAction(131, ")", "REDUCE", 28);
            addAction(131, "<", "REDUCE", 28);
            addAction(131, "==", "REDUCE", 28);
            addAction(132, "end", "SHIFT", 155);
            addAction(133, ")", "SHIFT", 156);
            addAction(134, "+", "SHIFT", 158);
            addAction(134, "++", "SHIFT", 159);
            addAction(134, ")", "REDUCE", 26);
            addAction(134, "OP_STMT'", "ERROR", None);
            addAction(135, "+", "REDUCE", 29);
            addAction(135, "++", "REDUCE", 29);
            addAction(135, "*", "SHIFT", 161);
            addAction(135, ")", "REDUCE", 29);
            addAction(135, "TERM'", "ERROR", None);
            addAction(136, "+", "REDUCE", 30);
            addAction(136, "++", "REDUCE", 30);
            addAction(136, "*", "REDUCE", 30);
            addAction(136, ")", "REDUCE", 30);
            addAction(137, "+", "REDUCE", 31);
            addAction(137, "++", "REDUCE", 31);
            addAction(137, "*", "REDUCE", 31);
            addAction(137, ")", "REDUCE", 31);
            addAction(138, ";", "REDUCE", 44);
            addAction(139, ";", "REDUCE", 30);
            addAction(140, ";", "REDUCE", 31);
            addAction(141, ";", "REDUCE", 45);
            addAction(142, ";", "REDUCE", 29);
            addAction(142, "*", "SHIFT", 113);
            addAction(142, "<", "REDUCE", 29);
            addAction(142, "==", "REDUCE", 29);
            addAction(142, "TERM'", "ERROR", None);
            addAction(143, "id", "REDUCE", 23);
            addAction(143, "number_literal", "REDUCE", 23);
            addAction(144, "id", "SHIFT", 119);
            addAction(144, "number_literal", "SHIFT", 118);
            addAction(144, "TERM", "ERROR", None);
            addAction(144, "FACTOR", "ERROR", None);
            addAction(145, "id", "REDUCE", 25);
            addAction(145, "number_literal", "REDUCE", 25);
            addAction(146, "id", "REDUCE", 27);
            addAction(146, "+", "REDUCE", 27);
            addAction(146, "++", "REDUCE", 27);
            addAction(146, "number_literal", "REDUCE", 27);
            addAction(147, "id", "SHIFT", 119);
            addAction(147, "number_literal", "SHIFT", 118);
            addAction(147, "FACTOR", "ERROR", None);
            addAction(148, "id", "REDUCE", 24);
            addAction(148, "end", "REDUCE", 24);
            addAction(148, ";", "REDUCE", 24);
            addAction(148, ",", "REDUCE", 24);
            addAction(148, "int", "REDUCE", 24);
            addAction(148, "integer", "REDUCE", 24);
            addAction(148, "if", "REDUCE", 24);
            addAction(148, "print_line", "REDUCE", 24);
            addAction(148, "display", "REDUCE", 24);
            addAction(148, "while", "REDUCE", 24);
            addAction(148, "for", "REDUCE", 24);
            addAction(148, "break", "REDUCE", 24);
            addAction(149, "id", "REDUCE", 28);
            addAction(149, "end", "REDUCE", 28);
            addAction(149, ";", "REDUCE", 28);
            addAction(149, ",", "REDUCE", 28);
            addAction(149, "int", "REDUCE", 28);
            addAction(149, "integer", "REDUCE", 28);
            addAction(149, "+", "REDUCE", 28);
            addAction(149, "++", "REDUCE", 28);
            addAction(149, "if", "REDUCE", 28);
            addAction(149, "print_line", "REDUCE", 28);
            addAction(149, "display", "REDUCE", 28);
            addAction(149, "while", "REDUCE", 28);
            addAction(149, "for", "REDUCE", 28);
            addAction(149, "break", "REDUCE", 28);
            addAction(150, ";", "REDUCE", 26);
            addAction(150, ",", "REDUCE", 26);
            addAction(150, "+", "SHIFT", 123);
            addAction(150, "++", "SHIFT", 124);
            addAction(150, "OP_STMT'", "ERROR", None);
            addAction(151, ";", "REDUCE", 29);
            addAction(151, ",", "REDUCE", 29);
            addAction(151, "+", "REDUCE", 29);
            addAction(151, "++", "REDUCE", 29);
            addAction(151, "*", "SHIFT", 126);
            addAction(151, "TERM'", "ERROR", None);
            addAction(152, "id", "SHIFT", 49);
            addAction(152, "number_literal", "SHIFT", 48);
            addAction(152, "TERM", "ERROR", None);
            addAction(152, "FACTOR", "ERROR", None);
            addAction(152, "COM_STMT", "ERROR", None);
            addAction(153, "id", "REDUCE", 36);
            addAction(153, "end", "REDUCE", 36);
            addAction(153, "int", "REDUCE", 36);
            addAction(153, "integer", "REDUCE", 36);
            addAction(153, "if", "REDUCE", 36);
            addAction(153, "print_line", "REDUCE", 36);
            addAction(153, "display", "REDUCE", 36);
            addAction(153, "while", "REDUCE", 36);
            addAction(153, "for", "REDUCE", 36);
            addAction(153, "break", "REDUCE", 36);
            addAction(154, "id", "REDUCE", 3);
            addAction(154, "end", "REDUCE", 3);
            addAction(154, "int", "REDUCE", 3);
            addAction(154, "integer", "REDUCE", 3);
            addAction(154, "if", "REDUCE", 3);
            addAction(154, "else_if", "REDUCE", 3);
            addAction(154, "else", "REDUCE", 3);
            addAction(154, "print_line", "REDUCE", 3);
            addAction(154, "display", "REDUCE", 3);
            addAction(154, "while", "REDUCE", 3);
            addAction(154, "for", "REDUCE", 3);
            addAction(154, "break", "REDUCE", 3);
            addAction(155, "id", "REDUCE", 3);
            addAction(155, "end", "REDUCE", 3);
            addAction(155, "int", "REDUCE", 3);
            addAction(155, "integer", "REDUCE", 3);
            addAction(155, "if", "REDUCE", 3);
            addAction(155, "print_line", "REDUCE", 3);
            addAction(155, "display", "REDUCE", 3);
            addAction(155, "while", "REDUCE", 3);
            addAction(155, "for", "REDUCE", 3);
            addAction(155, "break", "REDUCE", 3);
            addAction(156, "begin", "SHIFT", 107);
            addAction(156, "BEGIN_STMT", "ERROR", None);
            addAction(157, ")", "REDUCE", 23);
            addAction(158, "id", "SHIFT", 137);
            addAction(158, "number_literal", "SHIFT", 136);
            addAction(158, "TERM", "ERROR", None);
            addAction(158, "FACTOR", "ERROR", None);
            addAction(159, ")", "REDUCE", 25);
            addAction(160, "+", "REDUCE", 27);
            addAction(160, "++", "REDUCE", 27);
            addAction(160, ")", "REDUCE", 27);
            addAction(161, "id", "SHIFT", 137);
            addAction(161, "number_literal", "SHIFT", 136);
            addAction(161, "FACTOR", "ERROR", None);
            addAction(162, ";", "REDUCE", 28);
            addAction(162, "<", "REDUCE", 28);
            addAction(162, "==", "REDUCE", 28);
            addAction(163, "id", "REDUCE", 26);
            addAction(163, "+", "SHIFT", 144);
            addAction(163, "++", "SHIFT", 145);
            addAction(163, "number_literal", "REDUCE", 26);
            addAction(163, "OP_STMT'", "ERROR", None);
            addAction(164, "id", "REDUCE", 29);
            addAction(164, "+", "REDUCE", 29);
            addAction(164, "++", "REDUCE", 29);
            addAction(164, "*", "SHIFT", 147);
            addAction(164, "number_literal", "REDUCE", 29);
            addAction(164, "TERM'", "ERROR", None);
            addAction(165, ";", "REDUCE", 24);
            addAction(165, ",", "REDUCE", 24);
            addAction(166, ";", "REDUCE", 28);
            addAction(166, ",", "REDUCE", 28);
            addAction(166, "+", "REDUCE", 28);
            addAction(166, "++", "REDUCE", 28);
            addAction(167, ")", "SHIFT", 173);
            addAction(168, "id", "REDUCE", 41);
            addAction(168, "end", "REDUCE", 41);
            addAction(168, "int", "REDUCE", 41);
            addAction(168, "integer", "REDUCE", 41);
            addAction(168, "if", "REDUCE", 41);
            addAction(168, "print_line", "REDUCE", 41);
            addAction(168, "display", "REDUCE", 41);
            addAction(168, "while", "REDUCE", 41);
            addAction(168, "for", "REDUCE", 41);
            addAction(168, "break", "REDUCE", 41);
            addAction(169, "+", "SHIFT", 158);
            addAction(169, "++", "SHIFT", 159);
            addAction(169, ")", "REDUCE", 26);
            addAction(169, "OP_STMT'", "ERROR", None);
            addAction(170, "+", "REDUCE", 29);
            addAction(170, "++", "REDUCE", 29);
            addAction(170, "*", "SHIFT", 161);
            addAction(170, ")", "REDUCE", 29);
            addAction(170, "TERM'", "ERROR", None);
            addAction(171, "id", "REDUCE", 24);
            addAction(171, "number_literal", "REDUCE", 24);
            addAction(172, "id", "REDUCE", 28);
            addAction(172, "+", "REDUCE", 28);
            addAction(172, "++", "REDUCE", 28);
            addAction(172, "number_literal", "REDUCE", 28);
            addAction(173, "begin", "SHIFT", 98);
            addAction(173, "BEGIN_STMT", "ERROR", None);
            addAction(174, ")", "REDUCE", 24);
            addAction(175, "+", "REDUCE", 28);
            addAction(175, "++", "REDUCE", 28);
            addAction(175, ")", "REDUCE", 28);
            addAction(176, "id", "REDUCE", 37);
            addAction(176, "end", "REDUCE", 37);
            addAction(176, "int", "REDUCE", 37);
            addAction(176, "integer", "REDUCE", 37);
            addAction(176, "if", "REDUCE", 37);
            addAction(176, "else_if", "SHIFT", 128);
            addAction(176, "else", "SHIFT", 129);
            addAction(176, "print_line", "REDUCE", 37);
            addAction(176, "display", "REDUCE", 37);
            addAction(176, "while", "REDUCE", 37);
            addAction(176, "for", "REDUCE", 37);
            addAction(176, "break", "REDUCE", 37);
            addAction(176, "IF_STMT'", "ERROR", None);
            addAction(177, "id", "REDUCE", 35);
            addAction(177, "end", "REDUCE", 35);
            addAction(177, "int", "REDUCE", 35);
            addAction(177, "integer", "REDUCE", 35);
            addAction(177, "if", "REDUCE", 35);
            addAction(177, "print_line", "REDUCE", 35);
            addAction(177, "display", "REDUCE", 35);
            addAction(177, "while", "REDUCE", 35);
            addAction(177, "for", "REDUCE", 35);
            addAction(177, "break", "REDUCE", 35);
        }

    }

    private void addAction(int state, String symbol, String action, int number) {
        actionTable
                .computeIfAbsent(state, k -> new HashMap<>())
                .put(symbol, new Pair<>(action, number));
    }

    // Method to generate the goto table
    public void generateGotoTable() {
        goToTable = new HashMap<>();



    }

    public void addGoToEntry(int state, String nonterminal, int nextState) {
        if (!table.containsKey(state)) {
            table.put(state, new HashMap<>());
        }
        table.get(state).put(nonterminal, nextState);
    }

    public void parseList(Token token_table) {
        table = Token.getTable();
        //table.add(0, new Token.TokenEntry("$", "dollar"));
        idx = 0;
        max_len = table.size();

        declared_variables = new ArrayList<>();
        generate_stack();
        generate_parsingTable();

        start_parser();
        //System.out.println(pass);
    }

    private Token.TokenEntry getToken() {
        if(idx > max_len - 1) {
            if(stack.pop().equals("$")) {
                //System.out.println("[MATCH] [" + top + ", " + token.getValue() + "]"/*+ stack.get(0) + ": " + token.getValue() + "] ["*/);
                System.out.println("Parse OK");
                System.exit(0);
            }
            error("index overbound");
        }
        if(table.get(idx).getTokenType().equals("comment")) {
            idx++;
            return getToken();
        }
        return table.get(idx);
    }


    /*
    set ip to point to the first symbol of w;
    set X to the top stack symbol;
    while ( X $ ) {  stack is not empty
        if( X is a ) pop the stack and advance ip;
        else if ( X is a terminal ) errorQ;
        else if ( M[X,a] is an error entry ) errorQ;
        else if ( M[X,a] = X -> YY •••Y) { X2k
                output the production X -> Y\Y • • • Y; 2k
                pop the stack;
                push Yk, Yfc-i,... ,Yi onto the stack, with Y\ on top;
        }
        set X to the top stack symbol;
    }
     */

    public void start_parser() {

        // check if stack is empty
        // check if pop is NT or T
        // match?
        token = getToken();
        int count = 0;

        while (!stack.empty()) { //while stack not empty
            //System.out.println(count++);
            String top = stack.pop(); // pop stack and advance
            //printStack(stack, "POP");
            //System.out.println(top + " " + token.getValue() + " " + token.getTokenType());
            //Token.TokenEntry token = getToken();
            //String first = parsingTable.get(top).get(token.getTokenType()).get(0);

            if(top.equals("$")) {
                if(token.getValue().equals("$")) {
                    System.out.println("EOS");
                    System.out.println("Parse OK");
                }
                else error(top);
            }

            if(token.getValue().equals(top) || token.getTokenType().equals(top)) {
                //matched
                System.out.println("[MATCH] - " + token.getTokenType().toUpperCase() + " - " + token.getValue());

                if(token.getValue().equals("int") || token.getValue().equals("integer")) {
                    idx++;
                    token = getToken();
                    declared_variables.add(token.getValue());
                }
                else {
                    idx++;
                    token = getToken();
                }

                if(token.getTokenType().equals("identifier")) {
                    int flag=0;
                    if(table.get(idx-1).getValue().equals(",")) {
                        flag=1;
                        declared_variables.add(token.getValue());
                    }
                    if(table.get(idx-1).getValue().equals("program")) flag=1;
                    else {
                        for(String i:declared_variables) {
                            if(token.getValue().equals(i)) {
                                flag=1;
                                break;
                            }
                        }
                    }
                    if(flag == 0) {
                        if(containsKeyword(token.getValue())) error("wrong keyword");
                        else error("not declared");
                    }
                }
                //printStack(stack, "MATCH");

            }
            else if (Character.isUpperCase(top.charAt(0))) { // non terminal - push on stack
                HashMap<String, List<String>> nt = parsingTable.get(top);
                if(nt.containsKey(token.getValue())) {
                    //System.out.println("found " + token.getValue());
                    List<String> l = nt.get(token.getValue());
                    for (int i = l.size() - 1; i >= 0; i--) {
                        if(l.get(i).isEmpty()) {
                            //idx++;
                            break;
                        }
                        stack.push(l.get(i));
                    }
                    printStack(stack, "GENERATE-stack");
                }
                else if(nt.containsKey(token.getTokenType())) {
                    List<String> l = nt.get(token.getTokenType());

                    for (int i = l.size() - 1; i >= 0; i--) {
                        if(l.get(i).isEmpty()) {
                            //idx++;
                            break;
                        }
                        stack.push(l.get(i));
                    }
                    printStack(stack, "GENERATE-stack");
                }
            }
            else {
                error(top);
            }
        }

    }

    public void error(String top) {
        if(top.equals("index overbound")) System.out.println("ERROR: Parsing stack is empty." +
                "\nKeyword " + token.getValue() + " not matched.");
        else if (top.equals("not declared")) System.out.println("ERROR: " +token.getValue() + " " + top);
        else if (top.equals("wrong keyword")) System.out.println("ERROR: keyword \'" + token.getValue() + "\' spelling error.");
        else System.out.println("ERROR: expected " + top + ", got " + token.getValue() + " " + token.getTokenType());
        System.out.println("Parsing failed.");
        System.exit(0);
    }

    public void printStack(Stack<String> stack, String action) {
        System.out.print("[" + action + "] [" /*+ stack.get(0) + ": " + token.getValue() + "] ["*/);

        for (int i=0; i< stack.size(); i++) {
            System.out.print(stack.get(i));
            if(i!=stack.size()-1) System.out.print( ", ");
        }
        System.out.println("]");
    }

//    public boolean isTerminal (String s) {
//
//    }

    public boolean containsKeyword(String s) {
        for(String i:ExpandIdentifier.keywords){
            if(s.contains(i)) return true;
        }
        return false;
    }

}
