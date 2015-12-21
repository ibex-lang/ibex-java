package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.stmt.TranslationUnit;
import nl.thijsmolendijk.ibex.binding.NameBinder;
import nl.thijsmolendijk.ibex.parse.Parser;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class Main {
    public static void main(String... args) {
        ASTContext ctx = new ASTContext();

        Parser p = new Parser("foo.en",
                "type Int : () \n" +
                "10 -> foo \n" +
                "fn foo(a: Int) { }", ctx);
        TranslationUnit stmt = p.parseTranslationUnit();

        NameBinder binder = new NameBinder(ctx);
        binder.performBinding(stmt);

        System.out.println(stmt);
    }
}
