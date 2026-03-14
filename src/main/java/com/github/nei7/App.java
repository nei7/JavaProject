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
import com.github.nei7.parser.GrammarParser;
import com.github.nei7.parser.RegexParser;
import com.github.nei7.solver.LanguageDeriver;
import com.github.nei7.solver.RegexToGrammar;

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
        Path filePath = Path.of("grammar.txt");
        String regexText = "(a+b)*c";

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

            RegexLexer regexLexer = new RegexLexer(regexText);
            List<RegexToken> regexTokens = regexLexer.tokenize();
            System.out.println("\n\n2. Zbudowano tokenów: " + regexTokens.size());

            // Etap 2: Analiza Składniowa (Budowanie Drzewa AST)
            RegexParser regexParser = new RegexParser(regexTokens);
            RegexNode ast = regexParser.parse();
            System.out.println("3. Skonstruowano Drzewo Składniowe AST pomyślnie.");

            // Etap 3: Konwersja Drzewa do Gramatyki
            RegexToGrammar converter = new RegexToGrammar();
            Grammar generatedGrammar = converter.convert(ast);

            System.out.println("\n🎉 4. Wygenerowana Gramatyka:");
            System.out.println("Symbol startowy: " + generatedGrammar.startSymbol());
            System.out.println("Reguły:");
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

        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }
}