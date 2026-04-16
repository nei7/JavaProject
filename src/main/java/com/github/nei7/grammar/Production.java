package com.github.nei7.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Production {
    private final NonTerminal left;
    private final List<Symbol> right;

    public Production(NonTerminal left, List<Symbol> right) {
        this.left = left;
        this.right = new ArrayList<>(right);
    }

    public NonTerminal getLeft() {
        return left;
    }

    public List<Symbol> getRight() {
        // readonly
        return Collections.unmodifiableList(right);
    }

    @Override
    public String toString() {
        String rightSide = right.stream()
                .map(Symbol::toString)
                .collect(Collectors.joining(""));

        if (rightSide.isEmpty()) {
            rightSide = "ε";
        }

        return left + " -> " + rightSide;
    }
}