package nl.thijsmolendijk.swift_reference;

import nl.thijsmolendijk.swift_reference.ast.*;
import nl.thijsmolendijk.swift_reference.ast.decl.TranslationUnitDecl;
import nl.thijsmolendijk.swift_reference.ast.decl.TypeAliasDecl;
import nl.thijsmolendijk.swift_reference.ast.decl.ValueDecl;
import nl.thijsmolendijk.swift_reference.ast.expr.RefExpr;
import nl.thijsmolendijk.swift_reference.ast.expr.UnresolvedRefExpr;

/**
 * Created by molenzwiebel on 18-12-15.
 */
public class NameBinding {
    public static void performNameBinding(TranslationUnitDecl tud, ASTContext ctx) {
        NameBinding binder = new NameBinding();

        for (Node n : tud.getBody().getElements()) {
            if (n instanceof ValueDecl && !((ValueDecl) n).getName().getValue().isEmpty()) {
                binder.addNamedTopLevel((ValueDecl) n);
            }
        }

        for (TypeAliasDecl decl : tud.getUnresolvedTypes()) {
            TypeAliasDecl res = binder.lookupTypeName(decl.getName());
            if (res != null) {
                decl.setUnderlyingType(res.getUnderlyingType());
            } else {
                throw new RuntimeException("Use of undeclared type");
            }
        }

        for (Node n : tud.getBody().getElements()) {
            if (n instanceof ValueDecl && ((ValueDecl) n).getInit() != null) {
                ((ValueDecl) n).getInit().walkExpr(NameBinding::bindNames, null, binder);
            } else if (n instanceof Stmt) {
                new ExprWalker(NameBinding::bindNames, null, binder).run((Stmt) n);
            } else {
                ((Expr) n).walkExpr(NameBinding::bindNames, null, binder);
            }
        }
    }

    private static Expr bindNames(Expr ex, Expr.WalkOrder order, Object extra) {
        NameBinding binder = (NameBinding) extra;
        if (order == Expr.WalkOrder.PRE) return ex;

        if (ex instanceof UnresolvedRefExpr) {
            ValueDecl decl = binder.bindValueName(((UnresolvedRefExpr) ex).getName());
            if (decl == null) throw new RuntimeException("Unresolved identifier");
            return new RefExpr(decl, ex.getStartingLoc());
        }

        return ex;
    }
}
