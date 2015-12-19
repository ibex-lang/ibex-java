package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents a not yet resolved reference to something. Gets converted to DeclRefExpr once resolved.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class UnresolvedRefExpr extends Expression {
    private SourceLocation loc;
    private Identifier name;

    public UnresolvedRefExpr(SourceLocation loc, Identifier name) {
        this.loc = loc;
        this.name = name;
    }

    public Identifier getName() {
        return name;
    }

    @Override
    public SourceLocation getLocation() {
        return loc;
    }
}
