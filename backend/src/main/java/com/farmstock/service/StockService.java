package com.farmstock.service;

import com.farmstock.config.StockProperties;
import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.CreateStockRequest;
import com.farmstock.model.Crop;
import com.farmstock.model.Stock;
import com.farmstock.model.StockAdjustmentRequest;
import com.farmstock.model.StockMovement;
import com.farmstock.model.User;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.StockRepository;
import com.farmstock.repository.StockMovementRepository;
import com.farmstock.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final StockMovementRepository stockMovementRepository;
    private final StockProperties stockProperties;
    private final UserService userService;

    @Autowired
    public StockService(StockRepository stockRepository, CropRepository cropRepository,
                        StockMovementRepository stockMovementRepository, StockProperties stockProperties,
                        UserService userService) {
        this.stockRepository = stockRepository;
        this.cropRepository = cropRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.stockProperties = stockProperties;
        this.userService = userService;
    }

    @Transactional
    public Stock adjustStock(Long id, StockAdjustmentRequest request) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        // Add validation for sufficient quantity
        if (stock.getQuantity().add(request.getQuantity()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient stock quantity");
        }

        // Update quantity
        stock.setQuantity(stock.getQuantity().add(request.getQuantity()));

        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated to adjust stock");
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipal.getId());

        // Create and save stock movement
        StockMovement movement = new StockMovement();
        movement.setStock(stock);
        movement.setUser(user);
        movement.setQuantity(request.getQuantity());
        movement.setMovementType(request.getMovementType());
        movement.setReason(request.getReason());
        stockMovementRepository.save(movement);

        return stockRepository.save(stock);
    }

    @Transactional
    public Stock createStock(CreateStockRequest request) {
        Crop crop = new Crop();
        crop.setName(request.getCropName());
        crop.setVariety(request.getVariety());
        crop.setHarvestSeason(request.getHarvestSeason());
        crop.setDescription(request.getDescription());
        crop.setStorageConditions(request.getStorageConditions());
        cropRepository.save(crop);

        Stock stock = new Stock();
        stock.setCrop(crop);
        stock.setBatchCode(request.getBatchCode());
        stock.setQuantity(request.getQuantity());
        stock.setUnitOfMeasure(request.getUnitOfMeasure());
        stock.setStorageLocation(request.getStorageLocation());
        stock.setHarvestDate(request.getHarvestDate());
        stock.setQualityGrade(request.getQualityGrade());
        stock.setNotes(request.getNotes());

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
        return stockRepository.findAllActive();
    }

    @Transactional
    public void softDeleteStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));

        stock.setDeleted(true);
        stockRepository.save(stock);
    }

    public BigDecimal getDeleteThreshold() {
        return stockProperties.getDeleteThreshold();
    }

    public List<Stock> getAllDeletedStocks() {
        return stockRepository.findAllDeleted();
    }

    @Transactional
    public Stock restoreStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));

        stock.setDeleted(false);
        return stockRepository.save(stock);
    }
}