package com.farmstock.repository;

import com.farmstock.model.ForestMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ForestMovementRepository extends JpaRepository<ForestMovement, Long> {

    @Query("SELECT m FROM ForestMovement m LEFT JOIN FETCH m.forestItem LEFT JOIN FETCH m.user LEFT JOIN FETCH m.plantingSet ORDER BY m.createdAt DESC")
    List<ForestMovement> findAllOrderByCreatedAtDesc();

    @Query("SELECT COUNT(m) FROM ForestMovement m WHERE m.createdAt > :since AND m.movementType = :type")
    long countByCreatedAtAfterAndMovementType(@Param("since") LocalDateTime since, @Param("type") String type);
}
