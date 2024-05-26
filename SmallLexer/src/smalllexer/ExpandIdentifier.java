package smalllexer;

public class ExpandIdentifier extends Token{
	public static String currentToken;
	public static int isIllegal = 0;

	public ExpandIdentifier(String token) {
		super();
		// TODO Auto-generated constructor stub
	}


    public static boolean isDelimiter(char c) {
    	if(c=='=' || c=='+' || c=='*' || c=='(' || c==')' || c==';' || c==',' || c=='<' || c=='\"') {
    		return true;
    	}
    	return false;
    }
    
    public static void checkComment(String token) {
    	int currState = 0;
		for (char c : token.toString().toCharArray()) {
			//System.out.println(c);
		    currState = FSM.commentStates(currState, c);
		    if(currState == -1) break;
		}
		isIllegal = currState==-1? 1:0;
		//System.out.println(token + " isIllegal flag: " + isIllegal);
		if(isIllegal == 0) {
			Token.addSymbol(token, TokenType.COMMENTS);
	    	System.out.println(token + "\t\t\t" + TokenType.COMMENTS);
		}
		else {
			Token.addSymbol(token, "Illegal ID");
	    	System.out.println(token + "\t\t\t" + "Illegal ID");
		}
    }

    public void makeKeyword(StringBuilder token) {
    	String tokenType = TokenType.KEYWORDS; 
    	Token.addSymbol(token.toString(), tokenType);
    	System.out.println(token + "\t\t\t" + tokenType);
    	//Token.printTable();
    }
    
    public static void makeID(StringBuilder token) {
    	String tokenType = TokenType.IDENTIFIER; 
    	Token.addSymbol(token.toString(), tokenType);
    	System.out.println(token + "\t\t" + tokenType);
    	//Token.printTable();
    }
    
    public static void makeLP(StringBuilder token) {
    	String tokenType = TokenType.LPAREN; 
    	Token.addSymbol(token.toString(), tokenType);
    	System.out.println(token + "\t\t\t" + tokenType);

    }
    public static void makeRP(StringBuilder token) {
    	String tokenType = TokenType.RPAREN; 
    	Token.addSymbol(token.toString(), tokenType);
    	System.out.println(token + "\t\t\t" + tokenType);

    }
    
    public static boolean isKeyword(StringBuilder token) {
    	String [] keywords = {"program", "begin", "int", "if", "else_if", "else", "print_line", "end", "while"};
    	for (String keyword : keywords) {
            if (keyword.equals(token.toString().strip())) {
            	return true;
            }
        }
		//System.out.println("checking keyword: " + token);
    	return false;
    }
        
    
    
    
	public void tokenizeIdentifier(String token) {
		if(token.isEmpty()) return;
    	int position = 0;
    	int flag = 0;
    	char buf = ' ';
    	StringBuilder restToken = new StringBuilder();
    	
    	StringBuilder newToken = new StringBuilder();
    	token = token.strip();
    	
    	while (position < token.length()) {
            char currentChar = token.charAt(position);
            //System.out.println("current char: " + currentChar);
            if (isDelimiter(currentChar)) {
            	if(currentChar!=';' && currentChar!=',' && position == token.length()-1) {
            		isIllegal = 1;
            		
            	}
            	else if(currentChar == '\"') {
            		
            	}
            	else {
            		flag=1;
            		buf = currentChar;
            		restToken.append(token.substring(position+1));
            		
            	}
                break;
            }
            else if (!(Lexer.isLegalID(currentChar))) {
            	isIllegal = -1;
            }
            newToken.append(currentChar);
            position++;
        }
    	
    	if(isIllegal == -1) {
    		Token.addSymbol(token, "Illegal ID");
    	}
    	
    	if(isKeyword(newToken)) {
    		makeKeyword(newToken);
    		//System.out.println(newToken + " token made");
    		//Token.printToken(keyword);
    	}
    	else {
    		int currState = 0;
    		for (char c : newToken.toString().toCharArray()) {
    		    currState = FSM.idStates(currState, c);
    		    if(currState == -1) break;
    		}
    		if(currState != -1) {
    			
    			makeID(newToken);
    			
    		}
    		else {
    			Token.addSymbol(token, "Illegal ID");
    	    	System.out.println(token + "\t\t\t" + "Illegal ID");
    		}
    		//Token.printToken(makeID(newToken));
    	}
    	
//    	if(token.charAt(position)=='(') {
//    		
//    	}
    	if(position>=(token.length()-1)) return;
    	if(flag == 1) {
    		Operator op = new Operator();
    		op.checkOperator(buf);
    	}
    	if(!restToken.isEmpty()) tokenizeIdentifier(restToken.toString());
    	restToken.setLength(0);
    	
    }
}
