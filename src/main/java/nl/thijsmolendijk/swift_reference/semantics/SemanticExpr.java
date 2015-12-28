package nl.thijsmolendijk.swift_reference.semantics;

import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.decl.ArgDecl;
import nl.thijsmolendijk.swift_reference.ast.decl.ValueDecl;
import nl.thijsmolendijk.swift_reference.ast.expr.*;
import nl.thijsmolendijk.swift_reference.type.FunctionType;
import nl.thijsmolendijk.swift_reference.type.TupleType;
import nl.thijsmolendijk.swift_reference.util.Pair;

import java.util.List;

import static nl.thijsmolendijk.swift_reference.Diagnostics.SourceLocation;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class SemanticExpr extends BaseSemantic {
    public SemanticExpr(Semantic semantic) {
        super(semantic);
    }

    public Expr actOnIdentifierExpr(Identifier name, SourceLocation loc) {
        ValueDecl dec = semantic.decl.lookupValueName(name);
        if (dec == null) {
            return new UnresolvedRefExpr(name, loc);
        }

        return new RefExpr(dec, loc);
    }

    public Expr actOnNumber(String contents, SourceLocation loc) {
        IntegerLiteralExpr res = new IntegerLiteralExpr(loc, contents);
        res.setType(semantic.decl.lookupTypeName(semantic.context.getIdentifier("integer_literal_type")).getAliasType());
        return res;
    }

    public Expr actOnScopedIdentifierExpr(Identifier name, SourceLocation loc, SourceLocation colonLoc, Identifier name2, SourceLocation loc2) {
        return new UnresolvedScopedIdentExpr(semantic.decl.lookupTypeName(name), loc, colonLoc, loc, name2);
    }

    public Expr actOnCondition(Expr condition) {
        Identifier c2lv = semantic.context.getIdentifier("convertToLogicValue");
        Expr ref = actOnIdentifierExpr(c2lv, condition.getStartingLoc());
        return new CallExpr(ref, condition);
    }

    public FuncExpr actOnFunExprStart(SourceLocation funLoc, FunctionType funType) {
        TupleType inArgs = (TupleType) funType.getIn();
        ArgDecl[] args = new ArgDecl[inArgs.size()];

        for (int i = 0; i < inArgs.size(); i++) {
            TupleType.TupleElement el = inArgs.getElement(i);
            args[i] = new ArgDecl(el.getName(), el.getType(), funLoc);
        }

        return new FuncExpr(funType, funLoc, args, null);
    }

    public Expr actOnTupleExpr(SourceLocation start, List<Pair<Expr, Identifier>> items, SourceLocation end) {
        Expr[] exprs = new Expr[items.size()];
        Identifier[] names = new Identifier[items.size()];
        int nameCount = 0;

        for (int i = 0; i < items.size(); i++) {
            exprs[i] = items.get(i).getLeft();
            names[i] = items.get(i).getRight();
            if (names[i] != null) nameCount++;
        }

        boolean isGrouping = items.size() == 1 && (nameCount == 0 || names[0] == null);
        return new TupleExpr(start, end, exprs, names, isGrouping);
    }
}
