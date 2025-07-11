package com.spillabs.shape_management_appliaction_api.service;

import com.spillabs.shape_management_appliaction_api.dto.OverlapResponse;
import com.spillabs.shape_management_appliaction_api.dto.ShapeRequest;
import com.spillabs.shape_management_appliaction_api.dto.ShapeResponse;
import com.spillabs.shape_management_appliaction_api.exception.InvalidShapeException;
import com.spillabs.shape_management_appliaction_api.exception.ShapeNotFoundException;
import com.spillabs.shape_management_appliaction_api.model.Shape;
import com.spillabs.shape_management_appliaction_api.model.ShapeType;
import com.spillabs.shape_management_appliaction_api.repository.ShapeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShapeService Tests")
class ShapeServiceTest {

    @Mock
    private ShapeRepository shapeRepository;

    @InjectMocks
    private ShapeService shapeService;

    private ShapeRequest validCircleRequest;
    private ShapeRequest validTriangleRequest;
    private ShapeRequest validRectangleRequest;
    private ShapeRequest validPolygonRequest;
    private Shape savedCircle;
    private Shape savedTriangle;

    @BeforeEach
    void setUp() {
        validCircleRequest = new ShapeRequest();
        validCircleRequest.setName("Test Circle");
        validCircleRequest.setType("CIRCLE");
        validCircleRequest.setCoordinates("5.0,5.0");
        validCircleRequest.setRadius(3.0);

        validTriangleRequest = new ShapeRequest();
        validTriangleRequest.setName("Test Triangle");
        validTriangleRequest.setType("TRIANGLE");
        validTriangleRequest.setCoordinates("0.0,0.0;3.0,0.0;1.5,3.0");

        validRectangleRequest = new ShapeRequest();
        validRectangleRequest.setName("Test Rectangle");
        validRectangleRequest.setType("RECTANGLE");
        validRectangleRequest.setCoordinates("0.0,0.0;2.0,0.0;2.0,2.0;0.0,2.0");

        validPolygonRequest = new ShapeRequest();
        validPolygonRequest.setName("Test Polygon");
        validPolygonRequest.setType("POLYGON");
        validPolygonRequest.setCoordinates("0.0,0.0;2.0,0.0;3.0,2.0;1.0,3.0;-1.0,2.0");

        savedCircle = Shape.builder()
                .id(1L)
                .name("Test Circle")
                .type(ShapeType.CIRCLE)
                .coordinates("5.0,5.0")
                .radius(3.0)
                .build();

        savedTriangle = Shape.builder()
                .id(2L)
                .name("Test Triangle")
                .type(ShapeType.TRIANGLE)
                .coordinates("0.0,0.0;3.0,0.0;1.5,3.0")
                .build();
    }

    @Nested
    @DisplayName("Create Shape Tests")
    class CreateShapeTests {

        @Test
        @DisplayName("Should create valid circle successfully")
        void shouldCreateValidCircle() {
            when(shapeRepository.existsByName(anyString())).thenReturn(false);
            when(shapeRepository.save(any(Shape.class))).thenReturn(savedCircle);

            ShapeResponse response = shapeService.createShape(validCircleRequest);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("Test Circle", response.getName());
            assertEquals(ShapeType.CIRCLE, response.getType());
            assertEquals("5.0,5.0", response.getCoordinates());
            assertEquals(3.0, response.getRadius());

            verify(shapeRepository).existsByName("Test Circle");
            verify(shapeRepository).save(any(Shape.class));
        }

        @Test
        @DisplayName("Should create valid triangle successfully")
        void shouldCreateValidTriangle() {
            when(shapeRepository.existsByName(anyString())).thenReturn(false);
            when(shapeRepository.save(any(Shape.class))).thenReturn(savedTriangle);

            ShapeResponse response = shapeService.createShape(validTriangleRequest);

            assertNotNull(response);
            assertEquals(2L, response.getId());
            assertEquals("Test Triangle", response.getName());
            assertEquals(ShapeType.TRIANGLE, response.getType());
            assertEquals("0.0,0.0;3.0,0.0;1.5,3.0", response.getCoordinates());
            assertNull(response.getRadius());

            verify(shapeRepository).existsByName("Test Triangle");
            verify(shapeRepository).save(any(Shape.class));
        }

