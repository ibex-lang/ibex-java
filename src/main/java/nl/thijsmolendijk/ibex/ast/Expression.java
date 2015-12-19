package nl.thijsmolendijk.ibex.ast;

import nl.thijsmolendijk.ibex.type.Type;

/**
 * Basic expression superclass.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public abstract class Expression extends Node {
    protected Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
