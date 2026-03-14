package com.github.nei7.cli;

import com.github.nei7.lexer.GrammarLexer;
import com.github.nei7.lexer.GrammarToken;
import com.github.nei7.model.Production;
import com.github.nei7.parser.GrammarParser;
import com.github.nei7.solver.LanguageDeriver;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Command(name = "derive", description = "Reads the grammar from a text file and computes the final language using Arden's lemmatization")
public class DeriveCommand implements Runnable {

    @Parameters(index = "0", description = "Path to the grammar file")
    private String filePathStr;

    @Override
    public void run() {
        try {
            Path filePath = Path.of(filePathStr);
            String grammarText = Files.readString(filePath);

            GrammarLexer lexer = new GrammarLexer(grammarText);
            List<GrammarToken> tokens = lexer.tokenize();

            GrammarParser parser = new GrammarParser(tokens);
            List<Production> productions = parser.parse();

            LanguageDeriver deriver = new LanguageDeriver(productions);
            deriver.derive();

        } catch (Exception e) {
            System.err.println("❌ Błąd podczas analizy gramatyki: " + e.getMessage());
        }
    }
}