package com.farmstock.controller;

import com.farmstock.model.ForestItem;
import com.farmstock.model.PlantingSet;
import com.farmstock.model.forest.*;
import com.farmstock.service.ForestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forest")
public class ForestController {

    private final ForestService forestService;

    public ForestController(ForestService forestService) {
        this.forestService = forestService;
    }

    // ---- Items ----

    @GetMapping("/items")
    public ResponseEntity<List<ForestItem>> getAllItems(@RequestParam(required = false) String category) {
        if (category != null) {
            return ResponseEntity.ok(forestService.getItemsByCategory(category));
        }
        return ResponseEntity.ok(forestService.getAllItems());
    }

    @PostMapping("/items")
    public ResponseEntity<ForestItem> createItem(@RequestBody CreateForestItemRequest request) {
        return ResponseEntity.ok(forestService.createItem(request));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ForestItem> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(forestService.getItem(id));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ForestItem> updateItem(@PathVariable Long id, @RequestBody UpdateForestItemRequest request) {
        return ResponseEntity.ok(forestService.updateItem(id, request));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        forestService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items/{id}/adjust")
    public ResponseEntity<ForestItem> adjustItem(@PathVariable Long id, @RequestBody ForestAdjustmentRequest request) {
        return ResponseEntity.ok(forestService.adjustItem(id, request));
    }

    // ---- Planting Sets ----

    @GetMapping("/sets")
    public ResponseEntity<List<PlantingSet>> getAllSets() {
        return ResponseEntity.ok(forestService.getAllSets());
    }

    @GetMapping("/sets/active")
    public ResponseEntity<List<PlantingSet>> getActiveSets() {
        return ResponseEntity.ok(forestService.getActiveSets());
    }

    @PostMapping("/sets")
    public ResponseEntity<PlantingSet> createSet(@RequestBody CreatePlantingSetRequest request) {
        return ResponseEntity.ok(forestService.createSet(request));
    }

    @GetMapping("/sets/{id}")
    public ResponseEntity<PlantingSet> getSet(@PathVariable Long id) {
        return ResponseEntity.ok(forestService.getSet(id));
    }

    @PutMapping("/sets/{id}")
    public ResponseEntity<PlantingSet> updateSet(@PathVariable Long id, @RequestBody CreatePlantingSetRequest request) {
        return ResponseEntity.ok(forestService.updateSet(id, request));
    }

    @DeleteMapping("/sets/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable Long id) {
        forestService.deleteSet(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sets/{id}/execute")
    public ResponseEntity<Void> executeSet(@PathVariable Long id, @RequestBody ExecuteSetRequest request) {
        forestService.executeSet(id, request.getCount());
        return ResponseEntity.ok().build();
    }

    // ---- Movements & Statistics ----

    @GetMapping("/movements")
    public ResponseEntity<List<ForestMovementDTO>> getMovements() {
        return ResponseEntity.ok(forestService.getMovements());
    }

    @GetMapping("/statistics")
    public ResponseEntity<ForestStatistics> getStatistics() {
        return ResponseEntity.ok(forestService.getStatistics());
    }
}
