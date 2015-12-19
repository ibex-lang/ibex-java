package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.type.Type;

/**
 * Represents a variable declaration.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class VarDecl extends ValueDecl {
    private SourceLocation location;

    public VarDecl(Identifier name, Type type, Expression init, SourceLocation location) {
        super(name, type, init);
        this.location = location;
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
