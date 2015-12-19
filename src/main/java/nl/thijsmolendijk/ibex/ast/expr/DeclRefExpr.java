package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.stmt.ValueDecl;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents a resolved reference to a decl, whether it be a function, type, arg or variable.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class DeclRefExpr extends Expression {
    private ValueDecl decl;
    private SourceLocation loc;

    public DeclRefExpr(ValueDecl decl, SourceLocation loc) {
        this.decl = decl;
        this.loc = loc;
    }

    public ValueDecl getDecl() {
        return decl;
    }

    @Override
    public SourceLocation getLocation() {
        return loc;
    }
}
