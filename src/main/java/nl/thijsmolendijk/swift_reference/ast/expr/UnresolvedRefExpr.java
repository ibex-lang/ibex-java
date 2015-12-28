package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class UnresolvedRefExpr extends Expr {
    private Identifier name;
    private Diagnostics.SourceLocation loc;

    public UnresolvedRefExpr(Identifier name, Diagnostics.SourceLocation loc) {
        this.name = name;
        this.loc = loc;
    }

    public Identifier getName() {
        return name;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
