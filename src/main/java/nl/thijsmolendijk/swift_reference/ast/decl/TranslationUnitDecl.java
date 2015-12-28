package nl.thijsmolendijk.swift_reference.ast.decl;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import nl.thijsmolendijk.swift_reference.ast.stmt.BraceStmt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class TranslationUnitDecl extends Decl {
    private ASTContext context;
    private BraceStmt body;
    private List<TypeAliasDecl> unresolvedTypes = new ArrayList<>();

    public TranslationUnitDecl(ASTContext context) {
        this.context = context;
    }

    public BraceStmt getBody() {
        return body;
    }

    public void setBody(BraceStmt body) {
        this.body = body;
    }

    public List<TypeAliasDecl> getUnresolvedTypes() {
        return unresolvedTypes;
    }

    public void setUnresolvedTypes(List<TypeAliasDecl> unresolvedTypes) {
        this.unresolvedTypes = unresolvedTypes;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return body.getStartingLoc();
    }
}
