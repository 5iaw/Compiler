package smalllexer;

public class Number_Literal extends Token {

	public static String currentToken;
	
	public Number_Literal() {
		// TODO Auto-generated constructor stub
		
	}

	public void checkNumLiteral(String token) {
    	token = token.strip();
    	int currState = 0;
		for (char c : token.toString().toCharArray()) {
		    currState = FSM.numStates(currState, c);
		    if(currState == -1) break;
		}
		if(currState == 1) {
			makeNumLiteral(token);
			
		}
		else {
			Token.addSymbol(token, "Illegal ID");
	    	System.out.println(token + "\t\t\t" + "Illegal ID starting with digit");
		}
	}
	
	public void makeNumLiteral(String token) {
		String tokenType = TokenType.NUM_LITERAL; 
    	Token.addSymbol(token, tokenType);
    	System.out.println(token + "\t\t\t" + tokenType);
	}
}
