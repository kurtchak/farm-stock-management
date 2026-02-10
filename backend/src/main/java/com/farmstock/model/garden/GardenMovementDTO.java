package com.farmstock.model.garden;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GardenMovementDTO {
    private Long id;
    private String itemName;
    private String itemCategory;
    private String movementType;
    private BigDecimal quantity;
    private String unit;
    private String reason;
    private String plantingSetName;
    private String userName;
    private LocalDateTime createdAt;
}
