package com.github.nei7;

import com.github.nei7.lexer.Lexer;
import com.github.nei7.lexer.Token;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Path filePath = Path.of("grammar.txt");

        try {
            String grammarText = Files.readString(filePath);

            System.out.println(grammarText);

            Lexer lexer = new Lexer(grammarText);
            List<Token> tokens = lexer.tokenize();

            for (Token token : tokens) {
                System.out.println(token);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());

        }
    }
}