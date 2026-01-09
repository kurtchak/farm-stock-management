package com.farmstock.service.sms;

public interface SmsGateway {
    void sendSms(String phoneNumber, String message);
    String getProviderName();
}