package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class UnresolvedMemberExpr extends Expr {
    private Diagnostics.SourceLocation colonLoc;
    private Diagnostics.SourceLocation nameLoc;
    private Identifier name;

    public UnresolvedMemberExpr(Diagnostics.SourceLocation colonLoc, Diagnostics.SourceLocation nameLoc, Identifier name) {
        this.colonLoc = colonLoc;
        this.nameLoc = nameLoc;
        this.name = name;
    }

    public Diagnostics.SourceLocation getColonLoc() {
        return colonLoc;
    }

    public Diagnostics.SourceLocation getNameLoc() {
        return nameLoc;
    }

    public Identifier getName() {
        return name;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return colonLoc;
    }
}
