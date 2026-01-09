package com.farmstock.controller;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.CreateStockRequest;
import com.farmstock.model.Stock;
import com.farmstock.model.StockAdjustmentRequest;
import com.farmstock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/db-test")
    public String dbTest() {
        try {
            long count = stockService.getAllStocks().size();
            return "DB connection OK, stocks count: " + count;
        } catch (Exception e) {
            return "DB error: " + e.getMessage();
        }
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody CreateStockRequest request) {
        Stock stock = stockService.createStock(request);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/batch/{batchCode}")
    public ResponseEntity<Stock> getStockByBatchCode(@PathVariable String batchCode) {
        return stockService.findByBatchCode(batchCode)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
    }

    @PostMapping("/{id}/adjust")
    public ResponseEntity<Stock> adjustStock(
            @PathVariable Long id,
            @RequestBody StockAdjustmentRequest request) {
        Stock adjusted = stockService.adjustStock(id, request);
        return ResponseEntity.ok(adjusted);
    }
}