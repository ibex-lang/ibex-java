package nl.thijsmolendijk.swift_reference.ast.stmt;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.Stmt;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class IfStmt extends Stmt {
    private Diagnostics.SourceLocation ifLoc, elseLoc;
    private Expr cond;
    private Stmt ifThen;
    private Stmt ifElse;

    public IfStmt(Diagnostics.SourceLocation ifLoc, Diagnostics.SourceLocation elseLoc, Expr cond, Stmt ifThen, Stmt ifElse) {
        this.ifLoc = ifLoc;
        this.elseLoc = elseLoc;
        this.cond = cond;
        this.ifThen = ifThen;
        this.ifElse = ifElse;
    }

    public Expr getCond() {
        return cond;
    }

    public void setCond(Expr cond) {
        this.cond = cond;
    }

    public Stmt getIfThen() {
        return ifThen;
    }

    public void setIfThen(Stmt ifThen) {
        this.ifThen = ifThen;
    }

    public Stmt getIfElse() {
        return ifElse;
    }

    public void setIfElse(Stmt ifElse) {
        this.ifElse = ifElse;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return ifLoc;
    }
}
