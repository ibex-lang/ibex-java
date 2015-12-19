package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents a tuple 'literal'.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class TupleExpr extends Expression {
    private SourceLocation lbrace, rbrace;
    private Expression[] expressions;
    private Identifier[] names;

    private boolean isGrouping;

    public TupleExpr(SourceLocation lbrace, SourceLocation rbrace, Expression[] expressions, Identifier[] names) {
        this.lbrace = lbrace;
        this.rbrace = rbrace;
        this.expressions = expressions;
        this.names = names;

        this.isGrouping = expressions.length == 1 && (names.length == 0 || names[0] == null || names[0].getValue().isEmpty());
    }

    public Expression[] getExpressions() {
        return expressions;
    }

    public Identifier[] getNames() {
        return names;
    }

    /**
     * @return if this tuple is probably a grouping paren (as in, used in the context `(a + b) * c`)
     */
    public boolean isGrouping() {
        return isGrouping;
    }

    @Override
    public SourceLocation getLocation() {
        return lbrace;
    }
}
