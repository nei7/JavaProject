package com.github.nei7.regex;

public class RegexSyntaxException extends RuntimeException {

    private final int line;
    private final int column;

    public RegexSyntaxException(String message, String input, int line, int column) {
        super(formatMessage(message, input, line, column));
        this.line = line;
        this.column = column;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    private static String formatMessage(String message, String input, int line, int column) {
        String[] lines = input.split("\n", -1);
        String errorLine = (line <= lines.length) ? lines[line - 1] : "";

        String pointer = " ".repeat(column - 1) + "^";

        return String.format("""
                Syntax Error: %s
                  --> line %d:%d
                   |
                %2d | %s
                   | %s
                """,
                message, line, column, line, errorLine, pointer);
    }
}