package com.spendwise.dto.service;

import com.spendwise.dto.entity.UserApiKey;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.repository.UserApiKeyRepository;
import com.spendwise.dto.response.ApiKeyCreatedDto;
import com.spendwise.utils.ApiKeyHasher;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class UserApiKeyService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserApiKeyRepository userApiKeyRepository;
    private final AuditService auditService;

    public UserApiKeyService(UserApiKeyRepository userApiKeyRepository, AuditService auditService) {
        this.userApiKeyRepository = userApiKeyRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ApiKeyCreatedDto generate(UserProfile user, String name) {
        byte[] buffer = new byte[32];
        SECURE_RANDOM.nextBytes(buffer);
        String token = "spw_" + Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
        String hash = ApiKeyHasher.sha256(token);
        String prefix = token.substring(0, Math.min(token.length(), 16));

        UserApiKey apiKey = new UserApiKey();
        apiKey.setUser(user);
        apiKey.setName(name);
        apiKey.setKeyPrefix(prefix);
        apiKey.setKeyHash(hash);
        UserApiKey saved = userApiKeyRepository.save(apiKey);
        auditService.log(user, "CREATE_API_KEY", "API_KEY", saved.getId().toString(), prefix);
        return new ApiKeyCreatedDto(saved.getId().toString(), saved.getName(), saved.getKeyPrefix(), token);
    }

    @Transactional(readOnly = true)
    public List<UserApiKey> listActive(UserProfile user) {
        return userApiKeyRepository.findByUserIdAndRevokedAtIsNullOrderByCreatedAtDesc(user.getId());
    }

    @Transactional
    public void revoke(UserProfile user, UUID apiKeyId) {
        UserApiKey apiKey = userApiKeyRepository.findByIdAndUserId(apiKeyId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("API key not found"));
        apiKey.setRevokedAt(Instant.now());
        auditService.log(user, "REVOKE_API_KEY", "API_KEY", apiKey.getId().toString(), apiKey.getKeyPrefix());
    }

    @Transactional
    public UserProfile authenticate(String token) {
        String hash = ApiKeyHasher.sha256(token);
        UserApiKey apiKey = userApiKeyRepository.findByKeyHashAndRevokedAtIsNull(hash)
                .orElseThrow(() -> new EntityNotFoundException("API key not found"));
        apiKey.setLastUsedAt(Instant.now());
        return apiKey.getUser();
    }
}
