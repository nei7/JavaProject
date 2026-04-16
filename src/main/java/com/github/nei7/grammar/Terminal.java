package com.github.nei7.grammar;

// Represents lowercase letters
public class Terminal extends Symbol {

    public Terminal(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return value;
    }
}