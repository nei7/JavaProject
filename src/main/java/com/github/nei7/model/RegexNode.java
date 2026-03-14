package com.github.nei7.model;

// AST Nodes for Regular Expressions
public sealed interface RegexNode {

    // Regular symbol, e.g. 'a'
    record Literal(String value) implements RegexNode {
    }

    // 2. Concatenation, e.g. "ab" (left: a, right: b)
    record Concat(RegexNode left, RegexNode right) implements RegexNode {
    }

    // 3. Union, e.g. "a+b" (left: a, right: b)
    record Union(RegexNode left, RegexNode right) implements RegexNode {
    }

    // 4. Kleene star, e.g. "a*"
    record Star(RegexNode inner) implements RegexNode {
    }
}