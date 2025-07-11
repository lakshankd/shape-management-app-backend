package com.spillabs.shape_management_appliaction_api.util;

import com.spillabs.shape_management_appliaction_api.model.Point;
import com.spillabs.shape_management_appliaction_api.model.Shape;
import com.spillabs.shape_management_appliaction_api.model.ShapeGeometry;
import com.spillabs.shape_management_appliaction_api.model.ShapeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShapeUtils Tests")
class ShapeUtilsTest {

    @Nested
    @DisplayName("toGeometry Tests")
    class ToGeometryTests {

        @Test
        @DisplayName("Should convert circle shape to geometry")
        void shouldConvertCircleToGeometry() {
            // Given
            Shape circle = Shape.builder()
                    .id(1L)
                    .type(ShapeType.CIRCLE)
                    .coordinates("5.0,10.0")
                    .radius(3.0)
                    .build();

            // When
            ShapeGeometry geometry = ShapeUtils.toGeometry(circle);

            // Then
            assertEquals(1L, geometry.getId());
            assertEquals("CIRCLE", geometry.getType());
            assertTrue(geometry.isCircle());
            assertEquals(3.0, geometry.getRadius());
            assertEquals(5.0, geometry.getCenter().getX());
            assertEquals(10.0, geometry.getCenter().getY());
            assertNull(geometry.getPoints());
        }

        @Test
        @DisplayName("Should convert polygon shape to geometry")
        void shouldConvertPolygonToGeometry() {
            // Given
            Shape triangle = Shape.builder()
                    .id(2L)
                    .type(ShapeType.TRIANGLE)
                    .coordinates("0.0,0.0;3.0,0.0;1.5,3.0")
                    .build();

            // When
            ShapeGeometry geometry = ShapeUtils.toGeometry(triangle);

            // Then
            assertEquals(2L, geometry.getId());
            assertEquals("TRIANGLE", geometry.getType());
            assertFalse(geometry.isCircle());
            assertNull(geometry.getRadius());
            assertNull(geometry.getCenter());
            assertEquals(3, geometry.getPoints().size());
            assertEquals(0.0, geometry.getPoints().get(0).getX());
            assertEquals(0.0, geometry.getPoints().get(0).getY());
            assertEquals(3.0, geometry.getPoints().get(1).getX());
            assertEquals(0.0, geometry.getPoints().get(1).getY());
        }

