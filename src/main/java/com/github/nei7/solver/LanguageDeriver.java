package com.github.nei7.solver;

import com.github.nei7.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class LanguageDeriver {
    private final List<Production> productions;
    private final Map<NonTerminal, List<List<Symbol>>> equations = new LinkedHashMap<>();

    public LanguageDeriver(List<Production> productions) {
        this.productions = productions;
    }

    public void derive() {
        System.out.println("1. Inital equations");
        groupProductions();
        printEquations();

        System.out.println("\n 2. Cleaning up unreachable symbols");
        removeUnreachableSymbols();
        printEquations();

        List<NonTerminal> variables = new ArrayList<>(equations.keySet());

        System.out.println("\n 3. Eliminating left recursion (Arden's Lemma)");
        for (NonTerminal var : variables) {
            applyArdensLemma(var);
        }
        printEquations();

        System.out.println("\n 4. Substituting variables to eliminate indirect recursion");
        for (int i = variables.size() - 1; i >= 0; i--) {
            NonTerminal source = variables.get(i);
            for (int j = 0; j < variables.size(); j++) {
                if (i != j) {
                    NonTerminal target = variables.get(j);
                    substitute(source, target);
                }
            }
        }

        printEquations();
    }

    private void groupProductions() {
        for (Production p : productions) {
            equations.computeIfAbsent(p.left(), k -> new ArrayList<>()).add(p.right());
        }
    }

    // using BFS algorithm
    private void removeUnreachableSymbols() {
        if (equations.isEmpty())
            return;

        NonTerminal startSymbol = equations.keySet().iterator().next();

        Set<NonTerminal> reachable = new HashSet<>();
        Queue<NonTerminal> queue = new LinkedList<>();

        reachable.add(startSymbol);
        queue.add(startSymbol);

        while (!queue.isEmpty()) {
            NonTerminal current = queue.poll();
            List<List<Symbol>> terms = equations.get(current);
            if (terms == null)
                continue;

            for (List<Symbol> term : terms) {
                for (Symbol sym : term) {

                    if (sym instanceof NonTerminal nt) {
                        if (!reachable.contains(nt) && equations.containsKey(nt)) {
                            reachable.add(nt);
                            queue.add(nt);
                        }
                    }
                }
            }
        }

        List<NonTerminal> toRemove = new ArrayList<>();
        for (NonTerminal nt : equations.keySet()) {
            if (!reachable.contains(nt)) {
                toRemove.add(nt);
            }
        }

        for (NonTerminal nt : toRemove) {
            equations.remove(nt);
        }
    }

    private void applyArdensLemma(NonTerminal lhs) {
        List<List<Symbol>> terms = equations.get(lhs);
        if (terms == null)
            return;

        List<List<Symbol>> recursiveTerms = new ArrayList<>();
        List<List<Symbol>> baseTerms = new ArrayList<>();

        for (List<Symbol> term : terms) {
            if (!term.isEmpty() && term.get(term.size() - 1).equals(lhs)) {
                recursiveTerms.add(term);
            } else {
                baseTerms.add(term);
            }
        }

        if (!recursiveTerms.isEmpty()) {
            List<Symbol> alpha = recursiveTerms.get(0).subList(0, recursiveTerms.get(0).size() - 1);
            String alphaStr = alpha.stream().map(Symbol::toString).collect(Collectors.joining());

            String starStr = "(" + alphaStr + ")*";
            if (alphaStr.startsWith("(") && alphaStr.endsWith(")*")) {
                starStr = alphaStr;
            }
            Symbol starSymbol = new Terminal(starStr);

            List<List<Symbol>> newTerms = new ArrayList<>();
            for (List<Symbol> beta : baseTerms) {
                List<Symbol> newTerm = new ArrayList<>();
                newTerm.add(starSymbol);
                newTerm.addAll(beta);
                newTerms.add(newTerm);
            }
            equations.put(lhs, newTerms);
        }
    }

    private void substitute(NonTerminal source, NonTerminal target) {
        if (!equations.containsKey(source) || !equations.containsKey(target))
            return;

        List<List<Symbol>> sourceTerms = equations.get(source);
        List<List<Symbol>> targetTerms = equations.get(target);
        List<List<Symbol>> newTargetTerms = new ArrayList<>();

        for (List<Symbol> term : targetTerms) {
            if (term.contains(source)) {
                for (List<Symbol> sTerm : sourceTerms) {
                    List<Symbol> newTerm = new ArrayList<>();
                    for (Symbol sym : term) {
                        if (sym.equals(source)) {
                            newTerm.addAll(sTerm);
                        } else {
                            newTerm.add(sym);
                        }
                    }
                    newTargetTerms.add(newTerm);
                }
            } else {
                newTargetTerms.add(term);
            }
        }
        equations.put(target, newTargetTerms);
    }

    private void printEquations() {
        for (NonTerminal nt : equations.keySet()) {
            String rightSide = equations.get(nt).stream()
                    .map(term -> term.stream().map(Symbol::toString).collect(Collectors.joining()))
                    .collect(Collectors.joining(" + "));

            System.out.println(nt + " = " + rightSide);
        }
    }
}