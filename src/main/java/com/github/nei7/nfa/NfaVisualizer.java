package com.github.nei7.nfa;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static guru.nidi.graphviz.model.Factory.*;

public class NfaVisualizer {

    public static void draw(NFA nfa, String outputFilename) {
        Graph g = graph("NFA").directed();

        Node startPoint = node("start_point").with(Shape.POINT, Label.of(""));
        Node startNode = createFormattedNode(nfa.start(), nfa.acceptStates());
        g = g.with(startPoint.link(to(startNode)));

        for (NFA.Trans t : nfa.transitions()) {
            Node fromNode = createFormattedNode(t.from(), nfa.acceptStates());
            Node toNode = createFormattedNode(t.to(), nfa.acceptStates());
            g = g.with(fromNode.link(to(toNode).with(Label.of(t.symbol()))));
        }

        for (NFA.Eps e : nfa.epsilons()) {
            Node fromNode = createFormattedNode(e.from(), nfa.acceptStates());
            Node toNode = createFormattedNode(e.to(), nfa.acceptStates());
            g = g.with(fromNode.link(to(toNode).with(Label.of("ε"))));
        }

        for (NFA.State state : nfa.acceptStates()) {
            g = g.with(createFormattedNode(state, nfa.acceptStates()));
        }

        try {
            Graphviz.fromGraph(g)
                    .width(800)
                    .render(Format.SVG)
                    .toFile(new File(outputFilename));
        } catch (IOException ex) {
            System.err.println("Error while saving the graph: " + ex.getMessage());
        }
    }

    private static Node createFormattedNode(NFA.State state, List<NFA.State> acceptStates) {
        Node n = node(state.name());
        if (acceptStates.contains(state)) {
            return n.with(Shape.DOUBLE_CIRCLE);
        } else {
            return n.with(Shape.CIRCLE);
        }
    }
}