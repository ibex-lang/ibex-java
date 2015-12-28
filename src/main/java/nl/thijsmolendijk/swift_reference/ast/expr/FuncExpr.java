package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.decl.ArgDecl;
import nl.thijsmolendijk.swift_reference.ast.stmt.BraceStmt;
import nl.thijsmolendijk.swift_reference.type.Type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class FuncExpr extends Expr {
    private Diagnostics.SourceLocation funLoc;
    private ArgDecl[] args;
    private BraceStmt body;

    public FuncExpr(Type funType, Diagnostics.SourceLocation funLoc, ArgDecl[] args, BraceStmt body) {
        this.funLoc = funLoc;
        this.args = args;
        this.body = body;
        this.type = funType;
    }

    public ArgDecl[] getArgs() {
        return args;
    }

    public void setArgs(ArgDecl[] args) {
        this.args = args;
    }

    public BraceStmt getBody() {
        return body;
    }

    public void setBody(BraceStmt body) {
        this.body = body;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return funLoc;
    }
}
