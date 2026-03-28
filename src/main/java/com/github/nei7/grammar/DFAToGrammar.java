
package com.github.nei7.grammar;

import java.util.ArrayList;
import java.util.List;

import com.github.nei7.fsm.DFA;

public class DFAToGrammar {

    public Grammar convert(DFA dfa) {
        List<Production> productions = new ArrayList<>();

        for (DFA.State state : dfa.allStates()) {
            NonTerminal nt = new NonTerminal(state.name());

            // A → aB
            for (DFA.Trans t : dfa.transitions()) {
                if (t.from().equals(state)) {
                    List<Symbol> rhs = List.of(new Terminal(t.symbol()), new NonTerminal(t.to().name()));
                    productions.add(new Production(nt, rhs));
                }
            }

            // A → ε (stan akceptujący)
            if (dfa.acceptStates().contains(state)) {
                productions.add(new Production(nt, List.of()));
            }
        }

        return new Grammar(new NonTerminal(dfa.start().name()), productions);
    }
}
