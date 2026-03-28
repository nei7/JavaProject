package com.github.nei7.cli;

import com.github.nei7.errors.RegexSyntaxException;
import com.github.nei7.fsm.DFA;
import com.github.nei7.fsm.NFA;
import com.github.nei7.fsm.NFAToDFA;
import com.github.nei7.grammar.DFAToGrammar;
import com.github.nei7.grammar.Grammar;

import com.github.nei7.regex.RegexLexer;
import com.github.nei7.regex.RegexToken;
import com.github.nei7.regex.RegexNode;
import com.github.nei7.regex.RegexParser;
import com.github.nei7.regex.RegexToNFA;

import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;

import picocli.CommandLine.Option;

@Command(name = "regex", description = "It constructs a grammar from a regular expression and generates an NFA diagram.")
public class RegexCommand implements Runnable {

    @Parameters(index = "0", description = "Regular expression to process")
    private String input;

    @Option(names = { "-o",
            "--output" }, description = "Path and name of the file to save the NFA diagram Default: nfa_output", defaultValue = "nfa_output")
    private String outputPath;

    @Override
    public void run() {
        try {
            Graphviz.useEngine(new GraphvizCmdLineEngine());

            RegexLexer regexLexer = new RegexLexer(input);
            List<RegexToken> regexTokens = regexLexer.tokenize();

            RegexParser regexParser = new RegexParser(regexTokens, input);
            RegexNode ast = regexParser.parse();

            RegexToNFA nfaBuilder = new RegexToNFA();
            NFA nfa = nfaBuilder.convert(ast);
            NFAToDFA nfaToDfaConverter = new NFAToDFA();
            DFA dfa = nfaToDfaConverter.convert(nfa);

            DFAToGrammar converter = new DFAToGrammar();
            Grammar generatedGrammar = converter.convert(dfa);

            System.out.println("Generated Grammar:");
            System.out.println(generatedGrammar);

        } catch (RegexSyntaxException e) {
            System.err.println(e.getMessage());
        }
    }
}