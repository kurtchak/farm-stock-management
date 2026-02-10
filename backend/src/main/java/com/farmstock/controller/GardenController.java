package com.farmstock.controller;

import com.farmstock.model.GardenItem;
import com.farmstock.model.PlantingSet;
import com.farmstock.model.garden.*;
import com.farmstock.service.GardenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garden")
public class GardenController {

    private final GardenService gardenService;

    public GardenController(GardenService gardenService) {
        this.gardenService = gardenService;
    }

    // ---- Items ----

    @GetMapping("/items")
    public ResponseEntity<List<GardenItem>> getAllItems(@RequestParam(required = false) String category) {
        if (category != null) {
            return ResponseEntity.ok(gardenService.getItemsByCategory(category));
        }
        return ResponseEntity.ok(gardenService.getAllItems());
    }

    @PostMapping("/items")
    public ResponseEntity<GardenItem> createItem(@RequestBody CreateGardenItemRequest request) {
        return ResponseEntity.ok(gardenService.createItem(request));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<GardenItem> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(gardenService.getItem(id));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<GardenItem> updateItem(@PathVariable Long id, @RequestBody UpdateGardenItemRequest request) {
        return ResponseEntity.ok(gardenService.updateItem(id, request));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        gardenService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items/{id}/adjust")
    public ResponseEntity<GardenItem> adjustItem(@PathVariable Long id, @RequestBody GardenAdjustmentRequest request) {
        return ResponseEntity.ok(gardenService.adjustItem(id, request));
    }

    // ---- Planting Sets ----

    @GetMapping("/sets")
    public ResponseEntity<List<PlantingSet>> getAllSets() {
        return ResponseEntity.ok(gardenService.getAllSets());
    }

    @GetMapping("/sets/active")
    public ResponseEntity<List<PlantingSet>> getActiveSets() {
        return ResponseEntity.ok(gardenService.getActiveSets());
    }

    @PostMapping("/sets")
    public ResponseEntity<PlantingSet> createSet(@RequestBody CreatePlantingSetRequest request) {
        return ResponseEntity.ok(gardenService.createSet(request));
    }

    @GetMapping("/sets/{id}")
    public ResponseEntity<PlantingSet> getSet(@PathVariable Long id) {
        return ResponseEntity.ok(gardenService.getSet(id));
    }

    @PutMapping("/sets/{id}")
    public ResponseEntity<PlantingSet> updateSet(@PathVariable Long id, @RequestBody CreatePlantingSetRequest request) {
        return ResponseEntity.ok(gardenService.updateSet(id, request));
    }

    @DeleteMapping("/sets/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable Long id) {
        gardenService.deleteSet(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sets/{id}/execute")
    public ResponseEntity<Void> executeSet(@PathVariable Long id, @RequestBody ExecuteSetRequest request) {
        gardenService.executeSet(id, request.getCount());
        return ResponseEntity.ok().build();
    }

    // ---- Movements & Statistics ----

    @GetMapping("/movements")
    public ResponseEntity<List<GardenMovementDTO>> getMovements() {
        return ResponseEntity.ok(gardenService.getMovements());
    }

    @GetMapping("/statistics")
    public ResponseEntity<GardenStatistics> getStatistics() {
        return ResponseEntity.ok(gardenService.getStatistics());
    }
}
