package com.farmstock.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "sms.provider", havingValue = "messagebird")
@Slf4j
public class MessageBirdSmsGateway implements SmsGateway {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sms.messagebird.access-key}")
    private String accessKey;

    @Value("${sms.messagebird.originator}")
    private String originator;

    @Override
    public void sendSms(String phoneNumber, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "AccessKey " + accessKey);

        Map<String, Object> body = Map.of(
                "originator", originator,
                "recipients", List.of(phoneNumber),
                "body", message
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://rest.messagebird.com/messages", request, String.class);
        log.info("SMS sent via MessageBird to {}", phoneNumber);
    }

    @Override
    public String getProviderName() {
        return "messagebird";
    }
}