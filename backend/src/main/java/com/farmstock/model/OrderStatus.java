package com.farmstock.model;

public enum OrderStatus {
    NEW,
    PROCESSING,
    CONFIRMED,
    FULFILLED,
    PARTIALLY_FULFILLED,
    INSUFFICIENT_STOCK,
    CANCELLED,
    FAILED
}