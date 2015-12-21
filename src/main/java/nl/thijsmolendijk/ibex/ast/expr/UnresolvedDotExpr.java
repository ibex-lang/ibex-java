package nl.thijsmolendijk.ibex.ast.expr;

import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.stmt.ValueDecl;
import nl.thijsmolendijk.ibex.parse.SourceLocation;

/**
 * Represents dot (a.b) access to a type we don't yet know the type of.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class UnresolvedDotExpr extends Expression {
    private Expression subExpr;
    private SourceLocation dotLoc, nameLoc;
    private Identifier name;

    private ValueDecl resolvedDecl = null;

    public UnresolvedDotExpr(Expression subExpr, SourceLocation dotLoc, SourceLocation nameLoc, Identifier name) {
        this.subExpr = subExpr;
        this.dotLoc = dotLoc;
        this.nameLoc = nameLoc;
        this.name = name;
    }

    public Expression getSubExpr() {
        return subExpr;
    }

    public void setSubExpr(Expression subExpr) {
        this.subExpr = subExpr;
    }

    public Identifier getName() {
        return name;
    }

    public ValueDecl getResolvedDecl() {
        return resolvedDecl;
    }

    public void setResolvedDecl(ValueDecl resolvedDecl) {
        this.resolvedDecl = resolvedDecl;
    }

    @Override
    public SourceLocation getLocation() {
        return subExpr != null ? subExpr.getLocation() : dotLoc;
    }
}
