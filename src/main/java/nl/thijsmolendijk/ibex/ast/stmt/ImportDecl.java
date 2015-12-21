package nl.thijsmolendijk.ibex.ast.stmt;

import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.parse.SourceLocation;
import nl.thijsmolendijk.ibex.util.Pair;

import java.util.List;

/**
 * Created by molenzwiebel on 20-12-15.
 */
public class ImportDecl extends Statement {
    private SourceLocation loc;
    private List<Pair<Identifier, SourceLocation>> path;

    public ImportDecl(SourceLocation loc, List<Pair<Identifier, SourceLocation>> path) {
        this.loc = loc;
        this.path = path;
    }

    public SourceLocation getLoc() {
        return loc;
    }

    public List<Pair<Identifier, SourceLocation>> getPath() {
        return path;
    }

    @Override
    public SourceLocation getLocation() {
        return loc;
    }
}
