package com.spendwise.controller;

import com.spendwise.dto.request.ConversationMemoryUpsertRequest;
import com.spendwise.dto.request.TelegramBootstrapRequest;
import com.spendwise.dto.response.ConversationMemoryMessageResponse;
import com.spendwise.dto.response.ConversationMemoryResponse;
import com.spendwise.dto.response.TelegramBootstrapResponse;
import com.spendwise.dto.service.ConversationMemoryService;
import com.spendwise.dto.service.UserProfileService;
import com.spendwise.service.AutomationServiceAuthenticator;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@Slf4j
public class TelegramAutomationController {

    private final AutomationServiceAuthenticator automationServiceAuthenticator;
    private final UserProfileService userProfileService;
    private final ConversationMemoryService conversationMemoryService;

    public TelegramAutomationController(AutomationServiceAuthenticator automationServiceAuthenticator,
                                        UserProfileService userProfileService,
                                        ConversationMemoryService conversationMemoryService) {
        this.automationServiceAuthenticator = automationServiceAuthenticator;
        this.userProfileService = userProfileService;
        this.conversationMemoryService = conversationMemoryService;
    }

    @PostMapping("/telegram/bootstrap")
    public TelegramBootstrapResponse bootstrap(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                               @Valid @RequestBody TelegramBootstrapRequest request) {
        automationServiceAuthenticator.authenticate(authorization);
        var result = userProfileService.getOrCreateFromTelegram(
                request.getTelegramUserId(),
                request.getTelegramUsername(),
                request.getFirstName(),
                request.getLastName()
        );
        log.info("Bootstrapped Telegram user telegramId={} userId={} created={}",
                request.getTelegramUserId(), result.user().getId(), result.created());
        return new TelegramBootstrapResponse(
                result.user().getId().toString(),
                result.user().getTelegramId(),
                result.user().getDisplayName(),
                result.created()
        );
    }

    @GetMapping("/telegram/memory/{telegramUserId}")
    public ConversationMemoryResponse getMemory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                @PathVariable String telegramUserId) {
        automationServiceAuthenticator.authenticate(authorization);
        var user = resolveTelegramUser(telegramUserId);
        var snapshot = conversationMemoryService.load(user);
        return new ConversationMemoryResponse(telegramUserId, snapshot.messages(), snapshot.expiresAt());
    }

    @PutMapping("/telegram/memory/{telegramUserId}")
    public ConversationMemoryResponse upsertMemory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                   @PathVariable String telegramUserId,
                                                   @Valid @RequestBody ConversationMemoryUpsertRequest request) {
        automationServiceAuthenticator.authenticate(authorization);
        if (!telegramUserId.equals(request.getTelegramUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "telegramUserId path/body mismatch");
        }
        var user = resolveTelegramUser(telegramUserId);
        var snapshot = conversationMemoryService.replace(
                user,
                request.getMessages().stream()
                        .map(message -> new ConversationMemoryMessageResponse(message.getRole(), message.getContent()))
                        .toList()
        );
        return new ConversationMemoryResponse(telegramUserId, snapshot.messages(), snapshot.expiresAt());
    }

    @DeleteMapping("/telegram/memory/{telegramUserId}")
    public ConversationMemoryResponse clearMemory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                  @PathVariable String telegramUserId) {
        automationServiceAuthenticator.authenticate(authorization);
        var user = resolveTelegramUser(telegramUserId);
        conversationMemoryService.clear(user);
        return new ConversationMemoryResponse(telegramUserId, java.util.List.of(), null);
    }

    private com.spendwise.dto.entity.UserProfile resolveTelegramUser(String telegramUserId) {
        try {
            return userProfileService.getByTelegramId(telegramUserId);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Telegram user is not linked to Spendwise", ex);
        }
    }
}
