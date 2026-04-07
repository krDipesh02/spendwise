package com.spendwise.controller;

import com.spendwise.dto.request.AutomationApiKeyExchangeRequest;
import com.spendwise.dto.response.ApiKeyCreatedDto;
import com.spendwise.dto.service.UserApiKeyService;
import com.spendwise.dto.service.UserProfileService;
import com.spendwise.service.AutomationServiceAuthenticator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/automation")
@Slf4j
public class AutomationApiKeyExchangeController {

    private static final String DEFAULT_API_KEY_NAME = "mcp";

    private final AutomationServiceAuthenticator automationServiceAuthenticator;
    private final UserProfileService userProfileService;
    private final UserApiKeyService userApiKeyService;

    public AutomationApiKeyExchangeController(AutomationServiceAuthenticator automationServiceAuthenticator,
                                                UserProfileService userProfileService,
                                                UserApiKeyService userApiKeyService) {
        this.automationServiceAuthenticator = automationServiceAuthenticator;
        this.userProfileService = userProfileService;
        this.userApiKeyService = userApiKeyService;
    }

    /**
     * Service-authenticated exchange: ensures a Telegram-linked user exists and mints a new API key for MCP-style access.
     */
    @PostMapping("/api-key-exchange")
    public ApiKeyCreatedDto exchange(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                     @Valid @RequestBody AutomationApiKeyExchangeRequest request) {
        automationServiceAuthenticator.authenticate(authorization);
        var result = userProfileService.getOrCreateFromTelegram(
                request.getTelegramUserId(),
                request.getTelegramUsername(),
                request.getFirstName(),
                request.getLastName()
        );
        String keyName = request.getApiKeyName();
        if (keyName == null || keyName.isBlank()) {
            keyName = DEFAULT_API_KEY_NAME;
        }
        log.info("API key exchange for telegramId={} userId={} created={} keyName={}",
                request.getTelegramUserId(), result.user().getId(), result.created(), keyName);
        return userApiKeyService.generate(result.user(), keyName);
    }
}
