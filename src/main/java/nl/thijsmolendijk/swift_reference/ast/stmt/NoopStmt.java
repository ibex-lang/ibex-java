package nl.thijsmolendijk.swift_reference.ast.stmt;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Stmt;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class NoopStmt extends Stmt {
    private Diagnostics.SourceLocation loc;

    public NoopStmt(Diagnostics.SourceLocation loc) {
        this.loc = loc;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
