package nl.thijsmolendijk.ibex.util;

/**
 * Simple functional interface for a function taking 3 params and returning a value.
 *
 * Created by molenzwiebel on 17-12-15.
 */
@FunctionalInterface
public interface TriFunction<P1, P2, P3, Ret> {
    public Ret call(P1 param1, P2 param2, P3 param3);
}
