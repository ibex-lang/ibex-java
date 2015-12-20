package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.stmt.TranslationUnit;
import nl.thijsmolendijk.ibex.parse.Parser;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class Main {
    public static void main(String... args) {
        Parser p = new Parser("foo.en", "type Foo : (A, A) type Bar : (Foo)", new ASTContext());
        TranslationUnit stmt = p.parseTranslationUnit();
        System.out.println(stmt);
    }
}
