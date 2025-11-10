package com.sharedlib.core.i18n;

import com.sharedlib.core.context.LanguageContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP filter that ensures LanguageContext is populated per request.
 *
 * Behavior:
 *   - If ?lang is present, it overrides anything else.
 *   - Else, if LanguageContext is already set (by an earlier filter), keep it.
 *   - Else, derive from Accept-Language -> default 'ar'.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class LanguageContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String qp = request.getParameter("lang");

        // If already set and no explicit ?lang, do not override.
        if ((qp == null || qp.isBlank()) && LanguageContext.get() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String resolved = LanguageResolver.resolveLanguage(request);

        try {
            LanguageContext.setLanguage(resolved);
            filterChain.doFilter(request, response);
        } finally {
            LanguageContext.clear(); // IMPORTANT: avoid thread-local leaks
        }
    }
}
