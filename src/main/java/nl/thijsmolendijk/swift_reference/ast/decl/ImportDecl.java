package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.util.Pair;

import java.util.List;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class ImportDecl extends Decl {
    private Diagnostics.SourceLocation loc;
    private List<Pair<Identifier, Diagnostics.SourceLocation>> path;

    public ImportDecl(Diagnostics.SourceLocation loc, List<Pair<Identifier, Diagnostics.SourceLocation>> path) {
        this.loc = loc;
        this.path = path;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
