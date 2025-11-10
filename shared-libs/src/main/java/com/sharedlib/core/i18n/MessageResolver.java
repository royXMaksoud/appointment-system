package com.sharedlib.core.i18n;

import com.sharedlib.core.context.LanguageContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageResolver {

    private final MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code) {
        return getMessage(code, null, code);
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, code);
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        Locale locale = resolveLocale();
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    private Locale resolveLocale() {
        String lang = LanguageContext.getLanguage();
        return Locale.forLanguageTag(lang);
    }
}
