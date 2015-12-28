package nl.thijsmolendijk.swift_reference.parse;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.ASTContext;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.Node;
import nl.thijsmolendijk.swift_reference.ast.Stmt;
import nl.thijsmolendijk.swift_reference.ast.decl.*;
import nl.thijsmolendijk.swift_reference.ast.expr.*;
import nl.thijsmolendijk.swift_reference.ast.stmt.*;
import nl.thijsmolendijk.swift_reference.semantics.Semantic;
import nl.thijsmolendijk.swift_reference.type.FunctionType;
import nl.thijsmolendijk.swift_reference.type.TupleType;
import nl.thijsmolendijk.swift_reference.type.Type;
import nl.thijsmolendijk.swift_reference.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static nl.thijsmolendijk.swift_reference.Diagnostics.SourceLocation;
import static nl.thijsmolendijk.swift_reference.parse.Token.TokenType;


/**
 * Created by molenzwiebel on 16-12-15.
 */
public class Parser {
    private ASTContext context;
    private Semantic semantic;
    private Lexer lexer;

    private Token token;

    public Parser(String filename, String contents, ASTContext ctx) {
        this.context = ctx;
        this.semantic = new Semantic(ctx);
        this.lexer = new Lexer(filename, contents, ctx);

        this.token = lexer.lex();
    }

    public Identifier parseIdentifier(String message) {
        if (token.is(TokenType.IDENTIFIER) || token.is(TokenType.OPERATOR)) {
            Identifier res = context.getIdentifier(token.getText());
            consumeToken();
            return res;
        }

        error(token.getLocation(), message);
        return null;
    }

    public TranslationUnitDecl parseTranslationUnit() {
        TranslationUnitDecl result = new TranslationUnitDecl(context);
        List<Node> res = parseBraceItemList(true);
        if (res == null) return null;
        semantic.decl.handleEndOfTranslationUnit(result, res);
        return result;
    }

    public List<Node> parseBraceItemList(boolean isTopLvl) {
        List<Node> res = new ArrayList<>();

        while (token.isNot(TokenType.RIGHT_BRACE) && token.isNot(TokenType.EOF)) {
            switch (token.getKind()) {
                case SEMICOLON:
                case LEFT_BRACE:
                case KW_RETURN:
                case KW_IF:
                case KW_WHILE:
                    Stmt stmt = parseStmtOtherThanAssignment();
                    if (stmt == null) return null;
                    res.add(stmt);
                    break;
                case KW_LET:
                    res.add(parseDeclVar());
                    break;
                case KW_TYPEALIAS:
                    res.add(parseDeclTypeAlias());
                    break;
                case KW_ONEOF:
                    res.add(parseOneOf());
                    break;
                case KW_STRUCT:
                    res.add(parseDeclStruct());
                    break;
                case KW_FN:
                    res.add(parseDeclFunc());
                    break;
                default:
                    Expr ex = parseExpr("expected expression or statement");
                    if (ex == null) return null;

                    if (token.isNot(TokenType.EQUAL)) {
                        res.add(ex);
                        break;
                    }

                    SourceLocation eqLoc = consumeToken();
                    Expr right = parseExpr("expected expression in assignment");
                    if (right == null) return null;

                    res.add(new AssignStmt(ex, eqLoc, right));
            }
        }

        return res;
    }

    public Stmt parseStmtOtherThanAssignment() {
        switch (token.getKind()) {
            default:
                error(token.getLocation(), "expected statement");
                return null;
            case SEMICOLON: return new NoopStmt(consumeToken());
            case LEFT_BRACE: return parseStmtBrace();
            case KW_RETURN: return parseStmtReturn();
            case KW_IF: return parseStmtIf();
            case KW_WHILE: return parseStmtWhile();
        }
    }

