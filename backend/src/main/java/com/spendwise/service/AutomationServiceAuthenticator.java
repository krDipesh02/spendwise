package com.spendwise.service;

import com.spendwise.config.AuthProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class AutomationServiceAuthenticator {

    private final AuthProperties authProperties;

    public AutomationServiceAuthenticator(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public void authenticate(String authorizationHeader) {
        String configured = authProperties.getAutomationServiceToken();
        if (configured == null || configured.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Automation service token is not configured");
        }
        String expected = "Bearer " + configured.trim();
        if (authorizationHeader == null || !MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                authorizationHeader.trim().getBytes(StandardCharsets.UTF_8))
        ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid automation service credential");
        }
    }
}
