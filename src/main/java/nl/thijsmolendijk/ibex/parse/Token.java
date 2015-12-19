package nl.thijsmolendijk.ibex.parse;

/**
 * Represents a single token in a source file.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class Token {
    private TokenType kind;
    private String text;
    private SourceLocation location;
    private SourceRange range;

    public Token(TokenType kind, String text, SourceLocation loc) {
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

    public SourceLocation getLocation() {
        return location;
    }

    public SourceRange getRange() {
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
        return "Token{" + "kind=" + kind +
                ", text='" + text + '\'' +
                ", location=" + location +
                ", range=" + range +
                '}';
    }
}
