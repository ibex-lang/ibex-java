package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents a sequence, or a list of binary operations where the precedence has not yet been inferred.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class SequenceExpr extends Expression {
    private Expression[] contents;

    public SequenceExpr(Expression[] contents) {
        this.contents = contents;
    }

    public Expression[] getContents() {
        return contents;
    }

    @Override
    public SourceLocation getLocation() {
        return contents[0].getLocation();
    }
}
