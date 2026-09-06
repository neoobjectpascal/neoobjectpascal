package com.neoobjectpascal;

public class NeoException extends RuntimeException {
    private final Object value;

    public NeoException(Object value) {
        super(String.valueOf(value));
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
