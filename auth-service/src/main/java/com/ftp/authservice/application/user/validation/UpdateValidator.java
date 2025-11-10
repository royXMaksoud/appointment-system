package com.ftp.authservice.application.user.validation;

import com.ftp.authservice.domain.model.User;
import com.sharedlib.core.dto.ErrorResponse;
import com.sharedlib.core.exception.ValidationException;
import com.sharedlib.core.i18n.MessageResolver;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;



@Component
@RequiredArgsConstructor

public class UpdateValidator {

    private final MessageResolver messages;

    public void validate(User user) {
        if (user == null) {
            throw new ValidationException("error.validation", List.of(
                    ve(null, "error.validation")
            ));
        }

        List<ErrorResponse.ValidationError> errors = new ArrayList<>();


        if (user.getId() == null) {
            errors.add(ve("userId", "user.id.required"));
        }


        String email = StringUtils.trimToEmpty(user.getEmail());
        if (StringUtils.isNotBlank(email)) {
            if (email.length() > 200) {
                errors.add(ve("emailAddress", "user.email.max"));
            }
            if (!email.matches("(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$")) {
                errors.add(ve("emailAddress", "user.email.invalid"));
            }
        }


        if (StringUtils.length(user.getFirstName()) > 100) {
            errors.add(ve("firstName", "user.firstName.max"));
        }
        if (StringUtils.length(user.getSurName()) > 100) {
            errors.add(ve("surName", "user.surName.max"));
        }
        if (StringUtils.length(user.getFatherName()) > 100) {
            errors.add(ve("fatherName", "user.fatherName.max"));
        }
        if (StringUtils.length(user.getLanguage()) > 10) {
            errors.add(ve("language", "user.language.max"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("error.validation", errors);
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
