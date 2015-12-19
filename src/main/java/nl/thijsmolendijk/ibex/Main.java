package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.parse.Parser;
import nl.thijsmolendijk.ibex.type.Type;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class Main {
    public static void main(String... args) {
        Parser p = new Parser("foo.en", "(foo: Bar, Baz) -> Foobar[]", new ASTContext());
        Type t = p.parseType();
        System.out.println(t);
    }
}
