package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.ast.expr.*;
import nl.thijsmolendijk.ibex.parse.Diagnostics;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.type.DependentType;
import nl.thijsmolendijk.ibex.type.FunctionType;
import nl.thijsmolendijk.ibex.type.TupleType;
import nl.thijsmolendijk.ibex.type.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks and resolves types. Basically type inference with some work done already.
 *
 * Created by molenzwiebel on 21-12-15.
 */
public class TypeChecker {
    private ASTContext context;

    public Expression visitExpr(IntegerLiteralExpr expr) {
        // Type is already set in Semantic#handleInteger
        return expr;
    }

    public Expression visitExpr(DeclRefExpr expr) {
        if (expr.getDecl() == null) {
            errorAndExit(expr.getLocation(), "use of undeclared identifier");
        }
        expr.setType(expr.getDecl().getType());
        return expr;
    }

    public Expression visitExpr(UnresolvedRefExpr expr) {
        throw new RuntimeException("Should have been resolved by name binding.");
    }

    public Expression visitExpr(TupleExpr expr) {
        if (expr.isGrouping()) {
            expr.setType(expr.getExpressions()[0].getType());
            return expr;
        }

        List<TupleType.TupleElement> elements = new ArrayList<>();
        for (int i = 0; i < expr.getExpressions().length; i++) {
            Type ty = expr.getExpressions()[i].getType();

            // If one element is dependent, the resulting tuple is too.
            if (ty instanceof DependentType) {
                expr.setType(ty);
                return expr;
            }

            elements.add(new TupleType.TupleElement(expr.getNames()[i], ty));
        }

        expr.setType(TupleType.get(elements.toArray(new TupleType.TupleElement[elements.size()]), context));
        return expr;
    }

    public Expression visitExpr(CallExpr expr) {
        if (expr.getFunction().getType() instanceof FunctionType) {
            FunctionType fnTy = (FunctionType) expr.getFunction().getType();
            Expression arg2 = convertToType(expr.getArg(), fnTy.getIn());
            if (arg2 == null) {
                errorAndExit(expr.getArg().getLocation(), "while converting function argument to type");
                return null;
            }

            expr.setType(fnTy.getOut());
            return expr;
        }

        if (expr.getFunction().getType() instanceof DependentType) {
            // Can't do anything here yt.
            expr.setType(expr.getFunction().getType());
            return expr;
        }

        errorAndExit(expr.getLocation(), "calling expression which is not a function");
        return null;
    }

    public Expression visitExpr(BinaryExpr expr) {
        FunctionType ty = (FunctionType) expr.getFun().getType();
        TupleType input = (TupleType) ty.getIn();

        expr.setLeft(convertToType(expr.getLeft(), input.getElementType(0)));
        if (expr.getLeft() == null) {
            errorAndExit(expr.getLocation(), "converting left hand to expected type");
            return null;
        }

        expr.setRight(convertToType(expr.getRight(), input.getElementType(1)));
        if (expr.getRight() == null) {
            errorAndExit(expr.getLocation(), "converting right hand to expected type");
            return null;
        }

        expr.setType(ty.getOut());
        return expr;
    }

    public Expression visitExpr(AssignExpr expr) {
        expr.setValue(convertToType(expr.getValue(), expr.getDest().getType()));
        if (expr.getValue() == null) {
            errorAndExit(expr.getLocation(), "converting assigned value to destination type");
            return null;
        }
        return expr;
    }

    private Expression convertToType(Expression from, Type to) {

    }

    private Expression visit(Expression e) {
        try {
            Method m = getClass().getDeclaredMethod("visitExpr", e.getClass());
            return (Expression) m.invoke(this, e);
        } catch (NoSuchMethodException ex) {
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Statement visit(Statement e) {
        try {
            Method m = getClass().getDeclaredMethod("visitStmt", e.getClass());
            return (Statement) m.invoke(this, e);
        } catch (NoSuchMethodException ex) {
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void errorAndExit(SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, message, loc);
        System.err.flush();
        System.exit(1);
    }
}
