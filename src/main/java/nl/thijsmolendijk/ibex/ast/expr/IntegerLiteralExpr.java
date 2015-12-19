package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents an integer literal.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class IntegerLiteralExpr extends Expression {
    private SourceLocation loc;
    private String val;

    public IntegerLiteralExpr(SourceLocation loc, String val) {
        this.loc = loc;
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    @Override
    public SourceLocation getLocation() {
        return loc;
    }
}
