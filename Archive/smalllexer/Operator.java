package smalllexer;

public class Operator extends Token {

	public Operator() {}
	
	public void checkOperator(char token) {
		
		int currState = 0;
		currState = FSM.opStates(currState, token);
		if(currState == -1) {
			Token.addSymbol("" + token, "Illegal ID");
	    	System.out.println(token + "\t\t\t" + "Illegal ID");
	    	return;
		}
		//System.out.println("sign: " + currState);
		int sign = currState;
		if(token == '=') {
			sign = 0;
			makeOperator(token, sign);
		}
		else if(token == '+') {
			sign = 1;
			makeOperator(token, sign);
		}
		else if(token == '*') {
			sign = 2;
			makeOperator(token, sign);
		}
		else if(token == '<') {
			sign = 3;
			makeOperator(token, sign);
		}
		else if(token == ';') {
			sign = 4;
			makeOperator(token, sign);
		}
		else if(token == ',') {
			sign = 5;
			makeOperator(token, sign);
		}
		else if(token == ')') {
			sign = 6;
			makeOperator(token, sign);
		}
		else if(token == '(') {
			sign = 7;
			makeOperator(token, sign);
		}
		else {
			Token.addSymbol("" + token, "Illegal ID");
	    	System.out.println(token + "\t\t\t" + "Illegal ID");
		}
	}
	
	public void makeOperator(char token, int sign) {
		
		if(sign == 0) {
			String tokenType = TokenType.ASSIGNMENT_OP; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
		else if(sign == 1) {
			String tokenType = TokenType.PLUS_OP; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
		else if(sign == 2) {
			String tokenType = TokenType.MUL_OP; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
		else if(sign == 3) {
			String tokenType = TokenType.LT_OP; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
		else if(sign == 4) {
			String tokenType = TokenType.STATEMENT_TERM; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
		else if(sign == 5) {
			String tokenType = TokenType.COMMA; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
		else if(sign == 6) {
			String tokenType = TokenType.RPAREN; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
		else if(sign == 7) {
			String tokenType = TokenType.LPAREN; 
	    	Token.addSymbol(token+"", tokenType);
	    	System.out.println(token + "\t\t\t" + tokenType);
		}
	}
}
