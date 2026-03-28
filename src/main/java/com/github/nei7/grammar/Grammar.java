package com.github.nei7.grammar;

import java.util.List;
import java.util.stream.Collectors;

public record Grammar(NonTerminal startSymbol, List<Production> productions) {

    @Override
    public String toString() {
        return productions.stream()
                .collect(Collectors.groupingBy(p -> p.left().name()))
                .entrySet().stream()
                .map(entry -> {
                    String lhs = entry.getKey();
                    String rhs = entry.getValue().stream()
                            .map(p -> p.right().isEmpty() ? "ε"
                                    : p.right().stream()
                                            .map(Symbol::toString)
                                            .collect(Collectors.joining("")))
                            .collect(Collectors.joining(" | "));
                    return lhs + " -> " + rhs;
                })
                .collect(Collectors.joining("\n"));
    }
}