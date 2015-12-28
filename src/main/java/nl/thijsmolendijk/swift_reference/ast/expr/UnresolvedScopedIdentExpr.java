package nl.thijsmolendijk.swift_reference.ast.expr;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.decl.TypeAliasDecl;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class UnresolvedScopedIdentExpr extends Expr {
    private TypeAliasDecl typeDecl;
    private Diagnostics.SourceLocation declLoc, colonLoc, nameLoc;
    private Identifier name;

    public UnresolvedScopedIdentExpr(TypeAliasDecl typeDecl, Diagnostics.SourceLocation declLoc, Diagnostics.SourceLocation colonLoc, Diagnostics.SourceLocation nameLoc, Identifier name) {
        this.typeDecl = typeDecl;
        this.declLoc = declLoc;
        this.colonLoc = colonLoc;
        this.nameLoc = nameLoc;
        this.name = name;
    }

    public TypeAliasDecl getTypeDecl() {
        return typeDecl;
    }

    public Diagnostics.SourceLocation getDeclLoc() {
        return declLoc;
    }

    public Diagnostics.SourceLocation getColonLoc() {
        return colonLoc;
    }

    public Diagnostics.SourceLocation getNameLoc() {
        return nameLoc;
    }

    public Identifier getName() {
        return name;
    }

    @Override
    public Diagnostics.SourceLocation getStartingLoc() {
        return declLoc;
    }
}
