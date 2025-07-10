package com.spillabs.shape_management_appliaction_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShapeGeometry {
    private Long id;
    private String type;
    private List<Point> points; // For polygon
    private Double radius;      // For circle
    private Point center;       // For circle

    public boolean isCircle() {
        return "CIRCLE".equalsIgnoreCase(type);
    }
}