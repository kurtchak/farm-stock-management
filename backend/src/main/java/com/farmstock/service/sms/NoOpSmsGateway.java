package com.farmstock.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sms.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class NoOpSmsGateway implements SmsGateway {

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("[NO-OP] Would send SMS to {}: {}", phoneNumber, message);
    }

    @Override
    public String getProviderName() {
        return "noop";
    }
}