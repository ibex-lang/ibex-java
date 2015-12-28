package nl.thijsmolendijk.swift_reference.type;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class DependentType extends Type {
    public DependentType() {
        this.canonicalType = this;
    }

    @Override
    public String getName() {
        return "<<dependent type>>";
    }
}
