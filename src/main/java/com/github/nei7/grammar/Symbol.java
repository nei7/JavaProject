package com.github.nei7.grammar;

public abstract class Symbol {
    protected final String value;

    protected Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public abstract String toString();
}