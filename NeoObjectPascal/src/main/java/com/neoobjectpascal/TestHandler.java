package com.neoobjectpascal;

class TestHandler {

    private final InterpreterState state;
    private final ExpressionEvaluator expr;

    TestHandler(InterpreterState state, ExpressionEvaluator expr) {
        this.state = state;
        this.expr = expr;
    }

    Object runTest(NeoObjectPascalParser.TestDeclarationContext ctx, Interpreter interp) {
        String testName = ctx.STRING().getText();
        testName = testName.substring(1, testName.length() - 1);
        TestResult result = new TestResult(testName);
        try {
            interp.visit(ctx.block());
            result.setPassed(true);
        } catch (AssertionError e) {
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            result.setPassed(false);
            result.setErrorMessage("Exception: " + e.getMessage());
        }
        state.testResults.add(result);
        return null;
    }

    Object executeExpect(NeoObjectPascalParser.ExpectStatementContext ctx, Interpreter interp) {
        Object actual = interp.visit(ctx.expression(0));

        if (ctx.TO_BE() != null) {
            Object expected = interp.visit(ctx.expression(1));
            if (!expr.areEqual(actual, expected)) {
                throw new AssertionError("Expected " + expected + " but got " + actual);
            }
        } else if (ctx.TO_EQUAL() != null) {
            Object expected = interp.visit(ctx.expression(1));
            if (!expr.areEqual(actual, expected)) {
                throw new AssertionError("Expected " + expected + " but got " + actual);
            }
        } else if (ctx.TO_BE_TRUE() != null) {
            if (!expr.isTrue(actual)) {
                throw new AssertionError("Expected true but got " + actual);
            }
        } else if (ctx.TO_BE_FALSE() != null) {
            if (expr.isTrue(actual)) {
                throw new AssertionError("Expected false but got " + actual);
            }
        } else if (ctx.TO_BE_NULL() != null) {
            if (actual != null) {
                throw new AssertionError("Expected null but got " + actual);
            }
        }
        return null;
    }

    Object executeMock(NeoObjectPascalParser.MockStatementContext ctx, Interpreter interp) {
        if (ctx.identifier().size() >= 1) {
            String targetName = ctx.identifier(0).getText();
            if (ctx.identifier().size() == 2) {
                String methodName = ctx.identifier(1).getText();
                Object returnValue = ctx.expression() != null ? interp.visit(ctx.expression()) : null;
                state.mockManager.mockMethod(targetName, methodName, returnValue);
            } else {
                Object returnValue = ctx.expression() != null ? interp.visit(ctx.expression()) : null;
                state.mockManager.mockFunction(targetName, returnValue);
            }
        }
        return null;
    }

    Object executeVerify(NeoObjectPascalParser.VerifyStatementContext ctx) {
        if (ctx.identifier().size() >= 1) {
            String targetName = ctx.identifier(0).getText();
            if (ctx.identifier().size() == 2) {
                String methodName = ctx.identifier(1).getText();
                if (!state.mockManager.verifyMethodCalled(targetName, methodName)) {
                    throw new AssertionError("Expected method " + targetName + "." + methodName + " to be called");
                }
            } else {
                if (!state.mockManager.verifyFunctionCalled(targetName)) {
                    throw new AssertionError("Expected function " + targetName + " to be called");
                }
            }
        }
        return null;
    }
}
