package com.spendwise.dto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spendwise.config.AuthProperties;
import com.spendwise.dto.entity.ConversationMemory;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.repository.ConversationMemoryRepository;
import com.spendwise.dto.response.ConversationMemoryMessageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ConversationMemoryService {

    private static final TypeReference<List<ConversationMemoryMessageResponse>> TRANSCRIPT_TYPE = new TypeReference<>() {};

    private final ConversationMemoryRepository conversationMemoryRepository;
    private final AuthProperties authProperties;
    private final ObjectMapper objectMapper;
    private final AuditService auditService;

    public ConversationMemoryService(ConversationMemoryRepository conversationMemoryRepository,
                                     AuthProperties authProperties,
                                     ObjectMapper objectMapper,
                                     AuditService auditService) {
        this.conversationMemoryRepository = conversationMemoryRepository;
        this.authProperties = authProperties;
        this.objectMapper = objectMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public MemorySnapshot load(UserProfile user) {
        var memory = conversationMemoryRepository.findByUserId(user.getId());
        if (memory.isEmpty()) {
            return new MemorySnapshot(Collections.emptyList(), null);
        }

        ConversationMemory persisted = memory.get();
        if (persisted.getExpiresAt().isBefore(Instant.now())) {
            return new MemorySnapshot(Collections.emptyList(), persisted.getExpiresAt());
        }

        return new MemorySnapshot(readTranscript(persisted.getTranscriptJson()), persisted.getExpiresAt());
    }

    @Transactional
    public MemorySnapshot replace(UserProfile user, List<ConversationMemoryMessageResponse> messages) {
        List<ConversationMemoryMessageResponse> sanitized = trim(messages);
        if (sanitized.isEmpty()) {
            clear(user);
            return new MemorySnapshot(Collections.emptyList(), null);
        }

        ConversationMemory memory = conversationMemoryRepository.findByUserId(user.getId())
                .orElseGet(ConversationMemory::new);
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(authProperties.getConversationMemoryTtlSeconds());
        memory.setUser(user);
        memory.setTranscriptJson(writeTranscript(sanitized));
        memory.setLastActivityAt(now);
        memory.setExpiresAt(expiresAt);
        conversationMemoryRepository.save(memory);
        auditService.log(user, "UPSERT_CONVERSATION_MEMORY", "CONVERSATION_MEMORY", memory.getId().toString(),
                "messages=" + sanitized.size());
        return new MemorySnapshot(sanitized, expiresAt);
    }

    @Transactional
    public void clear(UserProfile user) {
        conversationMemoryRepository.findByUserId(user.getId()).ifPresent(memory -> {
            conversationMemoryRepository.delete(memory);
            auditService.log(user, "CLEAR_CONVERSATION_MEMORY", "CONVERSATION_MEMORY", memory.getId().toString(), "telegram");
        });
    }

    private List<ConversationMemoryMessageResponse> trim(List<ConversationMemoryMessageResponse> messages) {
        int maxMessages = Math.max(1, authProperties.getConversationMemoryMaxMessages());
        List<ConversationMemoryMessageResponse> normalized = new ArrayList<>();
        for (ConversationMemoryMessageResponse message : messages) {
            if (message == null || message.role() == null || message.content() == null) {
                continue;
            }
            String role = message.role().trim().toLowerCase();
            String content = message.content().trim();
            if (content.isBlank()) {
                continue;
            }
            if (!role.equals("user") && !role.equals("assistant") && !role.equals("system")) {
                throw new IllegalArgumentException("Unsupported conversation role: " + message.role());
            }
            normalized.add(new ConversationMemoryMessageResponse(role, content));
        }
        if (normalized.size() <= maxMessages) {
            return normalized;
        }
        return new ArrayList<>(normalized.subList(normalized.size() - maxMessages, normalized.size()));
    }

    private List<ConversationMemoryMessageResponse> readTranscript(String json) {
        try {
            return objectMapper.readValue(json, TRANSCRIPT_TYPE);
        } catch (JsonProcessingException ex) {
            throw new EntityNotFoundException("Stored conversation memory could not be parsed");
        }
    }

    private String writeTranscript(List<ConversationMemoryMessageResponse> messages) {
        try {
            return objectMapper.writeValueAsString(messages);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Conversation memory could not be serialized", ex);
        }
    }

    public record MemorySnapshot(List<ConversationMemoryMessageResponse> messages, Instant expiresAt) {
    }
}
