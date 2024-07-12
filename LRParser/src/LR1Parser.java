import java.util.*;

public class LR1Parser {

    public int idx;
    private Stack<String> symbolStack;
    private Stack<Integer> stateStack;
    private HashMap<Integer, HashMap<String, List<String>>> productionTable;
    public List<Token.TokenEntry> table;
    public int max_len;
    public Token.TokenEntry token;
    public List<String> declared_variables;
    private HashMap<Integer, HashMap<String, List<String>>> actionTable;
    private HashMap<Integer, HashMap<String, Integer>> goToTable;
    private String lookAtSymbol;
    private int current_state;

    public Token.TokenEntry entry;


    private void generate_stack() {
        symbolStack = new Stack<>();
        stateStack = new Stack<>();
        stateStack.push(0);
//        stack.push("S");
        //printStack(stack, "initial");
    }

    public static List<String> generateRule(String inputString) {
        String[] tokenArray = inputString.split("\\s+");
        //System.out.println(tokenArray[1]);
        return Arrays.asList(tokenArray);
    }

    private void addProduction(int index, String lhs, List<String> rhs) {
        HashMap<String, List<String>> production = new HashMap<>();
        production.put(lhs, rhs);
        productionTable.put(index, production);
    }

    private void generateProductionTable() {
        // Define and add production rules with unique indices
        productionTable = new HashMap<>();

        addProduction(0, "S_P", Arrays.asList("S"));
        addProduction(1, "S", Arrays.asList("BEGIN_BLOCK", "BEGIN_STMT"));
        addProduction(2, "BEGIN_BLOCK", Arrays.asList("program", "id"));
        addProduction(3, "BEGIN_STMT", Arrays.asList("begin", "STMTS", "end"));
        addProduction(4, "STMTS", Arrays.asList("STMT", "STMTS"));
        addProduction(5, "STMTS", Arrays.asList("")); // epsilon

        addProduction(6, "STMT", Arrays.asList("DECLARATION_LIST"));
        addProduction(7, "STMT", Arrays.asList("IF_STMT"));
        addProduction(8, "STMT", Arrays.asList("PRINT_STMT"));
        addProduction(9, "STMT", Arrays.asList("WHILE_STMT"));
        addProduction(10, "STMT", Arrays.asList("FOR_STMT"));
        addProduction(11, "STMT", Arrays.asList("BREAK_STMT"));

        addProduction(12, "DECLARATION_LIST", Arrays.asList("DECLARATION", "DECLARATION_LIST_P", ";"));

        addProduction(13, "DECLARATION_LIST_P", Arrays.asList(",", "DECLARATION", "DECLARATION_LIST_P"));
        addProduction(14, "DECLARATION_LIST_P", Arrays.asList("")); // epsilon

        addProduction(15, "DECLARATION", Arrays.asList("TYPE", "ID_LIST"));
        addProduction(16, "DECLARATION", Arrays.asList("ID_LIST"));

        addProduction(17, "TYPE", Arrays.asList("int"));
        addProduction(18, "TYPE", Arrays.asList("integer"));

        addProduction(19, "ASSIGNMENT", Arrays.asList("=", "OP_STMT"));
        addProduction(20, "ASSIGNMENT", Arrays.asList("")); // epsilon

        addProduction(21, "ID_LIST", Arrays.asList("id", "ASSIGNMENT", "ID_LIST_P"));
        addProduction(22, "ID_LIST_P", Arrays.asList("")); // epsilon

        addProduction(23, "OP_STMT", Arrays.asList("TERM", "OP_STMT_P"));

        addProduction(24, "OP_STMT_P", Arrays.asList("+", "TERM", "OP_STMT_P"));
        addProduction(25, "OP_STMT_P", Arrays.asList("++"));
        addProduction(26, "OP_STMT_P", Arrays.asList("")); // epsilon

        addProduction(27, "TERM", Arrays.asList("FACTOR", "TERM_P"));

        addProduction(28, "TERM_P", Arrays.asList("*", "FACTOR", "TERM_P"));
        addProduction(29, "TERM_P", Arrays.asList("")); // epsilon

        addProduction(30, "FACTOR", Arrays.asList("number_literal"));
        addProduction(31, "FACTOR", Arrays.asList("id"));

        addProduction(32, "PRINT_CONTENT", Arrays.asList("string_literal"));
        addProduction(33, "PRINT_CONTENT", Arrays.asList("id"));

        addProduction(34, "IF_STMT", Arrays.asList("if", "(", "COM_STMT", ")", "BEGIN_STMT", "IF_STMT_P"));

        addProduction(35, "IF_STMT_P", Arrays.asList("else_if", "(", "COM_STMT", ")", "BEGIN_STMT", "IF_STMT_P"));
        addProduction(36, "IF_STMT_P", Arrays.asList("else", "BEGIN_STMT"));
        addProduction(37, "IF_STMT_P", Arrays.asList("")); // epsilon

        addProduction(38, "PRINT_STMT", Arrays.asList("print_line", "(", "PRINT_CONTENT", ")", ";"));
        addProduction(39, "PRINT_STMT", Arrays.asList("display", "(", "PRINT_CONTENT", ")", ";"));

        addProduction(40, "WHILE_STMT", Arrays.asList("while", "(", "COM_STMT", ")", "BEGIN_STMT"));

        addProduction(41, "FOR_STMT", Arrays.asList("for", "(", "DECLARATION", ";", "COM_STMT", ";", "OP_STMT", ")", "BEGIN_STMT"));

        addProduction(42, "BREAK_STMT", Arrays.asList("break", ";"));

        addProduction(43, "COM_STMT", Arrays.asList("TERM", "COM_STMT_P"));

        addProduction(44, "COM_STMT_P", Arrays.asList("<", "FACTOR"));
        addProduction(45, "COM_STMT_P", Arrays.asList("==", "FACTOR"));
        addProduction(46, "COM_STMT_P", Arrays.asList("")); // epsilon
    }


//
//    private void generate_parsingTable() {
//        parsingTable = new HashMap<>();
//
//        // Define rules according to the new BNF
//        List<String> S = generateRule("BEGIN_BLOCK BEGIN_STMT");
//
//        List<String> BEGIN_BLOCK1 = generateRule("program id");
//
//        List<String> BEGIN_STMT1 = generateRule("begin STMTS end");
//
//        List<String> STMTS1 = generateRule("STMT STMTS");
//        List<String> STMTS2 = generateRule(""); // epsilon (empty)
//
//        List<String> STMT1 = generateRule("DECLARATION_LIST");
//        List<String> STMT2 = generateRule("IF_STMT");
//        List<String> STMT3 = generateRule("PRINT_STMT");
//        List<String> STMT4 = generateRule("WHILE_STMT");
//        List<String> STMT5 = generateRule("FOR_STMT");
//        List<String> STMT6 = generateRule("BREAK_STMT");
//
//        List<String> DECLARATION_LIST1 = generateRule("DECLARATION DECLARATION_LIST_P ;");
//
//        List<String> DECLARATION_LIST_P1 = generateRule(", DECLARATION DECLARATION_LIST_P");
//        List<String> DECLARATION_LIST_P2 = generateRule(""); // epsilon (empty)
//
//        List<String> DECLARATION1 = generateRule("TYPE ID_LIST");
//        List<String> DECLARATION2 = generateRule("ID_LIST");
//
//        List<String> TYPE1 = generateRule("int");
//        List<String> TYPE2 = generateRule("integer");
//
//        List<String> ASSIGNMENT1 = generateRule("= OP_STMT");
//        List<String> ASSIGNMENT2 = generateRule(""); // epsilon (empty)
//
//        List<String> ID_LIST1 = generateRule("id ASSIGNMENT ID_LIST_P");
//        List<String> ID_LIST_P1 = generateRule(""); // epsilon (empty)
//
//        List<String> OP_STMT1 = generateRule("TERM OP_STMT_P");
//
//        List<String> OP_STMT_P1 = generateRule("+ TERM OP_STMT_P");
//        List<String> OP_STMT_P2 = generateRule("++");
//        List<String> OP_STMT_P3 = generateRule(""); // epsilon (empty)
//
//        List<String> TERM1 = generateRule("FACTOR TERM_P");
//
//        List<String> TERM_P1 = generateRule("* FACTOR TERM_P");
//        List<String> TERM_P2 = generateRule(""); // epsilon (empty)
//
//        List<String> FACTOR1 = generateRule("number_literal");
//        List<String> FACTOR2 = generateRule("id");
//
//        List<String> PRINT_CONTENT1 = generateRule("string_literal");
//        List<String> PRINT_CONTENT2 = generateRule("id");
//
//        List<String> IF_STMT1 = generateRule("if ( COM_STMT ) BEGIN_STMT IF_STMT_P");
//
//        List<String> IF_STMT_P1 = generateRule("else_if ( COM_STMT ) BEGIN_STMT IF_STMT_P");
//        List<String> IF_STMT_P2 = generateRule("else BEGIN_STMT");
//        List<String> IF_STMT_P3 = generateRule(""); // epsilon (empty)
//
//        List<String> PRINT1 = generateRule("print_line ( PRINT_CONTENT ) ;");
//        List<String> PRINT2 = generateRule("display ( PRINT_CONTENT ) ;");
//
//        List<String> WHILE_STMT1 = generateRule("while ( COM_STMT ) BEGIN_STMT");
//
//        List<String> FOR_STMT1 = generateRule("for ( DECLARATION COM_STMT ; OP_STMT ) BEGIN_STMT");
//
//        List<String> BREAK_STMT1 = generateRule("break ;");
//
//        List<String> COM_STMT1 = generateRule("TERM COM_STMT_P");
//
//        List<String> COM_STMT_P1 = generateRule("< FACTOR");
//        List<String> COM_STMT_P2 = generateRule("== FACTOR");
//        List<String> COM_STMT_P3 = generateRule(""); // epsilon (empty)
//
//        // Fill parsing table
//        parsingTable.put("S", new HashMap<>());
//        parsingTable.get("S").put("program", S);
//
//        parsingTable.put("BEGIN_BLOCK", new HashMap<>());
//        parsingTable.get("BEGIN_BLOCK").put("program", BEGIN_BLOCK1);
//
//        parsingTable.put("BEGIN_STMT", new HashMap<>());
//        parsingTable.get("BEGIN_STMT").put("begin", BEGIN_STMT1);
//
//        parsingTable.put("STMTS", new HashMap<>());
//        parsingTable.get("STMTS").put("int", STMTS1);
//        parsingTable.get("STMTS").put("integer", STMTS1);
//        parsingTable.get("STMTS").put("id", STMTS1);
//        parsingTable.get("STMTS").put("if", STMTS1);
//        parsingTable.get("STMTS").put("print_line", STMTS1);
//        parsingTable.get("STMTS").put("display", STMTS1);
//        parsingTable.get("STMTS").put("while", STMTS1);
//        parsingTable.get("STMTS").put("for", STMTS1);
//        parsingTable.get("STMTS").put("break", STMTS1);
//        parsingTable.get("STMTS").put("end", STMTS2);
//
//        parsingTable.put("STMT", new HashMap<>());
//        parsingTable.get("STMT").put("int", STMT1);
//        parsingTable.get("STMT").put("integer", STMT1);
//        parsingTable.get("STMT").put("id", STMT1);
//        parsingTable.get("STMT").put("if", STMT2);
//        parsingTable.get("STMT").put("print_line", STMT3);
//        parsingTable.get("STMT").put("display", STMT3);
//        parsingTable.get("STMT").put("while", STMT4);
//        parsingTable.get("STMT").put("for", STMT5);
//        parsingTable.get("STMT").put("break", STMT6);
//
//        parsingTable.put("DECLARATION_LIST", new HashMap<>());
//        parsingTable.get("DECLARATION_LIST").put("int", DECLARATION_LIST1);
//        parsingTable.get("DECLARATION_LIST").put("integer", DECLARATION_LIST1);
//        parsingTable.get("DECLARATION_LIST").put("id", DECLARATION_LIST1);
//
//        parsingTable.put("DECLARATION_LIST_P", new HashMap<>());
//        parsingTable.get("DECLARATION_LIST_P").put(",", DECLARATION_LIST_P1);
//        parsingTable.get("DECLARATION_LIST_P").put(";", DECLARATION_LIST_P2);
//
//        parsingTable.put("DECLARATION", new HashMap<>());
//        parsingTable.get("DECLARATION").put("int", DECLARATION1);
//        parsingTable.get("DECLARATION").put("integer", DECLARATION1);
//        parsingTable.get("DECLARATION").put("id", DECLARATION2);
//
//        parsingTable.put("TYPE", new HashMap<>());
//        parsingTable.get("TYPE").put("int", TYPE1);
//        parsingTable.get("TYPE").put("integer", TYPE2);
//
//        parsingTable.put("ASSIGNMENT", new HashMap<>());
//        parsingTable.get("ASSIGNMENT").put("=", ASSIGNMENT1);
//        parsingTable.get("ASSIGNMENT").put(",", ASSIGNMENT2);
//        parsingTable.get("ASSIGNMENT").put(";", ASSIGNMENT2);
//
//        parsingTable.put("ID_LIST", new HashMap<>());
//        parsingTable.get("ID_LIST").put("id", ID_LIST1);
//
//        parsingTable.put("ID_LIST_P", new HashMap<>());
//        parsingTable.get("ID_LIST_P").put(",", ID_LIST_P1);
//        parsingTable.get("ID_LIST_P").put(";", ID_LIST_P1);
//
//        parsingTable.put("OP_STMT", new HashMap<>());
//        parsingTable.get("OP_STMT").put("id", OP_STMT1);
//        parsingTable.get("OP_STMT").put("number_literal", OP_STMT1);
//
//        parsingTable.put("OP_STMT_P", new HashMap<>());
//        parsingTable.get("OP_STMT_P").put("+", OP_STMT_P1);
//        parsingTable.get("OP_STMT_P").put("++", OP_STMT_P2);
//        parsingTable.get("OP_STMT_P").put(";", OP_STMT_P3);
//        parsingTable.get("OP_STMT_P").put(",", OP_STMT_P3);
//        parsingTable.get("OP_STMT_P").put(")", OP_STMT_P3);
//
//        parsingTable.put("TERM", new HashMap<>());
//        parsingTable.get("TERM").put("id", TERM1);
//        parsingTable.get("TERM").put("number_literal", TERM1);
//
//        parsingTable.put("TERM_P", new HashMap<>());
//        parsingTable.get("TERM_P").put("*", TERM_P1);
//        parsingTable.get("TERM_P").put(";", TERM_P2);
//        parsingTable.get("TERM_P").put(",", TERM_P2);
//        parsingTable.get("TERM_P").put(")", TERM_P2);
//
//        parsingTable.put("FACTOR", new HashMap<>());
//        parsingTable.get("FACTOR").put("number_literal", FACTOR1);
//        parsingTable.get("FACTOR").put("id", FACTOR2);
//
//        parsingTable.put("PRINT_CONTENT", new HashMap<>());
//        parsingTable.get("PRINT_CONTENT").put("string_literal", PRINT_CONTENT1);
//        parsingTable.get("PRINT_CONTENT").put("id", PRINT_CONTENT2);
//
//        parsingTable.put("IF_STMT", new HashMap<>());
//        parsingTable.get("IF_STMT").put("if", IF_STMT1);
//
//        parsingTable.put("IF_STMT_P", new HashMap<>());
//        parsingTable.get("IF_STMT_P").put("else_if", IF_STMT_P1);
//        parsingTable.get("IF_STMT_P").put("else", IF_STMT_P2);
//        parsingTable.get("IF_STMT_P").put(";", IF_STMT_P3);
//        parsingTable.get("IF_STMT_P").put("end", IF_STMT_P3);
//
//        parsingTable.put("PRINT_STMT", new HashMap<>());
//        parsingTable.get("PRINT_STMT").put("print_line", PRINT1);
//        parsingTable.get("PRINT_STMT").put("display", PRINT2);
//
//        parsingTable.put("WHILE_STMT", new HashMap<>());
//        parsingTable.get("WHILE_STMT").put("while", WHILE_STMT1);
//
//        parsingTable.put("FOR_STMT", new HashMap<>());
//        parsingTable.get("FOR_STMT").put("for", FOR_STMT1);
//
//        parsingTable.put("BREAK_STMT", new HashMap<>());
//        parsingTable.get("BREAK_STMT").put("break", BREAK_STMT1);
//
//        parsingTable.put("COM_STMT", new HashMap<>());
//        parsingTable.get("COM_STMT").put("id", COM_STMT1);
//        parsingTable.get("COM_STMT").put("number_literal", COM_STMT1);
//
//        parsingTable.put("COM_STMT_P", new HashMap<>());
//        parsingTable.get("COM_STMT_P").put("<", COM_STMT_P1);
//        parsingTable.get("COM_STMT_P").put("==", COM_STMT_P2);
//        parsingTable.get("COM_STMT_P").put(")", COM_STMT_P3);
//    }


