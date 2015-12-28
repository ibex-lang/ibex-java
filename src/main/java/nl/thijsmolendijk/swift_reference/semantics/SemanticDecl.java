package nl.thijsmolendijk.swift_reference.semantics;

import nl.thijsmolendijk.swift_reference.Diagnostics.SourceLocation;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.Node;
import nl.thijsmolendijk.swift_reference.ast.decl.*;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.ast.stmt.BraceStmt;
import nl.thijsmolendijk.swift_reference.type.OneOfType;
import nl.thijsmolendijk.swift_reference.type.Type;
import nl.thijsmolendijk.swift_reference.util.Pair;

import java.util.*;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class SemanticDecl extends BaseSemantic {
    private HashMap<Identifier, Pair<Integer, ValueDecl>> values = new HashMap<>();
    private HashMap<Identifier, Pair<Integer, TypeAliasDecl>> types = new HashMap<>();

    private List<TypeAliasDecl> unresolvedTypeList = new ArrayList<>();

    public SemanticDecl(Semantic semantic) {
        super(semantic);
    }

    public ValueDecl lookupValueName(Identifier name) {
        Pair<Integer, ValueDecl> fnd = values.get(name);
        if (fnd == null || fnd.getLeft() == 0) return null;
        return fnd.getRight();
    }

    public TypeAliasDecl lookupTypeName(Identifier name) {
        Pair<Integer, TypeAliasDecl> entry = types.get(name);
        if (entry == null) return null;

        TypeAliasDecl dec = entry.getRight();
        if (dec != null) {
            System.out.println("returning " + dec);
            return dec;
        }

        dec = new TypeAliasDecl(name, null, null);
        unresolvedTypeList.add(dec);

        types.put(name, new Pair<>(0, dec));
        return dec;
    }

    public void handleEndOfTranslationUnit(TranslationUnitDecl result, List<Node> items) {
        result.setBody(new BraceStmt(null, null, items.toArray(new Node[items.size()])));

        if (unresolvedTypeList.isEmpty()) return;

        Iterator<TypeAliasDecl> it = unresolvedTypeList.iterator();
        for (TypeAliasDecl decl = it.next(); it.hasNext(); decl = it.next()) {
            if (decl.getUnderlyingType() != null) {
                it.remove();
            }
        }

        result.setUnresolvedTypes(unresolvedTypeList);
    }

    public VarDecl actOnVarDecl(SourceLocation loc, String varName, Type type, Expr init) {
        if (type == null) {
            type = semantic.context.getDependentType();
        }

        return new VarDecl(semantic.context.getIdentifier(varName), type, init, loc);
    }

    public void addToScope(ValueDecl decl) {
        values.put(decl.getName(), new Pair<>(0, decl));
    }

    public TypeAliasDecl actOnTypeAlias(SourceLocation loc, Identifier name, Type type) {
        Pair<Integer, TypeAliasDecl> existing = types.get(name);

        if (existing == null) {
            TypeAliasDecl dec = new TypeAliasDecl(name, loc, type);
            types.put(name, new Pair<>(0, dec));
            return dec;
        }

        if (existing.getRight().getUnderlyingType() == null) {
            existing.getRight().setUnderlyingType(type);
            return existing.getRight();
        }

        throw new RuntimeException("Redefinition of " + name.getValue());
    }

    public TypeAliasDecl actOnStructDecl(SourceLocation structLoc, Identifier name, Type type) {
        TypeAliasDecl decl = actOnTypeAlias(structLoc, name, null);

        Pair<String, Type> el = new Pair<>(name.getValue(), type);
        OneOfType ty = semantic.type.actOnOneofType(structLoc, Collections.singletonList(el), decl);

        addToScope(ty.getElement(0));
        return decl;
    }
}