        @Test
        @DisplayName("Should throw exception for null coordinates")
        void shouldThrowExceptionForNullCoordinates() {
            // Given
            Shape shape = Shape.builder()
                    .id(1L)
                    .type(ShapeType.CIRCLE)
                    .coordinates(null)
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ShapeUtils.toGeometry(shape));
            assertEquals("Coordinates are missing for shape ID: 1", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for blank coordinates")
        void shouldThrowExceptionForBlankCoordinates() {
            // Given
            Shape shape = Shape.builder()
                    .id(1L)
                    .type(ShapeType.CIRCLE)
                    .coordinates("   ")
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ShapeUtils.toGeometry(shape));
            assertEquals("Coordinates are missing for shape ID: 1", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for invalid circle coordinates")
        void shouldThrowExceptionForInvalidCircleCoordinates() {
            // Given
            Shape circle = Shape.builder()
                    .id(1L)
                    .type(ShapeType.CIRCLE)
                    .coordinates("5.0")
                    .radius(3.0)
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ShapeUtils.toGeometry(circle));
            assertEquals("Invalid circle coordinates for shape ID: 1", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for invalid polygon coordinates")
        void shouldThrowExceptionForInvalidPolygonCoordinates() {
            // Given
            Shape triangle = Shape.builder()
                    .id(2L)
                    .type(ShapeType.TRIANGLE)
                    .coordinates("0.0,0.0;3.0")
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ShapeUtils.toGeometry(triangle));
            assertEquals("Invalid coordinate format in shape ID: 2", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Overlap Detection Tests")
    class OverlapDetectionTests {

        @Test
        @DisplayName("Should detect circle-circle overlap")
        void shouldDetectCircleCircleOverlap() {
            // Given
            ShapeGeometry circle1 = new ShapeGeometry(1L, "CIRCLE", null, 3.0, new Point(0.0, 0.0));
            ShapeGeometry circle2 = new ShapeGeometry(2L, "CIRCLE", null, 3.0, new Point(4.0, 0.0));

            // When
            boolean overlaps = ShapeUtils.doOverlap(circle1, circle2);

            // Then
            assertTrue(overlaps);
        }

        @Test
        @DisplayName("Should detect circle-circle no overlap")
        void shouldDetectCircleCircleNoOverlap() {
            // Given
            ShapeGeometry circle1 = new ShapeGeometry(1L, "CIRCLE", null, 2.0, new Point(0.0, 0.0));
            ShapeGeometry circle2 = new ShapeGeometry(2L, "CIRCLE", null, 2.0, new Point(10.0, 0.0));

            // When
            boolean overlaps = ShapeUtils.doOverlap(circle1, circle2);

            // Then
            assertFalse(overlaps);
        }

        @Test
        @DisplayName("Should detect polygon-polygon overlap")
        void shouldDetectPolygonPolygonOverlap() {
            // Given - two squares that overlap
            List<Point> square1 = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(2.0, 2.0),
                    new Point(0.0, 2.0)
            );
            List<Point> square2 = List.of(
                    new Point(1.0, 1.0),
                    new Point(3.0, 1.0),
                    new Point(3.0, 3.0),
                    new Point(1.0, 3.0)
            );

            ShapeGeometry poly1 = new ShapeGeometry(1L, "RECTANGLE", square1, null, null);
            ShapeGeometry poly2 = new ShapeGeometry(2L, "RECTANGLE", square2, null, null);

            // When
            boolean overlaps = ShapeUtils.doOverlap(poly1, poly2);

            // Then
            assertTrue(overlaps);
        }

        @Test
        @DisplayName("Should detect polygon-polygon no overlap")
        void shouldDetectPolygonPolygonNoOverlap() {
            // Given - two squares that don't overlap
            List<Point> square1 = List.of(
                    new Point(0.0, 0.0),
                    new Point(1.0, 0.0),
                    new Point(1.0, 1.0),
                    new Point(0.0, 1.0)
            );
            List<Point> square2 = List.of(
                    new Point(5.0, 5.0),
                    new Point(6.0, 5.0),
                    new Point(6.0, 6.0),
                    new Point(5.0, 6.0)
            );

            ShapeGeometry poly1 = new ShapeGeometry(1L, "RECTANGLE", square1, null, null);
            ShapeGeometry poly2 = new ShapeGeometry(2L, "RECTANGLE", square2, null, null);

            // When
            boolean overlaps = ShapeUtils.doOverlap(poly1, poly2);

            // Then
            assertFalse(overlaps);
        }

        @Test
        @DisplayName("Should detect circle-polygon overlap")
        void shouldDetectCirclePolygonOverlap() {
            // Given
            ShapeGeometry circle = new ShapeGeometry(1L, "CIRCLE", null, 2.0, new Point(1.0, 1.0));
            List<Point> triangle = List.of(
                    new Point(0.0, 0.0),
                    new Point(3.0, 0.0),
                    new Point(1.5, 3.0)
            );
            ShapeGeometry polygon = new ShapeGeometry(2L, "TRIANGLE", triangle, null, null);

            // When
            boolean overlaps = ShapeUtils.doOverlap(circle, polygon);

            // Then
            assertTrue(overlaps);
        }

        @Test
        @DisplayName("Should detect circle-polygon no overlap")
        void shouldDetectCirclePolygonNoOverlap() {
            // Given
            ShapeGeometry circle = new ShapeGeometry(1L, "CIRCLE", null, 1.0, new Point(10.0, 10.0));
            List<Point> triangle = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(1.0, 2.0)
            );
            ShapeGeometry polygon = new ShapeGeometry(2L, "TRIANGLE", triangle, null, null);

            // When
            boolean overlaps = ShapeUtils.doOverlap(circle, polygon);

            // Then
            assertFalse(overlaps);
        }
    }

    @Nested
    @DisplayName("Point in Polygon Tests")
    class PointInPolygonTests {

        @Test
        @DisplayName("Should detect point inside polygon")
        void shouldDetectPointInsidePolygon() {
            // Given
            Point point = new Point(1.0, 1.0);
            List<Point> square = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(2.0, 2.0),
                    new Point(0.0, 2.0)
            );

            // When
            boolean inside = ShapeUtils.pointInPolygon(point, square);

            // Then
            assertTrue(inside);
        }

        @Test
        @DisplayName("Should detect point outside polygon")
        void shouldDetectPointOutsidePolygon() {
            // Given
            Point point = new Point(5.0, 5.0);
            List<Point> square = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(2.0, 2.0),
                    new Point(0.0, 2.0)
            );

            // When
            boolean inside = ShapeUtils.pointInPolygon(point, square);

            // Then
            assertFalse(inside);
        }

        @Test
        @DisplayName("Should detect point on polygon edge")
        void shouldDetectPointOnPolygonEdge() {
            // Given
            Point point = new Point(1.0, 0.0); // On the edge
            List<Point> square = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(2.0, 2.0),
                    new Point(0.0, 2.0)
            );

            // When
            boolean inside = ShapeUtils.pointInPolygon(point, square);

            // Then
            assertFalse(inside); // Point on edge is typically considered outside
        }

