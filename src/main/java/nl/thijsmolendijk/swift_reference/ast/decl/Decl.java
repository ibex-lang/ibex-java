package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Node;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public abstract class Decl extends Node {
    public abstract Diagnostics.SourceLocation getStartingLoc();
}
