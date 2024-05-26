package smalllexer;

public class FSM {
	//private int currentState = 0;

	public static int idStates(int currentState, char input) {
        switch (currentState) {
            case 0:
                if (Character.isLetter(input) || input == '$') {
                    return 1;
                } else {
                    return -1; // invalid starting
                }
            case 1:
                if (Lexer.isLegalID(input)) {
                    return 1; 
                } else {
                    return -1; // invalid between
                }
            default:
                return -1; // Error
        }
    }
	
	public static int numStates(int currentState, char input) {
        switch (currentState) {
            case 0:
                if (Character.isDigit(input)) {
                    return 1;
                } else {
                    return -1; // not num
                }
            case 1:
                if (Character.isDigit(input)) {
                    return 1; 
                } else {
                    return -1; // invalid 
                }
            default:
                return -1; // Error
        }
    }
	
	public static int commentStates(int currentState, char input) {
		switch (currentState) {
        case 0:
            if (input == '-') {
                return 1;
            } else {
                return -1; // not comment
            }
        case 1:
        	if(input == '-') {
        		return 2;
        	} else {
        		return -1;
        	}
        case 2:
        	return (input == '\n') ? -1 : 2;
        default:
            return -1; // Error
		}
    
	}
	
	public static int opStates(int currentState, char input) {
		switch (currentState) {
        case 0:
            if (input == '=' || input == '+' || input == '*' || input == '<' || input == '(' || input == ')' || input == ',' || input == ';') {
                return 1;
            } else {
                return -1; // not ops?
            }
        case 1:
        	return (input == '\n') ? -1 : 2;
        default:
            return -1; // Error
		}
    
	}
	
	public static int stringStates(int currentState, char input) {
		switch (currentState) {
        case 0:
            if (input == '\"') { // first opening "
                return 1;
            } else {
                return -1; // not comment
            }
        case 1:
            if (input == '\"') { // closing "
                return 2;
            } else { // any other character inside the string
                return 1;
            }
        default:
            return -1; // Error
		}
    
	}
	
//	public static int generalState(int currentState, char input) {
//		switch (currentState) {
//		case 0:
//			if(input!=' ' && input!='-') {
//				return 0;
//			}
//			else if(input==' ') {
//				return 
//			}
//		}
//	}
	
}
