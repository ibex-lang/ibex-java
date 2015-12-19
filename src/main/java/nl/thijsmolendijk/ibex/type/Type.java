package nl.thijsmolendijk.ibex.type;

import nl.thijsmolendijk.ibex.ASTContext;

/**
 * Basic Type superclass.
 *
 * Created by molenzwiebel on 19-12-15.
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
     * @return the canonical representation of this type in the specified context.
     */
    public Type getCanonicalType(ASTContext inContext) {
        if (canonicalType != null) return canonicalType;
        throw new IllegalStateException("getCanonicalType not overridden and not set?");
    }

    /**
     * @return the type without any "syntax sugar", or the raw type
     */
    public Type getDesugaredType() {
        return this;
    }

    /**
     * @return the name of this type
     * @apiNote <b>This should only be used for diagnostics.</b>
     */
    public abstract String getName();
}
