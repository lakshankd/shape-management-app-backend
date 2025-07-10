package com.spillabs.shape_management_appliaction_api.service;

import com.spillabs.shape_management_appliaction_api.dto.OverlapResponse;
import com.spillabs.shape_management_appliaction_api.dto.ShapeRequest;
import com.spillabs.shape_management_appliaction_api.dto.ShapeResponse;
import com.spillabs.shape_management_appliaction_api.exception.InvalidShapeException;
import com.spillabs.shape_management_appliaction_api.exception.ShapeNotFoundException;
import com.spillabs.shape_management_appliaction_api.model.Shape;
import com.spillabs.shape_management_appliaction_api.model.ShapeGeometry;
import com.spillabs.shape_management_appliaction_api.model.ShapeType;
import com.spillabs.shape_management_appliaction_api.repository.ShapeRepository;
import com.spillabs.shape_management_appliaction_api.util.CoordinateUtils;
import com.spillabs.shape_management_appliaction_api.util.ShapeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShapeService {

    private final ShapeRepository shapeRepository;

    public ShapeResponse createShape(ShapeRequest shapeRequest) {
        if (shapeRepository.existsByName(shapeRequest.getName())) {
            throw new InvalidShapeException("Shape name must be unique");
        }

        validate(shapeRequest);

        ShapeType shapeType = ShapeType.fromString(shapeRequest.getType());

        Shape shape = Shape.builder()
                .name(shapeRequest.getName())
                .type(shapeType)
                .coordinates(shapeRequest.getCoordinates())
                .radius(shapeRequest.getRadius())
                .build();

        Shape saved = shapeRepository.save(shape);
        return toDto(saved);
    }

    public List<ShapeResponse> getAllShapes() {
        return shapeRepository.findAll()
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    public ShapeResponse
    getShapeById(Long id) {
        Shape shape = shapeRepository.findById(id)
                .orElseThrow(() -> new ShapeNotFoundException("Shape not found with ID: " + id));

        return toDto(shape);
    }

    public ShapeResponse updateShape(Long id, ShapeRequest shapeRequest) {
        Shape existing = shapeRepository.findById(id)
                .orElseThrow(() -> new ShapeNotFoundException("Shape not found with ID: " + id));

        shapeRepository.findByName(shapeRequest.getName())
                .ifPresent(shapeWithSameName -> {
                    if (!shapeWithSameName.getId().equals(id)) {
                        throw new InvalidShapeException("Another shape with the same name already exists");
                    }
                });

        validate(shapeRequest);

        ShapeType shapeType = ShapeType.fromString(shapeRequest.getType());

        existing.setName(shapeRequest.getName());
        existing.setType(shapeType);
        existing.setCoordinates(shapeRequest.getCoordinates());
        existing.setRadius(shapeRequest.getRadius());

        Shape updated = shapeRepository.save(existing);
        return toDto(updated);
    }

    public void deleteShape(Long id) {
        if (!shapeRepository.existsById(id)) {
            throw new ShapeNotFoundException("Shape not found with ID: " + id);
        }
        shapeRepository.deleteById(id);
    }

    public OverlapResponse findOverlappingShapes() {
        List<Shape> allShapes = shapeRepository.findAll();
        List<ShapeGeometry> geometries = allShapes.stream().map(ShapeUtils::toGeometry).toList();

        Set<Long> overlappingIds = new HashSet<>();
        List<List<Long>> overlappingGroups = new ArrayList<>();

        for (int i = 0; i < geometries.size(); i++) {
            for (int j = i + 1; j < geometries.size(); j++) {
                if (ShapeUtils.doOverlap(geometries.get(i), geometries.get(j))) {
                    overlappingIds.add(geometries.get(i).getId());
                    overlappingIds.add(geometries.get(j).getId());
                    overlappingGroups.add(List.of(geometries.get(i).getId(), geometries.get(j).getId()));
                }
            }
        }

        return new OverlapResponse(overlappingIds, overlappingGroups);
    }

    private void validate(ShapeRequest shapeRequest) {
        ShapeType type = ShapeType.fromString(shapeRequest.getType());

        if (type == ShapeType.CIRCLE) {
            if (shapeRequest.getRadius() == null || shapeRequest.getRadius() <= 0) {
                throw new InvalidShapeException("Circle must have a positive radius");
            }

            List<double[]> center = CoordinateUtils.parseCoordinates(shapeRequest.getCoordinates());
            if (center.size() != 1) {
                throw new InvalidShapeException("Circle must have exactly 1 center coordinate (x,y)");
            }
            return;
        }

        List<double[]> points = CoordinateUtils.parseCoordinates(shapeRequest.getCoordinates());

        if (points.isEmpty()) {
            throw new InvalidShapeException(type + " must have coordinates");
        }

        switch (type) {
            case TRIANGLE -> {
                if (points.size() != 3) {
                    throw new InvalidShapeException("Triangle must have exactly 3 coordinates");
                }
            }
            case RECTANGLE -> {
                if (points.size() != 4) {
                    throw new InvalidShapeException("Rectangle must have exactly 4 coordinates");
                }
            }
            case POLYGON -> {
                if (points.size() < 3) {
                    throw new InvalidShapeException("Polygon must have at least 3 coordinates");
                }
            }
            default -> throw new InvalidShapeException("Unsupported shape type");
        }
    }

    private ShapeResponse toDto(Shape shape) {
        return new ShapeResponse(
                shape.getId(),
                shape.getName(),
                shape.getType(),
                shape.getCoordinates(),
                shape.getRadius()
        );
    }
}
