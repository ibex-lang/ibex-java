package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class IntegerLiteralExpr extends Expr {
    private Diagnostics.SourceLocation loc;
    private String val;

    public IntegerLiteralExpr(Diagnostics.SourceLocation loc, String val) {
        this.loc = loc;
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
