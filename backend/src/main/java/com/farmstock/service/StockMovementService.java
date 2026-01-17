package com.farmstock.service;

import com.farmstock.model.StockMovement;
import com.farmstock.model.StockMovementDTO;
import com.farmstock.model.StockStatistics;
import com.farmstock.repository.StockMovementRepository;
import com.farmstock.repository.StockRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final StockRepository stockRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository,
                                StockRepository stockRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockRepository = stockRepository;
    }

    public StockStatistics getStatistics() {
        // Calculate total items (count of all stock entries)
        long totalItems = stockRepository.count();

        // Calculate weekly statistics
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<StockMovement> weeklyMovements = stockMovementRepository.findAll().stream()
                .filter(m -> m.getCreatedAt().isAfter(weekAgo))
                .collect(Collectors.toList());

        long weeklyIn = weeklyMovements.stream()
                .filter(m -> "IN".equalsIgnoreCase(m.getMovementType()))
                .count();

        long weeklyOut = weeklyMovements.stream()
                .filter(m -> "OUT".equalsIgnoreCase(m.getMovementType()))
                .count();

        return new StockStatistics(totalItems, weeklyIn, weeklyOut);
    }

    public List<StockMovementDTO> getRecentMovements(int limit) {
        List<StockMovement> movements = stockMovementRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        return movements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private StockMovementDTO convertToDTO(StockMovement movement) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setCreatedAt(movement.getCreatedAt());
        dto.setReason(movement.getReason());

        if (movement.getStock() != null) {
            dto.setUnitOfMeasure(movement.getStock().getUnitOfMeasure());
            if (movement.getStock().getCrop() != null) {
                dto.setCropName(movement.getStock().getCrop().getName());
                dto.setCropVariety(movement.getStock().getCrop().getVariety());
            }
        }

        return dto;
    }
}