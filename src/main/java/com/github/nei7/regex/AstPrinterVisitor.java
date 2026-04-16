package com.github.nei7.regex;

public class AstPrinterVisitor implements RegexNodeVisitor<String> {

    public String print(RegexNode node) {
        return node.accept(this);
    }

    @Override
    public String visit(RegexNode.Literal literal) {
        return literal.getValue();
    }

    @Override
    public String visit(RegexNode.Concat concat) {
        return "Concat(" + concat.getLeft().accept(this) + ", " + concat.getRight().accept(this) + ")";
    }

    @Override
    public String visit(RegexNode.Union union) {
        return "Union(" + union.getLeft().accept(this) + ", " + union.getRight().accept(this) + ")";
    }

    @Override
    public String visit(RegexNode.Star star) {
        return "Star(" + star.getInner().accept(this) + ")";
    }
}