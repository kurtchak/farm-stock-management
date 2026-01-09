package com.farmstock.repository;

import com.farmstock.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findAllByOrderByNameAscVarietyAsc();
    Optional<Crop> findByNameAndVariety(String name, String variety);
}