package com.spendwise.controller;

import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.request.CreateApiKeyRequest;
import com.spendwise.dto.response.ApiKeyCreatedDto;
import com.spendwise.dto.response.ApiKeyDto;
import com.spendwise.service.CurrentUserService;
import com.spendwise.dto.service.UserApiKeyService;
import com.spendwise.utils.AuthenticationType;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/api-keys")
public class ApiKeyController {

    private final CurrentUserService currentUserService;
    private final UserApiKeyService userApiKeyService;

    public ApiKeyController(CurrentUserService currentUserService, UserApiKeyService userApiKeyService) {
        this.currentUserService = currentUserService;
        this.userApiKeyService = userApiKeyService;
    }

    @PostMapping
    public ApiKeyCreatedDto create(@Valid @RequestBody CreateApiKeyRequest request) {
        ensureGoogleAuthenticated();
        UserProfile user = currentUserService.getCurrentUser();
        return userApiKeyService.generate(user, request.getName());
    }

    @GetMapping
    public List<ApiKeyDto> list() {
        ensureGoogleAuthenticated();
        UserProfile user = currentUserService.getCurrentUser();
        return userApiKeyService.listActive(user).stream().map(ApiKeyDto::from).toList();
    }

    @DeleteMapping("/{id}")
    public void revoke(@PathVariable UUID id) {
        ensureGoogleAuthenticated();
        UserProfile user = currentUserService.getCurrentUser();
        userApiKeyService.revoke(user, id);
    }

    private void ensureGoogleAuthenticated() {
        if (currentUserService.getAuthenticationType() != AuthenticationType.GOOGLE) {
            throw new AccessDeniedException("API key management requires Google-authenticated browser session");
        }
    }
}
