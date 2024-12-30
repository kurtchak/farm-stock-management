package com.farmstock.service;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.Crop;
import com.farmstock.model.Stock;
import com.farmstock.model.StockMovement;
import com.farmstock.model.User;
import com.farmstock.repository.StockMovementRepository;
import com.farmstock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StockService {
    private final StockRepository stockRepository;
    private final StockMovementRepository movementRepository;
    private final UserService userService;

    @Autowired
    public StockService(StockRepository stockRepository,
                        StockMovementRepository movementRepository,
                        UserService userService) {
        this.stockRepository = stockRepository;
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
}