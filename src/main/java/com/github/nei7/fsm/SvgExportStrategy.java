package com.github.nei7.fsm;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;

import java.io.File;
import java.io.IOException;

public class SvgExportStrategy implements ExportStrategy {
    @Override
    public void export(Graph graph, String outputFilename) {
        try {
            String filename = outputFilename.endsWith(".svg") ? outputFilename : outputFilename + ".svg";
            Graphviz.fromGraph(graph)
                    .width(800)
                    .render(Format.SVG)
                    .toFile(new File(filename));
        } catch (IOException ex) {
            System.err.println("Błąd podczas zapisu grafu SVG: " + ex.getMessage());
        }
    }
}