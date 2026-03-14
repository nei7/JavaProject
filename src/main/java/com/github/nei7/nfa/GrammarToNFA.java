package com.github.nei7.nfa;

import com.github.nei7.model.*;
import java.util.*;

public class GrammarToNFA {

    public NFA convert(Grammar grammar) {
        NFA.State startState = new NFA.State(grammar.startSymbol().name());

        Set<NFA.State> acceptStates = new HashSet<>();
        List<NFA.Trans> transitions = new ArrayList<>();
        List<NFA.Eps> epsilons = new ArrayList<>();

        for (Production p : grammar.productions()) {
            NFA.State fromState = new NFA.State(p.left().name());
            List<Symbol> rhs = p.right();

            if (rhs.isEmpty()) {
                acceptStates.add(fromState);
            } else if (rhs.size() == 2 && rhs.get(0) instanceof Terminal term
                    && rhs.get(1) instanceof NonTerminal nextNt) {
                NFA.State toState = new NFA.State(nextNt.name());

                transitions.add(new NFA.Trans(fromState, term.value(), toState));
            } else if (rhs.size() == 1 && rhs.get(0) instanceof Terminal term) {

                NFA.State endState = new NFA.State("End");
                acceptStates.add(endState);
                transitions.add(new NFA.Trans(fromState, term.value(), endState));
            }
        }

        return new NFA(startState, new ArrayList<>(acceptStates), transitions, epsilons);
    }
}