package com.github.nei7.lexer;

import java.util.ArrayList;
import java.util.List;

public class GrammarLexer {
    private final String input;
    private int position = 0;

    public GrammarLexer(String input) {
        this.input = input;
    }

    public List<GrammarToken> tokenize() {
        List<GrammarToken> tokens = new ArrayList<>();

        while (position < input.length()) {
            char currentChar = input.charAt(position);

            if (Character.isWhitespace(currentChar)) {
                position++;
                continue;
            }

            if (currentChar == '-' && position + 1 < input.length() && input.charAt(position + 1) == '>') {
                tokens.add(new GrammarToken(GrammarTokenType.ARROW, "->"));
                position += 2;
                continue;
            }

            if (currentChar == '|') {
                tokens.add(new GrammarToken(GrammarTokenType.PIPE, "|"));
                position++;
                continue;
            }

            if (Character.isUpperCase(currentChar)) {
                tokens.add(new GrammarToken(GrammarTokenType.NON_TERMINAL, String.valueOf(currentChar)));
                position++;
                continue;
            }

            if (Character.isLowerCase(currentChar)) {
                tokens.add(new GrammarToken(GrammarTokenType.TERMINAL, String.valueOf(currentChar)));
                position++;
                continue;
            }

            throw new RuntimeException("Lexer Error: Nieznany znak '" + currentChar + "' na pozycji " + position);
        }

        tokens.add(new GrammarToken(GrammarTokenType.EOF, ""));
        return tokens;
    }
}