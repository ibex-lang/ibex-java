package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.stmt.BraceStmt;
import nl.thijsmolendijk.ibex.parse.Parser;
import nl.thijsmolendijk.ibex.scoping.ScopedHashMap;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class Main {
    public static void main(String... args) {
        ScopedHashMap<String, Integer> foo = new ScopedHashMap<>();

        Parser p = new Parser("foo.en", "{ type Foo : (A, A) type Bar : (Foo) }", new ASTContext());
        BraceStmt stmt = p.parseBraceStmt();
        System.out.println(stmt);
    }
}
