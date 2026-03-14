package com.github.nei7.lexer;

public enum RegexTokenType {
    CHAR, // Character literal, np. 'a', 'b', '0', '1'
    STAR, // Kleene star '*'
    PIPE, // Union '+'
    LPAREN, // Left parenthesis '('
    RPAREN, // Right parenthesis ')'
    EOF // End of file
}