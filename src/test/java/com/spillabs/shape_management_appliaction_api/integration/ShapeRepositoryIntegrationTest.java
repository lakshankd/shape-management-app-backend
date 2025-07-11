package com.spillabs.shape_management_appliaction_api.integration;

import com.spillabs.shape_management_appliaction_api.model.Shape;
import com.spillabs.shape_management_appliaction_api.model.ShapeType;
import com.spillabs.shape_management_appliaction_api.repository.ShapeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Shape Repository Integration Tests")
class ShapeRepositoryIntegrationTest {

    @Autowired
    private ShapeRepository shapeRepository;

    @BeforeEach
    void setUp() {
        shapeRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve shape successfully")
    void shouldSaveAndRetrieveShape() {
        Shape circle = Shape.builder()
                .name("Test Circle")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();

        Shape savedShape = shapeRepository.save(circle);

        assertThat(savedShape.getId()).isNotNull();
        assertThat(savedShape.getName()).isEqualTo("Test Circle");
        assertThat(savedShape.getType()).isEqualTo(ShapeType.CIRCLE);
        assertThat(savedShape.getCoordinates()).isEqualTo("0,0");
        assertThat(savedShape.getRadius()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should find shape by name")
    void shouldFindShapeByName() {
        Shape triangle = Shape.builder()
                .name("Unique Triangle")
                .type(ShapeType.TRIANGLE)
                .coordinates("0,0;5,0;2.5,5")
                .build();
        shapeRepository.save(triangle);

        Optional<Shape> foundShape = shapeRepository.findByName("Unique Triangle");

        assertThat(foundShape).isPresent();
        assertThat(foundShape.get().getName()).isEqualTo("Unique Triangle");
        assertThat(foundShape.get().getType()).isEqualTo(ShapeType.TRIANGLE);
    }

    @Test
    @DisplayName("Should return empty when shape name not found")
    void shouldReturnEmptyWhenShapeNameNotFound() {
        Optional<Shape> foundShape = shapeRepository.findByName("Non-existent Shape");

        assertThat(foundShape).isEmpty();
    }

    @Test
    @DisplayName("Should check if shape exists by name")
    void shouldCheckIfShapeExistsByName() {
        Shape rectangle = Shape.builder()
                .name("Test Rectangle")
                .type(ShapeType.RECTANGLE)
                .coordinates("0,0;4,0;4,3;0,3")
                .build();
        shapeRepository.save(rectangle);

        assertThat(shapeRepository.existsByName("Test Rectangle")).isTrue();
        assertThat(shapeRepository.existsByName("Non-existent Shape")).isFalse();
    }

    @Test
    @DisplayName("Should find all shapes")
    void shouldFindAllShapes() {
        Shape circle = Shape.builder()
                .name("Circle")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();

        Shape triangle = Shape.builder()
                .name("Triangle")
                .type(ShapeType.TRIANGLE)
                .coordinates("0,0;5,0;2.5,5")
                .build();

        shapeRepository.save(circle);
        shapeRepository.save(triangle);

        List<Shape> allShapes = shapeRepository.findAll();

        assertThat(allShapes).hasSize(2);
        assertThat(allShapes).extracting(Shape::getName).containsExactlyInAnyOrder("Circle", "Triangle");
    }

    @Test
    @DisplayName("Should delete shape by ID")
    void shouldDeleteShapeById() {
        Shape shape = Shape.builder()
                .name("To Delete")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(3.0)
                .build();
        Shape savedShape = shapeRepository.save(shape);

        shapeRepository.deleteById(savedShape.getId());

        assertThat(shapeRepository.findById(savedShape.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should update shape")
    void shouldUpdateShape() {
        Shape originalShape = Shape.builder()
                .name("Original Name")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();
        Shape savedShape = shapeRepository.save(originalShape);

        savedShape.setName("Updated Name");
        savedShape.setRadius(7.0);
        Shape updatedShape = shapeRepository.save(savedShape);

        assertThat(updatedShape.getId()).isEqualTo(savedShape.getId());
        assertThat(updatedShape.getName()).isEqualTo("Updated Name");
        assertThat(updatedShape.getRadius()).isEqualTo(7.0);
    }

    @Test
    @DisplayName("Should handle polygon shapes")
    void shouldHandlePolygonShapes() {
        Shape polygon = Shape.builder()
                .name("Test Polygon")
                .type(ShapeType.POLYGON)
                .coordinates("0,0;5,0;5,5;0,5;2.5,2.5")
                .build();

        Shape savedPolygon = shapeRepository.save(polygon);

        assertThat(savedPolygon.getId()).isNotNull();
        assertThat(savedPolygon.getName()).isEqualTo("Test Polygon");
        assertThat(savedPolygon.getType()).isEqualTo(ShapeType.POLYGON);
        assertThat(savedPolygon.getCoordinates()).isEqualTo("0,0;5,0;5,5;0,5;2.5,2.5");
        assertThat(savedPolygon.getRadius()).isNull();
    }
}