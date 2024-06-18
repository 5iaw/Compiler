import java.util.*;

public class LR1Parser {

    public int idx;
    private Stack<String> stack;
    private HashMap<Integer, HashMap<String, List<String>>> productionTable;
    public List<Token.TokenEntry> table;
    public int max_len;
    public Token.TokenEntry token;
    public List<String> declared_variables;
    private HashMap<Integer, HashMap<String, Pair<String, Integer>>> actionTable;
    private HashMap<Integer, HashMap<String, Integer>> goToTable;

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

    private void addProduction(int index, String lhs, List<String> rhs) {
        HashMap<String, List<String>> production = new HashMap<>();
        production.put(lhs, rhs);
        productionTable.put(index, production);
    }

    private void generateProductionTable() {
        // Define and add production rules with unique indices
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

        addProduction(12, "DECLARATION_LIST", Arrays.asList("DECLARATION", "DECLARATION_LIST_P"));

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
        addProduction(33, "PRINT_CONTENT", Arrays.asList("identifier"));

        addProduction(34, "IF_STMT", Arrays.asList("if", "(", "COM_STMT", ")", "BEGIN_STMT", "IF_STMT_P"));

        addProduction(35, "IF_STMT_P", Arrays.asList("else_if", "(", "COM_STMT", ")", "BEGIN_STMT", "IF_STMT_P"));
        addProduction(36, "IF_STMT_P", Arrays.asList("else", "BEGIN_STMT"));
        addProduction(37, "IF_STMT_P", Arrays.asList("")); // epsilon

        addProduction(38, "PRINT_STMT", Arrays.asList("print_line", "(", "PRINT_CONTENT", ")", ";"));
        addProduction(39, "PRINT_STMT", Arrays.asList("display", "(", "PRINT_CONTENT", ")", ";"));

        addProduction(40, "WHILE_STMT", Arrays.asList("while", "(", "COM_STMT", ")", "BEGIN_STMT"));

        addProduction(41, "FOR_STMT", Arrays.asList("for", "(", "DECLARATION", "COM_STMT", ";", "OP_STMT", ")", "BEGIN_STMT"));

        addProduction(42, "BREAK_STMT", Arrays.asList("break", ";"));

        addProduction(43, "COM_STMT", Arrays.asList("TERM", "COM_STMT_P"));

        addProduction(44, "COM_STMT_P", Arrays.asList("<", "FACTOR"));
        addProduction(45, "COM_STMT_P", Arrays.asList("==", "FACTOR"));
        addProduction(46, "COM_STMT_P", Arrays.asList("")); // epsilon
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

        addAction(3, "program", "SHIFT", 3)
        addAction(5, "begin", "SHIFT", 5)
        addAction(6, "id", "SHIFT", 6)
        addAction(7, "$", "REDUCE", 1)
        addAction(8, "id", "SHIFT", 26)
        addAction(8, "end", "REDUCE", 5)
        addAction(8, "int", "SHIFT", 24)
        addAction(8, "integer", "SHIFT", 25)
        addAction(8, "if", "SHIFT", 16)
        addAction(8, "print_line", "SHIFT", 17)
        addAction(8, "display", "SHIFT", 18)
        addAction(8, "while", "SHIFT", 19)
        addAction(8, "for", "SHIFT", 20)
        addAction(8, "break", "SHIFT", 21)
        addAction(9, "begin", "REDUCE", 2)
        addAction(10, "end", "SHIFT", 27)
        addAction(11, "id", "SHIFT", 26)
        addAction(11, "end", "REDUCE", 5)
        addAction(11, "int", "SHIFT", 24)
        addAction(11, "integer", "SHIFT", 25)
        addAction(11, "if", "SHIFT", 16)
        addAction(11, "print_line", "SHIFT", 17)
        addAction(11, "display", "SHIFT", 18)
        addAction(11, "while", "SHIFT", 19)
        addAction(11, "for", "SHIFT", 20)
        addAction(11, "break", "SHIFT", 21)
        addAction(12, "id", "REDUCE", 6)
        addAction(12, "end", "REDUCE", 6)
        addAction(12, "int", "REDUCE", 6)
        addAction(12, "integer", "REDUCE", 6)
        addAction(12, "if", "REDUCE", 6)
        addAction(12, "print_line", "REDUCE", 6)
        addAction(12, "display", "REDUCE", 6)
        addAction(12, "while", "REDUCE", 6)
        addAction(12, "for", "REDUCE", 6)
        addAction(12, "break", "REDUCE", 6)
        addAction(13, "id", "REDUCE", 7)
        addAction(13, "end", "REDUCE", 7)
        addAction(13, "int", "REDUCE", 7)
        addAction(13, "integer", "REDUCE", 7)
        addAction(13, "if", "REDUCE", 7)
        addAction(13, "print_line", "REDUCE", 7)
        addAction(13, "display", "REDUCE", 7)
        addAction(13, "while", "REDUCE", 7)
        addAction(13, "for", "REDUCE", 7)
        addAction(13, "break", "REDUCE", 7)
        addAction(14, "id", "REDUCE", 8)
        addAction(14, "end", "REDUCE", 8)
        addAction(14, "int", "REDUCE", 8)
        addAction(14, "integer", "REDUCE", 8)
        addAction(14, "if", "REDUCE", 8)
        addAction(14, "print_line", "REDUCE", 8)
        addAction(14, "display", "REDUCE", 8)
        addAction(14, "while", "REDUCE", 8)
        addAction(14, "for", "REDUCE", 8)
        addAction(14, "break", "REDUCE", 8)
        addAction(15, "id", "REDUCE", 9)
        addAction(15, "end", "REDUCE", 9)
        addAction(15, "int", "REDUCE", 9)
        addAction(15, "integer", "REDUCE", 9)
        addAction(15, "if", "REDUCE", 9)
        addAction(15, "print_line", "REDUCE", 9)
        addAction(15, "display", "REDUCE", 9)
        addAction(15, "while", "REDUCE", 9)
        addAction(15, "for", "REDUCE", 9)
        addAction(15, "break", "REDUCE", 9)
        addAction(16, "id", "REDUCE", 10)
        addAction(16, "end", "REDUCE", 10)
        addAction(16, "int", "REDUCE", 10)
        addAction(16, "integer", "REDUCE", 10)
        addAction(16, "if", "REDUCE", 10)
        addAction(16, "print_line", "REDUCE", 10)
        addAction(16, "display", "REDUCE", 10)
        addAction(16, "while", "REDUCE", 10)
        addAction(16, "for", "REDUCE", 10)
        addAction(16, "break", "REDUCE", 10)
        addAction(17, "id", "REDUCE", 11)
        addAction(17, "end", "REDUCE", 11)
        addAction(17, "int", "REDUCE", 11)
        addAction(17, "integer", "REDUCE", 11)
        addAction(17, "if", "REDUCE", 11)
        addAction(17, "print_line", "REDUCE", 11)
        addAction(17, "display", "REDUCE", 11)
        addAction(17, "while", "REDUCE", 11)
        addAction(17, "for", "REDUCE", 11)
        addAction(17, "break", "REDUCE", 11)
        addAction(18, ";", "REDUCE", 14)
        addAction(18, ",", "SHIFT", 30)
        addAction(19, "(", "SHIFT", 31)
        addAction(20, "(", "SHIFT", 32)
        addAction(21, "(", "SHIFT", 33)
        addAction(22, "(", "SHIFT", 34)
        addAction(23, "(", "SHIFT", 35)
        addAction(24, ";", "SHIFT", 36)
        addAction(25, "id", "SHIFT", 26)
        addAction(26, "id", "REDUCE", 16)
        addAction(26, "end", "REDUCE", 16)
        addAction(26, ";", "REDUCE", 16)
        addAction(26, ",", "REDUCE", 16)
        addAction(26, "int", "REDUCE", 16)
        addAction(26, "integer", "REDUCE", 16)
        addAction(26, "if", "REDUCE", 16)
        addAction(26, "print_line", "REDUCE", 16)
        addAction(26, "display", "REDUCE", 16)
        addAction(26, "while", "REDUCE", 16)
        addAction(26, "for", "REDUCE", 16)
        addAction(26, "break", "REDUCE", 16)
        addAction(27, "id", "REDUCE", 17)
        addAction(28, "id", "REDUCE", 18)
        addAction(29, "id", "REDUCE", 20)
        addAction(29, "end", "REDUCE", 20)
        addAction(29, ";", "REDUCE", 20)
        addAction(29, ",", "REDUCE", 20)
        addAction(29, "int", "REDUCE", 20)
        addAction(29, "integer", "REDUCE", 20)
        addAction(29, "=", "SHIFT", 39)
        addAction(29, "if", "REDUCE", 20)
        addAction(29, "print_line", "REDUCE", 20)
        addAction(29, "display", "REDUCE", 20)
        addAction(29, "while", "REDUCE", 20)
        addAction(29, "for", "REDUCE", 20)
        addAction(29, "break", "REDUCE", 20)
        addAction(30, "$", "REDUCE", 3)
        addAction(31, "end", "REDUCE", 4)
        addAction(32, ";", "SHIFT", 40)
        addAction(33, "id", "SHIFT", 44)
        addAction(33, "int", "SHIFT", 24)
        addAction(33, "integer", "SHIFT", 25)
        addAction(34, "id", "SHIFT", 49)
        addAction(34, "number_literal", "SHIFT", 48)
        addAction(35, "string_literal", "SHIFT", 51)
        addAction(35, "identifier", "SHIFT", 52)
        addAction(36, "string_literal", "SHIFT", 51)
        addAction(36, "identifier", "SHIFT", 52)
        addAction(37, "id", "SHIFT", 49)
        addAction(37, "number_literal", "SHIFT", 48)
        addAction(38, "id", "SHIFT", 58)
        addAction(38, "int", "SHIFT", 24)
        addAction(38, "integer", "SHIFT", 25)
        addAction(39, "id", "REDUCE", 42)
        addAction(39, "end", "REDUCE", 42)
        addAction(39, "int", "REDUCE", 42)
        addAction(39, "integer", "REDUCE", 42)
        addAction(39, "if", "REDUCE", 42)
        addAction(39, "print_line", "REDUCE", 42)
        addAction(39, "display", "REDUCE", 42)
        addAction(39, "while", "REDUCE", 42)
        addAction(39, "for", "REDUCE", 42)
        addAction(39, "break", "REDUCE", 42)
        addAction(40, "id", "REDUCE", 15)
        addAction(40, "end", "REDUCE", 15)
        addAction(40, ";", "REDUCE", 15)
        addAction(40, ",", "REDUCE", 15)
        addAction(40, "int", "REDUCE", 15)
        addAction(40, "integer", "REDUCE", 15)
        addAction(40, "if", "REDUCE", 15)
        addAction(40, "print_line", "REDUCE", 15)
        addAction(40, "display", "REDUCE", 15)
        addAction(40, "while", "REDUCE", 15)
        addAction(40, "for", "REDUCE", 15)
        addAction(40, "break", "REDUCE", 15)
        addAction(41, "id", "REDUCE", 22)
        addAction(41, "end", "REDUCE", 22)
        addAction(41, ";", "REDUCE", 22)
        addAction(41, ",", "REDUCE", 22)
        addAction(41, "int", "REDUCE", 22)
        addAction(41, "integer", "REDUCE", 22)
        addAction(41, "if", "REDUCE", 22)
        addAction(41, "print_line", "REDUCE", 22)
        addAction(41, "display", "REDUCE", 22)
        addAction(41, "while", "REDUCE", 22)
        addAction(41, "for", "REDUCE", 22)
        addAction(41, "break", "REDUCE", 22)
        addAction(42, "id", "SHIFT", 64)
        addAction(42, "number_literal", "SHIFT", 63)
        addAction(43, "id", "REDUCE", 12)
        addAction(43, "end", "REDUCE", 12)
        addAction(43, "int", "REDUCE", 12)
        addAction(43, "integer", "REDUCE", 12)
        addAction(43, "if", "REDUCE", 12)
        addAction(43, "print_line", "REDUCE", 12)
        addAction(43, "display", "REDUCE", 12)
        addAction(43, "while", "REDUCE", 12)
        addAction(43, "for", "REDUCE", 12)
        addAction(43, "break", "REDUCE", 12)
        addAction(44, ";", "REDUCE", 14)
        addAction(44, ",", "SHIFT", 30)
        addAction(45, "id", "SHIFT", 44)
        addAction(46, ";", "REDUCE", 16)
        addAction(46, ",", "REDUCE", 16)
        addAction(47, ";", "REDUCE", 20)
        addAction(47, ",", "REDUCE", 20)
        addAction(47, "=", "SHIFT", 68)
        addAction(48, ")", "SHIFT", 69)
        addAction(49, ")", "REDUCE", 46)
        addAction(49, "<", "SHIFT", 71)
        addAction(49, "==", "SHIFT", 72)
        addAction(50, "*", "SHIFT", 74)
        addAction(50, ")", "REDUCE", 29)
        addAction(50, "<", "REDUCE", 29)
        addAction(50, "==", "REDUCE", 29)
        addAction(51, "*", "REDUCE", 30)
        addAction(51, ")", "REDUCE", 30)
        addAction(51, "<", "REDUCE", 30)
        addAction(51, "==", "REDUCE", 30)
        addAction(52, "*", "REDUCE", 31)
        addAction(52, ")", "REDUCE", 31)
        addAction(52, "<", "REDUCE", 31)
        addAction(52, "==", "REDUCE", 31)
        addAction(53, ")", "SHIFT", 75)
        addAction(54, ")", "REDUCE", 32)
        addAction(55, ")", "REDUCE", 33)
        addAction(56, ")", "SHIFT", 76)
        addAction(57, ")", "SHIFT", 77)
        addAction(58, "id", "SHIFT", 82)
        addAction(58, "number_literal", "SHIFT", 81)
        addAction(59, "id", "SHIFT", 58)
        addAction(60, "id", "REDUCE", 16)
        addAction(60, "number_literal", "REDUCE", 16)
        addAction(61, "id", "REDUCE", 20)
        addAction(61, "=", "SHIFT", 85)
        addAction(61, "number_literal", "REDUCE", 20)
        addAction(62, "id", "REDUCE", 21)
        addAction(62, "end", "REDUCE", 21)
        addAction(62, ";", "REDUCE", 21)
        addAction(62, ",", "REDUCE", 21)
        addAction(62, "int", "REDUCE", 21)
        addAction(62, "integer", "REDUCE", 21)
        addAction(62, "if", "REDUCE", 21)
        addAction(62, "print_line", "REDUCE", 21)
        addAction(62, "display", "REDUCE", 21)
        addAction(62, "while", "REDUCE", 21)
        addAction(62, "for", "REDUCE", 21)
        addAction(62, "break", "REDUCE", 21)
        addAction(63, "id", "REDUCE", 19)
        addAction(63, "end", "REDUCE", 19)
        addAction(63, ";", "REDUCE", 19)
        addAction(63, ",", "REDUCE", 19)
        addAction(63, "int", "REDUCE", 19)
        addAction(63, "integer", "REDUCE", 19)
        addAction(63, "if", "REDUCE", 19)
        addAction(63, "print_line", "REDUCE", 19)
        addAction(63, "display", "REDUCE", 19)
        addAction(63, "while", "REDUCE", 19)
        addAction(63, "for", "REDUCE", 19)
        addAction(63, "break", "REDUCE", 19)
        addAction(64, "id", "REDUCE", 26)
        addAction(64, "end", "REDUCE", 26)
        addAction(64, ";", "REDUCE", 26)
        addAction(64, ",", "REDUCE", 26)
        addAction(64, "int", "REDUCE", 26)
        addAction(64, "integer", "REDUCE", 26)
        addAction(64, "+", "SHIFT", 87)
        addAction(64, "++", "SHIFT", 88)
        addAction(64, "if", "REDUCE", 26)
        addAction(64, "print_line", "REDUCE", 26)
        addAction(64, "display", "REDUCE", 26)
        addAction(64, "while", "REDUCE", 26)
        addAction(64, "for", "REDUCE", 26)
        addAction(64, "break", "REDUCE", 26)
        addAction(65, "id", "REDUCE", 29)
        addAction(65, "end", "REDUCE", 29)
        addAction(65, ";", "REDUCE", 29)
        addAction(65, ",", "REDUCE", 29)
        addAction(65, "int", "REDUCE", 29)
        addAction(65, "integer", "REDUCE", 29)
        addAction(65, "+", "REDUCE", 29)
        addAction(65, "++", "REDUCE", 29)
        addAction(65, "*", "SHIFT", 90)
        addAction(65, "if", "REDUCE", 29)
        addAction(65, "print_line", "REDUCE", 29)
        addAction(65, "display", "REDUCE", 29)
        addAction(65, "while", "REDUCE", 29)
        addAction(65, "for", "REDUCE", 29)
        addAction(65, "break", "REDUCE", 29)
        addAction(66, "id", "REDUCE", 30)
        addAction(66, "end", "REDUCE", 30)
        addAction(66, ";", "REDUCE", 30)
        addAction(66, ",", "REDUCE", 30)
        addAction(66, "int", "REDUCE", 30)
        addAction(66, "integer", "REDUCE", 30)
        addAction(66, "+", "REDUCE", 30)
        addAction(66, "++", "REDUCE", 30)
        addAction(66, "*", "REDUCE", 30)
        addAction(66, "if", "REDUCE", 30)
        addAction(66, "print_line", "REDUCE", 30)
        addAction(66, "display", "REDUCE", 30)
        addAction(66, "while", "REDUCE", 30)
        addAction(66, "for", "REDUCE", 30)
        addAction(66, "break", "REDUCE", 30)
        addAction(67, "id", "REDUCE", 31)
        addAction(67, "end", "REDUCE", 31)
        addAction(67, ";", "REDUCE", 31)
        addAction(67, ",", "REDUCE", 31)
        addAction(67, "int", "REDUCE", 31)
        addAction(67, "integer", "REDUCE", 31)
        addAction(67, "+", "REDUCE", 31)
        addAction(67, "++", "REDUCE", 31)
        addAction(67, "*", "REDUCE", 31)
        addAction(67, "if", "REDUCE", 31)
        addAction(67, "print_line", "REDUCE", 31)
        addAction(67, "display", "REDUCE", 31)
        addAction(67, "while", "REDUCE", 31)
        addAction(67, "for", "REDUCE", 31)
        addAction(67, "break", "REDUCE", 31)
        addAction(68, ";", "REDUCE", 13)
        addAction(69, ";", "REDUCE", 15)
        addAction(69, ",", "REDUCE", 15)
        addAction(70, ";", "REDUCE", 22)
        addAction(70, ",", "REDUCE", 22)
        addAction(71, "id", "SHIFT", 96)
        addAction(71, "number_literal", "SHIFT", 95)
        addAction(72, "begin", "SHIFT", 98)
        addAction(73, ")", "REDUCE", 43)
        addAction(74, "id", "SHIFT", 101)
        addAction(74, "number_literal", "SHIFT", 100)
        addAction(75, "id", "SHIFT", 101)
        addAction(75, "number_literal", "SHIFT", 100)
        addAction(76, ")", "REDUCE", 27)
        addAction(76, "<", "REDUCE", 27)
        addAction(76, "==", "REDUCE", 27)
        addAction(77, "id", "SHIFT", 49)
        addAction(77, "number_literal", "SHIFT", 48)
        addAction(78, ";", "SHIFT", 104)
        addAction(79, ";", "SHIFT", 105)
        addAction(80, "begin", "SHIFT", 107)
        addAction(81, ";", "SHIFT", 108)
        addAction(82, ";", "REDUCE", 46)
        addAction(82, "<", "SHIFT", 110)
        addAction(82, "==", "SHIFT", 111)
        addAction(83, ";", "REDUCE", 29)
        addAction(83, "*", "SHIFT", 113)
        addAction(83, "<", "REDUCE", 29)
        addAction(83, "==", "REDUCE", 29)
        addAction(84, ";", "REDUCE", 30)
        addAction(84, "*", "REDUCE", 30)
        addAction(84, "<", "REDUCE", 30)
        addAction(84, "==", "REDUCE", 30)
        addAction(85, ";", "REDUCE", 31)
        addAction(85, "*", "REDUCE", 31)
        addAction(85, "<", "REDUCE", 31)
        addAction(85, "==", "REDUCE", 31)
        addAction(86, "id", "REDUCE", 15)
        addAction(86, "number_literal", "REDUCE", 15)
        addAction(87, "id", "REDUCE", 22)
        addAction(87, "number_literal", "REDUCE", 22)
        addAction(88, "id", "SHIFT", 119)
        addAction(88, "number_literal", "SHIFT", 118)
        addAction(89, "id", "REDUCE", 23)
        addAction(89, "end", "REDUCE", 23)
        addAction(89, ";", "REDUCE", 23)
        addAction(89, ",", "REDUCE", 23)
        addAction(89, "int", "REDUCE", 23)
        addAction(89, "integer", "REDUCE", 23)
        addAction(89, "if", "REDUCE", 23)
        addAction(89, "print_line", "REDUCE", 23)
        addAction(89, "display", "REDUCE", 23)
        addAction(89, "while", "REDUCE", 23)
        addAction(89, "for", "REDUCE", 23)
        addAction(89, "break", "REDUCE", 23)
        addAction(90, "id", "SHIFT", 64)
        addAction(90, "number_literal", "SHIFT", 63)
        addAction(91, "id", "REDUCE", 25)
        addAction(91, "end", "REDUCE", 25)
        addAction(91, ";", "REDUCE", 25)
        addAction(91, ",", "REDUCE", 25)
        addAction(91, "int", "REDUCE", 25)
        addAction(91, "integer", "REDUCE", 25)
        addAction(91, "if", "REDUCE", 25)
        addAction(91, "print_line", "REDUCE", 25)
        addAction(91, "display", "REDUCE", 25)
        addAction(91, "while", "REDUCE", 25)
        addAction(91, "for", "REDUCE", 25)
        addAction(91, "break", "REDUCE", 25)
        addAction(92, "id", "REDUCE", 27)
        addAction(92, "end", "REDUCE", 27)
        addAction(92, ";", "REDUCE", 27)
        addAction(92, ",", "REDUCE", 27)
        addAction(92, "int", "REDUCE", 27)
        addAction(92, "integer", "REDUCE", 27)
        addAction(92, "+", "REDUCE", 27)
        addAction(92, "++", "REDUCE", 27)
        addAction(92, "if", "REDUCE", 27)
        addAction(92, "print_line", "REDUCE", 27)
        addAction(92, "display", "REDUCE", 27)
        addAction(92, "while", "REDUCE", 27)
        addAction(92, "for", "REDUCE", 27)
        addAction(92, "break", "REDUCE", 27)
        addAction(93, "id", "SHIFT", 64)
        addAction(93, "number_literal", "SHIFT", 63)
        addAction(94, ";", "REDUCE", 21)
        addAction(94, ",", "REDUCE", 21)
        addAction(95, ";", "REDUCE", 19)
        addAction(95, ",", "REDUCE", 19)
        addAction(96, ";", "REDUCE", 26)
        addAction(96, ",", "REDUCE", 26)
        addAction(96, "+", "SHIFT", 123)
        addAction(96, "++", "SHIFT", 124)
        addAction(97, ";", "REDUCE", 29)
        addAction(97, ",", "REDUCE", 29)
        addAction(97, "+", "REDUCE", 29)
        addAction(97, "++", "REDUCE", 29)
        addAction(97, "*", "SHIFT", 126)
        addAction(98, ";", "REDUCE", 30)
        addAction(98, ",", "REDUCE", 30)
        addAction(98, "+", "REDUCE", 30)
        addAction(98, "++", "REDUCE", 30)
        addAction(98, "*", "REDUCE", 30)
        addAction(99, ";", "REDUCE", 31)
        addAction(99, ",", "REDUCE", 31)
        addAction(99, "+", "REDUCE", 31)
        addAction(99, "++", "REDUCE", 31)
        addAction(99, "*", "REDUCE", 31)
        addAction(100, "id", "REDUCE", 37)
        addAction(100, "end", "REDUCE", 37)
        addAction(100, "int", "REDUCE", 37)
        addAction(100, "integer", "REDUCE", 37)
        addAction(100, "if", "REDUCE", 37)
        addAction(100, "else_if", "SHIFT", 128)
        addAction(100, "else", "SHIFT", 129)
        addAction(100, "print_line", "REDUCE", 37)
        addAction(100, "display", "REDUCE", 37)
        addAction(100, "while", "REDUCE", 37)
        addAction(100, "for", "REDUCE", 37)
        addAction(100, "break", "REDUCE", 37)
        addAction(101, "id", "SHIFT", 26)
        addAction(101, "end", "REDUCE", 5)
        addAction(101, "int", "SHIFT", 24)
        addAction(101, "integer", "SHIFT", 25)
        addAction(101, "if", "SHIFT", 16)
        addAction(101, "print_line", "SHIFT", 17)
        addAction(101, "display", "SHIFT", 18)
        addAction(101, "while", "SHIFT", 19)
        addAction(101, "for", "SHIFT", 20)
        addAction(101, "break", "SHIFT", 21)
        addAction(102, ")", "REDUCE", 44)
        addAction(103, ")", "REDUCE", 30)
        addAction(104, ")", "REDUCE", 31)
        addAction(105, ")", "REDUCE", 45)
        addAction(106, "*", "SHIFT", 74)
        addAction(106, ")", "REDUCE", 29)
        addAction(106, "<", "REDUCE", 29)
        addAction(106, "==", "REDUCE", 29)
        addAction(107, "id", "REDUCE", 38)
        addAction(107, "end", "REDUCE", 38)
        addAction(107, "int", "REDUCE", 38)
        addAction(107, "integer", "REDUCE", 38)
        addAction(107, "if", "REDUCE", 38)
        addAction(107, "print_line", "REDUCE", 38)
        addAction(107, "display", "REDUCE", 38)
        addAction(107, "while", "REDUCE", 38)
        addAction(107, "for", "REDUCE", 38)
        addAction(107, "break", "REDUCE", 38)
        addAction(108, "id", "REDUCE", 39)
        addAction(108, "end", "REDUCE", 39)
        addAction(108, "int", "REDUCE", 39)
        addAction(108, "integer", "REDUCE", 39)
        addAction(108, "if", "REDUCE", 39)
        addAction(108, "print_line", "REDUCE", 39)
        addAction(108, "display", "REDUCE", 39)
        addAction(108, "while", "REDUCE", 39)
        addAction(108, "for", "REDUCE", 39)
        addAction(108, "break", "REDUCE", 39)
        addAction(109, "id", "REDUCE", 40)
        addAction(109, "end", "REDUCE", 40)
        addAction(109, "int", "REDUCE", 40)
        addAction(109, "integer", "REDUCE", 40)
        addAction(109, "if", "REDUCE", 40)
        addAction(109, "print_line", "REDUCE", 40)
        addAction(109, "display", "REDUCE", 40)
        addAction(109, "while", "REDUCE", 40)
        addAction(109, "for", "REDUCE", 40)
        addAction(109, "break", "REDUCE", 40)
        addAction(110, "id", "SHIFT", 26)
        addAction(110, "end", "REDUCE", 5)
        addAction(110, "int", "SHIFT", 24)
        addAction(110, "integer", "SHIFT", 25)
        addAction(110, "if", "SHIFT", 16)
        addAction(110, "print_line", "SHIFT", 17)
        addAction(110, "display", "SHIFT", 18)
        addAction(110, "while", "SHIFT", 19)
        addAction(110, "for", "SHIFT", 20)
        addAction(110, "break", "SHIFT", 21)
        addAction(111, "id", "SHIFT", 137)
        addAction(111, "number_literal", "SHIFT", 136)
        addAction(112, ";", "REDUCE", 43)
        addAction(113, "id", "SHIFT", 140)
        addAction(113, "number_literal", "SHIFT", 139)
        addAction(114, "id", "SHIFT", 140)
        addAction(114, "number_literal", "SHIFT", 139)
        addAction(115, ";", "REDUCE", 27)
        addAction(115, "<", "REDUCE", 27)
        addAction(115, "==", "REDUCE", 27)
        addAction(116, "id", "SHIFT", 82)
        addAction(116, "number_literal", "SHIFT", 81)
        addAction(117, "id", "REDUCE", 21)
        addAction(117, "number_literal", "REDUCE", 21)
        addAction(118, "id", "REDUCE", 19)
        addAction(118, "number_literal", "REDUCE", 19)
        addAction(119, "id", "REDUCE", 26)
        addAction(119, "+", "SHIFT", 144)
        addAction(119, "++", "SHIFT", 145)
        addAction(119, "number_literal", "REDUCE", 26)
        addAction(120, "id", "REDUCE", 29)
        addAction(120, "+", "REDUCE", 29)
        addAction(120, "++", "REDUCE", 29)
        addAction(120, "*", "SHIFT", 147)
        addAction(120, "number_literal", "REDUCE", 29)
        addAction(121, "id", "REDUCE", 30)
        addAction(121, "+", "REDUCE", 30)
        addAction(121, "++", "REDUCE", 30)
        addAction(121, "*", "REDUCE", 30)
        addAction(121, "number_literal", "REDUCE", 30)
        addAction(122, "id", "REDUCE", 31)
        addAction(122, "+", "REDUCE", 31)
        addAction(122, "++", "REDUCE", 31)
        addAction(122, "*", "REDUCE", 31)
        addAction(122, "number_literal", "REDUCE", 31)
        addAction(123, "id", "REDUCE", 26)
        addAction(123, "end", "REDUCE", 26)
        addAction(123, ";", "REDUCE", 26)
        addAction(123, ",", "REDUCE", 26)
        addAction(123, "int", "REDUCE", 26)
        addAction(123, "integer", "REDUCE", 26)
        addAction(123, "+", "SHIFT", 87)
        addAction(123, "++", "SHIFT", 88)
        addAction(123, "if", "REDUCE", 26)
        addAction(123, "print_line", "REDUCE", 26)
        addAction(123, "display", "REDUCE", 26)
        addAction(123, "while", "REDUCE", 26)
        addAction(123, "for", "REDUCE", 26)
        addAction(123, "break", "REDUCE", 26)
        addAction(124, "id", "REDUCE", 29)
        addAction(124, "end", "REDUCE", 29)
        addAction(124, ";", "REDUCE", 29)
        addAction(124, ",", "REDUCE", 29)
        addAction(124, "int", "REDUCE", 29)
        addAction(124, "integer", "REDUCE", 29)
        addAction(124, "+", "REDUCE", 29)
        addAction(124, "++", "REDUCE", 29)
        addAction(124, "*", "SHIFT", 90)
        addAction(124, "if", "REDUCE", 29)
        addAction(124, "print_line", "REDUCE", 29)
        addAction(124, "display", "REDUCE", 29)
        addAction(124, "while", "REDUCE", 29)
        addAction(124, "for", "REDUCE", 29)
        addAction(124, "break", "REDUCE", 29)
        addAction(125, ";", "REDUCE", 23)
        addAction(125, ",", "REDUCE", 23)
        addAction(126, "id", "SHIFT", 96)
        addAction(126, "number_literal", "SHIFT", 95)
        addAction(127, ";", "REDUCE", 25)
        addAction(127, ",", "REDUCE", 25)
        addAction(128, ";", "REDUCE", 27)
        addAction(128, ",", "REDUCE", 27)
        addAction(128, "+", "REDUCE", 27)
        addAction(128, "++", "REDUCE", 27)
        addAction(129, "id", "SHIFT", 96)
        addAction(129, "number_literal", "SHIFT", 95)
        addAction(130, "id", "REDUCE", 34)
        addAction(130, "end", "REDUCE", 34)
        addAction(130, "int", "REDUCE", 34)
        addAction(130, "integer", "REDUCE", 34)
        addAction(130, "if", "REDUCE", 34)
        addAction(130, "print_line", "REDUCE", 34)
        addAction(130, "display", "REDUCE", 34)
        addAction(130, "while", "REDUCE", 34)
        addAction(130, "for", "REDUCE", 34)
        addAction(130, "break", "REDUCE", 34)
        addAction(131, "(", "SHIFT", 152)
        addAction(132, "begin", "SHIFT", 107)
        addAction(133, "end", "SHIFT", 154)
        addAction(134, ")", "REDUCE", 28)
        addAction(134, "<", "REDUCE", 28)
        addAction(134, "==", "REDUCE", 28)
        addAction(135, "end", "SHIFT", 155)
        addAction(136, ")", "SHIFT", 156)
        addAction(137, "+", "SHIFT", 158)
        addAction(137, "++", "SHIFT", 159)
        addAction(137, ")", "REDUCE", 26)
        addAction(138, "+", "REDUCE", 29)
        addAction(138, "++", "REDUCE", 29)
        addAction(138, "*", "SHIFT", 161)
        addAction(138, ")", "REDUCE", 29)
        addAction(139, "+", "REDUCE", 30)
        addAction(139, "++", "REDUCE", 30)
        addAction(139, "*", "REDUCE", 30)
        addAction(139, ")", "REDUCE", 30)
        addAction(140, "+", "REDUCE", 31)
        addAction(140, "++", "REDUCE", 31)
        addAction(140, "*", "REDUCE", 31)
        addAction(140, ")", "REDUCE", 31)
        addAction(141, ";", "REDUCE", 44)
        addAction(142, ";", "REDUCE", 30)
        addAction(143, ";", "REDUCE", 31)
        addAction(144, ";", "REDUCE", 45)
        addAction(145, ";", "REDUCE", 29)
        addAction(145, "*", "SHIFT", 113)
        addAction(145, "<", "REDUCE", 29)
        addAction(145, "==", "REDUCE", 29)
        addAction(146, "id", "REDUCE", 23)
        addAction(146, "number_literal", "REDUCE", 23)
        addAction(147, "id", "SHIFT", 119)
        addAction(147, "number_literal", "SHIFT", 118)
        addAction(148, "id", "REDUCE", 25)
        addAction(148, "number_literal", "REDUCE", 25)
        addAction(149, "id", "REDUCE", 27)
        addAction(149, "+", "REDUCE", 27)
        addAction(149, "++", "REDUCE", 27)
        addAction(149, "number_literal", "REDUCE", 27)
        addAction(150, "id", "SHIFT", 119)
        addAction(150, "number_literal", "SHIFT", 118)
        addAction(151, "id", "REDUCE", 24)
        addAction(151, "end", "REDUCE", 24)
        addAction(151, ";", "REDUCE", 24)
        addAction(151, ",", "REDUCE", 24)
        addAction(151, "int", "REDUCE", 24)
        addAction(151, "integer", "REDUCE", 24)
        addAction(151, "if", "REDUCE", 24)
        addAction(151, "print_line", "REDUCE", 24)
        addAction(151, "display", "REDUCE", 24)
        addAction(151, "while", "REDUCE", 24)
        addAction(151, "for", "REDUCE", 24)
        addAction(151, "break", "REDUCE", 24)
        addAction(152, "id", "REDUCE", 28)
        addAction(152, "end", "REDUCE", 28)
        addAction(152, ";", "REDUCE", 28)
        addAction(152, ",", "REDUCE", 28)
        addAction(152, "int", "REDUCE", 28)
        addAction(152, "integer", "REDUCE", 28)
        addAction(152, "+", "REDUCE", 28)
        addAction(152, "++", "REDUCE", 28)
        addAction(152, "if", "REDUCE", 28)
        addAction(152, "print_line", "REDUCE", 28)
        addAction(152, "display", "REDUCE", 28)
        addAction(152, "while", "REDUCE", 28)
        addAction(152, "for", "REDUCE", 28)
        addAction(152, "break", "REDUCE", 28)
        addAction(153, ";", "REDUCE", 26)
        addAction(153, ",", "REDUCE", 26)
        addAction(153, "+", "SHIFT", 123)
        addAction(153, "++", "SHIFT", 124)
        addAction(154, ";", "REDUCE", 29)
        addAction(154, ",", "REDUCE", 29)
        addAction(154, "+", "REDUCE", 29)
        addAction(154, "++", "REDUCE", 29)
        addAction(154, "*", "SHIFT", 126)
        addAction(155, "id", "SHIFT", 49)
        addAction(155, "number_literal", "SHIFT", 48)
        addAction(156, "id", "REDUCE", 36)
        addAction(156, "end", "REDUCE", 36)
        addAction(156, "int", "REDUCE", 36)
        addAction(156, "integer", "REDUCE", 36)
        addAction(156, "if", "REDUCE", 36)
        addAction(156, "print_line", "REDUCE", 36)
        addAction(156, "display", "REDUCE", 36)
        addAction(156, "while", "REDUCE", 36)
        addAction(156, "for", "REDUCE", 36)
        addAction(156, "break", "REDUCE", 36)
        addAction(157, "id", "REDUCE", 3)
        addAction(157, "end", "REDUCE", 3)
        addAction(157, "int", "REDUCE", 3)
        addAction(157, "integer", "REDUCE", 3)
        addAction(157, "if", "REDUCE", 3)
        addAction(157, "else_if", "REDUCE", 3)
        addAction(157, "else", "REDUCE", 3)
        addAction(157, "print_line", "REDUCE", 3)
        addAction(157, "display", "REDUCE", 3)
        addAction(157, "while", "REDUCE", 3)
        addAction(157, "for", "REDUCE", 3)
        addAction(157, "break", "REDUCE", 3)
        addAction(158, "id", "REDUCE", 3)
        addAction(158, "end", "REDUCE", 3)
        addAction(158, "int", "REDUCE", 3)
        addAction(158, "integer", "REDUCE", 3)
        addAction(158, "if", "REDUCE", 3)
        addAction(158, "print_line", "REDUCE", 3)
        addAction(158, "display", "REDUCE", 3)
        addAction(158, "while", "REDUCE", 3)
        addAction(158, "for", "REDUCE", 3)
        addAction(158, "break", "REDUCE", 3)
        addAction(159, "begin", "SHIFT", 107)
        addAction(160, ")", "REDUCE", 23)
        addAction(161, "id", "SHIFT", 137)
        addAction(161, "number_literal", "SHIFT", 136)
        addAction(162, ")", "REDUCE", 25)
        addAction(163, "+", "REDUCE", 27)
        addAction(163, "++", "REDUCE", 27)
        addAction(163, ")", "REDUCE", 27)
        addAction(164, "id", "SHIFT", 137)
        addAction(164, "number_literal", "SHIFT", 136)
        addAction(165, ";", "REDUCE", 28)
        addAction(165, "<", "REDUCE", 28)
        addAction(165, "==", "REDUCE", 28)
        addAction(166, "id", "REDUCE", 26)
        addAction(166, "+", "SHIFT", 144)
        addAction(166, "++", "SHIFT", 145)
        addAction(166, "number_literal", "REDUCE", 26)
        addAction(167, "id", "REDUCE", 29)
        addAction(167, "+", "REDUCE", 29)
        addAction(167, "++", "REDUCE", 29)
        addAction(167, "*", "SHIFT", 147)
        addAction(167, "number_literal", "REDUCE", 29)
        addAction(168, ";", "REDUCE", 24)
        addAction(168, ",", "REDUCE", 24)
        addAction(169, ";", "REDUCE", 28)
        addAction(169, ",", "REDUCE", 28)
        addAction(169, "+", "REDUCE", 28)
        addAction(169, "++", "REDUCE", 28)
        addAction(170, ")", "SHIFT", 173)
        addAction(171, "id", "REDUCE", 41)
        addAction(171, "end", "REDUCE", 41)
        addAction(171, "int", "REDUCE", 41)
        addAction(171, "integer", "REDUCE", 41)
        addAction(171, "if", "REDUCE", 41)
        addAction(171, "print_line", "REDUCE", 41)
        addAction(171, "display", "REDUCE", 41)
        addAction(171, "while", "REDUCE", 41)
        addAction(171, "for", "REDUCE", 41)
        addAction(171, "break", "REDUCE", 41)
        addAction(172, "+", "SHIFT", 158)
        addAction(172, "++", "SHIFT", 159)
        addAction(172, ")", "REDUCE", 26)
        addAction(173, "+", "REDUCE", 29)
        addAction(173, "++", "REDUCE", 29)
        addAction(173, "*", "SHIFT", 161)
        addAction(173, ")", "REDUCE", 29)
        addAction(174, "id", "REDUCE", 24)
        addAction(174, "number_literal", "REDUCE", 24)
        addAction(175, "id", "REDUCE", 28)
        addAction(175, "+", "REDUCE", 28)
        addAction(175, "++", "REDUCE", 28)
        addAction(175, "number_literal", "REDUCE", 28)
        addAction(176, "begin", "SHIFT", 98)
        addAction(177, ")", "REDUCE", 24)
        addAction(178, "+", "REDUCE", 28)
        addAction(178, "++", "REDUCE", 28)
        addAction(178, ")", "REDUCE", 28)
        addAction(179, "id", "REDUCE", 37)
        addAction(179, "end", "REDUCE", 37)
        addAction(179, "int", "REDUCE", 37)
        addAction(179, "integer", "REDUCE", 37)
        addAction(179, "if", "REDUCE", 37)
        addAction(179, "else_if", "SHIFT", 128)
        addAction(179, "else", "SHIFT", 129)
        addAction(179, "print_line", "REDUCE", 37)
        addAction(179, "display", "REDUCE", 37)
        addAction(179, "while", "REDUCE", 37)
        addAction(179, "for", "REDUCE", 37)
        addAction(179, "break", "REDUCE", 37)
        addAction(180, "id", "REDUCE", 35)
        addAction(180, "end", "REDUCE", 35)
        addAction(180, "int", "REDUCE", 35)
        addAction(180, "integer", "REDUCE", 35)
        addAction(180, "if", "REDUCE", 35)
        addAction(180, "print_line", "REDUCE", 35)
        addAction(180, "display", "REDUCE", 35)
        addAction(180, "while", "REDUCE", 35)
        addAction(180, "for", "REDUCE", 35)
        addAction(180, "break", "REDUCE", 35)


    }

