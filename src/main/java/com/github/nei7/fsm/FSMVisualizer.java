package com.github.nei7.fsm;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static guru.nidi.graphviz.model.Factory.*;

public class FSMVisualizer {

    public static void draw(NFA nfa, String outputFilename) {
        Graph g = graph("NFA").directed().graphAttr().with(Rank.dir(LEFT_TO_RIGHT));

        Node startPoint = node("start_point").with(Shape.POINT, Label.of(""));
        Node startNode = createNfaNode(nfa.start(), nfa.acceptStates());
        g = g.with(startPoint.link(to(startNode)));

        for (NFA.Trans t : nfa.transitions()) {
            Node fromNode = createNfaNode(t.from(), nfa.acceptStates());
            Node toNode = createNfaNode(t.to(), nfa.acceptStates());
            g = g.with(fromNode.link(to(toNode).with(Label.of(t.symbol()))));
        }

        for (NFA.Eps e : nfa.epsilons()) {
            Node fromNode = createNfaNode(e.from(), nfa.acceptStates());
            Node toNode = createNfaNode(e.to(), nfa.acceptStates());
            g = g.with(fromNode.link(to(toNode).with(Label.of("ε"))));
        }

        for (NFA.State state : nfa.acceptStates()) {
            g = g.with(createNfaNode(state, nfa.acceptStates()));
        }

        saveGraph(g, outputFilename);
    }

    private static Node createNfaNode(NFA.State state, List<NFA.State> acceptStates) {
        Node n = node(state.name());
        return acceptStates.contains(state) ? n.with(Shape.DOUBLE_CIRCLE) : n.with(Shape.CIRCLE);
    }

    private static void saveGraph(Graph g, String outputFilename) {
        try {
            Graphviz.fromGraph(g)
                    .width(800)
                    .render(Format.SVG)
                    .toFile(new File(outputFilename));

        } catch (IOException ex) {
            System.err.println("Error while saving the graph: " + ex.getMessage());
        }
    }
}