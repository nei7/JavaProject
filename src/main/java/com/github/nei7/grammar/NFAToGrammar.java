package com.github.nei7.grammar;

import com.github.nei7.fsm.NFA;

public class NFAToGrammar {

    public Grammar convert(NFA nfa) {
        GrammarBuilder builder = new GrammarBuilder();

        builder.setStartSymbol(new NonTerminal(nfa.start().name()));

        for (NFA.Trans t : nfa.transitions()) {
            builder.addProduction(
                    new NonTerminal(t.from().name()),
                    new Terminal(t.symbol()),
                    new NonTerminal(t.to().name()));
        }

        for (NFA.Eps e : nfa.epsilons()) {
            builder.addProduction(
                    new NonTerminal(e.from().name()),
                    new NonTerminal(e.to().name()));
        }

        for (NFA.State state : nfa.acceptStates()) {
            builder.addProduction(new NonTerminal(state.name()));
        }

        return builder.build();
    }
}