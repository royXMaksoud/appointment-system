package com.ftp.authservice.application.service;

import com.ftp.authservice.domain.model.OAuthProvider;
import com.ftp.authservice.infrastructure.db.entities.IdentityProviderAccountEntity;
import com.ftp.authservice.infrastructure.db.entities.UserJpaEntity;
import com.ftp.authservice.infrastructure.db.repositories.IdentityProviderAccountRepository;
import com.ftp.authservice.infrastructure.db.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthAccountLinkService {

    private final IdentityProviderAccountRepository idpAccountRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserJpaEntity findOrCreateUserFromOAuth(OAuthProvider provider, Map<String, Object> profile) {
        String providerUserId = resolveProviderUserId(provider, profile);
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new IllegalArgumentException("OAuth profile missing provider user id");
        }

        String email = resolveEmail(profile);

        Optional<IdentityProviderAccountEntity> existingAccount =
                idpAccountRepository.findByProviderAndProviderUserId(provider.name(), providerUserId);

        if (existingAccount.isPresent()) {
            return userRepository.findById(existingAccount.get().getUserId())
                    .orElseThrow(() -> new IllegalStateException("Linked user record not found"));
        }

        if (email != null) {
            Optional<UserJpaEntity> existingUser = userRepository.findByEmailIgnoreCase(email);
            if (existingUser.isPresent()) {
                UserJpaEntity linkedUser = existingUser.get();
                linkAccount(provider, providerUserId, email, profile, linkedUser);
                return linkedUser;
            }
        }

        UserJpaEntity newUser = createUserFromProfile(provider, email, profile);
        linkAccount(provider, providerUserId, email, profile, newUser);
        return newUser;
    }

    private UserJpaEntity createUserFromProfile(OAuthProvider provider, String email, Map<String, Object> profile) {
        Instant now = Instant.now();

        String fullName = asString(profile.getOrDefault("name", ""));
        String givenName = asString(profile.getOrDefault("given_name", ""));
        String familyName = asString(profile.getOrDefault("family_name", ""));

        UserJpaEntity user = UserJpaEntity.builder()
                .authMethod(UserJpaEntity.AuthMethod.OAUTH)
                .lastAuthProvider(provider.name())
                .email(email != null ? email.toLowerCase(Locale.ROOT) : provider.name().toLowerCase(Locale.ROOT) + "+user@" + provider.name().toLowerCase(Locale.ROOT) + ".oauth")
                .firstName(!givenName.isBlank() ? givenName : null)
                .surName(!familyName.isBlank() ? familyName : null)
                .fatherName(null)
                .fullName(!fullName.isBlank() ? fullName : buildFullName(givenName, familyName))
                .language(resolveLanguage(profile))
                .enabled(true)
                .deleted(false)
                .accountKind(UserJpaEntity.AccountKind.GENERAL)
                .passwordChangedAt(now)
                .passwordExpiresAt(null)
                .mustChangePassword(false)
                .build();

        UserJpaEntity saved = userRepository.save(user);
        log.info("Created new user {} from {} OAuth profile", saved.getEmail(), provider);
        return saved;
    }

    private void linkAccount(OAuthProvider provider,
                             String providerUserId,
                             String email,
                             Map<String, Object> profile,
                             UserJpaEntity user) {

        if (user.getAuthMethod() != UserJpaEntity.AuthMethod.OAUTH) {
            user.setAuthMethod(UserJpaEntity.AuthMethod.OAUTH);
        }
        user.setLastAuthProvider(provider.name());
        userRepository.save(user);

        IdentityProviderAccountEntity entity = idpAccountRepository
                .findByProviderAndProviderUserId(provider.name(), providerUserId)
                .map(existing -> {
                    existing.setUserId(user.getId());
                    existing.setProviderEmail(email);
                    existing.setIsEmailVerified(resolveEmailVerified(profile));
                    return existing;
                })
                .orElseGet(() -> IdentityProviderAccountEntity.builder()
                        .userId(user.getId())
                        .provider(provider.name())
                        .providerUserId(providerUserId)
                        .providerEmail(email)
                        .isEmailVerified(resolveEmailVerified(profile))
                        .build());

        idpAccountRepository.save(entity);
        log.info("Linked user {} with {} provider account {}", user.getEmail(), provider, providerUserId);
    }

    private String resolveProviderUserId(OAuthProvider provider, Map<String, Object> profile) {
        return switch (provider) {
            case GOOGLE -> asString(profile.get("sub"));
            case MICROSOFT -> asString(profile.get("id"));
        };
    }

    private String resolveEmail(Map<String, Object> profile) {
        String email = asString(profile.get("email"));
        if (email == null || email.isBlank()) {
            email = asString(profile.get("mail"));
        }
        if (email == null || email.isBlank()) {
            email = asString(profile.get("userPrincipalName"));
        }
        return email;
    }

    private boolean resolveEmailVerified(Map<String, Object> profile) {
        Object verified = profile.get("email_verified");
        if (verified instanceof Boolean bool) {
            return bool;
        }
        if (verified instanceof String str) {
            return Boolean.parseBoolean(str);
        }
        return false;
    }

    private String resolveLanguage(Map<String, Object> profile) {
        String locale = asString(profile.get("locale"));
        if (locale == null || locale.isBlank()) {
            locale = asString(profile.get("preferredLanguage"));
        }
        if (locale == null || locale.isBlank()) {
            return "en";
        }
        return locale.split("[-_]")[0];
    }

    private String buildFullName(String givenName, String familyName) {
        String combined = (givenName + " " + familyName).trim();
        return combined.isEmpty() ? null : combined;
    }

    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}

