package com.github.nei7.lexer;

import java.util.ArrayList;
import java.util.List;
import java.lang.Character;

public class RegexLexer {
    private final String input;
    private int position = 0;

    public RegexLexer(String input) {
        this.input = input;
    }

    public List<RegexToken> tokenize() {
        List<RegexToken> tokens = new ArrayList<>();

        while (position < input.length()) {
            char c = input.charAt(position);

            if (Character.isWhitespace(c)) {
                position++;
                continue;
            }

            switch (c) {
                case '*' -> tokens.add(new RegexToken(RegexTokenType.STAR, "*"));
                case '+' -> tokens.add(new RegexToken(RegexTokenType.PIPE, "+"));
                case '(' -> tokens.add(new RegexToken(RegexTokenType.LPAREN, "("));
                case ')' -> tokens.add(new RegexToken(RegexTokenType.RPAREN, ")"));

                default -> {
                    if (!Character.isLetterOrDigit(c)) {
                        throw new IllegalArgumentException("Unexpected character: " + c);
                    }

                    tokens.add(new RegexToken(RegexTokenType.CHAR, String.valueOf(c)));
                }
            }
            position++;
        }

        tokens.add(new RegexToken(RegexTokenType.EOF, ""));
        return tokens;
    }
}