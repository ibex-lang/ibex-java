package nl.thijsmolendijk.ibex.binding;

import nl.thijsmolendijk.ibex.ast.Node;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.ast.stmt.ImportDecl;
import nl.thijsmolendijk.ibex.ast.stmt.TranslationUnit;
import nl.thijsmolendijk.ibex.ast.stmt.TypeDecl;
import nl.thijsmolendijk.ibex.ast.stmt.ValueDecl;

import java.util.HashMap;

/**
 * Created by molenzwiebel on 20-12-15.
 */
public class ImportedModule {
    private TranslationUnit decl;

    private HashMap<Identifier, ValueDecl> toplevelDecls;
    private HashMap<Identifier, TypeDecl> toplevelTypes;

    public ImportedModule(TranslationUnit unit) {
        this.decl = unit;

        this.toplevelDecls = new HashMap<>();
        this.toplevelTypes = new HashMap<>();

        for (Node el : decl.getBody().getElements()) {
            if (el instanceof TypeDecl) toplevelTypes.put(((TypeDecl) el).getName(), (TypeDecl) el);
            if (el instanceof ValueDecl) toplevelDecls.put(((ValueDecl) el).getName(), (ValueDecl) el);
        }
    }

    /**
     * Looks up a specific type in this module
     * @param imprt the import statement that references this module
     * @param name the name of the type to lookup
     * @return the type, or null if not found
     */
    public TypeDecl lookupType(ImportDecl imprt, Identifier name) {
        if (imprt.getPath().size() >= 2) throw new RuntimeException("FIXME: No nesting");
        return toplevelTypes.get(name);
    }

    /**
     * Looks up a specific value declaration in this module
     * @param imprt the import statement that references this module
     * @param name the name of the value declaration to lookup
     * @return the value declaration, or null if not found
     */
    public ValueDecl lookupDecl(ImportDecl imprt, Identifier name) {
        if (imprt.getPath().size() >= 2) throw new RuntimeException("FIXME: No nesting");
        return toplevelDecls.get(name);
    }
}
