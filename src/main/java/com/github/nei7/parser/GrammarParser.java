package com.github.nei7.parser;

import com.github.nei7.lexer.GrammarToken;
import com.github.nei7.lexer.GrammarTokenType;
import com.github.nei7.model.*;

import java.util.ArrayList;
import java.util.List;

public class GrammarParser {
    private final List<GrammarToken> tokens;
    private int current = 0;

    public GrammarParser(List<GrammarToken> tokens) {
        this.tokens = tokens;
    }

    public List<Production> parse() {
        List<Production> productions = new ArrayList<>();

        while (!isAtEnd()) {
            GrammarToken leftToken = consume(GrammarTokenType.NON_TERMINAL,
                    "Non-terminal expected at the beginning of a production rule.");
            NonTerminal left = new NonTerminal(leftToken.value());

            consume(GrammarTokenType.ARROW, "Arrow expected after non-terminal.");

            List<Symbol> currentRightSide = new ArrayList<>();

            while (!isAtEnd()) {
                GrammarToken token = peek();

                if (token.type() == GrammarTokenType.NON_TERMINAL && checkNext(GrammarTokenType.ARROW)) {
                    break;
                }

                advance();

                if (token.type() == GrammarTokenType.TERMINAL) {
                    currentRightSide.add(new Terminal(token.value()));
                } else if (token.type() == GrammarTokenType.NON_TERMINAL) {
                    currentRightSide.add(new NonTerminal(token.value()));
                } else if (token.type() == GrammarTokenType.PIPE) {

                    productions.add(new Production(left, new ArrayList<>(currentRightSide)));
                    currentRightSide.clear();
                }
            }

            productions.add(new Production(left, currentRightSide));
        }
        return productions;
    }

    private GrammarToken advance() {
        if (!isAtEnd())
            current++;
        return tokens.get(current - 1);
    }

    private GrammarToken peek() {
        return tokens.get(current);
    }

    private boolean checkNext(GrammarTokenType type) {
        if (current + 1 >= tokens.size())
            return false;
        return tokens.get(current + 1).type() == type;
    }

    private boolean isAtEnd() {
        return peek().type() == GrammarTokenType.EOF;
    }

    private GrammarToken consume(GrammarTokenType type, String errorMessage) {
        if (peek().type() == type)
            return advance();
        throw new RuntimeException("Failed to parse: " + errorMessage + " found: " + peek().value());
    }
}