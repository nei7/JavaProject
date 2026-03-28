package com.github.nei7.regex;

import java.util.ArrayList;
import java.util.List;

public class RegexLexer {
    private final String input;
    private int position = 0;
    private int line = 1;
    private int column = 1;

    public RegexLexer(String input) {
        this.input = input;
    }

    public List<RegexToken> tokenize() {
        List<RegexToken> tokens = new ArrayList<>();

        while (position < input.length()) {
            char c = input.charAt(position);

            if (c == '\n') {
                line++;
                column = 1;
                position++;
                continue;
            }

            if (Character.isWhitespace(c)) {
                position++;
                column++;
                continue;
            }

            switch (c) {
                case '*' -> tokens.add(new RegexToken(RegexTokenType.STAR, "*", line, column));
                case '+' -> tokens.add(new RegexToken(RegexTokenType.UNION, "+", line, column));
                case '(' -> tokens.add(new RegexToken(RegexTokenType.LPAREN, "(", line, column));
                case ')' -> tokens.add(new RegexToken(RegexTokenType.RPAREN, ")", line, column));

                default -> {
                    if (!Character.isLetterOrDigit(c)) {
                        throw new RegexSyntaxException(
                                "Unknown symbol '" + c + "'",
                                input, line, column);
                    }
                    tokens.add(new RegexToken(RegexTokenType.CHAR, String.valueOf(c), line, column));
                }
            }

            position++;
            column++;
        }

        tokens.add(new RegexToken(RegexTokenType.EOF, "", line, column));
        return tokens;
    }
}