    public BraceStmt parseStmtBrace() {
        if (token.isNot(TokenType.LEFT_BRACE)) {
            error(token.getLocation(), "expected '{' here");
            return null;
        }
        SourceLocation start = consumeToken();
        List<Node> res = parseBraceItemList(false);
        if (res == null) return null;

        SourceLocation end = token.getLocation();
        if (!parseToken(TokenType.RIGHT_BRACE, "expected '}' at end of brace expression", TokenType.RIGHT_BRACE)) {
            note(start, "to match this opening '{'");
            return null;
        }

        return new BraceStmt(start, end, res.toArray(new Node[res.size()]));
    }

    public Stmt parseStmtReturn() {
        SourceLocation loc = consumeToken();
        Expr val;
        if (isStartOfExpr(token)) {
            val = parseExpr("expected expression in 'return' statement");
            if (val == null) return null;
        } else {
            val = new TupleExpr(null, null, null, null, false);
        }

        return new ReturnStmt(loc, val);
    }

    public Stmt parseStmtIf() {
        SourceLocation ifLoc = consumeToken();

        Expr condition = parseSingleExpr("expected expression in 'if' condition");
        if (condition == null) return null;
        Stmt body = parseStmtBrace();
        if (body == null) return null;

        SourceLocation elseLoc = token.getLocation();
        Stmt elseBody = null;
        if (consumeIf(TokenType.KW_ELSE)) {
            if (token.is(TokenType.KW_IF)) {
                elseBody = parseStmtIf();
            } else {
                elseBody = parseStmtBrace();
            }
            if (elseBody == null) return null;
        } else {
            elseLoc = null;
        }

        return new IfStmt(ifLoc, elseLoc, semantic.expr.actOnCondition(condition), body, elseBody);
    }

    public Stmt parseStmtWhile() {
        throw new RuntimeException("TODO: While");
    }

    public Decl parseDeclVar() {
        SourceLocation varLoc = consumeToken();

        if (token.isNot(TokenType.IDENTIFIER)) {
            error(token.getLocation(), "expected identifier here");
            note(varLoc, "due to the 'let' here");
            return null;
        }

        String name = token.getText();
        consumeToken();

        Pair<Type, Expr> data = parseValueSpecifier(false);
        if (data == null) return null;

        VarDecl result = semantic.decl.actOnVarDecl(varLoc, name, data.getLeft(), data.getRight());
        if (result == null) return null;

        semantic.decl.addToScope(result);
        return result;
    }

    public Decl parseDeclTypeAlias() {
        SourceLocation loc = consumeToken();

        Identifier name = parseIdentifier("expected identifier in typealias");
        if (name == null) return null;

        if (!parseToken(TokenType.COLON, "expected ':' in typealias declaration", TokenType.COLON)) {
            return null;
        }

        Type type = parseType("expected type in var declaration");
        if (type == null) return null;

        return semantic.decl.actOnTypeAlias(loc, name, type);
    }

    public Decl parseOneOf() {
        SourceLocation loc = consumeToken();
        SourceLocation nameLoc = token.getLocation();

        Identifier name = parseIdentifier("expected identifier in oneof declaration");
        if (name == null) return null;

        TypeAliasDecl typeAliasDecl = semantic.decl.actOnTypeAlias(nameLoc, name, null);
        Type oneofType = parseOneofType(loc, typeAliasDecl);
        if (oneofType == null) return null;

        return typeAliasDecl;
    }

    public Decl parseDeclStruct() {
        SourceLocation start = consumeToken();
        Identifier name = parseIdentifier("expected identifier in struct def");
        if (name == null) return null;

        SourceLocation lbrace = token.getLocation();
        if (!parseToken(TokenType.LEFT_BRACE, "expected '{' in struct", TokenType.RIGHT_BRACE)) {
            return null;
        }

        Type tuple = parseTupleType(lbrace);
        if (tuple == null) return null;

        if (!parseToken(TokenType.RIGHT_BRACE, "expected '}' in struct", TokenType.RIGHT_BRACE)) {
            note(lbrace, "to match this opening '{'");
            return null;
        }

        return semantic.decl.actOnStructDecl(start, name, tuple);
    }

