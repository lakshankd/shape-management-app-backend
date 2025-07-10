package com.spillabs.shape_management_appliaction_api.model;

import com.spillabs.shape_management_appliaction_api.exception.InvalidShapeException;

public enum ShapeType {

    CIRCLE,
    RECTANGLE,
    TRIANGLE,
    POLYGON;

    public static ShapeType fromString(String value) {
        try {
            return ShapeType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidShapeException("Unsupported shape type: " + value);
        }
    }
}
