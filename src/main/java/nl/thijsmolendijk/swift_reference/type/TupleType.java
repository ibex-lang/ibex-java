package nl.thijsmolendijk.swift_reference.type;

import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class TupleType extends Type {
    protected TupleElement[] fields;

    private TupleType(TupleElement[] fields, boolean canonical) {
        this.fields = fields;
        this.canonicalType = canonical ? this : null;
    }

    public static TupleType get(TupleElement[] elements, ASTContext context) {
        for (TupleType t : context.getTupleTypes()) {
            if (t.fields.length != elements.length) continue;

            boolean ok = true;
            for (int i = 0; i < elements.length; i++) {
                TupleElement existing = t.fields[i];
                TupleElement proposed = elements[i];

                if (existing.type != proposed.type || existing.name != proposed.name || existing.initializer != proposed.initializer) {
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
    public String getName() {
        return "(" + Stream.of(fields).map(x -> x.name.getValue() + ": " + x.type.getName()).collect(Collectors.joining(", ")) + ")";
    }

    public static final class TupleElement {
        protected Identifier name;
        protected Type type;
        protected Expr initializer;

        public TupleElement(Identifier name, Type type, Expr initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }

        public Identifier getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public Expr getInitializer() {
            return initializer;
        }
    }
}
