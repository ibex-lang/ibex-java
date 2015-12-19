package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents accessing a field of a tuple, e.g. (1, 2).0
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class TupleAccessExpr extends Expression {
    private Expression name; // @Nullable
    private SourceLocation dotLoc, nameLoc;
    private int fieldNo; // unused if name != null.

    public TupleAccessExpr(Expression name, SourceLocation dotLoc, SourceLocation nameLoc, int fieldNo) {
        this.name = name;
        this.dotLoc = dotLoc;
        this.nameLoc = nameLoc;
        this.fieldNo = fieldNo;
    }

    public Expression getName() {
        return name;
    }

    public void setName(Expression name) {
        this.name = name;
    }

    @Override
    public SourceLocation getLocation() {
        return dotLoc;
    }
}
