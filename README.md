# Java Project

A command-line interface (CLI) tool built in Java as a university project. Its primary purpose is to parse a regular expression and transform it into grammar.

## Flow

1. **Regex to AST:** The input string is tokenized (Lexer) and parsed (Recursive Descent Parser) into an Abstract Syntax Tree.
2. **AST to NFA:** The AST is converted into a Non-deterministic Finite Automaton (NFA) using the **McNaughton-Yamada-Thompson algorithm** (Thompson's construction).
3. **NFA to DFA:** The NFA is transformed into a Deterministic Finite Automaton (DFA) using the Subset Construction (Powerset) algorithm.
4. **DFA to Regular Grammar:** Finally, the DFA transitions are mapped to production rules to generate a Regular Grammar (Right-Linear).

## Supported Operators

- **Concatenation:** Implicitly applied between adjacent symbols (e.g., `ab`).
- **Union (`+`):** Matches either the expression before or after the operator (e.g., `a+b`).
- **Kleene Star (`*`):** Matches zero or more occurrences of the preceding element (e.g., `a*`).
- **Parentheses `()`:** Used to group expressions and override default precedence (e.g., `(a+b)*c`).

## Getting started

### Running via Command Line (Maven)

You can compile and run the application directly from your terminal:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.github.nei7.App" -Dexec.args="regex a(b+c)*d"
```

### Running Tests

To execute the unit tests run:

```bash
mvn test
```

### Nix & Direnv Support

This project includes a `flake.nix` and `.envrc` configuration for fully reproducible development environments.

If you have Nix and direnv installed, simply run:

```bash
direnv allow
```
