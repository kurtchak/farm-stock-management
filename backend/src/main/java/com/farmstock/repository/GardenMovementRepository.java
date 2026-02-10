package com.farmstock.repository;

import com.farmstock.model.GardenMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GardenMovementRepository extends JpaRepository<GardenMovement, Long> {

    @Query("SELECT m FROM GardenMovement m LEFT JOIN FETCH m.gardenItem LEFT JOIN FETCH m.user LEFT JOIN FETCH m.plantingSet ORDER BY m.createdAt DESC")
    List<GardenMovement> findAllOrderByCreatedAtDesc();

    @Query("SELECT COUNT(m) FROM GardenMovement m WHERE m.createdAt > :since AND m.movementType = :type")
    long countByCreatedAtAfterAndMovementType(@Param("since") LocalDateTime since, @Param("type") String type);
}
