package com.github.nei7.regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.nei7.fsm.NFA;
import com.github.nei7.fsm.NFABuilder;

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
                Position pos = new Position(positionCounter++, l.getValue());
                followSets.put(pos, new HashSet<>());
                yield new NodeInfo(false, Set.of(pos), Set.of(pos));
            }

            case RegexNode.Union u -> {
                NodeInfo left = computeInfo(u.getLeft());
                NodeInfo right = computeInfo(u.getRight());

                Set<Position> first = new HashSet<>(left.first());
                first.addAll(right.first());

                Set<Position> last = new HashSet<>(left.last());
                last.addAll(right.last());

                yield new NodeInfo(left.nullable() || right.nullable(), first, last);
            }

            case RegexNode.Concat c -> {
                NodeInfo left = computeInfo(c.getLeft());
                NodeInfo right = computeInfo(c.getRight());

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
                NodeInfo inner = computeInfo(s.getInner());

                for (Position p : inner.last()) {
                    followSets.get(p).addAll(inner.first());
                }

                yield new NodeInfo(true, inner.first(), inner.last());
            }
            // Compiler safeguard (required for classic inheritance,
            // if RegexNode is not sealed)
            default -> throw new IllegalArgumentException("Unknown node type");
        };
    }

    private NFA buildNFA(NodeInfo rootInfo) {
        NFABuilder builder = new NFABuilder();
        Map<Position, NFA.State> positionToState = new HashMap<>();

        NFA.State startState = new NFA.State("S");
        builder.setStart(startState);

        if (rootInfo.nullable()) {
            builder.addAcceptState(startState);
        }

        for (Position p : rootInfo.first()) {
            NFA.State toState = getOrCreateState(p, positionToState);
            builder.addTransition(startState, p.value(), toState); // Zakładam, że Position nadal jest rekordem
        }

        for (Map.Entry<Position, Set<Position>> entry : followSets.entrySet()) {
            Position currentPos = entry.getKey();
            NFA.State currentState = getOrCreateState(currentPos, positionToState);

            if (rootInfo.last().contains(currentPos)) {
                builder.addAcceptState(currentState);
            }

            for (Position nextPos : entry.getValue()) {
                NFA.State nextState = getOrCreateState(nextPos, positionToState);
                builder.addTransition(currentState, nextPos.value(), nextState);
            }
        }

        return builder.build();
    }

    private NFA.State getOrCreateState(Position p, Map<Position, NFA.State> map) {
        return map.computeIfAbsent(p, key -> new NFA.State(getLetterName(key.id())));
    }

    private String getLetterName(int id) {
        StringBuilder name = new StringBuilder();
        while (id > 0) {
            id--;
            name.insert(0, (char) ('A' + (id % 26)));
            id /= 26;
        }
        return name.toString();
    }
}