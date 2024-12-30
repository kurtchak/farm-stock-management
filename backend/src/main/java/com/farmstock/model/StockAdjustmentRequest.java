package com.farmstock.model;

import lombok.Data;

@Data
public class StockAdjustmentRequest {
    private Double quantity;
    private String movementType;  // IN, OUT, ADJUSTMENT
    private String reason;
    private Long userId;
}