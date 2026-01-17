package com.farmstock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "app.stock")
public class StockProperties {
    private BigDecimal deleteThreshold = BigDecimal.ZERO;

    public BigDecimal getDeleteThreshold() {
        return deleteThreshold;
    }

    public void setDeleteThreshold(BigDecimal deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }
}