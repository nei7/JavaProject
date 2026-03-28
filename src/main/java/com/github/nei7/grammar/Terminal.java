package com.github.nei7.grammar;

// Represents lowercase letters
public record Terminal(String value) implements Symbol {
    @Override
    public String toString() {
        return value;
    }
}