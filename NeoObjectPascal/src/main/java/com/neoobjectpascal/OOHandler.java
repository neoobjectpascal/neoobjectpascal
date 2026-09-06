package com.neoobjectpascal;

import java.util.ArrayList;
import java.util.List;

class OOHandler {

    private final InterpreterState state;

    OOHandler(InterpreterState state) {
        this.state = state;
    }

    // ---- Function registration ----

    Object registerFunction(NeoObjectPascalParser.FunctionDeclarationContext ctx) {
        String name = ctx.identifier().getText();
        List<String> params = new ArrayList<>();
        if (ctx.parameterList() != null) {
            for (NeoObjectPascalParser.ParameterContext p : ctx.parameterList().parameter()) {
                params.add(p.identifier().getText());
            }
        }
        state.functions.put(name, new Function(name, params, ctx.block()));
        return null;
    }

    // ---- Class registration ----

    Object registerClass(NeoObjectPascalParser.ClassDeclarationContext ctx, Interpreter interp) {
        String className = ctx.identifier(0).getText();
        ClassDefinition classDef = new ClassDefinition(className);

        if (ctx.EXTENDS() != null) {
            String parentName = ctx.identifier(1).getText();
            ClassDefinition parent = state.classes.get(parentName);
            if (parent == null) throw new RuntimeException("Parent class '" + parentName + "' not found");
            classDef.setParent(parent);
        }

        if (ctx.IMPLEMENTS() != null) {
            int startIdx = ctx.EXTENDS() != null ? 2 : 1;
            for (int i = startIdx; i < ctx.identifier().size(); i++) {
                classDef.addInterface(ctx.identifier(i).getText());
            }
        }

        if (ctx.classBody() != null) {
            for (NeoObjectPascalParser.ClassMemberContext member : ctx.classBody().classMember()) {
                if (member.fieldDeclaration() != null) {
                    processField(member.fieldDeclaration(), classDef);
                } else if (member.methodDeclaration() != null) {
                    processMethod(member.methodDeclaration(), classDef);
                } else if (member.procedureDeclaration() != null) {
                    processProcedure(member.procedureDeclaration(), classDef);
                } else if (member.constructorDeclaration() != null) {
                    processConstructor(member.constructorDeclaration(), classDef);
                }
            }
        }

        state.classes.put(className, classDef);
        return null;
    }

    // ---- Interface registration ----

    Object registerInterface(NeoObjectPascalParser.InterfaceDeclarationContext ctx) {
        String name = ctx.identifier().getText();
        InterfaceDefinition def = new InterfaceDefinition(name);
        for (NeoObjectPascalParser.MethodSignatureContext sig : ctx.methodSignature()) {
            String methodName = sig.identifier().getText();
            List<String> params = new ArrayList<>();
            if (sig.parameterList() != null) {
                for (NeoObjectPascalParser.ParameterContext p : sig.parameterList().parameter()) {
                    params.add(p.identifier().getText());
                }
            }
            Type returnType = InterpreterState.resolveType(sig.type());
            def.addMethodSignature(methodName, new InterfaceDefinition.MethodSignature(methodName, params, returnType));
        }
        state.interfaces.put(name, def);
        return null;
    }

    // ---- Object creation ----

    Object createInstance(NeoObjectPascalParser.NewExpressionContext ctx, Interpreter interp) {
        String className = ctx.identifier().getText();
        ClassDefinition classDef = state.classes.get(className);
        if (classDef == null) throw new RuntimeException("Class '" + className + "' not found");

        ObjectInstance instance = new ObjectInstance(classDef);

        MethodDefinition constructor = classDef.getConstructor();
        if (constructor != null) {
            List<Object> args = collectArgs(ctx.expressionList(), interp);
            executeMethod(instance, constructor, args, interp);
        }
        return instance;
    }

    // ---- Member access ----

    Object resolveMemberAccess(NeoObjectPascalParser.MemberAccessContext ctx, Interpreter interp) {
        List<String> parts = new ArrayList<>();
        boolean isSelf = ctx.SELF() != null;
        if (isSelf) {
            parts.add("self");
        } else {
            parts.add(ctx.identifier(0).getText());
        }
        int startIdx = isSelf ? 0 : 1;
        for (int i = startIdx; i < ctx.identifier().size(); i++) {
            parts.add(ctx.identifier(i).getText());
        }

        Object current;
        if (parts.get(0).equals("self")) {
            if (state.currentInstance == null) {
                throw new RuntimeException("'self' used outside of object context");
            }
            current = state.currentInstance;
        } else {
            Symbol symbol = state.symbolTable.get(parts.get(0));
            current = symbol != null ? symbol.getValue() : null;
        }

        for (int i = 1; i < parts.size(); i++) {
            String memberName = parts.get(i);

            // Record (Map) field access: `record.field`. Returns the value directly
            // (records are read-only value bags — no MemberAccessResult / assignment target).
            if (current instanceof java.util.Map) {
                Object value = ((java.util.Map<?, ?>) current).get(memberName);
                if (i == parts.size() - 1) return value;
                current = value;
                continue;
            }

            if (!(current instanceof ObjectInstance)) {
                throw new RuntimeException("Cannot access member '" + parts.get(i) + "' on non-object");
            }
            ObjectInstance obj = (ObjectInstance) current;

            if (i == parts.size() - 1) {
                return new MemberAccessResult(obj, memberName);
            }
            if (!obj.hasField(memberName)) {
                throw new RuntimeException("Field '" + memberName + "' not found in class '" +
                                           obj.getClassDefinition().getName() + "'");
            }
            current = obj.getFieldValue(memberName);
        }
        return current;
    }

    // ---- Method call ----

