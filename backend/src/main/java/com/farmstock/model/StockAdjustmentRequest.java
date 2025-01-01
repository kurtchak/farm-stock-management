package com.farmstock.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockAdjustmentRequest {
    private BigDecimal quantity;
    private String movementType;
    private String reason;
    private Long userId;
}