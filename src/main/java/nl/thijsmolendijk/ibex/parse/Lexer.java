package nl.thijsmolendijk.ibex.parse;

import static nl.thijsmolendijk.ibex.parse.TokenType.*;

/**
 * Lexes source into tokens.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class Lexer {
    private static final Object[][] KEYWORDS = new Object[][] {
            new Object[]{"fn", KW_FN},
            new Object[]{"match", KW_MATCH},
            new Object[]{"if", KW_IF},
            new Object[]{"else", KW_ELSE},
            new Object[]{"use", KW_USE},
            new Object[]{"module", KW_MODULE},
            new Object[]{"type", KW_TYPE},
            new Object[]{"as", KW_AS},
            new Object[]{"extern", KW_EXTERN},
            new Object[]{"let", KW_LET}
    };

    private String filename;
    private String source;
    private int offset;

    private Token nextToken;

    public Lexer(String filename, String source) {
        this.filename = filename;
        this.source = source;
        this.offset = 0;

        this.nextToken = lexImpl();
    }

    public Token lex() {
        Token res = nextToken;
        if (res.isNot(EOF)) {
            this.nextToken = lexImpl();
        }
        return res;
    }

    public Token peekToken() {
        return nextToken;
    }

    public Token lexImpl() {
        assert offset >= 0 && offset < source.length() : "Invalid bounds in lexer.";
        int start = offset;

        switch (nextChar()) {
            default:
                error("invalid character in source file");
                return newToken(UNKNOWN, start);

            case ' ':
            case '\t':
            case '\n':
            case '\r':
                // Skip whitespace.
                return lexImpl();

            case 0:
                if (offset != source.length()) {
                    warning("nul character in middle of file");
                    return lexImpl();
                }
                return newToken(EOF, start);

            case '(': return newToken(LPAREN, start);
            case ')': return newToken(RPAREN, start);
            case '[': return newToken(LBRACKET, start);
            case ']': return newToken(RBRACKET, start);
            case '{': return newToken(LBRACE, start);
            case '}': return newToken(RBRACE, start);
            case ',': return newToken(COMMA, start);

            case ':':
                if (source.charAt(offset) != ':') return newToken(COLON, start);
                offset++;
                return newToken(DOUBLE_COLON, start);

            case '=':
            case '-':
            case '+':
            case '*':
            case '%':
            case '<':
            case '>':
            case '!':
            case '&':
            case '|':
            case '^':
            case '/':
                return lexOperatorOrPunctuation();

            // This is ugly, but that is the unfortunate result of java switches.
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G':
            case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N':
            case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U':
            case 'V': case 'W': case 'X': case 'Y': case 'Z':
            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g':
            case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n':
            case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u':
            case 'v': case 'w': case 'x': case 'y': case 'z':
            case '_':
                return lexIdentifier();

            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return lexDigit();
        }
    }

    /**
     * Lexes any operator, unless it is '->' or '=' in which case it returns the respective kind.
     */
    private Token lexOperatorOrPunctuation() {
        int start = offset - 1;
        while (isOperator(curChar())) {
            offset++;
        }

        // '=' is reserved, as is '->'
        if (offset - start == 1 && source.charAt(start) == '=') {
            return newToken(EQUAL, start);
        } else if (offset - start == 2 && source.charAt(start) == '-' && source.charAt(start + 1) == '>') {
            return newToken(ARROW, start);
        }

        return newToken(OPERATOR, start);
    }

    /**
     * Lexes an identifier and/or keyword.
     */
    private Token lexIdentifier() {
        int start = offset - 1;

        while (isValidIdentPart(curChar())) {
            offset++;
        }

        String part = source.substring(start, offset);
        for (Object[] opt : KEYWORDS) {
            if (opt[0].equals(part)) return newToken((TokenType) opt[1], start);
        }

        return newToken(IDENTIFIER, start);
    }

    /**
     * Lexes an integer literal.
     */
    public Token lexDigit() {
        int start = offset - 1;

        while (isNumber(curChar())) {
            offset++;
        }

        return newToken(INTEGER, start);
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isValidIdentPart(char c) {
        return c == '_' || c == '$' || c == '-' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    private boolean isOperator(char c) {
        return "/=-+*%<>!&|^".contains(c + "");
    }

    private char curChar() {
        return offset == source.length() ? 0 : source.charAt(offset);
    }

    private char nextChar() {
        return offset == source.length() ? 0 : source.charAt(offset++);
    }

    private Token newToken(TokenType type, int start) {
        return new Token(type, source.substring(start, offset), new SourceLocation(filename, source, start));
    }

    private void warning(String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.WARNING, message, new SourceLocation(filename, source, offset));
    }

    private void error(String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, message, new SourceLocation(filename, source, offset));
    }
}
