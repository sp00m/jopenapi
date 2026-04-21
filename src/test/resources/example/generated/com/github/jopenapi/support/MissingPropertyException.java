package com.github.jopenapi.support;

/**
 * Thrown when a required property is missing during deserialization.
 */
public class MissingPropertyException extends IllegalArgumentException {

    private final String propertyName;

    public MissingPropertyException(String propertyName) {
        super("Property '" + propertyName + "' is required");
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

}

