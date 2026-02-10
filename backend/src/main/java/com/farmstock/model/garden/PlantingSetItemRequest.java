package com.farmstock.model.garden;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlantingSetItemRequest {
    private Long gardenItemId;
    private BigDecimal quantity;
}
