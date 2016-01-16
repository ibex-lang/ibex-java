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

    // 'true'
    KW_TRUE,
    // 'false'
    KW_FALSE,
    // 'fn'
    KW_FN,
    // 'match'
    KW_MATCH,
    // 'if'
    KW_IF,
    // 'else'
    KW_ELSE,
    // 'import'
    KW_IMPORT,
    // 'module'
    KW_MODULE,
    // 'typealias'
    KW_TYPEALIAS,
    // 'as'
    KW_AS,
    // 'extern'
    KW_EXTERN,
    // 'val'
    KW_VAL,
    // 'var'
    KW_VAR,
    // 'let'
    KW_LET,
    // 'return'
    KW_RETURN,
    // 'for'
    KW_FOR
}