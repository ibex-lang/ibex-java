package nl.thijsmolendijk.swift_reference.semantics;

import nl.thijsmolendijk.swift_reference.ast.ASTContext;

/**
 * Base semantic collection.
 * Created by molenzwiebel on 17-12-15.
 */
public class Semantic {
    public ASTContext context;

    public SemanticDecl decl;
    public SemanticExpr expr;
    public SemanticType type;

    public Semantic(ASTContext context) {
        this.context = context;
        this.decl = new SemanticDecl(this);
        this.expr = new SemanticExpr(this);
        this.type = new SemanticType(this);
    }
}