    public void generateActionTable() {
        // Example of populating the action table
        // (State, Symbol) -> (Action, Number)

        actionTable = new HashMap<>();

        addAction(0, "program", "SHIFT", 3);
        addAction(1, "$", "ACCEPT", 0);
        addAction(2, "begin", "SHIFT", 5);
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
        addAction(16, "(", "SHIFT", 31);
        addAction(17, "(", "SHIFT", 32);
        addAction(18, "(", "SHIFT", 33);
        addAction(19, "(", "SHIFT", 34);
        addAction(20, "(", "SHIFT", 35);
        addAction(21, ";", "SHIFT", 36);
        addAction(22, "id", "SHIFT", 26);
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
        addAction(27, "$", "REDUCE", 3);
        addAction(28, "end", "REDUCE", 4);
        addAction(29, ";", "SHIFT", 40);
        addAction(30, "id", "SHIFT", 44);
        addAction(30, "int", "SHIFT", 24);
        addAction(30, "integer", "SHIFT", 25);
        addAction(31, "id", "SHIFT", 49);
        addAction(31, "number_literal", "SHIFT", 48);
        addAction(32, "string_literal", "SHIFT", 51);
        addAction(32, "id", "SHIFT", 52);
        addAction(33, "string_literal", "SHIFT", 51);
        addAction(33, "id", "SHIFT", 52);
        addAction(34, "id", "SHIFT", 49);
        addAction(34, "number_literal", "SHIFT", 48);
        addAction(35, "id", "SHIFT", 58);
        addAction(35, "int", "SHIFT", 24);
        addAction(35, "integer", "SHIFT", 25);
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
        addAction(39, "id", "SHIFT", 64);
        addAction(39, "number_literal", "SHIFT", 63);
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
        addAction(42, "id", "SHIFT", 44);
        addAction(43, ";", "REDUCE", 16);
        addAction(43, ",", "REDUCE", 16);
        addAction(44, ";", "REDUCE", 20);
        addAction(44, ",", "REDUCE", 20);
        addAction(44, "=", "SHIFT", 68);
        addAction(45, ")", "SHIFT", 69);
        addAction(46, ")", "REDUCE", 46);
        addAction(46, "<", "SHIFT", 71);
        addAction(46, "==", "SHIFT", 72);
        addAction(47, "*", "SHIFT", 74);
        addAction(47, ")", "REDUCE", 29);
        addAction(47, "<", "REDUCE", 29);
        addAction(47, "==", "REDUCE", 29);
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
        addAction(55, ";", "SHIFT", 78);
        addAction(56, "id", "SHIFT", 58);
        addAction(57, ";", "REDUCE", 16);
        addAction(58, ";", "REDUCE", 20);
        addAction(58, "=", "SHIFT", 81);
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
        addAction(61, "+", "SHIFT", 83);
        addAction(61, "++", "SHIFT", 84);
        addAction(61, "if", "REDUCE", 26);
        addAction(61, "print_line", "REDUCE", 26);
        addAction(61, "display", "REDUCE", 26);
        addAction(61, "while", "REDUCE", 26);
        addAction(61, "for", "REDUCE", 26);
        addAction(61, "break", "REDUCE", 26);
        addAction(62, "id", "REDUCE", 29);
        addAction(62, "end", "REDUCE", 29);
        addAction(62, ";", "REDUCE", 29);
        addAction(62, ",", "REDUCE", 29);
        addAction(62, "int", "REDUCE", 29);
        addAction(62, "integer", "REDUCE", 29);
        addAction(62, "+", "REDUCE", 29);
        addAction(62, "++", "REDUCE", 29);
        addAction(62, "*", "SHIFT", 86);
        addAction(62, "if", "REDUCE", 29);
        addAction(62, "print_line", "REDUCE", 29);
        addAction(62, "display", "REDUCE", 29);
        addAction(62, "while", "REDUCE", 29);
        addAction(62, "for", "REDUCE", 29);
        addAction(62, "break", "REDUCE", 29);
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
        addAction(68, "id", "SHIFT", 92);
        addAction(68, "number_literal", "SHIFT", 91);
        addAction(69, "begin", "SHIFT", 94);
        addAction(70, ")", "REDUCE", 43);
        addAction(71, "id", "SHIFT", 97);
        addAction(71, "number_literal", "SHIFT", 96);
        addAction(72, "id", "SHIFT", 97);
        addAction(72, "number_literal", "SHIFT", 96);
        addAction(73, ")", "REDUCE", 27);
        addAction(73, "<", "REDUCE", 27);
        addAction(73, "==", "REDUCE", 27);
        addAction(74, "id", "SHIFT", 49);
        addAction(74, "number_literal", "SHIFT", 48);
        addAction(75, ";", "SHIFT", 100);
        addAction(76, ";", "SHIFT", 101);
        addAction(77, "begin", "SHIFT", 103);
        addAction(78, "id", "SHIFT", 108);
        addAction(78, "number_literal", "SHIFT", 107);
        addAction(79, ";", "REDUCE", 15);
        addAction(80, ";", "REDUCE", 22);
        addAction(81, "id", "SHIFT", 114);
        addAction(81, "number_literal", "SHIFT", 113);
        addAction(82, "id", "REDUCE", 23);
        addAction(82, "end", "REDUCE", 23);
        addAction(82, ";", "REDUCE", 23);
        addAction(82, ",", "REDUCE", 23);
        addAction(82, "int", "REDUCE", 23);
        addAction(82, "integer", "REDUCE", 23);
        addAction(82, "if", "REDUCE", 23);
        addAction(82, "print_line", "REDUCE", 23);
        addAction(82, "display", "REDUCE", 23);
        addAction(82, "while", "REDUCE", 23);
        addAction(82, "for", "REDUCE", 23);
        addAction(82, "break", "REDUCE", 23);
        addAction(83, "id", "SHIFT", 64);
        addAction(83, "number_literal", "SHIFT", 63);
        addAction(84, "id", "REDUCE", 25);
        addAction(84, "end", "REDUCE", 25);
        addAction(84, ";", "REDUCE", 25);
        addAction(84, ",", "REDUCE", 25);
        addAction(84, "int", "REDUCE", 25);
        addAction(84, "integer", "REDUCE", 25);
        addAction(84, "if", "REDUCE", 25);
        addAction(84, "print_line", "REDUCE", 25);
        addAction(84, "display", "REDUCE", 25);
        addAction(84, "while", "REDUCE", 25);
        addAction(84, "for", "REDUCE", 25);
        addAction(84, "break", "REDUCE", 25);
        addAction(85, "id", "REDUCE", 27);
        addAction(85, "end", "REDUCE", 27);
        addAction(85, ";", "REDUCE", 27);
        addAction(85, ",", "REDUCE", 27);
        addAction(85, "int", "REDUCE", 27);
        addAction(85, "integer", "REDUCE", 27);
        addAction(85, "+", "REDUCE", 27);
        addAction(85, "++", "REDUCE", 27);
        addAction(85, "if", "REDUCE", 27);
        addAction(85, "print_line", "REDUCE", 27);
        addAction(85, "display", "REDUCE", 27);
        addAction(85, "while", "REDUCE", 27);
        addAction(85, "for", "REDUCE", 27);
        addAction(85, "break", "REDUCE", 27);
        addAction(86, "id", "SHIFT", 64);
        addAction(86, "number_literal", "SHIFT", 63);
        addAction(87, ";", "REDUCE", 21);
        addAction(87, ",", "REDUCE", 21);
        addAction(88, ";", "REDUCE", 19);
        addAction(88, ",", "REDUCE", 19);
        addAction(89, ";", "REDUCE", 26);
        addAction(89, ",", "REDUCE", 26);
        addAction(89, "+", "SHIFT", 118);
        addAction(89, "++", "SHIFT", 119);
        addAction(90, ";", "REDUCE", 29);
        addAction(90, ",", "REDUCE", 29);
        addAction(90, "+", "REDUCE", 29);
        addAction(90, "++", "REDUCE", 29);
        addAction(90, "*", "SHIFT", 121);
        addAction(91, ";", "REDUCE", 30);
        addAction(91, ",", "REDUCE", 30);
        addAction(91, "+", "REDUCE", 30);
        addAction(91, "++", "REDUCE", 30);
        addAction(91, "*", "REDUCE", 30);
        addAction(92, ";", "REDUCE", 31);
        addAction(92, ",", "REDUCE", 31);
        addAction(92, "+", "REDUCE", 31);
        addAction(92, "++", "REDUCE", 31);
        addAction(92, "*", "REDUCE", 31);
        addAction(93, "id", "REDUCE", 37);
        addAction(93, "end", "REDUCE", 37);
        addAction(93, "int", "REDUCE", 37);
        addAction(93, "integer", "REDUCE", 37);
        addAction(93, "if", "REDUCE", 37);
        addAction(93, "else_if", "SHIFT", 123);
        addAction(93, "else", "SHIFT", 124);
        addAction(93, "print_line", "REDUCE", 37);
        addAction(93, "display", "REDUCE", 37);
        addAction(93, "while", "REDUCE", 37);
        addAction(93, "for", "REDUCE", 37);
        addAction(93, "break", "REDUCE", 37);
        addAction(94, "id", "SHIFT", 26);
        addAction(94, "end", "REDUCE", 5);
        addAction(94, "int", "SHIFT", 24);
        addAction(94, "integer", "SHIFT", 25);
        addAction(94, "if", "SHIFT", 16);
        addAction(94, "print_line", "SHIFT", 17);
        addAction(94, "display", "SHIFT", 18);
        addAction(94, "while", "SHIFT", 19);
        addAction(94, "for", "SHIFT", 20);
        addAction(94, "break", "SHIFT", 21);
        addAction(95, ")", "REDUCE", 44);
        addAction(96, ")", "REDUCE", 30);
        addAction(97, ")", "REDUCE", 31);
        addAction(98, ")", "REDUCE", 45);
        addAction(99, "*", "SHIFT", 74);
        addAction(99, ")", "REDUCE", 29);
        addAction(99, "<", "REDUCE", 29);
        addAction(99, "==", "REDUCE", 29);
        addAction(100, "id", "REDUCE", 38);
        addAction(100, "end", "REDUCE", 38);
        addAction(100, "int", "REDUCE", 38);
        addAction(100, "integer", "REDUCE", 38);
        addAction(100, "if", "REDUCE", 38);
        addAction(100, "print_line", "REDUCE", 38);
        addAction(100, "display", "REDUCE", 38);
        addAction(100, "while", "REDUCE", 38);
        addAction(100, "for", "REDUCE", 38);
        addAction(100, "break", "REDUCE", 38);
        addAction(101, "id", "REDUCE", 39);
        addAction(101, "end", "REDUCE", 39);
        addAction(101, "int", "REDUCE", 39);
        addAction(101, "integer", "REDUCE", 39);
        addAction(101, "if", "REDUCE", 39);
        addAction(101, "print_line", "REDUCE", 39);
        addAction(101, "display", "REDUCE", 39);
        addAction(101, "while", "REDUCE", 39);
        addAction(101, "for", "REDUCE", 39);
        addAction(101, "break", "REDUCE", 39);
        addAction(102, "id", "REDUCE", 40);
        addAction(102, "end", "REDUCE", 40);
        addAction(102, "int", "REDUCE", 40);
        addAction(102, "integer", "REDUCE", 40);
        addAction(102, "if", "REDUCE", 40);
        addAction(102, "print_line", "REDUCE", 40);
        addAction(102, "display", "REDUCE", 40);
        addAction(102, "while", "REDUCE", 40);
        addAction(102, "for", "REDUCE", 40);
        addAction(102, "break", "REDUCE", 40);
        addAction(103, "id", "SHIFT", 26);
        addAction(103, "end", "REDUCE", 5);
        addAction(103, "int", "SHIFT", 24);
        addAction(103, "integer", "SHIFT", 25);
        addAction(103, "if", "SHIFT", 16);
        addAction(103, "print_line", "SHIFT", 17);
        addAction(103, "display", "SHIFT", 18);
        addAction(103, "while", "SHIFT", 19);
        addAction(103, "for", "SHIFT", 20);
        addAction(103, "break", "SHIFT", 21);
        addAction(104, ";", "SHIFT", 128);
        addAction(105, ";", "REDUCE", 46);
        addAction(105, "<", "SHIFT", 130);
        addAction(105, "==", "SHIFT", 131);
        addAction(106, ";", "REDUCE", 29);
        addAction(106, "*", "SHIFT", 133);
        addAction(106, "<", "REDUCE", 29);
        addAction(106, "==", "REDUCE", 29);
        addAction(107, ";", "REDUCE", 30);
        addAction(107, "*", "REDUCE", 30);
        addAction(107, "<", "REDUCE", 30);
        addAction(107, "==", "REDUCE", 30);
        addAction(108, ";", "REDUCE", 31);
        addAction(108, "*", "REDUCE", 31);
        addAction(108, "<", "REDUCE", 31);
        addAction(108, "==", "REDUCE", 31);
        addAction(109, ";", "REDUCE", 21);
        addAction(110, ";", "REDUCE", 19);
        addAction(111, ";", "REDUCE", 26);
        addAction(111, "+", "SHIFT", 135);
        addAction(111, "++", "SHIFT", 136);
        addAction(112, ";", "REDUCE", 29);
        addAction(112, "+", "REDUCE", 29);
        addAction(112, "++", "REDUCE", 29);
        addAction(112, "*", "SHIFT", 138);
        addAction(113, ";", "REDUCE", 30);
        addAction(113, "+", "REDUCE", 30);
        addAction(113, "++", "REDUCE", 30);
        addAction(113, "*", "REDUCE", 30);
        addAction(114, ";", "REDUCE", 31);
        addAction(114, "+", "REDUCE", 31);
        addAction(114, "++", "REDUCE", 31);
        addAction(114, "*", "REDUCE", 31);
        addAction(115, "id", "REDUCE", 26);
        addAction(115, "end", "REDUCE", 26);
        addAction(115, ";", "REDUCE", 26);
        addAction(115, ",", "REDUCE", 26);
        addAction(115, "int", "REDUCE", 26);
        addAction(115, "integer", "REDUCE", 26);
        addAction(115, "+", "SHIFT", 83);
        addAction(115, "++", "SHIFT", 84);
        addAction(115, "if", "REDUCE", 26);
        addAction(115, "print_line", "REDUCE", 26);
        addAction(115, "display", "REDUCE", 26);
        addAction(115, "while", "REDUCE", 26);
        addAction(115, "for", "REDUCE", 26);
        addAction(115, "break", "REDUCE", 26);
        addAction(116, "id", "REDUCE", 29);
        addAction(116, "end", "REDUCE", 29);
        addAction(116, ";", "REDUCE", 29);
        addAction(116, ",", "REDUCE", 29);
        addAction(116, "int", "REDUCE", 29);
        addAction(116, "integer", "REDUCE", 29);
        addAction(116, "+", "REDUCE", 29);
        addAction(116, "++", "REDUCE", 29);
        addAction(116, "*", "SHIFT", 86);
        addAction(116, "if", "REDUCE", 29);
        addAction(116, "print_line", "REDUCE", 29);
        addAction(116, "display", "REDUCE", 29);
        addAction(116, "while", "REDUCE", 29);
        addAction(116, "for", "REDUCE", 29);
        addAction(116, "break", "REDUCE", 29);
        addAction(117, ";", "REDUCE", 23);
        addAction(117, ",", "REDUCE", 23);
        addAction(118, "id", "SHIFT", 92);
        addAction(118, "number_literal", "SHIFT", 91);
        addAction(119, ";", "REDUCE", 25);
        addAction(119, ",", "REDUCE", 25);
        addAction(120, ";", "REDUCE", 27);
        addAction(120, ",", "REDUCE", 27);
        addAction(120, "+", "REDUCE", 27);
        addAction(120, "++", "REDUCE", 27);
        addAction(121, "id", "SHIFT", 92);
        addAction(121, "number_literal", "SHIFT", 91);
        addAction(122, "id", "REDUCE", 34);
        addAction(122, "end", "REDUCE", 34);
        addAction(122, "int", "REDUCE", 34);
        addAction(122, "integer", "REDUCE", 34);
        addAction(122, "if", "REDUCE", 34);
        addAction(122, "print_line", "REDUCE", 34);
        addAction(122, "display", "REDUCE", 34);
        addAction(122, "while", "REDUCE", 34);
        addAction(122, "for", "REDUCE", 34);
        addAction(122, "break", "REDUCE", 34);
        addAction(123, "(", "SHIFT", 143);
        addAction(124, "begin", "SHIFT", 103);
        addAction(125, "end", "SHIFT", 145);
        addAction(126, ")", "REDUCE", 28);
        addAction(126, "<", "REDUCE", 28);
        addAction(126, "==", "REDUCE", 28);
        addAction(127, "end", "SHIFT", 146);
        addAction(128, "id", "SHIFT", 151);
        addAction(128, "number_literal", "SHIFT", 150);
        addAction(129, ";", "REDUCE", 43);
        addAction(130, "id", "SHIFT", 154);
        addAction(130, "number_literal", "SHIFT", 153);
        addAction(131, "id", "SHIFT", 154);
        addAction(131, "number_literal", "SHIFT", 153);
        addAction(132, ";", "REDUCE", 27);
        addAction(132, "<", "REDUCE", 27);
        addAction(132, "==", "REDUCE", 27);
        addAction(133, "id", "SHIFT", 108);
        addAction(133, "number_literal", "SHIFT", 107);
        addAction(134, ";", "REDUCE", 23);
        addAction(135, "id", "SHIFT", 114);
        addAction(135, "number_literal", "SHIFT", 113);
        addAction(136, ";", "REDUCE", 25);
        addAction(137, ";", "REDUCE", 27);
        addAction(137, "+", "REDUCE", 27);
        addAction(137, "++", "REDUCE", 27);
        addAction(138, "id", "SHIFT", 114);
        addAction(138, "number_literal", "SHIFT", 113);
        addAction(139, "id", "REDUCE", 24);
        addAction(139, "end", "REDUCE", 24);
        addAction(139, ";", "REDUCE", 24);
        addAction(139, ",", "REDUCE", 24);
        addAction(139, "int", "REDUCE", 24);
        addAction(139, "integer", "REDUCE", 24);
        addAction(139, "if", "REDUCE", 24);
        addAction(139, "print_line", "REDUCE", 24);
        addAction(139, "display", "REDUCE", 24);
        addAction(139, "while", "REDUCE", 24);
        addAction(139, "for", "REDUCE", 24);
        addAction(139, "break", "REDUCE", 24);
        addAction(140, "id", "REDUCE", 28);
        addAction(140, "end", "REDUCE", 28);
        addAction(140, ";", "REDUCE", 28);
        addAction(140, ",", "REDUCE", 28);
        addAction(140, "int", "REDUCE", 28);
        addAction(140, "integer", "REDUCE", 28);
        addAction(140, "+", "REDUCE", 28);
        addAction(140, "++", "REDUCE", 28);
        addAction(140, "if", "REDUCE", 28);
        addAction(140, "print_line", "REDUCE", 28);
        addAction(140, "display", "REDUCE", 28);
        addAction(140, "while", "REDUCE", 28);
        addAction(140, "for", "REDUCE", 28);
        addAction(140, "break", "REDUCE", 28);
        addAction(141, ";", "REDUCE", 26);
        addAction(141, ",", "REDUCE", 26);
        addAction(141, "+", "SHIFT", 118);
        addAction(141, "++", "SHIFT", 119);
        addAction(142, ";", "REDUCE", 29);
        addAction(142, ",", "REDUCE", 29);
        addAction(142, "+", "REDUCE", 29);
        addAction(142, "++", "REDUCE", 29);
        addAction(142, "*", "SHIFT", 121);
        addAction(143, "id", "SHIFT", 49);
        addAction(143, "number_literal", "SHIFT", 48);
        addAction(144, "id", "REDUCE", 36);
        addAction(144, "end", "REDUCE", 36);
        addAction(144, "int", "REDUCE", 36);
        addAction(144, "integer", "REDUCE", 36);
        addAction(144, "if", "REDUCE", 36);
        addAction(144, "print_line", "REDUCE", 36);
        addAction(144, "display", "REDUCE", 36);
        addAction(144, "while", "REDUCE", 36);
        addAction(144, "for", "REDUCE", 36);
        addAction(144, "break", "REDUCE", 36);
        addAction(145, "id", "REDUCE", 3);
        addAction(145, "end", "REDUCE", 3);
        addAction(145, "int", "REDUCE", 3);
        addAction(145, "integer", "REDUCE", 3);
        addAction(145, "if", "REDUCE", 3);
        addAction(145, "else_if", "REDUCE", 3);
        addAction(145, "else", "REDUCE", 3);
        addAction(145, "print_line", "REDUCE", 3);
        addAction(145, "display", "REDUCE", 3);
        addAction(145, "while", "REDUCE", 3);
        addAction(145, "for", "REDUCE", 3);
        addAction(145, "break", "REDUCE", 3);
        addAction(146, "id", "REDUCE", 3);
        addAction(146, "end", "REDUCE", 3);
        addAction(146, "int", "REDUCE", 3);
        addAction(146, "integer", "REDUCE", 3);
        addAction(146, "if", "REDUCE", 3);
        addAction(146, "print_line", "REDUCE", 3);
        addAction(146, "display", "REDUCE", 3);
        addAction(146, "while", "REDUCE", 3);
        addAction(146, "for", "REDUCE", 3);
        addAction(146, "break", "REDUCE", 3);
        addAction(147, ")", "SHIFT", 162);
        addAction(148, "+", "SHIFT", 164);
        addAction(148, "++", "SHIFT", 165);
        addAction(148, ")", "REDUCE", 26);
        addAction(149, "+", "REDUCE", 29);
        addAction(149, "++", "REDUCE", 29);
        addAction(149, "*", "SHIFT", 167);
        addAction(149, ")", "REDUCE", 29);
        addAction(150, "+", "REDUCE", 30);
        addAction(150, "++", "REDUCE", 30);
        addAction(150, "*", "REDUCE", 30);
        addAction(150, ")", "REDUCE", 30);
        addAction(151, "+", "REDUCE", 31);
        addAction(151, "++", "REDUCE", 31);
        addAction(151, "*", "REDUCE", 31);
        addAction(151, ")", "REDUCE", 31);
        addAction(152, ";", "REDUCE", 44);
        addAction(153, ";", "REDUCE", 30);
        addAction(154, ";", "REDUCE", 31);
        addAction(155, ";", "REDUCE", 45);
        addAction(156, ";", "REDUCE", 29);
        addAction(156, "*", "SHIFT", 133);
        addAction(156, "<", "REDUCE", 29);
        addAction(156, "==", "REDUCE", 29);
        addAction(157, ";", "REDUCE", 26);
        addAction(157, "+", "SHIFT", 135);
        addAction(157, "++", "SHIFT", 136);
        addAction(158, ";", "REDUCE", 29);
        addAction(158, "+", "REDUCE", 29);
        addAction(158, "++", "REDUCE", 29);
        addAction(158, "*", "SHIFT", 138);
        addAction(159, ";", "REDUCE", 24);
        addAction(159, ",", "REDUCE", 24);
        addAction(160, ";", "REDUCE", 28);
        addAction(160, ",", "REDUCE", 28);
        addAction(160, "+", "REDUCE", 28);
        addAction(160, "++", "REDUCE", 28);
        addAction(161, ")", "SHIFT", 171);
        addAction(162, "begin", "SHIFT", 103);
        addAction(163, ")", "REDUCE", 23);
        addAction(164, "id", "SHIFT", 151);
        addAction(164, "number_literal", "SHIFT", 150);
        addAction(165, ")", "REDUCE", 25);
        addAction(166, "+", "REDUCE", 27);
        addAction(166, "++", "REDUCE", 27);
        addAction(166, ")", "REDUCE", 27);
        addAction(167, "id", "SHIFT", 151);
        addAction(167, "number_literal", "SHIFT", 150);
        addAction(168, ";", "REDUCE", 28);
        addAction(168, "<", "REDUCE", 28);
        addAction(168, "==", "REDUCE", 28);
        addAction(169, ";", "REDUCE", 24);
        addAction(170, ";", "REDUCE", 28);
        addAction(170, "+", "REDUCE", 28);
        addAction(170, "++", "REDUCE", 28);
        addAction(171, "begin", "SHIFT", 94);
        addAction(172, "id", "REDUCE", 41);
        addAction(172, "end", "REDUCE", 41);
        addAction(172, "int", "REDUCE", 41);
        addAction(172, "integer", "REDUCE", 41);
        addAction(172, "if", "REDUCE", 41);
        addAction(172, "print_line", "REDUCE", 41);
        addAction(172, "display", "REDUCE", 41);
        addAction(172, "while", "REDUCE", 41);
        addAction(172, "for", "REDUCE", 41);
        addAction(172, "break", "REDUCE", 41);
        addAction(173, "+", "SHIFT", 164);
        addAction(173, "++", "SHIFT", 165);
        addAction(173, ")", "REDUCE", 26);
        addAction(174, "+", "REDUCE", 29);
        addAction(174, "++", "REDUCE", 29);
        addAction(174, "*", "SHIFT", 167);
        addAction(174, ")", "REDUCE", 29);
        addAction(175, "id", "REDUCE", 37);
        addAction(175, "end", "REDUCE", 37);
        addAction(175, "int", "REDUCE", 37);
        addAction(175, "integer", "REDUCE", 37);
        addAction(175, "if", "REDUCE", 37);
        addAction(175, "else_if", "SHIFT", 123);
        addAction(175, "else", "SHIFT", 124);
        addAction(175, "print_line", "REDUCE", 37);
        addAction(175, "display", "REDUCE", 37);
        addAction(175, "while", "REDUCE", 37);
        addAction(175, "for", "REDUCE", 37);
        addAction(175, "break", "REDUCE", 37);
        addAction(176, ")", "REDUCE", 24);
        addAction(177, "+", "REDUCE", 28);
        addAction(177, "++", "REDUCE", 28);
        addAction(177, ")", "REDUCE", 28);
        addAction(178, "id", "REDUCE", 35);
        addAction(178, "end", "REDUCE", 35);
        addAction(178, "int", "REDUCE", 35);
        addAction(178, "integer", "REDUCE", 35);
        addAction(178, "if", "REDUCE", 35);
        addAction(178, "print_line", "REDUCE", 35);
        addAction(178, "display", "REDUCE", 35);
        addAction(178, "while", "REDUCE", 35);
        addAction(178, "for", "REDUCE", 35);
        addAction(178, "break", "REDUCE", 35);

    }


