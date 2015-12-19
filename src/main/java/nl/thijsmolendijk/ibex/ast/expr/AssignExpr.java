package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents an assignment.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class AssignExpr extends Expression {
    private Expression dest;
    private SourceLocation eqLoc;
    private Expression value;

    public AssignExpr(Expression dest, SourceLocation eqLoc, Expression value) {
        this.dest = dest;
        this.eqLoc = eqLoc;
        this.value = value;
    }

    public Expression getDest() {
        return dest;
    }

    public void setDest(Expression dest) {
        this.dest = dest;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public SourceLocation getLocation() {
        return dest.getLocation();
    }
}
