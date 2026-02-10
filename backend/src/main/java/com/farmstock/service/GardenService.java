package com.farmstock.service;

import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.model.*;
import com.farmstock.model.garden.*;
import com.farmstock.repository.GardenItemRepository;
import com.farmstock.repository.GardenMovementRepository;
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
public class GardenService {

    private final GardenItemRepository gardenItemRepository;
    private final PlantingSetRepository plantingSetRepository;
    private final GardenMovementRepository gardenMovementRepository;
    private final UserService userService;

    @Autowired
    public GardenService(GardenItemRepository gardenItemRepository,
                         PlantingSetRepository plantingSetRepository,
                         GardenMovementRepository gardenMovementRepository,
                         UserService userService) {
        this.gardenItemRepository = gardenItemRepository;
        this.plantingSetRepository = plantingSetRepository;
        this.gardenMovementRepository = gardenMovementRepository;
        this.userService = userService;
    }

    // ---- Garden Items ----

    public List<GardenItem> getAllItems() {
        return gardenItemRepository.findAllActive();
    }

    public List<GardenItem> getItemsByCategory(String category) {
        return gardenItemRepository.findActiveByCategory(category);
    }

    public GardenItem getItem(Long id) {
        return gardenItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garden item not found"));
    }

    @Transactional
    public GardenItem createItem(CreateGardenItemRequest request) {
        GardenItem item = new GardenItem();
        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setQuantity(request.getQuantity() != null ? request.getQuantity() : BigDecimal.ZERO);
        item.setUnit(request.getUnit() != null ? request.getUnit() : "ks");
        item.setMinStock(request.getMinStock() != null ? request.getMinStock() : BigDecimal.ZERO);
        item.setNotes(request.getNotes());
        return gardenItemRepository.save(item);
    }

    @Transactional
    public GardenItem updateItem(Long id, UpdateGardenItemRequest request) {
        GardenItem item = gardenItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garden item not found"));

        if (request.getName() != null) item.setName(request.getName());
        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getUnit() != null) item.setUnit(request.getUnit());
        if (request.getMinStock() != null) item.setMinStock(request.getMinStock());
        if (request.getNotes() != null) item.setNotes(request.getNotes());

        return gardenItemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        GardenItem item = gardenItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garden item not found"));
        item.setDeleted(true);
        gardenItemRepository.save(item);
    }

    @Transactional
    public GardenItem adjustItem(Long id, GardenAdjustmentRequest request) {
        GardenItem item = gardenItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garden item not found"));

        BigDecimal adjustQty = request.getQuantity();
        if ("OUT".equals(request.getMovementType())) {
            adjustQty = adjustQty.negate();
        }

        if (item.getQuantity().add(adjustQty).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient stock quantity");
        }

        item.setQuantity(item.getQuantity().add(adjustQty));

        User user = getAuthenticatedUser();

        GardenMovement movement = new GardenMovement();
        movement.setGardenItem(item);
        movement.setUser(user);
        movement.setQuantity(request.getQuantity());
        movement.setMovementType(request.getMovementType());
        movement.setReason(request.getReason());
        gardenMovementRepository.save(movement);

        return gardenItemRepository.save(item);
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
                GardenItem gardenItem = gardenItemRepository.findById(itemReq.getGardenItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Garden item not found: " + itemReq.getGardenItemId()));

                PlantingSetItem setItem = new PlantingSetItem();
                setItem.setGardenItem(gardenItem);
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
                GardenItem gardenItem = gardenItemRepository.findById(itemReq.getGardenItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Garden item not found: " + itemReq.getGardenItemId()));

                PlantingSetItem setItem = new PlantingSetItem();
                setItem.setGardenItem(gardenItem);
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
            GardenItem gardenItem = gardenItemRepository.findById(setItem.getGardenItem().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garden item not found"));

            BigDecimal required = setItem.getQuantity().multiply(BigDecimal.valueOf(count));
            if (gardenItem.getQuantity().compareTo(required) < 0) {
                throw new IllegalArgumentException(
                        "Nedostatok materiálu: " + gardenItem.getName() +
                        " (potrebné: " + required + " " + gardenItem.getUnit() +
                        ", dostupné: " + gardenItem.getQuantity() + " " + gardenItem.getUnit() + ")");
            }
        }

        User user = getAuthenticatedUser();

        // Deduct all quantities and record movements
        for (PlantingSetItem setItem : set.getItems()) {
            GardenItem gardenItem = gardenItemRepository.findById(setItem.getGardenItem().getId()).get();
            BigDecimal deduction = setItem.getQuantity().multiply(BigDecimal.valueOf(count));

            gardenItem.setQuantity(gardenItem.getQuantity().subtract(deduction));
            gardenItemRepository.save(gardenItem);

            GardenMovement movement = new GardenMovement();
            movement.setGardenItem(gardenItem);
            movement.setUser(user);
            movement.setQuantity(deduction);
            movement.setMovementType("OUT");
            movement.setReason("Výsadba: " + set.getName() + " (x" + count + ")");
            movement.setPlantingSet(set);
            gardenMovementRepository.save(movement);
        }
    }

    // ---- Statistics ----

    public GardenStatistics getStatistics() {
        long totalItems = gardenItemRepository.countActive();
        long treeCount = gardenItemRepository.countActiveByCategory("TREE");
        long stakeCount = gardenItemRepository.countActiveByCategory("STAKE");
        long protectionCount = gardenItemRepository.countActiveByCategory("PROTECTION");
        long otherCount = gardenItemRepository.countActiveByCategory("OTHER");
        long lowStockCount = gardenItemRepository.findLowStock().size();

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long weeklyIn = gardenMovementRepository.countByCreatedAtAfterAndMovementType(weekAgo, "IN");
        long weeklyOut = gardenMovementRepository.countByCreatedAtAfterAndMovementType(weekAgo, "OUT");

        return new GardenStatistics(totalItems, treeCount, stakeCount, protectionCount, otherCount, lowStockCount, weeklyIn, weeklyOut);
    }

    // ---- Movements ----

    public List<GardenMovementDTO> getMovements() {
        return gardenMovementRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private GardenMovementDTO convertToDTO(GardenMovement movement) {
        GardenMovementDTO dto = new GardenMovementDTO();
        dto.setId(movement.getId());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setCreatedAt(movement.getCreatedAt());
        dto.setReason(movement.getReason());

        if (movement.getGardenItem() != null) {
            dto.setItemName(movement.getGardenItem().getName());
            dto.setItemCategory(movement.getGardenItem().getCategory());
            dto.setUnit(movement.getGardenItem().getUnit());
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
