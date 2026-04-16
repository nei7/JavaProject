package com.github.nei7.grammar;

import java.util.ArrayList;
import java.util.List;

public class GrammarBuilder {
    private NonTerminal startSymbol;
    private final List<Production> productions = new ArrayList<>();

    public GrammarBuilder setStartSymbol(NonTerminal startSymbol) {
        this.startSymbol = startSymbol;
        return this;
    }

    public GrammarBuilder addProduction(NonTerminal left, List<Symbol> right) {
        this.productions.add(new Production(left, right));
        return this;
    }

    public GrammarBuilder addProduction(NonTerminal left, Symbol... right) {
        this.productions.add(new Production(left, List.of(right)));
        return this;
    }

    public Grammar build() {
        if (startSymbol == null) {
            throw new IllegalStateException("Zanim zbudujesz gramatykę, musisz ustawić Start Symbol!");
        }
        return new Grammar(startSymbol, productions);
    }
}