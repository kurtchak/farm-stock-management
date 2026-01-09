package com.farmstock.model.dto;

import com.farmstock.model.OrderStatus;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsWebhookResponse {
    private boolean success;
    private String message;
    private String orderNumber;
    private OrderStatus status;
    private List<OrderItemDto> items;
    private LocalDateTime receivedAt;
    private String errorMessage;
    private boolean notificationSent;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDto {
        private String product;
        private String requestedQuantity;
        private String availableQuantity;
        private String status;
    }

    public static SmsWebhookResponse success(String orderNumber, String message) {
        return SmsWebhookResponse.builder()
                .success(true)
                .orderNumber(orderNumber)
                .message(message)
                .build();
    }

    public static SmsWebhookResponse error(String errorMessage) {
        return SmsWebhookResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}