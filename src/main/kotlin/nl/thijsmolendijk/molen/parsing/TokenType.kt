package nl.thijsmolendijk.molen.parsing

/**
 * Any kind of token.
 *
 * Created by molenzwiebel on 09-01-16.
 */
enum class TokenType {
    // Signals unknown token.
    UNKNOWN,
    // Signals End-Of-File.
    EOF,
    // Integer literal
    INTEGER,
    // String literal.
    STRING,

    // '('
    LPAREN,
    // ')'
    RPAREN,
    // ':'
    COLON,
    // '::'
    DOUBLE_COLON,
    // '->'
    ARROW,
    // ','
    COMMA,
    // '['
    LBRACKET,
    // ']'
    RBRACKET,
    // '{'
    LBRACE,
    // '}'
    RBRACE,
    // '='
    EQUAL,
    // '.'
    DOT,

    // Any operator consisting of [/=-+*%<>!&|^]*
    OPERATOR,
    // Any valid identifier, [A-Za-z_] [A-Za-z_0-9$-]*
    IDENTIFIER,

    // 'fn'
    KW_FN,
    // 'match'
    KW_MATCH,
    // 'if'
    KW_IF,
    // 'else'
    KW_ELSE,
    // 'use'
    KW_USE,
    // 'module'
    KW_MODULE,
    // 'type'
    KW_TYPE,
    // 'as'
    KW_AS,
    // 'extern'
    KW_EXTERN,
    // 'let'
    KW_LET,
    // 'return'
    KW_RETURN
}