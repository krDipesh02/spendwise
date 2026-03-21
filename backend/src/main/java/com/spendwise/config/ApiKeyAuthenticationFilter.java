package com.spendwise.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.service.UserApiKeyService;
import com.spendwise.utils.AuthenticatedUser;
import com.spendwise.utils.AuthenticationType;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final AuthProperties authProperties;
    private final UserApiKeyService userApiKeyService;

    public ApiKeyAuthenticationFilter(AuthProperties authProperties, UserApiKeyService userApiKeyService) {
        this.authProperties = authProperties;
        this.userApiKeyService = userApiKeyService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/mcp/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null && securityContext.getAuthentication().isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        String providedApiKey = request.getHeader(authProperties.getApiKeyHeader());
        if (providedApiKey == null || providedApiKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return;
        }
        UserProfile user = userApiKeyService.authenticate(providedApiKey);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(user.getId(), AuthenticationType.API_KEY),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_API_KEY_CLIENT")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
