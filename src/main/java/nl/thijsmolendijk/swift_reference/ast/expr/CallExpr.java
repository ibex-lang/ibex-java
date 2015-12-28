package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class CallExpr extends Expr {
    private Expr function;
    private Expr arg;

    public CallExpr(Expr function, Expr arg) {
        this.function = function;
        this.arg = arg;
    }

    public Expr getFunction() {
        return function;
    }

    public void setFunction(Expr function) {
        this.function = function;
    }

    public Expr getArg() {
        return arg;
    }

    public void setArg(Expr arg) {
        this.arg = arg;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return arg.getStartingLoc();
    }
}
