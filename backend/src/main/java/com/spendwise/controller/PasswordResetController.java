package com.spendwise.controller;

import com.spendwise.dto.entity.PasswordResetToken;
import com.spendwise.dto.request.PasswordResetConfirmRequest;
import com.spendwise.dto.request.PasswordResetRequest;
import com.spendwise.dto.response.PasswordResetTokenResponse;
import com.spendwise.dto.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth/password/reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public PasswordResetTokenResponse request(@Valid @RequestBody PasswordResetRequest request) {
        PasswordResetToken token = passwordResetService.requestReset(request.getUsername());
        return new PasswordResetTokenResponse(token.getToken(), token.getExpiresAt());
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirm(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            passwordResetService.confirmReset(request.getToken(), request.getNewPassword());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return Map.of("status", "password_reset");
    }
}
