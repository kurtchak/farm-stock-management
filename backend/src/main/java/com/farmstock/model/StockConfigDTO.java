package com.farmstock.model;

import java.math.BigDecimal;

public class StockConfigDTO {
    private BigDecimal deleteThreshold;

    public StockConfigDTO() {
    }

    public StockConfigDTO(BigDecimal deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }

    public BigDecimal getDeleteThreshold() {
        return deleteThreshold;
    }

    public void setDeleteThreshold(BigDecimal deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }
}