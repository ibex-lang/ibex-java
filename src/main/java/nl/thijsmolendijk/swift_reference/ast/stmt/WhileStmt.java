package nl.thijsmolendijk.swift_reference.ast.stmt;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.Stmt;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class WhileStmt extends Stmt {
    private Diagnostics.SourceLocation loc;
    private Expr cond;
    private Stmt body;

    public WhileStmt(Diagnostics.SourceLocation loc, Expr cond, Stmt body) {
        this.loc = loc;
        this.cond = cond;
        this.body = body;
    }

    public Expr getCond() {
        return cond;
    }

    public void setCond(Expr cond) {
        this.cond = cond;
    }

    public Stmt getBody() {
        return body;
    }

    public void setBody(Stmt body) {
        this.body = body;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
