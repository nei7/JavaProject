package com.github.nei7.lexer;

public enum TokenType {
    NON_TERMINAL, // Uppercase letters
    TERMINAL, // Lowercase letters
    ARROW, // Arrow ->
    PIPE, // Vertical bar (OR) |
    EOF // EOF
}