package nl.thijsmolendijk.swift_reference.ast.stmt;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.Stmt;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class AssignStmt extends Stmt {
    private Expr dest;
    private Diagnostics.SourceLocation eqLoc;
    private Expr value;

    public AssignStmt(Expr dest, Diagnostics.SourceLocation eqLoc, Expr value) {
        this.dest = dest;
        this.eqLoc = eqLoc;
        this.value = value;
    }

    public Expr getDest() {
        return dest;
    }

    public void setDest(Expr dest) {
        this.dest = dest;
    }

    public Expr getValue() {
        return value;
    }

    public void setValue(Expr value) {
        this.value = value;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return dest.getStartingLoc();
    }
}
