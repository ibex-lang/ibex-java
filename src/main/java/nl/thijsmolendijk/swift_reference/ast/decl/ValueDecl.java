package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.type.Type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public abstract class ValueDecl extends NamedDecl {
    protected Type type;
    protected Expr init;

    public ValueDecl(Identifier name, Type type, Expr init) {
        super(name);
        this.type = type;
        this.init = init;
    }

    public Expr getInit() {
        return init;
    }

    public void setInit(Expr init) {
        this.init = init;
    }
}
