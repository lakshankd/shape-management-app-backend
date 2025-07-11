package com.spillabs.shape_management_appliaction_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spillabs.shape_management_appliaction_api.dto.ShapeRequest;
import com.spillabs.shape_management_appliaction_api.model.Shape;
import com.spillabs.shape_management_appliaction_api.model.ShapeType;
import com.spillabs.shape_management_appliaction_api.repository.ShapeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Shape Controller Integration Tests")
class ShapeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ShapeRepository shapeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        shapeRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/shapes - Should create a new circle successfully")
    void shouldCreateCircleSuccessfully() throws Exception {
        ShapeRequest circleRequest = new ShapeRequest(
                "Test Circle",
                "CIRCLE",
                "0,0",
                5.0
        );

        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(circleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Circle"))
                .andExpect(jsonPath("$.type").value("CIRCLE"))
                .andExpect(jsonPath("$.coordinates").value("0,0"))
                .andExpect(jsonPath("$.radius").value(5.0));
    }

    @Test
    @DisplayName("POST /api/v1/shapes - Should create a new triangle successfully")
    void shouldCreateTriangleSuccessfully() throws Exception {
        ShapeRequest triangleRequest = new ShapeRequest(
                "Test Triangle",
                "TRIANGLE",
                "0,0;5,0;2.5,5",
                null
        );

        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(triangleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Triangle"))
                .andExpect(jsonPath("$.type").value("TRIANGLE"))
                .andExpect(jsonPath("$.coordinates").value("0,0;5,0;2.5,5"))
                .andExpect(jsonPath("$.radius").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/v1/shapes - Should create a new rectangle successfully")
    void shouldCreateRectangleSuccessfully() throws Exception {
        ShapeRequest rectangleRequest = new ShapeRequest(
                "Test Rectangle",
                "RECTANGLE",
                "0,0;4,0;4,3;0,3",
                null
        );

        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rectangleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Rectangle"))
                .andExpect(jsonPath("$.type").value("RECTANGLE"))
                .andExpect(jsonPath("$.coordinates").value("0,0;4,0;4,3;0,3"));
    }

    @Test
    @DisplayName("POST /api/v1/shapes - Should return 400 when creating circle without radius")
    void shouldReturnBadRequestWhenCircleWithoutRadius() throws Exception {
        ShapeRequest invalidCircleRequest = new ShapeRequest(
                "Invalid Circle",
                "CIRCLE",
                "0,0",
                null
        );

        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCircleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/shapes - Should return 400 when creating triangle with wrong number of coordinates")
    void shouldReturnBadRequestWhenTriangleWithWrongCoordinates() throws Exception {
        ShapeRequest invalidTriangleRequest = new ShapeRequest(
                "Invalid Triangle",
                "TRIANGLE",
                "0,0;5,0",
                null
        );

        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTriangleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/shapes - Should return 400 when creating shape with duplicate name")
    void shouldReturnBadRequestWhenDuplicateName() throws Exception {
        // Create first shape
        ShapeRequest firstRequest = new ShapeRequest(
                "Duplicate Name",
                "CIRCLE",
                "0,0",
                5.0
        );

        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        ShapeRequest duplicateRequest = new ShapeRequest(
                "Duplicate Name",
                "TRIANGLE",
                "0,0;5,0;2.5,5",
                null
        );

        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/shapes - Should return all shapes")
    void shouldGetAllShapes() throws Exception {
        createTestShapes();

        mockMvc.perform(get("/api/v1/shapes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Circle 1", "Triangle 1", "Rectangle 1")));
    }

    @Test
    @DisplayName("GET /api/v1/shapes - Should return empty list when no shapes exist")
    void shouldReturnEmptyListWhenNoShapes() throws Exception {
        mockMvc.perform(get("/api/v1/shapes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/shapes/{id} - Should return specific shape by ID")
    void shouldGetShapeById() throws Exception {
        Shape savedShape = createTestCircle();

        mockMvc.perform(get("/api/v1/shapes/{id}", savedShape.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedShape.getId()))
                .andExpect(jsonPath("$.name").value("Circle 1"))
                .andExpect(jsonPath("$.type").value("CIRCLE"))
                .andExpect(jsonPath("$.coordinates").value("0,0"))
                .andExpect(jsonPath("$.radius").value(5.0));
    }

    @Test
    @DisplayName("GET /api/v1/shapes/{id} - Should return 404 when shape not found")
    void shouldReturnNotFoundWhenShapeDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/shapes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/shapes/{id} - Should update shape successfully")
    void shouldUpdateShapeSuccessfully() throws Exception {
        Shape savedShape = createTestCircle();

        ShapeRequest updateRequest = new ShapeRequest(
                "Updated Circle",
                "CIRCLE",
                "10,10",
                7.5
        );

        mockMvc.perform(put("/api/v1/shapes/{id}", savedShape.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedShape.getId()))
                .andExpect(jsonPath("$.name").value("Updated Circle"))
                .andExpect(jsonPath("$.coordinates").value("10,10"))
                .andExpect(jsonPath("$.radius").value(7.5));
    }

    @Test
    @DisplayName("PUT /api/v1/shapes/{id} - Should return 404 when updating non-existent shape")
    void shouldReturnNotFoundWhenUpdatingNonExistentShape() throws Exception {
        ShapeRequest updateRequest = new ShapeRequest(
                "Non-existent Shape",
                "CIRCLE",
                "0,0",
                5.0
        );

        mockMvc.perform(put("/api/v1/shapes/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/shapes/{id} - Should return 400 when updating with duplicate name")
    void shouldReturnBadRequestWhenUpdatingWithDuplicateName() throws Exception {
        // Create two shapes
        Shape shape1 = createTestCircle();
        Shape shape2 = createTestTriangle();

        // Try to update shape2 with shape1's name
        ShapeRequest updateRequest = new ShapeRequest(
                "Circle 1", // Same name as shape1
                "TRIANGLE",
                "0,0;5,0;2.5,5",
                null
        );

        mockMvc.perform(put("/api/v1/shapes/{id}", shape2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/shapes/{id} - Should delete shape successfully")
    void shouldDeleteShapeSuccessfully() throws Exception {
        Shape savedShape = createTestCircle();

        mockMvc.perform(delete("/api/v1/shapes/{id}", savedShape.getId()))
                .andExpect(status().isNoContent());

        // Verify shape is deleted
        mockMvc.perform(get("/api/v1/shapes/{id}", savedShape.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/shapes/{id} - Should return 404 when deleting non-existent shape")
    void shouldReturnNotFoundWhenDeletingNonExistentShape() throws Exception {
        mockMvc.perform(delete("/api/v1/shapes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/shapes/overlaps - Should return overlapping shapes")
    void shouldReturnOverlappingShapes() throws Exception {
        // Create overlapping circles
        Shape circle1 = Shape.builder()
                .name("Circle 1")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();

        Shape circle2 = Shape.builder()
                .name("Circle 2")
                .type(ShapeType.CIRCLE)
                .coordinates("3,0") // Distance 3, both have radius 5, so they overlap
                .radius(5.0)
                .build();

        Shape circle3 = Shape.builder()
                .name("Circle 3")
                .type(ShapeType.CIRCLE)
                .coordinates("20,20") // Far away, no overlap
                .radius(2.0)
                .build();

        shapeRepository.save(circle1);
        shapeRepository.save(circle2);
        shapeRepository.save(circle3);

        mockMvc.perform(get("/api/v1/shapes/overlaps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overlappingShapeIds", hasSize(2)))
                .andExpect(jsonPath("$.overlappingGroups", hasSize(1)))
                .andExpect(jsonPath("$.overlappingGroups[0]", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/v1/shapes/overlaps - Should return empty when no overlaps")
    void shouldReturnEmptyWhenNoOverlaps() throws Exception {
        // Create non-overlapping circles
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

        mockMvc.perform(get("/api/v1/shapes/overlaps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overlappingShapeIds", hasSize(0)))
                .andExpect(jsonPath("$.overlappingGroups", hasSize(0)));
    }

    @Test
    @DisplayName("Full CRUD workflow - Should work end-to-end")
    void shouldWorkEndToEnd() throws Exception {
        // 1. Create a shape
        ShapeRequest createRequest = new ShapeRequest(
                "E2E Test Shape",
                "CIRCLE",
                "0,0",
                5.0
        );

        String createResponse = mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long shapeId = objectMapper.readTree(createResponse).get("id").asLong();

        // 2. Get the shape
        mockMvc.perform(get("/api/v1/shapes/{id}", shapeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("E2E Test Shape"));

        // 3. Update the shape
        ShapeRequest updateRequest = new ShapeRequest(
                "Updated E2E Test Shape",
                "CIRCLE",
                "5,5",
                7.0
        );

        mockMvc.perform(put("/api/v1/shapes/{id}", shapeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated E2E Test Shape"))
                .andExpect(jsonPath("$.coordinates").value("5,5"))
                .andExpect(jsonPath("$.radius").value(7.0));

        // 4. List all shapes (should include our updated shape)
        mockMvc.perform(get("/api/v1/shapes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Updated E2E Test Shape"));

        // 5. Delete the shape
        mockMvc.perform(delete("/api/v1/shapes/{id}", shapeId))
                .andExpect(status().isNoContent());

        // 6. Verify it's deleted
        mockMvc.perform(get("/api/v1/shapes/{id}", shapeId))
                .andExpect(status().isNotFound());

        // 7. List all shapes (should be empty)
        mockMvc.perform(get("/api/v1/shapes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Helper methods
    private void createTestShapes() {
        Shape circle = Shape.builder()
                .name("Circle 1")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();

        Shape triangle = Shape.builder()
                .name("Triangle 1")
                .type(ShapeType.TRIANGLE)
                .coordinates("0,0;5,0;2.5,5")
                .build();

        Shape rectangle = Shape.builder()
                .name("Rectangle 1")
                .type(ShapeType.RECTANGLE)
                .coordinates("0,0;4,0;4,3;0,3")
                .build();

        shapeRepository.save(circle);
        shapeRepository.save(triangle);
        shapeRepository.save(rectangle);
    }

    private Shape createTestCircle() {
        Shape circle = Shape.builder()
                .name("Circle 1")
                .type(ShapeType.CIRCLE)
                .coordinates("0,0")
                .radius(5.0)
                .build();
        return shapeRepository.save(circle);
    }

    private Shape createTestTriangle() {
        Shape triangle = Shape.builder()
                .name("Triangle 1")
                .type(ShapeType.TRIANGLE)
                .coordinates("0,0;5,0;2.5,5")
                .build();
        return shapeRepository.save(triangle);
    }
}