        @Test
        @DisplayName("Should work with triangle")
        void shouldWorkWithTriangle() {
            // Given
            Point insidePoint = new Point(1.0, 1.0);
            Point outsidePoint = new Point(5.0, 5.0);
            List<Point> triangle = List.of(
                    new Point(0.0, 0.0),
                    new Point(3.0, 0.0),
                    new Point(1.5, 3.0)
            );

            // When
            boolean insideResult = ShapeUtils.pointInPolygon(insidePoint, triangle);
            boolean outsideResult = ShapeUtils.pointInPolygon(outsidePoint, triangle);

            // Then
            assertTrue(insideResult);
            assertFalse(outsideResult);
        }
    }

    @Nested
    @DisplayName("Polygon Overlap Tests")
    class PolygonOverlapTests {

        @Test
        @DisplayName("Should detect overlapping squares")
        void shouldDetectOverlappingSquares() {
            // Given
            List<Point> square1 = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(2.0, 2.0),
                    new Point(0.0, 2.0)
            );
            List<Point> square2 = List.of(
                    new Point(1.0, 1.0),
                    new Point(3.0, 1.0),
                    new Point(3.0, 3.0),
                    new Point(1.0, 3.0)
            );

            // When
            boolean overlaps = ShapeUtils.polygonOverlap(square1, square2);

            // Then
            assertTrue(overlaps);
        }

        @Test
        @DisplayName("Should detect non-overlapping squares")
        void shouldDetectNonOverlappingSquares() {
            // Given
            List<Point> square1 = List.of(
                    new Point(0.0, 0.0),
                    new Point(1.0, 0.0),
                    new Point(1.0, 1.0),
                    new Point(0.0, 1.0)
            );
            List<Point> square2 = List.of(
                    new Point(5.0, 5.0),
                    new Point(6.0, 5.0),
                    new Point(6.0, 6.0),
                    new Point(5.0, 6.0)
            );

            // When
            boolean overlaps = ShapeUtils.polygonOverlap(square1, square2);

            // Then
            assertFalse(overlaps);
        }
    }

    @Nested
    @DisplayName("Circle Polygon Overlap Tests")
    class CirclePolygonOverlapTests {

        @Test
        @DisplayName("Should detect circle overlapping with polygon vertex")
        void shouldDetectCircleOverlappingWithPolygonVertex() {
            // Given
            ShapeGeometry circle = new ShapeGeometry(1L, "CIRCLE", null, 2.0, new Point(1.0, 1.0));
            List<Point> triangle = List.of(
                    new Point(0.0, 0.0),
                    new Point(3.0, 0.0),
                    new Point(1.5, 3.0)
            );
            ShapeGeometry polygon = new ShapeGeometry(2L, "TRIANGLE", triangle, null, null);

            // When
            boolean overlaps = ShapeUtils.circlePolygonOverlap(circle, polygon);

            // Then
            assertTrue(overlaps);
        }

        @Test
        @DisplayName("Should detect circle center inside polygon")
        void shouldDetectCircleCenterInsidePolygon() {
            // Given
            ShapeGeometry circle = new ShapeGeometry(1L, "CIRCLE", null, 0.5, new Point(1.0, 1.0));
            List<Point> square = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(2.0, 2.0),
                    new Point(0.0, 2.0)
            );
            ShapeGeometry polygon = new ShapeGeometry(2L, "RECTANGLE", square, null, null);

            // When
            boolean overlaps = ShapeUtils.circlePolygonOverlap(circle, polygon);

            // Then
            assertTrue(overlaps);
        }

        @Test
        @DisplayName("Should detect no overlap between circle and polygon")
        void shouldDetectNoOverlapBetweenCircleAndPolygon() {
            // Given
            ShapeGeometry circle = new ShapeGeometry(1L, "CIRCLE", null, 1.0, new Point(10.0, 10.0));
            List<Point> triangle = List.of(
                    new Point(0.0, 0.0),
                    new Point(2.0, 0.0),
                    new Point(1.0, 2.0)
            );
            ShapeGeometry polygon = new ShapeGeometry(2L, "TRIANGLE", triangle, null, null);

            // When
            boolean overlaps = ShapeUtils.circlePolygonOverlap(circle, polygon);

            // Then
            assertFalse(overlaps);
        }
    }
}