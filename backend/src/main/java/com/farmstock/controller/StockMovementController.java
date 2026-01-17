package com.farmstock.controller;

import com.farmstock.model.StockMovementDTO;
import com.farmstock.model.StockStatistics;
import com.farmstock.service.StockMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StockStatistics> getStatistics() {
        return ResponseEntity.ok(stockMovementService.getStatistics());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<StockMovementDTO>> getRecentMovements(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(stockMovementService.getRecentMovements(limit));
    }
}