package com.github.nei7.fsm;

import java.util.*;
import java.util.stream.Collectors;

public class NFAToDFA {
    private int nameCounter = 0;

    public DFA convert(NFA nfa) {
        List<DFA.Trans> dfaTransitions = new ArrayList<>();

        Map<Set<NFA.State>, DFA.State> stateMap = new LinkedHashMap<>();
        Set<DFA.State> acceptStates = new HashSet<>();

        Queue<Set<NFA.State>> queue = new LinkedList<>();

        Set<NFA.State> startSet = epsilonClosure(Set.of(nfa.start()), nfa.epsilons());
        DFA.State dfaStart = new DFA.State(stateName());
        stateMap.put(startSet, dfaStart);
        queue.add(startSet);

        if (isAccepting(startSet, nfa.acceptStates())) {
            acceptStates.add(dfaStart);
        }

        while (!queue.isEmpty()) {
            Set<NFA.State> current = queue.poll();
            DFA.State dfaCurrent = stateMap.get(current);

            Set<String> symbols = current.stream()
                    .flatMap(s -> nfa.transitions().stream()
                            .filter(t -> t.from().equals(s))
                            .map(NFA.Trans::symbol))
                    .collect(Collectors.toSet());

            for (String symbol : symbols) {
                Set<NFA.State> moved = move(current, symbol, nfa.transitions());
                Set<NFA.State> nextSet = epsilonClosure(moved, nfa.epsilons());

                if (nextSet.isEmpty())
                    continue;

                if (!stateMap.containsKey(nextSet)) {
                    DFA.State newState = new DFA.State(stateName());
                    stateMap.put(nextSet, newState);
                    queue.add(nextSet);

                    if (isAccepting(nextSet, nfa.acceptStates())) {
                        acceptStates.add(newState);
                    }
                }

                DFA.State dfaNext = stateMap.get(nextSet);
                dfaTransitions.add(new DFA.Trans(dfaCurrent, symbol, dfaNext));
            }
        }

        return new DFA(dfaStart, new ArrayList<>(acceptStates), dfaTransitions);
    }

    private String stateName() {
        this.nameCounter++;
        if (this.nameCounter < 26) {
            return String.valueOf((char) ('A' + this.nameCounter));
        }
        return String.valueOf((char) ('A' + (this.nameCounter / 26) - 1)) +
                (char) ('A' + (this.nameCounter % 26));
    }

    private Set<NFA.State> epsilonClosure(Set<NFA.State> states, List<NFA.Eps> epsilons) {
        Set<NFA.State> closure = new HashSet<>(states);
        Queue<NFA.State> queue = new LinkedList<>(states);

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

    private Set<NFA.State> move(Set<NFA.State> states, String symbol, List<NFA.Trans> transitions) {
        return states.stream()
                .flatMap(s -> transitions.stream()
                        .filter(t -> t.from().equals(s) && t.symbol().equals(symbol))
                        .map(NFA.Trans::to))
                .collect(Collectors.toSet());
    }

    private boolean isAccepting(Set<NFA.State> states, List<NFA.State> acceptStates) {
        return states.stream().anyMatch(acceptStates::contains);
    }
}