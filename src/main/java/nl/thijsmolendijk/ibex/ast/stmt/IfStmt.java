package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class IfStmt extends Statement {
    private SourceLocation loc;
    private Expression condition;
    private Statement ifThen;
    private Statement ifElse; // @Nullable

    public IfStmt(SourceLocation loc, Expression condition, Statement ifThen, Statement ifElse) {
        this.loc = loc;
        this.condition = condition;
        this.ifThen = ifThen;
        this.ifElse = ifElse;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Statement getIfThen() {
        return ifThen;
    }

    public void setIfThen(Statement ifThen) {
        this.ifThen = ifThen;
    }

    public Statement getIfElse() {
        return ifElse;
    }

    public void setIfElse(Statement ifElse) {
        this.ifElse = ifElse;
    }

    @Override
    public SourceLocation getLocation() {
        return loc;
    }
}
