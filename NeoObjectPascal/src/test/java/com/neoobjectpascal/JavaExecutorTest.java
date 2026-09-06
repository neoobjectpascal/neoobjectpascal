package com.neoobjectpascal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for inline Java blocks, focusing on named argument aliases: when a java-block argument is a
 * bare identifier, it is exposed inside the block by that name (typed), in addition to the always
 * available positional {@code paramN}.
 */
public class JavaExecutorTest {

    @Test
    @DisplayName("argument is accessible by its name (typed, no cast needed)")
    void namedArgument_isAccessibleByName() {
        Object result = JavaExecutor.executeJavaCode(
                "{ return \"Ola, \" + nome.toUpperCase(); }",
                Collections.singletonList("ada"),
                Collections.singletonList("nome"));
        assertEquals("Ola, ADA", result);
    }

    @Test
    @DisplayName("positional paramN still works (backward compatible)")
    void positionalParam_stillWorks() {
        Object result = JavaExecutor.executeJavaCode(
                "{ return ((String)param0).toUpperCase(); }",
                Collections.singletonList("grace"),
                Collections.singletonList("nome"));
        assertEquals("GRACE", result);
    }

    @Test
    @DisplayName("multiple named arguments")
    void multipleNamedArguments() {
        Object result = JavaExecutor.executeJavaCode(
                "{ return a + b; }",
                Arrays.asList(3, 4),
                Arrays.asList("a", "b"));
        assertEquals(7, result);
    }

    @Test
    @DisplayName("non-identifier argument (no name) falls back to paramN")
    void expressionArgument_fallsBackToParam() {
        Object result = JavaExecutor.executeJavaCode(
                "{ return ((Integer)param0) * 10; }",
                Collections.singletonList(6),
                Collections.singletonList((String) null));
        assertEquals(60, result);
    }

    @Test
    @DisplayName("Java reserved word as name is skipped; paramN still compiles/works")
    void reservedWordName_isSkipped() {
        // If 'class' were emitted as an alias, the generated Java would fail to compile.
        Object result = JavaExecutor.executeJavaCode(
                "{ return ((String)param0); }",
                Collections.singletonList("x"),
                Collections.singletonList("class"));
        assertEquals("x", result);
    }

    @Test
    @DisplayName("two-arg overload (no names) is unchanged")
    void legacyOverload_unchanged() {
        Object result = JavaExecutor.executeJavaCode(
                "{ return ((String)param0); }",
                Collections.singletonList("y"));
        assertEquals("y", result);
    }
}
