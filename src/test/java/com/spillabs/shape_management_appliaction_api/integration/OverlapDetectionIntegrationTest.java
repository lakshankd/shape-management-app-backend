package com.spillabs.shape_management_appliaction_api.integration;

import com.spillabs.shape_management_appliaction_api.dto.OverlapResponse;
import com.spillabs.shape_management_appliaction_api.model.Shape;
import com.spillabs.shape_management_appliaction_api.model.ShapeType;
import com.spillabs.shape_management_appliaction_api.repository.ShapeRepository;
import com.spillabs.shape_management_appliaction_api.service.ShapeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Overlap Detection Integration Tests")
class OverlapDetectionIntegrationTest {

    @Autowired
    private ShapeService shapeService;

    @Autowired
    private ShapeRepository shapeRepository;

    @BeforeEach
    void setUp() {
        shapeRepository.deleteAll();
    }

    @Test
    @DisplayName("Should detect overlapping circles")
    void shouldDetectOverlappingCircles() {
        Shape circle1 = Shape.builder()
                .name("Circle 1")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();

        Shape circle2 = Shape.builder()
                .name("Circle 2")
                .type(ShapeType.CIRCLE)
                .coordinates("6,0")
                .radius(5.0)
                .build();

        shapeRepository.save(circle1);
        shapeRepository.save(circle2);

        OverlapResponse response = shapeService.findOverlappingShapes();

        assertThat(response.getOverlappingShapeIds()).hasSize(2);
        assertThat(response.getOverlappingGroups()).hasSize(1);
        assertThat(response.getOverlappingGroups().get(0)).hasSize(2);
    }

    @Test
    @DisplayName("Should detect no overlaps for distant shapes")
    void shouldDetectNoOverlapsForDistantShapes() {
        Shape circle1 = Shape.builder()
                .name("Circle 1")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(2.0)
                .build();

        Shape circle2 = Shape.builder()
                .name("Circle 2")
                .type(ShapeType.CIRCLE)
                .coordinates("10,10")
                .radius(2.0)
                .build();

        shapeRepository.save(circle1);
        shapeRepository.save(circle2);

        OverlapResponse response = shapeService.findOverlappingShapes();

        assertThat(response.getOverlappingShapeIds()).isEmpty();
        assertThat(response.getOverlappingGroups()).isEmpty();
    }

    @Test
    @DisplayName("Should detect overlapping triangles")
    void shouldDetectOverlappingTriangles() {
        Shape triangle1 = Shape.builder()
                .name("Triangle 1")
                .type(ShapeType.TRIANGLE)
                .coordinates("0,0;4,0;2,4")
                .build();

        Shape triangle2 = Shape.builder()
                .name("Triangle 2")
                .type(ShapeType.TRIANGLE)
                .coordinates("1,1;5,1;3,5")
                .build();

        shapeRepository.save(triangle1);
        shapeRepository.save(triangle2);

        OverlapResponse response = shapeService.findOverlappingShapes();

        assertThat(response.getOverlappingShapeIds()).hasSize(2);
        assertThat(response.getOverlappingGroups()).hasSize(1);
    }

    @Test
    @DisplayName("Should detect circle-polygon overlap")
    void shouldDetectCirclePolygonOverlap() {
        Shape circle = Shape.builder()
                .name("Circle")
                .type(ShapeType.CIRCLE)
                .coordinates("2,2")
                .radius(3.0)
                .build();

        Shape rectangle = Shape.builder()
                .name("Rectangle")
                .type(ShapeType.RECTANGLE)
                .coordinates("0,0;4,0;4,4;0,4")
                .build();

        shapeRepository.save(circle);
        shapeRepository.save(rectangle);

        OverlapResponse response = shapeService.findOverlappingShapes();

        assertThat(response.getOverlappingShapeIds()).hasSize(2);
        assertThat(response.getOverlappingGroups()).hasSize(1);
    }

    @Test
    @DisplayName("Should handle multiple overlapping groups")
    void shouldHandleMultipleOverlappingGroups() {
        Shape circle1 = Shape.builder()
                .name("Circle 1")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(2.0)
                .build();

        Shape circle2 = Shape.builder()
                .name("Circle 2")
                .type(ShapeType.CIRCLE)
                .coordinates("1,0")
                .radius(2.0)
                .build();

        Shape circle3 = Shape.builder()
                .name("Circle 3")
                .type(ShapeType.CIRCLE)
                .coordinates("10,10")
                .radius(2.0)
                .build();

        Shape circle4 = Shape.builder()
                .name("Circle 4")
                .type(ShapeType.CIRCLE)
                .coordinates("11,10")
                .radius(2.0)
                .build();

        shapeRepository.save(circle1);
        shapeRepository.save(circle2);
        shapeRepository.save(circle3);
        shapeRepository.save(circle4);

        OverlapResponse response = shapeService.findOverlappingShapes();

        assertThat(response.getOverlappingShapeIds()).hasSize(4);
        assertThat(response.getOverlappingGroups()).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty response when no shapes exist")
    void shouldReturnEmptyResponseWhenNoShapesExist() {
        OverlapResponse response = shapeService.findOverlappingShapes();

        assertThat(response.getOverlappingShapeIds()).isEmpty();
        assertThat(response.getOverlappingGroups()).isEmpty();
    }

    @Test
    @DisplayName("Should return empty response when only one shape exists")
    void shouldReturnEmptyResponseWhenOnlyOneShapeExists() {
        Shape circle = Shape.builder()
                .name("Lonely Circle")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();

        shapeRepository.save(circle);

        OverlapResponse response = shapeService.findOverlappingShapes();

        assertThat(response.getOverlappingShapeIds()).isEmpty();
        assertThat(response.getOverlappingGroups()).isEmpty();
    }
}