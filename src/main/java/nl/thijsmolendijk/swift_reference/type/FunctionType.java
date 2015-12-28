package nl.thijsmolendijk.swift_reference.type;

import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import nl.thijsmolendijk.swift_reference.util.Pair;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class FunctionType extends Type {
    protected Type in;
    protected Type out;

    private FunctionType(Type in, Type out) {
        this.in = in;
        this.out = out;
        this.canonicalType = in.isCanonical() && out.isCanonical() ? this : null;
    }

    public Type getIn() {
        return in;
    }

    public Type getOut() {
        return out;
    }

    public static FunctionType create(Type in, Type out, ASTContext context) {
        Pair<Type, Type> key = new Pair<>(in, out);
        if (context.getFunctionTypes().containsKey(key)) {
            return context.getFunctionTypes().get(key);
        }

        FunctionType ret = new FunctionType(in, out);
        context.getFunctionTypes().put(key, ret);
        return ret;
    }

    @Override
    public String getName() {
        return in.getName() + " -> " + out.getName();
    }
}
