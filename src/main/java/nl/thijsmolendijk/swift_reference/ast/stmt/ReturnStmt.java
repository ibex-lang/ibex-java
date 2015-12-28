package nl.thijsmolendijk.swift_reference.ast.stmt;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.Stmt;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class ReturnStmt extends Stmt {
    private Diagnostics.SourceLocation loc;
    private Expr result;

    public ReturnStmt(Diagnostics.SourceLocation loc, Expr result) {
        this.loc = loc;
        this.result = result;
    }

    public Expr getResult() {
        return result;
    }

    public void setResult(Expr result) {
        this.result = result;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
