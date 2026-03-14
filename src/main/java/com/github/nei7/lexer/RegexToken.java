package com.github.nei7.lexer;

public record RegexToken(RegexTokenType type, String value) {
    @Override
    public String toString() {
        return String.format("[%s: '%s']", type, value);
    }
}