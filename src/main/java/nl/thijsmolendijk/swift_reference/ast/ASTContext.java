package nl.thijsmolendijk.swift_reference.ast;

import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.type.*;
import nl.thijsmolendijk.swift_reference.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains all context and program information for a specific AST expr.
 * Created by molenzwiebel on 16-12-15.
 */
public class ASTContext {
    private HashMap<String, Identifier> identifiers;
    private List<TupleType> tupleTypes;
    private HashMap<Pair<Type, Type>, FunctionType> functionTypes;
    private HashMap<Pair<Type, Integer>, ArrayType> arrayTypes;

    private final Type unitType; // Empty tuple, ()
    private final Type dependentType; // Meaning the type depends on context.

    private final Type int1Type;
    private final Type int8Type;
    private final Type int16Type;
    private final Type int32Type;
    private final Type int64Type;

    public ASTContext() {
        this.identifiers = new HashMap<>();
        this.tupleTypes = new ArrayList<>();
        this.functionTypes = new HashMap<>();
        this.arrayTypes = new HashMap<>();

        this.unitType = TupleType.get(new TupleType.TupleElement[0], this);
        this.dependentType = new DependentType();

        this.int1Type = new BuiltinType(BuiltinType.BuiltinKind.INT1);
        this.int8Type = new BuiltinType(BuiltinType.BuiltinKind.INT8);
        this.int16Type = new BuiltinType(BuiltinType.BuiltinKind.INT16);
        this.int32Type = new BuiltinType(BuiltinType.BuiltinKind.INT32);
        this.int64Type = new BuiltinType(BuiltinType.BuiltinKind.INT64);
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

    public Type getUnitType() {
        return unitType;
    }

    public Type getDependentType() {
        return dependentType;
    }

    public Type getInt1Type() {
        return int1Type;
    }

    public Type getInt8Type() {
        return int8Type;
    }

    public Type getInt16Type() {
        return int16Type;
    }

    public Type getInt32Type() {
        return int32Type;
    }

    public Type getInt64Type() {
        return int64Type;
    }
}
