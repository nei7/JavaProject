package com.github.nei7.model;

import java.util.List;

public record Production(NonTerminal left, List<Symbol> right) {

    @Override
    public String toString() {
        String rightSide = String.join("", right.stream().map(Symbol::toString).toList());

        if (rightSide.isEmpty()) {
            rightSide = "ε";
        }

        return left + " -> " + rightSide;
    }
}