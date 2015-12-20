package nl.thijsmolendijk.ibex.scoping;

import nl.thijsmolendijk.ibex.Semantic;

/**
 * Handles depth and scope tracking for the declaration semantics.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class Scope {
    private int depth;
    private Semantic sem;
    private Scope prevScope;

    public Scope(Semantic sem) {
        this.depth = sem.scope != null ? sem.scope.depth + 1 : 0;
        this.sem = sem;
        this.prevScope = sem.scope;

        sem.scope = this;
        sem.typeScope.extend();
        sem.valueScope.extend();
    }

    /**
     * Marks this scope as "ended", removing it from the lookup options.
     */
    public void end() {
        if (sem.scope != this) {
            throw new IllegalStateException("Unbalanced scope");
        }
        sem.scope = prevScope;
        sem.typeScope.end();
        sem.valueScope.end();
    }

    public int getDepth() {
        return depth;
    }
}
