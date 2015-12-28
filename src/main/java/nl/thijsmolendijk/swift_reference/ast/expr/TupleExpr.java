package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class TupleExpr extends Expr {
    private Diagnostics.SourceLocation lparenLoc;
    private Diagnostics.SourceLocation rparenLoc;

    private Expr[] subExprs;
    private Identifier[] names;

    private boolean isGrouping;

    public TupleExpr(Diagnostics.SourceLocation lparenLoc, Diagnostics.SourceLocation rparenLoc, Expr[] subExprs, Identifier[] names, boolean isGrouping) {
        this.lparenLoc = lparenLoc;
        this.rparenLoc = rparenLoc;
        this.subExprs = subExprs;
        this.names = names;
        this.isGrouping = isGrouping;

        assert ((!isGrouping || (subExprs.length == 1 && names[0] != null && subExprs[0] != null))) : "Invalid isGrouping";
    }

    public Identifier getElementName(int idx) {
        return names[idx];
    }

    public boolean isGrouping() {
        return isGrouping;
    }

    public Expr[] getSubExprs() {
        return subExprs;
    }

    public Identifier[] getNames() {
        return names;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return lparenLoc;
    }
}
