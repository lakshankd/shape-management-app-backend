package com.spillabs.shape_management_appliaction_api.repository;

import com.spillabs.shape_management_appliaction_api.model.Shape;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShapeRepository extends JpaRepository<Shape, Long> {

    boolean existsByName(String name);
    Optional<Shape> findByName(String name);
}
