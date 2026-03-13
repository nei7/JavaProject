package com.github.nei7.model;

// Represents uppercase letters
public record NonTerminal(String name) implements Symbol {
    @Override
    public String toString() {
        return name;
    }
}