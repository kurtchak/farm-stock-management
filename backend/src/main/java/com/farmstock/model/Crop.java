package com.farmstock.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "crops")
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String variety;

    @Column(name = "harvest_season")
    private String harvestSeason;  // SPRING, SUMMER, FALL, WINTER

    private String description;

    @Column(name = "storage_conditions")
    private String storageConditions;

    @Column(name = "minimum_storage_temp")
    private Float minimumStorageTemp;

    @Column(name = "maximum_storage_temp")
    private Float maximumStorageTemp;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}