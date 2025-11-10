package com.sharedlib.core.i18n;

import com.sharedlib.core.context.CurrentUserContext;
import com.sharedlib.core.context.LanguageContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * Central language/locale resolver that cooperates with your existing contexts.
 *
 * Priority (with HttpServletRequest):
 *   1) ?lang=
 *   2) LanguageContext (if already set by some earlier filter, e.g., JWT)
 *   3) CurrentUserContext.getUserLanguage()
 *   4) Accept-Language header
 *   5) default = "ar"
 *
 * Priority (without request):
 *   1) LanguageContext
 *   2) CurrentUserContext.getUserLanguage()
 *   3) default = "ar"
 */
public final class LanguageResolver {

    private LanguageResolver() {}

    public static String resolveLanguage(HttpServletRequest req) {
        // 1) explicit query param
        String qp = normalize(req != null ? req.getParameter("lang") : null);
        if (qp != null) return qp;

        // 2) already set in LanguageContext (use get() not getLanguage() to avoid default masking)
        String ctx = normalize(LanguageContext.get());
        if (ctx != null) return ctx;

        // 3) from current user (JWT)
        String userLang = normalize(CurrentUserContext.getUserLanguage());
        if (userLang != null) return userLang;

        // 4) Accept-Language
        String al = acceptToLang(req != null ? req.getHeader("Accept-Language") : null);
        if (al != null) return al;

        // 5) default
        return "ar";
    }

    public static String resolveLanguage() {
        String ctx = normalize(LanguageContext.get());
        if (ctx != null) return ctx;

        String userLang = normalize(CurrentUserContext.getUserLanguage());
        if (userLang != null) return userLang;

        return "ar";
    }

    public static Locale resolveLocale(HttpServletRequest req) {
        return Locale.forLanguageTag(resolveLanguage(req));
    }

    public static Locale resolveLocale() {
        return Locale.forLanguageTag(resolveLanguage());
    }

    private static String acceptToLang(String header) {
        if (header == null || header.isBlank()) return null;
        String lang = Locale.forLanguageTag(header).getLanguage();
        return normalize(lang);
    }

    private static String normalize(String code) {
        if (code == null) return null;
        String s = code.trim();
        return s.isEmpty() ? null : s.toLowerCase();
    }
}
