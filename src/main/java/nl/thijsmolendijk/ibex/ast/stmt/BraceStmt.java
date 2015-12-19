package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.Node;
import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents a "body" between braces.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class BraceStmt extends Statement {
    private SourceLocation lbLoc, rbLoc;
    private Node[] elements;

    public BraceStmt(SourceLocation lbLoc, SourceLocation rbLoc, Node[] elements) {
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
    public SourceLocation getLocation() {
        return lbLoc;
    }
}
