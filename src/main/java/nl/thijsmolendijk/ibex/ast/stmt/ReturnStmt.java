package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents 'return expr?'
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class ReturnStmt extends Statement {
    private SourceLocation loc;
    private Expression value;

    public ReturnStmt(SourceLocation loc, Expression value) {
        this.loc = loc;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public SourceLocation getLocation() {
        return loc;
    }
}
