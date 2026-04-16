package com.github.nei7.cli;

import com.github.nei7.facade.RegexFacade;
import com.github.nei7.regex.AstPrinterVisitor;
import com.github.nei7.regex.RegexNode;
import com.github.nei7.regex.RegexSyntaxException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "parse", description = "Returns the parse tree of a given regular expression.")
public class RegexASTCommand implements Runnable {

    @Parameters(index = "0", description = "Regular expression to process")
    private String input;

    @Override
    public void run() {
        try {

            RegexFacade facade = new RegexFacade();
            RegexNode ast = facade.parseAst(input);

            AstPrinterVisitor printer = new AstPrinterVisitor();
            String astString = printer.print(ast);

            System.out.println(astString);
        } catch (RegexSyntaxException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
    }
}
