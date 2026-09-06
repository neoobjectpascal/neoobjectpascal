package com.neoobjectpascal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceDefinition {
    private final String name;
    private final Map<String, MethodSignature> methodSignatures;

    public InterfaceDefinition(String name) {
        this.name = name;
        this.methodSignatures = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, MethodSignature> getMethodSignatures() {
        return methodSignatures;
    }

    public void addMethodSignature(String name, MethodSignature signature) {
        methodSignatures.put(name, signature);
    }

    public MethodSignature getMethodSignature(String name) {
        return methodSignatures.get(name);
    }

    @Override
    public String toString() {
        return "InterfaceDefinition{" +
                "name='" + name + '\'' +
                ", methodSignatures=" + methodSignatures.keySet() +
                '}';
    }

    public static class MethodSignature {
        private final String name;
        private final List<String> parameters;
        private final Type returnType;

        public MethodSignature(String name, List<String> parameters, Type returnType) {
            this.name = name;
            this.parameters = parameters != null ? parameters : new ArrayList<>();
            this.returnType = returnType;
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

        @Override
        public String toString() {
            return "MethodSignature{" +
                    "name='" + name + '\'' +
                    ", parameters=" + parameters +
                    ", returnType=" + returnType +
                    '}';
        }
    }
}
