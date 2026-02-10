package com.farmstock.model.forest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlantingSetItemRequest {
    private Long forestItemId;
    private BigDecimal quantity;
}
