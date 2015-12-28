package nl.thijsmolendijk.swift_reference.ast;

import nl.thijsmolendijk.swift_reference.Diagnostics;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public abstract class Stmt extends Node {
    public abstract Diagnostics.SourceLocation getStartingLoc();
}
