package nl.thijsmolendijk.molen.parsing

import nl.thijsmolendijk.molen.ast.*
import nl.thijsmolendijk.molen.parsing.TokenType.*
import nl.thijsmolendijk.molen.type.FunctionType
import nl.thijsmolendijk.molen.type.TupleElement
import nl.thijsmolendijk.molen.type.TupleType
import nl.thijsmolendijk.molen.type.Type
import java.util.*

/**
 * Parses source file into AST.
 *
 * Created by molenzwiebel on 09-01-16.
 */
fun main(args: Array<String>) {
    val i = Scanner(System.`in`)
    while (true) {
        val p = Parser("<repl>", i.nextLine())
        val q = p.parseTranslationUnit()
        println(q.text)
    }
}

class Parser(val filename: String, val source: String) {
    private val lexer = Lexer(filename, source)
    private var token = lexer.lex()

    public fun parseTranslationUnit(): TranslationUnit {
        return TranslationUnit(token.location, parseList())
    }

    private fun parseList(): List<Node> {
        val ret: MutableList<Node> = arrayListOf()

        while (token isnt RBRACE && token isnt EOF) {
            ret.add(when (token.kind) {
                LBRACE, KW_RETURN, KW_IF, KW_FOR -> parseStatement()
                KW_LET, KW_VAL, KW_VAR -> parseVarDecl()
                KW_TYPEALIAS -> parseTypeDecl()
                KW_FN -> parseFunDecl()
                KW_IMPORT -> parseImportDecl()
                else -> parseExpr()
            })
        }

        return ret
    }

    private fun parseBraceStmt(): BraceStmt {
        val begin = parseToken(LBRACE, "expected '{' here").location
        val res = BraceStmt(begin, parseList())
        parseToken(RBRACE, "expected '}' here", begin, "to match the '{' here")
        return res
    }

    private fun parseNode(): Node {
        return when (token.kind) {
            KW_VAL, KW_VAR, KW_LET, KW_IF, KW_FOR, KW_RETURN -> parseStatement()
            else -> parseExpr()
        }
    }

    private fun parseStatement(): Statement {
        return when (token.kind) {
            KW_VAL, KW_VAR, KW_LET -> parseVarDecl()
            KW_IF -> parseIf()
            KW_FOR -> parseFor()
            KW_RETURN -> parseReturn()
            else -> {
                abrt("expected statement here, received ${token.kind}")
                throw RuntimeException("Unreachable")
            }
        }
    }

    private fun parseIf(): Statement {
        val start = consumeToken(KW_IF)
        val cond = parseExpr()
        val body = parseBraceStmt()
        var elseBody: BraceStmt? = null

        consumeIf(KW_ELSE) {
            elseBody = if (token kind KW_IF) BraceStmt(it, listOf(parseIf())) else parseBraceStmt()
        }

        return IfStmt(start, cond, body, elseBody)
    }

    private fun parseFor(): Statement {
        val start = consumeToken(KW_FOR)

        val init = if (consumeIf(COMMA)) null else { val ret = parseNode(); parseToken(COMMA, "expected ',' after for initialization", start, "due to the 'for' here"); ret }
        val cond = parseExpr()
        parseToken(COMMA, "expected ',' after for condition", start, "due to the 'for' here")
        val step = if (token kind LBRACE) null else parseNode()

        val body = parseBraceStmt()
        return ForStmt(start, init, cond, step, body)
    }

    private fun parseReturn(): Statement {
        return ReturnStmt(consumeToken(KW_RETURN), parseExpr())
    }

    private fun parseExpr(): Expression {
        val els = arrayListOf<Expression>()

        while (true) {
            val expr = parseExprPrimary()
            els.add(expr)

            if (token isnt OPERATOR) break
            val op = parseToken(OPERATOR, "expected operator after expression")
            els.add(UnresolvedDeclRef(op.location, op.text))
        }

        return if (els.size == 1) els.first() else SequenceExpr(els.first().location, els)
    }

    private fun parseExprPrimary(): Expression {
        val tok = token

        var ret = when (token.kind) {
            KW_TRUE, KW_FALSE -> {
                consumeToken()
                BooleanLiteral(tok.location, tok.text == "true")
            }

            LPAREN -> parseTupleLiteral()

            INTEGER -> {
                consumeToken(INTEGER)
                IntLiteral(tok.location, tok.text.toInt())
            }

            IDENTIFIER -> {
                consumeToken(IDENTIFIER)
                UnresolvedDeclRef(tok.location, tok.text)
            }

            else -> {
                abrt("expected expression here, received ${token.kind}")
                throw RuntimeException("Unreachable")
            }
        }

        // Handle suffixes
        while (true) {
            // ret.foo
            if (consumeIf(COLON)) {
                ret = UnresolvedDotExpr(tok.location, ret, parseToken(IDENTIFIER, "expected identifier after '.'").text)
                continue
            }

            // ret(foo)
            if (token kind LPAREN) {
                ret = CallExpr(tok.location, ret, parseTupleLiteral())
                continue
            }

            // ret = foo
            if (consumeIf(EQUAL)) {
                ret = AssignExpr(tok.location, ret, parseExpr())
                continue
            }

            // FIXME: Array access

            break
        }

        return ret
    }

