package com.farmstock.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("${sms.enabled:false} == false or '${sms.provider:}' == ''")
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