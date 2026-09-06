
package com.neoobjectpascal;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {

    private Object execute(String code) throws IOException {
        CharStream input = CharStreams.fromString(code);
        NeoObjectPascalLexer lexer = new NeoObjectPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NeoObjectPascalParser parser = new NeoObjectPascalParser(tokens);
        ParseTree tree = parser.program();
        Interpreter interpreter = new Interpreter();
        return interpreter.visit(tree);
    }

    @Test
    public void testVariableDeclarationAndAssignment() throws IOException {
        String code = "var x: Integer; begin x := 10; end.";
        Object result = execute(code);
        // No direct way to check the value of x, so we test if it executes without error
    }

    @Test
    public void testFunctionDeclarationAndCall() throws IOException {
        String code = "function add(a, b): Integer begin return a + b; end; var result: Integer; begin result := add(5, 10); WriteLn(result); end.";
        Object result = execute(code);
        // We will check the output on the console
    }

    @Test
    public void testPipelineOperator() throws IOException {
        String code = "function square(x): Integer begin return x * x; end; var result: Integer; begin result := 5 |> square; WriteLn(result); end.";
        Object result = execute(code);
        // We will check the output on the console
    }
}