    Object executeMethodCall(NeoObjectPascalParser.MethodCallContext ctx, Interpreter interp) {
        Object result = interp.visit(ctx.memberAccess());
        if (!(result instanceof MemberAccessResult)) {
            throw new RuntimeException("Invalid method call target");
        }
        MemberAccessResult mar = (MemberAccessResult) result;
        MethodDefinition method = mar.object.getClassDefinition().getMethod(mar.memberName);
        if (method == null) {
            throw new RuntimeException("Method '" + mar.memberName + "' not found in class '" +
                                       mar.object.getClassDefinition().getName() + "'");
        }
        List<Object> args = collectArgs(ctx.expressionList(), interp);
        return executeMethod(mar.object, method, args, interp);
    }

    // ---- Method execution ----

    Object executeMethod(ObjectInstance instance, MethodDefinition method, List<Object> args, Interpreter interp) {
        String className = instance.getClassDefinition().getName();
        String methodName = method.getName();

        // Coverage: a public method invoked during a test run counts as covered.
        if (method.getVisibility() == MethodDefinition.Visibility.PUBLIC) {
            CoverageTracker.recordInvocation(className, methodName);
        }

        if (state.mockManager.isMethodMocked(className, methodName)) {
            return state.mockManager.getMockedMethodReturn(className, methodName, args);
        }
        state.mockManager.recordMethodCallForVerify(className, methodName, args);

        SymbolTable newScope = new SymbolTable(state.symbolTable);
        for (int i = 0; i < method.getParameters().size() && i < args.size(); i++) {
            String param = method.getParameters().get(i);
            newScope.put(param, new Symbol(param, null, args.get(i)));
        }

        SymbolTable oldScope = state.symbolTable;
        ObjectInstance oldInstance = state.currentInstance;
        state.symbolTable = newScope;
        state.currentInstance = instance;

        boolean framed = interp.enterDebugFrame(className + "." + methodName, newScope, instance);
        try {
            interp.visit(method.getBody());
            return null;
        } catch (ReturnValue rv) {
            return rv.value;
        } finally {
            state.symbolTable = oldScope;
            state.currentInstance = oldInstance;
            if (framed) state.debugger.exitFrame();
        }
    }

    // ---- Private helpers ----

    private void processField(NeoObjectPascalParser.FieldDeclarationContext ctx, ClassDefinition classDef) {
        String name = ctx.identifier().getText();
        Type type = InterpreterState.resolveType(ctx.type());
        classDef.addField(name, new FieldDefinition(name, type, FieldDefinition.Visibility.PRIVATE));
    }

    private void processMethod(NeoObjectPascalParser.MethodDeclarationContext ctx, ClassDefinition classDef) {
        String name = ctx.identifier().getText();
        List<String> params = extractParams(ctx.parameterList());
        Type returnType = InterpreterState.resolveType(ctx.type());
        MethodDefinition.Visibility vis = methodVisibility(ctx);
        boolean isVirtual = ctx.VIRTUAL() != null;
        boolean isOverride = ctx.OVERRIDE() != null;
        classDef.addMethod(name, new MethodDefinition(name, params, returnType, ctx.block(), vis, isVirtual, isOverride, false));
    }

    private void processProcedure(NeoObjectPascalParser.ProcedureDeclarationContext ctx, ClassDefinition classDef) {
        String name = ctx.identifier().getText();
        List<String> params = extractParams(ctx.parameterList());
        MethodDefinition.Visibility vis = procedureVisibility(ctx);
        boolean isVirtual = ctx.VIRTUAL() != null;
        boolean isOverride = ctx.OVERRIDE() != null;
        classDef.addMethod(name, new MethodDefinition(name, params, null, ctx.block(), vis, isVirtual, isOverride, true));
    }

    private void processConstructor(NeoObjectPascalParser.ConstructorDeclarationContext ctx, ClassDefinition classDef) {
        String name = ctx.identifier().getText();
        List<String> params = extractParams(ctx.parameterList());
        classDef.setConstructor(new MethodDefinition(name, params, null, ctx.block(),
                MethodDefinition.Visibility.PUBLIC, false, false, true));
    }

    private List<String> extractParams(NeoObjectPascalParser.ParameterListContext ctx) {
        List<String> params = new ArrayList<>();
        if (ctx != null) {
            for (NeoObjectPascalParser.ParameterContext p : ctx.parameter()) {
                params.add(p.identifier().getText());
            }
        }
        return params;
    }

    private List<Object> collectArgs(NeoObjectPascalParser.ExpressionListContext listCtx, Interpreter interp) {
        List<Object> args = new ArrayList<>();
        if (listCtx != null) {
            for (NeoObjectPascalParser.ExpressionContext e : listCtx.expression()) {
                args.add(interp.visit(e));
            }
        }
        return args;
    }

    private MethodDefinition.Visibility methodVisibility(NeoObjectPascalParser.MethodDeclarationContext ctx) {
        if (ctx.PUBLIC() != null) return MethodDefinition.Visibility.PUBLIC;
        if (ctx.PRIVATE() != null) return MethodDefinition.Visibility.PRIVATE;
        if (ctx.PROTECTED() != null) return MethodDefinition.Visibility.PROTECTED;
        return MethodDefinition.Visibility.PUBLIC;
    }

    private MethodDefinition.Visibility procedureVisibility(NeoObjectPascalParser.ProcedureDeclarationContext ctx) {
        if (ctx.PUBLIC() != null) return MethodDefinition.Visibility.PUBLIC;
        if (ctx.PRIVATE() != null) return MethodDefinition.Visibility.PRIVATE;
        if (ctx.PROTECTED() != null) return MethodDefinition.Visibility.PROTECTED;
        return MethodDefinition.Visibility.PUBLIC;
    }
}
