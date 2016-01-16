package nl.thijsmolendijk.molen.parsing

/**
 * Main lexer.
 *
 * Created by molenzwiebel on 09-01-16.
 */
class Lexer(val filename: String, val source: String) {
    private final val KEYWORDS = mapOf(
            "true" to TokenType.KW_TRUE,
            "false" to TokenType.KW_FALSE,
            "fn" to TokenType.KW_FN,
            "match" to TokenType.KW_MATCH,
            "if" to TokenType.KW_IF,
            "else" to TokenType.KW_ELSE,
            "import" to TokenType.KW_IMPORT,
            "module" to TokenType.KW_MODULE,
            "typealias" to TokenType.KW_TYPEALIAS,
            "as" to TokenType.KW_AS,
            "extern" to TokenType.KW_EXTERN,
            "var" to TokenType.KW_VAR,
            "val" to TokenType.KW_VAL,
            "let" to TokenType.KW_LET,
            "return" to TokenType.KW_RETURN,
            "for" to TokenType.KW_FOR
    )

    private var offset: Int = 0
    private var nextToken: Token = lexImpl()

    public fun peek() = nextToken

    public fun lex(): Token {
        val res = nextToken
        if (res isnt TokenType.EOF) {
            this.nextToken = lexImpl()
        }
        return res
    }

    private fun lexImpl(): Token {
        val start: Int = offset

        val c = nextChar()
        return when (c) {
            in charArrayOf(' ', '\t', '\n', '\r') -> lexImpl()
            0.toChar() -> newToken(TokenType.EOF, start)

            '(' -> newToken(TokenType.LPAREN, start)
            ')' -> newToken(TokenType.RPAREN, start)
            '{' -> newToken(TokenType.LBRACE, start)
            '}' -> newToken(TokenType.RBRACE, start)
            '[' -> newToken(TokenType.LBRACKET, start)
            ']' -> newToken(TokenType.RBRACKET, start)
            ',' -> newToken(TokenType.COMMA, start)
            '.' -> newToken(TokenType.COLON, start)

            ':' -> {
                if (source[offset] == ':') {
                    offset++
                    newToken(TokenType.DOUBLE_COLON, start)
                } else newToken(TokenType.COLON, start)
            }

            in charArrayOf('/', '=', '-', '+', '*', '%', '<', '>', '!', '&', '|', '^') -> lexOperator()
            in 'A'..'Z', in 'a'..'z', '_' -> lexIdentifier()
            in '0'..'9' -> lexDigit()

            else -> {
                abrt("invalid character '$c' in source file")
                newToken(TokenType.UNKNOWN, start)
            }
        }
    }

    private fun lexOperator(): Token {
        val start = offset - 1
        while (curChar().isOperator()) offset++

        if (offset - start == 1 && source[start] == '=') return newToken(TokenType.EQUAL, start)
        if (offset - start == 2 && source[start] == '-' && source[start + 1] == '>') return newToken(TokenType.ARROW, start)

        return newToken(TokenType.OPERATOR, start)
    }

    private fun lexIdentifier(): Token {
        val start = offset - 1
        while (curChar().isValidIdentPart()) offset++

        val text = source.substring(start, offset)
        if (KEYWORDS[text] != null) return newToken(KEYWORDS[text]!!, start)
        return newToken(TokenType.IDENTIFIER, start)
    }

    private fun lexDigit(): Token {
        val start = offset - 1
        while (curChar().isNumber()) offset++
        return newToken(TokenType.INTEGER, start)
    }

    private fun curChar() = if (offset == source.length) 0.toChar() else source[offset]
    private fun nextChar() = if (offset == source.length) 0.toChar() else source[offset++]
    private fun Char.isNumber(): Boolean = this in '0'..'9'
    private fun Char.isOperator(): Boolean = this in "/=-+*%<>!&|^"
    private fun Char.isValidIdentPart(): Boolean = this == '_' || this == '$' || this == '-' || (this >= 'a' && this <= 'z') || (this >= 'A' && this <= 'Z') || (this >= '0' && this <= '9')
    private fun newToken(type: TokenType, start: Int) = Token(type, source.substring(start, offset), SourceLocation(filename, source, start))

    private fun abrt(msg: String) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, msg, SourceLocation(filename, source, offset))
        System.exit(1)
    }
}