    public Decl parseDeclFunc() {
        SourceLocation start = consumeToken();

        Type receiverType = null;
        SourceLocation nameLoc = token.getLocation();
        Identifier name = parseIdentifier("expected identifier in func decl");

        if (name == null) return null;
        if (consumeIf(TokenType.DOUBLE_COLON)) {
            receiverType = semantic.type.actOnTypeName(nameLoc, name);

            name = parseIdentifier("expected name in func declaration");
            if (name == null) return null;
        }

        if (token.isNot(TokenType.LEFT_PAREN) && token.isNot(TokenType.LEFT_PAREN_SPACE)) {
            error(token.getLocation(), "expected '(' in argument list");
        }

        Type funTy = parseType("expected type here");
        if (funTy == null) return null;

        if (!(funTy instanceof FunctionType)) {
            funTy = semantic.type.actOnFunctionType(funTy, null, context.getUnitType());
        }

        if (receiverType != null) {
            TupleType.TupleElement thiz = new TupleType.TupleElement(context.getIdentifier("this"), receiverType, null);
            funTy = semantic.type.actOnFunctionType(
                    TupleType.get(new TupleType.TupleElement[]{thiz}, context),
                    null, funTy);
        }

        FuncExpr ex = semantic.expr.actOnFunExprStart(start, (FunctionType) funTy);
        if (token.is(TokenType.LEFT_BRACE)) {
            ex.setBody(parseStmtBrace());
            if (ex.getBody() == null) return null;
        }

        FuncDecl decl = new FuncDecl(name, funTy, ex, start);
        semantic.decl.addToScope(decl);
        return decl;
    }

    // Parses:
    // (':' type)? ('=' expr)?
    public Pair<Type, Expr> parseValueSpecifier(boolean singleInit) {
        if (token.isNot(TokenType.COLON) && token.isNot(TokenType.EQUAL)) {
            error(token.getLocation(), "expected a type of an initializer");
            return null;
        }

        Type ty = consumeIf(TokenType.COLON) ? parseType("expected type in var declaration") : null;
        Expr init = null;
        if (consumeIf(TokenType.EQUAL)) {
            init = singleInit ? parseSingleExpr("expected expression in value specifier") : parseExpr("expected expression in value specifier");
            if (init == null) {
                ty = context.getInt32Type(); // TODO: Error type.
            }
        }

        return new Pair<>(ty, init);
    }

    public Type parseType(String errMsg) {
        Type result;
        switch (token.getKind()) {
            case IDENTIFIER:
                result = semantic.type.actOnTypeName(token.getLocation(), context.getIdentifier(token.getText()));
                consumeToken();
                break;
            case KW_INT1_TYPE:
                result = context.getInt1Type();
                consumeToken();
                break;
            case KW_INT8_TYPE:
                result = context.getInt8Type();
                consumeToken();
                break;
            case KW_INT16_TYPE:
                result = context.getInt16Type();
                consumeToken();
                break;
            case KW_INT32_TYPE:
                result = context.getInt32Type();
                consumeToken();
                break;
            case KW_INT64_TYPE:
                result = context.getInt64Type();
                consumeToken();
                break;
            case LEFT_PAREN:
            case LEFT_PAREN_SPACE:
                SourceLocation leftLoc = consumeToken();
                result = parseTupleType(leftLoc);
                if (result == null) return null;

                if (!parseToken(TokenType.RIGHT_PAREN, "expected ')' at end of tuple type", TokenType.RIGHT_PAREN)) {
                    note(leftLoc, "to match this opening '('");
                    return null;
                }
                break;
            case KW_ONEOF:
                result = parseOneofType(consumeToken(), null);
                if (result == null) return null;
                break;
            default:
                error(token.getLocation(), errMsg);
                return null;
        }

        assert result != null;

        while (true) {
            SourceLocation tokLoc = token.getLocation();
            if (consumeIf(TokenType.ARROW)) {
                Type secondHalf = parseType("expected type in result of function type");
                if (secondHalf == null) return null;
                result = semantic.type.actOnFunctionType(result, tokLoc, secondHalf);

                continue;
            }

            if (consumeIf(TokenType.LEFT_SQUARE)) {
                Expr size = null;
                if (token.isNot(TokenType.RIGHT_SQUARE) && (size = parseSingleExpr("expected expression for array type size")) == null) {
                    return null;
                }

                SourceLocation rloc = token.getLocation();
                if (!parseToken(TokenType.RIGHT_SQUARE, "expected ']' in array type", TokenType.UNKNOWN)) {
                    note(tokLoc, "to match this '['");
                    return null;
                }

                result = semantic.type.actOnArrayType(result, size, tokLoc, rloc);

                continue;
            }

            break;
        }

        return result;
    }

