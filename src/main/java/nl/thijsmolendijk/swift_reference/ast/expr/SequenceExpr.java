package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class SequenceExpr extends Expr {
    private Expr[] elements;

    public SequenceExpr(Expr[] elements) {
        this.elements = elements;
    }

    public Expr[] getElements() {
        return elements;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return elements[0].getStartingLoc();
    }
}
