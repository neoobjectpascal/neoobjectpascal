package com.neoobjectpascal;

import java.util.List;

public class MethodDefinition {
    private final String name;
    private final List<String> parameters;
    private final Type returnType;
    private final NeoObjectPascalParser.BlockContext body;
    private final Visibility visibility;
    private final boolean isVirtual;
    private final boolean isOverride;
    private final boolean isProcedure;

    public enum Visibility {
        PUBLIC, PRIVATE, PROTECTED
    }

    public MethodDefinition(String name, List<String> parameters, Type returnType,
                            NeoObjectPascalParser.BlockContext body, Visibility visibility,
                            boolean isVirtual, boolean isOverride, boolean isProcedure) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
        this.visibility = visibility != null ? visibility : Visibility.PUBLIC;
        this.isVirtual = isVirtual;
        this.isOverride = isOverride;
        this.isProcedure = isProcedure;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public NeoObjectPascalParser.BlockContext getBody() {
        return body;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public boolean isOverride() {
        return isOverride;
    }

    public boolean isProcedure() {
        return isProcedure;
    }

    @Override
    public String toString() {
        return "MethodDefinition{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                ", returnType=" + returnType +
                ", visibility=" + visibility +
                ", isVirtual=" + isVirtual +
                ", isOverride=" + isOverride +
                ", isProcedure=" + isProcedure +
                '}';
    }
}
