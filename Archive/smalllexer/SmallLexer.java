package smalllexer;
import java.io.IOException;


public class SmallLexer {
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
            System.err.println("Usage: java Main <filename>");
            return;
        }
		String filename = args[0];
		//String filename = "Input1.txt";
        Token table = new Token();
        Lexer lexer = new Lexer(filename, table);
        lexer.tokenize();
    }
	
	
}
