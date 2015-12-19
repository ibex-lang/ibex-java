package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.type.Type;

/**
 * Represents the declaration of a function.
 * The type in this decl is the type of the function, whereas the init is the contents of the function.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class FnDecl extends ValueDecl {
    private SourceLocation location;

    public FnDecl(Identifier name, Type type, Expression init, SourceLocation location) {
        super(name, type, init);
        this.location = location;
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
