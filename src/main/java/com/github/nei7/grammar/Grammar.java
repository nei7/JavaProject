package com.github.nei7.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Grammar {
    private final NonTerminal startSymbol;
    private final List<Production> productions;

    public Grammar(NonTerminal startSymbol, List<Production> productions) {
        this.startSymbol = startSymbol;
        this.productions = new ArrayList<>(productions);
    }

    public NonTerminal getStartSymbol() {
        return startSymbol;
    }

    public List<Production> getProductions() {
        return Collections.unmodifiableList(productions);
    }

    @Override
    public String toString() {
        return productions.stream()
                .collect(Collectors.groupingBy(p -> p.getLeft().getName()))
                .entrySet().stream()
                .map(entry -> {
                    String lhs = entry.getKey();
                    String rhs = entry.getValue().stream()
                            .map(p -> p.getRight().isEmpty() ? "ε"
                                    : p.getRight().stream()
                                            .map(Symbol::toString)
                                            .collect(Collectors.joining("")))
                            .collect(Collectors.joining(" | "));
                    return lhs + " -> " + rhs;
                })
                .collect(Collectors.joining("\n"));
    }
}