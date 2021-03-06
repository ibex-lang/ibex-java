package nl.thijsmolendijk.swift_reference.type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class BuiltinType extends Type {
    private BuiltinKind kind;

    public BuiltinType(BuiltinKind kind) {
        this.kind = kind;
        this.canonicalType = this;
    }

    @Override
    public String getName() {
        return "<builtin: " + kind.name() + ">";
    }

    public enum BuiltinKind {
        INT1,
        INT8,
        INT16,
        INT32,
        INT64;
    }
}
