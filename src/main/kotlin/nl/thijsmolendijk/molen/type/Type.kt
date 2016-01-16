package nl.thijsmolendijk.molen.type

import nl.thijsmolendijk.molen.ast.TypeAliasDecl

/**
 * Created by molenzwiebel on 09-01-16.
 */
abstract class Type {
    abstract val text: String
}

class TypeAliasType(val node: TypeAliasDecl) : Type() {
    override val text: String
        get() = "<alias of ${node.underlyingType?.text}>"
}

class FunctionType(val args: Type, val ret: Type) : Type() {
    override val text: String
        get() = "${args.text} -> ${ret.text}"
}

class TupleType(val elements: Array<TupleElement>) : Type() {
    override val text: String
        get() = "(${elements.map { it.text }.joinToString(", ")})"
}

data class TupleElement(val name: String?, val type: Type) {
    val text = "${if (name != null) "${name}: " else ""}${type.text}"
}