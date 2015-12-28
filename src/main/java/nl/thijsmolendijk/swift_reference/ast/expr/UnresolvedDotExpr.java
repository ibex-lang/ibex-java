package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class UnresolvedDotExpr extends Expr {
    private Expr subExpr;
    private Diagnostics.SourceLocation dotLoc;
    private Identifier name;
    private Diagnostics.SourceLocation nameLoc;

    public UnresolvedDotExpr(Expr subExpr, Diagnostics.SourceLocation dotLoc, Identifier name, Diagnostics.SourceLocation nameLoc) {
        this.subExpr = subExpr;
        this.dotLoc = dotLoc;
        this.name = name;
        this.nameLoc = nameLoc;
    }

    public Expr getSubExpr() {
        return subExpr;
    }

    public void setSubExpr(Expr subExpr) {
        this.subExpr = subExpr;
    }

    public Identifier getName() {
        return name;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return subExpr != null ? subExpr.getStartingLoc() : dotLoc;
    }
}
