package com.github.nei7.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.nei7.fsm.NFA;

public class RegexToNFA {
    private record Position(int id, String value) {
        @Override
        public String toString() {
            return value + "_" + id;
        }
    }

    private record NodeInfo(
            boolean nullable,
            Set<Position> first,
            Set<Position> last) {
    }

    private int positionCounter = 1;
    private final Map<Position, Set<Position>> followSets = new HashMap<>();

    public NFA compile(RegexNode root) {
        positionCounter = 1;
        followSets.clear();

        NodeInfo rootInfo = computeInfo(root);

        return buildNFA(rootInfo);
    }

    private NodeInfo computeInfo(RegexNode node) {
        return switch (node) {
            case RegexNode.Literal l -> {
                Position pos = new Position(positionCounter++, l.value());
                followSets.put(pos, new HashSet<>());
                yield new NodeInfo(false, Set.of(pos), Set.of(pos));
            }

            case RegexNode.Union u -> {
                NodeInfo left = computeInfo(u.left());
                NodeInfo right = computeInfo(u.right());

                Set<Position> first = new HashSet<>(left.first());
                first.addAll(right.first());

                Set<Position> last = new HashSet<>(left.last());
                last.addAll(right.last());

                yield new NodeInfo(left.nullable() || right.nullable(), first, last);
            }

            case RegexNode.Concat c -> {
                NodeInfo left = computeInfo(c.left());
                NodeInfo right = computeInfo(c.right());

                Set<Position> first = new HashSet<>(left.first());
                if (left.nullable())
                    first.addAll(right.first());

                Set<Position> last = new HashSet<>(right.last());
                if (right.nullable())
                    last.addAll(left.last());

                for (Position p : left.last()) {
                    followSets.get(p).addAll(right.first());
                }

                yield new NodeInfo(left.nullable() && right.nullable(), first, last);
            }

            case RegexNode.Star s -> {
                NodeInfo inner = computeInfo(s.inner());

                for (Position p : inner.last()) {
                    followSets.get(p).addAll(inner.first());
                }

                yield new NodeInfo(true, inner.first(), inner.last());
            }
        };
    }

    private NFA buildNFA(NodeInfo rootInfo) {
        NFA.State startState = new NFA.State("S");
        List<NFA.State> acceptStates = new ArrayList<>();
        List<NFA.Trans> transitions = new ArrayList<>();
        List<NFA.Eps> epsilons = new ArrayList<>();
        Map<Position, NFA.State> positionToState = new HashMap<>();

        if (rootInfo.nullable()) {
            acceptStates.add(startState);
        }

        for (Position p : rootInfo.first()) {
            NFA.State toState = getOrCreateState(p, positionToState);
            transitions.add(new NFA.Trans(startState, p.value(), toState));
        }

        for (Map.Entry<Position, Set<Position>> entry : followSets.entrySet()) {
            Position currentPos = entry.getKey();
            NFA.State currentState = getOrCreateState(currentPos, positionToState);

            if (rootInfo.last().contains(currentPos)) {
                acceptStates.add(currentState);
            }

            for (Position nextPos : entry.getValue()) {
                NFA.State nextState = getOrCreateState(nextPos, positionToState);
                transitions.add(new NFA.Trans(currentState, nextPos.value(), nextState));
            }
        }

        return new NFA(startState, acceptStates, transitions, epsilons);
    }

    private NFA.State getOrCreateState(Position p, Map<Position, NFA.State> map) {
        return map.computeIfAbsent(p, key -> new NFA.State("q" + key.id()));
    }
}