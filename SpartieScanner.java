import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpartieScanner {
    //TODO: Minimize hardcoded strings

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

        tokens.add(new Token(TokenType.EOF, "", line));
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

    private Token getSingleCharacterToken() {
        /* Single character tokens should include:
            Simple tokens
                SEMICOLON   ';'
                COMMA       ','
                LEFT_BRACE  '{'
                RIGHT_BRACE '}'
                LEFT_PAREN  '('
                RIGHT_PAREN ')'
                MULTIPLY    '*'
                ADD         '+'
                SUBTRACT    '-'
            Logical
                AND         '&'
                OR          '|'
            Ignore
                IGNORE      ' '
            Markers
                EOL         \n
         */

        // Hint: Examine the character, if you can get a token, return it, otherwise return null
        // Hint: Start of not knowing what the token is, if we can determine it, return it, otherwise, return null
        TokenType type = TokenType.UNDEFINED;
        char currentCharacter = source.charAt(current);
        switch (currentCharacter) {
            case ';':
                type = TokenType.SEMICOLON;
                break;
            case ',':
                type = TokenType.COMMA;
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
            case '&':
                type = TokenType.AND;
                break;
            case '|':
                type = TokenType.OR;
                break;
            case ' ':
                type = TokenType.IGNORE;
                break;
            case '\n':
                type =TokenType.EOL;
                line++;
                break;
        }

        if (type != TokenType.UNDEFINED) {
            current++;
            return new Token(type, String.valueOf(currentCharacter), line);
        }

        return null;
    }

    private Token getComparisonToken() {
        // Hint: Examine the character for a comparison but check the next character (as long as one is available)
        // For example: < or <=
        /* Comparison Tokens should include:
            Comparison
                EQUIVALENT      ==
                NOT_EQUAL       !=
                LESS_THAN       <
                LESS_EQUAL      <=
                GREATER_THAN    >
                GREATER_EQUAL   >=
            Simple tokens
                ASSIGN          =
                NOT             !
         */
        char currentCharacter = source.charAt(current);
        switch (currentCharacter) {
            case '=':
                if (examine('=')) { // If it's '=='
                    current += 2;
                    return new Token(TokenType.EQUIVALENT, "==", line);
                }
                // If it's '='
                current++;
                return new Token(TokenType.ASSIGN, String.valueOf(currentCharacter), line);
            case '!':
                if (examine('=')) { // if it's '!='
                    current += 2;
                    return new Token(TokenType.NOT_EQUAL, "!=", line);
                }
                // If it's '!'
                current++;
                return new Token(TokenType.NOT, "!", line);
            case '<':
                if (examine('=')) { // If it's '<='
                    current += 2;
                    return new Token(TokenType.LESS_EQUAL, "<=", line);
                }
                // If it's '<'
                current++;
                return new Token(TokenType.LESS_THAN, "<", line);
            case '>':
                if (examine('=')) { // If it's '>='
                    current += 2;
                    return new Token(TokenType.GREATER_EQUAL, ">=", line);
                }
                // If it's '>'
                current++;
                return new Token(TokenType.GREATER_THAN, ">", line);

        }
        return null;
    }

    private Token getDivideOrComment() {
        /* Divide or Comment should include:
            Simple Tokens
                DIVIDE  /
            Ignore
                IGNORE
         */
        char currentCharacter = source.charAt(current);
        if (currentCharacter == '/') {
            if (examine('/')) { // If it's '//'
                current += 2;
                while (source.charAt(current) != '\n') { // Go to the end of the line
                    current++;
                }
                current++;
                return new Token(TokenType.IGNORE, "", line);
            }
            // If it's '/'
            current++;
            return new Token(TokenType.DIVIDE, "/", line);
        }
        return null;
    }

    // TODO: Complete implementation
    private Token getStringToken() {
        // Hint: Check if you have a double quote, then keep reading until you hit another double quote
        // But, if you do not hit another double quote, you should report an error
        /* String Token should include:
            Value Types:
                STRING  ""
         */
        char currentCharacter = source.charAt(current);
        if (currentCharacter == '"') {
            int start = current;
            current++;
            while (true) {
                if (isAtEnd()) error(line, String.format("Unterminated String %s at %d", source.substring(start, current), start));
                currentCharacter = source.charAt(current);
                if (currentCharacter == '\n') error(line, String.format("Unterminated String %s at %d", source.substring(start, current), start));
                if (currentCharacter == '"') {
                    int end = current;
                    current++;
                    return new Token(TokenType.STRING, source.substring(start, end), line);
                }
                current++;
            }
        }
        return null;
    }

    private Token getNumericToken() {
        // Hint: Follow similar idea of String, but in this case if it is a digit
        // You should only allow one period in your scanner
        char currentCharacter = source.charAt(current);
        if (isDigit(currentCharacter)) {
            int start = current;
            boolean decimalPoint = false;
            while (isDigit(currentCharacter)) {
                if (examine('.')) {
                    if (decimalPoint) error(line, String.format("Unterminated number %s at %d", source.substring(start, current), current));
                    decimalPoint = true;
                    current += 2;
                    if (isAtEnd() || !isDigit(source.charAt(current))) error(line, String.format("Unterminated number %s at %d", source.substring(start, current), current));
                    currentCharacter = source.charAt(current);
                } else if (examine(' ') || examine('\n')) {
                    current++;
                    return new Token(TokenType.NUMBER, source.substring(start, current), line);
                } else {
                    current++;
                    currentCharacter = source.charAt(current);
                }
            }
            error(line, String.format("Unexpected character %c at %d", currentCharacter, current));
        }
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
