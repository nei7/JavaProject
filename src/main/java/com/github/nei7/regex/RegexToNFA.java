package com.github.nei7.regex;

import java.util.ArrayList;
import java.util.List;

import com.github.nei7.fsm.NFA;
import com.github.nei7.regex.RegexNode.*;

// McNaughton–Yamada–Thompson algorithm
// https://en.wikipedia.org/wiki/Thompson%27s_construction
// https://medium.com/swlh/visualizing-thompsons-construction-algorithm-for-nfas-step-by-step-f92ef378581b
public class RegexToNFA {
    private char nameGenerator = 'A';
    private int fallbackCounter = 1;

    private final List<NFA.Trans> transitions = new ArrayList<>();
    private final List<NFA.Eps> epsilons = new ArrayList<>();

    private record NFAFrag(NFA.State start, NFA.State end) {
    }

    public NFA convert(RegexNode root) {
        nameGenerator = 'A';
        fallbackCounter = 1;
        transitions.clear();
        epsilons.clear();

        NFA.State start = new NFA.State("S");
        NFAFrag frag = build(root, start);

        return new NFA(frag.start(), List.of(frag.end()), new ArrayList<>(transitions), new ArrayList<>(epsilons));
    }

    private NFA.State newState() {
        if (nameGenerator == 'S')
            nameGenerator++;
        if (nameGenerator <= 'Z') {
            return new NFA.State(String.valueOf(nameGenerator++));
        } else {
            return new NFA.State("N" + (fallbackCounter++));
        }
    }

    private NFAFrag build(RegexNode node, NFA.State targetStart) {
        NFA.State start = (targetStart != null) ? targetStart : newState();
        NFA.State end = newState();

        if (node instanceof Literal lit) {
            transitions.add(new NFA.Trans(start, String.valueOf(lit.value()), end));
        } else if (node instanceof Concat cat) {
            NFAFrag left = build(cat.left(), start);
            NFAFrag right = build(cat.right(), null);
            epsilons.add(new NFA.Eps(left.end(), right.start()));
            end = right.end();
        } else if (node instanceof Union un) {
            NFAFrag left = build(un.left(), null);
            NFAFrag right = build(un.right(), null);
            epsilons.add(new NFA.Eps(start, left.start()));
            epsilons.add(new NFA.Eps(start, right.start()));
            epsilons.add(new NFA.Eps(left.end(), end));
            epsilons.add(new NFA.Eps(right.end(), end));
        } else if (node instanceof Star star) {

            NFAFrag inner = build(star.inner(), null);
            epsilons.add(new NFA.Eps(start, inner.start()));
            epsilons.add(new NFA.Eps(start, end));
            epsilons.add(new NFA.Eps(inner.end(), inner.start()));
            epsilons.add(new NFA.Eps(inner.end(), end));
        }

        return new NFAFrag(start, end);
    }
}