    public Type parseTupleType(SourceLocation leftLoc) {
        List<TupleType.TupleElement> elements = new ArrayList<>();
        if (token.isNot(TokenType.RIGHT_PAREN) && token.isNot(TokenType.RIGHT_BRACE)) {
            do {
                Identifier name = null;
                if (token.is(TokenType.IDENTIFIER)) {
                    name = parseIdentifier("expected identifier here");
                }
                Pair<Type, Expr> valueSpecifier = parseValueSpecifier(true);
                elements.add(new TupleType.TupleElement(name, valueSpecifier.getLeft(), valueSpecifier.getRight()));
            } while (consumeIf(TokenType.COMMA));
        }

        SourceLocation rightLoc = token.getLocation();
        return semantic.type.actOnTupleType(leftLoc, elements, rightLoc);
    }

    public Type parseOneofType(SourceLocation begin, TypeAliasDecl node) {
        if (!parseToken(TokenType.LEFT_BRACE, "expected '{' in oneof type", TokenType.LEFT_BRACE)) {
            return null;
        }

        List<Pair<String, Type>> elements = new ArrayList<>();
        while (token.is(TokenType.IDENTIFIER)) {
            String name = token.getText();
            consumeToken();

            Type ty = parseType("expected type while parsing oneof element");
            if (ty == null) {
                skipUntil(TokenType.RIGHT_BRACE);
                return null;
            }

            elements.add(new Pair<>(name, ty));
            if (!consumeIf(TokenType.COMMA)) break;
        }

        parseToken(TokenType.RIGHT_BRACE, "expected '}' at end of oneof", TokenType.RIGHT_BRACE);
        return semantic.type.actOnOneofType(begin, elements, node);
    }

    public Expr parseExprPrimary(String errMsg) {
        Expr result;

        switch (token.getKind()) {
            case NUMBER:
                result = semantic.expr.actOnNumber(token.getText(), token.getLocation());
                consumeToken();
                break;
            case IDENTIFIER:
                result = parseExprIdentifier();
                if (result == null) return null;
                break;
            case COLON:
                SourceLocation colonLoc = consumeToken();
                SourceLocation nameLoc = token.getLocation();
                Identifier name = parseIdentifier("expected identifier after ':' expression");
                if (name == null) return null;
                result = new UnresolvedMemberExpr(colonLoc, nameLoc, name);
                break;
            case LEFT_PAREN:
            case LEFT_PAREN_SPACE:
                result = parseExprParen();
                if (result == null) return null;
                break;
            default:
                error(token.getLocation(), errMsg);
                return null;
        }

        while (true) {
            SourceLocation loc = token.getLocation();

            // RESULT.field
            if (consumeIf(TokenType.PERIOD)) {
                if (token.isNot(TokenType.IDENTIFIER)) {
                    error(token.getLocation(), "expected field name here");
                    note(loc, "because of this dot");
                    return null;
                }

                Identifier name = context.getIdentifier(token.getText());
                result = new UnresolvedDotExpr(result, loc, name, token.getLocation());
                consumeToken();

                continue;
            }

            // RESULT(args)
            if (token.is(TokenType.LEFT_PAREN)) {
                Expr args = parseExprParen();
                if (args == null) return null;
                result = new CallExpr(result, args);
                continue;
            }

            break;
        }

        return result;
    }

