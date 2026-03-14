package com.github.nei7;

import com.github.nei7.lexer.GrammarLexer;
import com.github.nei7.lexer.GrammarToken;
import com.github.nei7.lexer.RegexLexer;
import com.github.nei7.lexer.RegexToken;
import com.github.nei7.model.Grammar;
import com.github.nei7.model.NonTerminal;
import com.github.nei7.model.Production;
import com.github.nei7.model.RegexNode;
import com.github.nei7.model.Symbol;
import com.github.nei7.nfa.GrammarToNFA;
import com.github.nei7.nfa.NFA;
import com.github.nei7.nfa.NfaVisualizer;
import com.github.nei7.nfa.RegexToNFA;
import com.github.nei7.parser.GrammarParser;
import com.github.nei7.parser.RegexParser;
import com.github.nei7.solver.LanguageDeriver;
import com.github.nei7.solver.RegexToGrammar;

import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        Graphviz.useEngine(new GraphvizCmdLineEngine());

        Path filePath = Path.of("grammar.txt");
        String regexText = "(x + z)*z* + w*";

        try {
            String grammarText = Files.readString(filePath);

            System.out.println(grammarText);

            GrammarLexer lexer = new GrammarLexer(grammarText);
            List<GrammarToken> tokens = lexer.tokenize();

            for (GrammarToken token : tokens) {
                System.out.println(token);
            }

            GrammarParser parser = new com.github.nei7.parser.GrammarParser(tokens);
            List<Production> productions = parser.parse();

            LanguageDeriver deriver = new com.github.nei7.solver.LanguageDeriver(productions);
            deriver.derive();

            System.out.println("\n\n0. Regex: " + regexText);
            RegexLexer regexLexer = new RegexLexer(regexText);
            List<RegexToken> regexTokens = regexLexer.tokenize();

            System.out.println("1. Tokens build: " + regexTokens.size());

            RegexParser regexParser = new RegexParser(regexTokens);
            RegexNode ast = regexParser.parse();
            System.out.println("2. AST built successfully.");

            RegexToGrammar converter = new RegexToGrammar();
            RegexToNFA nfaBuilder = new RegexToNFA();
            NFA nfa = nfaBuilder.convert(ast);
            Grammar generatedGrammar = converter.convert(nfa);

            System.out.println("\n3. Generated Grammar:");
            System.out.println("Initial symbol: " + generatedGrammar.startSymbol());
            System.out.println("Production rules:");
            Map<NonTerminal, List<List<Symbol>>> grouped = new LinkedHashMap<>();
            for (Production p : generatedGrammar.productions()) {
                grouped.computeIfAbsent(p.left(), k -> new ArrayList<>()).add(p.right());
            }

            for (Map.Entry<NonTerminal, List<List<Symbol>>> entry : grouped.entrySet()) {
                String rightSide = entry.getValue().stream()
                        .map(list -> {
                            if (list.isEmpty())
                                return "ε";
                            return list.stream().map(Symbol::toString).collect(Collectors.joining());
                        })
                        .collect(Collectors.joining(" | "));

                System.out.println("   " + entry.getKey() + " -> " + rightSide);
            }

            GrammarToNFA grammarToNFA = new GrammarToNFA();
            NFA grammarNFA = grammarToNFA.convert(generatedGrammar);

            NfaVisualizer.draw(grammarNFA, "nfa");

        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }
}