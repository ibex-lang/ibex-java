package nl.thijsmolendijk.swift_reference.parse;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import static nl.thijsmolendijk.swift_reference.parse.Token.TokenType.*;

/**
 * Created by molenzwiebel on 16-12-15.
 */
public class Lexer {
    private static final Object[][] KEYWORDS = new Object[][] {
            new Object[]{"_int1", KW_INT1_TYPE},
            new Object[]{"_int8", KW_INT8_TYPE},
            new Object[]{"_int16", KW_INT16_TYPE},
            new Object[]{"_int32", KW_INT32_TYPE},
            new Object[]{"_int64", KW_INT64_TYPE},

            new Object[]{"import", KW_IMPORT},
            new Object[]{"oneof", KW_ONEOF},
            new Object[]{"struct", KW_STRUCT},
            new Object[]{"fn", KW_FN},
            new Object[]{"let", KW_LET},
            new Object[]{"module", KW_MODULE},
            new Object[]{"typealias", KW_TYPEALIAS},

            new Object[]{"if", KW_IF},
            new Object[]{"else", KW_ELSE},
            new Object[]{"while", KW_WHILE},
            new Object[]{"return", KW_RETURN}
    };

    private ASTContext context;

    private String filename;
    private String source;
    private int offset;

    private Token nextToken;

    public Lexer(String filename, String source, ASTContext context) {
        this.context = context;
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
                return lexImpl();

            case 0:
                if (offset != source.length()) {
                    warning("nul character in middle of file");
                    return lexImpl();
                }
                return newToken(EOF, start);

            case '(':
                boolean previousWasSpace = offset - 2 <= 0 || source.charAt(offset - 2) == ' ';
                return newToken(previousWasSpace ? LEFT_PAREN_SPACE : LEFT_PAREN, start);
            case ')': return newToken(RIGHT_PAREN, start);
            case '{': return newToken(LEFT_BRACE, start);
            case '}': return newToken(RIGHT_BRACE, start);
            case '[': return newToken(LEFT_SQUARE, start);
            case ']': return newToken(RIGHT_SQUARE, start);

            case '.': return newToken(PERIOD, start);
            case ';': return newToken(SEMICOLON, start);
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

    private Token lexOperatorOrPunctuation() {
        int start = offset - 1;
        while (isPunctuation(curChar())) {
            offset++;
        }

        if (offset - start == 1 && source.charAt(start) == '=') {
            return newToken(EQUAL, start);
        } else if (offset - start == 2 && source.charAt(start) == '-' && source.charAt(start + 1) == '>') {
            return newToken(ARROW, start);
        }

        return newToken(OPERATOR, start);
    }

    private Token lexIdentifier() {
        int start = offset - 1;

        while (isValidIdentPart(curChar())) {
            offset++;
        }

        String part = source.substring(start, offset);
        for (Object[] opt : KEYWORDS) {
            if (opt[0].equals(part)) return newToken((Token.TokenType) opt[1], start);
        }

        return newToken(IDENTIFIER, start);
    }

    private Token lexDigit() {
        int start = offset - 1;

        while (isNumber(curChar())) {
            offset++;
        }

        return newToken(NUMBER, start);
    }

    private boolean isPunctuation(char c) {
        return "/=-+*%<>!&|^".contains(c + "");
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isValidIdentPart(char c) {
        return c == '_' || c == '$' || c == '-' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    private char curChar() {
        return offset == source.length() ? 0 : source.charAt(offset);
    }

    private char nextChar() {
        return offset == source.length() ? 0 : source.charAt(offset++);
    }

    private Token newToken(Token.TokenType type, int start) {
        return new Token(type, source.substring(start, offset), new Diagnostics.SourceLocation(filename, source, start));
    }

    private void warning(String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.WARNING, message, new Diagnostics.SourceLocation(filename, source, offset));
    }

    private void error(String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, message, new Diagnostics.SourceLocation(filename, source, offset));
    }
}
