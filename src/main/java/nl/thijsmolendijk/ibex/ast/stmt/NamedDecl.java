package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.expr.Identifier;

/**
 * Represents a declaration with a name.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public abstract class NamedDecl extends Decl {
    private Identifier name;

    protected NamedDecl(Identifier name) {
        this.name = name;
    }

    public Identifier getName() {
        return name;
    }
}
