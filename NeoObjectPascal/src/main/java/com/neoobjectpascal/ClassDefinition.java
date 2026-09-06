package com.neoobjectpascal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDefinition {
    private final String name;
    private ClassDefinition parent;
    private final List<String> interfaces;
    private final Map<String, FieldDefinition> fields;
    private final Map<String, MethodDefinition> methods;
    private MethodDefinition constructor;

    public ClassDefinition(String name) {
        this.name = name;
        this.interfaces = new ArrayList<>();
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public ClassDefinition getParent() {
        return parent;
    }

    public void setParent(ClassDefinition parent) {
        this.parent = parent;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void addInterface(String interfaceName) {
        interfaces.add(interfaceName);
    }

    public Map<String, FieldDefinition> getFields() {
        return fields;
    }

    public void addField(String name, FieldDefinition field) {
        fields.put(name, field);
    }

    public FieldDefinition getField(String name) {
        FieldDefinition field = fields.get(name);
        if (field == null && parent != null) {
            return parent.getField(name);
        }
        return field;
    }

    public Map<String, MethodDefinition> getMethods() {
        return methods;
    }

    public void addMethod(String name, MethodDefinition method) {
        methods.put(name, method);
    }

    public MethodDefinition getMethod(String name) {
        MethodDefinition method = methods.get(name);
        if (method == null && parent != null) {
            return parent.getMethod(name);
        }
        return method;
    }

    public MethodDefinition getConstructor() {
        return constructor;
    }

    public void setConstructor(MethodDefinition constructor) {
        this.constructor = constructor;
    }

    public boolean isInstanceOf(String className) {
        if (name.equals(className)) {
            return true;
        }
        if (parent != null && parent.isInstanceOf(className)) {
            return true;
        }
        return interfaces.contains(className);
    }

    @Override
    public String toString() {
        return "ClassDefinition{" +
                "name='" + name + '\'' +
                ", parent=" + (parent != null ? parent.getName() : "null") +
                ", interfaces=" + interfaces +
                ", fields=" + fields.keySet() +
                ", methods=" + methods.keySet() +
                '}';
    }
}
