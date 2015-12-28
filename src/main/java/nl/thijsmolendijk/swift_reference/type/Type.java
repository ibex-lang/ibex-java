package nl.thijsmolendijk.swift_reference.type;

import nl.thijsmolendijk.swift_reference.ast.ASTContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a type in Ibex.
 *
 * Created by molenzwiebel on 17-12-15.
 */
public abstract class Type {
    // The canonical, or unique form of this type.
    // This is for example a tuple without the names.
    // As defined by wikipedia: data that has been canonicalized into a completely unique representation, from a previous form that had more than one possible representation
    protected Type canonicalType;

    /**
     * Creates a new type with no canonical type.
     */
    public Type() {
        this(null);
    }

    /**
     * Creates a new type with the specified canonical type.
     * @param canonicalType the canonical type
     */
    public Type(Type canonicalType) {
        this.canonicalType = canonicalType;
    }

    /**
     * @return if this type is equal to its canonical representation
     */
    public boolean isCanonical() {
        return canonicalType == this;
    }

    /**
     * @return if a canonical representation has been computed
     */
    public boolean hasCanonical() {
        return canonicalType != null;
    }

    /**
     * @return the canonical representation in the provided context
     */
    public Type getCanonicalType(ASTContext inContext) {
        if (canonicalType != null) return canonicalType;

        if (this instanceof NameAliasType) {
            return getDesugaredType().getCanonicalType(inContext);
        } else if (this instanceof TupleType) {
            TupleType tupleType = (TupleType) this;
            List<TupleType.TupleElement> els = new ArrayList<>();
            for (TupleType.TupleElement el : tupleType.fields) {
                els.add(new TupleType.TupleElement(el.name, el.type.getCanonicalType(inContext), null));
            }
            return TupleType.get(els.toArray(new TupleType.TupleElement[els.size()]), inContext);
        } else if (this instanceof FunctionType) {
            FunctionType funType = (FunctionType) this;
            return FunctionType.create(funType.in.getCanonicalType(inContext), funType.out.getCanonicalType(inContext), inContext);
        } else if (this instanceof ArrayType) {
            ArrayType arrType = (ArrayType) this;
            return ArrayType.create(arrType.element.getCanonicalType(inContext), arrType.size, inContext);
        }

        assert false : "Unimplemented getCanonicalType?";
        return null;
    }

    /**
     * @return the type without any "syntax sugar", or the raw type
     */
    public Type getDesugaredType() {
        if (this instanceof NameAliasType) {
            return ((NameAliasType) this).node.getUnderlyingType();
        }
        return this;
    }

    /**
     * @return the name of this type
     * @apiNote <b>This should only be used for diagnostics.</b>
     */
    public abstract String getName();
}
