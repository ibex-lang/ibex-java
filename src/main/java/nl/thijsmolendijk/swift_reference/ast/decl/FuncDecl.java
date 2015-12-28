package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.type.Type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class FuncDecl extends ValueDecl {
    private Diagnostics.SourceLocation loc;

    public FuncDecl(Identifier name, Type type, Expr init, Diagnostics.SourceLocation loc) {
        super(name, type, init);
        this.loc = loc;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
