## Syntax:

**translation-unit**:

    translation-unit ::= (<import-decl>)* (<toplevel-decl>)*

**import-decl**:

    import-decl ::= 'import' <string-literal>

**toplevel-decl**:

    toplevel-decl ::= <type-decl>
                  ::= <fun-decl>
                  ::= <var-decl>

**type-decl**:

    type-decl ::= <typealias-decl>
              ::= <class-decl>
              ::= <struct-decl>
              ::= <oneof-decl>

**typealias-decl**:

    typealias-decl ::= 'typealias' <ident> ':' <type>

**class-decl**:

    class-decl ::= 'class' <ident> ('::' <ident>)? '{' <class-body-decl> '}'

**class-body-decl**:

    class-body-decl ::= <fun-decl>
                    ::= <var-decl>
                    ::= <type-decl>

**struct-decl**:

    struct-decl ::= 'struct' <ident> '{' <var-decl>* '}'

**oneof-decl**:

    oneof-decl ::= 'oneof' <ident> '{' <oneof-element-list> '}'

**oneof-element-list**:

    oneof-element-list ::= <oneof-element> (',' <oneof-element>)*

**oneof-element**:

    oneof-element ::= <ident> <tuple-type>?

**type**:

    type ::= <builtin-type>
         ::= <user-type>
         ::= <array-type>
         ::= <function-type>
         ::= <tuple-type>

**builtin-type**:

    builtin-type ::= '__builtin_int8'
                 ::= '__builtin_int16'
                 ::= '__builtin_int32'
                 ::= '__builtin_int64'

**user-type**:

    user-type ::= <ident>

**array-type**:

    array-type ::= <type> '[' <int-literal>? ']'

**function-type**:

    function-type ::= <tuple-type> '->' <tuple-type>

**tuple-type**:

    tuple-type ::= '(' (<tuple-type-element> (',' <tuple-type-element>)*)? ')'

**tuple-type-element**:

    tuple-type-element ::= (<ident> ':')? <type>

**var-decl**:

    var-decl ::= 'var' <ident> (':' <type>)? ('=' <expression>)?
             ::= 'val' <ident> (':' <type>)? ('=' <expression>)?
             ::= 'let' <ident> (':' <type>)? ('=' <expression>)?

**fun-decl**:

    fun-decl ::= 'fun' <ident> <function-type> '{' <body> '}'
             ::= 'fun' <ident> <function-type> '=' <expression>
             ::= 'fun' <ident> '(' (<tuple-type-element> (',' <tuple-type-element>)*)? ')' '{' <body> '}'
             ::= 'fun' <ident> '(' (<tuple-type-element> (',' <tuple-type-element>)*)? ')' '=' <expression>

**body**:

    body ::= (<statement> | <expression>)*

**statement**:

    statement ::= <var-decl>
              ::= <for-stmt>
              ::= <if-stmt>
              ::= 'return' <expression>?

**for-stmt**:

    for-stmt ::= 'for' <expression>? ',' <expression> ',' <expression> '{' <body> '}'
             ::= 'for' <ident> 'in' <expression> '{' <body> '}'
             ::= 'for' <expression> '{' <body> '}'

**if-stmt**:

    if-stmt ::= 'if' <expression> '{' <body> '}'
            ::= 'if' <expression> '{' <body> '}' 'else' '{' <body> '}'
            ::= 'if' <expression> '{' <body> '}' 'else' <if-stmt>

**expression**:

    expression ::= <literal>
               ::= <ident>
               ::= <expression> '.' <ident>
               ::= <expression> <tuple-literal>
               ::= <expression> '[' <expression> ']'
               ::= <expression> '=' <expression>
               ::= <expression> <operator> <expression>
               ::= <expression> '?' <expression> ':' <expression>

**literal**:

    literal ::= <int-literal>
            ::= <string-literal>
            ::= <tuple-literal>
            ::= 'true'
            ::= 'false'

**tuple-literal**:

    tuple-literal ::= '(' (<tuple-literal-element> (',' <tuple-literal-element>)*)? ')'

**tuple-literal-element**:

    tuple-literal-element ::= (<ident> ':')? <expression>

**int-literal**:

    int-literal ::= [0-9]+

**string-literal**:

    string-literal ::= '"' .*? '"'
