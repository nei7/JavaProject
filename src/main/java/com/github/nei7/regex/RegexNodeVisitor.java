package com.github.nei7.regex;

public interface RegexNodeVisitor<T> {
    T visit(RegexNode.Literal literal);

    T visit(RegexNode.Union union);

    T visit(RegexNode.Concat concat);

    T visit(RegexNode.Star star);
}