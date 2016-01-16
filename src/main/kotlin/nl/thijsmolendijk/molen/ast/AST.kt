package nl.thijsmolendijk.molen.ast

import nl.thijsmolendijk.molen.parsing.SourceLocation
import nl.thijsmolendijk.molen.type.Type
import nl.thijsmolendijk.molen.type.TypeAliasType

interface Node {
    val location: SourceLocation
    val text: String
}

abstract class Expression(override val location: SourceLocation) : Node
abstract class Statement(override val location: SourceLocation) : Node

class TranslationUnit(loc: SourceLocation, val contents: List<Node>) : Statement(loc) {
    override val text: String
        get() = contents.map { it.text }.joinToString("\n")
}

class AssignExpr(loc: SourceLocation, val left: Expression, val right: Expression) : Expression(loc) {
    override val text: String
        get() = "${left.text} = ${right.text}"
}

class BraceStmt(loc: SourceLocation, val items: List<Node>) : Statement(loc) {
    override val text: String
        get() = "{ ${items.map { it.text }.joinToString("\n")} }"
}

class ClosureExpr(loc: SourceLocation, val args: List<ArgDecl>, val body: BraceStmt) : Expression(loc) {
    override val text: String
        get() = "func(${args.joinToString(", ") { it.text }}) ${body.text}"
}

class IfStmt(loc: SourceLocation, val cond: Expression, val ifTrue: BraceStmt, val ifFalse: BraceStmt?) : Statement(loc) {
    override val text: String
        get() = "if ${cond.text} ${ifTrue.text}${if (ifFalse != null) " else ${ifFalse.text}" else ""}"
}

class ForStmt(loc: SourceLocation, val init: Node?, val cond: Expression, val step: Node?, val body: BraceStmt) : Statement(loc) {
    override val text: String
        get() = "for ${init?.text}, ${cond.text}, ${step?.text} ${body.text}"
}

class ReturnStmt(loc: SourceLocation, val value: Expression) : Statement(loc) {
    override val text: String
        get() = "return ${value.text}"
}

class UnresolvedDeclRef(loc: SourceLocation, val name: String) : Expression(loc) {
    override val text: String
        get() = "<unresolved: $name>"
}

class UnresolvedDotExpr(loc: SourceLocation, val expr: Expression, val name: String) : Expression(loc) {
    override val text: String
        get() = "<unresolved: ${expr.text}.$name>"
}

class CallExpr(loc: SourceLocation, val target: Expression, val args: TupleLiteral) : Expression(loc) {
    override val text: String
        get() = "${target.text}${args.text}"
}

class SequenceExpr(loc: SourceLocation, val elements: List<Expression>) : Expression(loc) {
    override val text: String
        get() = "(" + elements.joinToString(" ") { it.text } + ")"
}

// Decls
abstract class NamedDecl(loc: SourceLocation, val name: String) : Statement(loc)
abstract class ValueDecl(loc: SourceLocation, name: String, val type: Type?, val init: Expression?) : NamedDecl(loc, name)

class ArgDecl(loc: SourceLocation, name: String, type: Type) : ValueDecl(loc, name, type, null) {
    override val text: String
        get() = "$name: ${type!!.text}"
}

class VarDecl(loc: SourceLocation, name: String, type: Type?, init: Expression?) : ValueDecl(loc, name, type, init) {
    override val text: String
        get() = "val $name${if (type != null) " : ${type.text}" else ""}${if (init != null) " = ${init.text}" else ""}"
}
class TypeAliasDecl(loc: SourceLocation, name: String, var underlyingType: Type?) : NamedDecl(loc, name) {
    override val text: String
        get() = "typealias $name : ${if (underlyingType != null) underlyingType?.text else "<unresolved>"}"

    val aliasType: Type by lazy {
        TypeAliasType(this)
    }
}
class FunDecl(loc: SourceLocation, name: String, type: Type, init: Expression) : ValueDecl(loc, name, type, init) {
    override val text: String
        get() = "fn name${type!!.text} { ${init!!.text} }"
}
class ImportDecl(loc: SourceLocation, val path: String) : Statement(loc)  {
    override val text: String
        get() = "import \"$path\""
}

// Literals
abstract class Literal<T>(loc: SourceLocation, val value: T) : Expression(loc)

class IntLiteral(loc: SourceLocation, value: Int) : Literal<Int>(loc, value) {
    override val text: String
        get() = value.toString()
}

class BooleanLiteral(loc: SourceLocation, value: Boolean) : Literal<Boolean>(loc, value) {
    override val text: String
        get() = value.toString()
}

class StringLiteral(loc: SourceLocation, value: String) : Literal<String>(loc, value)  {
    override val text: String
        get() = "\"$value\""
}

data class TupleLiteralElement(val name: String?, val value: Expression) {
    val text: String = "${if (name != null) ".$name = " else ""}${value.text}"
}

class TupleLiteral(loc: SourceLocation, value: List<TupleLiteralElement>) : Literal<List<TupleLiteralElement>>(loc, value)  {
    override val text: String
        get() = "(${value.joinToString(", ") { it.text }})"
}