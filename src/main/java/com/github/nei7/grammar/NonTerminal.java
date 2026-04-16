package com.github.nei7.grammar;

// Represents uppercase letters
public class NonTerminal extends Symbol {

    public NonTerminal(String name) {
        super(name);
    }

    public String getName() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}