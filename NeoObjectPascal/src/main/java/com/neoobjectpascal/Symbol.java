package com.neoobjectpascal;

public class Symbol {
    private final String name;
    private final Type type;
    private Object value;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public Symbol(String name, Type type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

