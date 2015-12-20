package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.Node;
import nl.thijsmolendijk.ibex.ast.expr.*;
import nl.thijsmolendijk.ibex.ast.stmt.*;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.scoping.Scope;
import nl.thijsmolendijk.ibex.scoping.ScopedHashMap;
import nl.thijsmolendijk.ibex.type.ArrayType;
import nl.thijsmolendijk.ibex.type.FunctionType;
import nl.thijsmolendijk.ibex.type.TupleType;
import nl.thijsmolendijk.ibex.type.Type;
import nl.thijsmolendijk.ibex.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages scope, complex expression construction and preliminary type lookup.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class Semantic {
    private ASTContext context;

    public ScopedHashMap<Identifier, Pair<Integer, ValueDecl>> valueScope = new ScopedHashMap<>();
    public ScopedHashMap<Identifier, Pair<Integer, TypeDecl>> typeScope = new ScopedHashMap<>();

    private List<TypeDecl> unresolvedTypes = new ArrayList<>();

    public Scope scope = new Scope(this);

    public Semantic(ASTContext context) {
        this.context = context;
    }

    /* ========================================================
     *                      Expressions
     * ======================================================== */

    public Expression handleIdentifier(SourceLocation loc, Identifier ident) {
        ValueDecl decl = lookupValue(ident);
        if (decl == null) {
            return new UnresolvedRefExpr(loc, ident);
        }
        return new DeclRefExpr(decl, loc);
    }

    public IntegerLiteralExpr handleInteger(SourceLocation loc, String val) {
        IntegerLiteralExpr ret = new IntegerLiteralExpr(loc, val);
        ret.setType(context.getInt32());
        return ret;
    }

    public TupleExpr handleTupleExpr(SourceLocation lbrace, List<Pair<Expression, Identifier>> elements, SourceLocation rbrace) {
        Expression[] exprs = new Expression[elements.size()];
        Identifier[] names = new Identifier[elements.size()];

        for (int i = 0; i < elements.size(); i++) {
            exprs[i] = elements.get(i).getLeft();
            names[i] = elements.get(i).getRight();
        }

        return new TupleExpr(lbrace, rbrace, exprs, names);
    }

    public FuncExpr handleFunc(SourceLocation start, FunctionType funType) {
        TupleType inArgs = (TupleType) funType.getIn();
        ArgDecl[] args = new ArgDecl[inArgs.size()];

        for (int i = 0; i < inArgs.size(); i++) {
            TupleType.TupleElement el = inArgs.getElement(i);
            args[i] = new ArgDecl(el.getName(), el.getType(), start);
            addToScope(args[i]);
        }

        return new FuncExpr(funType, start, args, null);
    }

    public void handleEndOfUnit(TranslationUnit result, SourceLocation start, SourceLocation end, List<Node> contents) {
        result.setBody(new BraceStmt(start, end, contents.toArray(new Node[contents.size()])));

        Iterator<TypeDecl> it = unresolvedTypes.iterator();
        for (TypeDecl decl = it.next(); it.hasNext(); decl = it.next()) {
            if (decl.getUnderlyingType() != null) {
                it.remove();
            }
        }

        result.setUnresolvedTypesAfterParsing(unresolvedTypes);
    }

    /* ========================================================
     *                      Type Constructing
     * ======================================================== */

    public Type handleTypeName(SourceLocation loc, Identifier name) {
        return lookupType(name, loc).getAliasType();
    }

    public FunctionType handleFunctionType(Type in, SourceLocation arrowLocation, Type out) {
        return FunctionType.create(in, out, context);
    }

    public ArrayType handleArrayType(Type element, Expression size) {
        return ArrayType.create(element, size == null ? 0 : Integer.parseInt(((IntegerLiteralExpr) size).getVal()), context);
    }

    public TupleType handleTupleType(SourceLocation begin, List<TupleType.TupleElement> elements, SourceLocation end) {
        return TupleType.get(elements.toArray(new TupleType.TupleElement[elements.size()]), context);
    }

    /* ========================================================
     *                      Decls and lookup
     * ======================================================== */

    public ValueDecl lookupValue(Identifier name) {
        Pair<Integer, ValueDecl> entry = valueScope.get(name);
        if (entry == null) return null;
        return entry.getRight();
    }

    public TypeDecl lookupType(Identifier name, SourceLocation loc) {
        Pair<Integer, TypeDecl> entry = typeScope.get(name);
        if (entry != null) {
            return entry.getRight();
        }

        TypeDecl decl = new TypeDecl(name, loc, null);
        unresolvedTypes.add(decl);
        // We have not resolved this, so put it at top level.
        // Otherwise, references to the same type in different scopes would create new empty decls.
        typeScope.putInScope(0, name, new Pair<>(0, decl));

        return decl;
    }

    public void addToScope(ValueDecl decl) {
        Pair<Integer, ValueDecl> existing = valueScope.get(decl.getName());
        if (existing != null && existing.getLeft() == scope.getDepth()) {
            throw new RuntimeException("Redefinition of " + decl.getName().getValue());
        }

        valueScope.put(decl.getName(), new Pair<>(scope.getDepth(), decl));
    }

    public VarDecl handleVarDecl(SourceLocation loc, Identifier name, Type givenType, Expression init) {
        if (givenType == null) {
            givenType = context.getDependentType();
        }
        return new VarDecl(name, givenType, init, loc);
    }

    public TypeDecl handleTypeDecl(SourceLocation loc, Identifier name, Type ty) {
        Pair<Integer, TypeDecl> entry = typeScope.get(name);

        if (entry == null || entry.getLeft() != scope.getDepth()) {
            TypeDecl decl = new TypeDecl(name, loc, ty);
            typeScope.put(name, new Pair<>(scope.getDepth(), decl));
            return decl;
        }

        if (entry.getRight().getUnderlyingType() == null) {
            entry.getRight().setUnderlyingType(ty);
            return entry.getRight();
        }

        throw new RuntimeException("Redefinition of " + ty.getName() + " in same scope.");
    }
}
