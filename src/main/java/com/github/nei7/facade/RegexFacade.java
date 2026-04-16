package com.github.nei7.facade;

import com.github.nei7.fsm.NFA;
import com.github.nei7.grammar.Grammar;
import com.github.nei7.grammar.NFAToGrammar;
import com.github.nei7.regex.RegexLexer;
import com.github.nei7.regex.RegexNode;
import com.github.nei7.regex.RegexParser;
import com.github.nei7.regex.RegexToNFA;
import com.github.nei7.regex.RegexToken;

import java.util.List;

// Facade pattern
public class RegexFacade {

    // Immutable DTO
    public record CompilationResult(Grammar grammar, NFA nfa) {
    }

    public CompilationResult compile(String regexInput) {
        RegexNode ast = parseAst(regexInput);

        RegexToNFA nfaBuilder = new RegexToNFA();
        NFA nfa = nfaBuilder.compile(ast);

        NFAToGrammar converter = new NFAToGrammar();
        Grammar grammar = converter.convert(nfa);

        return new CompilationResult(grammar, nfa);
    }

    public RegexNode parseAst(String regexInput) {
        RegexLexer lexer = new RegexLexer(regexInput);
        List<RegexToken> tokens = lexer.tokenize();

        RegexParser parser = new RegexParser(tokens, regexInput);
        return parser.parse();
    }
}