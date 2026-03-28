package com.github.nei7.fsm;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record DFA(State start, List<State> acceptStates, List<Trans> transitions) {

    public record State(String name) {
        @Override
        public String toString() {
            return name;
        }
    }

    public record Trans(State from, String symbol, State to) {
    }

    public Set<State> allStates() {
        Set<State> states = new LinkedHashSet<>();
        states.add(start);
        for (Trans t : transitions) {
            states.add(t.from());
            states.add(t.to());
        }
        return states;
    }
}
