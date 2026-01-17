package com.farmstock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockStatistics {
    private long totalItems;
    private long weeklyIn;
    private long weeklyOut;
}