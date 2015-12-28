package nl.thijsmolendijk.swift_reference.parse;

import nl.thijsmolendijk.swift_reference.Diagnostics;

/**
 * Created by molenzwiebel on 16-12-15.
 */
public class Token {
    private TokenType kind;
    private String text;
    private Diagnostics.SourceLocation location;
    private Diagnostics.SourceRange range;

    public Token(TokenType kind, String text, Diagnostics.SourceLocation loc) {
        this.kind = kind;
        this.text = text;
        this.location = loc;
        this.range = loc.range(text.length());
    }

    public TokenType getKind() {
        return kind;
    }

    public String getText() {
        return text;
    }

    public Diagnostics.SourceLocation getLocation() {
        return location;
    }

    public Diagnostics.SourceRange getRange() {
        return range;
    }

    public boolean is(TokenType check) {
        return kind == check;
    }

    public boolean isNot(TokenType check) {
        return kind != check;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Token{");
        sb.append("kind=").append(kind);
        sb.append(", text='").append(text).append('\'');
        sb.append(", location=").append(location);
        sb.append(", range=").append(range);
        sb.append('}');
        return sb.toString();
    }

    public enum TokenType {
        UNKNOWN,
        EOF,
        IDENTIFIER,
        NUMBER,

        KW_INT1_TYPE,
        KW_INT8_TYPE,
        KW_INT16_TYPE,
        KW_INT32_TYPE,
        KW_INT64_TYPE,

        KW_IMPORT,
        KW_ONEOF,
        KW_STRUCT,
        KW_MODULE,
        KW_FN,
        KW_LET,
        KW_TYPEALIAS,

        KW_IF,
        KW_ELSE,
        KW_WHILE,
        KW_RETURN,

        LEFT_PAREN_SPACE,
        LEFT_PAREN,
        RIGHT_PAREN,
        LEFT_BRACE,
        RIGHT_BRACE,
        LEFT_SQUARE,
        RIGHT_SQUARE,

        PERIOD,
        COMMA,
        COLON,
        DOUBLE_COLON,
        SEMICOLON,
        EQUAL,

        ARROW,
        OPERATOR;
    }
}