    private fun parseTupleLiteral(): TupleLiteral {
        val begin = consumeToken(LPAREN)
        val entries = arrayListOf<TupleLiteralElement>()

        if (token isnt RPAREN) {
            do {
                val name = if (consumeIf(COLON)) {
                    val name = parseToken(IDENTIFIER, "expected identifier after '.' in tuple literal").text
                    parseToken(EQUAL, "expected '=' after .identifier in tuple literal")
                    name
                } else null

                entries.add(TupleLiteralElement(name, parseExpr()))
            } while (consumeIf(COMMA))
        }

        parseToken(RPAREN, "expected ')' here", begin, "to match the '(' here")
        return TupleLiteral(begin, entries)
    }

    private fun parseVarDecl(): VarDecl {
        val loc = consumeToken()
        val name = parseToken(IDENTIFIER, "expected identifier here", loc, "due to the 'let'/'var'/'val' here").text

        var givenType: Type? = null
        consumeIf(COLON) {
            givenType = parseType()
        }

        var init: Expression? = null
        consumeIf(EQUAL) {
            init = parseExpr()
        }

        return VarDecl(loc, name, givenType, init)
    }

    private fun parseTypeDecl(): TypeAliasDecl {
        val loc = consumeToken(KW_TYPEALIAS)
        val name = parseToken(IDENTIFIER, "expected identifier in typealias declaration", loc, "due to the 'typealias' here").text
        parseToken(COLON, "expected ':' in typealias declaration", loc, "due to the 'typealias' here")

        return TypeAliasDecl(loc, name, parseType())
    }

    private fun parseFunDecl(): FunDecl {
        val start = consumeToken(KW_FN)
        val name = parseToken(IDENTIFIER, "expected identifier after 'fun'", start, "due to the 'fun' here").text

        if (token isnt LPAREN) abrt("expected '(' in function declaration")
        var funType = parseType()
        if (funType !is FunctionType) funType = FunctionType(funType, TupleType(arrayOf()))

        val argDecls = arrayListOf<ArgDecl>()
        for (arg in (funType.args as TupleType).elements) {
            argDecls.add(ArgDecl(start, arg.name ?: throw RuntimeException("Function argument must be named"), arg.type))
        }

        return FunDecl(start, name, funType, ClosureExpr(start, argDecls, BraceStmt(start, parseList())))
    }

    private fun parseImportDecl(): ImportDecl {
        val loc = consumeToken(KW_IMPORT)
        val lit = parseExpr()
        if (lit !is StringLiteral) abrt("expected string literal after 'import'", lit.location)
        return ImportDecl(loc, (lit as StringLiteral).value)
    }

     fun parseType(): Type {
        var result = when (token.kind) {
            IDENTIFIER -> {
                //FIXME: Perform actual lookup
                val decl = TypeAliasDecl(token.location, token.text, null)
                consumeToken()
                decl.aliasType
            }
            LPAREN -> {
                val leftLoc = consumeToken(LPAREN)
                val res = parseTupleType()
                parseToken(RPAREN, "expected ')' at end of tuple type", leftLoc, "to match this opening '('")
                res
            }
            else -> {
                abrt("expected type here")
                throw RuntimeException("Unreachable")
            }
        }

        while (true) {
            val loc = token.location

            consumeIf(ARROW) {
                val secondHalf = parseType()
                result = FunctionType(result, secondHalf)
            }

            if (token.location == loc) break
        }

        return result
    }

    private fun parseTupleType(): Type {
        val elements = arrayListOf<TupleElement>()
        if (token isnt RPAREN) {
            do {
                var name: String? = if (token kind IDENTIFIER && lexer.peek() kind COLON) {
                    val name = token.text
                    consumeToken()
                    parseToken(COLON, "expected colon after tuple element name")
                    name
                } else null

                elements.add(TupleElement(name, parseType()))
            } while (consumeIf(COMMA))
        }

        return TupleType(elements.toTypedArray())
    }

    private fun consumeToken(ty: TokenType = UNKNOWN): SourceLocation {
        if (ty != UNKNOWN) {
            if (token isnt ty) throw IllegalStateException("Consuming token of wrong kind. $ty expected, ${token.kind} given")
        }
        val old = token
        token = lexer.lex();
        return old.location
    }

    private fun consumeIf(type: TokenType): Boolean {
        if (token isnt type) return false
        consumeToken()
        return true
    }

    private fun <R> consumeIf(type: TokenType, fn: (SourceLocation) -> R): R? {
        if (token isnt type) return null
        val loc = consumeToken();
        return fn(loc)
    }

    private fun parseToken(expected: TokenType, msg: String, noteLoc: SourceLocation? = null, noteMsg: String? = null): Token {
        if (token kind expected) {
            val tok = token
            consumeToken()
            return tok
        }

        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, msg, token.location)
        if (noteLoc != null && noteMsg != null) {
            Diagnostics.printMessage(Diagnostics.MessageKind.NOTE, noteMsg, noteLoc)
        }

        val ex = Exception()
        ex.printStackTrace()

        System.err.flush()
        System.out.flush()
        System.exit(1)

        throw RuntimeException("Unreachable")
    }

    private fun abrt(msg: String, loc: SourceLocation = token.location) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, msg, loc)
        System.exit(1)
    }
}