package nl.thijsmolendijk.ibex.binding;

import com.google.common.io.Files;
import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.ast.ASTWalker;
import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.Node;
import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.ast.expr.DeclRefExpr;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.ast.expr.UnresolvedDotExpr;
import nl.thijsmolendijk.ibex.ast.expr.UnresolvedRefExpr;
import nl.thijsmolendijk.ibex.ast.stmt.ImportDecl;
import nl.thijsmolendijk.ibex.ast.stmt.TranslationUnit;
import nl.thijsmolendijk.ibex.ast.stmt.TypeDecl;
import nl.thijsmolendijk.ibex.ast.stmt.ValueDecl;
import nl.thijsmolendijk.ibex.parse.Diagnostics;
import nl.thijsmolendijk.ibex.parse.Parser;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.util.Pair;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * After parsing, this tries to bind any still unresolved names which were caused by imports.
 *
 * Created by molenzwiebel on 21-12-15.
 */
public class NameBinder {
    private ASTContext context;

    private HashMap<ImportDecl, ImportedModule> imports = new HashMap<>();
    private HashMap<Identifier, ValueDecl> toplevelDecls = new HashMap<>();

    public NameBinder(ASTContext context) {
        this.context = context;
    }

    /**
     * Does the actual binding on the provided TranslationUnit
     */
    public void performBinding(TranslationUnit unit) {
        // Preprocess all declarations for forward declarations.
        for (Node n : unit.getBody().getElements()) {
            if (n instanceof ValueDecl) {
                toplevelDecls.put(((ValueDecl) n).getName(), (ValueDecl) n);
            }

            if (n instanceof ImportDecl) {
                addImport((ImportDecl) n);
            }
        }

        // Check to see if any unresolved types were found in includes.
        for (TypeDecl unresolved : unit.getUnresolvedTypesAfterParsing()) {
            if (lookupType(unresolved.getName()) != null) {
                unresolved.setUnderlyingType(lookupType(unresolved.getName()).getUnderlyingType());
            } else {
                errorAndExit(unresolved.getLocation(), "use of undeclared type '" + unresolved.getName().getValue() + "'");
            }
        }

        // Finally, recursively resolve expressions.
        for (Node n : unit.getBody().getElements()) {
            if (n instanceof ValueDecl && ((ValueDecl) n).getInit() != null) {
                new ASTWalker<>(NameBinder::bindNames, null, this).run(((ValueDecl) n).getInit());
            }

            if (n instanceof Statement) {
                new ASTWalker<>(NameBinder::bindNames, null, this).run((Statement) n);
            }

            if (n instanceof Expression) {
                new ASTWalker<>(NameBinder::bindNames, null, this).run((Expression) n);
            }
        }
    }

    private static Expression bindNames(Expression expr, ASTWalker.WalkOrder order, NameBinder data) {
        if (order == ASTWalker.WalkOrder.PRE) return expr;

        if (expr instanceof UnresolvedRefExpr) {
            ValueDecl decl = data.lookupValue(((UnresolvedRefExpr) expr).getName());
            if (decl != null) {
                return new DeclRefExpr(decl, expr.getLocation());
            }
            errorAndExit(expr.getLocation(), "unresolved identifier '" + ((UnresolvedRefExpr) expr).getName().getValue() + "'");
        }

        // Because we can translate foo.bar to bar(foo), it doesnt matter if we don't find a match here.
        // It can still be a tuple element access, which gets resulved later.
        if (expr instanceof UnresolvedDotExpr) {
            ValueDecl decl = data.lookupValue(((UnresolvedDotExpr) expr).getName());
            if (decl != null) {
                ((UnresolvedDotExpr) expr).setResolvedDecl(decl);
            }
        }

        // We don't need to bind this.
        return expr;
    }

    /**
     * Adds the specified import to this NameBinder instance to allow looking it up later.
     */
    public void addImport(ImportDecl decl) {
        ImportedModule module = getModule(decl.getPath().get(0));
        imports.put(decl, module);
    }

    /**
     * Looks up the specified type in all imported modules.
     */
    public TypeDecl lookupType(Identifier name) {
        for (Map.Entry<ImportDecl, ImportedModule> mod : imports.entrySet()) {
            if (mod.getValue().lookupType(mod.getKey(), name) != null) {
                return mod.getValue().lookupType(mod.getKey(), name);
            }
        }

        return null;
    }

    /**
     * Looks up the specified declaration in both this file and all imported modules.
     */
    public ValueDecl lookupValue(Identifier name) {
        // Look in our file first in case we had forward references.
        if (toplevelDecls.containsKey(name)) {
            return toplevelDecls.get(name);
        }

        for (Map.Entry<ImportDecl, ImportedModule> mod : imports.entrySet()) {
            if (mod.getValue().lookupDecl(mod.getKey(), name) != null) {
                return mod.getValue().lookupDecl(mod.getKey(), name);
            }
        }

        return null;
    }

    /**
     * Loads the specified file and parses it, then adds it to the loaded modules.
     */
    private ImportedModule getModule(Pair<Identifier, SourceLocation> path) {
        try {
            File f = new File(path.getLeft().getValue() + ".en");
            String contents = Files.readLines(f, Charset.defaultCharset()).stream().collect(Collectors.joining("\n"));

            TranslationUnit unit = new Parser(f.getName(), contents, context).parseTranslationUnit();
            return new ImportedModule(unit);
        } catch (Exception ex) {
            errorAndExit(path.getRight(), "reading file '" + path.getLeft().getValue() + "'");
            return null;
        }
    }

    private static void errorAndExit(SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, message, loc);
        System.err.flush();
        System.exit(1);
    }
}
