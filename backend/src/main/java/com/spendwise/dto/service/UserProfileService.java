package com.spendwise.dto.service;

import com.spendwise.config.SeedDataConfig;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final SeedDataConfig seedDataConfig;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(UserProfileRepository userProfileRepository,
                              SeedDataConfig seedDataConfig,
                              AuditService auditService,
                              PasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.seedDataConfig = seedDataConfig;
        this.auditService = auditService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserProfile getById(UUID userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id=" + userId));
    }

    @Transactional(readOnly = true)
    public UserProfile getByGoogleSubject(String googleSubject) {
        return userProfileRepository.findByGoogleSubject(googleSubject)
                .orElseThrow(() -> new EntityNotFoundException("User not found for google subject"));
    }

    @Transactional
    public UserProfile getOrCreateFromGoogle(@NotBlank String googleSubject,
                                             String email,
                                             boolean emailVerified,
                                             String pictureUrl,
                                             String googleDisplayName) {
        var existingUser = userProfileRepository.findByGoogleSubject(googleSubject);
        UserProfile user = existingUser.orElseGet(UserProfile::new);
        user.setGoogleSubject(googleSubject);
        user.setEmail(email);
        user.setEmailVerified(emailVerified);
        user.setPictureUrl(pictureUrl);
        if (googleDisplayName != null && !googleDisplayName.isBlank()) {
            user.setDisplayName(googleDisplayName);
        }
        if (user.getBaseCurrency() == null || user.getBaseCurrency().isBlank()) {
            user.setBaseCurrency("INR");
        }
        if (user.getTimezone() == null || user.getTimezone().isBlank()) {
            user.setTimezone("Asia/Kolkata");
        }
        if (user.getMonthlyLimit() == null) {
            user.setMonthlyLimit(BigDecimal.ZERO);
        }
        UserProfile saved = userProfileRepository.save(user);
        seedDataConfig.seedDefaultCategories(saved);
        auditService.log(saved, "LOGIN_GOOGLE", "USER", saved.getId().toString(), saved.getEmail());
        return saved;
    }

    @Transactional
    public UserProfile updateProfile(UUID userId,
                                     @NotBlank String displayName,
                                     @NotBlank String baseCurrency,
                                     @NotBlank String timezone,
                                     @NotNull BigDecimal monthlyLimit) {
        UserProfile user = getById(userId);
        user.setDisplayName(displayName);
        user.setBaseCurrency(baseCurrency);
        user.setTimezone(timezone);
        user.setMonthlyLimit(monthlyLimit);
        UserProfile saved = userProfileRepository.save(user);
        auditService.log(saved, "UPDATE_PROFILE", "USER", saved.getId().toString(), saved.getDisplayName());
        return saved;
    }

    @Transactional
    public UserProfile registerPasswordUser(@NotBlank String username,
                                            @NotBlank String rawPassword,
                                            @NotBlank String displayName) {
        if (userProfileRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        UserProfile user = new UserProfile();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setDisplayName(displayName);
        user.setBaseCurrency("INR");
        user.setTimezone("Asia/Kolkata");
        user.setMonthlyLimit(BigDecimal.ZERO);
        UserProfile saved = userProfileRepository.save(user);
        seedDataConfig.seedDefaultCategories(saved);
        auditService.log(saved, "REGISTER_PASSWORD", "USER", saved.getId().toString(), saved.getUsername());
        return saved;
    }

    @Transactional(readOnly = true)
    public UserProfile getByUsername(@NotBlank String username) {
        return userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found for username"));
    }

    public boolean matchesPassword(UserProfile user, String rawPassword) {
        return user.getPasswordHash() != null && passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}
