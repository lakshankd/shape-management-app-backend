package com.spillabs.shape_management_appliaction_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class OverlapResponse {

    private Set<Long> overlappingShapeIds;
    private List<List<Long>> overlappingGroups;
}
