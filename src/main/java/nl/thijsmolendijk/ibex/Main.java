package nl.thijsmolendijk.ibex;

import nl.thijsmolendijk.ibex.parse.Lexer;
import nl.thijsmolendijk.ibex.parse.Token;
import nl.thijsmolendijk.ibex.parse.TokenType;

/**
 * Created by molenzwiebel on 19-12-15.
 */
public class Main {
    public static void main(String... args) {
        Lexer lexer = new Lexer("test.en", "fn match if else use as module type extern let\n 0 39187 3918 000\n+ +! !> = ->\n[](){},:: :\nfoo _foo _f$o F0Oo");
        Token token = lexer.lex();
        while (token.isNot(TokenType.EOF)) {
            System.out.println(token);
            token = lexer.lex();
        }
    }
}
