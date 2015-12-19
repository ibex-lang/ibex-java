package nl.thijsmolendijk.ibex.type;

import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.util.Pair;

/**
 * Represents a FunctionType. If the function takes multiple args, it is simply a tuple argument.
 *
 * Created by molenzwiebel on 19-12-15.
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
    public Type getCanonicalType(ASTContext inContext) {
        if (canonicalType != null) return canonicalType;

        canonicalType = FunctionType.create(in.getCanonicalType(inContext), out.getCanonicalType(inContext), inContext);
        return canonicalType;
    }

    @Override
    public String getName() {
        return "fn " + in.getName() + " -> " + out.getName();
    }
}
