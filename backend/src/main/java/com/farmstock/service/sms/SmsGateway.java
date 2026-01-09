package com.farmstock.service;

public interface SmsGateway {
    void sendSms(String phoneNumber, String message);
    String getProviderName();
}