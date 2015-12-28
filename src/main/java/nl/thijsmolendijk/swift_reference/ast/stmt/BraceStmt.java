package nl.thijsmolendijk.swift_reference.ast.stmt;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Node;
import nl.thijsmolendijk.swift_reference.ast.Stmt;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class BraceStmt extends Stmt {
    private Diagnostics.SourceLocation lbLoc, rbLoc;
    private Node[] elements;

    public BraceStmt(Diagnostics.SourceLocation lbLoc, Diagnostics.SourceLocation rbLoc, Node[] elements) {
        this.lbLoc = lbLoc;
        this.rbLoc = rbLoc;
        this.elements = elements;
    }

    public Node[] getElements() {
        return elements;
    }

    public void setElements(Node[] elements) {
        this.elements = elements;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return lbLoc;
    }
}