    public Expr parseExprIdentifier() {
        SourceLocation loc = token.getLocation();
        Identifier name = parseIdentifier("expected identifier here");
        if (name == null) return null;

        if (token.isNot(TokenType.DOUBLE_COLON)) {
            return semantic.expr.actOnIdentifierExpr(name, loc);
        }

        SourceLocation colonLoc = consumeToken();
        SourceLocation loc2 = token.getLocation();

        Identifier name2 = parseIdentifier("expected identifier after '" + name.getValue() + "::' expression");
        if (name2 == null) return null;
        return semantic.expr.actOnScopedIdentifierExpr(name, loc, colonLoc, name2, loc2);
    }

    public Expr parseExprParen() {
        SourceLocation loc = consumeToken();
        List<Pair<Expr, Identifier>> exprs = new ArrayList<>();

        if (token.isNot(TokenType.RIGHT_PAREN)) {
            do {
                Identifier fieldName = null;
                if (consumeIf(TokenType.PERIOD)) {
                    fieldName = parseIdentifier("expected field specifier name in tuple expression");
                    if (fieldName == null) return null;
                    if (!parseToken(TokenType.EQUAL, "expected '=' in tuple expression", TokenType.EQUAL)) return null;
                }

                Expr subExpr = parseSingleExpr("expected expression in parenthesis");
                if (subExpr == null) return null;

                exprs.add(new Pair<>(subExpr, fieldName));
            } while (consumeIf(TokenType.COMMA));
        }

        SourceLocation rightLoc = token.getLocation();
        if (!parseToken(TokenType.RIGHT_PAREN, "expected ')' in parenthesis expression", TokenType.RIGHT_PAREN)) {
            note(loc, "to match this opening '('");
            return null;
        }

        return semantic.expr.actOnTupleExpr(loc, exprs, rightLoc);
    }

    public Expr parseExprOperator() {
        SourceLocation loc = token.getLocation();
        Identifier name = parseIdentifier("expected operator here");
        return semantic.expr.actOnIdentifierExpr(name, loc);
    }

    public Expr parseExpr(String errMsg) {
        List<Expr> sequence = new ArrayList<>();

        while (true) {
            Expr primary = parseExprPrimary(errMsg);
            if (primary == null) {
                return null;
            }
            sequence.add(primary);

            if (!token.is(TokenType.OPERATOR)) break;
            sequence.add(parseExprOperator());
            errMsg = "expected expression after operator";
        }

        assert !sequence.isEmpty() : "Empty sequence?";
        if (sequence.size() == 1) {
            return sequence.get(0);
        }

        return new SequenceExpr(sequence.toArray(new Expr[sequence.size()]));
    }

    public Expr parseSingleExpr(String errMsg) {
        Expr result = parseExpr(errMsg);
        if (result == null) return null;

        if (isStartOfExpr(token)) {
            error(token.getLocation(), "expected only a single expression");
            note(lexer.peekToken().getLocation(), "this starts a new expression");
            do {
                Expr extra = parseExpr(errMsg);
                if (extra == null) return null;
            } while (isStartOfExpr(lexer.peekToken()));
        }

        return result;
    }

    /**
     * @return if the provided token is the start of a new expression
     */
    private boolean isStartOfExpr(Token next) {
        return next.is(TokenType.NUMBER) || next.is(TokenType.COLON) || next.is(TokenType.LEFT_PAREN_SPACE) || next.is(TokenType.IDENTIFIER) || next.is(TokenType.OPERATOR);
    }

    /**
     * If the current token is of specified type, consume it. Otherwise, print an error and skip to the specified token.
     */
    private boolean parseToken(TokenType expected, String errMsg, TokenType skipTo) {
        if (token.is(expected)) {
            consumeToken();
            return true;
        }

        error(token.getLocation(), errMsg);
        skipUntil(skipTo);

        if (expected == skipTo && token.is(skipTo)) {
            consumeToken();
        }
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
     * Skips until the specified token. Does not consume that token.
     */
    private void skipUntil(TokenType type) {
        if (type == TokenType.UNKNOWN) return;

        while (token.isNot(TokenType.EOF) && token.isNot(type)) {
            consumeToken();
        }
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
}
