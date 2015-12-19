package nl.thijsmolendijk.ibex.ast;

/**
 * Represents an identifier in the source file.
 * There is always a single instance of an identifier with a specified value.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class Identifier {
    private String value;

    public Identifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
