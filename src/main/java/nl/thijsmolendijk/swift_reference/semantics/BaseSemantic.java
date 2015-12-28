package nl.thijsmolendijk.swift_reference.semantics;

import nl.thijsmolendijk.swift_reference.Diagnostics;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public abstract class BaseSemantic {
    protected Semantic semantic;

    public BaseSemantic(Semantic semantic) {
        this.semantic = semantic;
    }

    protected void note(Diagnostics.SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.NOTE, message, loc);
    }

    protected void warning(Diagnostics.SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.WARNING, message, loc);
    }

    protected void error(Diagnostics.SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, message, loc);
    }
}
