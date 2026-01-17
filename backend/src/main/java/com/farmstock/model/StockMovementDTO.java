package com.farmstock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {
    private Long id;
    private String cropName;
    private String cropVariety;
    private String movementType;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private LocalDateTime createdAt;
    private String reason;
}