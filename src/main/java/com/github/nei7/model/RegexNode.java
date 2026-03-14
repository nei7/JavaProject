package com.github.nei7.model;

// AST Nodes for Regular Expressions

// Węzeł drzewa AST dla wyrażeń regularnych
public sealed interface RegexNode {

    // 1. Zwykły znak, np. 'a'
    record Literal(String value) implements RegexNode {
    }

    // 2. Łączenie, np. "ab" (lewy: a, prawy: b)
    record Concat(RegexNode left, RegexNode right) implements RegexNode {
    }

    // 3. Alternatywa, np. "a|b" (lewy: a, prawy: b)
    record Union(RegexNode left, RegexNode right) implements RegexNode {
    }

    // 4. Gwiazdka, np. "a*"
    record Star(RegexNode inner) implements RegexNode {
    }
}