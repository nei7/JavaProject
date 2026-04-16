package com.github.nei7.fsm;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;

import java.io.File;
import java.io.IOException;

public class PngExportStrategy implements ExportStrategy {
    @Override
    public void export(Graph graph, String outputFilename) {
        try {
            String filename = outputFilename.endsWith(".png") ? outputFilename : outputFilename + ".png";
            Graphviz.fromGraph(graph)
                    .width(800)
                    .render(Format.PNG)
                    .toFile(new File(filename));
        } catch (IOException ex) {
            System.err.println("Błąd podczas zapisu grafu PNG: " + ex.getMessage());
        }
    }
}