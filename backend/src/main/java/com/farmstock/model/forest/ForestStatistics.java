package com.farmstock.model.forest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForestStatistics {
    private long totalItems;
    private long treeCount;
    private long stakeCount;
    private long protectionCount;
    private long otherCount;
    private long lowStockCount;
    private long weeklyIn;
    private long weeklyOut;
}
