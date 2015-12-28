package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class BinaryExpr extends Expr {
    private Expr left;
    private Expr fun;
    private Expr right;

    public BinaryExpr(Expr left, Expr fun, Expr right) {
        this.left = left;
        this.fun = fun;
        this.right = right;
    }

    public Expr getLeft() {
        return left;
    }

    public void setLeft(Expr left) {
        this.left = left;
    }

    public Expr getRight() {
        return right;
    }

    public void setRight(Expr right) {
        this.right = right;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return left.getStartingLoc();
    }
}
