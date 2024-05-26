package smalllexer;

import java.util.HashMap;
import java.util.Map;

public class Token {
    private static Map<String, String> table;

    public Token() {
        table = new HashMap<>();
    }
    
    public static Map<String, String> getTable() {
        return table;
    }


	public static void addSymbol(String value, String tokenType) {
        table.put(value, tokenType);
        // print?
    }
	
	public static void printTable() {
        //System.out.println("Token Table:");
        for (Map.Entry<String, String> entry : table.entrySet()) {
            System.out.println(entry.getKey() + " \t " + entry.getValue());
        }
    }

//	public static void printToken(S) {
//    	System.out.println(tokenType + "\t" + value);
//	}
    
    
}


