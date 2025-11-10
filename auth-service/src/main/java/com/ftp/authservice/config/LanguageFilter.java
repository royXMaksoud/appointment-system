package com.ftp.authservice.config;



import com.sharedlib.core.context.LanguageContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LanguageFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String lang = request.getHeader("X-Lang");
            if (lang == null || lang.isBlank()) {
                lang = request.getHeader("Accept-Language");
            }
            LanguageContext.setLanguage(lang != null ? lang : "en");
            chain.doFilter(request, response);
        } finally {
            LanguageContext.clear();
        }
    }
}
