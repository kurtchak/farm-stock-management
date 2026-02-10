package com.farmstock.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "garden_movements")
public class GardenMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "garden_item_id", nullable = false)
    private GardenItem gardenItem;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "movement_type", nullable = false)
    private String movementType;  // IN, OUT

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "planting_set_id")
    private PlantingSet plantingSet;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
