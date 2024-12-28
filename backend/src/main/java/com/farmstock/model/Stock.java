package com.farmstock.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(name = "batch_code", nullable = false, unique = true)
    private String batchCode;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;  // KG, TON, PIECE

    @Column(name = "storage_location")
    private String storageLocation;

    @Column(name = "harvest_date")
    private LocalDateTime harvestDate;

    @Column(name = "quality_grade")
    private String qualityGrade;  // A, B, C

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
