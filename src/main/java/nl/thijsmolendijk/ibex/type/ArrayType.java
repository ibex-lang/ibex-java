package nl.thijsmolendijk.ibex.type;

import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.util.Pair;

/**
 * Represents a simple array type.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class ArrayType extends Type {
    protected Type element;
    protected int size; // 0 means unsized, or pointer.

    private ArrayType(Type el, int size) {
        this.element = el;
        this.size = size;
        this.canonicalType = el.isCanonical() ? this : null;
    }

    /**
     * Creates or reuses an ArrayType with the specified arguments.
     * @param el the element type
     * @param size the size of the array
     * @param context the context in which the type should be created
     * @return the type
     */
    public static ArrayType create(Type el, int size, ASTContext context) {
        Pair<Type, Integer> entryKey = new Pair<>(el, size);
        if (context.getArrayTypes().containsKey(entryKey)) {
            return context.getArrayTypes().get(entryKey);
        }

        ArrayType ret = new ArrayType(el, size);
        context.getArrayTypes().put(entryKey, ret);
        return ret;
    }

    @Override
    public Type getCanonicalType(ASTContext inContext) {
        if (canonicalType != null) return canonicalType;

        canonicalType = ArrayType.create(element.getCanonicalType(inContext), size, inContext);
        return canonicalType;
    }

    @Override
    public String getName() {
        return element.getName() + "[" + (size != 0 ? size : "") + "]";
    }
}
