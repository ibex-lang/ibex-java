package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.stmt.ArgDecl;
import nl.thijsmolendijk.ibex.ast.stmt.BraceStmt;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.type.Type;

/**
 * Doubles as a closure expression and the init of a FnDecl.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class FuncExpr extends Expression {
    private SourceLocation funLoc;
    private ArgDecl[] args;
    private BraceStmt body;

    public FuncExpr(Type funType, SourceLocation funLoc, ArgDecl[] args, BraceStmt body) {
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
    public SourceLocation getLocation() {
        return funLoc;
    }
}
