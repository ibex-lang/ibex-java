package nl.thijsmolendijk.ibex.parse;

/**
 * All types of tokens.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public enum TokenType {
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
    KW_LET;
}