    // Method to generate the goto table
    public void generateGotoTable() {
        goToTable = new HashMap<>();

        addGoTo(0, "S", 1);
        addGoTo(0, "BEGIN_BLOCK", 2);
        addGoTo(2, "BEGIN_STMT", 4);
        addGoTo(5, "STMTS", 7);
        addGoTo(5, "STMT", 8);
        addGoTo(5, "DECLARATION_LIST", 9);
        addGoTo(5, "DECLARATION", 15);
        addGoTo(5, "TYPE", 22);
        addGoTo(5, "ID_LIST", 23);
        addGoTo(5, "IF_STMT", 10);
        addGoTo(5, "PRINT_STMT", 11);
        addGoTo(5, "WHILE_STMT", 12);
        addGoTo(5, "FOR_STMT", 13);
        addGoTo(5, "BREAK_STMT", 14);
        addGoTo(8, "STMTS", 28);
        addGoTo(8, "STMT", 8);
        addGoTo(8, "DECLARATION_LIST", 9);
        addGoTo(8, "DECLARATION", 15);
        addGoTo(8, "TYPE", 22);
        addGoTo(8, "ID_LIST", 23);
        addGoTo(8, "IF_STMT", 10);
        addGoTo(8, "PRINT_STMT", 11);
        addGoTo(8, "WHILE_STMT", 12);
        addGoTo(8, "FOR_STMT", 13);
        addGoTo(8, "BREAK_STMT", 14);
        addGoTo(15, "DECLARATION_LIST_P", 29);
        addGoTo(22, "ID_LIST", 37);
        addGoTo(26, "ASSIGNMENT", 38);
        addGoTo(30, "DECLARATION", 41);
        addGoTo(30, "TYPE", 42);
        addGoTo(30, "ID_LIST", 43);
        addGoTo(31, "TERM", 46);
        addGoTo(31, "FACTOR", 47);
        addGoTo(31, "COM_STMT", 45);
        addGoTo(32, "PRINT_CONTENT", 50);
        addGoTo(33, "PRINT_CONTENT", 53);
        addGoTo(34, "TERM", 46);
        addGoTo(34, "FACTOR", 47);
        addGoTo(34, "COM_STMT", 54);
        addGoTo(35, "DECLARATION", 55);
        addGoTo(35, "TYPE", 56);
        addGoTo(35, "ID_LIST", 57);
        addGoTo(38, "ID_LIST_P", 59);
        addGoTo(39, "OP_STMT", 60);
        addGoTo(39, "TERM", 61);
        addGoTo(39, "FACTOR", 62);
        addGoTo(41, "DECLARATION_LIST_P", 65);
        addGoTo(42, "ID_LIST", 66);
        addGoTo(44, "ASSIGNMENT", 67);
        addGoTo(46, "COM_STMT_P", 70);
        addGoTo(47, "TERM_P", 73);
        addGoTo(56, "ID_LIST", 79);
        addGoTo(58, "ASSIGNMENT", 80);
        addGoTo(61, "OP_STMT_P", 82);
        addGoTo(62, "TERM_P", 85);
        addGoTo(67, "ID_LIST_P", 87);
        addGoTo(68, "OP_STMT", 88);
        addGoTo(68, "TERM", 89);
        addGoTo(68, "FACTOR", 90);
        addGoTo(69, "BEGIN_STMT", 93);
        addGoTo(71, "FACTOR", 95);
        addGoTo(72, "FACTOR", 98);
        addGoTo(74, "FACTOR", 99);
        addGoTo(77, "BEGIN_STMT", 102);
        addGoTo(78, "TERM", 105);
        addGoTo(78, "FACTOR", 106);
        addGoTo(78, "COM_STMT", 104);
        addGoTo(80, "ID_LIST_P", 109);
        addGoTo(81, "OP_STMT", 110);
        addGoTo(81, "TERM", 111);
        addGoTo(81, "FACTOR", 112);
        addGoTo(83, "TERM", 115);
        addGoTo(83, "FACTOR", 62);
        addGoTo(86, "FACTOR", 116);
        addGoTo(89, "OP_STMT_P", 117);
        addGoTo(90, "TERM_P", 120);
        addGoTo(93, "IF_STMT_P", 122);
        addGoTo(94, "STMTS", 125);
        addGoTo(94, "STMT", 8);
        addGoTo(94, "DECLARATION_LIST", 9);
        addGoTo(94, "DECLARATION", 15);
        addGoTo(94, "TYPE", 22);
        addGoTo(94, "ID_LIST", 23);
        addGoTo(94, "IF_STMT", 10);
        addGoTo(94, "PRINT_STMT", 11);
        addGoTo(94, "WHILE_STMT", 12);
        addGoTo(94, "FOR_STMT", 13);
        addGoTo(94, "BREAK_STMT", 14);
        addGoTo(99, "TERM_P", 126);
        addGoTo(103, "STMTS", 127);
        addGoTo(103, "STMT", 8);
        addGoTo(103, "DECLARATION_LIST", 9);
        addGoTo(103, "DECLARATION", 15);
        addGoTo(103, "TYPE", 22);
        addGoTo(103, "ID_LIST", 23);
        addGoTo(103, "IF_STMT", 10);
        addGoTo(103, "PRINT_STMT", 11);
        addGoTo(103, "WHILE_STMT", 12);
        addGoTo(103, "FOR_STMT", 13);
        addGoTo(103, "BREAK_STMT", 14);
        addGoTo(105, "COM_STMT_P", 129);
        addGoTo(106, "TERM_P", 132);
        addGoTo(111, "OP_STMT_P", 134);
        addGoTo(112, "TERM_P", 137);
        addGoTo(115, "OP_STMT_P", 139);
        addGoTo(116, "TERM_P", 140);
        addGoTo(118, "TERM", 141);
        addGoTo(118, "FACTOR", 90);
        addGoTo(121, "FACTOR", 142);
        addGoTo(124, "BEGIN_STMT", 144);
        addGoTo(128, "OP_STMT", 147);
        addGoTo(128, "TERM", 148);
        addGoTo(128, "FACTOR", 149);
        addGoTo(130, "FACTOR", 152);
        addGoTo(131, "FACTOR", 155);
        addGoTo(133, "FACTOR", 156);
        addGoTo(135, "TERM", 157);
        addGoTo(135, "FACTOR", 112);
        addGoTo(138, "FACTOR", 158);
        addGoTo(141, "OP_STMT_P", 159);
        addGoTo(142, "TERM_P", 160);
        addGoTo(143, "TERM", 46);
        addGoTo(143, "FACTOR", 47);
        addGoTo(143, "COM_STMT", 161);
        addGoTo(148, "OP_STMT_P", 163);
        addGoTo(149, "TERM_P", 166);
        addGoTo(156, "TERM_P", 168);
        addGoTo(157, "OP_STMT_P", 169);
        addGoTo(158, "TERM_P", 170);
        addGoTo(162, "BEGIN_STMT", 172);
        addGoTo(164, "TERM", 173);
        addGoTo(164, "FACTOR", 149);
        addGoTo(167, "FACTOR", 174);
        addGoTo(171, "BEGIN_STMT", 175);
        addGoTo(173, "OP_STMT_P", 176);
        addGoTo(174, "TERM_P", 177);
        addGoTo(175, "IF_STMT_P", 178);

    }

