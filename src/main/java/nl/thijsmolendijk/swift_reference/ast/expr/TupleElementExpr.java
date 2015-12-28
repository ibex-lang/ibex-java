package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class TupleElementExpr extends Expr {
    private Expr subExpr;
    private Diagnostics.SourceLocation dotLoc, nameLoc;
    private int fieldNo;

    public TupleElementExpr(Expr subExpr, Diagnostics.SourceLocation dotLoc, Diagnostics.SourceLocation nameLoc, int fieldNo) {
        this.subExpr = subExpr;
        this.dotLoc = dotLoc;
        this.nameLoc = nameLoc;
        this.fieldNo = fieldNo;
    }

    public Expr getSubExpr() {
        return subExpr;
    }

    public void setSubExpr(Expr subExpr) {
        this.subExpr = subExpr;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return dotLoc;
    }
}
