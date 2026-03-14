package com.github.nei7.model;

import java.util.List;

public record Grammar(NonTerminal startSymbol, List<Production> productions) {
}