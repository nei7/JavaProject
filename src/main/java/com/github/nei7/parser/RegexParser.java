package com.github.nei7.parser;

import com.github.nei7.lexer.RegexToken;
import com.github.nei7.lexer.RegexTokenType;
import com.github.nei7.model.RegexNode;
import com.github.nei7.model.RegexNode.*;

import java.util.List;

public class RegexParser {
    private final List<RegexToken> tokens;
    private int current = 0;

    public RegexParser(List<RegexToken> tokens) {
        this.tokens = tokens;
    }

    public RegexNode parse() {
        return parseUnion(); // Zaczynamy od działania z najniższym priorytetem
    }

    // 1. Priorytet najniższy: Alternatywa (|)
    private RegexNode parseUnion() {
        RegexNode left = parseConcat();

        while (match(RegexTokenType.PIPE)) {
            RegexNode right = parseConcat();
            left = new Union(left, right);
        }

        return left;
    }

    private RegexNode parseConcat() {
        RegexNode left = parseStar();

        // Ponieważ konkatenacja nie ma swojego operatora (nie piszemy a+b, tylko ab),
        // dopóki następny znak to litera lub otwierający nawias, łączymy to z
        // poprzednim!
        while (check(RegexTokenType.CHAR) || check(RegexTokenType.LPAREN)) {
            RegexNode right = parseStar();
            left = new Concat(left, right);
        }

        return left;
    }

    // 3. Wysoki priorytet: Gwiazdka Kleene'ego (*)
    private RegexNode parseStar() {
        RegexNode node = parseBase();

        // Używamy pętli while, by obsłużyć coś w stylu a**
        while (match(RegexTokenType.STAR)) {
            node = new Star(node);
        }

        return node;
    }

    // 4. Baza (Najwyższy priorytet): Pojedynczy znak lub nawiasy ()
    private RegexNode parseBase() {
        if (match(RegexTokenType.CHAR)) {
            return new Literal(previous().value());
        }

        if (match(RegexTokenType.LPAREN)) {
            RegexNode expr = parseUnion(); // Wchodzimy z powrotem na samą górę!
            consume(RegexTokenType.RPAREN, "Błąd składni: Oczekiwano zamykającego nawiasu ')'");
            return expr;
        }

        throw new RuntimeException("Błąd składni Regex: Oczekiwano znaku lub '(', ale znaleziono: " + peek().value());
    }

    // --- Metody pomocnicze do przesuwania się po tokenach ---

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
        throw new RuntimeException(message);
    }
}