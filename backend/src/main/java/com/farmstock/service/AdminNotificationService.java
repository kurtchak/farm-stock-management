package com.farmstock.service;

import com.farmstock.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class AdminNotificationService {

    private final RestTemplate restTemplate = new RestTemplate();
    
    // Optional - môže byť null ak nie je nakonfigurovaný
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${admin.notification.enabled:false}")
    private boolean enabled;

    @Value("${admin.notification.email:}")
    private String adminEmail;

    @Value("${admin.notification.telegram.bot-token:}")
    private String telegramBotToken;

    @Value("${admin.notification.telegram.chat-id:}")
    private String telegramChatId;

    /**
     * Notifikácia o novej objednávke
     */
    @Async
    public void notifyNewOrder(Order order) {
        if (!enabled) return;

        String message = String.format(
                "📦 Nová objednávka %s\nOd: %s\nStatus: %s",
                order.getOrderNumber(),
                order.getSenderPhone() != null ? order.getSenderPhone() : "N/A",
                order.getStatus()
        );

        sendNotifications("Nová objednávka", message);
    }

    /**
     * Notifikácia o nedostatku na sklade
     */
    @Async
    public void notifyInsufficientStock(Order order) {
        if (!enabled) return;

        String message = String.format(
                "⚠️ Nedostatok na sklade!\nObjednávka: %s\nOd: %s",
                order.getOrderNumber(),
                order.getSenderPhone() != null ? order.getSenderPhone() : "N/A"
        );

        sendNotifications("Nedostatok na sklade", message);
    }

    private void sendNotifications(String subject, String message) {
        sendEmailNotification(subject, message);
        sendTelegramNotification(message);
    }

    private void sendEmailNotification(String subject, String message) {
        if (adminEmail == null || adminEmail.isBlank() || mailSender == null) {
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(adminEmail);
            mail.setSubject("[FarmStock] " + subject);
            mail.setText(message);
            mailSender.send(mail);
            log.info("Email notification sent to {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
        }
    }

    private void sendTelegramNotification(String message) {
        if (telegramBotToken == null || telegramBotToken.isBlank() ||
            telegramChatId == null || telegramChatId.isBlank()) {
            return;
        }

        try {
            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage",
                    telegramBotToken
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "chat_id", telegramChatId,
                    "text", message,
                    "parse_mode", "HTML"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
            log.info("Telegram notification sent");
        } catch (Exception e) {
            log.error("Failed to send Telegram notification: {}", e.getMessage());
        }
    }
}