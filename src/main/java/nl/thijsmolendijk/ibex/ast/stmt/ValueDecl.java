package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.type.Type;

/**
 * Represents a declaration with a name, type and possible initialization.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public abstract class ValueDecl extends NamedDecl {
    protected Type type;
    protected Expression init;

    public ValueDecl(Identifier name, Type type, Expression init) {
        super(name);
        this.type = type;
        this.init = init;
    }

    public Expression getInit() {
        return init;
    }
    public void setInit(Expression init) {
        this.init = init;
    }

    public Type getType() {
        return type;
    }
}
