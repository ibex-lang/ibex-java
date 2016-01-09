package nl.thijsmolendijk.molen.parsing

/**
 * Represents a token in the source.
 *
 * Created by molenzwiebel on 09-01-16.
 */
data class Token(val kind: TokenType, val text: String, val location: SourceLocation, val range: SourceRange = location.range(text.length)) {
    infix fun kind(k: TokenType) = kind == k
    infix fun isnt(k: TokenType) = kind != k
}