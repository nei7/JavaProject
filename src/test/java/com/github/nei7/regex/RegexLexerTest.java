package com.github.nei7.regex;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegexLexerTest {

    @Test
    void shouldTokenizeEmptyStringAsEOF() {
        RegexLexer lexer = new RegexLexer("");
        List<RegexToken> tokens = lexer.tokenize();

        assertEquals(1, tokens.size());
        assertEquals(RegexTokenType.EOF, tokens.get(0).type());
    }

    @Test
    void shouldTokenizeSingleCharactersAndDigits() {
        RegexLexer lexer = new RegexLexer("a1B");
        List<RegexToken> tokens = lexer.tokenize();

        assertEquals(4, tokens.size()); // 3 znaki + EOF
        assertEquals(RegexTokenType.CHAR, tokens.get(0).type());
        assertEquals("a", tokens.get(0).value());
        assertEquals("1", tokens.get(1).value());
        assertEquals("B", tokens.get(2).value());
        assertEquals(RegexTokenType.EOF, tokens.get(3).type());
    }

    @Test
    void shouldTokenizeOperators() {
        // Uwaga: w Twoim Lexerze '+' to UNION, a nie '|'
        RegexLexer lexer = new RegexLexer("*+()");
        List<RegexToken> tokens = lexer.tokenize();

        assertEquals(5, tokens.size());
        assertEquals(RegexTokenType.STAR, tokens.get(0).type());
        assertEquals(RegexTokenType.UNION, tokens.get(1).type());
        assertEquals(RegexTokenType.LPAREN, tokens.get(2).type());
        assertEquals(RegexTokenType.RPAREN, tokens.get(3).type());
    }

    @Test
    void shouldIgnoreWhitespaceButUpdateColumnsAndLines() {
        RegexLexer lexer = new RegexLexer("a \n b");
        List<RegexToken> tokens = lexer.tokenize();

        assertEquals(3, tokens.size()); // 'a', 'b', EOF

        RegexToken tokenA = tokens.get(0);
        assertEquals("a", tokenA.value());
        assertEquals(1, tokenA.line());
        assertEquals(1, tokenA.column());

        RegexToken tokenB = tokens.get(1);
        assertEquals("b", tokenB.value());
        assertEquals(2, tokenB.line());
        assertEquals(2, tokenB.column()); // spacja przed 'b' w nowej linii
    }

    @Test
    void shouldThrowExceptionOnUnknownSymbol() {
        RegexLexer lexer = new RegexLexer("a$b");

        RegexSyntaxException exception = assertThrows(
                RegexSyntaxException.class,
                lexer::tokenize);

        assertTrue(exception.getMessage().contains("Unknown symbol '$'"));
    }
}