package com.spendwise.controller;

import com.spendwise.dto.entity.UserProfile;
import com.spendwise.service.CurrentUserService;
import com.spendwise.utils.AuthenticatedUser;
import com.spendwise.utils.AuthenticationType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthSessionController {

    private final CurrentUserService currentUserService;

    public AuthSessionController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/session")
    public Map<String, Object> session() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Map.of("authenticated", false);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oauth2User) {
            UserProfile user = currentUserService.getCurrentUser();
            return Map.of(
                    "authenticated", true,
                    "authType", AuthenticationType.GOOGLE.name(),
                    "userId", user.getId().toString(),
                    "name", oauth2User.getAttribute("name"),
                    "email", oauth2User.getAttribute("email"),
                    "sub", oauth2User.getAttribute("sub")
            );
        }

        if (principal instanceof AuthenticatedUser) {
            UserProfile user = currentUserService.getCurrentUser();
            return Map.of(
                    "authenticated", true,
                    "authType", currentUserService.getAuthenticationType().name(),
                    "userId", user.getId().toString(),
                    "username", user.getUsername(),
                    "displayName", user.getDisplayName()
            );
        }

        return Map.of("authenticated", false);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return Map.of("status", "logged_out");
    }
}
