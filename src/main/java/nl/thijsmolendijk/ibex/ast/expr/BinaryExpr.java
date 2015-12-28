package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents a binary function call.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class BinaryExpr extends Expression {
    private Expression left;
    private Expression fun;
    private Expression right;

    public BinaryExpr(Expression left, Expression fun, Expression right) {
        this.left = left;
        this.fun = fun;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Expression getFun() {
        return fun;
    }

    @Override
    public SourceLocation getLocation() {
        return left.getLocation();
    }
}
