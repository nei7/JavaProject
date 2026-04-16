package com.github.nei7.fsm;

import java.util.ArrayList;
import java.util.List;

public class NFABuilder {
    private NFA.State start;
    private final List<NFA.State> acceptStates = new ArrayList<>();
    private final List<NFA.Trans> transitions = new ArrayList<>();
    private final List<NFA.Eps> epsilons = new ArrayList<>();

    public NFABuilder setStart(NFA.State start) {
        this.start = start;
        return this;
    }

    public NFABuilder addAcceptState(NFA.State state) {
        this.acceptStates.add(state);
        return this;
    }

    public NFABuilder addTransition(NFA.State from, String symbol, NFA.State to) {
        this.transitions.add(new NFA.Trans(from, symbol, to));
        return this;
    }

    public NFABuilder addEpsilon(NFA.State from, NFA.State to) {
        this.epsilons.add(new NFA.Eps(from, to));
        return this;
    }

    public NFA build() {
        if (start == null) {
            throw new IllegalStateException("NFA must have a start state");
        }
        return new NFA(start, acceptStates, transitions, epsilons);
    }
}