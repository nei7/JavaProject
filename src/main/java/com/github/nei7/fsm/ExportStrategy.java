package com.github.nei7.fsm;

import guru.nidi.graphviz.model.Graph;

public interface ExportStrategy {
    void export(Graph graph, String outputFilename);
}