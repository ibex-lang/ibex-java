package nl.thijsmolendijk.swift_reference.type;

import nl.thijsmolendijk.swift_reference.ast.decl.TypeAliasDecl;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class NameAliasType extends Type {
    protected TypeAliasDecl node;

    public NameAliasType(TypeAliasDecl node) {
        this.node = node;
    }

    @Override
    public String getName() {
        //TODO
        return "<alias of " + node.getUnderlyingType().getName() + ">";
    }
}
