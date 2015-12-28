package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.decl.ValueDecl;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class RefExpr extends Expr {
    private ValueDecl decl;
    private Diagnostics.SourceLocation loc;

    public RefExpr(ValueDecl decl, Diagnostics.SourceLocation loc) {
        this.decl = decl;
        this.loc = loc;
    }

    public ValueDecl getDecl() {
        return decl;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
