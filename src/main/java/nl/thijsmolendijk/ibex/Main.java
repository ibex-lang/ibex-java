package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.stmt.BraceStmt;
import nl.thijsmolendijk.ibex.parse.Parser;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class Main {
    public static void main(String... args) {
        Parser p = new Parser("foo.en", "{ type Foo : (a: Int1, b: Int8) }", new ASTContext());
        BraceStmt stmt = p.parseBraceStmt();
        System.out.println(stmt);
    }
}
