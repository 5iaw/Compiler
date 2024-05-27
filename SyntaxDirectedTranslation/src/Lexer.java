
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Lexer {
    private static String input;
    //private int position;
    private static Token table;

    public Lexer(String filename, Token table) {
        Lexer.input = filename;
        Lexer.setTable(table);
        //this.position = 0;
    }

    public static boolean not_delim (char c) {
        return !(c==';'|| c==',' || c == '(' || c==')' || c=='=' || c=='*' || c=='+' || c=='<' || c==' ' || c=='\"' || c=='\n');
    }

    public void tokenize() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            StringBuilder buffer = new StringBuilder(); // adds until meet
            //int str_flag = 0;


            while ((line = reader.readLine()) != null) {
                line = line.trim();
                int max_len = line.length();
                //String[] tokens = line.split("\\s+"); // Split line by whitespace

                buffer.setLength(0);
                int idx = 0;
                //System.out.println(max_len);
                while(idx < max_len) {
                    //System.out.println(line);
                    buffer.setLength(0);
                    char curr = line.charAt(idx);
                    //System.out.println(idx);

                    if (curr == ' ') {
                        idx++;
                        continue;
                    } else if (Character.isLetter(curr) || curr == '$') {
                        /* when encountered, use while loop to go through, put char in buffer, increment idx
                        when meet delimiters, stop, set idx
                        pass buffer to check id
                         */
                        while (not_delim(curr)) {
                            buffer.append(curr);
                            //System.out.println(idx);
                            idx++;
                            if(idx >= max_len) break;
                            curr = line.charAt(idx);
                            //System.out.println(buffer);
                        }
                        ExpandIdentifier id = new ExpandIdentifier(buffer.toString());
                        //System.out.println("expand id");
                        id.tokenizeIdentifier(buffer.toString());

                    } else if (Character.isDigit(curr)) {
                        while (not_delim(curr)) {
                            buffer.append(curr);
                            curr = line.charAt(++idx);
                        }
                        int currState = 0;
                        for (char c : buffer.toString().toCharArray()) {
                            currState = FSM.numStates(currState, c);
                            if (currState == -1) break;
                        }
                        if (currState == 1) {
                            String tokenType = TokenType.NUM_LITERAL;
                            Token.addSymbol(buffer.toString(), tokenType);
                            //System.out.println(buffer + "\t\t\t\t\t" + tokenType);

                        } else {
                            Token.addSymbol(buffer.toString(), "Illegal ID starting with digit");
                            //System.out.println(buffer + "\t\t\t\t" + "Illegal ID starting with digit");
                        }
                    } else if (curr == '-') {
                        //System.out.println("Testing comment ");
                        while (idx < max_len) {
                            buffer.append(curr);
                            idx++;
                            if(idx >= max_len) break;
                            curr = line.charAt(idx);
                        }
                        int currState = 0;
                        for (char c : buffer.toString().toCharArray()) {
                            currState = FSM.commentStates(currState, c);
                            if (currState == -1) break;
                        }
                        if (currState == 2) {
                            String tokenType = TokenType.COMMENTS;
                            Token.addSymbol(buffer.toString(), tokenType);
                            //System.out.println(buffer + "\t\t\t\t" + tokenType);

                        } else {
                            Token.addSymbol(buffer.toString(), "Illegal ID");
                            //System.out.println(buffer + "\t\t\t\t" + "Illegal ID");
                        }
                    } else if (curr == '\"') {
                        buffer.append(curr);
                        //System.out.println(idx);
                        curr = line.charAt(++idx);
                        while (curr != '\"') {
                            buffer.append(curr);
                            //System.out.println(idx);
                            curr = line.charAt(++idx);
                        }
                        buffer.append(line.charAt(idx));
                        //System.out.println(buffer);
                        idx++;

                        String tokenType = TokenType.STRING_LITERAL;
                        Token.addSymbol(buffer.toString(), tokenType);
                        //System.out.println(buffer + "\t\t\t\t" + tokenType);

                    } else if (curr == '=' || curr == '+' || curr == '*' || curr == '<' || curr == ',' || curr == ';') {   // operators = + *  and <
                        //System.out.println(tokens[i] + " make Op");
                        String check = "";
                        //System.out.println(curr);
                        check = curr+"";
                        if(idx < max_len-1){
                            char next = line.charAt(idx+1);
                            if(next == '+' || next == '=') {
                                check = check + Character.toString(next);
                                //System.out.println(check);
                                idx++;
                            }
                        }
                        //System.out.println(check);
                        Operator op = new Operator();
                        op.checkOperator(check);
                        idx++;
                    } else if (curr == '(') {
                        idx++;
                        String tokenType = TokenType.LPAREN;
                        Token.addSymbol(Character.toString(curr), tokenType);
                        //System.out.println(curr + "\t\t\t\t\t" + tokenType);
                    } else if (curr == ')') {
                        idx++;
                        String tokenType = TokenType.RPAREN;
                        Token.addSymbol(Character.toString(curr), tokenType);
                        //System.out.println(curr + "\t\t\t\t\t" + tokenType);
                    } else {
                        Token.addSymbol(buffer.toString(), "Illegal ID?");
                        //System.out.println(buffer + "\t\t\t\t" + "Illegal ID starting with wrong character");
                        idx++;
                    }
                }
                //System.out.println(line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean isLegalID(char c) {
        if(Character.isLetter(c) || c=='$' || c=='_' || c=='.' || Character.isDigit(c)) {
            return true;
        }
        return false;
    }

    public static Token getTable() {
        return table;
    }

    public static void setTable(Token table) {
        Lexer.table = table;
    }


}