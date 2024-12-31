package com.farmstock.controller;

import com.farmstock.model.CreateStockRequest;
import com.farmstock.model.Stock;
import com.farmstock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody CreateStockRequest request) {
        Stock stock = stockService.createStock(request);
        return ResponseEntity.ok(stock);
    }
}