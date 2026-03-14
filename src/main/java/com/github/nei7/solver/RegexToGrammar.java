package com.github.nei7.solver;

import com.github.nei7.model.*;
import java.util.ArrayList;
import java.util.List;

import com.github.nei7.model.RegexNode.*;

public class RegexToGrammar {
    private char nameGenerator = 'A'; // Zaczynamy alfabet od 'A'
    private final List<Production> productions = new ArrayList<>();

    public Grammar convert(RegexNode root) {
        NonTerminal startSymbol = new NonTerminal("S");
        generateNode(root, startSymbol);
        return new Grammar(startSymbol, productions);
    }

    private NonTerminal generateNode(RegexNode node, NonTerminal targetVariable) {
        NonTerminal current = targetVariable;

        if (current == null) {
            if (nameGenerator == 'S')
                nameGenerator++;
            if (nameGenerator > 'Z')
                throw new RuntimeException("Wyrażenie jest zbyt skomplikowane (zabrakło liter w alfabecie!)");

            current = new NonTerminal(String.valueOf(nameGenerator++));
        }

        if (node instanceof Literal lit) {
            productions.add(new Production(current, List.of(new Terminal(lit.value()))));
        } else if (node instanceof Union union) {

            NonTerminal leftNt = generateNode(union.left(), null);
            NonTerminal rightNt = generateNode(union.right(), null);
            productions.add(new Production(current, List.of(leftNt)));
            productions.add(new Production(current, List.of(rightNt)));

        } else if (node instanceof Concat concat) {

            NonTerminal leftNt = generateNode(concat.left(), null);
            NonTerminal rightNt = generateNode(concat.right(), null);
            productions.add(new Production(current, List.of(leftNt, rightNt)));

        } else if (node instanceof Star star) {

            NonTerminal innerNt = generateNode(star.inner(), null);
            productions.add(new Production(current, List.of(innerNt, current)));
            productions.add(new Production(current, List.of()));

        }

        return current;
    }
}