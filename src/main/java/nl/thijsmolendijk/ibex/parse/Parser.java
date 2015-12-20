package nl.thijsmolendijk.ibex.parse;

import nl.thijsmolendijk.ibex.ASTContext;
import nl.thijsmolendijk.ibex.Semantic;
import nl.thijsmolendijk.ibex.ast.Expression;
import nl.thijsmolendijk.ibex.ast.Node;
import nl.thijsmolendijk.ibex.ast.Statement;
import nl.thijsmolendijk.ibex.ast.expr.*;
import nl.thijsmolendijk.ibex.ast.stmt.*;
import nl.thijsmolendijk.ibex.scoping.Scope;
import nl.thijsmolendijk.ibex.type.FunctionType;
import nl.thijsmolendijk.ibex.type.TupleType;
import nl.thijsmolendijk.ibex.type.Type;
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
     * @return the parsed translation unit
     */
    public TranslationUnit parseTranslationUnit() {
        TranslationUnit result = new TranslationUnit(context);
        SourceLocation start = token.getLocation();

        List<Node> contents = parseBrace();
        SourceLocation end = token.getLocation();

        sem.handleEndOfUnit(result, start, end, contents);

        return result;
    }

    /**
     * Parses a list of nodes in between '{' and '}' such that '{' node* '}'.
     */
    private List<Node> parseBrace() {
        sem.scope = new Scope(sem);
        List<Node> ret = new ArrayList<>();

        while (token.isNot(TokenType.RBRACE) && token.isNot(TokenType.EOF)) {
            switch (token.getKind()) {
                case LBRACE:
                case KW_RETURN:
                case KW_IF:
                    Statement stmt = parseStatement();
                    ret.add(stmt);
                    break;
                case KW_LET:
                    ret.add(parseVarDecl());
                    break;
                case KW_TYPE:
                    ret.add(parseTypeDecl());
                    break;
                case KW_FN:
                    ret.add(parseFnDecl());
                    break;
                default: {
                    Expression ex = parseExpr();

                    if (token.isNot(TokenType.EQUAL)) {
                        ret.add(ex);
                        break;
                    }

                    SourceLocation eqLoc = consumeToken();
                    Expression right = parseExpr();
                    if (right == null) {
                        sem.scope.end();
                        return null;
                    }

                    ret.add(new AssignExpr(ex, eqLoc, right));
                }
            }
        }

        sem.scope.end();
        return ret;
    }

    /**
     * @return the parsed var decl
     */
    public VarDecl parseVarDecl() {
        SourceLocation varLoc = consumeToken();

        String name = token.getText();
        parseToken(TokenType.IDENTIFIER, "expected identifier here", varLoc, "due to the 'let' here");

        Type givenType = null;
        if (consumeIf(TokenType.COLON)) {
            givenType = parseType();
        }

        Expression init = null;
        if (consumeIf(TokenType.EQUAL)) {
            init = parseExpr();
        }

        VarDecl result = sem.handleVarDecl(varLoc, context.getIdentifier(name), givenType, init);
        sem.addToScope(result);
        return result;
    }

    /**
     * @return the parsed type decl
     */
    public TypeDecl parseTypeDecl() {
        SourceLocation loc = consumeToken();

        Identifier name = parseIdentifier();
        parseToken(TokenType.COLON, "expected ':' in type declaration", loc, "due to the 'type' here");

        Type type = parseType();

        return sem.handleTypeDecl(loc, name, type);
    }

    /**
     * @return the parsed function decl
     */
    public FnDecl parseFnDecl() {
        SourceLocation start = consumeToken();

        Identifier name = parseIdentifier();
        if (token.isNot(TokenType.LPAREN)) {
            errorAndExit(token.getLocation(), "expected '(' in function declaration");
        }

        Type funType = parseType();
        if (!(funType instanceof FunctionType)) {
            funType = sem.handleFunctionType(funType, null, context.getUnit());
        }

        sem.scope = new Scope(sem);

        FuncExpr ex = sem.handleFunc(start, (FunctionType) funType);
        if (token.is(TokenType.LBRACE)) {
            ex.setBody(parseBraceStmt());
        }

        sem.scope.end();

        FnDecl decl = new FnDecl(name, funType, ex, start);
        sem.addToScope(decl);
        return decl;
    }

    /**
     * @return the parsed statement
     */
    public Statement parseStatement() {
        switch (token.getKind()) {
            default:
                errorAndExit(token.getLocation(), "expected statement here");
                return null;
            case KW_RETURN: return parseReturnStmt();
            case KW_IF: return parseIfStmt();
        }
    }

    /**
     * @return the parsed brace statement
     */
    public BraceStmt parseBraceStmt() {
        if (token.isNot(TokenType.LBRACE)) {
            errorAndExit(token.getLocation(), "expected '{' here");
            return null;
        }

        SourceLocation start = consumeToken();
        List<Node> res = parseBrace();

        SourceLocation end = token.getLocation();
        parseToken(TokenType.RBRACE, "expected '}' at end of brace expression", start, "to match this opening '{'");

        return new BraceStmt(start, end, res.toArray(new Node[res.size()]));
    }

    /**
     * @return the parsed return statement
     */
    public Statement parseReturnStmt() {
        SourceLocation loc = consumeToken();

        Expression val = isStartOfExpr(token) ? parseExpr() : new TupleExpr(loc, loc, new Expression[0], new Identifier[0]);
        return new ReturnStmt(loc, val);
    }

    /**
     * @return the parsed if statement
     */
    public Statement parseIfStmt() {
        SourceLocation ifLoc = consumeToken();
        Expression condition = parseExpr();

        Statement body = parseBraceStmt();

        Statement elseBody = null;
        if (consumeIf(TokenType.KW_ELSE)) {
            elseBody = token.is(TokenType.KW_IF) ? parseIfStmt() : parseBraceStmt();
        }

        return new IfStmt(ifLoc, condition, body, elseBody);
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
     * @return the parsed type
     */
    public Type parseType() {
        Type result;
        switch (token.getKind()) {
            case IDENTIFIER:
                result = sem.handleTypeName(token.getLocation(), context.getIdentifier(token.getText()));
                consumeToken();
                break;
            case LPAREN:
                SourceLocation leftLoc = consumeToken();
                result = parseTupleType(leftLoc);
                parseToken(TokenType.RPAREN, "expected ')' at end of tuple type", leftLoc, "to match this opening '('");
                break;
            default:
                errorAndExit(token.getLocation(), "expected type here");
                return null;
        }

        assert result != null;

        while (true) {
            SourceLocation tokLoc = token.getLocation();
            if (consumeIf(TokenType.ARROW)) {
                Type secondHalf = parseType();
                if (secondHalf == null) return null;
                result = sem.handleFunctionType(result, tokLoc, secondHalf);

                continue;
            }

            if (consumeIf(TokenType.LBRACKET)) {
                Expression size = null;
                if (token.isNot(TokenType.RBRACKET)) {
                    size = parseExpr();
                }

                if (size != null && !(size instanceof IntegerLiteralExpr)) {
                    errorAndExit(size.getLocation(), "expected size constant here");
                }

                parseToken(TokenType.RBRACKET, "expected ']' in array type", tokLoc, "to match this opening '['");

                result = sem.handleArrayType(result, size);
                continue;
            }

            break;
        }

        return result;
    }

    /**
     * @return the parsed tuple type
     */
    public Type parseTupleType(SourceLocation leftLoc) {
        List<TupleType.TupleElement> elements = new ArrayList<>();
        if (token.isNot(TokenType.RPAREN)) {
            do {
                Identifier name = null;
                if (token.is(TokenType.IDENTIFIER) && lexer.peekToken().is(TokenType.COLON)) {
                    name = parseIdentifier();
                    parseToken(TokenType.COLON, "expected colon after identifier");
                }

                Type type = parseType();
                elements.add(new TupleType.TupleElement(name, type));
            } while (consumeIf(TokenType.COMMA));
        }

        SourceLocation rightLoc = token.getLocation();
        return sem.handleTupleType(leftLoc, elements, rightLoc);
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

    /**
     * @return if the specified token signals the start of an expression
     */
    private boolean isStartOfExpr(Token next) {
        return next.is(TokenType.INTEGER) || next.is(TokenType.LPAREN) || next.is(TokenType.IDENTIFIER) || next.is(TokenType.OPERATOR);
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
