package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public abstract class NamedDecl extends Decl {
    protected Identifier name;

    public NamedDecl(Identifier name) {
        this.name = name;
    }

    public Identifier getName() {
        return name;
    }
}
