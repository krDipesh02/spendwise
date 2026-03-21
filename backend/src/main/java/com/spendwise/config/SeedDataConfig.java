package com.spendwise.config;

import com.spendwise.dto.entity.Category;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.repository.CategoryRepository;
import com.spendwise.dto.repository.UserProfileRepository;
import com.spendwise.dto.service.AuditService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Configuration
public class SeedDataConfig {

    private static final List<String[]> DEFAULT_CATEGORIES = List.of(
            new String[]{"Food", "utensils"},
            new String[]{"Travel", "plane"},
            new String[]{"Shopping", "shopping-bag"},
            new String[]{"Bills", "receipt"},
            new String[]{"Health", "heart-pulse"},
            new String[]{"Subscriptions", "repeat"}
    );

    private final CategoryRepository categoryRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuditService auditService;
    private final TransactionTemplate transactionTemplate;

    public SeedDataConfig(CategoryRepository categoryRepository,
                          UserProfileRepository userProfileRepository,
                          AuditService auditService,
                          PlatformTransactionManager transactionManager) {
        this.categoryRepository = categoryRepository;
        this.userProfileRepository = userProfileRepository;
        this.auditService = auditService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Bean
    ApplicationRunner seedDefaultDataOnStartup() {
        return args -> transactionTemplate.execute(status -> {
            userProfileRepository.findAll().forEach(this::seedDefaultCategories);
            return null;
        });
    }

    @Transactional
    public List<Category> seedDefaultCategories(UserProfile user) {
        List<Category> existingCategories = categoryRepository.findByUserIdOrderByNameAsc(user.getId());
        if (!existingCategories.isEmpty()) {
            return existingCategories;
        }

        List<Category> categories = DEFAULT_CATEGORIES.stream()
                .map(defaultCategory -> {
                    Category category = new Category();
                    category.setUser(user);
                    category.setName(defaultCategory[0]);
                    category.setIcon(defaultCategory[1]);
                    category.setActive(true);
                    return category;
                })
                .toList();

        List<Category> saved = categoryRepository.saveAll(categories);
        auditService.log(user, "SEED_DEFAULT_CATEGORIES", "CATEGORY", "BATCH", String.valueOf(saved.size()));
        return saved;
    }
}
