//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;

public class syntrans {
    public syntrans() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java RecurParser <filename>");
            return;
        }
        String filename = args[0];
//            String filename = "src/input.txt";
            Token table = new Token();
            Lexer lexer = new Lexer(filename, table);
            lexer.tokenize();
            //Token.printTable();

            SDT parser = new SDT();
            parser.parseList();

            //Token.printTable();

    }
}