    private void addAction(int state, String symbol, String action, int number) {
        // Retrieve or create the HashMap<String, List<String>> for the state
        HashMap<String, List<String>> stateMap = actionTable.computeIfAbsent(state, k -> new HashMap<>());

        // Retrieve or create the List<String> for the symbol
        List<String> actionList = stateMap.computeIfAbsent(symbol, k -> new ArrayList<>());

        // Create the action string and add it to the list
        String statenum = String.valueOf(number);
        actionList.add(action);
        actionList.add(statenum);
    }

    public void addGoTo(int state, String nonterminal, int nextState) {
        if (!goToTable.containsKey(state)) {
            goToTable.put(state, new HashMap<>());
        }
        goToTable.get(state).put(nonterminal, nextState);
    }

    public void stackInit(Token token_table) {
        table = Token.getTable();

        table.add(new Token.TokenEntry("$", "$"));
        idx = 0;
        max_len = table.size();

        declared_variables = new ArrayList<>();
        generate_stack();

        generateActionTable();
        generateGotoTable();
        generateProductionTable();

//        printSymbol(symbolStack, "printing");
//
//        printProductionTable();
//        printActionTable();
//        printGoToTable();


        start_parser();
        //System.out.println(pass);
    }

    private void printProductionTable() {
        System.out.println("Production Table:");
        for (Map.Entry<Integer, HashMap<String, List<String>>> entry : productionTable.entrySet()) {
            int index = entry.getKey();
            HashMap<String, List<String>> production = entry.getValue();
            System.out.print(index + ":");
            for (Map.Entry<String, List<String>> prodEntry : production.entrySet()) {
                String lhs = prodEntry.getKey();
                List<String> rhs = prodEntry.getValue();
                System.out.println("  " + lhs + " -> " + String.join(" ", rhs));
            }
            System.out.println();
        }
    }

