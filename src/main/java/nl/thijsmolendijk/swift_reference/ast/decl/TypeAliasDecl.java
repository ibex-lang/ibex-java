package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.type.NameAliasType;
import nl.thijsmolendijk.swift_reference.type.Type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class TypeAliasDecl extends NamedDecl {
    private Diagnostics.SourceLocation loc;
    private Type underlyingType;
    private NameAliasType aliasType;

    public TypeAliasDecl(Identifier name, Diagnostics.SourceLocation loc, Type underlyingType) {
        super(name);
        this.loc = loc;
        this.underlyingType = underlyingType;
    }

    public NameAliasType getAliasType() {
        if (aliasType == null) {
            aliasType = new NameAliasType(this);
        }
        return aliasType;
    }

    public Type getUnderlyingType() {
        return underlyingType;
    }

    public void setUnderlyingType(Type underlyingType) {
        this.underlyingType = underlyingType;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return loc;
    }
}
