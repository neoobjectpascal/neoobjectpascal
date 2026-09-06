package com.neoobjectpascal;

import java.util.HashMap;
import java.util.Map;

public class ObjectInstance {
    private final ClassDefinition classDefinition;
    private final Map<String, Object> fieldValues;

    public ObjectInstance(ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
        this.fieldValues = new HashMap<>();
        initializeFields();
    }

    private void initializeFields() {
        // Initialize fields from class hierarchy
        ClassDefinition current = classDefinition;
        while (current != null) {
            for (Map.Entry<String, FieldDefinition> entry : current.getFields().entrySet()) {
                String fieldName = entry.getKey();
                FieldDefinition fieldDef = entry.getValue();
                if (!fieldValues.containsKey(fieldName)) {
                    fieldValues.put(fieldName, getDefaultValue(fieldDef.getType()));
                }
            }
            current = current.getParent();
        }
    }

    private Object getDefaultValue(Type type) {
        switch (type) {
            case INTEGER:
                return 0;
            case REAL:
                return 0.0;
            case BOOLEAN:
                return false;
            case STRING:
                return "";
            default:
                return null;
        }
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public Object getFieldValue(String fieldName) {
        return fieldValues.get(fieldName);
    }

    public void setFieldValue(String fieldName, Object value) {
        FieldDefinition field = classDefinition.getField(fieldName);
        if (field != null) {
            fieldValues.put(fieldName, value);
        } else {
            throw new RuntimeException("Field '" + fieldName + "' not found in class '" + 
                                     classDefinition.getName() + "'");
        }
    }

    public boolean hasField(String fieldName) {
        return classDefinition.getField(fieldName) != null;
    }

    public boolean isInstanceOf(String className) {
        return classDefinition.isInstanceOf(className);
    }

    @Override
    public String toString() {
        return "ObjectInstance{" +
                "class=" + classDefinition.getName() +
                ", fields=" + fieldValues +
                '}';
    }
}
