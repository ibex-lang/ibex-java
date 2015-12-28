package nl.thijsmolendijk.swift_reference.ast;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.type.FunctionType;
import nl.thijsmolendijk.swift_reference.type.Type;
import nl.thijsmolendijk.swift_reference.util.TriFunction;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public abstract class Expr extends Node {
    protected Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract Diagnostics.SourceLocation getStartingLoc();

    public Expr walkExpr(
            TriFunction<Expr, WalkOrder, Object, Expr> exprFun,
            TriFunction<Stmt, WalkOrder, Object, Stmt> stmtFun,
            Object data) {
        return new ExprWalker(exprFun, stmtFun, data).run(this);
    }

    public ConversionRank getConversionRank(Type dest, ASTContext ctx) {
        assert type != null : "Getting conversion rank without computed type";

        if (type.getCanonicalType(ctx) == dest.getCanonicalType(ctx)) {
            return ConversionRank.IDENTITY;
        }

        //TODO: Tuples

        if (dest instanceof FunctionType) {
            return getConversionRank(((FunctionType) dest).getOut(), ctx) == ConversionRank.INVALID ? ConversionRank.INVALID : ConversionRank.AUTO_CLOSURE;
        }

        return ConversionRank.INVALID;
    }

    public enum WalkOrder {
        PRE,
        POST;
    }

    public enum ConversionRank {
        IDENTITY,
        AUTO_CLOSURE,
        INVALID;
    }
}
