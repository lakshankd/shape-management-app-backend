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
                validateTriangle(points);
            }
            case RECTANGLE -> {
                if (points.size() != 4) {
                    throw new InvalidShapeException("Rectangle must have exactly 4 coordinates");
                }
                validateRectangle(points);
            }
            case POLYGON -> {
                if (points.size() < 3) {
                    throw new InvalidShapeException("Polygon must have at least 3 coordinates");
                }
                validatePolygon(points);
            }
            default -> throw new InvalidShapeException("Unsupported shape type");
        }
    }

    private void validateTriangle(List<double[]> points) {
        // Check if three points are collinear (don't form a triangle)
        double[] p1 = points.get(0);
        double[] p2 = points.get(1);
        double[] p3 = points.get(2);

        // Calculate area using cross product
        // Area = 0.5 * |x1(y2-y3) + x2(y3-y1) + x3(y1-y2)|
        double area = Math.abs(p1[0] * (p2[1] - p3[1]) +
                p2[0] * (p3[1] - p1[1]) +
                p3[0] * (p1[1] - p2[1])) / 2.0;

        if (area < 1e-10) { // Using small epsilon for floating point comparison
            throw new InvalidShapeException("Triangle coordinates are collinear - cannot form a valid triangle");
        }
    }

    private void validateRectangle(List<double[]> points) {
        // Check if all points are distinct
        if (hasDuplicatePoints(points)) {
            throw new InvalidShapeException("Rectangle cannot have duplicate points");
        }

        // Sort points to form a proper rectangle (clockwise or counter-clockwise)
        List<double[]> sortedPoints = sortRectanglePoints(points);

        // Check if it forms a valid rectangle
        if (!isValidRectangle(sortedPoints)) {
            throw new InvalidShapeException("Given coordinates do not form a valid rectangle");
        }
    }

    private void validatePolygon(List<double[]> points) {
        // Check for duplicate points
        if (hasDuplicatePoints(points)) {
            throw new InvalidShapeException("Polygon cannot have duplicate points");
        }

        // Check if polygon is self-intersecting
        if (isSelfIntersecting(points)) {
            throw new InvalidShapeException("Polygon cannot be self-intersecting");
        }

        // Check if all points are collinear
        if (areAllPointsCollinear(points)) {
            throw new InvalidShapeException("All polygon points cannot be collinear");
        }
    }

    private boolean hasDuplicatePoints(List<double[]> points) {
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                if (Math.abs(points.get(i)[0] - points.get(j)[0]) < 1e-10 &&
                        Math.abs(points.get(i)[1] - points.get(j)[1]) < 1e-10) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<double[]> sortRectanglePoints(List<double[]> points) {
        // Find the centroid
        double centerX = points.stream().mapToDouble(p -> p[0]).average().orElse(0);
        double centerY = points.stream().mapToDouble(p -> p[1]).average().orElse(0);

        // Sort points by angle from centroid
        return points.stream()
                .sorted((p1, p2) -> {
                    double angle1 = Math.atan2(p1[1] - centerY, p1[0] - centerX);
                    double angle2 = Math.atan2(p2[1] - centerY, p2[0] - centerX);
                    return Double.compare(angle1, angle2);
                })
                .collect(Collectors.toList());
    }

    private boolean isValidRectangle(List<double[]> points) {
        // Calculate all four sides
        double[] sides = new double[4];
        for (int i = 0; i < 4; i++) {
            double[] p1 = points.get(i);
            double[] p2 = points.get((i + 1) % 4);
            sides[i] = Math.sqrt(Math.pow(p2[0] - p1[0], 2) + Math.pow(p2[1] - p1[1], 2));
        }

        // Check if opposite sides are equal
        boolean oppositeSidesEqual = Math.abs(sides[0] - sides[2]) < 1e-10 &&
                Math.abs(sides[1] - sides[3]) < 1e-10;

        if (!oppositeSidesEqual) {
            return false;
        }

        // Check if all angles are 90 degrees
        for (int i = 0; i < 4; i++) {
            double[] p1 = points.get(i);
            double[] p2 = points.get((i + 1) % 4);
            double[] p3 = points.get((i + 2) % 4);

            // Calculate vectors
            double[] v1 = {p1[0] - p2[0], p1[1] - p2[1]};
            double[] v2 = {p3[0] - p2[0], p3[1] - p2[1]};

            // Calculate dot product
            double dotProduct = v1[0] * v2[0] + v1[1] * v2[1];

            // Check if angle is 90 degrees (dot product should be 0)
            if (Math.abs(dotProduct) > 1e-10) {
                return false;
            }
        }

        return true;
    }

    private boolean isSelfIntersecting(List<double[]> points) {
        int n = points.size();

        // Check every pair of non-adjacent edges
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                // Skip if edges share a vertex
                if (j == n - 1 && i == 0) continue;

                double[] p1 = points.get(i);
                double[] p2 = points.get((i + 1) % n);
                double[] p3 = points.get(j);
                double[] p4 = points.get((j + 1) % n);

                if (doLinesIntersect(p1, p2, p3, p4)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean doLinesIntersect(double[] p1, double[] p2, double[] p3, double[] p4) {
        // Calculate the direction of the four line segments
        double d1 = direction(p3, p4, p1);
        double d2 = direction(p3, p4, p2);
        double d3 = direction(p1, p2, p3);
        double d4 = direction(p1, p2, p4);

        // Check if line segments intersect
        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
                ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
            return true;
        }

        // Check if points are collinear and on segment
        if (d1 == 0 && onSegment(p3, p1, p4)) return true;
        if (d2 == 0 && onSegment(p3, p2, p4)) return true;
        if (d3 == 0 && onSegment(p1, p3, p2)) return true;
        if (d4 == 0 && onSegment(p1, p4, p2)) return true;

        return false;
    }

    private double direction(double[] p1, double[] p2, double[] p3) {
        return (p3[0] - p1[0]) * (p2[1] - p1[1]) - (p2[0] - p1[0]) * (p3[1] - p1[1]);
    }

    private boolean onSegment(double[] p, double[] q, double[] r) {
        return q[0] <= Math.max(p[0], r[0]) && q[0] >= Math.min(p[0], r[0]) &&
                q[1] <= Math.max(p[1], r[1]) && q[1] >= Math.min(p[1], r[1]);
    }

    private boolean areAllPointsCollinear(List<double[]> points) {
        if (points.size() < 3) return true;

        double[] p1 = points.get(0);
        double[] p2 = points.get(1);

        for (int i = 2; i < points.size(); i++) {
            double[] p3 = points.get(i);

            // Calculate cross product to check collinearity
            double crossProduct = (p2[0] - p1[0]) * (p3[1] - p1[1]) - (p2[1] - p1[1]) * (p3[0] - p1[0]);

            if (Math.abs(crossProduct) > 1e-10) {
                return false;
            }
        }

        return true;
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
