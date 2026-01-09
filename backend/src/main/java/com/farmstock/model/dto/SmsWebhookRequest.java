package com.farmstock.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsWebhookRequest {
    
    @JsonAlias({"from", "From", "sender", "phone", "phoneNumber", "msisdn"})
    private String senderPhone;
    
    @JsonAlias({"text", "body", "Body", "message", "smsMessage", "content"})
    private String messageText;
    
    @JsonAlias({"timestamp", "receivedAt", "DateReceived", "date"})
    private String timestamp;
    
    @JsonAlias({"id", "messageId", "MessageSid", "smsId"})
    private String messageId;
}