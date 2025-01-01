package com.farmstock.service;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.CreateStockRequest;
import com.farmstock.model.Crop;
import com.farmstock.model.Stock;
import com.farmstock.model.StockAdjustmentRequest;
import com.farmstock.model.StockMovement;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StockService {
    private final StockRepository stockRepository;
    private final CropRepository cropRepository;

    @Autowired
    public StockService(StockRepository stockRepository, CropRepository cropRepository) {
        this.stockRepository = stockRepository;
        this.cropRepository = cropRepository;
    }

    public Stock adjustStock(Long id, StockAdjustmentRequest request) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        // Add validation for sufficient quantity
        if (stock.getQuantity().add(request.getQuantity()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient stock quantity");
        }

        // Update quantity
        stock.setQuantity(stock.getQuantity().add(request.getQuantity()));

        StockMovement movement = new StockMovement();
        movement.setStock(stock);
        movement.setQuantity(request.getQuantity());
        movement.setMovementType(request.getMovementType());
        movement.setReason(request.getReason());

        return stockRepository.save(stock);
    }

    public Stock createStock(CreateStockRequest request) {
        // Verify crop exists
        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found"));

        // Create new stock
        Stock stock = new Stock();
        stock.setCrop(crop);
        stock.setQuantity(BigDecimal.valueOf(request.getQuantity()));
        stock.setUnitOfMeasure(request.getUnitOfMeasure());
        stock.setHarvestDate(request.getHarvestDate());
        stock.setQualityGrade("A"); // Default grade

        // Generate batch code: [CROP_NAME_3_CHARS]-YYMM-[SEQUENTIAL]
        String prefix = crop.getName().substring(0, Math.min(3, crop.getName().length())).toUpperCase();
        String datePart = new SimpleDateFormat("yyMM").format(Date.from(request.getHarvestDate().atZone(ZoneId.systemDefault()).toInstant()));
        String sequence = String.format("%03d", getNextSequenceNumber(prefix, datePart));

        stock.setBatchCode(String.format("%s-%s-%s", prefix, datePart, sequence));

        // Save and return
        return stockRepository.save(stock);
    }

    private int getNextSequenceNumber(String prefix, String datePart) {
        String pattern = prefix + "-" + datePart + "-%";
        return stockRepository.findMaxSequenceForPattern(pattern)
                .map(seq -> seq + 1)
                .orElse(1);
    }

    public Optional<Stock> findByBatchCode(String batchCode) {
        return stockRepository.findByBatchCode(batchCode);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAllWithCrop();
    }
}