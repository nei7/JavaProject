package com.github.nei7.regex;

public record RegexToken(RegexTokenType type, String value, int line, int column) {
    @Override
    public String toString() {
        return String.format("[%s: '%s']", type, value);
    }
}