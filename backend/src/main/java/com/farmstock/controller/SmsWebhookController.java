package com.farmstock.controller;

import com.farmstock.model.Order;
import com.farmstock.model.OrderItem;
import com.farmstock.model.SmsLog;
import com.farmstock.model.dto.SmsWebhookRequest;
import com.farmstock.model.dto.SmsWebhookResponse;
import com.farmstock.model.dto.SmsWebhookResponse.OrderItemDto;
import com.farmstock.repository.SmsLogRepository;
import com.farmstock.service.AdminNotificationService;
import com.farmstock.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Slf4j
public class SmsWebhookController {

    private final OrderService orderService;
    private final SmsLogRepository smsLogRepository;
    private final AdminNotificationService adminNotificationService;

    /**
     * Webhook pre Twilio
     */
    @PostMapping(value = "/webhook/twilio", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> handleTwilioWebhook(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        return processIncomingWebhook(from, body, "twilio");
    }

    /**
     * Webhook pre MessageBird
     */
    @PostMapping("/webhook/messagebird")
    public ResponseEntity<String> handleMessageBirdWebhook(
            @RequestParam("originator") String from,
            @RequestParam("body") String body) {

        return processIncomingWebhook(from, body, "messagebird");
    }

    /**
     * Webhook pre Vonage
     */
    @PostMapping("/webhook/vonage")
    public ResponseEntity<String> handleVonageWebhook(@RequestBody Map<String, Object> request) {
        String from = (String) request.get("msisdn");
        String body = (String) request.get("text");

        return processIncomingWebhook("+" + from, body, "vonage");
    }

    private ResponseEntity<String> processIncomingWebhook(String from, String body, String provider) {
        log.info("Received {} webhook: from={}, body={}", provider, from, body);

        // 1. Zaloguj SMS do DB
        SmsLog smsLog = SmsLog.builder()
                              .senderPhone(normalizePhoneNumber(from))
                              .messageText(body)
                              .direction("IN")
                              .receivedAt(LocalDateTime.now())
                              .processed(false)
                              .build();
        smsLogRepository.save(smsLog);

        // 2. Notifikuj admina o novej SMS
        adminNotificationService.notifyNewSms(smsLog);

        try {
            // 3. Spracuj objednávku
            Order order = orderService.processIncomingSms(
                    normalizePhoneNumber(from),
                    body,
                    LocalDateTime.now()
            );

            // 4. Aktualizuj SMS log
            smsLog.setOrder(order);
            smsLog.setProcessed(true);
            smsLogRepository.save(smsLog);

            // 5. Notifikuj admina o novej objednávke
            adminNotificationService.notifyNewOrder(order);

            return ResponseEntity.ok("");

        } catch (Exception e) {
            log.error("Error processing webhook from {}: {}", provider, e.getMessage());
            smsLog.setErrorMessage(e.getMessage());
            smsLogRepository.save(smsLog);
            return ResponseEntity.ok(""); // Vráť 200 OK aj pri chybe
        }
    }

    /**
     * Manuálne testovanie cez JSON
     */
    @PostMapping("/incoming")
    public ResponseEntity<SmsWebhookResponse> handleIncomingSms(@RequestBody SmsWebhookRequest request) {
        log.info("Received SMS: from={}, text={}", request.getSenderPhone(), request.getMessageText());

        try {
            validateRequest(request);

            Order order = orderService.processIncomingSms(
                    normalizePhoneNumber(request.getSenderPhone()),
                    request.getMessageText(),
                    LocalDateTime.now()
            );

            List<OrderItemDto> items = order.getItems().stream()
                                            .map(this::toItemDto)
                                            .collect(Collectors.toList());

            SmsWebhookResponse response = SmsWebhookResponse.builder()
                                                            .success(true)
                                                            .orderNumber(order.getOrderNumber())
                                                            .status(order.getStatus())
                                                            .message("Objednavka " + order.getOrderNumber() + " bola prijata")
                                                            .items(items)
                                                            .receivedAt(order.getReceivedAt())
                                                            .notificationSent(order.hasInsufficientStock())
                                                            .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid SMS request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(SmsWebhookResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing SMS", e);
            return ResponseEntity.internalServerError()
                                 .body(SmsWebhookResponse.error("Chyba pri spracovani SMS: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "SMS Webhook",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<SmsLog>> getRecentLogs(@RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(orderService.getRecentSmsLogs(hours));
    }

    private void validateRequest(SmsWebhookRequest request) {
        if (request.getSenderPhone() == null || request.getSenderPhone().isBlank()) {
            throw new IllegalArgumentException("Chyba telefonné cislo odosielatela");
        }
        if (request.getMessageText() == null || request.getMessageText().isBlank()) {
            throw new IllegalArgumentException("Chyba text SMS spravy");
        }
    }

    private String normalizePhoneNumber(String phone) {
        if (phone == null) return null;
        String normalized = phone.replaceAll("[\\s\\-()]", "");
        if (!normalized.startsWith("+") && normalized.matches("^\\d.*")) {
            if (normalized.startsWith("09")) {
                normalized = "+421" + normalized.substring(1);
            } else if (normalized.startsWith("421")) {
                normalized = "+" + normalized;
            }
        }
        return normalized;
    }

    private OrderItemDto toItemDto(OrderItem item) {
        return OrderItemDto.builder()
                           .product(item.getRequestedProduct())
                           .requestedQuantity(item.getRequestedQuantity() + " " + item.getUnitOfMeasure())
                           .availableQuantity(item.getAvailableQuantity() + " " + item.getUnitOfMeasure())
                           .status(item.getStatus().name())
                           .build();
    }
}