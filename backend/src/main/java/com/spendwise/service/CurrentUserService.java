package com.spendwise.service;

import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.service.UserProfileService;
import com.spendwise.utils.AuthenticatedUser;
import com.spendwise.utils.AuthenticationType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserProfileService userProfileService;

    public CurrentUserService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public UserProfile getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new EntityNotFoundException("No authenticated user found");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser authenticatedUser) {
            return userProfileService.getById(authenticatedUser.getUserId());
        }
        if (principal instanceof OAuth2User oauth2User) {
            String googleSubject = oauth2User.getAttribute("sub");
            return userProfileService.getByGoogleSubject(googleSubject);
        }
        throw new EntityNotFoundException("Unsupported authentication principal");
    }

    public AuthenticationType getAuthenticationType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new EntityNotFoundException("No authenticated user found");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser authenticatedUser) {
            return authenticatedUser.getAuthenticationType();
        }
        if (principal instanceof OAuth2User) {
            return AuthenticationType.GOOGLE;
        }
        throw new EntityNotFoundException("Unsupported authentication principal");
    }
}
