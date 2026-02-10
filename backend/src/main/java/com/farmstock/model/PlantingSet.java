package com.farmstock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planting_sets")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlantingSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(nullable = false, length = 20)
    private String color = "#16a34a";

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "plantingSet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PlantingSetItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

    public void addItem(PlantingSetItem item) {
        items.add(item);
        item.setPlantingSet(this);
    }
}
