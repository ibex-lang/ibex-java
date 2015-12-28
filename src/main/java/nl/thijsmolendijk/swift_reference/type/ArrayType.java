package nl.thijsmolendijk.swift_reference.type;

import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import nl.thijsmolendijk.swift_reference.util.Pair;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class ArrayType extends Type {
    protected Type element;
    protected int size; // 0 means unsized, or pointer.

    private ArrayType(Type el, int size) {
        this.element = el;
        this.size = size;
        this.canonicalType = el.isCanonical() ? this : null;
    }

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
    public String getName() {
        return element.getName() + "[" + (size != 0 ? size : "") + "]";
    }
}
