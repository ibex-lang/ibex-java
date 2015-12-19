package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.parse.Parser;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class Main {
    public static void main(String... args) {
        Parser p = new Parser("foo.en", "(.foo = 1, .bar = 42) -> computeStuff", new ASTContext());
        Expression e = p.parseExpr();
        System.out.println(e);
    }
}
