package com.github.nei7.solver;

import com.github.nei7.model.*;
import com.github.nei7.nfa.NFA;

import java.util.*;

public class RegexToGrammar {

    public Grammar convert(NFA nfa) {
        List<Production> productions = new ArrayList<>();
        Set<NFA.State> reachable = new HashSet<>();
        Queue<NFA.State> queue = new LinkedList<>();

        reachable.add(nfa.start());
        queue.add(nfa.start());

        while (!queue.isEmpty()) {
            NFA.State curr = queue.poll();

            Set<NFA.State> closure = getEpsilonClosure(curr, nfa.epsilons());
            Set<List<Symbol>> uniqueRhs = new LinkedHashSet<>();

            boolean isAccepting = closure.stream().anyMatch(s -> nfa.acceptStates().contains(s));
            if (isAccepting) {
                uniqueRhs.add(List.of());
            }

            for (NFA.State cState : closure) {
                for (NFA.Trans t : nfa.transitions()) {
                    if (t.from().equals(cState)) {
                        NonTerminal toNt = new NonTerminal(t.to().name());
                        uniqueRhs.add(List.of(new Terminal(t.symbol()), toNt));

                        if (!reachable.contains(t.to())) {
                            reachable.add(t.to());
                            queue.add(t.to());
                        }
                    }
                }
            }

            NonTerminal currNt = new NonTerminal(curr.name());
            for (List<Symbol> rhs : uniqueRhs) {
                productions.add(new Production(currNt, rhs));
            }
        }

        return new Grammar(new NonTerminal(nfa.start().name()), productions);
    }

    private Set<NFA.State> getEpsilonClosure(NFA.State start, List<NFA.Eps> epsilons) {
        Set<NFA.State> closure = new HashSet<>();
        Queue<NFA.State> queue = new LinkedList<>();
        closure.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            NFA.State curr = queue.poll();
            for (NFA.Eps e : epsilons) {
                if (e.from().equals(curr) && !closure.contains(e.to())) {
                    closure.add(e.to());
                    queue.add(e.to());
                }
            }
        }
        return closure;
    }
}