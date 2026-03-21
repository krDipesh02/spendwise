package com.spendwise.controller;

import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.request.UpdateProfileRequest;
import com.spendwise.dto.response.UserProfileDto;
import com.spendwise.service.CurrentUserService;
import com.spendwise.dto.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final CurrentUserService currentUserService;
    private final UserProfileService userProfileService;

    public UserProfileController(CurrentUserService currentUserService, UserProfileService userProfileService) {
        this.currentUserService = currentUserService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public UserProfileDto getProfile() {
        return UserProfileDto.from(currentUserService.getCurrentUser());
    }

    @PutMapping
    public UserProfileDto updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfile user = currentUserService.getCurrentUser();
        return UserProfileDto.from(userProfileService.updateProfile(
                user.getId(),
                request.getDisplayName(),
                request.getBaseCurrency(),
                request.getTimezone(),
                request.getMonthlyLimit()
        ));
    }
}
