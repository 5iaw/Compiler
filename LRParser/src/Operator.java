
public class Operator extends Token {

    public Operator() {}

    public void checkOperator(String token) {

        int currState = 0;
        currState = FSM.opStates(currState, token);
        if(currState == -1) {
            Token.addSymbol("" + token, "Illegal ID?");
            //System.out.println(token + "\t\t\t\t\t\t" + "Illegal ID");
            return;
        }
        //System.out.println("sign: " + currState);
        int sign = currState;
        if(token.equals("=")) {
            sign = 0;
            makeOperator(token, sign);
        }
        else if(token.equals("+")) {
            sign = 1;
            makeOperator(token, sign);
        }
        else if(token.equals("*")) {
            sign = 2;
            makeOperator(token, sign);
        }
        else if(token.equals("<")) {
            sign = 3;
            makeOperator(token, sign);
        }
        else if(token.equals(";")) {
            sign = 4;
            makeOperator(token, sign);
        }
        else if(token.equals(",")) {
            sign = 5;
            makeOperator(token, sign);
        }
        else if(token.equals(")")) {
            sign = 6;
            makeOperator(token, sign);
        }
        else if(token.equals("(")) {
            sign = 7;
            makeOperator(token, sign);
        }
        else if(token.equals("==")) {
            sign = 8;
            makeOperator(token, sign);       // change to make for
        }
        else if(token.equals("++")) {
            sign = 9;
            makeOperator(token, sign);       // change to make for
            //System.out.println("check ++");
        }
        else {
            Token.addSymbol("" + token, "Illegal ID");
            System.out.println(token + "\t\t\t\t\t\t" + "Illegal ID");
        }
    }

    public void makeOperator(String token, int sign) {

        if(sign == 0) {
            String tokenType = TokenType.ASSIGNMENT_OP;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 1) {
            String tokenType = TokenType.PLUS_OP;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 2) {
            String tokenType = TokenType.MUL_OP;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 3) {
            String tokenType = TokenType.LT_OP;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 4) {
            String tokenType = TokenType.STATEMENT_TERM;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 5) {
            String tokenType = TokenType.COMMA;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 6) {
            String tokenType = TokenType.RPAREN;
            Token.addSymbol(token, tokenType);
            System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 7) {
            String tokenType = TokenType.LPAREN;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 8) {
            String tokenType = TokenType.EQ_OP;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
        else if(sign == 9) {
            String tokenType = TokenType.INCREMENT;
            Token.addSymbol(token, tokenType);
            //System.out.println(token + "\t\t\t\t\t" + tokenType);
        }
    }
}