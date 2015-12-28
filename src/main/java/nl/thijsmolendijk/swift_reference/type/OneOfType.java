package nl.thijsmolendijk.swift_reference.type;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.ast.decl.OneOfDecl;

import java.util.List;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class OneOfType extends Type {
    private Diagnostics.SourceLocation location;
    private List<OneOfDecl.OneOfElementDecl> elements;

    private OneOfType(Diagnostics.SourceLocation location, List<OneOfDecl.OneOfElementDecl> elements) {
        this.location = location;
        this.elements = elements;
        this.canonicalType = this;
    }

    public static OneOfType create(Diagnostics.SourceLocation loc, List<OneOfDecl.OneOfElementDecl> elements, ASTContext context) {
        return new OneOfType(loc, elements);
    }

    public OneOfDecl.OneOfElementDecl getElement(Identifier name) {
        return elements.stream().filter(x -> x.getName() == name).findFirst().orElse(null);
    }

    public OneOfDecl.OneOfElementDecl getElement(int idx) {
        return elements.get(idx);
    }

    public boolean hasSingleElement() {
        return elements.size() == 0 && elements.get(0).getArgumentType() != null;
    }

    @Override
    public String getName() {
        // TODO
        return "oneof <<TODO>>";
    }
}
