package com.farmstock.model.garden;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GardenStatistics {
    private long totalItems;
    private long treeCount;
    private long stakeCount;
    private long protectionCount;
    private long otherCount;
    private long lowStockCount;
    private long weeklyIn;
    private long weeklyOut;
}
