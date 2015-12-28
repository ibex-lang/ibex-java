package nl.thijsmolendijk.swift_reference.ast.expr;

/**
 * Created by molenzwiebel on 17-12-15.
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
