//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;

public class SDT {
    public SDT() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java RecurParser <filename>");
            return;
        }
        String filename = args[0];
//        String filename = "src/test3.txt";
            Token table = new Token();
            Lexer lexer = new Lexer(filename, table);
            lexer.tokenize();
            //Token.printTable();

    }
}
