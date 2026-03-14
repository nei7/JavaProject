package com.github.nei7.lexer;

public record GrammarToken(GrammarTokenType type, String value) {
    @Override
    public String toString() {
        return String.format("[%s: '%s']", type, value);
    }
}