        @Test
        @DisplayName("Should create valid rectangle successfully")
        void shouldCreateValidRectangle() {
            Shape savedRectangle = Shape.builder()
                    .id(3L)
                    .name("Test Rectangle")
                    .type(ShapeType.RECTANGLE)
                    .coordinates("0.0,0.0;2.0,0.0;2.0,2.0;0.0,2.0")
                    .build();

            when(shapeRepository.existsByName(anyString())).thenReturn(false);
            when(shapeRepository.save(any(Shape.class))).thenReturn(savedRectangle);

            ShapeResponse response = shapeService.createShape(validRectangleRequest);

            assertNotNull(response);
            assertEquals(3L, response.getId());
            assertEquals("Test Rectangle", response.getName());
            assertEquals(ShapeType.RECTANGLE, response.getType());
            assertEquals("0.0,0.0;2.0,0.0;2.0,2.0;0.0,2.0", response.getCoordinates());
            assertNull(response.getRadius());
        }

        @Test
        @DisplayName("Should create valid polygon successfully")
        void shouldCreateValidPolygon() {
            Shape savedPolygon = Shape.builder()
                    .id(4L)
                    .name("Test Polygon")
                    .type(ShapeType.POLYGON)
                    .coordinates("0.0,0.0;2.0,0.0;3.0,2.0;1.0,3.0;-1.0,2.0")
                    .build();

            when(shapeRepository.existsByName(anyString())).thenReturn(false);
            when(shapeRepository.save(any(Shape.class))).thenReturn(savedPolygon);

            ShapeResponse response = shapeService.createShape(validPolygonRequest);

            assertNotNull(response);
            assertEquals(4L, response.getId());
            assertEquals("Test Polygon", response.getName());
            assertEquals(ShapeType.POLYGON, response.getType());
            assertEquals("0.0,0.0;2.0,0.0;3.0,2.0;1.0,3.0;-1.0,2.0", response.getCoordinates());
            assertNull(response.getRadius());
        }

        @Test
        @DisplayName("Should throw exception when shape name already exists")
        void shouldThrowExceptionWhenNameExists() {
            when(shapeRepository.existsByName(anyString())).thenReturn(true);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(validCircleRequest));

            assertEquals("Shape name must be unique", exception.getMessage());
            verify(shapeRepository).existsByName("Test Circle");
            verify(shapeRepository, never()).save(any(Shape.class));
        }

        @Test
        @DisplayName("Should throw exception for circle without radius")
        void shouldThrowExceptionForCircleWithoutRadius() {
            ShapeRequest invalidCircle = new ShapeRequest();
            invalidCircle.setName("Invalid Circle");
            invalidCircle.setType("CIRCLE");
            invalidCircle.setCoordinates("5.0,5.0");
            invalidCircle.setRadius(null);

            when(shapeRepository.existsByName(anyString())).thenReturn(false);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(invalidCircle));

