package nl.thijsmolendijk.ibex.ast;

import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Top class of all AST nodes.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public abstract class Node {
    public abstract SourceLocation getLocation();
}
