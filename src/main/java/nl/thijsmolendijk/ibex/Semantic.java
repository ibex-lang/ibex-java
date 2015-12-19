package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.expr.*;
import nl.thijsmolendijk.ibex.ast.stmt.ArgDecl;
import nl.thijsmolendijk.ibex.ast.stmt.NamedDecl;
import nl.thijsmolendijk.ibex.ast.stmt.TypeDecl;
import nl.thijsmolendijk.ibex.ast.stmt.VarDecl;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.type.*;
import nl.thijsmolendijk.ibex.util.Pair;

import java.util.List;

/**
 * Manages scope, complex expression construction and preliminary type lookup.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class Semantic {
    private ASTContext context;

    public Semantic(ASTContext context) {
        this.context = context;
    }

    /* ========================================================
     *                      Expressions
     * ======================================================== */

    public Expression handleIdentifier(SourceLocation loc, Identifier ident) {
        //FIXME: Actually perform lookup.
        return new UnresolvedRefExpr(loc, ident);
    }

    public IntegerLiteralExpr handleInteger(SourceLocation loc, String val) {
        //FIXME: Integer type
        return new IntegerLiteralExpr(loc, val);
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
        //FIXME: Scope.
        TupleType inArgs = (TupleType) funType.getIn();
        ArgDecl[] args = new ArgDecl[inArgs.size()];

        for (int i = 0; i < inArgs.size(); i++) {
            TupleType.TupleElement el = inArgs.getElement(i);
            args[i] = new ArgDecl(el.getName(), el.getType(), start);
        }

        return new FuncExpr(funType, start, args, null);
    }

    /* ========================================================
     *                      Type Constructing
     * ======================================================== */

    public Type handleTypeName(SourceLocation loc, Identifier name) {
        //FIXME: Perform lookup
        return new NameAliasType(new TypeDecl(name, loc, null));
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

    public void addToScope(NamedDecl decl) {

    }

    public VarDecl handleVarDecl(SourceLocation loc, Identifier name, Type givenType, Expression init) {
        if (givenType == null) {
            givenType = context.getDependentType();
        }
        return new VarDecl(name, givenType, init, loc);
    }

    public TypeDecl handleTypeDecl(SourceLocation loc, Identifier name, Type ty) {
        //FIXME: Mark as resolved if needed.
        return new TypeDecl(name, loc, ty);
    }
}
