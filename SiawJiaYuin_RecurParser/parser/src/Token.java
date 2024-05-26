import java.util.ArrayList;
import java.util.List;

public class Token {
    private static List<TokenEntry> table;

    static {
        table = new ArrayList<>();
    }

    public static List<TokenEntry> getTable() {
        return table;
    }

    public static void addSymbol(String value, String tokenType) {
        table.add(new TokenEntry(value, tokenType));
    }

    public static void printTable() {
        System.out.println("Token Table:");
        for (TokenEntry entry : table) {
            System.out.println(entry.getValue() + " \t " + entry.getTokenType());
        }
    }

    public static class TokenEntry {
        private String value;
        private String tokenType;

        public TokenEntry(String value, String tokenType) {
            this.value = value;
            this.tokenType = tokenType;
        }

        public String getValue() {
            return value;
        }

        public String getTokenType() {
            return tokenType;
        }
    }
}
