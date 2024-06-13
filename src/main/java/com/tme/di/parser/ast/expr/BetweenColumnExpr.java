package com.tme.di.parser.ast.expr;
import lombok.EqualsAndHashCode;


// columnExpr NOT? BETWEEN columnExpr AND columnExpr 
@EqualsAndHashCode(callSuper=true)
public class BetweenColumnExpr extends ColumnExpr {
    private Boolean not;
    private ColumnExpr column;
    private ColumnExpr left;
    private ColumnExpr right;

    protected BetweenColumnExpr(Boolean not, ColumnExpr column, ColumnExpr left, ColumnExpr right) {
        super(ExprType.BETWEEN);
        this.not = not;
        this.column = column;
        this.left = left;
        this.right = right;
    }

    public Boolean getNot() {
        return not;
    }

    public ColumnExpr getColumn() {
        return column;
    }

    public ColumnExpr getLeft() {
        return left;
    }

    public ColumnExpr getRight() {
        return right;
    }
}
