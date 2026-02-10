package com.farmstock.model.garden;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GardenAdjustmentRequest {
    private BigDecimal quantity;
    private String movementType;
    private String reason;
}
