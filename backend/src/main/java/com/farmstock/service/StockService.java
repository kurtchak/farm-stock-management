package com.farmstock.service;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.CreateStockRequest;
import com.farmstock.model.Crop;
import com.farmstock.model.Stock;
import com.farmstock.model.StockMovement;
import com.farmstock.model.User;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.StockMovementRepository;
import com.farmstock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StockService {
    private final StockRepository stockRepository;
    private final CropRepository cropRepository;
    private final StockMovementRepository movementRepository;
    private final UserService userService;

    @Autowired
    public StockService(StockRepository stockRepository, CropRepository cropRepository,
                        StockMovementRepository movementRepository,
                        UserService userService) {
        this.stockRepository = stockRepository;
        this.cropRepository = cropRepository;
        this.movementRepository = movementRepository;
        this.userService = userService;
    }

    public Stock createStock(Stock stock) {
        stock.setBatchCode(generateBatchCode(stock.getCrop()));
        return stockRepository.save(stock);
    }

    public void adjustStock(Long stockId, Double quantity, String movementType, String reason, Long userId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
        User user = userService.getUserById(userId);

        // Create movement record
        StockMovement movement = new StockMovement();
        movement.setStock(stock);
        movement.setUser(user);
        movement.setQuantity(quantity);
        movement.setMovementType(movementType);
        movement.setReason(reason);

        // Update stock quantity
        if ("IN".equals(movementType)) {
            stock.setQuantity(stock.getQuantity() + quantity);
        } else if ("OUT".equals(movementType)) {
            if (stock.getQuantity() < quantity) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            stock.setQuantity(stock.getQuantity() - quantity);
        }

        stockRepository.save(stock);
        movementRepository.save(movement);
    }

    private String generateBatchCode(Crop crop) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%s-%d-%03d",
                crop.getName().substring(0, Math.min(3, crop.getName().length())).toUpperCase(),
                now.getYear(),
                stockRepository.findByCropId(crop.getId()).size() + 1
        );
    }

    public List<Stock> getLowStockItems(Double threshold) {
        return stockRepository.findByQuantityLessThan(threshold);
    }


    public Stock createStock(CreateStockRequest request) {
        // Verify crop exists
        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found"));

        // Create new stock
        Stock stock = new Stock();
        stock.setCrop(crop);
        stock.setQuantity(request.getQuantity());
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
}