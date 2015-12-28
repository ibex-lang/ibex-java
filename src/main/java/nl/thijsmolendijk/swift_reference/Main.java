package nl.thijsmolendijk.swift_reference;

import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import nl.thijsmolendijk.swift_reference.ast.decl.TranslationUnitDecl;
import nl.thijsmolendijk.swift_reference.parse.Parser;

/**
 * Created by molenzwiebel on 16-12-15.
 */
public class Main {
    public static void main(String... args) {
        Parser p = new Parser("foo.en",
                "typealias integer_literal_type : _int8" +
                "fn foo(a: _int8) -> _int8 { return a }" +
                "let x = 3" +
                "foo(x)", new ASTContext());

        TranslationUnitDecl res = p.parseTranslationUnit();
        System.out.println(res);
    }
}