    private void printActionTable() {
        System.out.println("Action Table: ");
        for (Map.Entry<Integer, HashMap<String, List<String>>> entry : actionTable.entrySet()) {
            int state = entry.getKey();
            HashMap<String, List<String>> stateMap = entry.getValue();
            System.out.print(state);

            for (Map.Entry<String, List<String>> symbolEntry : stateMap.entrySet()) {
                String symbol = symbolEntry.getKey();
                List<String> actionList = symbolEntry.getValue();

                System.out.print("  " + symbol);
                for (String action : actionList) {
                    System.out.print("      " + action);
                }
            }
            System.out.println();
        }
    }

    private void printGoToTable() {
        System.out.println("GoTo Table:");
        for (Map.Entry<Integer, HashMap<String, Integer>> entry : goToTable.entrySet()) {
            int state = entry.getKey();
            HashMap<String, Integer> goToEntries = entry.getValue();
            System.out.print(state);
            for (Map.Entry<String, Integer> goToEntry : goToEntries.entrySet()) {
                String nonterminal = goToEntry.getKey();
                int nextState = goToEntry.getValue();
                System.out.print(":  " + nonterminal + " -> " + nextState);
            }
            System.out.println();
        }
    }


    private Token.TokenEntry getToken() {
        if(idx > max_len - 1) {
            if(symbolStack.pop().equals("$")) {
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

    private void shift(String symbol, int nextState) {
        symbolStack.push(symbol);
        stateStack.push(nextState);
        //idx++; // Remove the shifted token
//        printState(stateStack, symbol);
        printSymbol(symbolStack, "S");
        idx++;
//        System.out.println("Shift Current: " + stateStack.peek() + " " + symbolStack.peek());
    }

    private void reduce(String curr, int productionIndex) {
        HashMap<String, List<String>> production = productionTable.get(productionIndex);
        String lhs = null;
        List<String> rhs = null;
        if (production != null) {
            for (Map.Entry<String, List<String>> entry : production.entrySet()) {
                lhs = entry.getKey();      // Get the left-hand side
//                System.out.println(lhs);
                rhs = entry.getValue();    // Get the right-hand side
//                System.out.println(rhs.size());
                break;                     // Assuming only one entry in production HashMap
            }
        }


        if (rhs.get(0) != "") {
            for (int i = 0; i < rhs.size(); i++) {
//                System.out.println("Popping: " + stateStack.peek() + " " + symbolStack.peek());
                symbolStack.pop();
                stateStack.pop();

            }
        }

        symbolStack.push(lhs);
        //symbolStack.push(curr);
//        System.out.println("Reduce Current: " + stateStack.peek() + " " + symbolStack.peek());

        int next_state = goToTable.get(stateStack.peek()).get(lhs);
//        System.out.println(next_state);
        stateStack.push(next_state);

//        printState(stateStack, lhs);
        printSymbol(symbolStack, "R");
//        idx++;
    }

    /*
    initialize pointer on input to be index 0
    stack.push(0)  // Start by adding state 0 onto the stack

    loop:
        current_state = stack.top()
        input_symbol = input[index]

        action = getAction(current_state, input_symbol)
        if action.type == 'ACCEPT':
            break

        elif action.type == 'SHIFT':
            next_state = action.number
            stack.push(input_symbol)
            stack.push(next_state)
            index += 1

        elif action.type == 'REDUCE':
            production = getProduction(action.number)
            rhs_length = len(production.rhs)
            for i in range(2 * rhs_length):
                stack.pop()
            lhs_symbol = production.lhs
            current_state = stack.top()
            next_state = getGoto(current_state, lhs_symbol)
            stack.push(lhs_symbol)
            stack.push(next_state)

        else:
            error("Unexpected action")

    end loop

     */

    public void start_parser() {
        while (idx < max_len) {
            token = getToken(); // Assuming this initializes or fetches the first token
            current_state = stateStack.peek();
            if(token.getTokenType().equals("id") || token.getTokenType().equals("number_literal") || token.getTokenType().equals("string_literal")) lookAtSymbol = token.getTokenType();
//            else if(token.getTokenType().equals("num"))
            else lookAtSymbol = token.getValue(); // Assuming getTokenType() returns the type of the current token
//            System.out.println("Current: " + current_state + " " + lookAtSymbol);
            if (actionTable.containsKey(current_state) && actionTable.get(current_state).containsKey(lookAtSymbol)) {
                List<String> action = actionTable.get(current_state).get(lookAtSymbol);
                String actionType = action.get(0);
                int actionValue = Integer.parseInt(action.get(1));

                if (actionType.equals("SHIFT")) {
                    shift(lookAtSymbol, actionValue);
                    //lookAtSymbol = getToken(); // Get the next token
                } else if (actionType.equals("REDUCE")) {
                    int productionIndex = actionValue;
                    reduce(lookAtSymbol, productionIndex);
                } else if (actionType.equals("ACCEPT")) {
                    System.out.println("Accepted\nParsing completed successfully.");
                    break; // Exit the parsing loop upon acceptance
                } else {
                    // Handle error or unexpected action
//                    System.err.println("Error: Unexpected action type " + actionType);
                    break; // Exit the parsing loop on error
                }
            } else {
                // Handle error: no valid action found in the action table
                System.err.println("Error: Shifting error");
                break; // Exit the parsing loop on error
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

    public void printSymbol(Stack<String> stack, String action) {
        System.out.print("[" + action + "] [" /*+ stack.get(0) + ": " + token.getValue() + "] ["*/);

        for (int i=0; i< stack.size(); i++) {
            System.out.print(stack.get(i));
            if(i!=stack.size()-1) System.out.print( ", ");
        }
        System.out.println("]");
    }

    public void printState(Stack<Integer> stack, String action) {
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
