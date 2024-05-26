
import java.io.IOException;


public class RecurParser {
    public static void main(String[] args) throws IOException {
//        if (args.length < 1) {
//            System.err.println("Usage: java Main <filename>");
//            return;
//        }
//        String filename = args[0];
        String filename = "src/test1error.txt";
        Token table = new Token();
        Lexer lexer = new Lexer(filename, table);
        lexer.tokenize();
        //Token.printTable();

        RecursiveDescentParser parser = new RecursiveDescentParser();
        parser.parseList(table);
        if(parser.pass) System.out.println("Parse OK");
    }


}
