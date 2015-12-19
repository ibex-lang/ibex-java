package nl.thijsmolendijk.ibex.type;

import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a tuple type.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class TupleType extends Type {
    protected TupleElement[] fields;

    private TupleType(TupleElement[] fields, boolean canonical) {
        this.fields = fields;
        this.canonicalType = canonical ? this : null;
    }

    /**
     * Creates a new TupleType, or reuses an existing one, with the specified arguments.
     * @param elements the elements of the tuple
     * @param context in the specified context
     * @return the created type
     */
    public static TupleType get(TupleElement[] elements, ASTContext context) {
        // Reuse tuple types first.
        for (TupleType t : context.getTupleTypes()) {
            if (t.fields.length != elements.length) continue;

            boolean ok = true;
            for (int i = 0; i < elements.length; i++) {
                TupleElement existing = t.fields[i];
                TupleElement proposed = elements[i];

                if (existing.type != proposed.type || existing.name != proposed.name) {
                    ok = false;
                }
            }

            if (ok) return t;
        }

        boolean canonical = true;
        for (TupleElement e : elements) {
            canonical &= e.type != null && e.type.isCanonical();
        }

        TupleType ret = new TupleType(elements, canonical);
        context.getTupleTypes().add(ret);
        return ret;
    }

    public Type getElementType(int idx) {
        return fields[idx].type;
    }

    public TupleElement getElement(int idx) {
        return fields[idx];
    }

    public int getElementWithName(Identifier name) {
        for (int i = 0; i < fields.length; i++) {
            if (name == fields[i].name) return i;
        }
        return -1;
    }

    public int size() {
        return fields.length;
    }

    @Override
    public Type getCanonicalType(ASTContext inContext) {
        if (canonicalType != null) return canonicalType;

        List<TupleElement> els = new ArrayList<>();
        for (TupleType.TupleElement el : this.fields) {
            els.add(new TupleType.TupleElement(el.name, el.type.getCanonicalType(inContext)));
        }
        canonicalType = TupleType.get(els.toArray(new TupleType.TupleElement[els.size()]), inContext);
        return canonicalType;
    }

    @Override
    public String getName() {
        return "(" + Stream.of(fields).map(x -> x.name.getValue() + ": " + x.type.getName()).collect(Collectors.joining(", ")) + ")";
    }

    /**
     * Represents an element in a TupleType.
     */
    public static final class TupleElement {
        protected Identifier name;
        protected Type type;

        public TupleElement(Identifier name, Type type) {
            this.name = name;
            this.type = type;
        }

        public Identifier getName() {
            return name;
        }

        public Type getType() {
            return type;
        }
    }
}
