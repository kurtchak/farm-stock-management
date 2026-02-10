package com.farmstock.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "planting_set_items")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlantingSetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planting_set_id", nullable = false)
    @JsonBackReference
    private PlantingSet plantingSet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garden_item_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private GardenItem gardenItem;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
}
