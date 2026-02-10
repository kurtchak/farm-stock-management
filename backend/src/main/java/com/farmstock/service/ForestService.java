package com.farmstock.service;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.*;
import com.farmstock.model.forest.*;
import com.farmstock.repository.ForestItemRepository;
import com.farmstock.repository.ForestMovementRepository;
import com.farmstock.repository.PlantingSetRepository;
import com.farmstock.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ForestService {

    private final ForestItemRepository forestItemRepository;
    private final PlantingSetRepository plantingSetRepository;
    private final ForestMovementRepository forestMovementRepository;
    private final UserService userService;

    @Autowired
    public ForestService(ForestItemRepository forestItemRepository,
                         PlantingSetRepository plantingSetRepository,
                         ForestMovementRepository forestMovementRepository,
                         UserService userService) {
        this.forestItemRepository = forestItemRepository;
        this.plantingSetRepository = plantingSetRepository;
        this.forestMovementRepository = forestMovementRepository;
        this.userService = userService;
    }

    // ---- Forest Items ----

    public List<ForestItem> getAllItems() {
        return forestItemRepository.findAllActive();
    }

    public List<ForestItem> getItemsByCategory(String category) {
        return forestItemRepository.findActiveByCategory(category);
    }

    public ForestItem getItem(Long id) {
        return forestItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forest item not found"));
    }

    @Transactional
    public ForestItem createItem(CreateForestItemRequest request) {
        ForestItem item = new ForestItem();
        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setQuantity(request.getQuantity() != null ? request.getQuantity() : BigDecimal.ZERO);
        item.setUnit(request.getUnit() != null ? request.getUnit() : "ks");
        item.setMinStock(request.getMinStock() != null ? request.getMinStock() : BigDecimal.ZERO);
        item.setNotes(request.getNotes());
        return forestItemRepository.save(item);
    }

    @Transactional
    public ForestItem updateItem(Long id, UpdateForestItemRequest request) {
        ForestItem item = forestItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forest item not found"));

        if (request.getName() != null) item.setName(request.getName());
        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getUnit() != null) item.setUnit(request.getUnit());
        if (request.getMinStock() != null) item.setMinStock(request.getMinStock());
        if (request.getNotes() != null) item.setNotes(request.getNotes());

        return forestItemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        ForestItem item = forestItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forest item not found"));
        item.setDeleted(true);
        forestItemRepository.save(item);
    }

    @Transactional
    public ForestItem adjustItem(Long id, ForestAdjustmentRequest request) {
        ForestItem item = forestItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forest item not found"));

        BigDecimal adjustQty = request.getQuantity();
        if ("OUT".equals(request.getMovementType())) {
            adjustQty = adjustQty.negate();
        }

        if (item.getQuantity().add(adjustQty).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient stock quantity");
        }

        item.setQuantity(item.getQuantity().add(adjustQty));

        User user = getAuthenticatedUser();

        ForestMovement movement = new ForestMovement();
        movement.setForestItem(item);
        movement.setUser(user);
        movement.setQuantity(request.getQuantity());
        movement.setMovementType(request.getMovementType());
        movement.setReason(request.getReason());
        forestMovementRepository.save(movement);

        return forestItemRepository.save(item);
    }

    // ---- Planting Sets ----

    public List<PlantingSet> getAllSets() {
        return plantingSetRepository.findAllWithItems();
    }

    public List<PlantingSet> getActiveSets() {
        return plantingSetRepository.findAllActiveWithItems();
    }

    public PlantingSet getSet(Long id) {
        return plantingSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planting set not found"));
    }

    @Transactional
    public PlantingSet createSet(CreatePlantingSetRequest request) {
        PlantingSet set = new PlantingSet();
        set.setName(request.getName());
        set.setDescription(request.getDescription());
        if (request.getColor() != null) set.setColor(request.getColor());

        if (request.getItems() != null) {
            for (PlantingSetItemRequest itemReq : request.getItems()) {
                ForestItem forestItem = forestItemRepository.findById(itemReq.getForestItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Forest item not found: " + itemReq.getForestItemId()));

                PlantingSetItem setItem = new PlantingSetItem();
                setItem.setForestItem(forestItem);
                setItem.setQuantity(itemReq.getQuantity());
                set.addItem(setItem);
            }
        }

        return plantingSetRepository.save(set);
    }

    @Transactional
    public PlantingSet updateSet(Long id, CreatePlantingSetRequest request) {
        PlantingSet set = plantingSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planting set not found"));

        set.setName(request.getName());
        set.setDescription(request.getDescription());
        if (request.getColor() != null) set.setColor(request.getColor());

        set.getItems().clear();

        if (request.getItems() != null) {
            for (PlantingSetItemRequest itemReq : request.getItems()) {
                ForestItem forestItem = forestItemRepository.findById(itemReq.getForestItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Forest item not found: " + itemReq.getForestItemId()));

                PlantingSetItem setItem = new PlantingSetItem();
                setItem.setForestItem(forestItem);
                setItem.setQuantity(itemReq.getQuantity());
                set.addItem(setItem);
            }
        }

        return plantingSetRepository.save(set);
    }

    @Transactional
    public void deleteSet(Long id) {
        PlantingSet set = plantingSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planting set not found"));
        plantingSetRepository.delete(set);
    }

    @Transactional
    public void executeSet(Long setId, int count) {
        PlantingSet set = plantingSetRepository.findById(setId)
                .orElseThrow(() -> new ResourceNotFoundException("Planting set not found"));

        // Validate all components have sufficient stock
        for (PlantingSetItem setItem : set.getItems()) {
            ForestItem forestItem = forestItemRepository.findById(setItem.getForestItem().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Forest item not found"));

            BigDecimal required = setItem.getQuantity().multiply(BigDecimal.valueOf(count));
            if (forestItem.getQuantity().compareTo(required) < 0) {
                throw new IllegalArgumentException(
                        "Nedostatok materiálu: " + forestItem.getName() +
                        " (potrebné: " + required + " " + forestItem.getUnit() +
                        ", dostupné: " + forestItem.getQuantity() + " " + forestItem.getUnit() + ")");
            }
        }

        User user = getAuthenticatedUser();

        // Deduct all quantities and record movements
        for (PlantingSetItem setItem : set.getItems()) {
            ForestItem forestItem = forestItemRepository.findById(setItem.getForestItem().getId()).get();
            BigDecimal deduction = setItem.getQuantity().multiply(BigDecimal.valueOf(count));

            forestItem.setQuantity(forestItem.getQuantity().subtract(deduction));
            forestItemRepository.save(forestItem);

            ForestMovement movement = new ForestMovement();
            movement.setForestItem(forestItem);
            movement.setUser(user);
            movement.setQuantity(deduction);
            movement.setMovementType("OUT");
            movement.setReason("Výsadba: " + set.getName() + " (x" + count + ")");
            movement.setPlantingSet(set);
            forestMovementRepository.save(movement);
        }
    }

    // ---- Statistics ----

    public ForestStatistics getStatistics() {
        long totalItems = forestItemRepository.countActive();
        long treeCount = forestItemRepository.countActiveByCategory("TREE");
        long stakeCount = forestItemRepository.countActiveByCategory("STAKE");
        long protectionCount = forestItemRepository.countActiveByCategory("PROTECTION");
        long otherCount = forestItemRepository.countActiveByCategory("OTHER");
        long lowStockCount = forestItemRepository.findLowStock().size();

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long weeklyIn = forestMovementRepository.countByCreatedAtAfterAndMovementType(weekAgo, "IN");
        long weeklyOut = forestMovementRepository.countByCreatedAtAfterAndMovementType(weekAgo, "OUT");

        return new ForestStatistics(totalItems, treeCount, stakeCount, protectionCount, otherCount, lowStockCount, weeklyIn, weeklyOut);
    }

    // ---- Movements ----

    public List<ForestMovementDTO> getMovements() {
        return forestMovementRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ForestMovementDTO convertToDTO(ForestMovement movement) {
        ForestMovementDTO dto = new ForestMovementDTO();
        dto.setId(movement.getId());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setCreatedAt(movement.getCreatedAt());
        dto.setReason(movement.getReason());

        if (movement.getForestItem() != null) {
            dto.setItemName(movement.getForestItem().getName());
            dto.setItemCategory(movement.getForestItem().getCategory());
            dto.setUnit(movement.getForestItem().getUnit());
        }

        if (movement.getUser() != null) {
            dto.setUserName(movement.getUser().getFullName());
        }

        if (movement.getPlantingSet() != null) {
            dto.setPlantingSetName(movement.getPlantingSet().getName());
        }

        return dto;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated");
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userService.getUserById(userPrincipal.getId());
    }
}
