package com.github.jopenapi.support;

/**
 * Thrown when a property value is invalid during deserialization.
 */
public class InvalidPropertyException extends IllegalArgumentException {

    private final String propertyName;
    private final Object propertyValue;

    public InvalidPropertyException(String propertyName, Object propertyValue) {
        super("No " + propertyName + " with value " + propertyValue);
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

}

