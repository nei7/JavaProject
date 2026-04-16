package com.github.nei7.cli;

import com.github.nei7.facade.RegexFacade;
import com.github.nei7.fsm.FSMVisualizer;
import com.github.nei7.fsm.PngExportStrategy;
import com.github.nei7.fsm.SvgExportStrategy;
import com.github.nei7.regex.RegexSyntaxException;

import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "regex", description = "It constructs a grammar from a regular expression and generates an NFA diagram.")
public class RegexCommand implements Runnable {

    @Parameters(index = "0", description = "Regular expression to process")
    private String input;

    @Option(names = { "-o",
            "--output" }, description = "Path and name of the file to save the NFA diagram", defaultValue = "nfa_output")
    private String outputPath;

    @Option(names = { "-f", "--format" }, description = "Export format (svg or png)", defaultValue = "svg")
    private String format;

    @Override
    public void run() {
        try {
            Graphviz.useEngine(new GraphvizCmdLineEngine());

            RegexFacade facade = new RegexFacade();
            RegexFacade.CompilationResult result = facade.compile(input);

            FSMVisualizer visualizer;
            if ("png".equalsIgnoreCase(format)) {
                visualizer = new FSMVisualizer(new PngExportStrategy());
            } else {
                visualizer = new FSMVisualizer(new SvgExportStrategy());
            }

            visualizer.draw(result.nfa(), outputPath + "_nfa");

            System.out.println(result.grammar());

            String terminal = System.getenv("TERM");
            if (terminal != null && terminal.equals("xterm-kitty") && "svg".equalsIgnoreCase(format)) {
                new ProcessBuilder("kitten", "icat", outputPath + "_nfa.svg")
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .start();
            }

        } catch (RegexSyntaxException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
    }
}