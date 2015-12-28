package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.type.Type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class ArgDecl extends ValueDecl {
    private Diagnostics.SourceLocation funLoc;

    public ArgDecl(Identifier name, Type type, Diagnostics.SourceLocation funLoc) {
        super(name, type, null);
        this.funLoc = funLoc;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return funLoc;
    }
}
