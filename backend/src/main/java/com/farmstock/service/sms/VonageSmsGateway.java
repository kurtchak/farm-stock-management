package com.farmstock.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "sms.provider", havingValue = "vonage")
@Slf4j
public class VonageSmsGateway implements SmsGateway {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sms.vonage.api-key}")
    private String apiKey;

    @Value("${sms.vonage.api-secret}")
    private String apiSecret;

    @Value("${sms.vonage.from-number}")
    private String fromNumber;

    @Override
    public void sendSms(String phoneNumber, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "api_key", apiKey,
                "api_secret", apiSecret,
                "from", fromNumber,
                "to", phoneNumber.replace("+", ""),
                "text", message
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://rest.nexmo.com/sms/json", request, String.class);
        log.info("SMS sent via Vonage to {}", phoneNumber);
    }

    @Override
    public String getProviderName() {
        return "vonage";
    }
}