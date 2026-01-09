package com.farmstock.service;

import com.farmstock.model.Order;
import com.farmstock.model.OrderItem;
import com.farmstock.model.SmsLog;
import com.farmstock.repository.SmsLogRepository;
import com.farmstock.service.sms.SmsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationService {

    private final SmsLogRepository smsLogRepository;
    private final SmsGateway smsGateway;
    private final AdminNotificationService adminNotificationService;

    @Async
    public void sendInsufficientStockNotification(Order order) {
        String message = buildInsufficientStockMessage(order);
        sendSms(order.getSenderPhone(), message, order);

        // Notifikácia admina
        adminNotificationService.notifyInsufficientStock(order);
    }

    @Async
    public void sendOrderConfirmation(Order order) {
        String message = String.format("Vasa objednavka %s bola prijata. Dakujeme!", order.getOrderNumber());
        sendSms(order.getSenderPhone(), message, order);
    }

    private String buildInsufficientStockMessage(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Objednavka ").append(order.getOrderNumber()).append(": ");

        String insufficientItems = order.getItems().stream()
                                        .filter(OrderItem::isInsufficient)
                                        .map(item -> String.format("%s (mate %s kg, ziadate %s kg)",
                                                item.getRequestedProduct(),
                                                formatQuantity(item.getAvailableQuantity()),
                                                formatQuantity(item.getRequestedQuantity())))
                                        .collect(Collectors.joining(", "));

        sb.append("Nedostatok na sklade: ").append(insufficientItems);
        sb.append(". Kontaktujte nas pre riesenie.");

        return sb.toString();
    }

    private String formatQuantity(BigDecimal quantity) {
        if (quantity == null) return "0";
        return quantity.stripTrailingZeros().toPlainString();
    }

    private void sendSms(String phoneNumber, String message, Order order) {
        log.info("Sending SMS via {} to {}: {}", smsGateway.getProviderName(), phoneNumber, message);

        SmsLog smsLog = SmsLog.builder()
                              .senderPhone(phoneNumber)
                              .messageText(message)
                              .direction("OUT")
                              .order(order)
                              .processed(false)
                              .build();

        try {
            smsGateway.sendSms(phoneNumber, message);
            smsLog.setProcessed(true);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            smsLog.setErrorMessage(e.getMessage());
        }

        smsLogRepository.save(smsLog);
    }
}