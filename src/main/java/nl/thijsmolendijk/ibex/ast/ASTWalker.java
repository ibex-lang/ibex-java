package nl.thijsmolendijk.ibex.ast;

import nl.thijsmolendijk.ibex.ast.expr.*;
import nl.thijsmolendijk.ibex.ast.stmt.BraceStmt;
import nl.thijsmolendijk.ibex.ast.stmt.IfStmt;
import nl.thijsmolendijk.ibex.ast.stmt.ReturnStmt;
import nl.thijsmolendijk.ibex.ast.stmt.ValueDecl;
import nl.thijsmolendijk.ibex.util.TriFunction;

import java.lang.reflect.Method;

/**
 * Walks the specified AST and possibly replaces nodes.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class ASTWalker {
    private TriFunction<Expression, WalkOrder, Object, Expression> exprFun;
    private TriFunction<Statement, WalkOrder, Object, Statement> stmtFun;
    private Object data;

    public ASTWalker(TriFunction<Expression, WalkOrder, Object, Expression> exprFun, TriFunction<Statement, WalkOrder, Object, Statement> stmtFun, Object data) {
        this.exprFun = exprFun;
        this.stmtFun = stmtFun;
        this.data = data;
    }

    public Expression visitExpr(IntegerLiteralExpr expr) {
        return expr;
    }

    public Expression visitExpr(DeclRefExpr expr) {
        return expr;
    }

    public Expression visitExpr(UnresolvedRefExpr expr) {
        return expr;
    }

    public Expression visitExpr(TupleExpr expr) {
        if (expr.getExpressions() == null) return expr;

        for (int i = 0; i < expr.getExpressions().length; i++) {
            if (expr.getExpressions()[i] == null) continue;
            Expression newE = run(expr.getExpressions()[i]);
            if (newE != null) {
                expr.getExpressions()[i] = newE;
            } else {
                return null;
            }
        }

        return expr;
    }

    public Expression visitExpr(UnresolvedDotExpr expr) {
        if (expr.getSubExpr() != null) return expr;

        Expression newE = run(expr.getSubExpr());
        if (newE != null) {
            expr.setSubExpr(newE);
            return expr;
        }
        return null;
    }

    public Expression visitExpr(TupleAccessExpr expr) {
        Expression newE = run(expr.getName());
        if (newE != null) {
            expr.setName(newE);
            return expr;
        }
        return null;
    }

    public Expression visitExpr(CallExpr expr) {
        Expression newE = run(expr.getFunction());
        if (newE == null) return null;
        expr.setFunction(newE);

        newE = run(expr.getArg());
        if (newE == null) return null;
        expr.setArg(newE);

        return expr;
    }

    public Expression visitExpr(SequenceExpr expr) {
        for (int i = 0; i < expr.getContents().length; i++) {
            if (expr.getContents()[i] == null) continue;
            Expression newE = run(expr.getContents()[i]);
            if (newE != null) {
                expr.getContents()[i] = newE;
            } else {
                return null;
            }
        }
        return expr;
    }

    public Expression visitExpr(FuncExpr expr) {
        Statement newE = run(expr.getBody());
        if (newE != null) {
            expr.setBody((BraceStmt) newE);
            return expr;
        }
        return null;
    }

    public Expression visitExpr(BinaryExpr expr) {
        Expression newE = run(expr.getLeft());
        if (newE == null) return null;
        expr.setLeft(newE);

        newE = run(expr.getRight());
        if (newE == null) return null;
        expr.setRight(newE);

        return expr;
    }

    public Expression visitExpr(AssignExpr expr) {
        Expression newE = run(expr.getDest());
        if (newE == null) return null;
        expr.setDest(newE);

        newE = run(expr.getValue());
        if (newE == null) return null;
        expr.setValue(newE);

        return expr;
    }

    public Statement visitStmt(BraceStmt stmt) {
        for (int i = 0; i < stmt.getElements().length; i++) {
            Node n = stmt.getElements()[i];
            if (n instanceof ValueDecl) {
                Expression newE = run(((ValueDecl) n).getInit());
                if (newE != null) {
                    ((ValueDecl) n).setInit(newE);
                } else return null;
            } else if (n instanceof Expression) {
                Expression newE = run((Expression) n);
                if (newE != null) {
                    stmt.getElements()[i] = newE;
                } else return null;
            } else if (n instanceof Statement) {
                Statement newS = run((Statement) n);
                if (newS != null) {
                    stmt.getElements()[i] = newS;
                } else return null;
            }
        }
        return stmt;
    }

    public Statement visitStmt(ReturnStmt stmt) {
        Expression newE = run(stmt.getValue());
        if (newE != null) {
            stmt.setValue(newE);
            return stmt;
        }
        return null;
    }

    public Statement visitStmt(IfStmt stmt) {
        Expression e2 = run(stmt.getCondition());
        if (e2 == null) return null;
        stmt.setCondition(e2);

        Statement s2 = run(stmt.getIfThen());
        if (s2 == null) return null;
        stmt.setIfThen(s2);

        if (stmt.getIfElse() != null) {
            s2 = run(stmt.getIfElse());
            if (s2 == null) return null;
            stmt.setIfElse(s2);
        }

        return stmt;
    }

    public Expression run(Expression e) {
        if (exprFun == null) return visit(e);

        Expression e2 = exprFun.call(e, WalkOrder.PRE, data);
        if (e2 == null) return e;

        if (e != null) e = visit(e);
        if (e != null) e = exprFun.call(e, WalkOrder.POST, data);
        return e;
    }

    public Statement run(Statement s) {
        if (stmtFun == null) return visit(s);

        Statement s2 = stmtFun.call(s, WalkOrder.PRE, data);
        if (s2 == null) return s;

        if (s != null) s = visit(s);
        if (s != null) s = stmtFun.call(s, WalkOrder.POST, data);
        return s;
    }

    private Expression visit(Expression e) {
        try {
            Method m = getClass().getDeclaredMethod("visitExpr", e.getClass());
            return (Expression) m.invoke(this, e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Statement visit(Statement e) {
        try {
            Method m = getClass().getDeclaredMethod("visitStmt", e.getClass());
            return (Statement) m.invoke(this, e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public enum WalkOrder {
        PRE,
        POST;
    }
}
