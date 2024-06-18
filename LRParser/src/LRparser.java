import java.io.IOException;

public class LRparser {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java LRparser <filename>");
            return;
        }
        String filename = args[0];
//        String filename = "test1.txt";
        Token table = new Token();
        Lexer lexer = new Lexer(filename, table);
        lexer.tokenize();
//        Token.printTable();
        LR1Parser parser = new LR1Parser();
        parser.stackInit(table);
        //parser.parseList(table);
        //System.out.println("Parse OK");

    }
}