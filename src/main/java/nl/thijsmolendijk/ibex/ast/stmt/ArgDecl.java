package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.type.Type;

/**
 * Represents a declaration of a function argument.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class ArgDecl extends ValueDecl {
    private SourceLocation location;

    public ArgDecl(Identifier name, Type type, SourceLocation loc) {
        super(name, type, null);
        this.location = loc;
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
