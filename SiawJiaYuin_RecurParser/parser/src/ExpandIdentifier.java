
public class ExpandIdentifier extends Token{
    public static String currentToken;
    public static int isIllegal = 0;

    public ExpandIdentifier(String token) {
        super();
        // TODO Auto-generated constructor stub
    }

    public void makeKeyword(String token) {
        String tokenType = TokenType.KEYWORDS;
        Token.addSymbol(token, tokenType);
        //System.out.println(token + "\t\t\t\t" + tokenType);
        //Token.printTable();
    }

    public static void makeID(String token) {
        String tokenType = TokenType.IDENTIFIER;
        Token.addSymbol(token, tokenType);
        //System.out.println(token + "\t\t\t" + tokenType);
        //Token.printTable();
    }
    public static boolean isKeyword(String token) {
        String [] keywords = {"program", "begin", "int", "if", "else_if", "else", "print_line", "end", "while"};
        for (String keyword : keywords) {
            if (keyword.equals(token.strip())) {
                return true;
            }
        }
        //System.out.println("checking keyword: " + token);
        return false;
    }

    public void tokenizeIdentifier(String token) {
        if(isKeyword(token)) {
            makeKeyword(token);
            //System.out.println(newToken + " token made");
            //Token.printToken(keyword);
        }
        else {
            int currState = 0;
            for (char c : token.toCharArray()) {
                currState = FSM.idStates(currState, c);
                if(currState == -1) break;
            }
            if(currState == 1) {
                makeID(token);
            }
            else {
                Token.addSymbol(token, "Illegal ID");
                //System.out.println(token + "\t\t\t\t" + "Illegal ID?");
            }
            //Token.printToken(makeID(newToken));
        }
    }
}
