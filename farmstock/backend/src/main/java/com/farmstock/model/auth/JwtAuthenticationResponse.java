package com.farmstock.model.auth;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {
    private String token;
    private String type = "Bearer";

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }
}