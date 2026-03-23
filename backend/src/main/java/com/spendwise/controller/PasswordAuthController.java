package com.spendwise.controller;

import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.request.PasswordLoginRequest;
import com.spendwise.dto.request.PasswordRegisterRequest;
import com.spendwise.dto.response.UserProfileDto;
import com.spendwise.dto.service.UserProfileService;
import com.spendwise.utils.AuthenticatedUser;
import com.spendwise.utils.AuthenticationType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/password")
public class PasswordAuthController {

    private final UserProfileService userProfileService;
    private final HttpSessionSecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public PasswordAuthController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/register")
    public UserProfileDto register(@Valid @RequestBody PasswordRegisterRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse) {
        UserProfile user;
        try {
            user = userProfileService.registerPasswordUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getDisplayName()
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        authenticateSession(user, httpRequest, httpResponse);
        return UserProfileDto.from(user);
    }

    @PostMapping("/login")
    public UserProfileDto login(@Valid @RequestBody PasswordLoginRequest request,
                                HttpServletRequest httpRequest,
                                HttpServletResponse httpResponse) {
        UserProfile user = userProfileService.getByUsername(request.getUsername());
        if (!userProfileService.matchesPassword(user, request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        authenticateSession(user, httpRequest, httpResponse);
        return UserProfileDto.from(user);
    }

    private void authenticateSession(UserProfile user,
                                     HttpServletRequest httpRequest,
                                     HttpServletResponse httpResponse) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(user.getId(), AuthenticationType.PASSWORD),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PASSWORD_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);
    }
}
