package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

import java.util.List;

/**
 * Represents a translation unit, e.g. a single file.
 *
 * Created by molenzwiebel on 20-12-15.
 */
public class TranslationUnit extends Statement {
    private ASTContext context;
    private BraceStmt body;

    private List<TypeDecl> unresolvedTypesAfterParsing;

    public TranslationUnit(ASTContext context) {
        this.context = context;
    }

    public BraceStmt getBody() {
        return body;
    }

    public void setBody(BraceStmt body) {
        this.body = body;
    }

    public List<TypeDecl> getUnresolvedTypesAfterParsing() {
        return unresolvedTypesAfterParsing;
    }

    public void setUnresolvedTypesAfterParsing(List<TypeDecl> unresolvedTypesAfterParsing) {
        this.unresolvedTypesAfterParsing = unresolvedTypesAfterParsing;
    }

    @Override
    public SourceLocation getLocation() {
        return body != null ? body.getLocation() : null;
    }
}
