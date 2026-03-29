package com.github.nei7.grammar;

import java.util.ArrayList;
import java.util.List;

import com.github.nei7.fsm.NFA;

public class NFAToGrammar {

    public Grammar convert(NFA nfa) {
        List<Production> productions = new ArrayList<>();

        for (NFA.Trans t : nfa.transitions()) {
            NonTerminal ntFrom = new NonTerminal(t.from().name());
            List<Symbol> rhs = List.of(
                    new Terminal(t.symbol()),
                    new NonTerminal(t.to().name()));
            productions.add(new Production(ntFrom, rhs));
        }

        for (NFA.Eps e : nfa.epsilons()) {
            NonTerminal ntFrom = new NonTerminal(e.from().name());
            List<Symbol> rhs = List.of(new NonTerminal(e.to().name()));
            productions.add(new Production(ntFrom, rhs));
        }

        for (NFA.State state : nfa.acceptStates()) {
            NonTerminal nt = new NonTerminal(state.name());
            productions.add(new Production(nt, List.of()));
        }

        return new Grammar(new NonTerminal(nfa.start().name()), productions);
    }
}