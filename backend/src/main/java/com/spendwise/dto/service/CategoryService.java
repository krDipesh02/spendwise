package com.spendwise.dto.service;

import com.spendwise.config.SeedDataConfig;
import com.spendwise.dto.entity.Category;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuditService auditService;
    private final SeedDataConfig seedDataConfig;

    public CategoryService(CategoryRepository categoryRepository,
                           AuditService auditService,
                           SeedDataConfig seedDataConfig) {
        this.categoryRepository = categoryRepository;
        this.auditService = auditService;
        this.seedDataConfig = seedDataConfig;
    }

    @Transactional
    public List<Category> list(UserProfile user) {
        List<Category> categories = categoryRepository.findByUserIdOrderByNameAsc(user.getId());
        if (!categories.isEmpty()) {
            return categories;
        }
        return seedDataConfig.seedDefaultCategories(user);
    }

    @Transactional
    public Category create(UserProfile user, String name, String icon) {
        Category category = new Category();
        category.setUser(user);
        category.setName(name);
        category.setIcon(icon);
        category.setActive(true);
        Category saved = categoryRepository.save(category);
        auditService.log(user, "CREATE_CATEGORY", "CATEGORY", saved.getId().toString(), name);
        return saved;
    }

    @Transactional
    public Category update(UserProfile user, UUID id, String name, String icon, boolean active) {
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        category.setName(name);
        category.setIcon(icon);
        category.setActive(active);
        auditService.log(user, "UPDATE_CATEGORY", "CATEGORY", category.getId().toString(), name);
        return category;
    }
}
