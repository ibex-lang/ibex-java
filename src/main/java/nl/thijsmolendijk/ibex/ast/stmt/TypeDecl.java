package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.type.NameAliasType;
import nl.thijsmolendijk.ibex.type.Type;

/**
 * Represents a 'type' node.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class TypeDecl extends NamedDecl {
    private SourceLocation loc;
    private Type underlyingType;
    private NameAliasType aliasType;

    public TypeDecl(Identifier name, SourceLocation loc, Type underlyingType) {
        super(name);
        this.loc = loc;
        this.underlyingType = underlyingType;
    }

    public Type getUnderlyingType() {
        return underlyingType;
    }

    public void setUnderlyingType(Type underlyingType) {
        this.underlyingType = underlyingType;
    }

    public NameAliasType getAliasType() {
        if (aliasType == null) {
            aliasType = new NameAliasType(this);
        }
        return aliasType;
    }

    @Override
    public SourceLocation getLocation() {
        return loc;
    }
}
