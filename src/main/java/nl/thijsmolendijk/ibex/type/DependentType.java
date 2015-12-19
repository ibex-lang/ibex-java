package nl.thijsmolendijk.ibex.type;

/**
 * Signals that the type of the expression depends on some other type in the context.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class DependentType extends Type {
    @Override
    public String getName() {
        return "<<dependent type>>";
    }
}
