package com.github.nei7;

import com.github.nei7.cli.RegexCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "java-project", mixinStandardHelpOptions = true, version = "1.0", description = "A toolkit for formal language theory and compilers.", subcommands = {
        RegexCommand.class
})
public class App implements Runnable {

    public static void main(String[] args) {

        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {

        new CommandLine(this).usage(System.out);
    }
}