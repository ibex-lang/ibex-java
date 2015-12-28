package nl.thijsmolendijk.swift_reference.semantics;

import nl.thijsmolendijk.swift_reference.Diagnostics;
import nl.thijsmolendijk.swift_reference.ast.Expr;
import nl.thijsmolendijk.swift_reference.ast.decl.TypeAliasDecl;
import nl.thijsmolendijk.swift_reference.ast.expr.Identifier;
import nl.thijsmolendijk.swift_reference.type.FunctionType;
import nl.thijsmolendijk.swift_reference.type.OneOfType;
import nl.thijsmolendijk.swift_reference.type.TupleType;
import nl.thijsmolendijk.swift_reference.type.Type;
import nl.thijsmolendijk.swift_reference.util.Pair;

import java.util.List;

/**
 * Created by molenzwiebel on 17-12-15.
 */
public class SemanticType extends BaseSemantic {
    public SemanticType(Semantic semantic) {
        super(semantic);
    }

    public Type actOnTypeName(Diagnostics.SourceLocation loc, Identifier ident) {
        return semantic.decl.lookupTypeName(ident).getAliasType();
    }

    public Type actOnFunctionType(Type left, Diagnostics.SourceLocation arrowLoc, Type right) {
        return FunctionType.create(left, right, semantic.context);
    }

    public Type actOnArrayType(Type element, Expr size, Diagnostics.SourceLocation lbracket, Diagnostics.SourceLocation rbracket) {
        //TODO
        return null;
    }

    public Type actOnTupleType(Diagnostics.SourceLocation lloc, List<TupleType.TupleElement> elems, Diagnostics.SourceLocation rloc) {
        return TupleType.get(elems.toArray(new TupleType.TupleElement[elems.size()]), semantic.context);
    }

    public OneOfType actOnOneofType(Diagnostics.SourceLocation loc, List<Pair<String, Type>> elements, TypeAliasDecl name) {
        //TODO
        return null;
    }
}
