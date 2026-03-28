package com.github.nei7.regex;

public enum RegexTokenType {
    CHAR, // Character literal, np. 'a', 'b', '0', '1'
    STAR, // Kleene star '*'
    UNION, // Union '+'
    LPAREN, // Left parenthesis '('
    RPAREN, // Right parenthesis ')'
    EOF // End of file
}