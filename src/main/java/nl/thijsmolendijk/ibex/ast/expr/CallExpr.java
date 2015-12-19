package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents the calling of a function.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class CallExpr extends Expression {
    private Expression function;
    private Expression arg;

    public CallExpr(Expression function, Expression arg) {
        this.function = function;
        this.arg = arg;
    }

    public Expression getFunction() {
        return function;
    }

    public void setFunction(Expression function) {
        this.function = function;
    }

    public Expression getArg() {
        return arg;
    }

    public void setArg(Expression arg) {
        this.arg = arg;
    }

    @Override
    public SourceLocation getLocation() {
        return arg.getLocation();
    }
}
