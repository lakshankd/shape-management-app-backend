package com.spillabs.shape_management_appliaction_api.util;

import com.spillabs.shape_management_appliaction_api.exception.InvalidShapeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CoordinateUtils Tests")
class CoordinateUtilsTest {

    @Test
    @DisplayName("Should parse valid coordinates successfully")
    void shouldParseValidCoordinates() {
        // Given
        String coordString = "1.0,2.0;3.0,4.0;5.0,6.0";

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertEquals(3, result.size());
        assertArrayEquals(new double[]{1.0, 2.0}, result.get(0));
        assertArrayEquals(new double[]{3.0, 4.0}, result.get(1));
        assertArrayEquals(new double[]{5.0, 6.0}, result.get(2));
    }

    @Test
    @DisplayName("Should parse single coordinate successfully")
    void shouldParseSingleCoordinate() {
        // Given
        String coordString = "10.5,20.5";

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertEquals(1, result.size());
        assertArrayEquals(new double[]{10.5, 20.5}, result.get(0));
    }

    @Test
    @DisplayName("Should handle coordinates with extra whitespace")
    void shouldHandleWhitespace() {
        // Given
        String coordString = " 1.0 , 2.0 ; 3.0 , 4.0 ";

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertEquals(2, result.size());
        assertArrayEquals(new double[]{1.0, 2.0}, result.get(0));
        assertArrayEquals(new double[]{3.0, 4.0}, result.get(1));
    }

    @Test
    @DisplayName("Should return empty list for null input")
    void shouldReturnEmptyListForNull() {
        // Given
        String coordString = null;

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for empty string")
    void shouldReturnEmptyListForEmptyString() {
        // Given
        String coordString = "";

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for blank string")
    void shouldReturnEmptyListForBlankString() {
        // Given
        String coordString = "   ";

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception for invalid coordinate format - single value")
    void shouldThrowExceptionForSingleValue() {
        // Given
        String coordString = "1.0";

        // When & Then
        InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                () -> CoordinateUtils.parseCoordinates(coordString));

        assertEquals("Invalid coordinate format. Use 'x1,y1;x2,y2;...'", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid coordinate format - three values")
    void shouldThrowExceptionForThreeValues() {
        // Given
        String coordString = "1.0,2.0,3.0";

        // When & Then
        InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                () -> CoordinateUtils.parseCoordinates(coordString));

        assertEquals("Invalid coordinate format. Use 'x1,y1;x2,y2;...'", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for non-numeric coordinates")
    void shouldThrowExceptionForNonNumericCoordinates() {
        // Given
        String coordString = "abc,def";

        // When & Then
        InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                () -> CoordinateUtils.parseCoordinates(coordString));

        assertEquals("Coordinates must be valid numbers", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for mixed valid and invalid coordinates")
    void shouldThrowExceptionForMixedCoordinates() {
        // Given
        String coordString = "1.0,2.0;abc,def";

        // When & Then
        InvalidShapeException exception = assertThrows(InvalidShapeException.class,
                () -> CoordinateUtils.parseCoordinates(coordString));

        assertEquals("Coordinates must be valid numbers", exception.getMessage());
    }

    @Test
    @DisplayName("Should parse negative coordinates")
    void shouldParseNegativeCoordinates() {
        // Given
        String coordString = "-1.0,-2.0;3.0,-4.0";

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertEquals(2, result.size());
        assertArrayEquals(new double[]{-1.0, -2.0}, result.get(0));
        assertArrayEquals(new double[]{3.0, -4.0}, result.get(1));
    }

    @Test
    @DisplayName("Should parse integer coordinates")
    void shouldParseIntegerCoordinates() {
        // Given
        String coordString = "1,2;3,4";

        // When
        List<double[]> result = CoordinateUtils.parseCoordinates(coordString);

        // Then
        assertEquals(2, result.size());
        assertArrayEquals(new double[]{1.0, 2.0}, result.get(0));
        assertArrayEquals(new double[]{3.0, 4.0}, result.get(1));
    }
}