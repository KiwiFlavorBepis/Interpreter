import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpartieScanner {
    private String source;

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("fun", TokenType.FUN);
        keywords.put("return", TokenType.RETURN);
        keywords.put("var", TokenType.VAR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("null", TokenType.NULL);
    }

    public SpartieScanner(String source) {
        this.source = source;
    }

    public List<Token> scan() {
        List<Token> tokens = new ArrayList<>();

        Token token = null;
        while (!isAtEnd() && (token = getNextToken()) != null) {
            if (token.type != TokenType.IGNORE) tokens.add(token);
        }

        return tokens;
    }

    private Token getNextToken() {
        Token token = null;

        // Try to get each type of token, starting with a simple token, and getting a little more complex
        token = getSingleCharacterToken();
        if (token == null) token = getComparisonToken();
        if (token == null) token = getDivideOrComment();
        if (token == null) token = getStringToken();
        if (token == null) token = getNumericToken();
        if (token == null) token = getIdentifierOrReservedWord();
        if (token == null) {
            error(line, String.format("Unexpected character '%c' at %d", source.charAt(current), current));
        }

        return token;
    }

    // TODO: Complete implementation
    private Token getSingleCharacterToken() {
        /* Single character tokens should include:
           Simple tokens
            SEMICOLON,
            COMMA,
            ASSIGN, // =
            LEFT_BRACE, RIGHT_BRACE, // { }
            LEFT_PAREN, RIGHT_PAREN, // ( )
            MULTIPLY, //  *
            ADD, SUBTRACT, // + -
            NOT, // !
           Logical
            AND, OR, // & |
         */

        // Hint: Examine the character, if you can get a token, return it, otherwise return null
        // Hint: Be careful with the divide, we have ot know if it is a single character
        // Hint: Start of not knowing what the token is, if we can determine it, return it, otherwise, return null
        TokenType type = TokenType.UNDEFINED;
        char nextCharacter = source.charAt(current);
        switch (nextCharacter) {
            case ';':
                type = TokenType.SEMICOLON;
                break;
            case ',':
                type = TokenType.COMMA;
                break;
            case '=':
                type = TokenType.ASSIGN;
                break;
            case '{':
                type = TokenType.LEFT_BRACE;
                break;
            case '}':
                type = TokenType.RIGHT_BRACE;
                break;
            case '(':
                type = TokenType.LEFT_PAREN;
                break;
            case ')':
                type = TokenType.RIGHT_PAREN;
                break;
            case '*':
                type = TokenType.MULTIPLY;
                break;
            case '+':
                type = TokenType.ADD;
                break;
            case '-':
                type = TokenType.SUBTRACT;
                break;
            case '!':
                type = TokenType.NOT;
                break;
            case '&':
                type = TokenType.AND;
                break;
            case '|':
                type = TokenType.OR;
                break;
        }

        if (type != TokenType.UNDEFINED) return new Token(type, String.valueOf(nextCharacter), line);
        return null;
    }

    // TODO: Complete implementation
    private Token getComparisonToken() {
        // Hint: Examine the character for a comparison but check the next character (as long as one is available)
        // For example: < or <=
        char nextCharacter = source.charAt(current);

        return null;
    }

    // TODO: Complete implementation
    private Token getDivideOrComment() {
        // Hint: Examine the character for a comparison but check the next character (as long as one is available)
        char nextCharacter = source.charAt(current);

        return null;
    }

    // TODO: Complete implementation
    private Token getStringToken() {
        // Hint: Check if you have a double quote, then keep reading until you hit another double quote
        // But, if you do not hit another double quote, you should report an error
        char nextCharacter = source.charAt(current);

        String string = null;

        return null;
    }

    // TODO: Complete implementation
    private Token getNumericToken() {
        // Hint: Follow similar idea of String, but in this case if it is a digit
        // You should only allow one period in your scanner
        return null;
    }

    // TODO: Complete implementation
    private Token getIdentifierOrReservedWord() {
        // Hint: Assume first it is an identifier and once you capture it, then check if it is a reserved word.
        return null;
    }
    
    // Helper Methods
    private boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

    private boolean isAlpha(char character) {
        return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z';
    }

    // This will check if a character is what you expect, if so, it will advance
    // Useful for checking <= or //
    private boolean examine(char expected) {
        if (isAtEnd()) return false;
        return source.charAt(current + 1) == expected;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Error handling
    private void error(int line, String message) {
        System.err.printf("Error occurred on line %d : %s\n", line, message);
        System.exit(ErrorCode.INTERPRET_ERROR);
    }
}
