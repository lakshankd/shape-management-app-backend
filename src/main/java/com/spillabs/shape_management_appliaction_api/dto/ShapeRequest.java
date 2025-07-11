package com.spillabs.shape_management_appliaction_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShapeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    private String coordinates;

    @Positive(message = "Radius must be positive")
    private Double radius;
}
