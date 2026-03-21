package com.spendwise.controller;

import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.request.SaveCategoryRequest;
import com.spendwise.dto.request.UpdateCategoryRequest;
import com.spendwise.dto.response.CategoryDto;
import com.spendwise.dto.service.CategoryService;
import com.spendwise.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public CategoryController(CategoryService categoryService, CurrentUserService currentUserService) {
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.list(currentUserService.getCurrentUser()).stream().map(CategoryDto::from).toList();
    }

    @PostMapping
    public CategoryDto create(@Valid @RequestBody SaveCategoryRequest request) {
        UserProfile user = currentUserService.getCurrentUser();
        return CategoryDto.from(categoryService.create(user, request.getName(), request.getIcon()));
    }

    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest request) {
        UserProfile user = currentUserService.getCurrentUser();
        return CategoryDto.from(categoryService.update(user, id, request.getName(), request.getIcon(), request.isActive()));
    }
}
