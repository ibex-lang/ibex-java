package nl.thijsmolendijk.ibex.type;

import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.ast.TypeDecl;

/**
 * Represents an 'aliased' type, as created with the type keyword.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class NameAliasType extends Type {
    private TypeDecl node;

    public NameAliasType(TypeDecl node) {
        this.node = node;
    }

    @Override
    public Type getDesugaredType() {
        return node.getUnderlyingType();
    }

    @Override
    public Type getCanonicalType(ASTContext inContext) {
        if (canonicalType != null) return canonicalType;

        canonicalType = getDesugaredType().getCanonicalType(inContext);
        return canonicalType;
    }

    @Override
    public String getName() {
        //TODO
        return "<alias of " + node.getUnderlyingType().getName() + ">";
    }
}
