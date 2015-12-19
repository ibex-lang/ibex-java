package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.type.*;
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

    private final BuiltinType int1;
    private final BuiltinType int8;
    private final BuiltinType int16;
    private final BuiltinType int32;
    private final BuiltinType int64;

    private final TupleType unit;

    public ASTContext() {
        this.identifiers = new HashMap<>();
        this.tupleTypes = new ArrayList<>();
        this.functionTypes = new HashMap<>();
        this.arrayTypes = new HashMap<>();

        this.int1 = new BuiltinType(BuiltinType.BuiltinKind.INT1);
        this.int8 = new BuiltinType(BuiltinType.BuiltinKind.INT8);
        this.int16 = new BuiltinType(BuiltinType.BuiltinKind.INT16);
        this.int32 = new BuiltinType(BuiltinType.BuiltinKind.INT32);
        this.int64 = new BuiltinType(BuiltinType.BuiltinKind.INT64);

        this.unit = TupleType.get(new TupleType.TupleElement[0], this);
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

    public BuiltinType getInt1() {
        return int1;
    }

    public BuiltinType getInt8() {
        return int8;
    }

    public BuiltinType getInt16() {
        return int16;
    }

    public BuiltinType getInt32() {
        return int32;
    }

    public BuiltinType getInt64() {
        return int64;
    }

    public TupleType getUnit() {
        return unit;
    }
}
