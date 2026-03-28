package com.github.nei7.fsm;

import java.util.List;

public record NFA(State start, List<State> acceptStates, List<Trans> transitions, List<Eps> epsilons) {

    public record State(String name) {
        @Override
        public String toString() {
            return name;
        }
    }

    public record Trans(State from, String symbol, State to) {
    }

    public record Eps(State from, State to) {
    }
}