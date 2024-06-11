package com.tme.di.parser.ast.expr;

import com.tme.di.parser.ast.Literal;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class LiteralColumnExpr extends ColumnExpr {

    private Literal literal;

    protected LiteralColumnExpr(Literal literal) {
        super(ExprType.LITERAL);
        this.literal = literal;
    }

    public Literal getLiteral() {
        return literal;
    }

}