            assertEquals("Circle must have a positive radius", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for circle with negative radius")
        void shouldThrowExceptionForCircleWithNegativeRadius() {
            ShapeRequest invalidCircle = new ShapeRequest();
            invalidCircle.setName("Invalid Circle");
            invalidCircle.setType("CIRCLE");
            invalidCircle.setCoordinates("5.0,5.0");
            invalidCircle.setRadius(-1.0);

            when(shapeRepository.existsByName(anyString())).thenReturn(false);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(invalidCircle));

            assertEquals("Circle must have a positive radius", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for circle with multiple coordinates")
        void shouldThrowExceptionForCircleWithMultipleCoordinates() {
            ShapeRequest invalidCircle = new ShapeRequest();
            invalidCircle.setName("Invalid Circle");
            invalidCircle.setType("CIRCLE");
            invalidCircle.setCoordinates("5.0,5.0;3.0,3.0");
            invalidCircle.setRadius(2.0);

            when(shapeRepository.existsByName(anyString())).thenReturn(false);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(invalidCircle));

            assertEquals("Circle must have exactly 1 center coordinate (x,y)", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for triangle with wrong number of coordinates")
        void shouldThrowExceptionForTriangleWithWrongCoordinates() {
            ShapeRequest invalidTriangle = new ShapeRequest();
            invalidTriangle.setName("Invalid Triangle");
            invalidTriangle.setType("TRIANGLE");
            invalidTriangle.setCoordinates("0.0,0.0;3.0,0.0");

            when(shapeRepository.existsByName(anyString())).thenReturn(false);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(invalidTriangle));

            assertEquals("Triangle must have exactly 3 coordinates", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for rectangle with wrong number of coordinates")
        void shouldThrowExceptionForRectangleWithWrongCoordinates() {
            ShapeRequest invalidRectangle = new ShapeRequest();
            invalidRectangle.setName("Invalid Rectangle");
            invalidRectangle.setType("RECTANGLE");
            invalidRectangle.setCoordinates("0.0,0.0;2.0,0.0;2.0,2.0");

            when(shapeRepository.existsByName(anyString())).thenReturn(false);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(invalidRectangle));

            assertEquals("Rectangle must have exactly 4 coordinates", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for polygon with insufficient coordinates")
        void shouldThrowExceptionForPolygonWithInsufficientCoordinates() {
            ShapeRequest invalidPolygon = new ShapeRequest();
            invalidPolygon.setName("Invalid Polygon");
            invalidPolygon.setType("POLYGON");
            invalidPolygon.setCoordinates("0.0,0.0;2.0,0.0");

            when(shapeRepository.existsByName(anyString())).thenReturn(false);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(invalidPolygon));

            assertEquals("Polygon must have at least 3 coordinates", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for empty coordinates")
        void shouldThrowExceptionForEmptyCoordinates() {
            ShapeRequest invalidShape = new ShapeRequest();
            invalidShape.setName("Invalid Shape");
            invalidShape.setType("TRIANGLE");
            invalidShape.setCoordinates("");

            when(shapeRepository.existsByName(anyString())).thenReturn(false);

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.createShape(invalidShape));

            assertEquals("TRIANGLE must have coordinates", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Shape Tests")
    class GetShapeTests {

        @Test
        @DisplayName("Should get all shapes successfully")
        void shouldGetAllShapes() {
            List<Shape> shapes = Arrays.asList(savedCircle, savedTriangle);
            when(shapeRepository.findAll()).thenReturn(shapes);

            List<ShapeResponse> responses = shapeService.getAllShapes();

            assertNotNull(responses);
            assertEquals(2, responses.size());
            assertEquals("Test Circle", responses.get(0).getName());
            assertEquals("Test Triangle", responses.get(1).getName());

            verify(shapeRepository).findAll();
        }

        @Test
        @DisplayName("Should get shape by ID successfully")
        void shouldGetShapeById() {
            when(shapeRepository.findById(1L)).thenReturn(Optional.of(savedCircle));

            ShapeResponse response = shapeService.getShapeById(1L);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("Test Circle", response.getName());

            verify(shapeRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when shape not found by ID")
        void shouldThrowExceptionWhenShapeNotFound() {
            when(shapeRepository.findById(999L)).thenReturn(Optional.empty());

            ShapeNotFoundException exception = assertThrows(ShapeNotFoundException.class,
                    () -> shapeService.getShapeById(999L));

            assertEquals("Shape not found with ID: 999", exception.getMessage());
            verify(shapeRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("Update Shape Tests")
    class UpdateShapeTests {

        @Test
        @DisplayName("Should update shape successfully")
        void shouldUpdateShapeSuccessfully() {
            ShapeRequest updateRequest = new ShapeRequest();
            updateRequest.setName("Updated Circle");
            updateRequest.setType("CIRCLE");
            updateRequest.setCoordinates("10.0,10.0");
            updateRequest.setRadius(5.0);

            Shape updatedShape = Shape.builder()
                    .id(1L)
                    .name("Updated Circle")
                    .type(ShapeType.CIRCLE)
                    .coordinates("10.0,10.0")
                    .radius(5.0)
                    .build();

            when(shapeRepository.findById(1L)).thenReturn(Optional.of(savedCircle));
            when(shapeRepository.findByName("Updated Circle")).thenReturn(Optional.empty());
            when(shapeRepository.save(any(Shape.class))).thenReturn(updatedShape);

            ShapeResponse response = shapeService.updateShape(1L, updateRequest);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("Updated Circle", response.getName());
            assertEquals("10.0,10.0", response.getCoordinates());
            assertEquals(5.0, response.getRadius());

            verify(shapeRepository).findById(1L);
            verify(shapeRepository).findByName("Updated Circle");
            verify(shapeRepository).save(any(Shape.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent shape")
        void shouldThrowExceptionWhenUpdatingNonExistentShape() {
            when(shapeRepository.findById(999L)).thenReturn(Optional.empty());

            ShapeNotFoundException exception = assertThrows(ShapeNotFoundException.class,
                    () -> shapeService.updateShape(999L, validCircleRequest));

            assertEquals("Shape not found with ID: 999", exception.getMessage());
            verify(shapeRepository).findById(999L);
            verify(shapeRepository, never()).save(any(Shape.class));
        }

        @Test
        @DisplayName("Should throw exception when updating with duplicate name")
        void shouldThrowExceptionWhenUpdatingWithDuplicateName() {
            Shape anotherShape = Shape.builder()
                    .id(2L)
                    .name("Test Circle")
                    .type(ShapeType.CIRCLE)
                    .coordinates("1.0,1.0")
                    .radius(1.0)
                    .build();

            when(shapeRepository.findById(1L)).thenReturn(Optional.of(savedCircle));
            when(shapeRepository.findByName("Test Circle")).thenReturn(Optional.of(anotherShape));

            InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                    () -> shapeService.updateShape(1L, validCircleRequest));

            assertEquals("Another shape with the same name already exists", exception.getMessage());
            verify(shapeRepository).findById(1L);
            verify(shapeRepository).findByName("Test Circle");
            verify(shapeRepository, never()).save(any(Shape.class));
        }

        @Test
        @DisplayName("Should allow updating shape with same name")
        void shouldAllowUpdatingShapeWithSameName() {
            when(shapeRepository.findById(1L)).thenReturn(Optional.of(savedCircle));
            when(shapeRepository.findByName("Test Circle")).thenReturn(Optional.of(savedCircle));
            when(shapeRepository.save(any(Shape.class))).thenReturn(savedCircle);

            ShapeResponse response = shapeService.updateShape(1L, validCircleRequest);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("Test Circle", response.getName());

            verify(shapeRepository).findById(1L);
            verify(shapeRepository).findByName("Test Circle");
            verify(shapeRepository).save(any(Shape.class));
        }
    }

    @Nested
    @DisplayName("Delete Shape Tests")
    class DeleteShapeTests {

        @Test
        @DisplayName("Should delete shape successfully")
        void shouldDeleteShapeSuccessfully() {
            when(shapeRepository.existsById(1L)).thenReturn(true);

            shapeService.deleteShape(1L);

            verify(shapeRepository).existsById(1L);
            verify(shapeRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent shape")
        void shouldThrowExceptionWhenDeletingNonExistentShape() {
            when(shapeRepository.existsById(999L)).thenReturn(false);

            ShapeNotFoundException exception = assertThrows(ShapeNotFoundException.class,
                    () -> shapeService.deleteShape(999L));

            assertEquals("Shape not found with ID: 999", exception.getMessage());
            verify(shapeRepository).existsById(999L);
            verify(shapeRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Find Overlapping Shapes Tests")
    class FindOverlappingShapesTests {

        @Test
        @DisplayName("Should find overlapping circles")
        void shouldFindOverlappingCircles() {
            Shape circle1 = Shape.builder()
                    .id(1L)
                    .name("Circle 1")
                    .type(ShapeType.CIRCLE)
                    .coordinates("0.0,0.0")
                    .radius(3.0)
                    .build();

            Shape circle2 = Shape.builder()
                    .id(2L)
                    .name("Circle 2")
                    .type(ShapeType.CIRCLE)
                    .coordinates("4.0,0.0")
                    .radius(3.0)
                    .build();

            when(shapeRepository.findAll()).thenReturn(Arrays.asList(circle1, circle2));

            OverlapResponse response = shapeService.findOverlappingShapes();

            assertNotNull(response);
            assertEquals(2, response.getOverlappingShapeIds().size());
            assertTrue(response.getOverlappingShapeIds().contains(1L));
            assertTrue(response.getOverlappingShapeIds().contains(2L));
            assertEquals(1, response.getOverlappingGroups().size());
            assertTrue(response.getOverlappingGroups().get(0).contains(1L));
            assertTrue(response.getOverlappingGroups().get(0).contains(2L));

            verify(shapeRepository).findAll();
        }

        @Test
        @DisplayName("Should find no overlapping shapes")
        void shouldFindNoOverlappingShapes() {
            Shape circle1 = Shape.builder()
                    .id(1L)
                    .name("Circle 1")
                    .type(ShapeType.CIRCLE)
                    .coordinates("0.0,0.0")
                    .radius(2.0)
                    .build();

            Shape circle2 = Shape.builder()
                    .id(2L)
                    .name("Circle 2")
                    .type(ShapeType.CIRCLE)
                    .coordinates("10.0,0.0")
                    .radius(2.0)
                    .build();

            when(shapeRepository.findAll()).thenReturn(Arrays.asList(circle1, circle2));

            OverlapResponse response = shapeService.findOverlappingShapes();

            assertNotNull(response);
            assertTrue(response.getOverlappingShapeIds().isEmpty());
            assertTrue(response.getOverlappingGroups().isEmpty());

            verify(shapeRepository).findAll();
        }

        @Test
        @DisplayName("Should find overlapping polygon and circle")
        void shouldFindOverlappingPolygonAndCircle() {
            Shape triangle = Shape.builder()
                    .id(1L)
                    .name("Triangle")
                    .type(ShapeType.TRIANGLE)
                    .coordinates("0.0,0.0;3.0,0.0;1.5,3.0")
                    .build();

            Shape circle = Shape.builder()
                    .id(2L)
                    .name("Circle")
                    .type(ShapeType.CIRCLE)
                    .coordinates("1.0,1.0")
                    .radius(2.0)
                    .build();

            when(shapeRepository.findAll()).thenReturn(Arrays.asList(triangle, circle));

            OverlapResponse response = shapeService.findOverlappingShapes();

            assertNotNull(response);
            assertEquals(2, response.getOverlappingShapeIds().size());
            assertTrue(response.getOverlappingShapeIds().contains(1L));
            assertTrue(response.getOverlappingShapeIds().contains(2L));

            verify(shapeRepository).findAll();
        }

        @Test
        @DisplayName("Should handle empty shape list")
        void shouldHandleEmptyShapeList() {
            when(shapeRepository.findAll()).thenReturn(Arrays.asList());

            OverlapResponse response = shapeService.findOverlappingShapes();

            assertNotNull(response);
            assertTrue(response.getOverlappingShapeIds().isEmpty());
            assertTrue(response.getOverlappingGroups().isEmpty());

            verify(shapeRepository).findAll();
        }

        @Test
        @DisplayName("Should handle single shape")
        void shouldHandleSingleShape() {
            when(shapeRepository.findAll()).thenReturn(Arrays.asList(savedCircle));

            OverlapResponse response = shapeService.findOverlappingShapes();

            assertNotNull(response);
            assertTrue(response.getOverlappingShapeIds().isEmpty());
            assertTrue(response.getOverlappingGroups().isEmpty());

            verify(shapeRepository).findAll();
        }

        @Test
        @DisplayName("Should find multiple overlapping groups")
        void shouldFindMultipleOverlappingGroups() {
            Shape circle1 = Shape.builder()
                    .id(1L)
                    .name("Circle 1")
                    .type(ShapeType.CIRCLE)
                    .coordinates("0.0,0.0")
                    .radius(3.0)
                    .build();

            Shape circle2 = Shape.builder()
                    .id(2L)
                    .name("Circle 2")
                    .type(ShapeType.CIRCLE)
                    .coordinates("4.0,0.0")
                    .radius(3.0)
                    .build();

            Shape rectangle1 = Shape.builder()
                    .id(3L)
                    .name("Rectangle 1")
                    .type(ShapeType.RECTANGLE)
                    .coordinates("10.0,10.0;12.0,10.0;12.0,12.0;10.0,12.0")
                    .build();

            Shape rectangle2 = Shape.builder()
                    .id(4L)
                    .name("Rectangle 2")
                    .type(ShapeType.RECTANGLE)
                    .coordinates("11.0,11.0;13.0,11.0;13.0,13.0;11.0,13.0")
                    .build();

            when(shapeRepository.findAll()).thenReturn(Arrays.asList(circle1, circle2, rectangle1, rectangle2));

            OverlapResponse response = shapeService.findOverlappingShapes();

            assertNotNull(response);
            assertEquals(4, response.getOverlappingShapeIds().size());
            assertEquals(2, response.getOverlappingGroups().size());

            verify(shapeRepository).findAll();
        }
    }
}