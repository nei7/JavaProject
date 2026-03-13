package com.github.nei7.parser;

import com.github.nei7.lexer.Token;
import com.github.nei7.lexer.TokenType;
import com.github.nei7.model.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Production> parse() {
        List<Production> productions = new ArrayList<>();

        while (!isAtEnd()) {
            Token leftToken = consume(TokenType.NON_TERMINAL,
                    "Non-terminal expected at the beginning of a production rule.");
            NonTerminal left = new NonTerminal(leftToken.value());

            consume(TokenType.ARROW, "Arrow expected after non-terminal.");

            List<Symbol> currentRightSide = new ArrayList<>();

            while (!isAtEnd()) {
                Token token = peek();

                if (token.type() == TokenType.NON_TERMINAL && checkNext(TokenType.ARROW)) {
                    break;
                }

                advance();

                if (token.type() == TokenType.TERMINAL) {
                    currentRightSide.add(new Terminal(token.value()));
                } else if (token.type() == TokenType.NON_TERMINAL) {
                    currentRightSide.add(new NonTerminal(token.value()));
                } else if (token.type() == TokenType.PIPE) {

                    productions.add(new Production(left, new ArrayList<>(currentRightSide)));
                    currentRightSide.clear();
                }
            }

            productions.add(new Production(left, currentRightSide));
        }
        return productions;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return tokens.get(current - 1);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean checkNext(TokenType type) {
        if (current + 1 >= tokens.size())
            return false;
        return tokens.get(current + 1).type() == type;
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token consume(TokenType type, String errorMessage) {
        if (peek().type() == type)
            return advance();
        throw new RuntimeException("Failed to parse: " + errorMessage + " found: " + peek().value());
    }
}