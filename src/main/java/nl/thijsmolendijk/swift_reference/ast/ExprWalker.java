package nl.thijsmolendijk.swift_reference.ast;

import nl.thijsmolendijk.swift_reference.ast.decl.ValueDecl;
import nl.thijsmolendijk.swift_reference.ast.expr.*;
import nl.thijsmolendijk.swift_reference.ast.stmt.*;
import nl.thijsmolendijk.swift_reference.util.TriFunction;

import java.lang.reflect.Method;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class ExprWalker {
    private TriFunction<Expr, Expr.WalkOrder, Object, Expr> exprFun;
    private TriFunction<Stmt, Expr.WalkOrder, Object, Stmt> stmtFun;
    private Object data;

    public ExprWalker(TriFunction<Expr, Expr.WalkOrder, Object, Expr> exprFun, TriFunction<Stmt, Expr.WalkOrder, Object, Stmt> stmtFun, Object data) {
        this.exprFun = exprFun;
        this.stmtFun = stmtFun;
        this.data = data;
    }

    public Expr visitExpr(IntegerLiteralExpr expr) {
        return expr;
    }

    public Expr visitExpr(RefExpr expr) {
        return expr;
    }

    public Expr visitExpr(UnresolvedRefExpr expr) {
        return expr;
    }

    public Expr visitExpr(UnresolvedMemberExpr expr) {
        return expr;
    }

    public Expr visitExpr(UnresolvedScopedIdentExpr expr) {
        return expr;
    }

    public Expr visitExpr(TupleExpr expr) {
        if (expr.getSubExprs() == null) return expr;

        for (int i = 0; i < expr.getSubExprs().length; i++) {
            if (expr.getSubExprs()[i] == null) continue;
            Expr newE = run(expr.getSubExprs()[i]);
            if (newE != null) {
                expr.getSubExprs()[i] = newE;
            } else {
                return null;
            }
        }

        return expr;
    }

    public Expr visitExpr(UnresolvedDotExpr expr) {
        if (expr.getSubExpr() != null) return expr;

        Expr newE = run(expr.getSubExpr());
        if (newE != null) {
            expr.setSubExpr(newE);
            return expr;
        }
        return null;
    }

    public Expr visitExpr(TupleElementExpr expr) {
        Expr newE = run(expr.getSubExpr());
        if (newE != null) {
            expr.setSubExpr(newE);
            return expr;
        }
        return null;
    }

    public Expr visitExpr(CallExpr expr) {
        Expr newE = run(expr.getFunction());
        if (newE == null) return null;
        expr.setFunction(newE);

        newE = run(expr.getArg());
        if (newE == null) return null;
        expr.setArg(newE);

        return expr;
    }

    public Expr visitExpr(SequenceExpr expr) {
        for (int i = 0; i < expr.getElements().length; i++) {
            if (expr.getElements()[i] == null) continue;
            Expr newE = run(expr.getElements()[i]);
            if (newE != null) {
                expr.getElements()[i] = newE;
            } else {
                return null;
            }
        }
        return expr;
    }

    public Expr visitExpr(FuncExpr expr) {
        Stmt newE = run(expr.getBody());
        if (newE != null) {
            expr.setBody((BraceStmt) newE);
            return expr;
        }
        return null;
    }

    public Expr visitExpr(BinaryExpr expr) {
        Expr newE = run(expr.getLeft());
        if (newE == null) return null;
        expr.setLeft(newE);

        newE = run(expr.getRight());
        if (newE == null) return null;
        expr.setRight(newE);

        return expr;
    }

    public Stmt visitStmt(NoopStmt stmt) {
        return stmt;
    }

    public Stmt visitStmt(AssignStmt stmt) {
        Expr newE = run(stmt.getDest());
        if (newE == null) return null;
        stmt.setDest(newE);

        newE = run(stmt.getValue());
        if (newE == null) return null;
        stmt.setValue(newE);

        return stmt;
    }

    public Stmt visitStmt(BraceStmt stmt) {
        for (int i = 0; i < stmt.getElements().length; i++) {
            Node n = stmt.getElements()[i];
            if (n instanceof Expr) {
                Expr newE = run((Expr) n);
                if (newE != null) {
                    stmt.getElements()[i] = newE;
                } else return null;
            } else if (n instanceof Stmt) {
                Stmt newS = run((Stmt) n);
                if (newS != null) {
                    stmt.getElements()[i] = newS;
                } else return null;
            } else if (n instanceof ValueDecl) {
                Expr newE = run(((ValueDecl) n).getInit());
                if (newE != null) {
                    ((ValueDecl) n).setInit(newE);
                } else return null;
            }
        }
        return stmt;
    }

    public Stmt visitStmt(ReturnStmt stmt) {
        Expr newE = run(stmt.getResult());
        if (newE != null) {
            stmt.setResult(newE);
            return stmt;
        }
        return null;
    }

    public Stmt visitStmt(IfStmt stmt) {
        Expr e2 = run(stmt.getCond());
        if (e2 == null) return null;
        stmt.setCond(e2);

        Stmt s2 = run(stmt.getIfThen());
        if (s2 == null) return null;
        stmt.setIfThen(s2);

        if (stmt.getIfElse() != null) {
            s2 = run(stmt.getIfElse());
            if (s2 == null) return null;
            stmt.setIfElse(s2);
        }

        return stmt;
    }

    public Stmt visitStmt(WhileStmt stmt) {
        Expr e2 = run(stmt.getCond());
        if (e2 == null) return null;
        stmt.setCond(e2);

        Stmt s2 = run(stmt.getBody());
        if (s2 == null) return null;
        stmt.setBody(s2);

        return stmt;
    }

    public Expr run(Expr e) {
        if (exprFun == null) return visit(e);

        Expr e2 = exprFun.call(e, Expr.WalkOrder.PRE, data);
        if (e2 == null) return e;

        if (e != null) e = visit(e);
        if (e != null) e = exprFun.call(e, Expr.WalkOrder.POST, data);
        return e;
    }

    public Stmt run(Stmt s) {
        if (stmtFun == null) return visit(s);

        Stmt s2 = stmtFun.call(s, Expr.WalkOrder.PRE, data);
        if (s2 == null) return s;

        if (s != null) s = visit(s);
        if (s != null) s = stmtFun.call(s, Expr.WalkOrder.POST, data);
        return s;
    }

    private Expr visit(Expr e) {
        try {
            Method m = getClass().getDeclaredMethod("visitExpr", e.getClass());
            return (Expr) m.invoke(this, e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Stmt visit(Stmt e) {
        try {
            Method m = getClass().getDeclaredMethod("visitStmt", e.getClass());
            return (Stmt) m.invoke(this, e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
