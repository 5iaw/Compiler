package smalllexer;
import java.io.BufferedReader;
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
    
    public void tokenize() {
    	try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            StringBuilder buffer = new StringBuilder();
            //int str_flag = 0;
            
            
            while ((line = reader.readLine()) != null) {
            	line = line.trim();
                String[] tokens = line.split("\\s+"); // Split line by whitespace
                
                
                
                // Process tokens
                for (int i=0; i<tokens.length; i++) {
                    //System.out.println("Token: " + token);
                	tokens[i] = tokens[i].trim();
                	//System.out.println(tokens[i]);
                	
                	                	
                	char firstChar = tokens[i].charAt(0);                 	

                	if(Character.isLetter(firstChar) || firstChar=='$') {    // ID or keyword or other
                		ExpandIdentifier id = new ExpandIdentifier(tokens[i]);
                		//System.out.println("expand id");
                		id.tokenizeIdentifier(tokens[i]); 
                		//System.out.println(tokens[i] + " done");
                	}
                	

                	else if(Character.isDigit(firstChar)) {                  // number literal
                		int hasDel = 0;
                		StringBuilder splitTok = new StringBuilder();
                		if(tokens[i].charAt(tokens[i].length()-1) == ',' || tokens[i].charAt(tokens[i].length()-1) == ';' || tokens[i].charAt(tokens[i].length()-1) == ')') {
                			if(tokens[i].charAt(tokens[i].length()-1) == ',') hasDel = 1;
                			else hasDel = 2;
                			splitTok.append(tokens[i].substring(0, tokens[i].length()-1));
                		}
                		if(hasDel != 0) {
                    		Number_Literal num = new Number_Literal();
                    		num.checkNumLiteral(splitTok.toString());
                    		Operator op = new Operator();
                    		if(hasDel == 1) op.checkOperator(',');
                    		else op.checkOperator(';');
                		}
                		else {
                		Number_Literal num = new Number_Literal();
                		num.checkNumLiteral(tokens[i]);
                			
                		}
                	}
                	
                	else if (tokens[i].length() == 1 && (firstChar=='=' || firstChar=='+' || firstChar=='*' || firstChar=='<' || firstChar==',' || firstChar==';')) {   // operators = + *  and <
                		//System.out.println(tokens[i] + " make Op");
                		Operator op = new Operator();
                		op.checkOperator(firstChar);
                	}
                	
                	else if(firstChar=='-' /*&& secChar=='-' */) {                // comments --
                		//if(str_flag == 0) str_flag = 1;
                		
                		for(int j=i; j<tokens.length; j++) {
                			//System.out.println(tokens[j]);
            	            if (j == i) {
            	                buffer.append(tokens[j]); 
            	            } else {
            	                buffer.append(" ").append(tokens[j]);    // combine all tokens to form comment
            	            }
            	        
                		}
                		//System.out.println(buffer);
                		ExpandIdentifier.checkComment(buffer.toString());
                		break;
                		
                	}
                	                	
                	else if(firstChar=='(') {                                // left parenthesis
                		StringBuilder splitTok = new StringBuilder();
                		if(tokens[i].length() > 1) {
                			Token.addSymbol(firstChar+"", TokenType.LPAREN);
                	    	System.out.println(firstChar + "\t\t\t" + TokenType.LPAREN);
                			splitTok.append(tokens[i].substring(1));
                			ExpandIdentifier id = new ExpandIdentifier(tokens[i]);
                			id.tokenizeIdentifier(splitTok.toString());
                			continue;
                		}
            			Token.addSymbol(tokens[i], TokenType.LPAREN);
            	    	System.out.println(tokens[i] + "\t" + TokenType.LPAREN);
                	}
                	
                	else if(firstChar==')') {                                // right parenthesis
                		StringBuilder splitTok = new StringBuilder();
                		if(tokens[i].length() == 2) {
                			Token.addSymbol(firstChar+"", TokenType.RPAREN);
                	    	System.out.println(firstChar + "\t\t\t" + TokenType.RPAREN);
                	    	if(tokens[i].charAt(1) == ';') {
                        		Operator op = new Operator();
                        		op.checkOperator(tokens[i].charAt(1));
                	    	}
                	    	else {
                	    		ExpandIdentifier id = new ExpandIdentifier(tokens[i]);
                	    		id.tokenizeIdentifier(splitTok.toString());
                	    	}
                			continue;
                		}
                		Token.addSymbol(tokens[i], TokenType.RPAREN);
            	    	System.out.println(tokens[i] + "\t" + TokenType.RPAREN);
                	}
                	
                	else {
            			Token.addSymbol(tokens[i], "Illegal ID");
            	    	System.out.println(tokens[i] + "\t\t\t" + "Illegal ID starting with wrong character");
                	
                	}
                	
//                	if(str_flag == 1) {
//                		str_flag=0;
//                		break;
//                	}
                
                }
                buffer.setLength(0);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
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