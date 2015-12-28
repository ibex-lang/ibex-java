package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.type.Type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class OneOfDecl {
    public static final class OneOfElementDecl extends ValueDecl {
        private Diagnostics.SourceLocation loc;
        private Type argumentType;

        public OneOfElementDecl(Identifier name, Type type, Diagnostics.SourceLocation loc, Type argumentType) {
            super(name, type, null);
            this.loc = loc;
            this.argumentType = argumentType;
        }

        public Type getArgumentType() {
            return argumentType;
        }

        @Override
        public Diagnostics.SourceLocation getStartingLoc() {
            return loc;
        }
    }
}
