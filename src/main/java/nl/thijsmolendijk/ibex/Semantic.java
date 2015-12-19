package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.ast.expr.IntegerLiteralExpr;
import nl.thijsmolendijk.ibex.ast.expr.TupleExpr;
import nl.thijsmolendijk.ibex.ast.expr.UnresolvedRefExpr;
import nl.thijsmolendijk.ibex.ast.stmt.TypeDecl;
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

    public Type handleTypeName(SourceLocation loc, Identifier name) {
        //FIXME: Perform lookup
        return new NameAliasType(new TypeDecl(name, loc, null));
    }

    public FunctionType handleFunctionType(Type in, SourceLocation arrowLocation, Type out) {
        return FunctionType.create(in, out, context);
    }

    public ArrayType handleArrayType(Type element, Expression size) {
        if (size != null && !(size instanceof IntegerLiteralExpr)) {
            throw new RuntimeException("No literal array size"); //FIXME: Pretty error.
        }
        return ArrayType.create(element, size == null ? 0 : Integer.parseInt(((IntegerLiteralExpr) size).getVal()), context);
    }

    public TupleType handleTupleType(SourceLocation begin, List<TupleType.TupleElement> elements, SourceLocation end) {
        return TupleType.get(elements.toArray(new TupleType.TupleElement[elements.size()]), context);
    }
}
