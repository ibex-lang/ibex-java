package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.type.ArrayType;
import nl.thijsmolendijk.ibex.type.FunctionType;
import nl.thijsmolendijk.ibex.type.TupleType;
import nl.thijsmolendijk.ibex.type.Type;
import nl.thijsmolendijk.ibex.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the 'context' of an AST. This basically manages all the instances that should be reused or otherwise kept in check.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class ASTContext {
    private HashMap<String, Identifier> identifiers;
    private List<TupleType> tupleTypes;
    private HashMap<Pair<Type, Type>, FunctionType> functionTypes;
    private HashMap<Pair<Type, Integer>, ArrayType> arrayTypes;

    public ASTContext() {
        this.identifiers = new HashMap<>();
        this.tupleTypes = new ArrayList<>();
        this.functionTypes = new HashMap<>();
        this.arrayTypes = new HashMap<>();
    }

    public Identifier getIdentifier(String text) {
        if (text == null) return null;
        if (identifiers.containsKey(text)) return identifiers.get(text);
        Identifier ret = new Identifier(text);
        identifiers.put(text, ret);
        return ret;
    }

    public List<TupleType> getTupleTypes() {
        return tupleTypes;
    }

    public HashMap<Pair<Type, Type>, FunctionType> getFunctionTypes() {
        return functionTypes;
    }

    public HashMap<Pair<Type, Integer>, ArrayType> getArrayTypes() {
        return arrayTypes;
    }
}
