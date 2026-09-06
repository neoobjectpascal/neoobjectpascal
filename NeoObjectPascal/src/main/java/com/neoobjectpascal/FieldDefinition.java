package com.neoobjectpascal;

public class FieldDefinition {
    private final String name;
    private final Type type;
    private final Visibility visibility;
    private Object defaultValue;

    public enum Visibility {
        PUBLIC, PRIVATE, PROTECTED
    }

    public FieldDefinition(String name, Type type, Visibility visibility) {
        this.name = name;
        this.type = type;
        this.visibility = visibility != null ? visibility : Visibility.PUBLIC;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "FieldDefinition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", visibility=" + visibility +
                '}';
    }
}
