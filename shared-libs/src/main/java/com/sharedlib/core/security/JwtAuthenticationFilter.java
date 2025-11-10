package com.sharedlib.core.security;

import com.sharedlib.core.context.CurrentUser;
import com.sharedlib.core.context.LanguageContext;
import com.sharedlib.core.context.CurrentUserContext;
import io.jsonwebtoken.Claims; // <-- added
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * JWT authentication filter that:
 * - Extracts JWT from Authorization header.
 * - Validates token.
 * - Sets authenticated user in Spring Security Context.
 * - Sets language context for i18n.
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            // Extract core claims
            UUID userId = tokenProvider.getUserId(token);
            String userType = tokenProvider.getUserType(token);
            String lang = tokenProvider.getLanguage(token); // reads "lang"
            String email = tokenProvider.getEmail(token);
            List<String> roles = tokenProvider.getRoles(token);
            List<String> permissions = tokenProvider.getPermissions(token);

            // Build attributes map from all claims (using existing getClaims)
            Claims all = tokenProvider.getClaims(token);          // Claims extends Map<String,Object>
            Map<String, Object> attributes = new HashMap<>(all);  // copy into mutable map

            // Remove standard fields to avoid duplication
            attributes.remove("sub");
            attributes.remove("iat");
            attributes.remove("exp");
            attributes.remove("email");
            attributes.remove("userType");
            attributes.remove("lang");
            attributes.remove("roles");
            attributes.remove("permissions");

            // Set language context for this thread
            LanguageContext.setLanguage(lang);

            // Create CurrentUser (7 params: id,userType,email,language,roles,permissions,attributes)
            CurrentUser currentUser = new CurrentUser(userId, userType, email, lang, roles, permissions, attributes);
            CurrentUserContext.set(currentUser);

            // Build authorities from roles and/or userType
            List<SimpleGrantedAuthority> authorities =
                    (roles != null && !roles.isEmpty())
                            ? roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList()
                            : (userType != null ? List.of(new SimpleGrantedAuthority("ROLE_" + userType)) : Collections.emptyList());

            // Set Spring Security Authentication context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(currentUser, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clear ThreadLocal to prevent memory leaks
            LanguageContext.clear();
            CurrentUserContext.clear();
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
                ? bearerToken.substring(7)
                : null;
    }
}
