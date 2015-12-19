package nl.thijsmolendijk.ibex.parse;

import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.Semantic;
import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.expr.CallExpr;
import nl.thijsmolendijk.ibex.ast.expr.Identifier;
import nl.thijsmolendijk.ibex.ast.expr.SequenceExpr;
import nl.thijsmolendijk.ibex.ast.expr.UnresolvedDotExpr;
import nl.thijsmolendijk.ibex.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses tokens into an AST tree.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class Parser {
    private ASTContext context;
    private Semantic sem;
    private Lexer lexer;

    private Token token;

    public Parser(String filename, String source, ASTContext context) {
        this.context = context;
        this.sem = new Semantic(context);

        this.lexer = new Lexer(filename, source);
        this.token = lexer.lex();
    }

    /**
     * @return the parsed expression
     */
    public Expression parseExpr() {
        List<Expression> sequence = new ArrayList<>();

        while (true) {
            Expression primary = parseExprPrimary();
            if (primary == null) {
                return null;
            }
            sequence.add(primary);

            if (!token.is(TokenType.OPERATOR)) break;
            sequence.add(parseExprOperator());
        }

        assert !sequence.isEmpty() : "Empty sequence?";
        if (sequence.size() == 1) {
            // Single element means just a simple expression, return it.
            return sequence.get(0);
        }

        return new SequenceExpr(sequence.toArray(new Expression[sequence.size()]));
    }

    /**
     * @return the parsed operator ref.
     */
    public Expression parseExprOperator() {
        SourceLocation loc = token.getLocation();
        Identifier name = parseIdentifier();
        return sem.handleIdentifier(loc, name);
    }

    /**
     * @return the parsed identifier
     */
    public Identifier parseIdentifier() {
        if (token.is(TokenType.IDENTIFIER) || token.is(TokenType.OPERATOR)) {
            Identifier res = context.getIdentifier(token.getText());
            consumeToken();
            return res;
        }

        errorAndExit(token.getLocation(), "expected identifier here");
        return null;
    }

    /**
     * @return the parsed expression
     */
    public Expression parseExprPrimary() {
        Expression result;

        switch (token.getKind()) {
            case INTEGER:
                result = sem.handleInteger(token.getLocation(), token.getText());
                consumeToken();
                break;
            case IDENTIFIER:
                result = sem.handleIdentifier(token.getLocation(), parseIdentifier());
                if (result == null) return null;
                break;
            case LPAREN:
                result = parseExprParen();
                if (result == null) return null;
                break;
            default:
                errorAndExit(token.getLocation(), "expected expression here");
                return null;
        }

        while (true) {
            SourceLocation loc = token.getLocation();

            // RESULT.field
            if (consumeIf(TokenType.DOT)) {
                parseToken(TokenType.IDENTIFIER, "expected field name here", loc, "because of this dot");

                Identifier name = context.getIdentifier(token.getText());
                result = new UnresolvedDotExpr(result, loc, token.getLocation(), name);
                consumeToken();

                continue;
            }

            // RESULT -> fun
            if (consumeIf(TokenType.ARROW)) {
                Expression fun = sem.handleIdentifier(token.getLocation(), parseIdentifier());
                result = new CallExpr(fun, result);
            }

            break;
        }

        return result;
    }

    /**
     * @return the parsed tuple expression
     */
    private Expression parseExprParen() {
        SourceLocation loc = consumeToken();
        List<Pair<Expression, Identifier>> exprs = new ArrayList<>();

        if (token.isNot(TokenType.RPAREN)) {
            do {
                Identifier fieldName = null;
                if (consumeIf(TokenType.DOT)) {
                    fieldName = parseIdentifier();
                    if (fieldName == null) return null;
                    if (!parseToken(TokenType.EQUAL, "expected '=' in tuple expression")) return null;
                }

                Expression subExpr = parseExpr();
                if (subExpr == null) return null;

                exprs.add(new Pair<>(subExpr, fieldName));
            } while (consumeIf(TokenType.COMMA));
        }

        SourceLocation rightLoc = token.getLocation();
        parseToken(TokenType.RPAREN, "expected ')' in parenthesis expression", loc, "to match this opening '('");

        return sem.handleTupleExpr(loc, exprs, rightLoc);
    }

    /**
     * Same as other parseToken but without extra notes.
     */
    private boolean parseToken(TokenType expected, String errMsg) {
        return parseToken(expected, errMsg, null, null);
    }

    /**
     * If the current token is of specified type, consume it. Otherwise, print an error and stop.
     */
    private boolean parseToken(TokenType expected, String errMsg, SourceLocation noteLoc, String noteMsg) {
        if (token.is(expected)) {
            consumeToken();
            return true;
        }

        error(token.getLocation(), errMsg);
        if (noteLoc != null && noteMsg != null) {
            note(noteLoc, noteMsg);
        }

        System.err.flush();
        System.exit(1);
        return false;
    }

    /**
     * Advances the current token and returns the location of the previous one.
     */
    private SourceLocation consumeToken() {
        Token old = token;
        assert token.isNot(TokenType.EOF) : "Lexing past EOF";
        token = lexer.lex();
        return old.getLocation();
    }

    /**
     * Consumes the token if of the specified type, otherwise returns false.
     */
    private boolean consumeIf(TokenType type) {
        if (token.isNot(type)) return false;
        consumeToken();
        return true;
    }

    private void note(SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.NOTE, message, loc);
    }

    private void warning(SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.WARNING, message, loc);
    }

    private void error(SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, message, loc);
    }

    private void errorAndExit(SourceLocation loc, String message) {
        Diagnostics.printMessage(Diagnostics.MessageKind.ERROR, message, loc);
        System.err.flush();
        System.exit(1);
    }
}
