package com.github.nei7.regex;

import com.github.nei7.errors.RegexSyntaxException;
import com.github.nei7.regex.RegexNode.*;

import java.util.List;

public class RegexParser {
    private final List<RegexToken> tokens;
    private final String input;
    private int current = 0;

    public RegexParser(List<RegexToken> tokens, String input) {
        this.tokens = tokens;
        this.input = input;
    }

    public RegexNode parse() {
        RegexNode node = parseUnion();

        if (!isAtEnd()) {
            RegexToken token = peek();
            throw new RegexSyntaxException(
                    "Unexpected token '" + token.value() + "'",
                    input, token.line(), token.column());
        }

        return node;
    }

    private RegexNode parseUnion() {
        RegexNode left = parseConcat();

        while (match(RegexTokenType.UNION)) {
            RegexNode right = parseConcat();
            left = new Union(left, right);
        }

        return left;
    }

    private RegexNode parseConcat() {
        RegexNode left = parseStar();

        while (check(RegexTokenType.CHAR) || check(RegexTokenType.LPAREN)) {
            RegexNode right = parseStar();
            left = new Concat(left, right);
        }

        return left;
    }

    private RegexNode parseStar() {
        RegexNode node = parseBase();

        while (match(RegexTokenType.STAR)) {
            node = new Star(node);
        }

        return node;
    }

    private RegexNode parseBase() {
        if (match(RegexTokenType.CHAR)) {
            return new Literal(previous().value());
        }

        if (match(RegexTokenType.LPAREN)) {
            RegexNode expr = parseUnion();
            consume(RegexTokenType.RPAREN, "A closing parenthesis was expected ')'");
            return expr;
        }

        RegexToken token = peek();
        throw new RegexSyntaxException(
                "The character or '(' was expected, but found: '" + token.value() + "'",
                input, token.line(), token.column());
    }

    private boolean match(RegexTokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(RegexTokenType type) {
        if (isAtEnd())
            return false;
        return peek().type() == type;
    }

    private RegexToken advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == RegexTokenType.EOF;
    }

    private RegexToken peek() {
        return tokens.get(current);
    }

    private RegexToken previous() {
        return tokens.get(current - 1);
    }

    private RegexToken consume(RegexTokenType type, String message) {
        if (check(type))
            return advance();
        RegexToken token = peek();
        throw new RegexSyntaxException(message, input, token.line(), token.column());
    }
}