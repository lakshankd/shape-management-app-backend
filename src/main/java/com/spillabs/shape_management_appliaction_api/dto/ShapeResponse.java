package com.spillabs.shape_management_appliaction_api.dto;

import com.spillabs.shape_management_appliaction_api.model.ShapeType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShapeResponse {

    private Long id;
    private String name;
    private ShapeType type;
    private String coordinates;
    private Double radius;
}
