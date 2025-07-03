package com.spillabs.shape_management_appliaction_api.controller;

import com.spillabs.shape_management_appliaction_api.dto.OverlapResponse;
import com.spillabs.shape_management_appliaction_api.dto.ShapeRequest;
import com.spillabs.shape_management_appliaction_api.dto.ShapeResponse;
import com.spillabs.shape_management_appliaction_api.service.ShapeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shapes")
@RequiredArgsConstructor
public class ShapeController {

    private final ShapeService shapeService;

    @PostMapping
    public ResponseEntity<ShapeResponse> createShape(@Valid @RequestBody ShapeRequest dto) {
        return ResponseEntity.ok(shapeService.createShape(dto));
    }

    @GetMapping
    public ResponseEntity<List<ShapeResponse>> getAllShapes() {
        return ResponseEntity.ok(shapeService.getAllShapes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShapeResponse> getShape(@PathVariable Long id) {
        return ResponseEntity.ok(shapeService.getShapeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShapeResponse> updateShape(@PathVariable Long id,
                                                     @Valid @RequestBody ShapeRequest dto) {
        return ResponseEntity.ok(shapeService.updateShape(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShape(@PathVariable Long id) {
        shapeService.deleteShape(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/overlaps")
    public ResponseEntity<OverlapResponse> getOverlaps() {
        return ResponseEntity.ok(shapeService.findOverlappingShapes());
    }
}
