package com.farmstock.model.forest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ForestAdjustmentRequest {
    private BigDecimal quantity;
    private String movementType;
    private String reason;
}