    private void addAction(int state, String symbol, String action, int number) {
        actionTable
                .computeIfAbsent(state, k -> new HashMap<>())
                .put(symbol, new Pair<>(action, number));
    }

    // Method to generate the goto table
    public void generateGotoTable() {
        goToTable = new HashMap<>();

        addGoTo(3, "S", 1)
        addGoTo(3, "BEGIN_BLOCK", 2)
        addGoTo(5, "BEGIN_STMT", 4)
        addGoTo(8, "STMTS", 7)
        addGoTo(8, "STMT", 8)
        addGoTo(8, "DECLARATION_LIST", 9)
        addGoTo(8, "DECLARATION", 15)
        addGoTo(8, "TYPE", 22)
        addGoTo(8, "ID_LIST", 23)
        addGoTo(8, "IF_STMT", 10)
        addGoTo(8, "PRINT_STMT", 11)
        addGoTo(8, "WHILE_STMT", 12)
        addGoTo(8, "FOR_STMT", 13)
        addGoTo(8, "BREAK_STMT", 14)
        addGoTo(11, "STMTS", 28)
        addGoTo(11, "STMT", 8)
        addGoTo(11, "DECLARATION_LIST", 9)
        addGoTo(11, "DECLARATION", 15)
        addGoTo(11, "TYPE", 22)
        addGoTo(11, "ID_LIST", 23)
        addGoTo(11, "IF_STMT", 10)
        addGoTo(11, "PRINT_STMT", 11)
        addGoTo(11, "WHILE_STMT", 12)
        addGoTo(11, "FOR_STMT", 13)
        addGoTo(11, "BREAK_STMT", 14)
        addGoTo(18, "DECLARATION_LIST'", 29)
        addGoTo(25, "ID_LIST", 37)
        addGoTo(29, "ASSIGNMENT", 38)
        addGoTo(33, "DECLARATION", 41)
        addGoTo(33, "TYPE", 42)
        addGoTo(33, "ID_LIST", 43)
        addGoTo(34, "TERM", 46)
        addGoTo(34, "FACTOR", 47)
        addGoTo(34, "COM_STMT", 45)
        addGoTo(35, "PRINT_CONTENT", 50)
        addGoTo(36, "PRINT_CONTENT", 53)
        addGoTo(37, "TERM", 46)
        addGoTo(37, "FACTOR", 47)
        addGoTo(37, "COM_STMT", 54)
        addGoTo(38, "DECLARATION", 55)
        addGoTo(38, "TYPE", 56)
        addGoTo(38, "ID_LIST", 57)
        addGoTo(41, "ID_LIST'", 59)
        addGoTo(42, "OP_STMT", 60)
        addGoTo(42, "TERM", 61)
        addGoTo(42, "FACTOR", 62)
        addGoTo(44, "DECLARATION_LIST'", 65)
        addGoTo(45, "ID_LIST", 66)
        addGoTo(47, "ASSIGNMENT", 67)
        addGoTo(49, "COM_STMT'", 70)
        addGoTo(50, "TERM'", 73)
        addGoTo(58, "TERM", 79)
        addGoTo(58, "FACTOR", 80)
        addGoTo(58, "COM_STMT", 78)
        addGoTo(59, "ID_LIST", 83)
        addGoTo(61, "ASSIGNMENT", 84)
        addGoTo(64, "OP_STMT'", 86)
        addGoTo(65, "TERM'", 89)
        addGoTo(70, "ID_LIST'", 91)
        addGoTo(71, "OP_STMT", 92)
        addGoTo(71, "TERM", 93)
        addGoTo(71, "FACTOR", 94)
        addGoTo(72, "BEGIN_STMT", 97)
        addGoTo(74, "FACTOR", 99)
        addGoTo(75, "FACTOR", 102)
        addGoTo(77, "FACTOR", 103)
        addGoTo(80, "BEGIN_STMT", 106)
        addGoTo(82, "COM_STMT'", 109)
        addGoTo(83, "TERM'", 112)
        addGoTo(87, "ID_LIST'", 114)
        addGoTo(88, "OP_STMT", 115)
        addGoTo(88, "TERM", 116)
        addGoTo(88, "FACTOR", 117)
        addGoTo(90, "TERM", 120)
        addGoTo(90, "FACTOR", 62)
        addGoTo(93, "FACTOR", 121)
        addGoTo(96, "OP_STMT'", 122)
        addGoTo(97, "TERM'", 125)
        addGoTo(100, "IF_STMT'", 127)
        addGoTo(101, "STMTS", 130)
        addGoTo(101, "STMT", 8)
        addGoTo(101, "DECLARATION_LIST", 9)
        addGoTo(101, "DECLARATION", 15)
        addGoTo(101, "TYPE", 22)
        addGoTo(101, "ID_LIST", 23)
        addGoTo(101, "IF_STMT", 10)
        addGoTo(101, "PRINT_STMT", 11)
        addGoTo(101, "WHILE_STMT", 12)
        addGoTo(101, "FOR_STMT", 13)
        addGoTo(101, "BREAK_STMT", 14)
        addGoTo(106, "TERM'", 131)
        addGoTo(110, "STMTS", 132)
        addGoTo(110, "STMT", 8)
        addGoTo(110, "DECLARATION_LIST", 9)
        addGoTo(110, "DECLARATION", 15)
        addGoTo(110, "TYPE", 22)
        addGoTo(110, "ID_LIST", 23)
        addGoTo(110, "IF_STMT", 10)
        addGoTo(110, "PRINT_STMT", 11)
        addGoTo(110, "WHILE_STMT", 12)
        addGoTo(110, "FOR_STMT", 13)
        addGoTo(110, "BREAK_STMT", 14)
        addGoTo(111, "OP_STMT", 133)
        addGoTo(111, "TERM", 134)
        addGoTo(111, "FACTOR", 135)
        addGoTo(113, "FACTOR", 138)
        addGoTo(114, "FACTOR", 141)
        addGoTo(116, "FACTOR", 142)
        addGoTo(119, "OP_STMT'", 143)
        addGoTo(120, "TERM'", 146)
        addGoTo(123, "OP_STMT'", 148)
        addGoTo(124, "TERM'", 149)
        addGoTo(126, "TERM", 150)
        addGoTo(126, "FACTOR", 94)
        addGoTo(129, "FACTOR", 151)
        addGoTo(132, "BEGIN_STMT", 153)
        addGoTo(137, "OP_STMT'", 157)
        addGoTo(138, "TERM'", 160)
        addGoTo(145, "TERM'", 162)
        addGoTo(147, "TERM", 163)
        addGoTo(147, "FACTOR", 117)
        addGoTo(150, "FACTOR", 164)
        addGoTo(153, "OP_STMT'", 165)
        addGoTo(154, "TERM'", 166)
        addGoTo(155, "TERM", 46)
        addGoTo(155, "FACTOR", 47)
        addGoTo(155, "COM_STMT", 167)
        addGoTo(159, "BEGIN_STMT", 168)
        addGoTo(161, "TERM", 169)
        addGoTo(161, "FACTOR", 135)
        addGoTo(164, "FACTOR", 170)
        addGoTo(166, "OP_STMT'", 171)
        addGoTo(167, "TERM'", 172)
        addGoTo(172, "OP_STMT'", 174)
        addGoTo(173, "TERM'", 175)
        addGoTo(176, "BEGIN_STMT", 176)
        addGoTo(179, "IF_STMT'", 177)
    }

    public void addGoTo(int state, String nonterminal, int nextState) {
        if (!goToTable.containsKey(state)) {
            goToTable.put(state, new HashMap<>());
        }
        goToTable.get(state).put(nonterminal, nextState);
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
