package com.ftp.authservice.application.user.validation;

import com.ftp.authservice.application.user.command.CreateUserCommand;
import com.ftp.authservice.infrastructure.db.repositories.UserRepository;
import com.sharedlib.core.dto.ErrorResponse;
import com.sharedlib.core.exception.ValidationException;
import com.sharedlib.core.i18n.MessageResolver;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CreateValidator {

    private final UserRepository repository;
    private final MessageResolver messages;

    private static final int NAME_MAX = 100;
    private static final int EMAIL_MAX = 200;
    private static final int LANGUAGE_MAX = 10;
    private static final int PASSWORD_MIN = 3;
    private static final int PASSWORD_MAX = 128;

    private static final Pattern EMAIL_RX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    public void validate(CreateUserCommand cmd) {
        if (cmd == null) {
            throw new ValidationException("error.validation", List.of(
                    ve(null, "error.validation")
            ));
        }

        List<ErrorResponse.ValidationError> errors = new ArrayList<>();

        String firstName = StringUtils.trimToEmpty(cmd.getFirstName());
        if (StringUtils.isBlank(firstName)) {
            errors.add(ve("firstName", "user.firstName.required"));
        } else if (firstName.length() > NAME_MAX) {
            errors.add(ve("firstName", "user.firstName.max"));
        }

        String surName = StringUtils.trimToEmpty(cmd.getSurName());
        if (StringUtils.isBlank(surName)) {
            errors.add(ve("surName", "user.surName.required"));
        } else if (surName.length() > NAME_MAX) {
            errors.add(ve("surName", "user.surName.max"));
        }

        String fatherName = StringUtils.trimToEmpty(cmd.getFatherName());
        if (StringUtils.isNotBlank(fatherName) && fatherName.length() > NAME_MAX) {
            errors.add(ve("fatherName", "user.fatherName.max"));
        }

        String email = StringUtils.trimToEmpty(cmd.getEmail());
        if (StringUtils.isBlank(email)) {
            errors.add(ve("emailAddress", "user.email.required"));
        } else {
            if (email.length() > EMAIL_MAX) {
                errors.add(ve("emailAddress", "user.email.max"));
            }
            if (!EMAIL_RX.matcher(email).matches()) {
                errors.add(ve("emailAddress", "user.email.invalid"));
            }
        }

        String language = StringUtils.trimToEmpty(cmd.getLanguage());
        if (StringUtils.isNotBlank(language) && language.length() > LANGUAGE_MAX) {
            errors.add(ve("language", "user.language.max"));
        }

        String rawPassword = StringUtils.trimToEmpty(cmd.getPassword());
        if (StringUtils.isBlank(rawPassword)) {
            errors.add(ve("password", "user.password.required"));
        } else if (rawPassword.length() < PASSWORD_MIN || rawPassword.length() > PASSWORD_MAX) {
            errors.add(ve("password", "user.password.size"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("error.validation", errors);
        }

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ValidationException("error.validation", List.of(
                    ve("emailAddress", "user.email.duplicate", email)
            ));
        }
    }

    private ErrorResponse.ValidationError ve(String field, String key, Object... args) {
        return ErrorResponse.ValidationError.builder()
                .field(field)
                .code(key)
                .message(messages.getMessage(key, args))
                .build();
    }
}
