package com.github.nei7.lexer;

public enum GrammarTokenType {
    NON_TERMINAL, // Uppercase letters
    TERMINAL, // Lowercase letters
    ARROW, // Arrow ->
    PIPE, // Vertical bar (OR) |
    EOF // EOF
}