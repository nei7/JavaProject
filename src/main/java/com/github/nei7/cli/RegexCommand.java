package com.github.nei7.cli;

import com.github.nei7.lexer.RegexLexer;
import com.github.nei7.lexer.RegexToken;
import com.github.nei7.model.Grammar;
import com.github.nei7.model.NonTerminal;
import com.github.nei7.model.Production;
import com.github.nei7.model.RegexNode;
import com.github.nei7.nfa.GrammarToNFA;
import com.github.nei7.nfa.NFA;
import com.github.nei7.nfa.NfaVisualizer;
import com.github.nei7.nfa.RegexToNFA;
import com.github.nei7.parser.RegexParser;
import com.github.nei7.solver.RegexToGrammar;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import com.github.nei7.model.Symbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import picocli.CommandLine.Option;

@Command(name = "regex", description = "It constructs a grammar from a regular expression and generates an NFA diagram.")
public class RegexCommand implements Runnable {

    @Parameters(index = "0", description = "Regular expression to process")
    private String regexText;

    @Option(names = { "-o",
            "--output" }, description = "Path and name of the file to save the NFA diagram Default: nfa_output", defaultValue = "nfa_output")
    private String outputPath;

    @Override
    public void run() {
        try {
            Graphviz.useEngine(new GraphvizCmdLineEngine());

            RegexLexer regexLexer = new RegexLexer(regexText);
            List<RegexToken> regexTokens = regexLexer.tokenize();

            RegexParser regexParser = new RegexParser(regexTokens);
            RegexNode ast = regexParser.parse();

            RegexToNFA nfaBuilder = new RegexToNFA();
            NFA nfa = nfaBuilder.convert(ast);

            RegexToGrammar converter = new RegexToGrammar();
            Grammar generatedGrammar = converter.convert(nfa);

            Map<NonTerminal, List<List<Symbol>>> grouped = new LinkedHashMap<>();
            for (Production p : generatedGrammar.productions()) {
                grouped.computeIfAbsent(p.left(), k -> new ArrayList<>()).add(p.right());
            }

            for (Map.Entry<NonTerminal, List<List<Symbol>>> entry : grouped.entrySet()) {
                String rightSide = entry.getValue().stream()
                        .map(list -> list.isEmpty() ? "ε"
                                : list.stream().map(Symbol::toString).collect(Collectors.joining()))
                        .collect(Collectors.joining(" | "));

                System.out.println("   " + entry.getKey() + " -> " + rightSide);
            }

            GrammarToNFA grammarToNFA = new GrammarToNFA();
            NFA grammarNFA = grammarToNFA.convert(generatedGrammar);

            NfaVisualizer.draw(grammarNFA, outputPath);

        } catch (Exception e) {
            System.err.println("Error " + e.getMessage());
        }
    }
}